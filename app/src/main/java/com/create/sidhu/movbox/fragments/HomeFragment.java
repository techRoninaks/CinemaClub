package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.CallbackDelegate;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.HomeAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.helpers.TransparentProgressDialog;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.HomeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SqlDelegate, CallbackDelegate {

    private final int LOAD_INITIAL = 0;
    private final int LOAD_REFRESH = 1;
    private final int LOAD_HISTORY = 2;
    View rootView;
    RecyclerView recyclerView;
    LinearLayout llContainerPlaceholder;
    HomeAdapter homeAdapter;
    public static String seeker = "";
    public static ArrayList<HomeModel> homeModels;
    Context context;
    TransparentProgressDialog pDialog;
    private SwipeRefreshLayout swipeContainer;
    long currentTime =0;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        try {
            recyclerView = rootView.findViewById(R.id.recyclerView);
            llContainerPlaceholder = rootView.findViewById(R.id.containerPlaceholder);
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
            toolbar.setTitle("");
            swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.VISIBLE);
            StringHelper.changeToolbarFont(toolbar, (MainActivity) context);
            currentTime = System.currentTimeMillis();
            if (homeModels == null)
            {
                fetchUpdates("0",LOAD_INITIAL);
            }
            else {
                markRead(homeModels);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                homeAdapter = new HomeAdapter(context, homeModels, rootView, this);
                recyclerView.setAdapter(homeAdapter);
            }
            //initRecyclerView();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        if(System.currentTimeMillis() - currentTime > 5000) {
                            if (seeker.equals(""))
                                Toast.makeText(context, "ExploreCinema Club more!!!", Toast.LENGTH_SHORT).show();
                            else
                                fetchUpdates(seeker, LOAD_HISTORY);
                        }
                        currentTime = System.currentTimeMillis();
                    }
                }
            });

        }
        catch (Exception e){
//            Toast.makeText(context, "Home fragment exception", Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUpdates("0",LOAD_REFRESH);
            }

        }); // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorTextPrimary
                );
        return rootView;
    }

    private void markRead(ArrayList<HomeModel> homeModels) {
        int length  = homeModels.size();
        String markList="";
        for(int i= 0;i<length;i++){
            if(!homeModels.get(i).getFavourites().getRead() && !(homeModels.get(i).getFavourites().getSubType().equals("new_releases") ||homeModels.get(i).getFavourites().getSubType().equals("recommendations"))){
                markList = markList + homeModels.get(i).getFavourites().getId()+",";
            }
        }
        MainActivity mainActivity = (MainActivity) context;
        if(markList.equals(""))
            markList = null;
        if(!(markList==null)){
            if (markList.substring(markList.length() - 1).equals(","))
                markList.replace(markList.substring(markList.length() - 1), "1");
            markList = markList.substring(0, markList.length() - 1);
        }
        mainActivity.removeMarked(markList);
    }

    /***
         * Fetches the user updates from server
     */
    private void fetchUpdates(String seeker,int loadType){
        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
        sqlHelper.setExecutePath("get-updates.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("home:"+loadType);
        ContentValues params = new ContentValues();
        params.put("u_id", MainActivity.currentUserModel.getUserId());
        params.put("seeker", seeker);
        params.put("fragment", "home");
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(loadType == LOAD_INITIAL || loadType == LOAD_HISTORY);
    }



    private void initRecyclerView(JSONObject jsonObject,int loadType){
        MainActivity mainActivity = (MainActivity) context;
        if (loadType == LOAD_INITIAL || loadType == LOAD_REFRESH)
            homeModels = new ArrayList<>();
        String markList="";
        MainActivity.unseenCounter = 0;
        MainActivity.followCounter = 0;
        try{
            int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
            if(length >= 0) {
                ModelHelper modelHelper = new ModelHelper(context);
                String castString = "";
                boolean flag = false;
                for (int i = 1; i <= length; i++) {
                    if (!jsonObject.getJSONObject("" + i).getString("type").equals("follow") && !jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")) {
                        HomeModel homeModel = new HomeModel();
                        homeModel.setFavourites(modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites"));
                        if(flag){
                            castString = castString.concat("!:");
                        }else{
                            flag = true;
                        }
                        castString = castString.concat(homeModel.getFavourites().getMovie().getId() + "!@" + homeModels.size() + "!@" + homeModel.getFavourites().getMovie().getCast());
                        homeModels.add(homeModel);
                    }
                    // Notification counter code HomeFragment
                    if(jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                        switch (jsonObject.getJSONObject("" + i).getString("type")){
                            case "watchlist_reminder":
                            case "review_reminder":
                            case "review_watched":
                            case "watching_now":{
                                MainActivity.unseenCounter += 1;
                                break;
                            }
                            case "watching":{
                                MainActivity.followCounter+=1;
                                MainActivity.unseenCounter+=1;
                                break;
                            }
                            case "rating":{
                                MainActivity.followCounter+=1;
                                MainActivity.unseenCounter+=1;
                                break;
                            }
                            case "review":{
                                MainActivity.followCounter+=1;
                                MainActivity.unseenCounter+=1;
                                break;
                            }
                            case "review_vote":{
                                MainActivity.followCounter+=1;
                                MainActivity.unseenCounter+=1;
                                break;
                            }
                            case "follow":{
                                MainActivity.followCounter+=1;
                                break;
                            }

                        }
                    }
                }
                if(loadType == LOAD_INITIAL || loadType == LOAD_HISTORY){
                    pDialog = new TransparentProgressDialog(context);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
                fetchActors(castString, true, loadType);
                markRead(homeModels);
//                if(MainActivity.unseenCounter>0)
//                    mainActivity.initnotif(MainActivity.BOTTOM_NAVIGATION_HOME,MainActivity.unseenCounter);
//                if(MainActivity.followCounter>0)
//                    mainActivity.initnotif(MainActivity.BOTTOM_NAVIGATION_FAV,MainActivity.followCounter);
            }
            else{
                recyclerView.setVisibility(View.GONE);
                llContainerPlaceholder.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchActors(String castString, boolean start, int loadType){
        if(start) {
            SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
            ContentValues params = new ContentValues();
            params.put("cast", castString);
            params.put("m_id", "");
            sqlHelper.setExecutePath("get-cast.php");
            sqlHelper.setParams(params);
            HashMap<String, String> extras = new HashMap<>();
            extras.put("load_type", "" + loadType);
            sqlHelper.setExtras(extras);
            sqlHelper.setActionString("cast");
            sqlHelper.setMethod("GET");
            sqlHelper.executeUrl(false);
        }else{
            try {
                if(loadType == LOAD_INITIAL) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    homeAdapter = new HomeAdapter(context, homeModels, rootView, HomeFragment.this);
                    recyclerView.setAdapter(homeAdapter);
                    pDialog.dismiss();
                }
                else
                {
                    if(loadType == LOAD_HISTORY)
                        pDialog.dismiss();
                    if (loadType == LOAD_REFRESH)
                        swipeContainer.setRefreshing(false);
                    ((HomeAdapter)recyclerView.getAdapter()).updateList(homeModels);
                }
            }catch (Exception e){
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }
        }
    }

    private void addCastData(JSONArray jsonArray, int loadType){
        int length = jsonArray.length();
        for(int i = 1; i < length; i++){
            ModelHelper modelHelper = new ModelHelper(context);
            ArrayList<ActorModel> actorModels = new ArrayList<>();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int position = Integer.parseInt(jsonObject.getString("position"));
                JSONArray jsonArray1 = jsonObject.getJSONArray("cast");
                int lengthCast = jsonArray1.length();
                for(int j = 0; j < lengthCast; j++) {
                    ActorModel actorModel = modelHelper.buildActorModel(jsonArray1.getJSONObject(j));
                    actorModels.add(actorModel);
                }
                homeModels.get(position).setCast(actorModels);
            } catch (JSONException e) {
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }

        }
        fetchActors("", false, loadType);
    }

    private void updateDataset(String type, HashMap<String, String> extras){
        try {
            switch (type) {
                case "rating": {
                    String id = extras.get("movie_id");
                    int totalRatings = Integer.parseInt(extras.get("total_ratings"));
                    float avgRatings = Float.parseFloat(extras.get("avg_ratings"));
                    int length = homeModels.size();
                    for (int i = 0; i < length; i++) {
                        if (homeModels.get(i).getFavourites().getMovie().getId().equals(id)) {
                            homeModels.get(i).getFavourites().getMovie().setTotalRatings(totalRatings);
                            homeModels.get(i).getFavourites().getMovie().setRating("" + StringHelper.roundFloat(avgRatings, 1));
                            homeModels.get(i).getFavourites().getMovie().setRated(true);
                            homeModels.get(i).getFavourites().getMovie().setWatched(true);
                        }
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void removeNotification(String id, Context context){
        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
        sqlHelper.setExecutePath("remove-update.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("remove_notification");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("n_id", id);
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    public void OnClick(int position, Context context, View rootView, ArrayList<?> model, View view, String mainType){
        MainActivity mainActivity = (MainActivity) context;
        Fragment fragment;
        Bundle bundle;
        this.context = context;
        this.rootView = rootView;
        try {
            if (mainType.equals("home")) {
                homeModels = (ArrayList<HomeModel>) model;
                HomeModel homeModel = (HomeModel) model.get(position);
                String subType = homeModel.getFavourites().getSubType();
                switch (view.getId()) {
                    case R.id.textView_TitleTypeSubject:
                    case R.id.definition_image: {
                        switch (subType) {
                            case "new_releases":
                            case "recommendations":
                            case "review_watched": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                bundle.putString("type", "watching");
                                Intent intent = new Intent(context, FollowReviewActivity.class);
                                intent.putExtra("bundle", bundle);
                                startActivity(intent);
                                break;
                            }
                            case "watching_now":
                            case "watching":
                            case "review":
                            case "rating": {
                                bundle = new ModelHelper(context).buildUserModelBundle(homeModel.getFavourites().getUser(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "watchlist_reminder": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "review_reminder": {
                                break;
                            }
                        }
                        break;
                    }
                    case R.id.containerTypeDefinition: {
                        switch (subType) {
                            case "new_releases": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "recommendations": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "review_watched": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "watching": {
                                break;
                            }
                            case "review": {
                                break;
                            }
                            case "rating": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                bundle.putString("r_type", "cast");
                                bundle.putBoolean("isIdentity", false);
                                bundle.putString("u_id", homeModel.getFavourites().getUser().getUserId());
                                RatingsDialog ratingsDialog = new RatingsDialog();
                                ratingsDialog.setCallbackDelegate(HomeFragment.this);
                                ratingsDialog.setRated(homeModel.getFavourites().getMovie().getIsRated());
                                mainActivity.initFragment(ratingsDialog, bundle);
                                break;
                            }
                            case "watchlist_reminder": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                            case "review_reminder": {
                                bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                                fragment = new ProfileFragment();
                                mainActivity.initFragment(fragment, bundle);
                                break;
                            }
                        }
                        break;
                    }
                    case R.id.imageViewMasterPoster:
                    case R.id.textView_TitleText: {
                        bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                        fragment = new ProfileFragment();
                        mainActivity.initFragment(fragment, bundle);
                        break;
                    }
                    case R.id.img_Watched: {
                        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
                        sqlHelper.setExecutePath("update-watching.php");
                        sqlHelper.setActionString("watching:" + position);
                        HashMap<String, String> extras = new HashMap<>();
                        extras.put("view_id", "" + R.id.img_Watched);
                        sqlHelper.setMethod("GET");
                        ContentValues params = new ContentValues();
                        params.put("m_id", homeModel.getFavourites().getMovie().getId());
                        params.put("u_id", MainActivity.currentUserModel.getUserId());
                        params.put("is_watched", "" + homeModel.getFavourites().getMovie().getIsWatched());
                        sqlHelper.setParams(params);
                        sqlHelper.setExtras(extras);
                        sqlHelper.executeUrl(true);
                        break;
                    }
                    case R.id.img_Rating: {
                        bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                        bundle.putString("r_type", "cast");
                        bundle.putString("is_watched", "" + homeModel.getFavourites().getMovie().getIsWatched());
                        RatingsDialog ratingsDialog = new RatingsDialog();
                        ratingsDialog.setCallbackDelegate(HomeFragment.this);
                        ratingsDialog.setRated(homeModel.getFavourites().getMovie().getIsRated());
                        mainActivity.initFragment(ratingsDialog, bundle);
                        break;
                    }
                    case R.id.img_Review: {
                        break;
                    }
                    case R.id.containerWatched: {
                        bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                        bundle.putString("return_path", "HomeFragment");
                        Intent intent = new Intent(context, FollowReviewActivity.class);
                        intent.putExtra("profile_type", "movie");
                        intent.putExtra("type", "followers");
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                        break;
                    }
                    case R.id.containerRating: {
                        bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                        bundle.putString("r_type", "list");
                        RatingsDialog ratingsDialog = new RatingsDialog();
                        ratingsDialog.setCallbackDelegate(HomeFragment.this);
                        mainActivity.initFragment(ratingsDialog, bundle);
                        break;
                    }
                    case R.id.containerReviews: {
                        break;
                    }
                }
            } else if (mainType.equals("cast")) {
                ActorModel actorModel = (ActorModel) model.get(position);
                bundle = new ModelHelper(context).buildActorModelBundle(actorModel, "ProfileFragment");
                fragment = new ProfileFragment();
                mainActivity.initFragment(fragment, bundle);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().contains("home")) {
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getJSONObject("0").getString("response");
                if (response.equals(context.getString(R.string.response_success))) {
                    seeker = jsonObject.getJSONObject("0").getString("new_seeker");
                    initRecyclerView(jsonObject, Integer.parseInt(sqlHelper.getActionString().split(":")[1]));
                } else if (response.equals(context.getString(R.string.response_unsuccessful))) {
                    recyclerView.setVisibility(View.GONE);
                    llContainerPlaceholder.setVisibility(View.VISIBLE);
                } else if (response.equals(context.getString(R.string.unexpected))) {
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().contains("watching")){
                recyclerView = rootView.findViewById(R.id.recyclerView);
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                int position = Integer.parseInt(sqlHelper.getActionString().split(":")[1]);
                if(response.equals(context.getString(R.string.response_success))){
                    ProfileFragment.currentUserWatchlist = null;
                    if(homeModels.get(position).getFavourites().getMovie().getIsWatched()){
                        homeModels.get(position).getFavourites().getMovie().setWatched(false);
                        homeModels.get(position).getFavourites().getMovie().setTotalWatched(homeModels.get(position).getFavourites().getMovie().getTotalWatched() - 1);
//                        ImageView imageView = (ImageView) recyclerView.findViewById(Integer.parseInt(sqlHelper.getExtras().get("view_id")));
//                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_eye));
                        int watching = MainActivity.currentUserModel.getTotalWatched();
                        MainActivity.currentUserModel.setTotalWatched(watching - 1);
                        Toast.makeText(context, "Movie has been marked as unwatched.", Toast.LENGTH_SHORT).show();
                    }else{
                        homeModels.get(position).getFavourites().getMovie().setWatched(true);
                        homeModels.get(position).getFavourites().getMovie().setTotalWatched(homeModels.get(position).getFavourites().getMovie().getTotalWatched() + 1);
//                        ImageView imageView = (ImageView) recyclerView.findViewById(Integer.parseInt(sqlHelper.getExtras().get("view_id")));
//                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_eye_filled));
                        new ModelHelper(context).addToUpdatesModel(homeModels.get(position).getFavourites().getMovie().getId(), "", "watching");
                        int watching = MainActivity.currentUserModel.getTotalWatched();
                        MainActivity.currentUserModel.setTotalWatched(watching + 1);
                        Toast.makeText(context, "Movie has been marked as watched.", Toast.LENGTH_SHORT).show();
                    }
                    recyclerView.getAdapter().notifyItemChanged(position);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(homeModels.get(position).getFavourites().getMovie().getIsWatched())
                        Toast.makeText(context, "Failed to remove from watching. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watching. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().contains("cast")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("cast_data");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String response = jsonObject.getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    addCastData(jsonArray, Integer.parseInt(sqlHelper.getExtras().get("load_type")));
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){

                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().equals("remove_notification")){
                
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }


    @Override
    public void onResultReceived(String type, boolean resultCode, HashMap<String, String> extras) {
        if(resultCode){
            switch (type){
                case "rating":{
                    updateDataset(type, extras);
                    break;
                }
            }
        }
    }

    @Override
    public void onResultReceived(String type, boolean resultCode, Bundle extras) {
        switch (type){
            case "profile_nav":{
                ProfileFragment fragment = new ProfileFragment();
                ((MainActivity) context).initFragment(fragment, extras);
                break;
            }
        }

    }
}
