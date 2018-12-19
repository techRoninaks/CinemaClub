package com.create.sidhu.movbox.activities;

import android.app.Fragment;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.PostStatusFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.fragments.RatingsDialog;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.services.UserFeedJobService;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UpdatesModel;
import com.create.sidhu.movbox.models.UserModel;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements SqlDelegate{

   //public static MainActivity mainActivity;
    private  static final int JOB_ID = 1000;
    private static final int JOB_ID_INSTANT = 1100;
    public static final int BOTTOM_NAVIGATION_HOME = 0;
    public static final int BOTTOM_NAVIGATION_FAV = 3;
    public String username;
    private ConstraintLayout masterParent;
    private FrameLayout masterFrame;
    private LinearLayout llSearch, llSearchPlaceholder, llSearchResults, llSearchMovie, llSearchUser;
    public static UserModel currentUserModel;
    public static ArrayList<UpdatesModel> updatesModels;
    private MenuItem PreviousMenuItem;
    private boolean LoginStatus;
    private android.support.v7.widget.Toolbar toolbar;
    private SearchView searchView;
    private TextView textTabMovie, textTabUser;
    private MenuItem searchItem;
    private BottomNavigationViewEx navigation;
    private boolean isFirst;
    private Badge xbadge,mbadge;
    public static  int unseenCounter = 0, followCounter = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            try {
                if(PreviousMenuItem != null)
                    setDefaultIcon(PreviousMenuItem);
                PreviousMenuItem = item;
                if(searchItem != null)
                    searchItem.collapseActionView();
                switch (item.getItemId()) {
                    //fragments are selected based on the item clicked
                    case R.id.navigation_home: //home fragment
                    {
                        item.setIcon(R.drawable.ic_home_filled);
                        HomeFragment fragment = new HomeFragment();
                        initFragment(fragment);
                        removeBadge(xbadge);
                    }
                    return true;
                    case R.id.navigation_movies://movies fragment
                    {
                        item.setIcon(R.drawable.ic_reel_filled);
                        MoviesFragment fragment = new MoviesFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_post_status: //post status fragment
                    {
                        //item.setIcon(R.drawable.ic_cross_filled);
                        PostStatusFragment bottomSheet = new PostStatusFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", getString(R.string.bottom_dialog_post_status));
                        initFragment(bottomSheet, bundle);
                    }
                        return true;
                    case R.id.navigation_favourites: //favourites fragment
                    {
                        item.setIcon(R.drawable.ic_heart_filled);
                        FavouritesFragment fragment = new FavouritesFragment();
                        initFragment(fragment);
                        removeBadge(mbadge);
                    }
                    return true;
                    case R.id.navigation_profile: //profile fragment
                    {
                        item.setIcon(R.drawable.ic_user_filled);
                        ProfileFragment fragment = new ProfileFragment();
                        Bundle bundle = new ModelHelper(MainActivity.this).buildUserModelBundle(currentUserModel, "ProfileFragment");
                        initFragment(fragment, bundle);
                    }
                    return true;
                }
            }catch (Exception e) {
                Log.e("Main:BottomSheet", e.getMessage());
            }
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            StringHelper.changeToolbarFont(toolbar, MainActivity.this);
            isFirst = true;
            SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
            username = sharedPreferences.getString("username", "");
            LoginStatus = sharedPreferences.getBoolean("login", false);
            if (!username.isEmpty() && LoginStatus) {
                if (currentUserModel == null) {
                    getUserDetails();
                    updatesModels = new ArrayList<>();
                } else {
                    scheduleJob();
                }
                masterParent = (ConstraintLayout) findViewById(R.id.containerMainParent);
                masterFrame = (FrameLayout) findViewById(R.id.content);
                llSearch = (LinearLayout) findViewById(R.id.containerSearchMaster);
                llSearchPlaceholder = (LinearLayout) findViewById(R.id.containerSearchPlaceholder);
                llSearchResults = (LinearLayout) findViewById(R.id.containerSearchResults);
                llSearchMovie = (LinearLayout) findViewById(R.id.containerSearchMovie);
                llSearchUser = (LinearLayout) findViewById(R.id.containerSearchUser);
                textTabMovie = findViewById(R.id.textView_SearchMovie);
                textTabUser = findViewById(R.id.textView_SearchUser);
                View.OnClickListener onSearchTabClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.textView_SearchUser:
                                textTabUser.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf"));
                                textTabMovie.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/myriadpro.otf"));
                                llSearchUser.setVisibility(View.VISIBLE);
                                llSearchMovie.setVisibility(View.GONE);
                                break;
                            case R.id.textView_SearchMovie:
                                textTabMovie.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf"));
                                textTabUser.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/myriadpro.otf"));
                                llSearchUser.setVisibility(View.GONE);
                                llSearchMovie.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                };
                textTabUser.setOnClickListener(onSearchTabClickListener);
                textTabMovie.setOnClickListener(onSearchTabClickListener);
                Bundle bundle = getIntent().getBundleExtra("bundle");
                try {
                    navigation = findViewById(R.id.navigation);
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                    masterFrame.setOnTouchListener(new OnSwipeTouchListener(this));

                    // Navigation Bar Customization
                    navigation.enableAnimation(false);
                    navigation.enableShiftingMode(false);
                    navigation.enableItemShiftingMode(false);
                    navigation.setTextVisibility(false);
                    navigation.setItemIconTintList(null);

                    // Load Home fragment on start
                    if ((bundle == null || bundle.isEmpty()) && currentUserModel != null) {
//                    navigation.setCurrentItem(R.id.navigation_home);
                        navigation.setSelectedItemId(R.id.navigation_home);
//                    HomeFragment fragment1 = new HomeFragment();
//                    initFragment(fragment1);
                    } else {
                        String returnPath = bundle.getString("return_path");
                        switch (returnPath) {
                            case "ProfileFragment": {
                                ProfileFragment fragment = new ProfileFragment();
                                initFragment(fragment, bundle);
                                break;
                            }
                            case "FavouritesFragment": {
                                navigation.setSelectedItemId(R.id.navigation_favourites);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Main:onCreate", e.getMessage());
                }
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }

    private void init() {
            initnotif(BOTTOM_NAVIGATION_HOME, unseenCounter);
            initnotif(BOTTOM_NAVIGATION_FAV, followCounter);
    }

    public void initnotif( int pos, int notifications) {
        switch (pos){
            case  0:
                xbadge = addBadgeAt(pos, notifications);
                break;
            case 3:
                mbadge = addBadgeAt(pos,notifications);
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                masterFrame.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                llSearch.setVisibility(View.GONE);
                masterFrame.setVisibility(View.VISIBLE);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.isEmpty()){
                    SqlHelper sqlHelper = new SqlHelper(MainActivity.this, MainActivity.this);
                    sqlHelper.setExecutePath("search.php");
                    sqlHelper.setMethod("GET");
                    sqlHelper.setActionString("search");
                    ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("u_id", currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("srch_key", s));
                    params.add(new BasicNameValuePair("mask", ""));
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        MenuItem shareItem = menu.findItem(R.id.app_bar_share);
        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Get diving into the world of Cinema.\n\nInstall Cinema Club now.\n\n" + MainActivity.this.getString(R.string.app_store_uri);
                String shareSub = "Cinema Club Invitation";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivityForResult(Intent.createChooser(shareIntent, "Share using"), 0);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    private void setDefaultIcon(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.navigation_home: //home fragment
            {
                menuItem.setIcon(R.drawable.ic_home);
            }
            break;
            case R.id.navigation_movies://movies fragment
            {
                menuItem.setIcon(R.drawable.ic_reel);
            }
            break;
            case R.id.navigation_post_status: //post status fragment
            {
                menuItem.setIcon(R.drawable.ic_cross);
            }
            break;
            case R.id.navigation_favourites: //favourites fragment
            {
                menuItem.setIcon(R.drawable.ic_heart);
            }
            break;
            case R.id.navigation_profile: //profile fragment
            {
                menuItem.setIcon(R.drawable.ic_user);
            }
            break;
        }
    }

    private void populateSearchResults(JSONObject jsonObject){
        try {
            int count = Integer.parseInt(jsonObject.getJSONObject("0").getString("count"));
            ModelHelper modelHelper = new ModelHelper(MainActivity.this);
            boolean isSet = false;
            for(int i = 1 ; i <= count ; i++){
                JSONArray jsonArray = jsonObject.getJSONArray("" + i);
                int length = jsonArray.length();
                if(length > 0) {
                    String type = jsonArray.getJSONObject(0).getString("type");
                    if (type.equalsIgnoreCase("users")) {
                        ArrayList<FavouritesModel> favouritesModels = new ArrayList<>();
                        for (int j = 1; j < length; j++) {
                            JSONObject tempObject = jsonArray.getJSONObject(j);
                            FavouritesModel favouritesModel = modelHelper.buildFavouritesModel(tempObject, "user");
                            favouritesModels.add(favouritesModel);
                        }
                        RecyclerView recyclerView = findViewById(R.id.rv_SearchUser);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                        FavouritesAdapter adapter = new FavouritesAdapter(MainActivity.this, favouritesModels, recyclerView);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        textTabUser.setVisibility(View.VISIBLE);
                        if (!isSet) {
                            llSearchUser.setVisibility(View.VISIBLE);
                            llSearchMovie.setVisibility(View.GONE);
                            textTabUser.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf"));
                            textTabMovie.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/myriadpro.otf"));
                        }

                    } else if (type.equalsIgnoreCase("movies")) {
                        ArrayList<MovieModel> movieModels = new ArrayList<>();
                        for (int j = 1; j < length; j++) {
                            JSONObject tempObject = jsonArray.getJSONObject(j);
                            MovieModel movieModel = modelHelper.buildMovieModel(tempObject);
                            movieModels.add(movieModel);
                        }
                        RecyclerView recyclerView = findViewById(R.id.rv_SearchMovie);
                        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, movieModels, recyclerView, "movie");
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        textTabMovie.setVisibility(View.VISIBLE);
                        if (!isSet) {
                            llSearchUser.setVisibility(View.GONE);
                            llSearchMovie.setVisibility(View.VISIBLE);
                            textTabMovie.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf"));
                            textTabUser.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/myriadpro.otf"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void initFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        if(!isFirst)
            fragmentTransaction.addToBackStack(null);
        else
            isFirst = false;
        fragmentTransaction.commit();
        searchItem.collapseActionView();
    }
    public void initFragment(Fragment fragment, Bundle args){
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        searchItem.collapseActionView();
    }
    public void initFragment(BottomSheetDialogFragment fragment, Bundle args){
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "BottomSheet");
        searchItem.collapseActionView();
    }
    public void initFragment(RatingsDialog ratingsDialog, Bundle args){
        ratingsDialog.setArguments(args);
        ratingsDialog.show(getFragmentManager(), "RatingsFragment");
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Badge addBadgeAt(int position, int number) {
        // adding  badge
        return new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .setBadgeBackgroundColor(getResources().getColor(R.color.colorTextPrimary))
                .setBadgeTextColor(getResources().getColor(R.color.colorPrimary))
                .bindTarget(navigation.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                            ;
                    }
                });
    }



    public   void removeBadge(Badge badge) {
        badge.hide(true);
    }


    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("get_user")) {
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                String response = jsonObject.getString("response");
                if (response.equals(getString(R.string.response_success))) {
                    currentUserModel = new ModelHelper(MainActivity.this).buildUserModel(jsonObject);
                    currentUserModel.setPreferences(jsonObject.getString("u_preference"));
                    navigation.setSelectedItemId(R.id.navigation_home);
                    scheduleJob();
                } else if (response.equals(getString(R.string.exception))) {
                    Toast.makeText(MainActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }else if(sqlHelper.getActionString().equals("search")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("search_data");
                String response = jsonObject.getJSONObject("0").getString("response");
                if(response.equals(getString(R.string.response_success))){
                    llSearchResults.setVisibility(View.VISIBLE);
                    llSearchPlaceholder.setVisibility(View.GONE);
                    populateSearchResults(jsonObject);
                }else if(response.equals(getString(R.string.response_unsuccessful))){
                    llSearchResults.setVisibility(View.GONE);
                    llSearchPlaceholder.setVisibility(View.VISIBLE);
                }else if(response.equals(getString(R.string.unexpected))){
                    Toast.makeText(MainActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void getUserDetails(){
        SqlHelper sqlHelper = new SqlHelper(MainActivity.this, MainActivity.this);
        sqlHelper.setExecutePath("get-user.php");
        sqlHelper.setActionString("get_user");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    /***
     * Starts the job service to fetch user home and favourites feed in the background
     */
    public void scheduleJob(){
        try {
            PersistableBundle pBundle = new PersistableBundle();
            pBundle.putString("userid", currentUserModel.getUserId());
            pBundle.putString("home_feed_mask", "false");
            pBundle.putString("favourites_feed_mask", "false");
            pBundle.putString("updates_mask", "false");
            pBundle.putString("initial_run", "false");
            ComponentName componentName = new ComponentName(MainActivity.this, UserFeedJobService.class);
            JobInfo info = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(15 * 60 * 1000)
                    .setExtras(pBundle)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            cancelJob(JOB_ID);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_FAILURE) {
                throw new Exception();
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void cancelJob(int id){
        try {
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(id);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void scheduleInstantJob(String type){
        try {
            PersistableBundle pBundle = new PersistableBundle();
            pBundle.putString("userid", currentUserModel.getUserId());
            pBundle.putString("home_feed_mask", type.charAt(0) == '1' ? "false" : "true");
            pBundle.putString("favourites_feed_mask", type.charAt(1) == '1' ? "false" : "true");
            pBundle.putString("updates_mask", type.charAt(2) == '1' ? "false" : "true");
            ComponentName componentName = new ComponentName(MainActivity.this, UserFeedJobService.class);
            JobInfo info = new JobInfo.Builder(JOB_ID_INSTANT, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setExtras(pBundle)
                    .setMinimumLatency(1)
                    .setOverrideDeadline(1)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            cancelJob(JOB_ID_INSTANT);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_FAILURE) {
                throw new Exception();
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(MainActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void OnClick(int position, Context context, View rootview, ArrayList<?> model, String type){
        try {
            switch (type) {
                case "user": {
                    ArrayList<FavouritesModel> favouritesModels = (ArrayList<FavouritesModel>) model;
                    Bundle bundle = new ModelHelper(MainActivity.this).buildUserModelBundle(favouritesModels.get(position).getUser(), "ProfileFragment");
                    ProfileFragment profileFragment = new ProfileFragment();
                    initFragment(profileFragment, bundle);
                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(MainActivity.this, EmailHelper.TECH_SUPPORT, "Error: MainActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
//            Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeLeft() {
//            Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}
