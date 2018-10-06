package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.MovieModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

//TODO: Fetch proper data
public class FollowReviewActivity extends AppCompatActivity implements SqlDelegate{

    private FavouritesAdapter favouritesAdapter;
    private ArrayList<FavouritesModel> favouritesList;
    private ArrayList<MovieModel> movieModels;
    RecyclerView recyclerView;
    LinearLayout llPlaceholder;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_review);
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        String type = intent.getStringExtra("type");
        favouritesList = new ArrayList<>();
        movieModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        setLayout(type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void setLayout(String type){
        SqlHelper sqlHelper = new SqlHelper(FollowReviewActivity.this, FollowReviewActivity.this);
        ArrayList<NameValuePair> params = new ArrayList<>();
        switch(type){
            case "review":
//                for(int i=0;i<=8;i++){
//                    FavouritesModel favouritesModel = new FavouritesModel();
//                    favouritesModel.setTitle("Review: " + i);
//                    favouritesModel.setSubtitle("Review Subtitle: " + i);
//                    favouritesModel.setType("review");
//                    favouritesModel.setDate("+" + i + 1);
//                    favouritesModel.setTime("-" + i + 1);
//                    favouritesList.add(favouritesModel);
//                }
                sqlHelper.setMethod("GET");
                sqlHelper.setActionString("review");
                sqlHelper.setExecutePath("get-review.php");
                params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                params.add(new BasicNameValuePair("type", getString(R.string.profile_user)));
                sqlHelper.setParams(params);
                sqlHelper.executeUrl(true);
                break;
            case "followers":
//                for(int i=0;i<=8;i++){
//                    FavouritesModel favouritesModel = new FavouritesModel();
//                    favouritesModel.setTitle("Followers: " + i);
//                    favouritesModel.setSubtitle("Followers Subtitle: " + i);
//                    favouritesModel.setType("followers");
//                    favouritesModel.setDate("Followers" + i + 1);
//                    favouritesModel.setTime("Following" + i + 1);
//                    favouritesList.add(favouritesModel);
//                }
                sqlHelper.setMethod("GET");
                sqlHelper.setActionString("followers");
                sqlHelper.setExecutePath("follow.php");
                params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                params.add(new BasicNameValuePair("type", getString(R.string.followers).toLowerCase()));
                sqlHelper.setParams(params);
                sqlHelper.executeUrl(true);
                break;
            case "following":
//                for(int i=0;i<=8;i++){
//                    FavouritesModel favouritesModel = new FavouritesModel();
//                    favouritesModel.setTitle("Following: " + i);
//                    favouritesModel.setSubtitle("Following Subtitle: " + i);
//                    favouritesModel.setType("following");
//                    favouritesModel.setDate("Followers" + i + 1);
//                    favouritesModel.setTime("Following" + i + 1);
//                    favouritesList.add(favouritesModel);
//                }
                sqlHelper.setMethod("GET");
                sqlHelper.setActionString("following");
                sqlHelper.setExecutePath("follow.php");
                params.add(new BasicNameValuePair("u_id", bundle.getString("id")));
                params.add(new BasicNameValuePair("type", getString(R.string.following).toLowerCase()));
                sqlHelper.setParams(params);
                sqlHelper.executeUrl(true);
                break;
            case "watched":
                addData();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                recyclerView.setLayoutManager(gridLayoutManager);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, movieModels, recyclerView, "watched");
                recyclerView.setAdapter(adapter);
                return;
        }
//        favouritesAdapter = new FavouritesAdapter(this, favouritesList, recyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(favouritesAdapter);
    }

    private void addData() {
        MovieModel model = new MovieModel();
        model.setImage("https://www.topmovierankings.com/images/albums/photos/comrade-in-america-malayalam-movie-stills-poster-4503.jpg");
        model.setName("C.I.A");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://upload.wikimedia.org/wikipedia/ml/thumb/3/30/Parava_movie_poster.jpeg/220px-Parava_movie_poster.jpeg");
        model.setName("Parava");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2016/03/kali-poster-2.jpg");
        model.setName("Kali");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://malayalam.samayam.com/img/64118605/Master.jpg");
        model.setName("Naam");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2015/12/charlie-poster-3.jpg");
        model.setName("Charlie");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://www.topmovierankings.com/images/albums/photos/comrade-in-america-malayalam-movie-stills-poster-4503.jpg");
        model.setName("C.I.A");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://upload.wikimedia.org/wikipedia/ml/thumb/3/30/Parava_movie_poster.jpeg/220px-Parava_movie_poster.jpeg");
        model.setName("Parava");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2016/03/kali-poster-2.jpg");
        model.setName("Kali");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://malayalam.samayam.com/img/64118605/Master.jpg");
        model.setName("Naam");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2015/12/charlie-poster-3.jpg");
        model.setName("Charlie");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://www.topmovierankings.com/images/albums/photos/comrade-in-america-malayalam-movie-stills-poster-4503.jpg");
        model.setName("C.I.A");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://upload.wikimedia.org/wikipedia/ml/thumb/3/30/Parava_movie_poster.jpeg/220px-Parava_movie_poster.jpeg");
        model.setName("Parava");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2016/03/kali-poster-2.jpg");
        model.setName("Kali");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://malayalam.samayam.com/img/64118605/Master.jpg");
        model.setName("Naam");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2015/12/charlie-poster-3.jpg");
        model.setName("Charlie");
        model.setRating("9");
        movieModels.add(model);

        Log.d(TAG, "Data initiated");
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

    public void OnClick(int position, Context context, View rootview, ArrayList<MovieModel> movieModel){
        bundle = new Bundle();
        bundle.putString("type", getString(R.string.profile_movies));
        bundle.putBoolean("isIdentity", false);
        bundle.putString("image",movieModel.get(position).getImage());
        bundle.putString("name",movieModel.get(position).getName());
        bundle.putString("return_path", "ProfileFragment");
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("user_data");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String response = jsonObject.getString("response");
            int length = jsonArray.length();
            if(response.equals(getString(R.string.response_success))){
                switch (sqlHelper.getActionString()){
                    case "review":{
                        for(int i=1 ; i<length; i++){
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
                    case "following":{
                        for(int i=1 ; i<length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            FavouritesModel favouritesModel = new FavouritesModel();
                            favouritesModel.setUserId(jsonObject.getString("u_id"));
                            favouritesModel.setTitle(jsonObject.getString("name"));
                            favouritesModel.setSubtitle(jsonObject.getString("movies_watched") + " movies watched");
                            favouritesModel.setType("following");
                            favouritesModel.setDate("Following: " + jsonObject.getString("following"));
                            favouritesModel.setTime("Followers: " + jsonObject.getString("followers"));
                            favouritesList.add(favouritesModel);
                        }
                    }
                    break;
                    case "followers":{
                        for(int i=1 ; i<length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            FavouritesModel favouritesModel = new FavouritesModel();
                            favouritesModel.setUserId(jsonObject.getString("u_id"));
                            favouritesModel.setTitle(jsonObject.getString("name"));
                            favouritesModel.setSubtitle(jsonObject.getString("movies_watched") + " movies watched");
                            favouritesModel.setType("followers");
                            favouritesModel.setDate("Following: " + jsonObject.getString("following"));
                            favouritesModel.setTime("Followers: " + jsonObject.getString("followers"));
                            favouritesList.add(favouritesModel);
                        }
                    }
                    break;
                }
                favouritesAdapter = new FavouritesAdapter(this, favouritesList, recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(favouritesAdapter);
            }else if(response.equals(getString(R.string.response_unsuccessful))){

            }
        }catch (Exception e){
            Log.e("FollowReview:onResp", e.getMessage());
            Intent intent = new Intent(FollowReviewActivity.this, MainActivity.class);
            bundle.putString("return_path", "ProfileFragment");
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }
}
