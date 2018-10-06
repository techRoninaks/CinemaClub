package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.activities.EditProfile;
import com.create.sidhu.movbox.activities.LoginActivity;
import com.create.sidhu.movbox.activities.ProfileImage;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.SettingsActivity;
import com.create.sidhu.movbox.adapters.ActorAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.MovieModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;
import static com.create.sidhu.movbox.helpers.StringHelper.formatTextCount;
import static com.create.sidhu.movbox.helpers.StringHelper.toSentenceCase;

//TODO: Fragment over fragment display issue for grid view of recycle adapter
//Reproduction steps:
//1: Make watchlist gridview and click on any movie.
//2: New fragment appears with elements of old fragment still visible.
/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements SqlDelegate{


    public ProfileFragment() {
        // Required empty public constructor
    }

    private ArrayList<MovieModel> movieModels;
    private ArrayList<ActorModel> actorModels;
    private Boolean bClick = true;
    private Context context;
    private String type;
    private Boolean isIdentity;
    private Boolean isFollowing;
    private Boolean isAddedToWatchlist;
    private Boolean isWatched;
    private String image;
    private String id;
    private String name;
    private String censorRating;
    private String rating;
    private String genre;
    private String totalRatings;
    private String cast;
    private String castType;
    private int duration;
    private String displayDimension;
    private int totalWatched;
    private int totalReviewed;
    private int followers;
    private int following;

    View rootview, statSeparator1, statSeparator2;
    LinearLayout llSettings, llLogout, llWatched, llMovieImage, llUserWatchInfo, llMovieInfo, llButtons, llFollowers, llFollowing, llReviews;
    RelativeLayout rlProfileImage;
    TextView textViewName, textViewWatched, textViewReviews, textViewFollowers, textViewFollowing, textViewGenre, textViewDuration, textViewCensorRating, textViewDisplayDimension, textViewFollowersText, textViewFollowingText, textViewWatchlistPlaceholder, textViewWatchlistText, textViewWatchedText;
    CircleImageView imgProfile;
    ImageView imgMovie;
    ScrollView parentView;
    Button btn_click, btnEditProfile, btnWatchlist;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
        toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_movies)));
        final Bundle bundle = getArguments();
        type = bundle.getString("type");
        id = bundle.getString("id");
        image = bundle.getString("image");
        name = bundle.getString("name");
        totalWatched = bundle.getInt("movies_watched");
        totalReviewed = bundle.getInt("movies_reviewed");
        if(type.equals(context.getString(R.string.profile_user))){
            isIdentity = bundle.getBoolean("isIdentity");
            followers = bundle.getInt("followers");
            following = bundle.getInt("following");
            movieModels = new ArrayList<>();
            if(!isIdentity){
                isFollowing = bundle.getBoolean("isFollowing");
            }
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_profile)));
        }else if(type.equals(context.getString(R.string.profile_movies))){
            censorRating = bundle.getString("censor_rating");
            rating = bundle.getString("rating");
            genre = bundle.getString("genre");
            duration = bundle.getInt("duration");
            displayDimension = bundle.getString("display_dimension");
            totalRatings = bundle.getString("total_ratings");
            cast = bundle.getString("cast");
            actorModels = new ArrayList<>();
            isAddedToWatchlist = bundle.getBoolean("is_watchlist");
            isWatched = bundle.getBoolean("is_watched");
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.profile_movies)));
        }else if(type.equals(context.getString(R.string.profile_cast))){
            castType = bundle.getString("cast_type");
            rating = "" + bundle.getFloat("rating");
            totalWatched = bundle.getInt("movies");
            movieModels = new ArrayList<>();
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.profile_cast)));
        }
        StringHelper.changeToolbarFont(toolbar, (MainActivity)context);

        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        statSeparator1 = rootview.findViewById(R.id.viewStatSeparator1);
        statSeparator2 = rootview.findViewById(R.id.viewStatSeparator2);
        parentView = (ScrollView) rootview.findViewById(R.id.parentView) ;
        llSettings = (LinearLayout) rootview.findViewById(R.id.containerSettings);
        llLogout = (LinearLayout) rootview.findViewById(R.id.containerLogout);
        llWatched = (LinearLayout) rootview.findViewById(R.id.containerWatched);
        llMovieImage = (LinearLayout) rootview.findViewById(R.id.containerMovieImage);
        llUserWatchInfo = (LinearLayout) rootview.findViewById(R.id.containerUserWatchInfo);
        llMovieInfo = (LinearLayout) rootview.findViewById(R.id.containerMovieInfo);
        llButtons = (LinearLayout) rootview.findViewById(R.id.containerFollowButton);
        llFollowers = (LinearLayout) rootview.findViewById(R.id.containerFollowers);
        llFollowing = (LinearLayout) rootview.findViewById(R.id.containerFollowing);
        llReviews = (LinearLayout) rootview.findViewById(R.id.containerReviews);
        rlProfileImage = (RelativeLayout) rootview.findViewById(R.id.containerUserImage);
        textViewName = rootview.findViewById(R.id.profile_name);
        textViewWatched = rootview.findViewById(R.id.profile_watch_count);
        textViewWatchedText = rootview.findViewById(R.id.watched);
        textViewReviews = rootview.findViewById(R.id.profile_review_count);
        textViewFollowers = rootview.findViewById(R.id.profile_followers_count);
        textViewFollowing = rootview.findViewById(R.id.profile_following_count);
        textViewFollowingText = rootview.findViewById(R.id.text_following);
        textViewFollowersText = rootview.findViewById(R.id.text_followers);
        textViewGenre = rootview.findViewById(R.id.textView_Genre);
        textViewDuration = rootview.findViewById(R.id.textView_Duration);
        textViewCensorRating = rootview.findViewById(R.id.textView_CensorRating);
        textViewDisplayDimension = rootview.findViewById(R.id.textView_DisplayDimension);
        textViewWatchlistPlaceholder = rootview.findViewById(R.id.textView_WatchlistPlaceholder);
        textViewWatchlistText = rootview.findViewById(R.id.text_watchlist);
        recyclerView = rootview.findViewById(R.id.recyclerView);
        imgProfile = rootview.findViewById(R.id.profile_image);
        imgMovie = rootview.findViewById(R.id.imgMoviePoster);
        btn_click = rootview.findViewById(R.id.btn_click);
        btnEditProfile = rootview.findViewById(R.id.btn_editProfile);
        btnWatchlist = rootview.findViewById(R.id.btn_watchlist);
        CircleImageView button_img = rootview.findViewById(R.id.btn_profile_image);
        populateView();
        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        llWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context,FollowReviewActivity.class);
                intent.putExtra("type","watched");
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        // onClickListener for Review count
        textViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context,FollowReviewActivity.class);
                intent.putExtra("type","review");
                intent.putExtra("bundle", bundle);
                startActivity(intent);

            }
        });

        // onClickListener for Followers
        textViewFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context,FollowReviewActivity.class);
                intent.putExtra("type","followers");
                intent.putExtra("bundle", bundle);
                startActivity(intent);

            }
        });

         // onClickListener for Followers
        textViewFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("return_path", "ProfileFragment");
                Intent intent = new Intent(context,FollowReviewActivity.class);
                intent.putExtra("type","following");
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        // onClickListener for Profile Image

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileImage.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
//                ProfileImageFragment fragment = new ProfileImageFragment();
//                MainActivity activity = (MainActivity) context;
//                activity.initFragment(fragment, bundle);

            }
        };
        imgProfile.setOnClickListener(onClickListener);
        button_img.setOnClickListener(onClickListener);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bClick = !bClick;       //achieves the toggle functionality
                initRecyclerView();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals(context.getString(R.string.profile_user))){
                    if(isIdentity) {
                        Intent intent = new Intent(context, EditProfile.class);
                        bundle.putString("return_path", "ProfileFragment");
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }else{
                        SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                        sqlHelper.setExecutePath("update-following.php");
                        sqlHelper.setActionString("follow");
                        sqlHelper.setMethod("GET");
                        ArrayList<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                        params.add(new BasicNameValuePair("f_id", id));
                        params.add(new BasicNameValuePair("is_following", isFollowing.toString()));
                    }
                }else if(type.equals(context.getString(R.string.profile_movies))){
                    SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                    sqlHelper.setExecutePath("update-watching.php");
                    sqlHelper.setActionString("watching");
                    sqlHelper.setMethod("GET");
                    ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("m_id", id));
                    params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("is_watched", isWatched.toString()));
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(false);

                }else if(type.equals(context.getString(R.string.profile_cast))){

                }
            }
        });
        btnWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                sqlHelper.setExecutePath("update-watchlist.php");
                sqlHelper.setActionString("watchlist");
                sqlHelper.setMethod("GET");
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("m_id", id));
                params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                params.add(new BasicNameValuePair("is_watchlist", isAddedToWatchlist.toString()));
                sqlHelper.setParams(params);
                sqlHelper.executeUrl(false);
            }
        });
        return rootview;
    }

    /***
     * Populates the views based on page roles
     */
    private void populateView(){
        textViewName.setText(name);
        if(type.equals(context.getString(R.string.profile_user))){
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.ic_user);
//            requestOptions.error(R.drawable.ic_user);
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .into(imgProfile);
            textViewWatched.setText(formatTextCount(totalWatched));
            textViewReviews.setText(formatTextCount(totalReviewed));
            textViewFollowers.setText(formatTextCount(followers));
            textViewFollowing.setText(formatTextCount(following));
            if(!isIdentity){
                btnEditProfile.setText(isFollowing ? context.getString(R.string.follow_button_following) : context.getString(R.string.follow_button_follow));
            }
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("u_id",id));
            populateWatchlist("get-watchlist.php", params);
        }else if(type.equals(context.getString(R.string.profile_movies))){
            rlProfileImage.setVisibility(View.GONE);
            llMovieImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .asBitmap()
                    .load(image.replace("portrait","landscape"))
                    .into(imgMovie);
            textViewName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            llUserWatchInfo.setVisibility(View.GONE);
            llMovieInfo.setVisibility(View.VISIBLE);
            textViewGenre.setText(genre);
            textViewDuration.setText("" + duration);
            textViewDisplayDimension.setText(displayDimension);
            textViewCensorRating.setText(censorRating);
            textViewFollowers.setText(formatTextCount(totalWatched));
            textViewFollowersText.setText(context.getString(R.string.tab_watched));
            textViewFollowing.setText(rating + "/10");
            textViewFollowingText.setText("(" + totalRatings + ")");
            textViewReviews.setText(formatTextCount(totalReviewed));
            textViewWatchlistText.setText("Cast");
            btnEditProfile.setText(isWatched ? context.getString(R.string.follow_button_watched) : context.getString(R.string.follow_button_watching));
            btnEditProfile.setBackground(isWatched ? context.getDrawable(R.drawable.custom_button_white) : context.getDrawable(R.drawable.custom_button_yellow));
            btnWatchlist.setVisibility(View.VISIBLE);
            btnWatchlist.setText(isAddedToWatchlist ? context.getString(R.string.follow_button_watchlist_remove) : context.getString(R.string.follow_button_watchlist_add));
            btnWatchlist.setBackground(isAddedToWatchlist ? context.getDrawable(R.drawable.custom_button_white) : context.getDrawable(R.drawable.custom_button_yellow));

            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("cast", cast));
            params.add(new BasicNameValuePair("m_id", id));
            populateWatchlist("get-cast.php", params);
        }else if(type.equals(context.getString(R.string.profile_cast))){
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .into(imgProfile);
            textViewWatched.setVisibility(View.GONE);
            textViewWatchedText.setText(toSentenceCase(castType));
            textViewFollowing.setText(rating + "/10");
            textViewFollowingText.setText("Rating");
            textViewFollowers.setText("" + totalWatched);
            textViewFollowersText.setText("Total Movies");
            llButtons.setVisibility(View.GONE);
            textViewWatchlistText.setText("Movies");
            llReviews.setVisibility(View.GONE);
            statSeparator2.setVisibility(View.GONE);

            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", id));
            populateWatchlist("get-cast-movies.php", params);
        }
    }

    /***
     * Calls the appropriate API to set Recyclerview
     * @param executePath - The API file to execute
     * @param params - The parameters in the API call
     */
    private void populateWatchlist(String executePath, ArrayList<NameValuePair> params){
        SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
        sqlHelper.setExecutePath(executePath);
        sqlHelper.setParams(params);
        sqlHelper.setActionString("populate_watchlist");
        sqlHelper.setMethod("GET");
        sqlHelper.executeUrl(true);
    }

    /***
     * Populate the recyclerview. Populates movies for Users and cast for Movies
     * @param jsonArray
     */
    private void addData(JSONArray jsonArray) {
        int length = jsonArray.length();
        for(int i = 1; i < length; i++){
            ModelHelper modelHelper = new ModelHelper(context);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast))) {
                    MovieModel movieModel = modelHelper.buildMovieModel(jsonObject);
                    movieModels.add(movieModel);
                }else if(type.equals(context.getString(R.string.profile_movies))){
                    ActorModel actorModel = modelHelper.buildActorModel(jsonObject);
                    actorModels.add(actorModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "Data initiated");
        initRecyclerView();
    }

    /***
     * Sets appropriate adapter to Recylerview
     */
    private void initRecyclerView() {
        if (bClick) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);            // Calling the RecyclerView Adapter with a layout
            recyclerView.setLayoutManager(layoutManager);
            if(type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast))) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels, rootview, "profile");
                recyclerView.setAdapter(adapter);
            }else if(type.equals(context.getString(R.string.profile_movies))){
                ActorAdapter adapter = new ActorAdapter(context, actorModels, rootview);
                recyclerView.setAdapter(adapter);
            }
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            if(type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast))) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels, rootview, "profile");
                recyclerView.setAdapter(adapter);
            }else if(type.equals(context.getString(R.string.profile_movies))){
                ActorAdapter adapter = new ActorAdapter(context, actorModels, rootview);
                recyclerView.setAdapter(adapter);
            }
        }
    }
    public void OnClick(int position, Context context,View rootview, ArrayList<MovieModel> movieModels) {
        //Custom code
        this.context = context;
        MainActivity mainActivity = (MainActivity) context;
        ModelHelper modelHelper = new ModelHelper(context);
        Bundle bundle = modelHelper.buildMovieModelBundle(movieModels.get(position), "ProfileFragment");
        ProfileFragment fragment2 = new ProfileFragment();
        mainActivity.initFragment(fragment2, bundle);
        Toast.makeText(context,"Inside Profile",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("populate_watchlist")) {
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray((type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast)) ? "user_data" : "cast_data"));
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String response = jsonObject.getString("response");
                if (response.equals(getString(R.string.response_success))) {
                    addData(jsonArray);
                }else if (response.equals(getString(R.string.exception))) {
                    Toast.makeText(context, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    textViewWatchlistPlaceholder.setText(context.getString(R.string.unexpected));
                    textViewWatchlistPlaceholder.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    btn_click.setVisibility(View.GONE);
                }else if (response.equals(getString(R.string.response_unsuccessful))) {
                    textViewWatchlistPlaceholder.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    btn_click.setVisibility(View.GONE);
                }
            }else if(sqlHelper.getActionString().equals("watching")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    if(isWatched){
                        btnEditProfile.setText(context.getString(R.string.follow_button_watching));
                        btnEditProfile.setBackground(context.getDrawable(R.drawable.custom_button_yellow));
                        isWatched = false;
                    }else{
                        btnEditProfile.setText(context.getString(R.string.follow_button_watched));
                        btnEditProfile.setBackground(context.getDrawable(R.drawable.custom_button_white));
                        isWatched = true;
                    }
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(isWatched)
                        Toast.makeText(context, "Failed to remove from watching. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watching. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().equals("watchlist")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    if(isAddedToWatchlist){
                        btnWatchlist.setText(context.getString(R.string.follow_button_watchlist_add));
                        btnWatchlist.setBackground(context.getDrawable(R.drawable.custom_button_yellow));
                        isAddedToWatchlist = false;
                    }else{
                        btnWatchlist.setText(context.getString(R.string.follow_button_watchlist_remove));
                        btnWatchlist.setBackground(context.getDrawable(R.drawable.custom_button_white));
                        isAddedToWatchlist = true;
                    }
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(isAddedToWatchlist)
                        Toast.makeText(context, "Failed to remove from watchlist. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watchlist. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().equals("follow")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    if(isFollowing){
                        btnEditProfile.setText(context.getString(R.string.follow_button_follow));
                        btnEditProfile.setBackground(context.getDrawable(R.drawable.custom_button_yellow));
                        isFollowing = false;
                    }else{
                        btnEditProfile.setText(context.getString(R.string.follow_button_following));
                        btnEditProfile.setBackground(context.getDrawable(R.drawable.custom_button_white));
                        isFollowing = true;
                    }
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(isFollowing)
                        Toast.makeText(context, "Could not remove from following. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Could not follow. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}