package com.create.sidhu.movbox.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.GlideApp;
import com.create.sidhu.movbox.Interfaces.CallbackDelegate;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.ProfileImage;
import com.create.sidhu.movbox.activities.ReviewsActivity;
import com.create.sidhu.movbox.activities.SettingsActivity;
import com.create.sidhu.movbox.adapters.ActorAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

import static com.create.sidhu.movbox.helpers.StringHelper.formatTextCount;
import static com.create.sidhu.movbox.helpers.StringHelper.toSentenceCase;

//Reproduction steps:
//1: Make watchlist gridview and click on any movie.
//2: New fragment appears with elements of old fragment still visible.
/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements SqlDelegate, CallbackDelegate {


    public ProfileFragment() {
        // Required empty public constructor
    }

    private ArrayList<MovieModel> movieModels;
    private ArrayList<ActorModel> actorModels;
    public static ArrayList<MovieModel> currentUserWatchlist;
    private Boolean bClick = true;
    private Context context;
    private String type;
    private Boolean isIdentity;
    private String privacy;
    private Boolean isFollowing;
    private Boolean isAddedToWatchlist;
    private Boolean isWatched;
    private Boolean isRated;
    private Boolean isReviewed;
    private String image;
    private String id;
    private String name;
    private String censorRating;
    private String rating;
    private String genre;
    private String totalRatings;
    private String cast;
    private String castType;
    private String language;
    private String story;
    private String release;
    private int duration;
    private String displayDimension;
    private int totalWatched;
    private int totalReviewed;
    private int followers;
    private int following;

    Typeface tfSemibold;
    Typeface tfRegular;

    RequestOptions requestOptions;
    View rootview, statSeparator1, statSeparator2;
    LinearLayout  llWatched, llMovieImage, llUserWatchInfo, llMovieInfo, llButtons, llFollowers, llFollowing, llReviews, llUserInfo, llSummary, llStat, llMovieStat;
    LinearLayout llWatchedMovie, llRatingMovie, llReviewMovie, llMovieName;
    RelativeLayout rlProfileImage, rlWatchlist;
    TextView textViewName, textViewWatched, textViewReviews, textViewFollowers, textViewFollowing, textViewGenre, textViewDuration, textViewCensorRating, textViewDisplayDimension, textViewFollowersText, textViewFollowingText, textViewWatchlistPlaceholder, textViewWatchlistText, textViewWatchedText;
    TextView tvSummaryText, tvSummaryTitle, tvMovieTotalWatched, tvMovieTotalRated, tvMovieRating, tvMovieTotalReviewed, tvMovieName, tvReleaseTitle, tvRelease;
    CircleImageView imgProfile, button_img;
    ImageView imgMovie, imgSettings, imgWatched, imgReview, imgRating, imgFavourites;
    ScrollView parentView;
    Button btn_click, btnEditProfile, btnWatchlist;
    RecyclerView recyclerView;
    FrameLayout flImageEdit;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        try {
            tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
            tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
            requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_placeholder);
            requestOptions.error(R.drawable.ic_placeholder);
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.GONE);
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_movies)));
            final Bundle bundle = getArguments();
            type = bundle.getString("type");
            id = bundle.getString("id");
            image = bundle.getString("image");
            name = bundle.getString("name");
            totalWatched = bundle.getInt("movies_watched");
            totalReviewed = bundle.getInt("movies_reviewed");
            if (type.equals(context.getString(R.string.profile_user))) {
                isIdentity = bundle.getBoolean("isIdentity");
                followers = bundle.getInt("followers");
                following = bundle.getInt("following");
                privacy = bundle.getString("privacy");
                movieModels = new ArrayList<>();
                if (!isIdentity) {
                    isFollowing = bundle.getBoolean("is_following");
                }
                toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_profile)));
            } else if (type.equals(context.getString(R.string.profile_movies))) {
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
                isRated = bundle.getBoolean("is_rated");
                isReviewed = bundle.getBoolean("is_reviewed");
                language = bundle.getString("language");
                story = bundle.getString("story");
                release = bundle.getString("release");
                toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.profile_movies)));
            } else if (type.equals(context.getString(R.string.profile_cast))) {
                castType = bundle.getString("cast_type");
                rating = "" + bundle.getFloat("rating");
                totalWatched = bundle.getInt("movies");
                movieModels = new ArrayList<>();
                toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.profile_cast)));
            }
            StringHelper.changeToolbarFont(toolbar, (MainActivity) context);

            rootview = inflater.inflate(R.layout.fragment_profile, container, false);
            statSeparator1 = rootview.findViewById(R.id.viewStatSeparator1);
            statSeparator2 = rootview.findViewById(R.id.viewStatSeparator2);
            parentView = (ScrollView) rootview.findViewById(R.id.parentView);
            flImageEdit = (FrameLayout) rootview.findViewById(R.id.containerImageEdit);
            llWatched = (LinearLayout) rootview.findViewById(R.id.containerWatched);
            llMovieImage = (LinearLayout) rootview.findViewById(R.id.containerMovieImage);
            llUserWatchInfo = (LinearLayout) rootview.findViewById(R.id.containerUserWatchInfo);
            llMovieInfo = (LinearLayout) rootview.findViewById(R.id.containerMovieInfo);
            llButtons = (LinearLayout) rootview.findViewById(R.id.containerFollowButton);
            llFollowers = (LinearLayout) rootview.findViewById(R.id.containerFollowers);
            llFollowing = (LinearLayout) rootview.findViewById(R.id.containerFollowing);
            llReviews = (LinearLayout) rootview.findViewById(R.id.containerReviews);
            llUserInfo = (LinearLayout) rootview.findViewById(R.id.containerOtherInfo);
            llSummary = (LinearLayout) rootview.findViewById(R.id.containerMovieSynopsis);
            llStat = (LinearLayout) rootview.findViewById(R.id.containerStat);
            llMovieStat = (LinearLayout) rootview.findViewById(R.id.containerMovieButtons);
            llWatchedMovie = (LinearLayout) rootview.findViewById(R.id.containerWatchedStat);
            llReviewMovie = (LinearLayout) rootview.findViewById(R.id.containerReviewsStats);
            llRatingMovie = (LinearLayout) rootview.findViewById(R.id.containerRatingStat);
            llMovieName = (LinearLayout) rootview.findViewById(R.id.containerMovieNameInfo);
            rlProfileImage = (RelativeLayout) rootview.findViewById(R.id.containerUserImage);
            rlWatchlist = (RelativeLayout) rootview.findViewById(R.id.RelLayouts2);
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
            tvSummaryText = rootview.findViewById(R.id.textView_Summary_Text);
            tvSummaryTitle = rootview.findViewById(R.id.textView_Summary_Title);
            tvMovieTotalRated = rootview.findViewById(R.id.textView_TotalRatings);
            tvMovieRating = rootview.findViewById(R.id.textView_Ratings);
            tvMovieTotalReviewed = rootview.findViewById(R.id.textView_TotalReviewed);
            tvMovieTotalWatched = rootview.findViewById(R.id.textView_TotalWatched);
            tvMovieName = rootview.findViewById(R.id.movie_name);
            tvReleaseTitle = rootview.findViewById(R.id.textView_Release_Title);
            tvRelease = rootview.findViewById(R.id.textView_Release);
            recyclerView = rootview.findViewById(R.id.recyclerView);
            imgProfile = rootview.findViewById(R.id.profile_image);
            imgMovie = rootview.findViewById(R.id.imgMoviePoster);
            imgSettings = rootview.findViewById(R.id.img_Settings);
            imgWatched = rootview.findViewById(R.id.img_Watched);
            imgRating = rootview.findViewById(R.id.img_Rating);
            imgReview = rootview.findViewById(R.id.img_Review);
            imgFavourites = rootview.findViewById(R.id.img_Favourites);
            btn_click = rootview.findViewById(R.id.btn_click);
            btnEditProfile = rootview.findViewById(R.id.btn_editProfile);
            btnWatchlist = rootview.findViewById(R.id.btn_watchlist);
            button_img = rootview.findViewById(R.id.btn_profile_image);
            btn_click.setVisibility(View.GONE);
            llUserInfo.setVisibility(View.GONE);
            modifyTypeface();
            populateView();

            llUserWatchInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type.equals(context.getString(R.string.profile_user))) {
                        bundle.putString("return_path", "ProfileFragment");
                        Intent intent = new Intent(context, FollowReviewActivity.class);
                        intent.putExtra("type", "watched");
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                }
            });
            // onClickListener for Review count
            llReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("movie")) {
                        bundle.putString("return_path", "ProfileFragment");
                        bundle.putString("type", type);
                        bundle.putString("user_id", MainActivity.currentUserModel.getUserId());
                        bundle.putString("user_name", MainActivity.currentUserModel.getName());
                        bundle.putString("user_image", MainActivity.currentUserModel.getImage());
                        bundle.putString("movie_id", id);
                        bundle.putString("movie_name", name);
                        bundle.putString("movie_genre", genre);
                        bundle.putString("movie_dimension", displayDimension);
                        bundle.putString("movie_language", language);
                        Intent intent = new Intent(context, ReviewsActivity.class);
                        intent.putExtra("type", "review");
                        intent.putExtra("bundle", bundle);
                        ReviewsActivity.currentFragment = ProfileFragment.this;
                        startActivity(intent);
                    } else if (type.equals("user")) {
                        if (!isIdentity && privacy.charAt(1) == '0' && !isFollowing) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(R.string.private_account_title);
                            alertDialog.setMessage(R.string.private_account_message);
                            alertDialog.setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        } else {
                            bundle.putString("return_path", "ProfileFragment");
                            bundle.putString("type", type);
                            bundle.putString("user_id", id);
                            bundle.putString("user_name", name);
                            bundle.putString("user_image", image);
                            Intent intent = new Intent(context, ReviewsActivity.class);
                            intent.putExtra("type", "review");
                            intent.putExtra("bundle", bundle);
                            ReviewsActivity.currentFragment = null;
                            startActivity(intent);
                        }
                    }
                }
            });

            // onClickListener for Followers
            llFollowers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals(context.getString(R.string.profile_user))) {
                        if (!isIdentity && privacy.charAt(1) == '0' && !isFollowing) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(R.string.private_account_title);
                            alertDialog.setMessage(R.string.private_account_message);
                            alertDialog.setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        } else {
                            bundle.putString("return_path", "ProfileFragment");
                            Intent intent = new Intent(context, FollowReviewActivity.class);
                            intent.putExtra("profile_type", type);
                            intent.putExtra("type", "followers");
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                        }
                    }

                }
            });

            // onClickListener for Followers
            llFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("return_path", "ProfileFragment");
                    Intent intent = new Intent(context, FollowReviewActivity.class);
                    if (type.equals(context.getString(R.string.profile_user))) {
                        if (!isIdentity && privacy.charAt(1) == '0' && !isFollowing) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(R.string.private_account_title);
                            alertDialog.setMessage(R.string.private_account_message);
                            alertDialog.setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        } else {
                            intent.putExtra("profile_type", type);
                            intent.putExtra("type", "following");
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                        }
                    } else if (type.equals(context.getString(R.string.profile_movies))) {
                        bundle.putString("type", "list");
                        bundle.putString("rating", rating);
                        bundle.putString("total_ratings", totalRatings);
                        bundle.putBoolean("is_rated", isRated);
                        RatingsDialog ratingsDialog = new RatingsDialog();
                        ratingsDialog.setRated(isRated);
                        ratingsDialog.setCallbackDelegate(ProfileFragment.this);
                        MainActivity mainActivity = (MainActivity) context;
                        mainActivity.initFragment(ratingsDialog, bundle);
                    }

                }
            });

            // onClickListener for Profile Image

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileImage.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
//                ProfileImageFragment fragment = new ProfileImageFragment();
//                MainActivity activity = (MainActivity) context;
//                activity.initFragment(fragment, bundle);

                }
            };
            if (type.equals("user")) {
                if (isIdentity) {
                    imgProfile.setOnClickListener(onClickListener);
                    button_img.setOnClickListener(onClickListener);
                }
            }
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
                    if (type.equals(context.getString(R.string.profile_user))) {
                        if (isIdentity) {

                        } else {
                            SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                            sqlHelper.setExecutePath("update-following.php");
                            sqlHelper.setActionString("follow");
                            sqlHelper.setMethod("GET");
                            ContentValues params = new ContentValues();
                            params.put("c_id", MainActivity.currentUserModel.getUserId());
                            params.put("u_id", id);
                            params.put("is_following", isFollowing.toString());
                            sqlHelper.setParams(params);
                            sqlHelper.executeUrl(true);
                        }
                    } else if (type.equals(context.getString(R.string.profile_movies))) {
                        SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                        sqlHelper.setExecutePath("update-watching.php");
                        sqlHelper.setActionString("watching");
                        sqlHelper.setMethod("GET");
                        ContentValues params = new ContentValues();
                        params.put("m_id", id);
                        params.put("u_id", MainActivity.currentUserModel.getUserId());
                        params.put("is_watched", isWatched.toString());
                        sqlHelper.setParams(params);
                        sqlHelper.executeUrl(true);

                    } else if (type.equals(context.getString(R.string.profile_cast))) {

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
                    ContentValues params = new ContentValues();
                    params.put("m_id", id);
                    params.put("u_id", MainActivity.currentUserModel.getUserId());
                    params.put("is_watchlist", isAddedToWatchlist.toString());
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                }
            });

            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, SettingsActivity.class));
                }
            });

            imgReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", "movie");
                    bundle1.putString("user_id", MainActivity.currentUserModel.getUserId());
                    bundle1.putString("user_name", MainActivity.currentUserModel.getName());
                    bundle1.putString("user_image", MainActivity.currentUserModel.getImage());
                    bundle1.putString("movie_id", id);
                    bundle1.putString("movie_name", name);
                    bundle1.putString("movie_genre", genre);
                    bundle1.putString("movie_dimension", displayDimension);
                    bundle1.putString("movie_language", language);
                    Intent intent = new Intent(context, ReviewsActivity.class);
                    intent.putExtra("bundle", bundle1);
                    ReviewsActivity.currentFragment = ProfileFragment.this;
                    startActivity(intent);
                }
            });

            imgWatched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                    sqlHelper.setExecutePath("update-watching.php");
                    sqlHelper.setActionString("watching");
                    sqlHelper.setMethod("GET");
                    ContentValues params = new ContentValues();
                    params.put("m_id", id);
                    params.put("u_id", MainActivity.currentUserModel.getUserId());
                    params.put("is_watched", isWatched.toString());
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                    imgWatched.setImageDrawable(isWatched ? context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                }
            });
            imgRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("type", "cast");
                    bundle.putString("rating", rating);
                    bundle.putString("total_ratings", totalRatings);
                    bundle.putBoolean("is_rated", isRated);
                    RatingsDialog ratingsDialog = new RatingsDialog();
                    ratingsDialog.setRated(isRated);
                    ratingsDialog.setCallbackDelegate(ProfileFragment.this);
                    ((MainActivity) context).initFragment(ratingsDialog, bundle);
                }
            });
            imgFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqlHelper sqlHelper = new SqlHelper(context, ProfileFragment.this);
                    sqlHelper.setExecutePath("update-watchlist.php");
                    sqlHelper.setActionString("watchlist");
                    sqlHelper.setMethod("GET");
                    ContentValues params = new ContentValues();
                    params.put("m_id", id);
                    params.put("u_id", MainActivity.currentUserModel.getUserId());
                    params.put("is_watchlist", isAddedToWatchlist.toString());
                    sqlHelper.setParams(params);
                    sqlHelper.executeUrl(true);
                    currentUserWatchlist = null;
                }
            });
            llWatchedMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("return_path", "ProfileFragment");
                    Intent intent = new Intent(context, FollowReviewActivity.class);
                    intent.putExtra("profile_type", type);
                    intent.putExtra("type", "followers");
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            });
            llReviewMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", "movie");
                    bundle1.putString("user_id", MainActivity.currentUserModel.getUserId());
                    bundle1.putString("user_name", MainActivity.currentUserModel.getName());
                    bundle1.putString("user_image", MainActivity.currentUserModel.getImage());
                    bundle1.putString("movie_id", id);
                    bundle1.putString("movie_name", name);
                    bundle1.putString("movie_genre", genre);
                    bundle1.putString("movie_dimension", displayDimension);
                    bundle1.putString("movie_language", language);
                    Intent intent = new Intent(context, ReviewsActivity.class);
                    intent.putExtra("bundle", bundle1);
                    ReviewsActivity.currentFragment = ProfileFragment.this;
                    startActivity(intent);
                }
            });
            llRatingMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("type", "list");
                    bundle.putString("rating", rating);
                    bundle.putString("total_ratings", totalRatings);
                    bundle.putBoolean("is_rated", isRated);
                    RatingsDialog ratingsDialog = new RatingsDialog();
                    ((MainActivity) context).initFragment(ratingsDialog, bundle);
                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return rootview;
    }

    private void modifyTypeface(){
        textViewName.setTypeface(tfSemibold);
        tvMovieName.setTypeface(tfSemibold);
        textViewFollowers.setTypeface(tfSemibold);
        textViewReviews.setTypeface(tfSemibold);
        textViewFollowing.setTypeface(tfSemibold);
        textViewWatchlistText.setTypeface(tfSemibold);
        tvSummaryTitle.setTypeface(tfSemibold);
        tvMovieTotalWatched.setTypeface(tfSemibold);
        tvMovieTotalReviewed.setTypeface(tfSemibold);
        tvMovieRating.setTypeface(tfSemibold);
        tvReleaseTitle.setTypeface(tfSemibold);
    }

    /***
     * Populates the views based on page roles
     */
    private void populateView(){
        try {
            textViewName.setText(name);
            if (type.equals(context.getString(R.string.profile_user))) {
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.ic_user);
//            requestOptions.error(R.drawable.ic_user);
                GlideApp.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .asBitmap()
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgProfile);
                textViewWatched.setText(formatTextCount(totalWatched));
                textViewReviews.setText(formatTextCount(totalReviewed));
                textViewFollowers.setText(formatTextCount(followers));
                textViewFollowing.setText(formatTextCount(following));
                textViewWatchlistText.setText(context.getString(R.string.profile_watchlist_text));
                if (!isIdentity) {
                    btnEditProfile.setText(isFollowing ? context.getString(R.string.follow_button_following) : context.getString(R.string.follow_button_follow));
                    if (privacy.charAt(1) == '0' && !isFollowing) {
                        rlWatchlist.setVisibility(View.GONE);
                        llUserInfo.setVisibility(View.GONE);
                    }
                } else {
                    imgSettings.setVisibility(View.VISIBLE);
                    btnEditProfile.setVisibility(View.GONE);
                    flImageEdit.setVisibility(View.VISIBLE);
                }
                if (currentUserWatchlist != null && isIdentity) {
                    movieModels = currentUserWatchlist;
                    initRecyclerView();
                } else {
                    ContentValues params = new ContentValues();
                    params.put("u_id", id);
                    populateWatchlist("get-watchlist.php", params);
                }
            } else if (type.equals(context.getString(R.string.profile_movies))) {
                btn_click.setVisibility(View.GONE);
                rlProfileImage.setVisibility(View.GONE);
                llMovieImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.film_placeholder)
                                .error(R.drawable.film_placeholder)
                        )
                        .asBitmap()
                        .load(image.replace("portrait", "landscape"))
                        .into(imgMovie);
                textViewName.setVisibility(View.GONE);
                tvMovieName.setText(name);
                llMovieName.setVisibility(View.VISIBLE);
                llUserWatchInfo.setVisibility(View.GONE);
                llMovieInfo.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);
                llStat.setVisibility(View.GONE);
                llMovieStat.setVisibility(View.VISIBLE);
                textViewGenre.setText(genre);
                textViewDuration.setText("" + duration + "min");
                textViewDisplayDimension.setText(displayDimension);
                textViewCensorRating.setText(censorRating);
                textViewReviews.setText(formatTextCount(totalReviewed));
                tvRelease.setText(release);
                llSummary.setVisibility(View.VISIBLE);
                tvSummaryText.setText(StringHelper.toSentenceCase(story));
                textViewWatchlistText.setVisibility(View.GONE);
                textViewWatchlistText.setText("Cast");
                imgReview.setImageDrawable(isReviewed ? context.getDrawable(R.drawable.ic_comment_filled) : context.getDrawable(R.drawable.ic_comment));
                imgRating.setImageDrawable(isRated ? context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                imgWatched.setImageDrawable(isWatched ? context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                imgFavourites.setImageDrawable(isAddedToWatchlist ? context.getDrawable(R.drawable.ic_bookmark_filled) : context.getDrawable(R.drawable.ic_bookmark));
                tvMovieTotalWatched.setText(StringHelper.formatTextCount(totalWatched));
                tvMovieTotalReviewed.setText(StringHelper.formatTextCount(totalReviewed));
                tvMovieRating.setText(rating + "/10");
                SpannableString spannableString = new SpannableString(StringHelper.formatTextCount(Integer.parseInt(totalRatings)));
                spannableString.setSpan(new CalligraphyTypefaceSpan(tfSemibold), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" from ");
                spannableStringBuilder.append(spannableString);
                spannableStringBuilder.append(" votes");
                tvMovieTotalRated.setText(spannableStringBuilder);
//            textViewFollowers.setText(formatTextCount(totalWatched));
//            textViewFollowersText.setText(context.getString(R.string.tab_watched));
//            textViewFollowing.setText(rating + "/10");
//            textViewFollowingText.setText("(" + totalRatings + ")");
//            btnEditProfile.setText(isWatched ? context.getString(R.string.follow_button_watched) : context.getString(R.string.follow_button_watching));
                //btnEditProfile.setBackground(isWatched ? context.getDrawable(R.drawable.custom_button_white) : context.getDrawable(R.drawable.custom_button_yellow));
//            btnWatchlist.setVisibility(View.VISIBLE);
//            btnWatchlist.setText(isAddedToWatchlist ? context.getString(R.string.follow_button_watchlist_remove) : context.getString(R.string.follow_button_watchlist_add));
                //btnWatchlist.setBackground(isAddedToWatchlist ? context.getDrawable(R.drawable.custom_button_white) : context.getDrawable(R.drawable.custom_button_yellow));

                ContentValues params = new ContentValues();
                params.put("cast", cast);
                params.put("m_id", id);
                populateWatchlist("get-cast.php", params);
            } else if (type.equals(context.getString(R.string.profile_cast))) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(image)
                        .into(imgProfile);
                btnEditProfile.setVisibility(View.GONE);
                textViewWatched.setVisibility(View.GONE);
                textViewWatchedText.setText(toSentenceCase(castType));
                textViewFollowing.setText(StringHelper.roundFloat(Float.parseFloat(rating), 1) + "/10");
                textViewFollowingText.setText("Rating");
                textViewFollowers.setText("" + totalWatched);
                textViewFollowersText.setText("Total Movies");
                llButtons.setVisibility(View.GONE);
                textViewWatchlistText.setText("Movies");
                llReviews.setVisibility(View.GONE);
                statSeparator2.setVisibility(View.GONE);
                button_img.setVisibility(View.GONE);
                ContentValues params = new ContentValues();
                params.put("id", id);
                params.put("c_id", MainActivity.currentUserModel.getUserId());
                populateWatchlist("get-cast-movies.php", params);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    /***
     * Calls the appropriate API to set Recyclerview
     * @param executePath - The API file to execute
     * @param params - The parameters in the API call
     */
    private void populateWatchlist(String executePath, ContentValues params){
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
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }

        }
        if(type.equals(context.getString(R.string.profile_user)) && isIdentity)
            currentUserWatchlist = movieModels;
        initRecyclerView();
    }

    /***
     * Sets appropriate adapter to Recylerview
     */
    private void initRecyclerView() {
        try {
            if (type.equals(context.getString(R.string.profile_movies))) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);            // Calling the RecyclerView Adapter with a layout
                recyclerView.setLayoutManager(layoutManager);
                if (type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast))) {
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels, rootview, "profile");
                    recyclerView.setAdapter(adapter);
                } else if (type.equals(context.getString(R.string.profile_movies))) {
                    ActorAdapter adapter = new ActorAdapter(context, actorModels, rootview);
                    recyclerView.setAdapter(adapter);
                }
            } else {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(gridLayoutManager);
                if (type.equals(context.getString(R.string.profile_user)) || type.equals(context.getString(R.string.profile_cast))) {
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels, rootview, "profile");
                    recyclerView.setAdapter(adapter);
                } else if (type.equals(context.getString(R.string.profile_movies))) {
                    ActorAdapter adapter = new ActorAdapter(context, actorModels, rootview);
                    recyclerView.setAdapter(adapter);
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
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
        //Toast.makeText(context,"Inside Profile",Toast.LENGTH_SHORT).show();
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
                    currentUserWatchlist = null;
                    if(isWatched){
                        isWatched = false;
                        Toast.makeText(context, "Movie has been marked as unwatched", Toast.LENGTH_SHORT).show();
                        int watching = MainActivity.currentUserModel.getTotalWatched();
                        MainActivity.currentUserModel.setTotalWatched(watching - 1);
                        totalWatched -= 1;
                        ((MainActivity) context).scheduleInstantJob("110");
                    }else{
                        isWatched = true;
                        Toast.makeText(context, "Movie has been marked as watched", Toast.LENGTH_SHORT).show();
                        new ModelHelper(context).addToUpdatesModel(id, "", "watching");
                        int watching = MainActivity.currentUserModel.getTotalWatched();
                        MainActivity.currentUserModel.setTotalWatched(watching + 1);
                        totalWatched += 1;
                        ((MainActivity) context).scheduleInstantJob("111");
                    }
                    tvMovieTotalWatched.setText("" + totalWatched);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(isWatched)
                        Toast.makeText(context, "Failed to remove from watching. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watching. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
                imgWatched.setImageDrawable(isWatched ? context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
            }else if(sqlHelper.getActionString().equals("watchlist")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    if(isAddedToWatchlist){
                        isAddedToWatchlist = false;
                        Toast.makeText(context, "Movie removed from favourites", Toast.LENGTH_SHORT).show();
                    }else{
                        isAddedToWatchlist = true;
                        currentUserWatchlist = null;
                        Toast.makeText(context, "Movie added to favourites", Toast.LENGTH_SHORT).show();
                    }
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(isAddedToWatchlist)
                        Toast.makeText(context, "Failed to remove from watchlist. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watchlist. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
                imgFavourites.setImageDrawable(isAddedToWatchlist ? context.getDrawable(R.drawable.ic_bookmark_filled)  : context.getDrawable(R.drawable.ic_bookmark));
            }else if(sqlHelper.getActionString().equals("follow")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    if(isFollowing){
                        btnEditProfile.setText(context.getString(R.string.follow_button_follow));
                        followers -= 1;
                        MainActivity.currentUserModel.setFollowing(MainActivity.currentUserModel.getFollowing() - 1);
                        textViewFollowers.setText(formatTextCount(followers));
                        isFollowing = false;
                    }else{
                        btnEditProfile.setText(context.getString(R.string.follow_button_following));
                        followers += 1;
                        MainActivity.currentUserModel.setFollowing(MainActivity.currentUserModel.getFollowing() + 1);
                        textViewFollowers.setText(formatTextCount(followers));
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
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    @Override
    public void onResultReceived(String type, boolean resultCode, HashMap<String, String> extras) {
        try {
            if (resultCode) {
                switch (type) {
                    case "review": {
                        int counter = Integer.parseInt(extras.get("counter"));
                        totalReviewed += counter;
                        tvMovieTotalReviewed.setText("" + totalReviewed);
                        isReviewed = true;
                        imgReview.setImageDrawable(context.getDrawable(R.drawable.ic_comment_filled));
                        ((MainActivity) context).scheduleInstantJob("111");
                        break;
                    }
                    case "rating": {
                        totalRatings = extras.get("total_ratings");
                        rating = extras.get("avg_ratings");
                        SpannableString spannableString = new SpannableString(StringHelper.formatTextCount(Integer.parseInt(totalRatings)));
                        spannableString.setSpan(new CalligraphyTypefaceSpan(tfSemibold), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" from ");
                        spannableStringBuilder.append(spannableString);
                        spannableStringBuilder.append(" votes");
                        tvMovieTotalRated.setText(spannableStringBuilder);
                        imgRating.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled));
                        tvMovieRating.setText(rating + "/10");
                        isRated = true;
                        break;
                    }
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ProfileFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}