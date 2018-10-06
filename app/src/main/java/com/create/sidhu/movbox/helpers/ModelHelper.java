package com.create.sidhu.movbox.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UserModel;

import org.json.JSONObject;

import java.util.Date;

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
            movieModel.setGenre(jsonObject.getString("genre"));
            movieModel.setStory(jsonObject.getString("story"));
            movieModel.setTotalWatched(Integer.parseInt(jsonObject.getString("total_watched")));
            movieModel.setTotalRatings(Integer.parseInt(jsonObject.getString("total_ratings")));
            movieModel.setTotalReviews(Integer.parseInt(jsonObject.getString("total_reviews")));
            movieModel.setRating(jsonObject.getString("rating"));
            movieModel.setDisplayDimension(jsonObject.getString("dimension"));
            movieModel.setCast(jsonObject.getString("director") + "!~" + jsonObject.getString("actor") + "!~" + jsonObject.getString("actress"));
            movieModel.setWatched(jsonObject.getString("is_watched").equals("1") ? true : false);
            movieModel.setAddedToWatchlist(jsonObject.getString("is_watchlist").equals("1") ? true : false);
            movieModel.setRated(jsonObject.getString("is_rated").equals("1") ? true : false);
            return movieModel;
        }catch(Exception e){
            Log.e("ModelHelper: build", e.getMessage());
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
            Log.e("ModelHelper: build", e.getMessage());
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
            return userModel;
        }catch (Exception e){
           Log.e("ModelHelper:build", e.getMessage());
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
                    favouritesModel.setSubType(jsonObject.getString("type"));
                    favouritesModel.setDate(jsonObject.getString("time").split(" ")[0]);
                    favouritesModel.setTime(jsonObject.getString("time").split(" ")[1]);
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
                    }
                    break;
                }
            }
            return favouritesModel;
        }catch (Exception e){
            Log.e("ModelHelper:build", e.getMessage());
        }
        return new FavouritesModel();
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
            }
        }catch (Exception e){
            Log.e("ModelHelper: build", e.getMessage());
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
                bundle.putBoolean("isIdentity", true);
                bundle.putString("id", userModel.getUserId());
                bundle.putString("name", userModel.getName());
                bundle.putString("image", userModel.getImage());
                bundle.putInt("movies_watched", userModel.getTotalWatched());
                bundle.putInt("movies_reviewed", userModel.getTotalReviews());
                bundle.putInt("followers", userModel.getFollowers());
                bundle.putInt("following", userModel.getFollowing());
            }
        }catch (Exception e){
            Log.e("ModelHelper:Build", e.getMessage());
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
            }
        }catch (Exception e){
            Log.e("ModelHelper:build", e.getMessage());
        }
        return bundle;
    }

}
