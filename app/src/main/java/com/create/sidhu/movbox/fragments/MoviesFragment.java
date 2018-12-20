package com.create.sidhu.movbox.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.SettingsActivity;
import com.create.sidhu.movbox.adapters.PreferenceAdapter;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.helpers.TransparentProgressDialog;
import com.create.sidhu.movbox.models.FilterModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.PreferenceModel;
import com.create.sidhu.movbox.services.UserFeedJobService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements SqlDelegate{


    Fragment fragment;
    public MoviesFragment() {
        // Required empty public constructor
    }
    public static ArrayList<ArrayList<MovieModel>> masterMovieList;
    public static ArrayList<String> masterMovieTypeList;
    private ArrayList<MovieModel> movieModels;
    private static FilterModel filterModel;
    private Boolean bClick = false;
    private Context context;
    private LinearLayout llPlaceholder, llMaster, llScroller, llFilter, llFilterLanguge, llFilterGenre, llFilterWatched, llFilterRating;
    private LinearLayout llFilterLanguageSub, llFilterGenreSub, llFilterWatchedSub, llFilterRatingSub;
    private ScrollView scrollView;
    private RecyclerView rvLanguage, rvGenre;
    private TextView tvApplyFilter, tvRemoveFilter, tvRatingsMin, tvRatingsMax, tvFilterTitle;
    private RangeSeekBar sbRatings;
    private CheckBox cbWatchedYes, cbWatchedNo;
    ImageView imgDDArrowLanguage, imgDDArrowGenre, imgDDArrowWatched, imgDDArrowRating;
    View rootview;
    Bundle masterBundle;
    FloatingActionButton floatingActionButton, floatingActionButtonRemove;
    HashMap<String, Boolean> viewState;

    Typeface tfSemibold, tfRegular;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fab_homeFilter:{
                    llPlaceholder.setVisibility(View.GONE);
                    if(viewState.get("filter")){
                        applyFilters();
                    }else {
                        toggleEditVisibility("filter", !viewState.get("filter"));
                        viewState.put("filter", !viewState.get("filter"));
                    }
                    break;
                }
                case R.id.fab_homeFilterRemove:{
                    initializeFilter();
                    populateFragmentView(masterMovieList, masterMovieTypeList);
                    toggleEditVisibility("filter", !viewState.get("filter"));
                    viewState.put("filter", !viewState.get("filter"));
                    break;
                }
                case R.id.containerFilterLanguage:{
                    toggleEditVisibility("filter_language", !viewState.get("filter_language"));
                    viewState.put("filter_language", !viewState.get("filter_language"));
                    break;
                }
                case R.id.containerFilterGenre:{
                    toggleEditVisibility("filter_genre", !viewState.get("filter_genre"));
                    viewState.put("filter_genre", !viewState.get("filter_genre"));
                    break;
                }
                case R.id.containerFilterWatched:{
                    toggleEditVisibility("filter_watched", !viewState.get("filter_watched"));
                    viewState.put("filter_watched", !viewState.get("filter_watched"));
                    break;
                }
                case R.id.containerFilterRating:{
                    toggleEditVisibility("filter_rating", !viewState.get("filter_rating"));
                    viewState.put("filter_rating", !viewState.get("filter_rating"));
                    break;
                }
                case R.id.textView_ApplyFilter:{
                    applyFilters();
                    break;
                }
                case R.id.textView_RemoveFilter:{
                    initializeFilter();
                    populateFragmentView(masterMovieList, masterMovieTypeList);
                    toggleEditVisibility("filter", !viewState.get("filter"));
                    viewState.put("filter", !viewState.get("filter"));
                    break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = this;
        context = getActivity();
        masterBundle = new Bundle();
        rootview = inflater.inflate(R.layout.fragment_movies, container, false);
        try {
            tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
            tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
            llMaster = (LinearLayout) rootview.findViewById(R.id.containerMaster);
            llPlaceholder = (LinearLayout) rootview.findViewById(R.id.containerPlaceholder);
            llScroller = (LinearLayout) rootview.findViewById(R.id.containerScrollView);
            llFilter = (LinearLayout) rootview.findViewById(R.id.containerFilterMaster);
            llFilterLanguge = (LinearLayout) rootview.findViewById(R.id.containerFilterLanguage);
            llFilterGenre = (LinearLayout) rootview.findViewById(R.id.containerFilterGenre);
            llFilterWatched = (LinearLayout) rootview.findViewById(R.id.containerFilterWatched);
            llFilterRating = (LinearLayout) rootview.findViewById(R.id.containerFilterRating);
            llFilterLanguageSub = (LinearLayout) rootview.findViewById(R.id.containerFilterLanguageSub);
            llFilterGenreSub = (LinearLayout) rootview.findViewById(R.id.containerFilterGenreSub);
            llFilterWatchedSub = (LinearLayout) rootview.findViewById(R.id.containerFilterWatchedSub);
            llFilterRatingSub = (LinearLayout) rootview.findViewById(R.id.containerFilterRatingSub);

            floatingActionButton = (FloatingActionButton) rootview.findViewById(R.id.fab_homeFilter);
            floatingActionButtonRemove = (FloatingActionButton) rootview.findViewById(R.id.fab_homeFilterRemove);
            scrollView = (ScrollView) rootview.findViewById(R.id.scrollView);
            rvLanguage = (RecyclerView) rootview.findViewById(R.id.recyclerView_Languages);
            rvGenre = (RecyclerView) rootview.findViewById(R.id.recyclerView_Genres);
            tvApplyFilter = (TextView) rootview.findViewById(R.id.textView_ApplyFilter);
            tvRemoveFilter = (TextView) rootview.findViewById(R.id.textView_RemoveFilter);
            tvRatingsMax = (TextView) rootview.findViewById(R.id.textView_RangeMax);
            tvRatingsMin = (TextView) rootview.findViewById(R.id.textView_RangeMin);
            tvFilterTitle = (TextView) rootview.findViewById(R.id.textView_FilterTitle);
            cbWatchedYes = (CheckBox) rootview.findViewById(R.id.cb_WatchedYes);
            cbWatchedNo = (CheckBox) rootview.findViewById(R.id.cb_WatchedNo);
            sbRatings = (RangeSeekBar) rootview.findViewById(R.id.rangeseekbar_Ratings);
            imgDDArrowLanguage = (ImageView) rootview.findViewById(R.id.img_DropDownArrowLanguages);
            imgDDArrowGenre = (ImageView) rootview.findViewById(R.id.img_DropDownArrowGenre);
            imgDDArrowWatched = (ImageView) rootview.findViewById(R.id.img_DropDownArrowWatched);
            imgDDArrowRating = (ImageView) rootview.findViewById(R.id.img_DropDownArrowRatings);
            sbRatings.setRangeValues(0, 10);
            sbRatings.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                    int min = (int) minValue;
                    int max = (int) maxValue;
                    tvRatingsMin.setText("" + min);
                    tvRatingsMax.setText("" + max);
                }
            });
            tvFilterTitle.setTypeface(tfSemibold);

