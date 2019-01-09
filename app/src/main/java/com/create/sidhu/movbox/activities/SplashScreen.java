package com.create.sidhu.movbox.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.services.UserFeedJobService;

import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity implements SqlDelegate {
    private static final int JOB_ID = 1001;
    ImageView imgLogoOuter;
    ImageView imgLogoInner;
    int stage = 1;
    boolean launched = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);
            imgLogoOuter = (ImageView) findViewById(R.id.img_LogoOuter);
            imgLogoInner = (ImageView) findViewById(R.id.img_LogoInner);
            Animation animationOuter = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.rotate_logo_outer);
            animationOuter.setStartOffset(650);
            animationOuter.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (stage) {
                        case 1: {
                            SharedPreferences sharedPreferences = getSharedPreferences("CinemaClub", 0);
                            if (sharedPreferences.getBoolean("login", false)) {
                                String username = sharedPreferences.getString("username", "");
                                if (!username.isEmpty()) {
                                    animation.setRepeatCount(Animation.INFINITE);
                                    animation.startNow();
                                    getUserDetails(username);
                                    stage = 2;
                                } else {
                                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                    SplashScreen.this.finish();
                                }
                            } else {
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                SplashScreen.this.finish();
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    animation.setStartOffset(500);
                    if (stage == 2) {
                        if (HomeFragment.homeModels != null) {
                            int size = HomeFragment.homeModels.size();
                            if (size == 0)
                                HomeFragment.homeModels = null;
                            if (!launched) {
                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                launched = true;
                            }
                            finish();
                        }
                    }
                }
            });
            Animation animationInner = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.rotate_logo_inner);
            animationInner.setStartOffset(500);
            animationInner.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.startNow();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    animation.setStartOffset(500);
                }
            });
            imgLogoOuter.startAnimation(animationOuter);
            imgLogoInner.startAnimation(animationInner);
        } catch (Resources.NotFoundException e) {
            EmailHelper emailHelper = new EmailHelper(SplashScreen.this, EmailHelper.TECH_SUPPORT, "Error: SplashScreen", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if (sqlHelper.getActionString().equals("get_user")) {
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                String response = jsonObject.getString("response");
                if (response.equals(getString(R.string.response_success))) {
                    MainActivity.currentUserModel = new ModelHelper(SplashScreen.this).buildUserModel(jsonObject);
                    MainActivity.currentUserModel.setPreferences(jsonObject.getString("u_preference"));
                    SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
                    sharedPreferences.edit().putString("current_usermodel", StringHelper.convertObjectToString(MainActivity.currentUserModel)).commit();
                    scheduleJob(MainActivity.currentUserModel.getUserId());
                } else if (response.equals(getString(R.string.exception))) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SplashScreen.this, EmailHelper.TECH_SUPPORT, "Error: SplashScreen", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void getUserDetails(String username){
        SqlHelper sqlHelper = new SqlHelper(SplashScreen.this, SplashScreen.this);
        sqlHelper.setExecutePath("get-user.php");
        sqlHelper.setActionString("get_user");
        ContentValues params = new ContentValues();
        params.put("username", username);
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    public void scheduleJob(String userId){
        try {
            PersistableBundle pBundle = new PersistableBundle();
            pBundle.putString("userid", userId);
            pBundle.putString("home_feed_mask", "false");
            pBundle.putString("favourites_feed_mask", "true");
            pBundle.putString("updates_mask", "true");
            ComponentName componentName = new ComponentName(SplashScreen.this, UserFeedJobService.class);
            JobInfo info = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setExtras(pBundle)
                    .setMinimumLatency(1)
                    .setOverrideDeadline(1)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            cancelJob();
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_FAILURE) {
                throw new Exception();
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SplashScreen.this, EmailHelper.TECH_SUPPORT, "Error: SplashScreen", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(SplashScreen.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
    }

    public void cancelJob(){
        try {
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(JOB_ID);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SplashScreen.this, EmailHelper.TECH_SUPPORT, "Error: SplashScreen", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
