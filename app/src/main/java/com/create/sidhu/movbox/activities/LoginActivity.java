package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.PermissionsHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.UserModel;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity implements SqlDelegate {
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;
    LoginButton loginButton;
    Activity activity;

    EditText editTextUsername;
    EditText editTextPassword;
    Button btnSignIn;
    TextView tvSignUp, tvForgotPassword;
    ImageView imgGoogleSignIn;
    ImageView imgFbSignIn;
    LinearLayout llContainerMaster;

    int errorCount = 0;
    public final int ERROR_THRESHOLD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            activity = this;
            SharedPreferences sharedPreferences = getSharedPreferences("CinemaClub", 0);
            if (sharedPreferences.getBoolean("login", false)) {
                if (!sharedPreferences.getString("username", "").isEmpty()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, gso);
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList(PermissionsHelper.FACEBOOK_EMAIL));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                attemptLogin(LoginActivity.this.getString(R.string.default_signin), object.getString("email"), object.getString("id"));
                            } catch (Exception e) {
                                Log.e("FB Register:", e.getMessage());
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Register Cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("FB Login failed", error.getMessage());
                }
            });
            editTextUsername = (EditText) findViewById(R.id.editText_Username);
            editTextPassword = (EditText) findViewById(R.id.editText_Password);
            btnSignIn = (Button) findViewById(R.id.buttonSignIn);
            tvSignUp = (TextView) findViewById(R.id.textView_Register);
            tvForgotPassword = (TextView) findViewById(R.id.textView_ForgotPassword);
            imgGoogleSignIn = (ImageView) findViewById(R.id.img_GoogleSignIn);
            imgFbSignIn = (ImageView) findViewById(R.id.img_FbSignIn);
            llContainerMaster = (LinearLayout) findViewById(R.id.containerMaster);
            View.OnClickListener onButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.buttonSignIn:
                            if (validateSignIn())
                                fetchSalt(editTextUsername.getText().toString());
                            break;
                        case R.id.textView_Register:
                            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                            break;
                        case R.id.img_GoogleSignIn:
                            attemptLogin(getString(R.string.google_signin), "", "");
                            break;
                        case R.id.img_FbSignIn:
                            attemptLogin(getString(R.string.fb_signin), "", "");
                            break;
                        case R.id.containerMaster:
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                            break;
                        case R.id.textView_ForgotPassword: {
                            Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                            intent.putExtra("type", "password_forgot");
                            startActivity(intent);
                            break;
                        }
                    }
                }
            };
            tvSignUp.setOnClickListener(onButtonClickListener);
            btnSignIn.setOnClickListener(onButtonClickListener);
            imgGoogleSignIn.setOnClickListener(onButtonClickListener);
            imgFbSignIn.setOnClickListener(onButtonClickListener);
            llContainerMaster.setOnClickListener(onButtonClickListener);
            tvForgotPassword.setOnClickListener(onButtonClickListener);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private boolean validateSignIn() {
        String userName, password;
        boolean signIn = true;
        userName = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        if(userName.isEmpty()){
            editTextUsername.setError(getString(R.string.required_field));
            signIn = false;
        }
        if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.required_field));
            signIn = false;
        }

        else if(!userName.isEmpty() || !password.isEmpty()){
            if(!isValidEmail(userName)){
//                editTextUsername.setError(getString(R.string.invalid_email));
                editTextPassword.setText("");
                editTextUsername.setText("");
                Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                signIn = false;
            }
            if(!isValidPassword(password)){
                //editTextPassword.setError(getString(R.string.incorrect_password_or_username));

                editTextPassword.setText("");
                editTextUsername.setText("");
                Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                signIn = false;
            }
        }



        return signIn;
    }
    

    public static boolean isValidEmail(String email){
        boolean isValid = false;
        if(email.contains("@"))
            isValid = true;
        return isValid;
    }
    public static boolean isValidPassword(String password){
        int charCount = 0, numCount = 0;
        int length = password.length();
        if(length < 6)
            return false;
        String numeral = "0123456789";
        for(int i=0 ; i < length; i++){
            char c = password.charAt(i);
            if(c >= 'A' && c <= 'Z')
                charCount++;
            else if(numeral.contains("" + c))
                numCount++;
        }
        if(charCount > 0 && numCount > 0)
            return true;
        else
            return false;
    }

    private void attemptLogin(String type, String username, String password){
        try {
            if (type.equals(getString(R.string.default_signin))) {
                SqlHelper sqlHelper = new SqlHelper(LoginActivity.this, LoginActivity.this);
                sqlHelper.setExecutePath("login.php");
                sqlHelper.setActionString("login");
                ContentValues contentValues = new ContentValues();
                contentValues.put("username", username);
                contentValues.put("password", password);
                sqlHelper.setParams(contentValues);
                sqlHelper.setMethod(getString(R.string.method_post));
                sqlHelper.executeUrl(true);
            } else if (type.equals(getString(R.string.google_signin))) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, PermissionsHelper.REQUEST_GOOGLE_AUTHENTICATION);
            } else if (type.equals(getString(R.string.fb_signin))) {
                loginButton.performClick();
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchSalt(String username){
        try{
            SqlHelper sqlHelper = new SqlHelper(LoginActivity.this, LoginActivity.this);
            sqlHelper.setExecutePath("fetch-extra.php");
            sqlHelper.setActionString("fetch_extra");
            ContentValues params = new ContentValues();
            params.put("username", username);
            sqlHelper.setParams(params);
            sqlHelper.setMethod(getString(R.string.method_get));
            sqlHelper.executeUrl(true);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            attemptLogin(getString(R.string.default_signin), account.getEmail(), account.getIdToken().substring(0, 30));
        } catch (ApiException e) {
            Log.e("Login:GoogleAuth", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed to login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onResponse(SqlHelper sqlHelper) {
        try {
            switch (sqlHelper.getActionString()) {
                case "login": {
                    JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                    String response = jsonObject.getString("response");
                    if (response.equals(getString(R.string.response_success))) {
                        UserModel currentUserModel = new ModelHelper(LoginActivity.this).buildUserModel(jsonObject);
                        MainActivity.currentUserModel = currentUserModel;
                        MainActivity.currentUserModel.setPreferences(jsonObject.getString("u_preference"));
                        SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
                        sharedPreferences.edit().putString("username", currentUserModel.getEmail()).commit();
                        sharedPreferences.edit().putBoolean("login", true).commit();
                        sharedPreferences.edit().putString("current_usermodel", StringHelper.convertObjectToString(MainActivity.currentUserModel)).commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (response.equals(getString(R.string.response_unsuccessful))) {
//                editTextPassword.setError(getString(R.string.invalid_cred));
//                editTextUsername.setError(getString(R.string.invalid_cred));
                        editTextPassword.setText("");
                        editTextUsername.setText("");
                        Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                        if (errorCount % ERROR_THRESHOLD == 0) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                            alertDialog.setTitle(getString(R.string.new_user));
                            alertDialog.setMessage(getString(R.string.new_user_prompt));
                            alertDialog.setPositiveButton(R.string.confirmation_yes, dialogClickListener);
                            alertDialog.setNegativeButton(R.string.confirmation_no, dialogClickListener);
                            alertDialog.show();
                            errorCount++;
                        } else {
                            errorCount++;
                            Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                        }
                    } else if (response.equals(getString(R.string.exception))) {
                        Toast.makeText(LoginActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

                case "fetch_extra":{
                    JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        attemptLogin(getString(R.string.default_signin), editTextUsername.getText().toString(), StringHelper.encryptPassword(editTextPassword.getText().toString(), StringHelper.convertSaltToByte(jsonObject.getString("extra"))));
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                    }else if(response.equals(getString(R.string.unexpected))){
                        Toast.makeText(LoginActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(LoginActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
        }

    }
}
