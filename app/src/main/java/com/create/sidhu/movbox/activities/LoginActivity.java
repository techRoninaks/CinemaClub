package com.create.sidhu.movbox.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.autofill.RegexValidator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.models.UserModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements SqlDelegate {
    EditText editTextUsername;
    EditText editTextPassword;
    Button btnSignIn;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("CinemaClub", 0);
        if(sharedPreferences.getBoolean("login", false)){
            if(sharedPreferences.getString("username", "").isEmpty()){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        }
        editTextUsername = (EditText) findViewById(R.id.editText_Username);
        editTextPassword = (EditText) findViewById(R.id.editText_Password);
        btnSignIn = (Button) findViewById(R.id.buttonSignIn);
        btnSignUp = (Button) findViewById(R.id.buttonSignUp);
        View.OnClickListener onButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.buttonSignIn:
                        if(validateSignIn())
                            attemptLogin();
                        break;
                    case R.id.buttonSignUp:
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        break;
                }
            }
        };
        btnSignUp.setOnClickListener(onButtonClickListener);
        btnSignIn.setOnClickListener(onButtonClickListener);
    }

    private boolean validateSignIn() {
        String userName, password;
        boolean signIn = true;
        userName = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        if(!isValidEmail(userName)){
            editTextUsername.setError(getString(R.string.invalid_email));
            signIn = false;
        }
        if(!isValidPassword(password)){
            editTextPassword.setError(getString(R.string.invalid_password));
            signIn = false;
        }
        if(userName.isEmpty()){
            editTextUsername.setError(getString(R.string.required_field));
            signIn = false;
        }
        if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.required_field));
            signIn = false;
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

    private void attemptLogin(){
        SqlHelper sqlHelper = new SqlHelper(LoginActivity.this, LoginActivity.this);
        sqlHelper.setExecutePath("login.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", editTextUsername.getText().toString()));
        params.add(new BasicNameValuePair("password", editTextPassword.getText().toString()));
        sqlHelper.setParams(params);
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.executeUrl(true);
    }
    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
            String response = jsonObject.getString("response");
            if(response.equals(getString(R.string.response_success))){
                UserModel currentUserModel = new ModelHelper(LoginActivity.this).buildUserModel(jsonObject);
                MainActivity.currentUserModel = currentUserModel;
                SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
                sharedPreferences.edit().putString("username", currentUserModel.getEmail()).commit();
                sharedPreferences.edit().putBoolean("login", true).commit();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }else if(response.equals(getString(R.string.response_unsuccessful))){
                editTextPassword.setError(getString(R.string.invalid_cred));
                editTextUsername.setError(getString(R.string.invalid_cred));
                Toast.makeText(LoginActivity.this, getString(R.string.invalid_cred), Toast.LENGTH_SHORT).show();
            }else if(response.equals(getString(R.string.exception))){
                Toast.makeText(LoginActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
        }

    }
}
