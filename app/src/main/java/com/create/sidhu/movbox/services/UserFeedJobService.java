package com.create.sidhu.movbox.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
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
    private static final int NOTIFICATION_ID = 6321;
    private static final int NOTIFICATION_GROUP_ID_RATING = 2788;
    private static final int NOTIFICATION_GROUP_ID_REVIEW = 2444;
    private static final int NOTIFICATION_GROUP_ID_FOLLOW = 2748;
    private static final int NOTIFICATION_GROUP_ID_REVIEW_VOTE = 2337;
    private static final int NOTIFICATION_GROUP_ID_WATCHING_NOW = 2774;
    private static final String NOTIFICATION_CHANNEL_ID = "DEFAULT_CHANNEL";
    private static final String NOTIFICATION_GROUP_RATING = "rating";
    private static final String NOTIFICATION_GROUP_REVIEW = "review";
    private static final String NOTIFICATION_GROUP_FOLLOW = "follow";
    private static final String NOTIFICATION_GROUP_REVIEW_VOTE = "review_vote";
    private static final String NOTIFICATION_GROUP_WATCHING_NOW = "watching_now";
    private boolean jobCancelled = false;
    private boolean homeFeedJob = false;
    private boolean favouritesFeedJob = false;
    private boolean updatesJob = false;
    private boolean isInitialRun = true;
    private int ratingNotifNo = 0, reviewNotifNo = 0, followNotifNo = 0, reviewVoteNotifNo = 0, watchingNowNotifNo = 0;
    private ArrayList<HomeModel> homeModels;
    private ArrayList<FavouritesModel> favouritesModels;
    private ArrayList<UpdatesModel> updatesModels;
    String userId;
    JobParameters jobParameters;
    Context context;
    int retryCounter;

    NotificationCompat.Builder notification;
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            PersistableBundle pBundle = params.getExtras();
            userId = pBundle.getString("userid");
            isInitialRun = pBundle.getString("initial_run") == null;
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
            createNotificationChannel();
            return true;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
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
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            if(isInitialRun)
                HomeFragment.homeModels = new ArrayList<>();
        }

    }

    private void initializeNotification(String type, String group, Bundle bundle){
        try {
            boolean isAssigned = false;
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.drawable.main_logo);
            notification.setTicker(getString(R.string.notification_ticker));
            notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notification.setCategory(NotificationCompat.CATEGORY_SOCIAL);
            notification.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            Intent intent = new Intent();
            switch (type) {
                case "favourites": {
                    isAssigned = true;
                    switch (group) {
                        case "follow": {
                            notification.setContentTitle(getString(R.string.notification_group_title_follow));
                            notification.setContentText(bundle.getString("name") + " started following you.");
                            notification.setGroup(NOTIFICATION_GROUP_FOLLOW);
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        }
                        case "review": {
                            notification.setContentTitle(getString(R.string.notification_group_title_review));
                            notification.setContentText(bundle.getString("user_name") + " wrote a review on " + bundle.getString("movie_name"));
                            notification.setGroup(NOTIFICATION_GROUP_REVIEW);
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        }
                        case "review_vote": {
                            notification.setContentTitle(getString(R.string.notification_group_title_review_vote));
                            notification.setContentText("Tap to find out who");
                            notification.setGroup(NOTIFICATION_GROUP_REVIEW_VOTE);
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        }
                        case "watching_now": {
                            notification.setContentTitle(getString(R.string.notification_group_title_watching_now));
                            notification.setContentText(bundle.getString("user_name") + " is now watching" + bundle.getString("name"));
                            notification.setGroup(NOTIFICATION_GROUP_WATCHING_NOW);
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        }
                    }
                    break;
                }
            }
            //Intent for notification
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            notification.setContentIntent(pendingIntent);

            if (isAssigned) {
                NotificationManager notificationManager = (NotificationManager) UserFeedJobService.this.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID + (int) System.currentTimeMillis(), notification.build());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                    updateSummaryNotification(type, group);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }

    private void updateSummaryNotification(String type, String group){
        try {
            Notification summary;
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(UserFeedJobService.this);
            switch (type) {
                case "favourites": {
                    Intent intent = new Intent(UserFeedJobService.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("return_path", "FavouritesFragment");
                    intent.putExtra("bundle", bundle);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    switch (group) {
                        case "follow": {
                            if (followNotifNo > 0) {
                                summary = new NotificationCompat.Builder(UserFeedJobService.this, NOTIFICATION_CHANNEL_ID)
                                        .setContentTitle(getString(R.string.notification_group_title_follow))
                                        .setContentText(++followNotifNo + " new followers")
                                        .setSmallIcon(R.drawable.main_logo)
                                        .setGroup(NOTIFICATION_GROUP_FOLLOW)
                                        .setGroupSummary(true)
                                        .setContentIntent(pendingIntent)
                                        .build();
                                notificationManagerCompat.notify(NOTIFICATION_GROUP_ID_FOLLOW, summary);
                            }
                            break;
                        }
                        case "review": {
                            if (reviewNotifNo > 0) {
                                summary = new NotificationCompat.Builder(UserFeedJobService.this, NOTIFICATION_CHANNEL_ID)
                                        .setContentTitle(getString(R.string.notification_group_title_review))
                                        .setContentText(++reviewNotifNo + " new reviews")
                                        .setSmallIcon(R.drawable.main_logo)
                                        .setGroup(NOTIFICATION_GROUP_REVIEW)
                                        .setGroupSummary(true)
                                        .setContentIntent(pendingIntent)
                                        .build();
                                notificationManagerCompat.notify(NOTIFICATION_GROUP_ID_REVIEW, summary);
                            }
                            break;
                        }
                        case "review_vote": {
                            if (reviewVoteNotifNo > 0) {
                                summary = new NotificationCompat.Builder(UserFeedJobService.this, NOTIFICATION_CHANNEL_ID)
                                        .setContentTitle(getString(R.string.notification_group_title_review_vote))
                                        .setContentText(++reviewVoteNotifNo + " new likes")
                                        .setSmallIcon(R.drawable.main_logo)
                                        .setGroup(NOTIFICATION_GROUP_REVIEW_VOTE)
                                        .setGroupSummary(true)
                                        .setContentIntent(pendingIntent)
                                        .build();
                                notificationManagerCompat.notify(NOTIFICATION_GROUP_ID_REVIEW_VOTE, summary);
                            }
                            break;
                        }
                        case "watching_now": {
                            if (watchingNowNotifNo > 0) {
                                summary = new NotificationCompat.Builder(UserFeedJobService.this, NOTIFICATION_CHANNEL_ID)
                                        .setContentTitle(getString(R.string.notification_group_title_follow))
                                        .setContentText(++watchingNowNotifNo + " users watching")
                                        .setSmallIcon(R.drawable.main_logo)
                                        .setGroup(NOTIFICATION_GROUP_WATCHING_NOW)
                                        .setGroupSummary(true)
                                        .setContentIntent(pendingIntent)
                                        .build();
                                notificationManagerCompat.notify(NOTIFICATION_GROUP_ID_WATCHING_NOW, summary);
                            }
                            break;
                        }
                    }

                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void doBackgroundHomeFeedFetch(final JobParameters jobParameters){
        try {
            SqlHelper sqlHelper = new SqlHelper(context, UserFeedJobService.this);
            sqlHelper.setExecutePath("get-updates.php");
            sqlHelper.setMethod("GET");
            sqlHelper.setActionString("home");
            sqlHelper.setService(true);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("u_id", userId));
            params.add(new BasicNameValuePair("seeker", "0"));
            params.add(new BasicNameValuePair("fragment", "home"));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void doBackgroundFavouritesFeedFetch(final JobParameters jobParameters){
        try {
            SqlHelper sqlHelper = new SqlHelper(context, UserFeedJobService.this);
            sqlHelper.setExecutePath("get-updates.php");
            sqlHelper.setMethod("GET");
            sqlHelper.setActionString("favourites");
            sqlHelper.setService(true);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("u_id", userId));
            params.add(new BasicNameValuePair("seeker", "0"));
            params.add(new BasicNameValuePair("fragment", "favourites"));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
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
            sqlHelper.setService(true);
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
        MainActivity.followCounter = 0;
       favouritesModels = new ArrayList<>();
       try{
           int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
           ModelHelper modelHelper = new ModelHelper(context);
           for(int i = 1; i <= length ; i++){
               FavouritesModel favouritesModel = modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites");
               favouritesModels.add(favouritesModel);
               if (jsonObject.getJSONObject("" + i).getString("type").equals("follow")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
               {
                   MainActivity.followCounter+=1;
               }
               if (jsonObject.getJSONObject("" + i).getString("type").equals("watching")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
               {
                   MainActivity.followCounter+=1;
               }
               if (jsonObject.getJSONObject("" + i).getString("type").equals("rating")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
               {
                   MainActivity.followCounter+=1;
               }
               if (jsonObject.getJSONObject("" + i).getString("type").equals("review")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
               {
                   MainActivity.followCounter+=1;
               }
               if (jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
               {
                   MainActivity.followCounter+=1;
               }
           }
           FavouritesFragment.favouritesList = favouritesModels;
           favouritesFeedJob = true;
           if(!isInitialRun)
               createNotifications(favouritesModels, "favourites");
           checkToFinish();
       }catch (Exception e){
           EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
           emailHelper.sendEmail();
       }
    }

    private void createNotifications(ArrayList<?> model, String type){
        String markAsRead = "";
        switch (type){
            case "favourites":{
                ArrayList<FavouritesModel> list = (ArrayList<FavouritesModel>) model;
                int length = list.size();
                for(int i = 0; i < length; i++){
                    FavouritesModel favouritesModel = list.get(i);
                    String subType = favouritesModel.getSubType();
                    if(!subType.equals("new_releases") && !subType.equals("recommendations")) {
                        if (i != 0)
                            markAsRead = markAsRead.concat(",");
                        markAsRead = markAsRead.concat(favouritesModel.getId());
                    }
                    if(subType.equals("follow") || subType.equals("review_vote") || subType.equals("review") || subType.equals("watching_now")){
                        if(!favouritesModel.getRead()){
                            Bundle bundle = new Bundle();
                            ModelHelper modelHelper = new ModelHelper(UserFeedJobService.this);
                            switch (subType){
                                case "follow":{
                                    bundle = modelHelper.buildUserModelBundle(favouritesModel.getUser(), "ProfileFragment");
                                    bundle.putString("return_path", "ProfileFragment");
                                    break;
                                }
                                case "review_vote":{
                                    bundle = modelHelper.buildReviewModelBundle(favouritesModel.getMovie(), "PostStatusFragment");
                                    bundle.putString("return_path", "ReviewsActivity");
                                    break;
                                }
                                case "review":{
                                    bundle = modelHelper.buildReviewModelBundle(favouritesModel.getMovie(), "PostStatusFragment");
                                    bundle.putString("return_path", "ReviewsActivity");
                                    break;
                                }
                                case "watching_now":{
                                    bundle = modelHelper.buildMovieModelBundle(favouritesModel.getMovie(), "ProfileFragment");
                                    bundle.putString("return_path", "ProfileFragment");
                                    bundle.putString("user_name", favouritesModel.getUser().getName());
                                    break;
                                }
                            }
                            initializeNotification("favourites", subType, bundle);
                        }
                    }
                }
            }
        }
        markFeedsAsRead(markAsRead);
    }

    private void markFeedsAsRead(String markAsRead){
        SqlHelper sqlHelper = new SqlHelper(UserFeedJobService.this, UserFeedJobService.this);
        sqlHelper.setActionString("mark_as_read");
        sqlHelper.setMethod("GET");
        sqlHelper.setExecutePath("mark-updates.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("update_string", markAsRead));
        sqlHelper.setParams(params);
        sqlHelper.setService(true);
        sqlHelper.executeUrl(false);
    }

    private void initializeHomeModel(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                homeModels = new ArrayList<>();
                MainActivity.unseenCounter= 0;
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
                            if(jsonObject.getJSONObject("" + i).getString("type").equals("watchlist_reminder")&&jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                                MainActivity.unseenCounter+=1;
                            }
                            if(jsonObject.getJSONObject("" + i).getString("type").equals("review_reminder")&&jsonObject.getJSONObject("" + i).getString("has_seen").equals("0")){
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("review_watched")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("watching_now")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("watching")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("rating")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("review")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                            if (jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")&& jsonObject.getJSONObject(""+ i).getString("has_seen").equals("0"))
                            {
                                MainActivity.unseenCounter+=1;
                            }
                        }
                        fetchActors(castString, true);
                    }
                }catch (Exception e){
                    EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
                    emailHelper.sendEmail();
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
            sqlHelper.setService(true);
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
                        EmailHelper emailHelper = new EmailHelper(UserFeedJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserFeedJobService", StringHelper.convertStackTrace(e));
                        emailHelper.sendEmail();
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
