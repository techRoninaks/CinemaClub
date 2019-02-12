package com.create.sidhu.movbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.CallbackDelegate;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.RatingsAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.PermissionsHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.RatingsModel;
import com.create.sidhu.movbox.models.UserModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

public class RatingsDialog extends DialogFragment implements SqlDelegate {
    Context context;
    TextView tvTitle, tvSubtitle, tvUserRating, tvTotalRatings, tvTabTop, tvTabFollowing, tvTabAll;
    Typeface tfSemibold;
    Typeface tfRegular;
    RecyclerView recyclerView;
    RatingBar rbUserRating;
    LinearLayout llRecyclerView, llPlaceholder, llContainerTabs;
    Button btnSubmit, btnCancel;
    ImageView imgMore;
    ArrayList<ActorModel> actorModels;
    ArrayList<RatingsModel> ratingsModels, ratingsModelsTop, ratingsModelsFollowing;
    Bundle bundle;
    String type;
    boolean isRated = false;
    boolean initiallyRated = false;
    float currentRating;
    int userfetchflag = 0;

    CallbackDelegate callbackDelegate;
    public void fetchUserinfo(ArrayList<RatingsModel> ratingList,int position, Context context) {
        userfetchflag = 1;
        SqlHelper sqlHelper = new SqlHelper(context, RatingsDialog.this);
        sqlHelper.setExecutePath("fetch-user.php");
        sqlHelper.setActionString("get_user");
        ContentValues params = new ContentValues();
        params.put("u_id", ratingList.get(position).getUserId());
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        sqlHelper.setMethod(getString(R.string.method_get));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSubmit:{
                    submitRatings();
                    break;
                }
                case R.id.btnCancel:{
                    dismiss();
                    break;
                }
                case R.id.textView_TabAll:{
                    toggleTab(R.id.textView_TabAll);
                    attachAdapter(recyclerView, ratingsModels);
                    break;
                }
                case R.id.textView_TabTop:{
                    toggleTab(R.id.textView_TabTop);
                    attachAdapter(recyclerView, ratingsModelsTop);
                    break;
                }
                case R.id.textView_TabFollowing:{
                    toggleTab(R.id.textView_TabFollowing);
                    attachAdapter(recyclerView, ratingsModelsFollowing);
                    break;
                }
                case R.id.img_More:{
                    PopupMenu popupMenu = new PopupMenu(context, imgMore);
                    popupMenu.getMenuInflater().inflate(R.menu.ratings_dialog_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if(id == R.id.menu_share){
                                takeScreenshot();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    break;
                }
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        context = getActivity();
        bundle = getArguments();
        type = bundle.getString("r_type");
        fetchRatings();
        tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ratings_dialog, null);
        builder.setView(view);
        try {
            tvTitle = view.findViewById(R.id.textView_Title);
            tvSubtitle = view.findViewById(R.id.textView_Subtitle);
            tvUserRating = view.findViewById(R.id.textView_UserRating);
            tvTotalRatings = view.findViewById(R.id.textView_TotalRating);
            rbUserRating = view.findViewById(R.id.rb_UserRating);
            recyclerView = view.findViewById(R.id.recyclerView);
            llRecyclerView = view.findViewById(R.id.containerRecyclerView);
            llPlaceholder = view.findViewById(R.id.containerPlaceholder);
            llContainerTabs = view.findViewById(R.id.containerTabs);
            tvTabAll = view.findViewById(R.id.textView_TabAll);
            tvTabFollowing = view.findViewById(R.id.textView_TabFollowing);
            tvTabTop = view.findViewById(R.id.textView_TabTop);
            btnSubmit = view.findViewById(R.id.btnSubmit);
            btnCancel = view.findViewById(R.id.btnCancel);
            imgMore = view.findViewById(R.id.img_More);
            tvTitle.setText(bundle.getString("name") + "(" + bundle.getString("language") + ") (" + bundle.getString("display_dimension") + ")");
            tvSubtitle.setText(bundle.getString("genre"));
            rbUserRating.setRating(0.0f);
            tvUserRating.setTypeface(tfSemibold);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(bundle.getString("rating") + "/10");
            stringBuilder.setSpan(new CalligraphyTypefaceSpan(tfSemibold), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(" from ");
            SpannableString spannableString = new SpannableString(StringHelper.formatTextCount(Integer.parseInt(bundle.getString("total_ratings"))));
            spannableString.setSpan(new CalligraphyTypefaceSpan(tfSemibold), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(spannableString);
            stringBuilder.append(" votes");
            if (type.equals("cast")) {
                tvUserRating.setText("0/10");
                if(bundle.containsKey("isIdentity")){
                    rbUserRating.setIsIndicator(true);
                }
            } else if (type.equals("list")) {
                tvUserRating.setVisibility(View.GONE);
                llContainerTabs.setVisibility(View.VISIBLE);
                tvTabTop.setTextColor(context.getResources().getColor(R.color.colorTextPrimary));
                tvTabFollowing.setTextColor(context.getResources().getColor(R.color.colorTextSecondary));
                tvTabAll.setTextColor(context.getResources().getColor(R.color.colorTextSecondary));
                btnSubmit.setVisibility(View.GONE);
                btnCancel.setText("Close");
                rbUserRating.setRating(Float.parseFloat(bundle.getString("rating")));
                rbUserRating.setIsIndicator(true);
            }
            tvTotalRatings.setText(stringBuilder);
            btnSubmit.setOnClickListener(onClickListener);
            btnCancel.setOnClickListener(onClickListener);
            tvTabAll.setOnClickListener(onClickListener);
            tvTabFollowing.setOnClickListener(onClickListener);
            tvTabTop.setOnClickListener(onClickListener);
            imgMore.setOnClickListener(onClickListener);
            rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    tvUserRating.setText(rating + "/10");
                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsDialog", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void dismiss(){
        super.dismiss();
        HashMap<String, String> extras = new HashMap<>();
        if(type.equals("cast")) {
            if (isRated && callbackDelegate != null) {
                int totalRatings = Integer.parseInt(bundle.getString("total_ratings"));
                if (!initiallyRated)
                    totalRatings += 1;
                extras.put("total_ratings", "" + totalRatings);
                extras.put("avg_ratings", "" + currentRating);
                extras.put("movie_id", bundle.getString("id"));
                callbackDelegate.onResultReceived("rating", true, extras);
            }
        }else if(type.equals("list")){
            if (userfetchflag == 1){
                callbackDelegate.onResultReceived("profile_nav", true, bundle.getBundle("r_bundle"));
            }
        }
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try{
            if(sqlHelper.getActionString().equals("get_cast_rating")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("ratings_data");
                String response = jsonArray.getJSONObject(0).getString("response");
                String rating = jsonArray.getJSONObject(0).getString("u_rating");
                tvUserRating.setText(rating + "/10");
                rbUserRating.setRating(Float.parseFloat(rating));
                if(response.equals(context.getString(R.string.response_success))){
                    initRecyclerView(jsonArray);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    llRecyclerView.setVisibility(View.GONE);
                    llPlaceholder.setVisibility(View.VISIBLE);
                }else if(response.equals(context.getString(R.string.unexpected))){
                    throw new Exception();
                }
            }else if(sqlHelper.getActionString().equals("get_user")){
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("user_data");
                String response = jsonObject.getString("response");
                if(response.equals(getString(R.string.response_success))) {
                    ModelHelper modelHelper = new ModelHelper(context);
                    UserModel userModel = modelHelper.buildUserModel(jsonObject);
                    Bundle rbundle = modelHelper.buildUserModelBundle(userModel, "ProfileFragment");
                    bundle.putBundle("r_bundle", rbundle);
                    dismiss();
                }

            }else if(sqlHelper.getActionString().equals("get_all_rating")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("ratings_data");
                String response = jsonArray.getJSONObject(0).getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    initRecyclerView(jsonArray);
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    llRecyclerView.setVisibility(View.GONE);
                    llPlaceholder.setVisibility(View.VISIBLE);
                }else if(response.equals(context.getString(R.string.unexpected))){
                    throw new Exception();
                }
            }else if(sqlHelper.getActionString().equals("update_rating")){
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                String watchedres = sqlHelper.getJSONResponse().getJSONObject("data").getString("iswatched");
                if(response.equals(context.getString(R.string.response_success))) {
                    Toast.makeText(context, "Your ratings have been saved", Toast.LENGTH_SHORT).show();
                    new ModelHelper(context).addToUpdatesModel(bundle.getString("id"), "", "rating");
                    isRated = true;
                    currentRating = Float.parseFloat(sqlHelper.getJSONResponse().getJSONObject("data").getString("avg_rating"));
                    currentRating =  StringHelper.roundFloat(currentRating, 1);
                    dismiss();
                }else if(response.equals(context.getString(R.string.response_unsuccessful)) || response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsDialog", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeScreenshot();
                }else{
                    Toast.makeText(context, context.getString(R.string.permission_external_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void toggleTab(int id){
        tvTabTop.setTextColor(id == R.id.textView_TabTop ? getResources().getColor(R.color.colorTextPrimary) : getResources().getColor(R.color.colorTextSecondary));
        tvTabAll.setTextColor(id == R.id.textView_TabAll ? getResources().getColor(R.color.colorTextPrimary) : getResources().getColor(R.color.colorTextSecondary));
        tvTabFollowing.setTextColor(id == R.id.textView_TabFollowing ? getResources().getColor(R.color.colorTextPrimary) : getResources().getColor(R.color.colorTextSecondary));
        tvTabTop.setTypeface(id == R.id.textView_TabTop ? tfSemibold : tfRegular);
        tvTabFollowing.setTypeface(id == R.id.textView_TabFollowing ? tfSemibold : tfRegular);
        tvTabAll.setTypeface(id == R.id.textView_TabAll ? tfSemibold : tfRegular);
    }

    private void fetchRatings(){
        SqlHelper sqlHelper = new SqlHelper(context, RatingsDialog.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setExecutePath("get-rating.php");
        sqlHelper.setActionString(type.equals("cast") ? "get_cast_rating" : "get_all_rating");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("u_id", bundle.containsKey("isIdentity") ? bundle.getString("u_id") : MainActivity.currentUserModel.getUserId());
        params.put("m_id", bundle.getString("id"));
        params.put("type", type);
        params.put("cast", bundle.getString("cast"));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    private void takeScreenshot(){
        if(new PermissionsHelper(context).requestPermissions(PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE)) {
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            try {
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

                // create bitmap screen capture
                View v1 = getDialog().getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                File imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 80;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                sendScreenshotFile(imageFile);
            } catch (Exception e) {
                EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsDialog: take screenshot", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                emailHelper.sendEmail();

            }
        }

    }

    private void sendScreenshotFile(File imageFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        String shareBody = "";
        switch (type){
            case "cast":{
                if(bundle.containsKey("isIdentity")){
                    shareBody = "Check out my friend's rating for " + bundle.getString("name");
                }else{
                    shareBody = "Check out my rating for " + bundle.getString("name");
                }
                break;
            }
            case "list":{
                shareBody = "Find out who has rated " + bundle.getString("name");
                break;
            }
        }
        shareBody = shareBody.concat("\n\nGet diving into the world of Cinema.\n\nInstall Cinema Club now.\n\n" + context.getString(R.string.app_store_uri));
        String shareSub = "Cinema Club Invitation";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile));
        startActivityForResult(Intent.createChooser(shareIntent, "Share using"), 0);
    }


    public void initRecyclerView(JSONArray jsonArray){
        try{
            if(type.equals("cast")){
                actorModels = new ArrayList<>();
                int length = jsonArray.length();
                ModelHelper modelHelper = new ModelHelper(context);
                for(int i = 1; i < length; i++){
                    ActorModel actorModel = modelHelper.buildActorModel(jsonArray.getJSONObject(i));
                    actorModels.add(actorModel);
                }
                attachAdapter(recyclerView, actorModels);
            }else if(type.equals("list")){
                ratingsModels = new ArrayList<>();
                ratingsModelsFollowing = new ArrayList<>();
                ratingsModelsTop = new ArrayList<>();
                ModelHelper modelHelper = new ModelHelper(context);
                int length = jsonArray.length();
                for(int i = 1; i < length; i++){
                    RatingsModel ratingsModel = modelHelper.buildRatingsModel(jsonArray.getJSONObject(i));
                    ratingsModels.add(ratingsModel);
                    addToSortedList(ratingsModel);
                    if(ratingsModel.getFollowing())
                        ratingsModelsFollowing.add(ratingsModel);
                }
                attachAdapter(recyclerView, ratingsModelsTop);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsDialog", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
            Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    public void addToSortedList(RatingsModel ratingsModel){

        int length = ratingsModelsTop.size();
        float rating = ratingsModel.getUserRating();
        for(int i = 0; i < length; i++){
            if(rating > ratingsModelsTop.get(i).getUserRating()){
                ratingsModelsTop.add(i, ratingsModel);
                return;
            }
        }
        ratingsModelsTop.add(ratingsModel);
    }

    public void attachAdapter(RecyclerView recyclerView, ArrayList<?> model){
        try {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            RatingsAdapter ratingsAdapter = new RatingsAdapter(context, model, recyclerView, type, !bundle.containsKey("isIdentity"), this);
            recyclerView.setAdapter(ratingsAdapter);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsDialog", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void submitRatings(){
        String castRating = "";
        for(int i = 0; i < actorModels.size(); i++){
            castRating = castRating.concat((i == 0 ? "" : "!~") + actorModels.get(i).getId() + "!@" + actorModels.get(i).getTempRating());
        }
        SqlHelper sqlHelper = new SqlHelper(context, RatingsDialog.this);
        sqlHelper.setExecutePath("update-rating.php");
        sqlHelper.setActionString("update_rating");
        sqlHelper.setMethod("GET");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("m_id", bundle.getString("id"));
        params.put("m_rating", "" + rbUserRating.getRating());
        params.put("u_id",MainActivity.currentUserModel.getUserId());
        params.put("is_watched", bundle.getString("is_watched"));
        params.put("cast_rating", castRating);
        params.put("type", type);
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    public void setCallbackDelegate(CallbackDelegate callbackDelegate){
        this.callbackDelegate = callbackDelegate;
    }

    public void setRated(boolean isRated){
        this.initiallyRated = isRated;
    }
}
