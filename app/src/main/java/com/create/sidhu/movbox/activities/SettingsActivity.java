package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.service.autofill.RegexValidator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.adapters.PreferenceAdapter;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.PreferenceModel;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.common.StringUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;


public class SettingsActivity extends AppCompatActivity implements SqlDelegate {
    Activity activity;
    Typeface tfSemibold;
    Typeface tfRegular;

    Bundle bundle;
    TextView tvTitleImage, tvTitleName, tvTitleEmail, tvTitlePhone, tvTitleDob, tvTitleCity, tvTitleCountry, tvTitlePassword, tvTitleLanguage, tvTitleGenre;
    TextView tvChangeProfileImage, tvChangeProfilePassword, tvName, tvEmail, tvPhone, tvDob, tvCity, tvCountry;
    TextView tvAboutDetailTitle, tvVersionDetailTitle, tvDesignDetailTitle, tvDesignDetail, tvEUAPrimaryTitle, tvEUAPrimary, tvEUALicenseTitle, tvEUALicense, tvEUARestrictionsTitle, tvEUARestrictions, tvEUAModificationsTitle, tvEUAModifications, tvEUATermTitle, tvEUATerm, tvEUASeverabilityTitle, tvEUASeverability, tvEUAAmendmentsTitle, tvEUAAmendments, tvEUAContact, tvEUAContactTitle;
    EditText etName, etPhone, etDob, etCity;
    ScrollView svMainSettings, svAboutDetails, svEUADetails;
    AutoCompleteTextView etCountry;
    LinearLayout llEditProfile, llPrivacySettings, llEUA, llAbout, llLogout, llProfileInfo, llProfileButtons, llPrivacySub, llProfilePreference, llProfilePreferenceSettings;
    ImageView imgProfileEdit, imgAboutClose, imgEUAClose, imgDDArrowProfile, imgDDArrowPreference, imgDDArrowPrivacy;
    Button btnProfileSave, btnProfileCancel, btnPreferenceSave, btnPreferenceCancel;
    Switch switchPrivacyReply, switchPrivacyInfo;
    RecyclerView rvLanguage, rvGenre;
    HashMap<String, Boolean> viewState;
    ArrayList<PreferenceModel> languageModels;
    ArrayList<PreferenceModel> genreModels;
    boolean countryFlag = false;

    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.containerEditProfile:{
                    toggleEditVisibility("edit_profile", !viewState.get("edit_profile"));
                    viewState.put("edit_profile", !viewState.get("edit_profile"));
                    break;
                }
                case R.id.containerProfilePreferenceSettings:{
                    toggleEditVisibility("edit_preference", !viewState.get("edit_preference"));
                    viewState.put("edit_preference", !viewState.get("edit_preference"));
                    break;
                }
                case R.id.containerPrivacySettings:{
                    toggleEditVisibility("privacy_settings", !viewState.get("privacy_settings"));
                    viewState.put("privacy_settings", !viewState.get("privacy_settings"));
                    break;
                }
                case R.id.containerAbout:{
                    toggleEditVisibility("about", !viewState.get("about"));
                    viewState.put("about", !viewState.get("about"));
                    break;
                }
                case R.id.containerEndUserAgreement:{
                    toggleEditVisibility("eua", !viewState.get("eua"));
                    viewState.put("eua", !viewState.get("eua"));
                    break;
                }
                case R.id.containerLogout:{
                    logoutUserConfirm();
                    break;
                }
                case R.id.textView_ProfileImageChange:{
                    Bundle bundle = new ModelHelper(SettingsActivity.this).buildUserModelBundle(MainActivity.currentUserModel, "ProfileFragment");
                    Intent intent = new Intent(SettingsActivity.this, ProfileImage.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                    break;
                }case R.id.textView_ProfilePasswordChange:{
                    Intent intent = new Intent(SettingsActivity.this, ForgotPassword.class);
                    intent.putExtra("type", "password_reset");
                    startActivity(intent);
                    break;
                }
                case R.id.img_ProfileEdit:{
                    toggleEditVisibility("profile", true);
                    break;
                }
                case R.id.img_AboutClose:{
                    toggleEditVisibility("about", !viewState.get("about"));
                    viewState.put("about", !viewState.get("about"));
                    break;
                }
                case R.id.img_EUAClose:{
                    toggleEditVisibility("eua", !viewState.get("eua"));
                    viewState.put("eua", !viewState.get("eua"));
                    break;
                }
                case R.id.btn_ProfileSave:{
                    saveInfo("profile");
                    break;
                }
                case R.id.btn_ProfileCancel:{
                    initialiseViews();
                    toggleEditVisibility("profile", false);
                    break;
                }
                case R.id.btn_PreferenceSave:{
                    saveInfo("edit_preference");
                    break;
                }
                case R.id.btn_PreferenceCancel:{
                    toggleEditVisibility("edit_preference", !viewState.get("edit_preference"));
                    viewState.put("edit_preference", !viewState.get("edit_preference"));
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            Toolbar toolbar = findViewById(R.id.toolbar);
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
            activity = this;
            tfSemibold = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf");
            tfRegular = Typeface.createFromAsset(getAssets(), "fonts/myriadpro.otf");
            fetchcountry();
            fetchlanguages();
            fetchgenres();
            tvTitleImage = (TextView) findViewById(R.id.textView_TitleImage);
            tvTitlePassword = (TextView) findViewById(R.id.textView_TitlePassword);
            tvTitleName = (TextView) findViewById(R.id.textView_TitleName);
            tvTitleEmail = (TextView) findViewById(R.id.textView_TitleEmail);
            tvTitlePhone = (TextView) findViewById(R.id.textView_TitlePhone);
            tvTitleDob = (TextView) findViewById(R.id.textView_TitleDob);
            tvTitleCity = (TextView) findViewById(R.id.textView_TitleCity);
            tvTitleCountry = (TextView) findViewById(R.id.textView_TitleCountry);
            tvChangeProfileImage = (TextView) findViewById(R.id.textView_ProfileImageChange);
            tvChangeProfilePassword = (TextView) findViewById(R.id.textView_ProfilePasswordChange);
            tvName = (TextView) findViewById(R.id.textView_Name);
            tvEmail = (TextView) findViewById(R.id.textView_Email);
            tvPhone = (TextView) findViewById(R.id.textView_Phone);
            tvDob = (TextView) findViewById(R.id.textView_Dob);
            tvCity = (TextView) findViewById(R.id.textView_City);
            tvCountry = (TextView) findViewById(R.id.textView_Country);
            tvAboutDetailTitle = (TextView) findViewById(R.id.textView_AboutPrimaryTitle);
            tvVersionDetailTitle = (TextView) findViewById(R.id.textView_VersionPrimaryTitle);
            tvDesignDetailTitle = (TextView) findViewById(R.id.textView_DesignPrimaryTitle);
            tvDesignDetail = (TextView) findViewById(R.id.textView_DesignMainContent);
            tvEUAPrimaryTitle = (TextView) findViewById(R.id.textView_EUAPrimaryTitle);
            tvEUAPrimary = (TextView) findViewById(R.id.textView_EUAMainContent);
            tvEUALicenseTitle = (TextView) findViewById(R.id.textView_EUALicenseTitle);
            tvEUALicense = (TextView) findViewById(R.id.textView_EUALicenseContent);
            tvEUARestrictionsTitle = (TextView) findViewById(R.id.textView_EUARestrictionsTitle);
            tvEUARestrictions = (TextView) findViewById(R.id.textView_EUARestrictionsContent);
            tvEUAModificationsTitle = (TextView) findViewById(R.id.textView_EUAModificationsTitle);
            tvEUAModifications = (TextView) findViewById(R.id.textView_EUAModificationsContent);
            tvEUATermTitle = (TextView) findViewById(R.id.textView_EUATermTitle);
            tvEUATerm = (TextView) findViewById(R.id.textView_EUATermContent);
            tvEUASeverabilityTitle = (TextView) findViewById(R.id.textView_EUASeverabilityTitle);
            tvEUASeverability = (TextView) findViewById(R.id.textView_EUASeverabilityContent);
            tvEUAAmendmentsTitle = (TextView) findViewById(R.id.textView_EUAAmendmentsTitle);
            tvEUAAmendments = (TextView) findViewById(R.id.textView_EUAAmendmentsContent);
            tvEUAContactTitle = (TextView) findViewById(R.id.textView_EUAContactTitle);
            tvEUAContact = (TextView) findViewById(R.id.textView_EUAContactContent);
            etName = (EditText) findViewById(R.id.editText_Name);
            etPhone = (EditText) findViewById(R.id.editText_Phone);
            etDob = (EditText) findViewById(R.id.editText_Dob);
            etCity = (EditText) findViewById(R.id.editText_City);
            etCountry = (AutoCompleteTextView) findViewById(R.id.editText_Country);
            rvGenre = (RecyclerView) findViewById(R.id.recyclerView_Genres);
            rvLanguage = (RecyclerView) findViewById(R.id.recyclerView_Languages);
            imgProfileEdit = (ImageView) findViewById(R.id.img_ProfileEdit);
            imgAboutClose = (ImageView) findViewById(R.id.img_AboutClose);
            imgEUAClose = (ImageView) findViewById(R.id.img_EUAClose);
            imgDDArrowProfile = (ImageView) findViewById(R.id.img_DropDownArrowEditProfile);
            imgDDArrowPreference = (ImageView) findViewById(R.id.img_DropDownArrowPreferences);
            imgDDArrowPrivacy = (ImageView) findViewById(R.id.img_DropDownArrowPrivacy);
            llEditProfile = (LinearLayout) findViewById(R.id.containerEditProfile);
            llAbout = (LinearLayout) findViewById(R.id.containerAbout);
            llEUA = (LinearLayout) findViewById(R.id.containerEndUserAgreement);
            llPrivacySettings = (LinearLayout) findViewById(R.id.containerPrivacySettings);
            llLogout = (LinearLayout) findViewById(R.id.containerLogout);
            llProfileInfo = (LinearLayout) findViewById(R.id.containerProfileInfo);
            llProfileButtons = (LinearLayout) findViewById(R.id.containerProfileButtons);
            llPrivacySub = (LinearLayout) findViewById(R.id.containerPrivacySub);
            llProfilePreferenceSettings = (LinearLayout) findViewById(R.id.containerProfilePreferenceSettings);
            llProfilePreference = (LinearLayout) findViewById(R.id.containerProfilePreference);
            svAboutDetails = (ScrollView) findViewById(R.id.scrollView_AboutDetails);
            svMainSettings = (ScrollView) findViewById(R.id.scrollView_Settings);
            svEUADetails = (ScrollView) findViewById(R.id.scrollView_EUA);
            btnProfileCancel = (Button) findViewById(R.id.btn_ProfileCancel);
            btnProfileSave = (Button) findViewById(R.id.btn_ProfileSave);
            btnPreferenceSave = (Button) findViewById(R.id.btn_PreferenceSave);
            btnPreferenceCancel = (Button) findViewById(R.id.btn_PreferenceCancel);
            switchPrivacyInfo = (Switch) findViewById(R.id.switch_PrivacyInfo);
            switchPrivacyReply = (Switch) findViewById(R.id.switch_PrivacyReply);
            initialiseViews();
            tvChangeProfileImage.setOnClickListener(onClickListener);
            tvChangeProfilePassword.setOnClickListener(onClickListener);
            imgProfileEdit.setOnClickListener(onClickListener);
            imgAboutClose.setOnClickListener(onClickListener);
            imgEUAClose.setOnClickListener(onClickListener);
            btnProfileSave.setOnClickListener(onClickListener);
            btnProfileCancel.setOnClickListener(onClickListener);
            btnPreferenceSave.setOnClickListener(onClickListener);
            btnPreferenceCancel.setOnClickListener(onClickListener);
            llEditProfile.setOnClickListener(onClickListener);
            llAbout.setOnClickListener(onClickListener);
            llEUA.setOnClickListener(onClickListener);
            llPrivacySettings.setOnClickListener(onClickListener);
            llLogout.setOnClickListener(onClickListener);
            llProfilePreferenceSettings.setOnClickListener(onClickListener);
            viewState = new HashMap<>();
            viewState.put("edit_profile", false);
            viewState.put("privacy_settings", false);
            viewState.put("about", false);
            viewState.put("eua", false);
            viewState.put("edit_preference", false);
            switchPrivacyInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String privacy = MainActivity.currentUserModel.getPrivacy();
                    privacy = privacy.charAt(0) + (isChecked ? "1" : "0");
                    savePrivacyInfo("privacy", Integer.parseInt(privacy, 2));
                }
            });
            switchPrivacyReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String privacy = MainActivity.currentUserModel.getPrivacy();
                    privacy = (isChecked ? "1" : "0") + privacy.charAt(1);
                    savePrivacyInfo("privacy", Integer.parseInt(privacy, 2));
                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try{
            switch (sqlHelper.getActionString()){
                case "country":{
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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_list_item_1, arrayList);
                        etCountry.setAdapter(arrayAdapter);
                        etCountry.setThreshold(1);
                        countryFlag = true;
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        countryFlag = false;
                    }
                    break;
                }
                case "language":{
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("language_data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        languageModels = new ArrayList<>();
                        int length = jsonArray.length();
                        ModelHelper modelHelper = new ModelHelper(SettingsActivity.this);
                        for(int i = 1; i < length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            PreferenceModel preferenceModel = modelHelper.buildPreferenceModel(jsonObject, "language");
                            languageModels.add(preferenceModel);
                        }
                        attachAdapter(rvLanguage, "language");
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        tvTitleLanguage.setVisibility(View.GONE);
                        rvLanguage.setVisibility(View.GONE);
                    }
                    break;
                }
                case "genre":{
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("genre_data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        genreModels = new ArrayList<>();
                        int length = jsonArray.length();
                        ModelHelper modelHelper = new ModelHelper(SettingsActivity.this);
                        for(int i = 1; i < length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            PreferenceModel preferenceModel = modelHelper.buildPreferenceModel(jsonObject, "genre");
                            genreModels.add(preferenceModel);
                        }
                        attachAdapter(rvGenre, "genre");
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        tvTitleGenre.setVisibility(View.GONE);
                        rvGenre.setVisibility(View.GONE);
                    }
                    break;
                }
                case "edit_profile":{
                    String response = sqlHelper.getJSONResponse().getJSONObject("user_data").getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        MainActivity.currentUserModel.setName(etName.getText().toString());
                        MainActivity.currentUserModel.setDob(etDob.getText().toString());
                        MainActivity.currentUserModel.setCity(etCity.getText().toString());
                        MainActivity.currentUserModel.setCountry(etCountry.getText().toString());
                        MainActivity.currentUserModel.setPhone(etPhone.getText().toString());
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        Toast.makeText(SettingsActivity.this, getString(R.string.unexpected) + ". Could not update information.", Toast.LENGTH_SHORT).show();
                    }else if(response.equals(getString(R.string.unexpected))){
                        Toast.makeText(SettingsActivity.this, getString(R.string.unexpected) + ". Could not update information.", Toast.LENGTH_SHORT).show();
                    }
                    initialiseViews();
                    toggleEditVisibility("profile", false);
                    break;
                }
                case "update_preference":{
                    String response = sqlHelper.getJSONResponse().getJSONObject("user_data").getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        Toast.makeText(SettingsActivity.this, "Your preferences have been saved.", Toast.LENGTH_SHORT).show();
                        toggleEditVisibility("edit_preference", !viewState.get("edit_preference"));
                        viewState.put("edit_preference", !viewState.get("edit_preference"));
                        MainActivity.currentUserModel.setLanguagePreference(sqlHelper.getExtras().get("language"));
                        MainActivity.currentUserModel.setGenrePreference(sqlHelper.getExtras().get("genre"));
                    }else if(response.equals(getString(R.string.response_unsuccessful))){
                        Toast.makeText(SettingsActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                }
                case "privacy":{
                    String response = sqlHelper.getJSONResponse().getJSONObject("user_data").getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        MainActivity.currentUserModel.setPrivacy(Integer.parseInt(sqlHelper.getExtras().get("privacy_value")));
                    }else if(response.equals(getString(R.string.response_unsuccessful))){

                    }
                    break;
                }
            }
        }catch (Exception e){
            Toast.makeText(SettingsActivity.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchcountry(){
        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
        sqlHelper.setExecutePath("country.php");
        sqlHelper.setActionString("country");
        sqlHelper.setParams(new ContentValues());
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.executeUrl(false);
    }

    private void fetchlanguages(){
        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
        sqlHelper.setExecutePath("get-languages.php");
        sqlHelper.setActionString("language");
        sqlHelper.setParams(new ContentValues());
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.executeUrl(false);
    }

    private void fetchgenres(){
        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
        sqlHelper.setExecutePath("get-genres.php");
        sqlHelper.setActionString("genre");
        sqlHelper.setParams(new ContentValues());
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.executeUrl(false);
    }

    private void initialiseViews(){
        try {
            tvDesignDetail.setMovementMethod(LinkMovementMethod.getInstance());
            tvTitleImage.setTypeface(tfSemibold);
            tvTitlePassword.setTypeface(tfSemibold);
            tvTitleName.setTypeface(tfSemibold);
            tvTitleEmail.setTypeface(tfSemibold);
            tvTitlePhone.setTypeface(tfSemibold);
            tvTitleDob.setTypeface(tfSemibold);
            tvTitleCity.setTypeface(tfSemibold);
            tvTitleCountry.setTypeface(tfSemibold);
            tvAboutDetailTitle.setTypeface(tfSemibold);
            tvDesignDetailTitle.setTypeface(tfSemibold);
            tvVersionDetailTitle.setTypeface(tfSemibold);
            tvEUAPrimaryTitle.setTypeface(tfSemibold);
            tvEUALicenseTitle.setTypeface(tfSemibold);
            tvEUARestrictionsTitle.setTypeface(tfSemibold);
            tvEUAModificationsTitle.setTypeface(tfSemibold);
            tvEUATermTitle.setTypeface(tfSemibold);
            tvEUASeverabilityTitle.setTypeface(tfSemibold);
            tvEUAAmendmentsTitle.setTypeface(tfSemibold);
            tvEUAContactTitle.setTypeface(tfSemibold);
            tvName.setText(MainActivity.currentUserModel.getName());
            tvEmail.setText(MainActivity.currentUserModel.getEmail());
            String phone = MainActivity.currentUserModel.getPhone();
            phone = phone.startsWith("*0*") ? "" : phone;
            tvPhone.setText(phone);
            String dob = MainActivity.currentUserModel.getDob();
            dob = dob.isEmpty() ? "" : dob.equals("null") ? "" : dob == null ? "" : dob;
            tvDob.setText(dob);
            String city = MainActivity.currentUserModel.getCity();
            city = city.isEmpty() ? "" : city.equals("null") ? "" : city == null ? "" : city;
            tvCity.setText(city);
            String country = MainActivity.currentUserModel.getCountry();
            country = country.isEmpty() ? "" : country.equals("null") ? "" : country == null ? "" : country;
            tvCountry.setText(country);
            etName.setText(MainActivity.currentUserModel.getName());
            etPhone.setText(phone);
            etCity.setText(city);
            etDob.setText(dob);
            etCountry.setText(country);
            String privacy = MainActivity.currentUserModel.getPrivacy();
            switchPrivacyReply.setChecked(privacy.charAt(0) == '1');
            switchPrivacyInfo.setChecked(privacy.charAt(1) == '1');
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void toggleEditVisibility(String viewClass, boolean visible){
        switch (viewClass){
            case "profile":{
                if(visible){
                    imgProfileEdit.setVisibility(View.INVISIBLE);
                    etCountry.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.VISIBLE);
                    etPhone.setVisibility(View.VISIBLE);
                    etCity.setVisibility(View.VISIBLE);
                    etDob.setVisibility(View.VISIBLE);
                    llProfileButtons.setVisibility(View.VISIBLE);
                    tvCountry.setVisibility(View.GONE);
                    tvName.setVisibility(View.GONE);
                    tvPhone.setVisibility(View.GONE);
                    tvCity.setVisibility(View.GONE);
                    tvDob.setVisibility(View.GONE);
                }else{
                    imgProfileEdit.setVisibility(View.VISIBLE);
                    etCountry.setVisibility(View.GONE);
                    etName.setVisibility(View.GONE);
                    etPhone.setVisibility(View.GONE);
                    etCity.setVisibility(View.GONE);
                    etDob.setVisibility(View.GONE);
                    llProfileButtons.setVisibility(View.GONE);
                    tvCountry.setVisibility(View.VISIBLE);
                    tvName.setVisibility(View.VISIBLE);
                    tvPhone.setVisibility(View.VISIBLE);
                    tvCity.setVisibility(View.VISIBLE);
                    tvDob.setVisibility(View.VISIBLE);
//                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
                break;
            }
            case "edit_profile":{
                if(visible){
                   llProfileInfo.setVisibility(View.VISIBLE);
                    imgDDArrowProfile.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llProfileInfo.setVisibility(View.GONE);
                    imgDDArrowProfile.setImageDrawable(getDrawable(R.drawable.ic_arrow_down));
                    toggleEditVisibility("profile", false);
                    initialiseViews();
                }
                break;
            }
            case "privacy_settings":{
                if(visible){
                    llPrivacySub.setVisibility(View.VISIBLE);
                    imgDDArrowPrivacy.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llPrivacySub.setVisibility(View.GONE);
                    imgDDArrowPrivacy.setImageDrawable(getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
            case "about":{
                if(visible){
                    svMainSettings.setVisibility(View.GONE);
                    svAboutDetails.setVisibility(View.VISIBLE);
                }else{
                    svMainSettings.setVisibility(View.VISIBLE);
                    svAboutDetails.setVisibility(View.GONE);
                }
                break;
            }
            case "eua":{
                if(visible){
                    svMainSettings.setVisibility(View.GONE);
                    svEUADetails.setVisibility(View.VISIBLE);
                }else{
                    svMainSettings.setVisibility(View.VISIBLE);
                    svEUADetails.setVisibility(View.GONE);
                }
                break;
            }
            case "edit_preference":{
                if(visible){
                    llProfilePreference.setVisibility(View.VISIBLE);
                    imgDDArrowPreference.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llProfilePreference.setVisibility(View.GONE);
                    imgDDArrowPreference.setImageDrawable(getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
        }
    }

    private void saveInfo(String viewClass){
        try {
            switch (viewClass) {
                case "profile": {
                    if (validateFields(viewClass)) {
                        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
                        sqlHelper.setExecutePath("update-profile.php");
                        sqlHelper.setMethod("GET");
                        sqlHelper.setActionString("edit_profile");
                        ContentValues params = new ContentValues();
                        params.put("name", etName.getText().toString());
                        params.put("c_id", MainActivity.currentUserModel.getUserId());
                        params.put("phone", etPhone.getText().toString());
                        params.put("country", etCountry.getText().toString());
                        params.put("city", etCity.getText().toString());
                        params.put("dob", etDob.getText().toString());
                        sqlHelper.setParams(params);
                        sqlHelper.executeUrl(true);
                    }
                    break;
                }
                case "edit_preference": {
                    String languagePreference = MainActivity.currentUserModel.getLanguagePreference();
                    String genrePreference = MainActivity.currentUserModel.getGenrePreference();
                    if (languagePreference.equals("NULL")) {
                        int size = languageModels.size();
                        String temp = "";
                        for (int i = 0; i < size; i++) {
                            temp = temp.concat("0");
                        }
                        languagePreference = temp;
                    }
                    if (genrePreference.equals("NULL")) {
                        int size = genreModels.size();
                        String temp = "";
                        for (int i = 0; i < size; i++) {
                            temp = temp.concat("0");
                        }
                        genrePreference = temp;
                    }
                    updatePreferences(languagePreference, genrePreference);
                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void savePrivacyInfo(String privacyType, int value){
        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString(privacyType);
        sqlHelper.setExecutePath("update-privacy.php");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("value", "" + value);
        sqlHelper.setParams(params);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("privacy_value", "" + value);
        sqlHelper.setExtras(extras);
        sqlHelper.executeUrl(false);
    }

    private void updatePreferences(String languagePreference, String genrePreference){
        try {
            String preference = "";
            int size = languageModels.size();
            for (int i = 0; i < size; i++) {
                int index = Integer.parseInt(languageModels.get(i).getId()) - 1;
                languagePreference = languagePreference.substring(0, index) + (languageModels.get(i).getChecked() ? "1" : "0") + languagePreference.substring(index + 1);
            }
            preference += size + "!@" + Integer.parseInt(languagePreference, 2) + "!~";
            size = genreModels.size();
            for (int i = 0; i < size; i++) {
                int index = Integer.parseInt(genreModels.get(i).getId()) - 1;
                genrePreference = genrePreference.substring(0, index) + (genreModels.get(i).getChecked() ? "1" : "0") + genrePreference.substring(index + 1);
            }
            preference += size + "!@" + Integer.parseInt(genrePreference, 2);
            savePreferenceInfo(preference, languagePreference, genrePreference);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void savePreferenceInfo(String preference, String languagePreference, String genrePreference){
        SqlHelper sqlHelper = new SqlHelper(SettingsActivity.this, SettingsActivity.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setExecutePath("update-preference.php");
        sqlHelper.setActionString("update_preference");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("preference", preference);
        sqlHelper.setParams(params);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("language", languagePreference);
        extras.put("genre", genrePreference);
        sqlHelper.setExtras(extras);
        sqlHelper.executeUrl(true);
    }

    private boolean validateFields(String viewClass){
        boolean isValid = true;
        switch (viewClass){
            case "profile":{
                Pattern pattern = Pattern.compile("((18|19|20|21)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])");
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String dob = etDob.getText().toString();
                String country = etCountry.getText().toString();
                String city = etCity.getText().toString();
                if(name.isEmpty()){
                    etName.setError(getString(R.string.required_field));
                    isValid = false;
                }
                if(!PhoneNumberUtils.isGlobalPhoneNumber(phone)){
                    etPhone.setError(getString(R.string.invalid_phone));
                    isValid = false;
                }
                if(!pattern.matcher(dob).matches()) {
                    etDob.setError(getString(R.string.invalid_date));
                    isValid = false;
                }
                break;
            }
        }
        return isValid;
    }

    private void logoutUserConfirm(){
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            logoutUser();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
            alertDialog.setTitle(getString(R.string.logout));
            alertDialog.setMessage(getString(R.string.confirmation));
            alertDialog.setPositiveButton(R.string.confirmation_yes, dialogClickListener);
            alertDialog.setNegativeButton(R.string.confirmation_no, dialogClickListener);
            alertDialog.show();
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }

    private void logoutUser(){
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("CinemaClub", 0);
            sharedPreferences.edit().putString("username", "").commit();
            sharedPreferences.edit().putBoolean("login", false).commit();
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            if (isLoggedIn)
                LoginManager.getInstance().logOut();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
            mGoogleApiClient.connect();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            if (mGoogleSignInClient != null) {
                mGoogleSignInClient.signOut();
            }
            MainActivity.currentUserModel = null;
            HomeFragment.homeModels = null;
            FavouritesFragment.favouritesList = null;
            ProfileFragment.currentUserWatchlist = null;
            MoviesFragment.masterMovieTypeList = null;
            MoviesFragment.masterMovieList = null;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void attachAdapter(RecyclerView recyclerView, String type){
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(SettingsActivity.this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            if (type.equals("language") || type.equals("genre")) {
                PreferenceAdapter preferenceAdapter = new PreferenceAdapter(SettingsActivity.this, type.equals("language") ? languageModels : genreModels, recyclerView, "settings");
                recyclerView.setAdapter(preferenceAdapter);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(SettingsActivity.this, EmailHelper.TECH_SUPPORT, "Error: SettingsActivity", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
