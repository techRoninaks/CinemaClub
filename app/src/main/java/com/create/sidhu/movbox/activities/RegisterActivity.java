package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.PermissionsHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity implements SqlDelegate{
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;
    Activity activity;
    LoginButton loginButton;
    EditText editTextName;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextPasswordConfirm;
//    EditText editTextPhone;
//    Spinner spinnerCountry;
    Button btnSignUp;
    ImageView imgGoogleSignIn;
    ImageView imgFbSignIn;
    LinearLayout llContainerMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            activity = this;
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(RegisterActivity.this, gso);
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList(PermissionsHelper.FACEBOOK_EMAIL, PermissionsHelper.FACEBOOK_USERNAME));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                attemptSignUp(RegisterActivity.this.getString(R.string.default_signin), object.getString("name"),
                                        object.getString("email"), object.getString("id"), "fb");
                            } catch (Exception e) {
                                Log.e("FB Register:", e.getMessage());
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(RegisterActivity.this, "Register Cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("FB Register failed", error.getMessage());
                }
            });
            editTextName = (EditText) findViewById(R.id.editText_Name);
            editTextUsername = (EditText) findViewById(R.id.editText_Username);
            editTextPassword = (EditText) findViewById(R.id.editText_Password);
            editTextPasswordConfirm = (EditText) findViewById(R.id.editText_ConfirmPassword);
//        editTextPhone = (EditText) findViewById(R.id.editText_Phone);
//        spinnerCountry = (Spinner) findViewById(R.id.spinner_Country);
            btnSignUp = (Button) findViewById(R.id.buttonSignUp);
            imgGoogleSignIn = (ImageView) findViewById(R.id.img_GoogleSignIn);
            imgFbSignIn = (ImageView) findViewById(R.id.img_FbSignIn);
            llContainerMaster = (LinearLayout) findViewById(R.id.containerMaster);
            View.OnClickListener onButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        switch (view.getId()) {
                            case R.id.buttonSignUp:
                                if (validateSignUp()) {
                                    attemptSignUp(getString(R.string.default_signin), editTextName.getText().toString(), editTextUsername.getText().toString(), StringHelper.encryptPassword(editTextPassword.getText().toString()), "app");
                                }
                                break;
                            case R.id.img_FbSignIn: {
                                attemptSignUp(getString(R.string.fb_signin), "", "", "", "");
                                break;
                            }
                            case R.id.img_GoogleSignIn: {
                                attemptSignUp(getString(R.string.google_signin), "", "", "", "");
                                break;
                            }
                            case R.id.containerMaster: {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                                break;
                            }
                        }
                    }catch (Exception e){
                        Toast.makeText(RegisterActivity.this, RegisterActivity.this.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            btnSignUp.setOnClickListener(onButtonClickListener);
            imgGoogleSignIn.setOnClickListener(onButtonClickListener);
            imgFbSignIn.setOnClickListener(onButtonClickListener);
            llContainerMaster.setOnClickListener(onButtonClickListener);

        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(RegisterActivity.this, EmailHelper.TECH_SUPPORT, "Error: RegisterActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void attemptSignUp(String type, String name, String username, String password, String password_type) {
        if(type.equals(getString(R.string.default_signin))) {
            SqlHelper sqlHelper = new SqlHelper(RegisterActivity.this, RegisterActivity.this);
            sqlHelper.setExecutePath("registration.php");
            sqlHelper.setActionString("register");
            sqlHelper.setMethod(getString(R.string.method_post));
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password_type.equals("app") ? password : ""));
            params.add(new BasicNameValuePair("fb_password", password_type.equals("fb") ? password : ""));
            params.add(new BasicNameValuePair("g_password", password_type.equals("google") ? password : ""));
            params.add(new BasicNameValuePair("type", password_type));
//        params.add(new BasicNameValuePair("country", spinnerCountry.getSelectedItem().toString()));
//        params.add(new BasicNameValuePair("phone", editTextPhone.getText().toString()));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(true);
            Toast.makeText(RegisterActivity.this, "Attempting SignUp", Toast.LENGTH_SHORT).show();
        }else if(type.equals(getString(R.string.google_signin))){
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, PermissionsHelper.REQUEST_GOOGLE_AUTHENTICATION);
        }else if(type.equals(getString(R.string.fb_signin))){
            loginButton.performClick();
        }
    }
    private void populateCountry(){
        SqlHelper sqlHelper = new SqlHelper(RegisterActivity.this, RegisterActivity.this);
        sqlHelper.setExecutePath("country.php");
        sqlHelper.setActionString("country");
        sqlHelper.setParams(new ArrayList<NameValuePair>());
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.executeUrl(true);
    }
    private boolean validateSignUp(){
        boolean signUp = true;
        String name, email, password, password_confirm, phone, country;
        name = editTextName.getText().toString();
        email = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();
        password_confirm = editTextPasswordConfirm.getText().toString();
//        phone = editTextPhone.getText().toString();

        if(!LoginActivity.isValidEmail(email)){
            editTextUsername.setError(getString(R.string.invalid_email));
            signUp = false;
        }
        if(!LoginActivity.isValidPassword(password)){
            editTextPassword.setError(getString(R.string.invalid_password));
            signUp = false;
        }
        if(!password.equals(password_confirm)){
            editTextPasswordConfirm.setError(getString(R.string.password_mismatch));
            signUp = false;
        }
        if(name.isEmpty()){
            editTextName.setError(getString(R.string.required_field));
            signUp = false;
        }
        if(email.isEmpty()){
            editTextUsername.setError(getString(R.string.required_field));
            signUp = false;
        }
        if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.required_field));
            signUp = false;
        }
        if(password_confirm.isEmpty()){
            editTextPasswordConfirm.setError(getString(R.string.required_field));
            signUp = false;
        }