//        Button btnClick = rootview.findViewById(R.id.bclick_movies_new);
//        btnClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //bClick is used for switching between layout types
//                bClick = !bClick;
//                //initRecyclerView();                 //Expands recyclerView on click
//            }
//        });
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.GONE);
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_movies)));
            StringHelper.changeToolbarFont(toolbar, (MainActivity) context);
            if (masterMovieList == null && masterMovieTypeList == null) {
                fetchData();
            } else {
                populateFragmentView(masterMovieList, masterMovieTypeList);
            }
            if (filterModel == null)
                filterModel = new FilterModel();
            viewState = new HashMap<>();
            viewState.put("filter", false);
            viewState.put("filter_language", false);
            viewState.put("filter_genre", false);
            viewState.put("filter_watched", false);
            viewState.put("filter_rating", false);
            initializeFilter();
            floatingActionButton.setOnClickListener(onClickListener);
            floatingActionButtonRemove.setOnClickListener(onClickListener);
            llFilterLanguge.setOnClickListener(onClickListener);
            llFilterGenre.setOnClickListener(onClickListener);
            llFilterWatched.setOnClickListener(onClickListener);
            llFilterRating.setOnClickListener(onClickListener);
            tvRemoveFilter.setOnClickListener(onClickListener);
            tvApplyFilter.setOnClickListener(onClickListener);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return rootview;
    }
    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            switch (sqlHelper.getActionString()) {
                case "get_movies": {
                    JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("movie_data");
                    String response = jsonObject.getJSONObject("0").getString("response");
                    if (response.equals(context.getString(R.string.response_success))) {
                        addData(jsonObject);
                    } else if (response.equals(context.getString(R.string.response_unsuccessful))) {
                        Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    } else if (response.equals(context.getString(R.string.unexpected))) {
                        Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case "language":{
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("language_data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        ArrayList<PreferenceModel> languageModels = new ArrayList<>();
                        int length = jsonArray.length();
                        ModelHelper modelHelper = new ModelHelper(context);
                        for(int i = 1; i < length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            PreferenceModel preferenceModel = modelHelper.buildPreferenceModel(jsonObject, "language");
                            languageModels.add(preferenceModel);
                        }
                        filterModel.setLanguageList(languageModels);
                        attachAdapter(rvLanguage, "language");
                    }else if(response.equals(getString(R.string.response_unsuccessful))){

                    }
                    break;
                }
                case "genre":{
                    JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("genre_data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String response = jsonObject.getString("response");
                    if(response.equals(getString(R.string.response_success))){
                        ArrayList<PreferenceModel> genreModels = new ArrayList<>();
                        int length = jsonArray.length();
                        ModelHelper modelHelper = new ModelHelper(context);
                        for(int i = 1; i < length; i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            PreferenceModel preferenceModel = modelHelper.buildPreferenceModel(jsonObject, "genre");
                            genreModels.add(preferenceModel);
                        }
                        filterModel.setGenreList(genreModels);
                        attachAdapter(rvGenre, "genre");
                    }else if(response.equals(getString(R.string.response_unsuccessful))){

                    }
                    break;
                }
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchData() {
        SqlHelper sqlHelper = new SqlHelper(context, MoviesFragment.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setExecutePath("get-movies.php");
        sqlHelper.setActionString("get_movies");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    private void populateFragmentView(ArrayList<ArrayList<MovieModel>> masterList, ArrayList<String> masterTypeList){
        try {
            llMaster.removeAllViews();
            llMaster.setVisibility(View.VISIBLE);
            llPlaceholder.setVisibility(View.GONE);
            llFilter.setVisibility(View.GONE);
            int size = masterList.size();
            boolean shown = false;
            for (int i = 0; i < size; i++) {
                if (masterList.get(i).size() != 0) {
                    shown = true;
                    populateView(masterList.get(i), masterTypeList.get(i), i + 1);
                }
            }
            if (!shown) {
                llMaster.setVisibility(View.GONE);
                llPlaceholder.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void populateView(ArrayList<MovieModel> movieModels, final String type, int position){
        try {
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundResource((position % 2 == 0 ? R.color.colorTextDisabled : android.R.color.white));
            int paddingLeftRight = (int) context.getResources().getDimension(R.dimen.standard_gap_large);
            int paddingTopBottom = (int) context.getResources().getDimension(R.dimen.standard_gap_small);
            int paddingBottom = (int) context.getResources().getDimension(R.dimen.standard_touch_space_small);
            linearLayout.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingBottom);
            LinearLayout llInner = new LinearLayout(context);
            llInner.setLayoutParams(layoutParams);
            llInner.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParamsInnerText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsInnerText.weight = 1;
            LinearLayout.LayoutParams layoutParamsInnerImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsInnerImage.weight = 1;
            LinearLayout llInnerText = new LinearLayout(context);
            llInnerText.setOrientation(LinearLayout.HORIZONTAL);
            llInnerText.setLayoutParams(layoutParamsInnerText);
            LinearLayout llInnerImage = new LinearLayout(context);
            llInnerImage.setOrientation(LinearLayout.HORIZONTAL);
            llInnerImage.setLayoutParams(layoutParamsInnerImage);
            llInnerImage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.movie_grid_icon), (int) context.getResources().getDimension(R.dimen.movie_grid_icon));
            layoutParamsImage.setMargins(0, paddingTopBottom, 0, 0);
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(context.getDrawable(R.drawable.ic_grid));
            imageView.setLayoutParams(layoutParamsImage);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, FollowReviewActivity.class);
                    intent.putExtra("type", "movie:" + type);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            });
            LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleLayoutParams.setMargins((int) context.getResources().getDimension(R.dimen.standard_touch_space_small), paddingTopBottom, 0, paddingTopBottom);
            TextView textView = new TextView(context);
            textView.setLayoutParams(titleLayoutParams);
            textView.setText(type);
            textView.setTextColor(context.getResources().getColor(R.color.colorTextPrimary));
            textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf"));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            LinearLayout.LayoutParams layoutParamsRv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsRv.setMargins((int) context.getResources().getDimension(R.dimen.standard_touch_space_small), 0, 0, 0);
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutParams(layoutParamsRv);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels, rootview, "movie");
            recyclerView.setAdapter(adapter);
            llInnerText.addView(textView);
            llInnerImage.addView(imageView);
            llInner.addView(llInnerText);
            llInner.addView(llInnerImage);
            linearLayout.addView(llInner);
            linearLayout.addView(recyclerView);
            llMaster.addView(linearLayout);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void addData(JSONObject jsonObject) {
        try {
            int count = Integer.parseInt(jsonObject.getJSONObject("0").getString("count"));
            ModelHelper modelHelper = new ModelHelper(context);
            masterMovieList = new ArrayList<>();
            masterMovieTypeList = new ArrayList<>();
            for(int i = 1 ; i <= count ; i++){
                movieModels = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("" + i);
                int length = jsonArray.length();
                String type = jsonArray.getJSONObject(0).getString("type");
                for(int j = 1 ; j < length ; j++){
                    JSONObject tempObject = jsonArray.getJSONObject(j);
                    MovieModel movieModel = modelHelper.buildMovieModel(tempObject);
                    masterBundle.putBundle(type, modelHelper.buildMovieModelBundle(movieModel, "ProfileFragment"));
                    movieModels.add(movieModel);
                }
                masterMovieTypeList.add(type);
                masterMovieList.add(movieModels);
                populateView(movieModels, type, i);
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    public void OnClick(int position, Context context,View rootview, ArrayList<MovieModel> movieModels) {
        try {
            this.context = context;
            MainActivity mainActivity = (MainActivity) context;
            Bundle bundle = new ModelHelper(context).buildMovieModelBundle(movieModels.get(position), "ProfileFragment");
            ProfileFragment fragment2 = new ProfileFragment();
            mainActivity.initFragment(fragment2, bundle);
            //Toast.makeText(context, "Inside Movies", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }

    }


    private void toggleEditVisibility(String viewClass, boolean visible){
        switch (viewClass){
            case "filter":{
                if(visible){
                    llScroller.setVisibility(View.GONE);
                    llFilter.setVisibility(View.VISIBLE);
                    floatingActionButtonRemove.setVisibility(View.VISIBLE);
                }else{
                    llScroller.setVisibility(View.VISIBLE);
                    llFilter.setVisibility(View.GONE);
                    floatingActionButtonRemove.setVisibility(View.GONE);
                }
                break;
            }
            case "filter_language":{
                if(visible){
                    llFilterLanguageSub.setVisibility(View.VISIBLE);
                    imgDDArrowLanguage.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llFilterLanguageSub.setVisibility(View.GONE);
                    imgDDArrowLanguage.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
            case "filter_genre":{
                if(visible){
                    llFilterGenreSub.setVisibility(View.VISIBLE);
                    imgDDArrowGenre.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llFilterGenreSub.setVisibility(View.GONE);
                    imgDDArrowGenre.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
            case "filter_watched":{
                if(visible){
                    llFilterWatchedSub.setVisibility(View.VISIBLE);
                    imgDDArrowWatched.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llFilterWatchedSub.setVisibility(View.GONE);
                    imgDDArrowWatched.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
            case "filter_rating":{
                if(visible){
                    llFilterRatingSub.setVisibility(View.VISIBLE);
                    imgDDArrowRating.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                }else{
                    llFilterRatingSub.setVisibility(View.GONE);
                    imgDDArrowRating.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                }
                break;
            }
        }
    }

    private void applyFilters(){
        try {
            ArrayList<ArrayList<MovieModel>> masterFilterList = masterMovieList;
            masterFilterList = applyLanguageFilter(masterFilterList);
            masterFilterList = applyGenreFilter(masterFilterList);
            masterFilterList = applyWatchedFilter(masterFilterList);
            masterFilterList = applyRatingsFilter(masterFilterList);
            populateFragmentView(masterFilterList, masterMovieTypeList);
            toggleEditVisibility("filter", !viewState.get("filter"));
            viewState.put("filter", !viewState.get("filter"));
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private ArrayList<ArrayList<MovieModel>> applyLanguageFilter(ArrayList<ArrayList<MovieModel>> masterFilterList){
        try {
            ArrayList<PreferenceModel> languageList = filterModel.getLanguageList();
            if (languageList != null) {
                int sizeLanguage = languageList.size();
                boolean check = false;
                String languageString = "";
                for (int i = 0; i < sizeLanguage; i++) {
                    if (languageList.get(i).getChecked()) {
                        check = true;
                        languageString += languageList.get(i).getName();
                    }
                }
                if (check) {
                    ArrayList<ArrayList<MovieModel>> outerList = new ArrayList<>();
                    int sizeOuter = masterFilterList.size();
                    for (int i = 0; i < sizeOuter; i++) {
                        int sizeInner = masterFilterList.get(i).size();
                        ArrayList<MovieModel> innerList = new ArrayList<>();
                        for (int j = 0; j < sizeInner; j++) {
                            MovieModel movieModel = masterFilterList.get(i).get(j);
                            if (languageString.toLowerCase().contains(movieModel.getLanguage().toLowerCase())) {
                                innerList.add(movieModel);
                            }
                        }
                        outerList.add(innerList);
                    }
                    masterFilterList = outerList;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return masterFilterList;
    }

    private ArrayList<ArrayList<MovieModel>> applyGenreFilter(ArrayList<ArrayList<MovieModel>> masterFilterList){
        try {
            ArrayList<PreferenceModel> genreList = filterModel.getGenreList();
            if (genreList != null) {
                int sizeGenre = genreList.size();
                boolean check = false;
                String genreString = "";
                for (int i = 0; i < sizeGenre; i++) {
                    if (genreList.get(i).getChecked()) {
                        check = true;
                        genreString += genreList.get(i).getName();
                    }
                }
                if (check) {
                    ArrayList<ArrayList<MovieModel>> outerList = new ArrayList<>();
                    int sizeOuter = masterFilterList.size();
                    for (int i = 0; i < sizeOuter; i++) {
                        int sizeInner = masterFilterList.get(i).size();
                        ArrayList<MovieModel> innerList = new ArrayList<>();
                        for (int j = 0; j < sizeInner; j++) {
                            MovieModel movieModel = masterFilterList.get(i).get(j);
                            String movieGenre[] = movieModel.getGenre().split(",");
                            for (String genre:movieGenre
                                 ) {
                                if (genreString.toLowerCase().contains(genre.toLowerCase())) {
                                    innerList.add(movieModel);
                                }
                            }
                        }
                        outerList.add(innerList);
                    }
                    masterFilterList = outerList;
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return masterFilterList;
    }

    private ArrayList<ArrayList<MovieModel>> applyWatchedFilter(ArrayList<ArrayList<MovieModel>> masterFilterList){
        try {
            boolean check = false;
            boolean watchedYes = false;
            boolean watchedNo = false;
            if (cbWatchedYes.isChecked()) {
                watchedYes = true;
                check = true;
            }
            if (cbWatchedNo.isChecked()) {
                watchedNo = true;
                check = true;
            }
            if (check) {
                ArrayList<ArrayList<MovieModel>> outerList = new ArrayList<>();
                int sizeOuter = masterFilterList.size();
                for (int i = 0; i < sizeOuter; i++) {
                    int sizeInner = masterFilterList.get(i).size();
                    ArrayList<MovieModel> innerList = new ArrayList<>();
                    for (int j = 0; j < sizeInner; j++) {
                        MovieModel movieModel = masterFilterList.get(i).get(j);
                        if (watchedYes) {
                            if (movieModel.getIsWatched()) {
                                innerList.add(movieModel);
                            }
                        }
                        if (watchedNo) {
                            if (!movieModel.getIsWatched()) {
                                innerList.add(movieModel);
                            }
                        }
                    }
                    outerList.add(innerList);
                }
                masterFilterList = outerList;
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return masterFilterList;
    }

    private ArrayList<ArrayList<MovieModel>> applyRatingsFilter(ArrayList<ArrayList<MovieModel>> masterFilterList){
        try {
            boolean check = false;
            int min = (int) sbRatings.getSelectedMinValue();
            int max = (int) sbRatings.getAbsoluteMaxValue();
            if (min == 0 && max == 10)
                check = true;
            if (check) {
                ArrayList<ArrayList<MovieModel>> outerList = new ArrayList<>();
                int sizeOuter = masterFilterList.size();
                for (int i = 0; i < sizeOuter; i++) {
                    int sizeInner = masterFilterList.get(i).size();
                    ArrayList<MovieModel> innerList = new ArrayList<>();
                    for (int j = 0; j < sizeInner; j++) {
                        MovieModel movieModel = masterFilterList.get(i).get(j);
                        float rating = Float.parseFloat(movieModel.getRating());
                        if (rating >= min && rating <= max) {
                            innerList.add(movieModel);
                        }
                    }
                    outerList.add(innerList);
                }
                masterFilterList = outerList;
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return masterFilterList;
    }

    private void initializeFilter(){
        initializeLanguageFilter();
        initializeGenreFilter();
        initializeWatchedFilter();
        initializeRatingsFilter();
    }

    private void initializeLanguageFilter(){
        try {
            ArrayList<PreferenceModel> temp = filterModel.getLanguageList();
            if (temp == null) {
                fetchFilters("language");
            } else {
                int size = temp.size();
                for (int i = 0; i < size; i++) {
                    temp.get(i).setChecked(false);
                }
                filterModel.setLanguageList(temp);
                attachAdapter(rvLanguage, "language");
            }
            llFilterLanguageSub.setVisibility(View.GONE);
            viewState.put("filter_language", false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void initializeGenreFilter(){
        try {
            ArrayList<PreferenceModel> temp = filterModel.getGenreList();
            if (temp == null) {
                fetchFilters("genre");
            } else {
                int size = temp.size();
                for (int i = 0; i < size; i++) {
                    temp.get(i).setChecked(false);
                }
                filterModel.setGenreList(temp);
                attachAdapter(rvGenre, "genre");
            }
            llFilterGenreSub.setVisibility(View.GONE);
            viewState.put("filter_genre", false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void initializeWatchedFilter(){
        try {
            ArrayList<Boolean> temp = new ArrayList<>();
            temp.add(false);
            temp.add(false);
            filterModel.setWatched(temp);
            cbWatchedYes.setChecked(false);
            cbWatchedNo.setChecked(false);
            llFilterWatchedSub.setVisibility(View.GONE);
            viewState.put("filter_watched", false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void initializeRatingsFilter(){
        try {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(0);
            temp.add(10);
            filterModel.setRating(temp);
            sbRatings.setSelectedMaxValue(10);
            sbRatings.setSelectedMinValue(0);
            llFilterRatingSub.setVisibility(View.GONE);
            viewState.put("filter_rating", false);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private void fetchFilters(String type){
        switch (type){
            case "language":{
                SqlHelper sqlHelper = new SqlHelper(context, MoviesFragment.this);
                sqlHelper.setExecutePath("get-languages.php");
                sqlHelper.setActionString("language");
                sqlHelper.setParams(new ArrayList<NameValuePair>());
                sqlHelper.setMethod("GET");
                sqlHelper.executeUrl(false);
                break;
            }case "genre":{
                SqlHelper sqlHelper = new SqlHelper(context, MoviesFragment.this);
                sqlHelper.setExecutePath("get-genres.php");
                sqlHelper.setActionString("genre");
                sqlHelper.setParams(new ArrayList<NameValuePair>());
                sqlHelper.setMethod("GET");
                sqlHelper.executeUrl(false);
                break;
            }
        }
    }

    private void attachAdapter(RecyclerView recyclerView, String type){
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            if (type.equals("language") || type.equals("genre")) {
                PreferenceAdapter preferenceAdapter = new PreferenceAdapter(context, type.equals("language") ? filterModel.getLanguageList() : filterModel.getGenreList(), recyclerView, "filter");
                recyclerView.setAdapter(preferenceAdapter);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: MoviesFragment", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
