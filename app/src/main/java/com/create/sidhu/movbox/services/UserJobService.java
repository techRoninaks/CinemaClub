package com.create.sidhu.movbox.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.util.Log;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;

import org.json.JSONObject;

public class UserJobService extends JobService implements SqlDelegate {
    private static final String TAG = "UserJobService";
    private boolean jobCancelled = false;
    JobParameters jobParameters;
    private String username;

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle pBundle = params.getExtras();
        username = pBundle.getString("username");
        jobParameters = params;
        fetchCurrentUserUpdates();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "User feed job cancelled");
        jobCancelled = true;
        return true;
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        if(!(jobCancelled || sqlHelper == null || sqlHelper.getJSONResponse() == null)) {
            try {
                if (sqlHelper.getActionString().equals("get_user")) {
                    JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                    String response = jsonObject.getString("response");
                    if (response.equals(getString(R.string.response_success))) {
                        MainActivity.currentUserModel = new ModelHelper(UserJobService.this).buildUserModel(jsonObject);
                        MainActivity.currentUserModel.setPreferences(jsonObject.getString("u_preference"));
                        SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
                        sharedPreferences.edit().putString("current_usermodel", StringHelper.convertObjectToString(MainActivity.currentUserModel)).commit();
                        jobFinished(jobParameters, false);
                    } else if (response.equals(getString(R.string.exception))) {

                    }
                }
            }catch (Exception e){
                EmailHelper emailHelper = new EmailHelper(UserJobService.this, EmailHelper.TECH_SUPPORT, "Error: UserJobService for Action:" + sqlHelper.getActionString(), e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();
            }
        }
    }

    private void fetchCurrentUserUpdates(){
        SqlHelper sqlHelper = new SqlHelper(UserJobService.this, UserJobService.this);
        sqlHelper.setExecutePath("get-user.php");
        sqlHelper.setActionString("get_user");
        ContentValues params = new ContentValues();
        params.put("username", username);
        sqlHelper.setMethod("GET");
        sqlHelper.setParams(params);
        sqlHelper.setService(true);
        sqlHelper.executeUrl(false);
    }
}