//        if(phone.isEmpty()){
//            editTextPhone.setError(getString(R.string.required_field));
//            signUp = false;
//        }
        return signUp;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            attemptSignUp(getString(R.string.default_signin), account.getDisplayName(),account.getEmail(), account.getIdToken().substring(0,30), "google");
        } catch (ApiException e) {
            EmailHelper emailHelper = new EmailHelper(RegisterActivity.this, EmailHelper.TECH_SUPPORT, "Error: RegisterActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(RegisterActivity.this, "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == PermissionsHelper.REQUEST_GOOGLE_AUTHENTICATION) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else if(requestCode == PermissionsHelper.REQUEST_FACEBOOK_AUTHENTICATION){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onResponse(SqlHelper sqlHelper){
        try {
            switch (sqlHelper.getActionString()) {
                case "register": {
                    String response = sqlHelper.getJSONResponse().getString("response");
                    if (response.equals(getString(R.string.exception))) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if(response.equals(getString(R.string.response_success))){
                        Toast.makeText(RegisterActivity.this, getString(R.string.success_register) + ". " + getString(R.string.continue_login), Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
                        sharedPreferences.edit().putString("username", sqlHelper.getParams().get(1).getValue()).commit();
                        sharedPreferences.edit().putBoolean("login", true).commit();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                    finish();
                }
                    break;
//                case "country": {
//                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("country_data");
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);
//                    String response = jsonObject.getString("response");
//                    if(response.equals(getString(R.string.response_success))){
//                        int length = jsonArray.length();
//                        ArrayList<String> arrayList = new ArrayList<>();
//                        for (int i = 1; i < length; i++) {
//                            jsonObject = jsonArray.getJSONObject(i);
//                            arrayList.add(jsonObject.getString("name"));
//                        }
//                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1, arrayList);
//                        spinnerCountry.setAdapter(arrayAdapter);
//
//                    }else if(response.equals(getString(R.string.response_unsuccessful))){
//                        Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                        finish();
//                    }
//
//                }
//                    break;
            }
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(RegisterActivity.this, EmailHelper.TECH_SUPPORT, "Error: RegisterActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
