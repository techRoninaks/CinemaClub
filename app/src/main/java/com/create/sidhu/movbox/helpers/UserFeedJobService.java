package com.create.sidhu.movbox.helpers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.UpdatesModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFeedJobService extends JobService implements SqlDelegate {
    private static final String TAG = "UserFeedJobService";
    private boolean jobCancelled = false;
    private boolean homeFeedJob = false;
    private boolean favouritesFeedJob = false;
    private boolean updatesJob = false;
    private ArrayList<HomeModel> homeModels;
    private ArrayList<FavouritesModel> favouritesModels;
    private ArrayList<UpdatesModel> updatesModels;
    String userId;
    JobParameters jobParameters;
    Context context;
    int retryCounter;
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            PersistableBundle pBundle = params.getExtras();
            userId = pBundle.getString("userid");
            jobParameters = params;
            context = UserFeedJobService.this;
            updatesModels = MainActivity.updatesModels;
            retryCounter = 0;
            Log.d(TAG, "User feed job started");
            if (!Boolean.parseBoolean(pBundle.getString("home_feed_mask")))
                doBackgroundHomeFeedFetch(params);
            else
                homeFeedJob = true;
            if (!Boolean.parseBoolean(pBundle.getString("favourites_feed_mask")))
                doBackgroundFavouritesFeedFetch(params);
            else
                favouritesFeedJob = true;
            if (!Boolean.parseBoolean(pBundle.getString("updates_mask")))
                doBackgroundUpdatesPush(jobParameters);
            else
                updatesJob = true;
            return true;
        }catch (Exception e){
            Log.e(TAG, "onStartJob: " + e.getMessage());
            return false;
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "User feed job cancelled");
        jobCancelled = true;
        return true;
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("home")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getJSONObject("0").getString("response");
                if (response.equals(context.getString(R.string.response_success))) {
                    initializeHomeModel(jsonObject);
                } else if (response.equals(context.getString(R.string.response_unsuccessful))) {
                    Log.e(TAG, context.getString(R.string.response_unsuccessful));
                    HomeFragment.homeModels = new ArrayList<>();
                } else if (response.equals(context.getString(R.string.unexpected))) {
                    Log.e(TAG, context.getString(R.string.unexpected));
                }
            }else if(sqlHelper.getActionString().contains("cast")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("cast_data");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String response = jsonObject.getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    addCastData(jsonArray);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){

                }else if(response.equals(context.getString(R.string.unexpected))){
                    Log.e(TAG, context.getString(R.string.unexpected));
                }
            }else if(sqlHelper.getActionString().equals("favourites")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getJSONObject("0").getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    initializeFavouritesModel(jsonObject);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){

                }else if(response.equals(context.getString(R.string.unexpected))){
                    Log.e(TAG, context.getString(R.string.unexpected));
                }
            }else if(sqlHelper.getActionString().equals("push_updates")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                int counter = Integer.parseInt(sqlHelper.getExtras().get("counter"));
                if(response.equals(context.getString(R.string.response_success))){
                    retryCounter = 0;
                    pushUpdates( counter + 1);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(retryCounter < 3){
                        retryCounter += 1;
                        pushUpdates(counter);
                    }else{
                        retryCounter = 0;
                        pushUpdates(counter + 1);
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            HomeFragment.homeModels = new ArrayList<>();
        }

    }

    private void doBackgroundHomeFeedFetch(final JobParameters jobParameters){
        try {
            SqlHelper sqlHelper = new SqlHelper(context, UserFeedJobService.this);
            sqlHelper.setExecutePath("get-updates.php");
            sqlHelper.setMethod("GET");
            sqlHelper.setActionString("home");
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("u_id", userId));
            params.add(new BasicNameValuePair("seeker", "0"));
            params.add(new BasicNameValuePair("fragment", "home"));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(false);
        }catch (Exception e){
            Log.e(TAG, "doBackgroundHomeFeedFetch: " + e.getMessage());
        }
    }

    private void doBackgroundFavouritesFeedFetch(final JobParameters jobParameters){
        try {
            SqlHelper sqlHelper = new SqlHelper(context, UserFeedJobService.this);
            sqlHelper.setExecutePath("get-updates.php");
            sqlHelper.setMethod("GET");
            sqlHelper.setActionString("favourites");
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("u_id", userId));
            params.add(new BasicNameValuePair("seeker", "0"));
            params.add(new BasicNameValuePair("fragment", "favourites"));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(false);
        }catch (Exception e){
            Log.e(TAG, "doBackgroundFavouritesFeedFetch: " + e.getMessage());
        }
    }

    private void doBackgroundUpdatesPush(final JobParameters jobParameters){
        if(updatesModels != null && updatesModels.size() > 0)
            pushUpdates(0);
    }

    private void pushUpdates(int position){
        if(position < updatesModels.size()) {
            UpdatesModel updatesModel = updatesModels.get(position);
            SqlHelper sqlHelper = new SqlHelper(UserFeedJobService.this, UserFeedJobService.this);
            sqlHelper.setExecutePath("publish-updates.php");
            sqlHelper.setMethod("GET");
            sqlHelper.setActionString("push_updates");
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("type", updatesModel.getType()));
            params.add(new BasicNameValuePair("u_id", updatesModel.getUserId()));
            params.add(new BasicNameValuePair("m_id", updatesModel.getMovieId()));
            params.add(new BasicNameValuePair("r_id", updatesModel.getReviewId()));
            sqlHelper.setParams(params);
            HashMap<String, String> extras = new HashMap<>();
            extras.put("counter", "" + position);
            sqlHelper.executeUrl(false);
        }else{
            MainActivity.updatesModels = new ArrayList<>();
            updatesJob = true;
            checkToFinish();
        }
    }

    private void initializeFavouritesModel(final JSONObject jsonObject){
       favouritesModels = new ArrayList<>();
       try{
           int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
           ModelHelper modelHelper = new ModelHelper(context);
           for(int i = 1; i < length ; i++){
               FavouritesModel favouritesModel = modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites");
               favouritesModels.add(favouritesModel);
           }
           FavouritesFragment.favouritesList = favouritesModels;
           favouritesFeedJob = true;
           checkToFinish();
       }catch (Exception e){
           Log.e(TAG, e.getMessage());
       }
    }

    private void initializeHomeModel(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                homeModels = new ArrayList<>();
                try{
                    int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
                    if(length >= 0) {
                        ModelHelper modelHelper = new ModelHelper(context);
                        String castString = "";
                        for (int i = 1; i <= length; i++) {
                            if (!jsonObject.getJSONObject("" + i).getString("type").equals("follow") && !jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")) {
                                HomeModel homeModel = new HomeModel();
                                homeModel.setFavourites(modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites"));
                                if(i != 1){
                                    castString = castString.concat("!:");
                                }
                                castString = castString.concat(homeModel.getFavourites().getMovie().getId() + "!@" + (i - 1) + "!@" + homeModel.getFavourites().getMovie().getCast());
                                homeModels.add(homeModel);
                            }
                        }
                        fetchActors(castString, true);
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private void fetchActors(String castString, boolean start){
        if(start) {
            SqlHelper sqlHelper = new SqlHelper(context, UserFeedJobService.this);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("cast", castString));
            params.add(new BasicNameValuePair("m_id", ""));
            sqlHelper.setExecutePath("get-cast.php");
            sqlHelper.setParams(params);
            sqlHelper.setActionString("cast");
            sqlHelper.setMethod("GET");
            sqlHelper.executeUrl(false);
        }else{
            HomeFragment.homeModels = homeModels;
            homeFeedJob = true;
            checkToFinish();
        }
    }

    private void addCastData(final JSONArray jsonArray){
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        e.printStackTrace();
                    }

                }
                fetchActors("", false);
            }
        }).start();
    }

    private void checkToFinish(){
        if(updatesJob && homeFeedJob && favouritesFeedJob)
            jobFinished(jobParameters, false);
    }

}
