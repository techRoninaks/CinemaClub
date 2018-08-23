package com.create.sidhu.movbox.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.PostStatusFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.models.UserModel;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SqlDelegate{

   //public static MainActivity mainActivity;
    public String username;
    private ConstraintLayout masterParent;
    private FrameLayout masterFrame;
    public static UserModel currentUserModel;
    private boolean LoginStatus;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            try {
                switch (item.getItemId()) {
                    //fragments are selected based on the item clicked
                    case R.id.navigation_home: //home fragment
                    {
                        HomeFragment fragment = new HomeFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_movies://movies fragment
                    {
                        MoviesFragment fragment = new MoviesFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_post_status: //post status fragment
                    {
                        PostStatusFragment bottomSheet = new PostStatusFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", getString(R.string.bottom_dialog_post_status));
                        initFragment(bottomSheet, bundle);
                    }
                        return true;
                    case R.id.navigation_favourites: //favourites fragment
                    {
                        FavouritesFragment fragment = new FavouritesFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_profile: //profile fragment
                    {
                        ProfileFragment fragment = new ProfileFragment(); //TODO: Get user info
                        Bundle bundle = new Bundle();
                        bundle.putString("type", getString(R.string.profile_user));
                        bundle.putBoolean("isIdentity", true);
                        bundle.putString("id", currentUserModel.getUserId());
                        bundle.putString("name", currentUserModel.getName());
                        bundle.putString("image", getString(R.string.master_url) + getString(R.string.profile_image_url) + currentUserModel.getUserId());
                        bundle.putInt("movies_watched", currentUserModel.getTotalWatched());
                        bundle.putInt("movies_reviewed", currentUserModel.getTotalReviews());
                        bundle.putInt("followers", currentUserModel.getFollowers());
                        bundle.putInt("following", currentUserModel.getFollowing());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
        username = sharedPreferences.getString("username","");
        LoginStatus = sharedPreferences.getBoolean("login", false);
        if(!username.isEmpty() && LoginStatus) {
            if(currentUserModel == null) {
                getUserDetails();
            }
            masterParent = (ConstraintLayout) findViewById(R.id.containerMainParent);
            masterFrame = (FrameLayout) findViewById(R.id.content);
            Bundle bundle = getIntent().getBundleExtra("bundle");
            try {
                BottomNavigationViewEx navigation = findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                masterFrame.setOnTouchListener(new OnSwipeTouchListener(this));

                // Navigation Bar Customization
                navigation.enableAnimation(false);
                navigation.enableShiftingMode(false);
                navigation.enableItemShiftingMode(false);
                navigation.setTextVisibility(false);

                // Load Home fragment on start
                if (bundle == null || bundle.isEmpty()) {
                    HomeFragment fragment1 = new HomeFragment();
                    initFragment(fragment1);
                } else {
                    String returnPath = bundle.getString("return_path");
                    switch (returnPath) {
                        case "ProfileFragment":
                            ProfileFragment fragment = new ProfileFragment();
                            initFragment(fragment, bundle);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e("Main:onCreate", e.getMessage());
            }
        }
        else{
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

    }

    public void initFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }
    public void initFragment(Fragment fragment, Bundle args){
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }
    public void initFragment(BottomSheetDialogFragment fragment, Bundle args){
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "BottomSheet");
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
    public static UserModel populateUserModel(JSONObject jsonObject){
        try {
            UserModel userModel = new UserModel();
            userModel.setUserId(jsonObject.getString("u_id"));
            userModel.setName(jsonObject.getString("name"));
            userModel.setEmail(jsonObject.getString("email"));
            userModel.setDob(jsonObject.getString("dob"));
            userModel.setCountry(jsonObject.getString("country"));
            userModel.setCity(jsonObject.getString("city"));
            userModel.setPhone(jsonObject.getString("phone"));
            userModel.setTotalWatched(Integer.parseInt(jsonObject.getString("mov_watched")));
            userModel.setTotalReviews(Integer.parseInt(jsonObject.getString("mov_reviewed")));
            userModel.setFollowing(Integer.parseInt(jsonObject.getString("u_following")));
            userModel.setFollowers(Integer.parseInt(jsonObject.getString("u_followers")));
            return userModel;
        }catch (Exception e){
            Log.e("Main:PopulateUser", e.getMessage());
            return null;
        }
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
            String response = jsonObject.getString("response");
            if(response.equals(getString(R.string.response_success))){
                currentUserModel = populateUserModel(jsonObject);
            }else if(response.equals(getString(R.string.exception))){
                Toast.makeText(MainActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getUserDetails(){
        SqlHelper sqlHelper = new SqlHelper(MainActivity.this, MainActivity.this);
        sqlHelper.setExecutePath("get-user.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl();
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
            Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeLeft() {
            Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}
