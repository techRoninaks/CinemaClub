package com.create.sidhu.movbox.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.SqlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements SqlDelegate{

    EditText editTextName;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextPasswordConfirm;
    EditText editTextPhone;
    Spinner spinnerCountry;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextName = (EditText) findViewById(R.id.editText_Name);
        editTextUsername = (EditText) findViewById(R.id.editText_Username);
        editTextPassword = (EditText) findViewById(R.id.editText_Password);
        editTextPasswordConfirm = (EditText) findViewById(R.id.editText_ConfirmPassword);
        editTextPhone = (EditText) findViewById(R.id.editText_Phone);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_Country);
        btnSignUp = (Button) findViewById(R.id.buttonSignUp);

        View.OnClickListener onButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.buttonSignUp:
                        if(validateSignUp()){
                            attemptSignUp();
                        }
                }
            }
        };
        btnSignUp.setOnClickListener(onButtonClickListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateCountry();
    }

    private void attemptSignUp() {
        SqlHelper sqlHelper = new SqlHelper(RegisterActivity.this, RegisterActivity.this);
        sqlHelper.setExecutePath("registration.php");
        sqlHelper.setActionString("register");
        sqlHelper.setMethod(getString(R.string.method_post));
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", editTextName.getText().toString()));
        params.add(new BasicNameValuePair("username", editTextUsername.getText().toString()));
        params.add(new BasicNameValuePair("password", editTextPassword.getText().toString()));
        params.add(new BasicNameValuePair("country", spinnerCountry.getSelectedItem().toString()));
        params.add(new BasicNameValuePair("phone", editTextPhone.getText().toString()));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
        Toast.makeText(RegisterActivity.this, "Attempting SignUp", Toast.LENGTH_SHORT).show();
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
        phone = editTextPhone.getText().toString();

        if(!LoginActivity.isValidEmail(email)){
            editTextUsername.setError(getString(R.string.invalid_email));
            signUp = false;
        }
        if(!LoginActivity.isValidPassword(password)){
            editTextPassword.setError(getString(R.string.password));
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
        if(phone.isEmpty()){
            editTextPhone.setError(getString(R.string.required_field));
            signUp = false;
        }
        return signUp;
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            switch (sqlHelper.getActionString()) {
                case "register": {
                    String response = sqlHelper.getJSONResponse().getString("response");
                    if (response.equals(getString(R.string.exception))) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    } else if(response.equals(getString(R.string.response_success))){
                        Toast.makeText(RegisterActivity.this, getString(R.string.success_register) + ". " + getString(R.string.continue_login), Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
                    break;
                case "country": {
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("country_data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        int length = jsonArray.length();
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (int i = 1; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            arrayList.add(jsonObject.getString("name"));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1, arrayList);
                        spinnerCountry.setAdapter(arrayAdapter);

                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                }
                    break;
            }
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
        }
    }
}
