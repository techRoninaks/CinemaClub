package com.create.sidhu.movbox.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UserModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowReviewActivity extends AppCompatActivity implements SqlDelegate{

    private FavouritesAdapter favouritesAdapter;
    private ArrayList<FavouritesModel> favouritesList;
    private ArrayList<MovieModel> movieModels;
    RecyclerView recyclerView;
    LinearLayout llPlaceholder;
    Bundle bundle;
    String profileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
        imgTitle.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        String type = intent.getStringExtra("type");
        if(type.equals("followers") || type.equals("following")){
            profileType = intent.getStringExtra("profile_type");
        }
        favouritesList = new ArrayList<>();
        movieModels = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llPlaceholder = (LinearLayout) findViewById(R.id.containerPlaceholder);
        setLayout(type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(type);
    }

    private void setTitle(String type){
        if(type.contains("movie")){
            String subType = type.split(":")[1];
            getSupportActionBar().setTitle(subType);
            getMovies(subType.toLowerCase().replaceAll(" ", "_"));
        }else {
            switch (type) {
                case "review": {
                    getSupportActionBar().setTitle("Reviews");
                    break;
                }
                case "followers": {
                    if (profileType.equals(getString(R.string.profile_user)))
                        getSupportActionBar().setTitle("Followers");
                    else if (profileType.equals(getString(R.string.profile_movies)))
                        getSupportActionBar().setTitle("Users Watched");
                    break;
                }
                case "following": {
                    getSupportActionBar().setTitle("Following");
                    break;
                }
                case "watched": {
                    getSupportActionBar().setTitle("Watched");
                    break;
                }
            }
        }
    }

    private void setLayout(String type){
        SqlHelper sqlHelper = new SqlHelper(FollowReviewActivity.this, FollowReviewActivity.this);
        ArrayList<NameValuePair> params = new ArrayList<>();
        switch(type){
            case "review":
                sqlHelper.setMethod("GET");
                sqlHelper.setActionString("review");
                sqlHelper.setExecutePath("get-review.php");
                params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                params.add(new BasicNameValuePair("type", getString(R.string.profile_user)));
                sqlHelper.setParams(params);
                sqlHelper.executeUrl(true);
                break;
            case "followers":
                if(profileType.equals(getString(R.string.profile_user))) {
                    sqlHelper.setMethod("GET");
                    sqlHelper.setActionString("followers");
                    sqlHelper.setExecutePath("follow.php");
                    params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                    params.add(new BasicNameValuePair("type", getString(R.string.followers).toLowerCase()));
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                }else if(profileType.equals(getString(R.string.profile_movies))){
                    sqlHelper.setMethod("GET");
                    sqlHelper.setActionString("followers");
                    sqlHelper.setExecutePath("get-users-watched.php");
                    params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("m_id", bundle.getString("id")));
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                }
                break;
            case "following":
                if(profileType.equals(getString(R.string.profile_user))) {
                    sqlHelper.setMethod("GET");
                    sqlHelper.setActionString("following");
                    sqlHelper.setExecutePath("follow.php");
                    params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                    params.add(new BasicNameValuePair("type", getString(R.string.following).toLowerCase()));
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                    break;
                }
            case "watched":
                getMoviesWatched();
                return;
        }
//        favouritesAdapter = new FavouritesAdapter(this, favouritesList, recyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(favouritesAdapter);
    }

    private void getMoviesWatched() {
        SqlHelper sqlHelper = new SqlHelper(FollowReviewActivity.this, FollowReviewActivity.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("get_watched");
        sqlHelper.setExecutePath("get-watched.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    private void getMovies(String type){
        SqlHelper sqlHelper = new SqlHelper(FollowReviewActivity.this, FollowReviewActivity.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("get_watched");
        sqlHelper.setExecutePath("fetch-movie.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("m_id", ""));
        params.add(new BasicNameValuePair("group_type", type));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnClick(int position, Context context, View rootview, ArrayList<?> model, String type){
        Bundle bundle;
        switch(type){
            case "watched":{
                ArrayList<MovieModel> movieModel = (ArrayList<MovieModel>) model;
                bundle = new ModelHelper(FollowReviewActivity.this).buildMovieModelBundle(movieModel.get(position), "ProfileFragment");
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
            }
            case "following":
            case "followers":{
                ArrayList<FavouritesModel> favouritesModels = (ArrayList<FavouritesModel>) model;
                getUserDetails(favouritesModels.get(position).getUserId());
                break;
            }
            case "user":{
                ArrayList<FavouritesModel> favouritesModels = (ArrayList<FavouritesModel>) model;
                bundle = new ModelHelper(FollowReviewActivity.this).buildUserModelBundle(favouritesModels.get(position).getUser(), "ProfileFragment");
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
            }
        }
        //finish();
    }

    public void getUserDetails(String userId){
        SqlHelper sqlHelper = new SqlHelper(FollowReviewActivity.this, FollowReviewActivity.this);
        sqlHelper.setExecutePath("fetch-user.php");
        sqlHelper.setActionString("get_user");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", userId));
        params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("get_watched")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("movie_data");
                String response = jsonArray.getJSONObject(0).getString("response");
                if(response.equals(getString(R.string.response_success))){
                    populateData(jsonArray, "get_watched");
                }else if(response.equals(getString(R.string.response_unsuccessful))){
                    recyclerView.setVisibility(View.GONE);
                    llPlaceholder.setVisibility(View.VISIBLE);
                }else if(response.equals(getString(R.string.unexpected))){
                    Toast.makeText(FollowReviewActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else if(sqlHelper.getActionString().equals("get_user")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                String response = jsonObject.getString("response");
                if(response.equals(getString(R.string.response_success))){
                    ModelHelper modelHelper = new ModelHelper(FollowReviewActivity.this);
                    UserModel userModel = modelHelper.buildUserModel(jsonObject);
                    Bundle bundle = modelHelper.buildUserModelBundle(userModel, "ProfileFragment");
                    bundle.putString("return_path", "ProfileFragment");
                    Intent intent = new Intent(FollowReviewActivity.this, MainActivity.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }else if(response.equals(getString(R.string.response_unsuccessful))){
                    Toast.makeText(FollowReviewActivity.this, getString(R.string.response_unsuccessful), Toast.LENGTH_SHORT).show();
                }else if(response.equals(getString(R.string.unexpected))){
                    Toast.makeText(FollowReviewActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else {
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("user_data");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String response = jsonObject.getString("response");
                int length = jsonArray.length();
                if (response.equals(getString(R.string.response_success))) {
                    if (length <= 1) {
                        recyclerView.setVisibility(View.GONE);
                        llPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        switch (sqlHelper.getActionString()) {
                            case "review": {
                                for (int i = 1; i < length; i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    FavouritesModel favouritesModel = new FavouritesModel();
                                    favouritesModel.setUserId(jsonObject.getString("u_id"));
                                    favouritesModel.setMovieId(jsonObject.getString("m_id"));
                                    favouritesModel.setTitle(jsonObject.getString("m_name"));
                                    favouritesModel.setSubtitle(jsonObject.getString("r_text"));
                                    favouritesModel.setType("review");
                                    favouritesModel.setDate("+" + jsonObject.getString("upvotes"));
                                    favouritesModel.setTime("-" + jsonObject.getString("downvotes"));
                                    favouritesList.add(favouritesModel);
                                }
                            }
                            break;
                            case "following": {
                                for (int i = 1; i < length; i++) {
                                    FavouritesModel favouritesModel = new ModelHelper(FollowReviewActivity.this).buildFavouritesModel(jsonArray.getJSONObject(i), "following");
                                    favouritesList.add(favouritesModel);
                                }
                            }
                            break;
                            case "followers": {
                                for (int i = 1; i < length; i++) {
                                    FavouritesModel favouritesModel = new ModelHelper(FollowReviewActivity.this).buildFavouritesModel(jsonArray.getJSONObject(i), "followers");
                                    favouritesList.add(favouritesModel);
                                }
                            }
                            break;
                        }
                        favouritesAdapter = new FavouritesAdapter(this, favouritesList, recyclerView);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(favouritesAdapter);
                    }
                } else if (response.equals(getString(R.string.response_unsuccessful))) {
                    recyclerView.setVisibility(View.GONE);
                    llPlaceholder.setVisibility(View.VISIBLE);
                } else if (response.equals(getString(R.string.unexpected))) {
                    Toast.makeText(FollowReviewActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    throw new Exception();
                }
            }
        }catch (Exception e){
            Log.e("FollowReview:onResp", e.getMessage());
            finish();
        }
    }

    private void populateData(JSONArray jsonArray, String type){
        try{
            switch (type){
                case "get_watched":{
                    int length = jsonArray.length();
                    movieModels = new ArrayList<>();
                    ModelHelper modelHelper = new ModelHelper(FollowReviewActivity.this);
                    for(int i = 1; i < length; i++){
                        MovieModel movieModel = modelHelper.buildMovieModel(jsonArray.getJSONObject(i));
                        movieModels.add(movieModel);
                    }
                    initRecyclerView(type, "grid");
                    break;
                }
            }
        }catch (Exception e){
            Toast.makeText(this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initRecyclerView(String dataType, String layoutType){
        if(layoutType.equals("grid")){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            if(dataType.equals("get_watched")){
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, movieModels, recyclerView, "watched");
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
