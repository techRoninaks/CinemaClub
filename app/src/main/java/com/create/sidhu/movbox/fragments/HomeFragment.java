package com.create.sidhu.movbox.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
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
import com.create.sidhu.movbox.activities.ReviewsActivity;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.adapters.HomeAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.helpers.TransparentProgressDialog;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UpdatesModel;
import com.create.sidhu.movbox.models.UserModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SqlDelegate, CallbackDelegate {

    View rootView;
    RecyclerView recyclerView;
    LinearLayout llContainerPlaceholder;
    HomeAdapter homeAdapter;
    public static ArrayList<HomeModel> homeModels;
    Context context;
    TransparentProgressDialog pDialog;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        try {
            recyclerView = rootView.findViewById(R.id.recyclerView);
            llContainerPlaceholder = rootView.findViewById(R.id.containerPlaceholder);
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
            toolbar.setTitle("");
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.VISIBLE);
            StringHelper.changeToolbarFont(toolbar, (MainActivity) context);
            if (homeModels == null)
            {
                fetchUpdates();
                markRead(homeModels);
            }
            else {
                markRead(homeModels);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                homeAdapter = new HomeAdapter(context, homeModels, rootView, this);
                recyclerView.setAdapter(homeAdapter);
            }
            //initRecyclerView();
        }catch (Exception e){
            Toast.makeText(context, "Home fragment exception", Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return rootView;
    }

    private void markRead(ArrayList<HomeModel> homeModels) {
        int length  = homeModels.size();
        String markList="";
        for(int i= 0;i<length;i++){
            if(homeModels.get(i).getFavourites().getSubType().equals("new_releases") ||homeModels.get(i).getFavourites().getSubType().equals("recommendations"))
            {

            }
            else if(!homeModels.get(i).getFavourites().getRead()){
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
    private void fetchUpdates(){
        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
        sqlHelper.setExecutePath("get-updates.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("home");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("seeker", "0"));
        params.add(new BasicNameValuePair("fragment", "home"));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);

    }



    private void initRecyclerView(JSONObject jsonObject){
        MainActivity mainActivity = (MainActivity) context;
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
                        if(jsonObject.getJSONObject("" + i).getString("type").equals("watchlist_reminder")&&jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.unseenCounter+=1;
                        }
                        if(jsonObject.getJSONObject("" + i).getString("type").equals("review_reminder")&&jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.unseenCounter+=1;
                        }
                        if(jsonObject.getJSONObject("" + i).getString("type").equals("follow")&&jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.followCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("watching")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.followCounter+=1;
                            MainActivity.unseenCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("rating")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.followCounter+=1;
                            MainActivity.unseenCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("review")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.followCounter+=1;
                            MainActivity.unseenCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.followCounter+=1;
                            MainActivity.unseenCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("review_watched")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.unseenCounter+=1;
                        }
                        if (jsonObject.getJSONObject("" + i).getString("type").equals("watching_now")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                        {
                            markList = markList+jsonObject.getJSONObject("" + i).getString("id")+",";
                            MainActivity.unseenCounter+=1;
                        }
                    }
                pDialog = new TransparentProgressDialog(context);
                pDialog.setCancelable(false);
                pDialog.show();
                fetchActors(castString, true);
                mainActivity.removeMarked(markList);
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
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchActors(String castString, boolean start){
        if(start) {
            SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("cast", castString));
            params.add(new BasicNameValuePair("m_id", ""));
            sqlHelper.setExecutePath("get-cast.php");
            sqlHelper.setParams(params);
            sqlHelper.setActionString("cast");
            sqlHelper.setMethod("GET");
            sqlHelper.executeUrl(false);
        }else{
            try {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                homeAdapter = new HomeAdapter(context, homeModels, rootView, HomeFragment.this);
                recyclerView.setAdapter(homeAdapter);
                pDialog.dismiss();
            }catch (Exception e){
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }
        }
    }

    private void addCastData(JSONArray jsonArray){
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
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }

        }
        fetchActors("", false);
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
                        }
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void removeNotification(String id, Context context){
        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
        sqlHelper.setExecutePath("remove-update.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("remove_notification");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("n_id", id));
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
                                bundle.putString("type", "cast");
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
                        ArrayList<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("m_id", homeModel.getFavourites().getMovie().getId()));
                        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                        params.add(new BasicNameValuePair("is_watched", "" + homeModel.getFavourites().getMovie().getIsWatched()));
                        sqlHelper.setParams(params);
                        sqlHelper.setExtras(extras);
                        sqlHelper.executeUrl(true);
                        break;
                    }
                    case R.id.img_Rating: {
                        bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                        bundle.putString("type", "cast");
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
                        bundle.putString("type", "list");
                        RatingsDialog ratingsDialog = new RatingsDialog();
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
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("home")) {
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getJSONObject("0").getString("response");

                if (response.equals(context.getString(R.string.response_success))) {
                    initRecyclerView(jsonObject);
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
                    addCastData(jsonArray);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){

                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().equals("remove_notification")){
                
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: HomeFragment", StringHelper.convertStackTrace(e));
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
}
