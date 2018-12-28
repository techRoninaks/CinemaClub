package com.create.sidhu.movbox.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;


import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.ReviewsActivity;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.MovieModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostStatusFragment extends BottomSheetDialogFragment implements SqlDelegate {
    private final int THRESHOLD = 2;
    Typeface tfSemibold, tfRegular;
    Context context;
    Bundle bundle;
    String type;
    ArrayList<MovieModel> movieModelsSearch;
    static ArrayList<MovieModel> movieModelsMaster;

    LinearLayout llMasterPostStatus, llMaster, llPlaceholder;
    LinearLayout llWatched, llReview, llRating;
    Button btnWatched, btnReview, btnRating;
    RecyclerView recyclerView;
    ImageView imgSearch, imgClose;
    EditText etSearch;
    TextView tvSearchTitle;
    View v;
    public PostStatusFragment() {
        // Required empty public constructor
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.containerReview:
                case R.id.btn_Review:{
                    type = "review";
                    if(movieModelsMaster != null) {
                        llMasterPostStatus.setVisibility(View.GONE);
                        llMaster.setVisibility(View.VISIBLE);
                        if(movieModelsMaster.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                            initRecyclerView(recyclerView, movieModelsMaster);
                        }
                    }
                    break;
                }
                case R.id.containerRating:
                case R.id.btn_Rating:{
                    type = "rating";
                    if(movieModelsMaster != null) {
                        llMasterPostStatus.setVisibility(View.GONE);
                        llMaster.setVisibility(View.VISIBLE);
                        if(movieModelsMaster.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                            initRecyclerView(recyclerView, movieModelsMaster);
                        }
                    }
                    break;
                }
                case R.id.containerWatched:
                case R.id.btn_Watched:{
                    type = "watched";
                    if(movieModelsMaster != null) {
                        llMasterPostStatus.setVisibility(View.GONE);
                        llMaster.setVisibility(View.VISIBLE);
                        if(movieModelsMaster.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                            initRecyclerView(recyclerView, movieModelsMaster);
                        }
                    }
                    break;
                }
                case R.id.img_SearchImage:{
                    llMasterPostStatus.setVisibility(View.VISIBLE);
                    llMaster.setVisibility(View.GONE);
                    break;
                }
                case R.id.img_Close:{
                    dismiss();
                    break;
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.post_status_dialog,container,false);
        context = getActivity();
        bundle = getArguments();
        tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
        type = "";
        if(movieModelsMaster == null) {
            movieModelsMaster = new ArrayList<>();
            fetchMovies();
        }
        llMasterPostStatus = (LinearLayout) v.findViewById(R.id.containerPostStatus);
        llMaster = (LinearLayout) v.findViewById(R.id.containerReviews);
        llPlaceholder = (LinearLayout) v.findViewById(R.id.containerPlaceholder);
        llWatched = (LinearLayout) v.findViewById(R.id.containerWatching);
        llReview = (LinearLayout) v.findViewById(R.id.containerReview);
        llRating = (LinearLayout) v.findViewById(R.id.containerRating);
        btnWatched = (Button) v.findViewById(R.id.btn_Watched);
        btnRating = (Button) v.findViewById(R.id.btn_Rating);
        btnReview = (Button) v.findViewById(R.id.btn_Review);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        etSearch = (EditText) v.findViewById(R.id.editText_Search);
        imgSearch = (ImageView) v.findViewById(R.id.img_SearchImage);
        imgClose = (ImageView) v.findViewById(R.id.img_Close);
        tvSearchTitle = (TextView) v.findViewById(R.id.textView_SearchTitle);
        tvSearchTitle.setTypeface(tfSemibold);
        llWatched.setOnClickListener(onClickListener);
        llReview.setOnClickListener(onClickListener);
        llRating.setOnClickListener(onClickListener);
        btnReview.setOnClickListener(onClickListener);
        btnRating.setOnClickListener(onClickListener);
        btnWatched.setOnClickListener(onClickListener);
        imgSearch.setOnClickListener(onClickListener);
        imgClose.setOnClickListener(onClickListener);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(count > 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                }else{
                    tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                    initRecyclerView(recyclerView, movieModelsMaster);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                    initRecyclerView(recyclerView, movieModelsMaster);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= THRESHOLD){
                    fetchSearchResults(s.toString());
                }
                else if(s.length() == 0) {
                    tvSearchTitle.setText(context.getString(R.string.post_status_new_releases));
                    initRecyclerView(recyclerView, movieModelsMaster);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        return v;
    }
    private void toggleTab(LinearLayout llFocus, TextView tvFocus, LinearLayout llDeFocus, TextView tvDeFocus){
        llDeFocus.setBackgroundResource(R.drawable.custom_tabview);
        tvDeFocus.setTypeface(Typeface.DEFAULT);
        llFocus.setBackgroundResource(R.drawable.custom_tabview_selected);
        tvFocus.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private void fetchMovies(){
        SqlHelper sqlHelper = new SqlHelper(context, PostStatusFragment.this);
        sqlHelper.setExecutePath("fetch-movie.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("fetch_movies");
        ContentValues params = new ContentValues();
        params.put("c_id", MainActivity.currentUserModel.getUserId());
        params.put("m_id", "");
        params.put("group_type", "new_releases");
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    private void fetchSearchResults(String searchKey){
        SqlHelper sqlHelper = new SqlHelper(context, PostStatusFragment.this);
        sqlHelper.setExecutePath("search.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("search");
        ContentValues params = new ContentValues();
        params.put("u_id", MainActivity.currentUserModel.getUserId());
        params.put("srch_key", searchKey);
        params.put("mask", "user");
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(false);
    }

    private void populateData(JSONArray jsonArray, boolean populateMaster){
        int length = jsonArray.length();
        ModelHelper modelHelper = new ModelHelper(context);
        movieModelsSearch = new ArrayList<>();
        try {
            for (int i = 1; i < length; i++) {
                MovieModel movieModel = modelHelper.buildMovieModel(jsonArray.getJSONObject(i));
                movieModelsSearch.add(movieModel);
            }
            if(populateMaster)
                movieModelsMaster = movieModelsSearch;
            else
                initRecyclerView(recyclerView, movieModelsSearch);
        }catch (Exception e){
            Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: PostStatusFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void initRecyclerView(RecyclerView recyclerView, ArrayList<MovieModel> models){
        try {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);            // Calling the RecyclerView Adapter with a layout
            recyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, models, recyclerView, "post_status", type, PostStatusFragment.this);
            recyclerView.setAdapter(adapter);
            this.recyclerView.setVisibility(View.VISIBLE);
            llPlaceholder.setVisibility(View.GONE);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: PostStatusFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
    @Override
    public void onCancel(DialogInterface dialog){

    }

    @Override
    public void dismiss(){
        super.dismiss();
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            switch (sqlHelper.getActionString()) {
                case "fetch_movies": {
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("movie_data");
                    String response = jsonArray.getJSONObject(0).getString("response");
                    if(response.equals(context.getString(R.string.response_success))){
                        populateData(jsonArray, true);
                    }else if(response.equals(context.getString(R.string.response_unsuccessful)) || response.equals(context.getString(R.string.unexpected))){

                    }
                    break;
                }
                case "search":{
                    JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("search_data");
                    String response = jsonObject.getJSONObject("0").getString("response");
                    if(response.equals(context.getString(R.string.response_success))){
                        tvSearchTitle.setText(context.getString(R.string.post_status_search_title));
                        JSONArray jsonArray = jsonObject.getJSONArray("1");
                        populateData(jsonArray, false);
                    }else if(response.equals(context.getString(R.string.response_unsuccessful)) || response.equals(context.getString(R.string.unexpected))){
                        recyclerView.setVisibility(View.INVISIBLE);
                        llPlaceholder.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        }catch (Exception e){
            Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: PostStatusFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void OnClick(final int position, final Context context, View rootView, final ArrayList<MovieModel> movieModels, String type){
        this.context = context;
        this.type = type;
        MainActivity mainActivity = (MainActivity) context;
        try {
            switch (type) {
                case "review": {
                    Bundle bundle = new ModelHelper(context).buildReviewModelBundle(movieModels.get(position), "PostStatusFragment");
                    Intent intent = new Intent(context, ReviewsActivity.class);
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                    dismiss();
                    break;
                }
                case "rating": {
                    bundle = new ModelHelper(context).buildMovieModelBundle(movieModels.get(position), "ProfileFragment");
                    bundle.putString("type", "cast");
                    RatingsDialog ratingsDialog = new RatingsDialog();
                    mainActivity.initFragment(ratingsDialog, bundle);
                    dismiss();
                    break;
                }
                case "watched": {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    SqlHelper sqlHelper = new SqlHelper(context, PostStatusFragment.this);
                                    sqlHelper.setMethod("GET");
                                    sqlHelper.setActionString("update_watching");
                                    sqlHelper.setExecutePath("add-now-watching.php");
                                    ContentValues params = new ContentValues();
                                    params.put("c_id", MainActivity.currentUserModel.getUserId());
                                    params.put("m_id", movieModels.get(position).getId());
                                    params.put("is_watched", "" + movieModels.get(position).getIsWatched());
                                    sqlHelper.setParams(params);
                                    sqlHelper.executeUrl(false);
                                    Toast.makeText(context, "Movie has been marked as now watching", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    PostStatusFragment.this.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(StringHelper.toTitleCase(type));
                    alertDialog.setMessage("Do you wish to add this movie to now watching?");
                    alertDialog.setPositiveButton("Yes", dialogClickListener);
                    alertDialog.setNegativeButton("No", dialogClickListener);
                    alertDialog.show();
                    break;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: PostStatusFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
