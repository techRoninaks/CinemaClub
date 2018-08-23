package com.create.sidhu.movbox.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.LoginActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nihalpradeep on 10/08/18.
 */

public class SqlHelper {
    private String MasterUrl;
    private Context context;
    private String ExecutePath;
    private JSONObject JSONResponse;
    private String StringResponse;
    private String ActionString;
    private SqlDelegate sqlDelegate;
    private ArrayList<NameValuePair> params;
    private String Method;

    //Constructors
    public SqlHelper(Context context){
        MasterUrl = context.getString(R.string.master_url);
        this.context = context;
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate){
        this.context = context;
        this.sqlDelegate = sqlDelegate;
        MasterUrl = context.getString(R.string.master_url);
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String executePath){
        this.MasterUrl = context.getString(R.string.master_url);;
        this.context = context;
        this.ExecutePath = executePath;
        this.sqlDelegate = sqlDelegate;
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String masterUrl, String executePath){
        this.context = context;
        this.sqlDelegate = sqlDelegate;
        this.MasterUrl = masterUrl;
        this.ExecutePath = executePath;
    }
    //Getters


    public android.content.Context getContext() {
        return context;
    }

    public String getExecutePath() {
        return ExecutePath;
    }

    public String getMasterUrl() {
        return MasterUrl;
    }

    public JSONObject getJSONResponse() {
        return JSONResponse;
    }

    public String getActionString() {
        return ActionString;
    }

    public String getStringResponse(String key) {
        try {
            return JSONResponse.getString(key);
        } catch (Exception e){
            Log.e("SqlHelper:getStringResp", e.getMessage());
            return "exception";
        }
    }

    public SqlDelegate getSqlDelegate() {
        return sqlDelegate;
    }

    public ArrayList<NameValuePair> getParams() {
        return params;
    }

    public String getMethod() {
        return Method;
    }
    //Setters


    public void setSqlDelegate(SqlDelegate sqlDelegate) {
        this.sqlDelegate = sqlDelegate;
    }

    public void setContext(android.content.Context context) {
        context = context;
    }

    public void setExecutePath(String executePath) {
        ExecutePath = executePath;
    }

    public void setMasterUrl(String masterUrl) {
        MasterUrl = masterUrl;
    }

    public void setActionString(String actionString) {
        ActionString = actionString;
    }

    public void setJSONResponse(JSONObject JSONResponse) {
        this.JSONResponse = JSONResponse;
    }

    public void setStringResponse(String stringResponse) {
        StringResponse = stringResponse;
    }

    public void setParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    public void setMethod(String method) {
        Method = method;
    }
    //Public methods

    public void executeUrl(){
        LoadResponse loadResponse = new LoadResponse();
        loadResponse.execute();
    }

    //Async Tasks

    public class LoadResponse extends AsyncTask<Void, Void, Void>{
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = null;
                if(Method.equals("GET"))
                    jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "GET", params);
                else if(Method.equals("POST"))
                    jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "POST", params);
                JSONResponse = jsonObject;
            }catch (Exception e){
                Log.e("SqlHelper:Background", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            sqlDelegate.onResponse(SqlHelper.this);
        }

    }
}
