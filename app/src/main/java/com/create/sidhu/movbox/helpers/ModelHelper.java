package com.create.sidhu.movbox.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.PreferenceModel;
import com.create.sidhu.movbox.models.RatingsModel;
import com.create.sidhu.movbox.models.ReviewModel;
import com.create.sidhu.movbox.models.UpdatesModel;
import com.create.sidhu.movbox.models.UserModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nihalpradeep on 05/09/18.
 */

public class ModelHelper {
    private ActorModel ActorModel;
    private FavouritesModel FavouritesModel;
    private HomeModel HomeModel;
    private MovieModel MovieModel;
    private UserModel UserModel;
    private Context context;

    public ModelHelper(Context context){
        this.context = context;
    }

    public void setActorModel(){

    }
    public void setFavouritesModel(){

    }
    public void setHomeModel(){

    }
    public void setMovieModel(){

    }
    public void setUserModel(){

    }

    /***
     * Populates MovieModel members from jsonObject
     * @param jsonObject
     * @return MovieModel object movieModel
     */
    public MovieModel buildMovieModel(JSONObject jsonObject){
        try {
            MovieModel movieModel = new MovieModel();
            movieModel.setId(jsonObject.getString("m_id"));
            movieModel.setName(jsonObject.getString("m_name"));
            movieModel.setImage(context.getString(R.string.master_url) + context.getString(R.string.movie_image_portrait_url) + jsonObject.getString("m_id") + ".jpg");
            movieModel.setCensorRating(jsonObject.getString("censor"));
            movieModel.setDuration(Integer.parseInt(jsonObject.getString("duration")));
            movieModel.setRelease(jsonObject.getString("release"));
            movieModel.setGenre(jsonObject.getString("genre").replace("!~", ","));
            movieModel.setStory(jsonObject.getString("story"));
            movieModel.setTotalWatched(Integer.parseInt(jsonObject.getString("total_watched")));
            movieModel.setTotalRatings(Integer.parseInt(jsonObject.getString("total_ratings")));
            movieModel.setTotalReviews(Integer.parseInt(jsonObject.getString("total_reviews")));
            movieModel.setRating("" + (int)Float.parseFloat(jsonObject.getString("rating")));
            movieModel.setDisplayDimension(jsonObject.getString("dimension"));
            movieModel.setCast(jsonObject.getString("director") + "!~" + jsonObject.getString("actor") + "!~" + jsonObject.getString("actress") + "!~" + jsonObject.getString("screenplay") + "!~" + jsonObject.getString("music"));
            movieModel.setWatched(jsonObject.getString("is_watched").equals("1"));
            movieModel.setAddedToWatchlist(jsonObject.getString("is_watchlist").equals("1"));
            movieModel.setRated(jsonObject.getString("is_rated").equals("1"));
            movieModel.setLanguage(jsonObject.getString("language"));
            movieModel.setReviewed(jsonObject.getString("is_reviewed").equals("1"));
            return movieModel;
        }catch(Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new MovieModel();
    }
    /***
     * Populates ActorModel members from jsonObject
     * @param jsonObject
     * @return ActorModel object actorModel
     */
    public ActorModel buildActorModel(JSONObject jsonObject){
        try{
            ActorModel actorModel = new ActorModel();
            actorModel.setId(jsonObject.getString("id"));
            actorModel.setName(jsonObject.getString("name"));
            actorModel.setImage(context.getString(R.string.master_url) + context.getString(R.string.cast_image_url) + jsonObject.getString("id") + ".jpg");
            actorModel.setRating(Float.parseFloat(jsonObject.getString("m_rating")));
            actorModel.setAverageRating(Float.parseFloat(jsonObject.getString("rating")));
            actorModel.setType(jsonObject.getString("type"));
            actorModel.setTotalMovies(Integer.parseInt(jsonObject.getString("total_movies")));
            return actorModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new ActorModel();
    }
    /***
     * Populates UserModel members from jsonObject
     * @param jsonObject
     * @return UserModel object userModel
     */
    public UserModel buildUserModel(JSONObject jsonObject){
        try{
            UserModel userModel = new UserModel();
            userModel.setUserId(jsonObject.getString("u_id"));
            userModel.setImage(context.getString(R.string.master_url) + context.getString(R.string.profile_image_url) + jsonObject.getString("u_id") + ".jpg");
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
            userModel.setPrivacy(Integer.parseInt(jsonObject.getString("u_privacy")));
            userModel.setIsFollowing(jsonObject.getString("is_following").equals("1"));
            return userModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new UserModel();
    }
    /***
     * Populates Favourites members from jsonObject
     * @param jsonObject
     * @return Favourites object userModel
     */
    public FavouritesModel buildFavouritesModel(JSONObject jsonObject, String type){
        try{
            FavouritesModel favouritesModel = new FavouritesModel();
            switch (type){
                case "user":
                    favouritesModel.setUser(buildUserModel(jsonObject));
                    favouritesModel.setType(type);
                    break;
                case "movie":
                    favouritesModel.setMovie(buildMovieModel(jsonObject));
                    favouritesModel.setType(type);
                    break;
                case "favourites":{
                    favouritesModel.setId(jsonObject.getString("id"));
                    favouritesModel.setType(type);
                    favouritesModel.setRead(jsonObject.getString("has_seen").equals("1"));
                    favouritesModel.setSubType(jsonObject.getString("type"));
                    favouritesModel.setDate(jsonObject.getString("time").split(" ")[0]);
                    favouritesModel.setTime(jsonObject.getString("time").split(" ")[1]);
                    favouritesModel.setDateTime(jsonObject.getString("time"));
                    switch (favouritesModel.getSubType()){
                        case "follow":
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results")));
                            break;
                        case "review":
                            favouritesModel.setSubtitle("\"" + jsonObject.getString("review_text") + "\"");
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results").getJSONObject("user_data")));
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results").getJSONObject("movie_data")));
                            break;
                        case "rating":
                            favouritesModel.setSubtitle("Rated: " + jsonObject.getString("rating") + "/10");
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results").getJSONObject("user_data")));
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results").getJSONObject("movie_data")));
                            break;
                        case "review_vote":
                            favouritesModel.setSubtitle("Upvotes: " + jsonObject.getString("review_upvotes") + " Downvotes: " + jsonObject.getString("review_downvotes"));
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results").getJSONObject("user_data")));
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results").getJSONObject("movie_data")));
                            break;
                        case "watching":
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results").getJSONObject("user_data")));
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results").getJSONObject("movie_data")));
                            break;
                        case "review_reminder":
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results")));
                            break;
                        case "watchlist_reminder":
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results")));
                            break;
                        case "watching_now":
                            favouritesModel.setUser(buildUserModel(jsonObject.getJSONObject("results").getJSONObject("user_data")));
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results").getJSONObject("movie_data")));
                            break;
                        case "new_releases":
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results")));
                            break;
                        case "recommendations":
                            favouritesModel.setMovie(buildMovieModel(jsonObject.getJSONObject("results")));
                            break;
                    }
                    break;
                }
                case "following":{
                    favouritesModel.setType("user");
                    favouritesModel.setUser(buildUserModel(jsonObject));
//                    favouritesModel.setUserId(jsonObject.getString("u_id"));
//                    favouritesModel.setTitle(jsonObject.getString("name"));
//                    favouritesModel.setSubtitle(jsonObject.getString("movies_watched"));
//                    favouritesModel.setDate("Following: " + jsonObject.getString("following"));
//                    favouritesModel.setTime("Followers: " + jsonObject.getString("followers"));
                    break;
                }
                case "followers":{
                    favouritesModel.setType("user");
                    favouritesModel.setUser(buildUserModel(jsonObject));
//                    favouritesModel.setUserId(jsonObject.getString("u_id"));
//                    favouritesModel.setTitle(jsonObject.getString("name"));
//                    favouritesModel.setSubtitle(jsonObject.getString("movies_watched"));
//                    favouritesModel.setDate("Following: " + jsonObject.getString("following"));
//                    favouritesModel.setTime("Followers: " + jsonObject.getString("followers"));
                    break;
                }
            }
            return favouritesModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new FavouritesModel();
    }
    /***
     * Populates ReviewModel members from jsonObject
     * @param jsonObject
     * @return ReviewModel object userModel
     */
    public ReviewModel buildReviewModel(JSONObject jsonObject){
        try{
            ReviewModel reviewModel = new ReviewModel();
            reviewModel.setReviewId(jsonObject.getString("r_id"));
            reviewModel.setReviewText(jsonObject.getString("r_text"));
            reviewModel.setLikes(Integer.parseInt(jsonObject.getString("upvotes")));
            reviewModel.setUserId(jsonObject.getString("u_id"));
            reviewModel.setUserName(jsonObject.getString("u_name"));
            reviewModel.setMovieId(jsonObject.getString("m_id"));
            reviewModel.setMovieName(jsonObject.getString("m_name"));
            reviewModel.setReplies(jsonObject.getString("r_reply"));
            reviewModel.setTime(jsonObject.getString("r_time"));
            reviewModel.setFollowing(jsonObject.getString("is_following").equals("1"));
            reviewModel.setLiked(jsonObject.getString("is_liked").equals("1"));
            reviewModel.setUserPrivacy(Integer.parseInt(jsonObject.getString("u_privacy")));
            return reviewModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new ReviewModel();
    }
    /***
     * Populates RatingsModel members from jsonObject
     * @param jsonObject
     * @return RatingsModel object ratingsModel
     */
    public RatingsModel buildRatingsModel(JSONObject jsonObject){
        try{
            RatingsModel ratingsModel = new RatingsModel();
            ratingsModel.setRatingId(jsonObject.getString("r_id"));
            ratingsModel.setUserName(jsonObject.getString("u_name"));
            ratingsModel.setUserId(jsonObject.getString("u_id"));
            ratingsModel.setUserRating(Float.parseFloat(jsonObject.getString("u_rating")));
            ratingsModel.setFollowing(jsonObject.getString("is_following").equals("1"));
            return ratingsModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new RatingsModel();
    }
    /***
     * Populates Preference members from jsonObject
     * @param jsonObject
     * @return PreferenceModel object preferenceModel
     */
    public PreferenceModel buildPreferenceModel(JSONObject jsonObject, String type){
        try{
            PreferenceModel preferenceModel = new PreferenceModel();
            preferenceModel.setType(type);
            preferenceModel.setId(jsonObject.getString("id"));
            preferenceModel.setName(jsonObject.getString("name"));
            return preferenceModel;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return new PreferenceModel();
    }

    /***
     * Builds bundle required for corresponding fragment
     * @param movieModel- MovieModel object
     * @param requestPath- Fragment path to which the bundle is to be attached
     * @return bundle
     */
    public Bundle buildMovieModelBundle(MovieModel movieModel, String requestPath){
        Bundle bundle = new Bundle();
        try{
            if(requestPath.equals("ProfileFragment")){
                bundle.putString("id", movieModel.getId());
                bundle.putString("type",context.getString(R.string.profile_movies));
                bundle.putString("name", movieModel.getName());
                bundle.putString("image", movieModel.getImage());
                bundle.putInt("movies_watched", movieModel.getTotalWatched());
                bundle.putInt("movies_reviewed", movieModel.getTotalReviews());
                bundle.putString("genre", movieModel.getGenre());
                bundle.putString("display_dimension", movieModel.getDisplayDimension());
                bundle.putInt("duration", movieModel.getDuration());
                bundle.putString("censor_rating", movieModel.getCensorRating());
                bundle.putString("rating", movieModel.getRating());
                bundle.putString("total_ratings", "" + movieModel.getTotalRatings());
                bundle.putString("cast", movieModel.getCast());
                bundle.putBoolean("is_watched", movieModel.getIsWatched());
                bundle.putBoolean("is_watchlist", movieModel.getIsAddedToWatchlist());
                bundle.putBoolean("is_rated", movieModel.getIsRated());
                bundle.putString("language", movieModel.getLanguage());
                bundle.putString("story", movieModel.getStory());
                bundle.putString("release", new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(movieModel.getRelease()));
                bundle.putBoolean("is_reviewed", movieModel.getIsReviewed());
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return bundle;
    }
    /***
     * Builds bundle required for corresponding fragment
     * @param userModel- UserModel object
     * @param requestPath- Fragment path to which the bundle is to be attached
     * @return bundle
     */
    public Bundle buildUserModelBundle(UserModel userModel, String requestPath){
        Bundle bundle = new Bundle();
        try{
            if(requestPath.equals("ProfileFragment")) {
                bundle.putString("type", context.getString(R.string.profile_user));
                bundle.putBoolean("isIdentity", userModel.getUserId().equals(MainActivity.currentUserModel.getUserId()));
                bundle.putString("id", userModel.getUserId());
                bundle.putString("name", userModel.getName());
                bundle.putString("image", userModel.getImage());
                bundle.putInt("movies_watched", userModel.getTotalWatched());
                bundle.putInt("movies_reviewed", userModel.getTotalReviews());
                bundle.putInt("followers", userModel.getFollowers());
                bundle.putInt("following", userModel.getFollowing());
                bundle.putString("privacy", userModel.getPrivacy());
                bundle.putBoolean("is_following", userModel.getIsFollowing());
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return bundle;
    }
    /***
     * Builds bundle required for corresponding fragment
     * @param actorModel- ActorModel object
     * @param requestPath- Fragment path to which the bundle is to be attached
     * @return bundle
     */
    public Bundle buildActorModelBundle(ActorModel actorModel, String requestPath){
        Bundle bundle = new Bundle();
        try{
            if(requestPath.equals("ProfileFragment")) {
                bundle.putString("type", "cast");
                bundle.putBoolean("isIdentity", false);
                bundle.putString("name", actorModel.getName());
                bundle.putString("image", actorModel.getImage());
                bundle.putString("cast_type", actorModel.getType());
                bundle.putFloat("rating", actorModel.getAverageRating());
                bundle.putInt("movies", actorModel.getTotalMovies());
                bundle.putString("id", actorModel.getId());
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return bundle;
    }
    /***
     * Builds bundle required for corresponding fragment
     * @param favouritesModel- Favourites Model object
     * @param requestPath- Fragment path to which the bundle is to be attached
     * @return bundle
     */
    public Bundle buildReviewModelBundle(FavouritesModel favouritesModel, String requestPath){
        Bundle bundle = new Bundle();
        try{
            if(requestPath.equals("HomeFragment")) {
                bundle.putString("type", "movie");
                bundle.putString("user_id", MainActivity.currentUserModel.getUserId());
                bundle.putString("user_name", MainActivity.currentUserModel.getName());
                bundle.putString("user_image", MainActivity.currentUserModel.getImage());
                bundle.putString("movie_id", favouritesModel.getMovie().getId());
                bundle.putString("movie_name", favouritesModel.getMovie().getName());
                bundle.putString("movie_genre", favouritesModel.getMovie().getGenre());
                bundle.putString("movie_dimension", favouritesModel.getMovie().getDisplayDimension());
                bundle.putString("movie_language", favouritesModel.getMovie().getLanguage());
            }
            else if(requestPath.equals("Notification")) {
                bundle.putString("type", "movie");
                bundle.putString("user_id", favouritesModel.getUser().getUserId());
                bundle.putString("user_name", favouritesModel.getUser().getName());
                bundle.putString("user_image", favouritesModel.getUser().getImage());
                bundle.putString("movie_id", favouritesModel.getMovie().getId());
                bundle.putString("movie_name", favouritesModel.getMovie().getName());
                bundle.putString("movie_genre", favouritesModel.getMovie().getGenre());
                bundle.putString("movie_dimension", favouritesModel.getMovie().getDisplayDimension());
                bundle.putString("movie_language", favouritesModel.getMovie().getLanguage());
            }

        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return bundle;
    }
    /***
     * Builds bundle required for corresponding fragment
     * @param movieModel- HomeModel object
     * @param requestPath- Fragment path to which the bundle is to be attached
     * @return bundle
     */
    public Bundle buildReviewModelBundle(MovieModel movieModel, String requestPath){
        Bundle bundle = new Bundle();
        try{
            if(requestPath.equals("PostStatusFragment")) {
                bundle.putString("type", "movie");
                bundle.putString("user_id", MainActivity.currentUserModel.getUserId());
                bundle.putString("user_name", MainActivity.currentUserModel.getName());
                bundle.putString("user_image", MainActivity.currentUserModel.getImage());
                bundle.putString("movie_id", movieModel.getId());
                bundle.putString("movie_name", movieModel.getName());
                bundle.putString("movie_genre", movieModel.getGenre());
                bundle.putString("movie_dimension", movieModel.getDisplayDimension());
                bundle.putString("movie_language", movieModel.getLanguage());
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return bundle;
    }

    /***
     *
     * @param movieId - Id of the Movie
     * @param reviewId- Id of the Review
     * @param type- Type identifier
     */
    public void addToUpdatesModel(String movieId, String reviewId, String type){
        try {
            UpdatesModel updatesModel = new UpdatesModel();
            updatesModel.setId("" + System.currentTimeMillis());
            updatesModel.setUserId(MainActivity.currentUserModel.getUserId());
            updatesModel.setReviewId(reviewId);
            updatesModel.setMovieId(movieId);
            updatesModel.setType(type);
            updatesModel.setUpdated(false);
            if (MainActivity.updatesModels == null)
                MainActivity.updatesModels = new ArrayList<>();
            MainActivity.updatesModels.add(updatesModel);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ModelHelper", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

}
