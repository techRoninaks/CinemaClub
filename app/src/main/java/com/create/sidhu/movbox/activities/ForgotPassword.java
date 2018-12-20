package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.nostra13.universalimageloader.utils.L;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForgotPassword extends AppCompatActivity implements SqlDelegate {

    Activity activity;

    LinearLayout llContainerMain, llContainerOtp, llContainerPassword, llContainerOriginalPassword;
    EditText etUserName, etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6, etPassword, etConfirmPassword, etOriginalPassword;
    Button btnSubmit, btnVerify, btnCancel, btnPasswordSave, btnOriginalPassword;
    String otp, email, password, type;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.buttonRecover:{
                    if(validate(1)){
                        email = etUserName.getText().toString();
                        SqlHelper sqlHelper = new SqlHelper(ForgotPassword.this, ForgotPassword.this);
                        sqlHelper.setMethod("GET");
                        sqlHelper.setExecutePath("forgot-password.php");
                        sqlHelper.setActionString("stage:1");
                        ArrayList<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("email", email));
                        sqlHelper.setParams(params);
                        sqlHelper.executeUrl(true);
                    }
                    break;
                }
                case R.id.buttonCancel:{
                    finish();
                    break;
                }
                case R.id.buttonSubmitOtp:{
                    if(validate(2)){
                        llContainerOtp.setVisibility(View.GONE);
                        llContainerPassword.setVisibility(View.VISIBLE);
                    }
                    else
                        Toast.makeText(ForgotPassword.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.buttonSubmitPassword:{
                    if(validate(3)){
                        try {
                            SqlHelper sqlHelper = new SqlHelper(ForgotPassword.this, ForgotPassword.this);
                            sqlHelper.setMethod("GET");
                            sqlHelper.setExecutePath("reset-password.php");
                            sqlHelper.setActionString("stage:3");
                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair("email", email));
                            params.add(new BasicNameValuePair("password", StringHelper.encryptPassword(etPassword.getText().toString())));
                            sqlHelper.setParams(params);
                            sqlHelper.executeUrl(true);
                        }catch (Exception e) {
                            EmailHelper emailHelper = new EmailHelper(ForgotPassword.this, EmailHelper.TECH_SUPPORT, "Error: PasswordReset - Stage 3", StringHelper.convertStackTrace(e));
                            emailHelper.sendEmail();
                        }
                    }
                    break;
                }
                case R.id.buttonSubmitOriginalPassword:{
                    try {
                        String passwordInfo[] = password.split("!~");
                        if (password.equals(StringHelper.encryptPassword(passwordInfo[0], StringHelper.convertSaltToByte(passwordInfo[1])))) {
                            llContainerOriginalPassword.setVisibility(View.GONE);
                            llContainerPassword.setVisibility(View.VISIBLE);
                        } else {
                            etOriginalPassword.setError(getString(R.string.password_mismatch));
                        }
                    }catch (Exception e){
                        EmailHelper emailHelper = new EmailHelper(ForgotPassword.this, EmailHelper.TECH_SUPPORT, "Error: PasswordReset", StringHelper.convertStackTrace(e));
                        emailHelper.sendEmail();
                    }
                    break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password);
            type = getIntent().getStringExtra("type");
            activity = this;
            llContainerMain = (LinearLayout) findViewById(R.id.containerMainComponent);
            llContainerOtp = (LinearLayout) findViewById(R.id.containerOtp);
            llContainerPassword = (LinearLayout) findViewById(R.id.containerPassword);
            llContainerOriginalPassword = (LinearLayout) findViewById(R.id.containerConfirmPassword);
            if (type.equals("password_reset")) {
                email = MainActivity.currentUserModel.getEmail();
                llContainerMain.setVisibility(View.GONE);
                llContainerOriginalPassword.setVisibility(View.VISIBLE);
                fetchPassword();
            }
            etUserName = (EditText) findViewById(R.id.editText_Username);
            etOtp1 = (EditText) findViewById(R.id.editText_Otp1);
            etOtp2 = (EditText) findViewById(R.id.editText_Otp2);
            etOtp3 = (EditText) findViewById(R.id.editText_Otp3);
            etOtp4 = (EditText) findViewById(R.id.editText_Otp4);
            etOtp5 = (EditText) findViewById(R.id.editText_Otp5);
            etOtp6 = (EditText) findViewById(R.id.editText_Otp6);
            etOriginalPassword = (EditText) findViewById(R.id.editText_OriginalPassword);
            etPassword = (EditText) findViewById(R.id.editText_Password);
            etConfirmPassword = (EditText) findViewById(R.id.editText_ConfirmPassword);
            btnSubmit = (Button) findViewById(R.id.buttonRecover);
            btnCancel = (Button) findViewById(R.id.buttonCancel);
            btnVerify = (Button) findViewById(R.id.buttonSubmitOtp);
            btnPasswordSave = (Button) findViewById(R.id.buttonSubmitPassword);
            btnOriginalPassword = (Button) findViewById(R.id.buttonSubmitOriginalPassword);

            btnSubmit.setOnClickListener(onClickListener);
            btnCancel.setOnClickListener(onClickListener);
            btnVerify.setOnClickListener(onClickListener);
            btnPasswordSave.setOnClickListener(onClickListener);
            btnOriginalPassword.setOnClickListener(onClickListener);

            etOtp1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp1.getText().toString().length() == 1) {
                        etOtp2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etOtp2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp2.getText().toString().length() == 1) {
                        etOtp3.requestFocus();
                    } else if (etOtp2.getText().toString().length() == 0) {
                        etOtp1.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etOtp3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp3.getText().toString().length() == 1) {
                        etOtp4.requestFocus();
                    } else if (etOtp3.getText().toString().length() == 0) {
                        etOtp2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etOtp4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp4.getText().toString().length() == 1) {
                        etOtp5.requestFocus();
                    } else if (etOtp4.getText().toString().length() == 0) {
                        etOtp3.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etOtp5.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp5.getText().toString().length() == 1) {
                        etOtp6.requestFocus();
                    } else if (etOtp5.getText().toString().length() == 0) {
                        etOtp4.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etOtp6.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etOtp6.getText().toString().length() == 1) {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    } else if (etOtp6.getText().toString().length() == 0) {
                        etOtp5.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ForgotPassword.this, EmailHelper.TECH_SUPPORT, "Error: ForgotPassword", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try{
            if(sqlHelper.getActionString().equals("stage:1")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getString("response");
                if(response.equals(getString(R.string.response_success))){
                    otp = jsonObject.getString("otp");
                    llContainerMain.setVisibility(View.GONE);
                    llContainerOtp.setVisibility(View.VISIBLE);
                }else if(response.equals(getString(R.string.response_unsuccessful))){
                    Toast.makeText(ForgotPassword.this, "Your email is invalid or unregistered", Toast.LENGTH_SHORT).show();
                }else if(response.equals(getString(R.string.unexpected))){
                    throw new Exception();
                }
            }else if(sqlHelper.getActionString().equals("stage:3")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                if(response.equals(getString(R.string.response_success))){
                    Toast.makeText(ForgotPassword.this, "Password successfully changed.", Toast.LENGTH_SHORT).show();
                }else if(response.equals(getString(R.string.response_unsuccessful)) || response.equals(getString(R.string.unexpected))){
                    Toast.makeText(ForgotPassword.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
                finish();
            }else if(sqlHelper.getActionString().equals("get_password")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                String response = jsonObject.getString("response");
                if(response.equals(getString(R.string.response_success))){
                    password = jsonObject.getString("password");
                }else if(response.equals(getString(R.string.response_unsuccessful)) || response.equals(getString(R.string.unexpected))){
                    Toast.makeText(ForgotPassword.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }catch (Exception e){
            Toast.makeText(ForgotPassword.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(ForgotPassword.this, EmailHelper.TECH_SUPPORT, "Error: ForgotPassword", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private boolean validate(int stage){
        boolean isValid = true;
        switch (stage){
            case 1:{
                String userName = etUserName.getText().toString();
                if(userName.isEmpty()) {
                    etUserName.setError(getString(R.string.required_field));
                    isValid = false;
                }else if(!userName.contains("@")){
                    etUserName.setError(getString(R.string.invalid_email));
                }
                break;
            }
            case 2:{
                String otp1 = etOtp1.getText().toString();
                String otp2 = etOtp2.getText().toString();
                String otp3 = etOtp3.getText().toString();
                String otp4 = etOtp4.getText().toString();
                String otp5 = etOtp5.getText().toString();
                String otp6 = etOtp6.getText().toString();
                if(otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()){
                    etOtp6.setError(getString(R.string.required_field));
                    isValid = false;
                }else{
                    String check = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
                    if(!check.equals(otp))
                        isValid = false;
                }
               break;
            }
            case 3:{
                String password = etPassword.getText().toString();
                String passwordConfirm = etConfirmPassword.getText().toString();
                if(password.isEmpty()){
                    etPassword.setError(getString(R.string.required_field));
                    isValid = false;
                }else if(!LoginActivity.isValidPassword(password)){
                    etPassword.setError(getString(R.string.invalid_password));
                    isValid = false;
                }
                if(passwordConfirm.isEmpty()){
                    etConfirmPassword.setError(getString(R.string.required_field));
                    isValid = false;
                }else if(!password.equals(passwordConfirm)){
                    etConfirmPassword.setError(getString(R.string.password_mismatch));
                    isValid = false;
                }
                break;
            }
        }
        return isValid;
    }

    private void fetchPassword(){
        try {
            SqlHelper sqlHelper = new SqlHelper(ForgotPassword.this, ForgotPassword.this);
            sqlHelper.setMethod("GET");
            sqlHelper.setExecutePath("get-password.php");
            sqlHelper.setActionString("get_password");
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
            sqlHelper.setParams(params);
            sqlHelper.executeUrl(false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ForgotPassword.this, EmailHelper.TECH_SUPPORT, "Error: ForgotPassword", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
