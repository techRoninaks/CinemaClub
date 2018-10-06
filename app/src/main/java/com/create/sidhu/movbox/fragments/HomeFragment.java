package com.create.sidhu.movbox.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.adapters.HomeAdapter;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UserModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SqlDelegate {

    View rootView;
    RecyclerView recyclerView;
    LinearLayout llContainerPlaceholder;
    HomeAdapter homeAdapter;
    ArrayList<HomeModel> homeModels;
    Context context;
    ProgressDialog pDialog;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        llContainerPlaceholder = rootView.findViewById(R.id.containerPlaceholder);
        Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
        toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.app_name)));
        StringHelper.changeToolbarFont(toolbar, (MainActivity)context);
        fetchUpdates();
        //initRecyclerView();
        return rootView;
    }

    /***
     * Fetches the user updates from server
     */
    private void fetchUpdates(){
        SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
        sqlHelper.setExecutePath("get-updates.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("home");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("seeker", "0"));
        params.add(new BasicNameValuePair("fragment", "home"));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    private void initRecyclerView(JSONObject jsonObject){
        homeModels = new ArrayList<>();
        try{
            int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
            ModelHelper modelHelper = new ModelHelper(context);
            for(int i = 1; i <= length ; i++){
                if(!jsonObject.getJSONObject("" + i).getString("type").equals("follow") && !jsonObject.getJSONObject("" + i).getString("type").equals("review_vote")) {
                    HomeModel homeModel = new HomeModel();
                    homeModel.setFavourites(modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites"));
                    homeModels.add(homeModel);
                }
            }
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            fetchActors(0);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            recyclerView.setLayoutManager(layoutManager);
//            homeAdapter = new HomeAdapter(context, homeModels, recyclerView);
//            recyclerView.setAdapter(homeAdapter);
        }catch (Exception e){
            Log.e("Home:InitRecyler", e.getMessage());
        }
    }

    private void fetchActors(int position){
        if(position < homeModels.size()) {
            SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("cast", homeModels.get(position).getFavourites().getMovie().getCast()));
            params.add(new BasicNameValuePair("m_id", homeModels.get(position).getFavourites().getMovie().getId()));
            sqlHelper.setExecutePath("get-cast.php");
            sqlHelper.setParams(params);
            sqlHelper.setActionString("cast:" + position);
            sqlHelper.setMethod("GET");
            sqlHelper.executeUrl(false);
        }else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            homeAdapter = new HomeAdapter(context, homeModels, rootView);
            recyclerView.setAdapter(homeAdapter);
            pDialog.dismiss();
        }
    }

    private void addCastData(JSONArray jsonArray, int position){
        int length = jsonArray.length();
        ArrayList<ActorModel> actorModels = new ArrayList<>();
        for(int i = 1; i < length; i++){
            ModelHelper modelHelper = new ModelHelper(context);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ActorModel actorModel = modelHelper.buildActorModel(jsonObject);
                actorModels.add(actorModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        homeModels.get(position).setCast(actorModels);
        fetchActors(position + 1);
    }

    public void OnClick(int position, Context context, View rootView, ArrayList<?> model, View view, String mainType){
        MainActivity mainActivity = (MainActivity) context;
        Fragment fragment;
        Bundle bundle;
        this.context = context;
        this.rootView = rootView;
        if(mainType.equals("home")){
            homeModels = (ArrayList<HomeModel>) model;
            HomeModel homeModel = (HomeModel) model.get(position);
           String subType = homeModel.getFavourites().getSubType();
           switch (view.getId()){
               case R.id.textView_TitleTypeSubject:
               case R.id.definition_image:{
                    switch (subType){
                        case "new_releases":{
                            bundle = new Bundle();
                            bundle.putString("type", "new_releases");
                            Intent intent = new Intent(context, FollowReviewActivity.class);
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                            break;
                        }
                        case "recommendations":{
                            bundle = new Bundle();
                            bundle.putString("type", "recommendations");
                            Intent intent = new Intent(context, FollowReviewActivity.class);
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                            break;
                        }
                        case "review_watched":{
                            bundle = new Bundle();
                            bundle.putString("type", "watching");
                            Intent intent = new Intent(context, FollowReviewActivity.class);
                            intent.putExtra("bundle", bundle);
                            startActivity(intent);
                            break;
                        }
                        case "watching":
                        case "review":
                        case "rating":{
                            bundle = new ModelHelper(context).buildUserModelBundle(homeModel.getFavourites().getUser(), "ProfileFragment");
                            fragment = new ProfileFragment();
                            mainActivity.initFragment(fragment, bundle);
                            break;
                        }
                        case "watchlist_reminder":{
                            bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                            fragment = new ProfileFragment();
                            mainActivity.initFragment(fragment, bundle);
                            break;
                        }
                        case "review_reminder":{
                            break;
                        }
                    }
                    break;
               }
               case R.id.containerTypeDefinition:{
                   switch (subType){
                       case "new_releases":{
                           bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                           fragment = new ProfileFragment();
                           mainActivity.initFragment(fragment, bundle);
                           break;
                       }
                       case "recommendations":{
                           bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                           fragment = new ProfileFragment();
                           mainActivity.initFragment(fragment, bundle);
                           break;
                       }
                       case "review_watched":{
                           bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                           fragment = new ProfileFragment();
                           mainActivity.initFragment(fragment, bundle);
                           break;
                       }
                       case "watching":{
                           break;
                       }
                       case "review":{
                           break;
                       }
                       case "rating":{
                           break;
                       }
                       case "watchlist_reminder":{
                           bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                           fragment = new ProfileFragment();
                           mainActivity.initFragment(fragment, bundle);
                           break;
                       }
                       case "review_reminder":{
                           bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                           fragment = new ProfileFragment();
                           mainActivity.initFragment(fragment, bundle);
                           break;
                       }
                   }
                   break;
               }
               case R.id.imageViewMasterPoster:
                case R.id.textView_TitleText:{
                    bundle = new ModelHelper(context).buildMovieModelBundle(homeModel.getFavourites().getMovie(), "ProfileFragment");
                    fragment = new ProfileFragment();
                    mainActivity.initFragment(fragment, bundle);
                    break;
                }
                case R.id.img_Watched:{
                    SqlHelper sqlHelper = new SqlHelper(context, HomeFragment.this);
                    sqlHelper.setExecutePath("update-watching.php");
                    sqlHelper.setActionString("watching:" + position);
                    HashMap<String,String> extras = new HashMap<>();
                    extras.put("view_id", "" + R.id.img_Watched);
                    sqlHelper.setMethod("GET");
                    ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("m_id", homeModel.getFavourites().getMovie().getId()));
                    params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                    params.add(new BasicNameValuePair("is_watched", "" + homeModel.getFavourites().getMovie().getIsWatched()));
                    sqlHelper.setParams(params);
                    sqlHelper.setExtras(extras);
                    sqlHelper.executeUrl(false);
                    break;
                }
                case R.id.img_Rating:{
                    break;
                }
                case R.id.img_Review:{
                    break;
                }
                case R.id.containerWatched:{
                    break;
                }
                case R.id.containerRating:{
                    break;
                }
                case R.id.containerReviews:{
                    break;
                }
           }
        }else if(mainType.equals("cast")){
            ActorModel actorModel = (ActorModel) model.get(position);
            bundle = new ModelHelper(context).buildActorModelBundle(actorModel, "ProfileFragment");
            fragment = new ProfileFragment();
            mainActivity.initFragment(fragment, bundle);
        }

    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            if(sqlHelper.getActionString().equals("home")) {
                JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
                String response = jsonObject.getJSONObject("0").getString("response");
                if (response.equals(context.getString(R.string.response_success))) {
                    initRecyclerView(jsonObject);
                } else if (response.equals(context.getString(R.string.response_unsuccessful))) {
                    recyclerView.setVisibility(View.GONE);
                    llContainerPlaceholder.setVisibility(View.VISIBLE);
                } else if (response.equals(context.getString(R.string.unexpected))) {
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().contains("watching")){
                recyclerView = rootView.findViewById(R.id.recyclerView);
                String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                int position = Integer.parseInt(sqlHelper.getActionString().split(":")[1]);
                if(response.equals(context.getString(R.string.response_success))){
                    if(homeModels.get(position).getFavourites().getMovie().getIsWatched()){
                        homeModels.get(position).getFavourites().getMovie().setWatched(false);
                        ImageView imageView = (ImageView) recyclerView.findViewById(Integer.parseInt(sqlHelper.getExtras().get("view_id")));
                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_eye));
                    }else{
                        homeModels.get(position).getFavourites().getMovie().setWatched(true);
                        ImageView imageView = (ImageView) recyclerView.findViewById(Integer.parseInt(sqlHelper.getExtras().get("view_id")));
                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_eye_filled));
                    }
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                    if(homeModels.get(position).getFavourites().getMovie().getIsWatched())
                        Toast.makeText(context, "Failed to remove from watching. Please try later", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed to add to watching. Please try later", Toast.LENGTH_SHORT).show();
                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }else if(sqlHelper.getActionString().contains("cast")){
                JSONArray jsonArray = sqlHelper.getJSONResponse().getJSONArray("cast_data");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String response = jsonObject.getString("response");
                if(response.equals(context.getString(R.string.response_success))){
                    addCastData(jsonArray,Integer.parseInt(sqlHelper.getActionString().split(":")[1]));
                }else if(response.equals(context.getString(R.string.response_unsuccessful))){

                }else if(response.equals(context.getString(R.string.unexpected))){
                    Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
