package com.create.sidhu.movbox.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.LoginActivity;
import com.create.sidhu.movbox.activities.NoInternetActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private boolean showLoading;
    private boolean isService;
    private String UploadFilePath;
    private HashMap<String, String> Extras;
    //Constructors
    public SqlHelper(Context context){
        MasterUrl = context.getString(R.string.master_url);
        this.context = context;
        isService = false;
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate){
        this.context = context;
        this.sqlDelegate = sqlDelegate;
        MasterUrl = context.getString(R.string.master_url);
        isService = false;
    }
        public SqlHelper(Context context, SqlDelegate sqlDelegate, String executePath){
        this.MasterUrl = context.getString(R.string.master_url);
        this.context = context;
        this.ExecutePath = executePath;
        this.sqlDelegate = sqlDelegate;
        isService = false;
    }
    public SqlHelper(Context context, SqlDelegate sqlDelegate, String masterUrl, String executePath){
        this.context = context;
        this.sqlDelegate = sqlDelegate;
        this.MasterUrl = masterUrl;
        this.ExecutePath = executePath;
        isService = false;
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

    public String getUploadFilePath() {
        return UploadFilePath;
    }

    public HashMap<String, String> getExtras() {
        return Extras;
    }

    public boolean isShowLoading(){
        return showLoading;
    }

    public boolean isService() {
        return isService;
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

    public void setUploadFilePath(String uploadFilePath) {
        UploadFilePath = uploadFilePath;
    }

    public void setExtras(HashMap<String, String> extras) {
        Extras = extras;
    }

    public void setService(boolean isService){
        this.isService = isService;
    }
    //Public methods

    public void executeUrl(Boolean showLoading){
        this.showLoading = showLoading;
        LoadResponse loadResponse = new LoadResponse();
        loadResponse.execute();
    }

    public void uploadFile(Boolean showLoading){
        this.showLoading = showLoading;
        UploadFile uploadFile = new UploadFile();
        uploadFile.execute();
    }

    //Async Tasks

    public class LoadResponse extends AsyncTask<Void, Void, Void>{
        TransparentProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        Boolean canceled = false;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(!(isNetworkAvailable() && isOnline())){
                    canceled = true;
                }else {
                    JSONObject jsonObject = null;
                    if (Method.equals("GET"))
                        jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "GET", params);
                    else if (Method.equals("POST"))
                        jsonObject = jParser.makeHttpRequest(MasterUrl + ExecutePath, "POST", params);
                    JSONResponse = jsonObject;
                }
            }catch (Exception e){
                Log.e("SqlHelper:Background", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(showLoading) {
                pDialog = new TransparentProgressDialog(context);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(showLoading)
                pDialog.dismiss();
            if(canceled && !isService){
                NoInternetActivity.sqlHelper = SqlHelper.this;
                context.startActivity(new Intent(context, NoInternetActivity.class));
            }else {
                if(sqlDelegate != null) {
                    sqlDelegate.onResponse(SqlHelper.this);
                }
            }
        }

    }

    public class UploadFile extends AsyncTask<Void, Void, Void>{
        ProgressDialog pDialog;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;
        File sourceFile = new File(getUploadFilePath());
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(getMasterUrl() + getExecutePath());

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", UploadFilePath);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false); // Don't use a Cached Copy

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(getQuery(params));
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                + UploadFilePath + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                DataInputStream inputStream = new DataInputStream(conn.getInputStream());
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200)
                    setStringResponse(serverResponseMessage);
                else
                    setStringResponse(context.getString(R.string.unexpected));
                fileInputStream.close();
                dos.flush();
                dos.close();
            }catch (Exception e){
                Log.e("SqlH: Upload", e.getMessage());
                setStringResponse(context.getString(R.string.unexpected));
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(showLoading) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Uploading file");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(showLoading)
                pDialog.dismiss();
            sqlDelegate.onResponse(SqlHelper.this);
        }

        private String getQuery(ArrayList<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

    }


    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
