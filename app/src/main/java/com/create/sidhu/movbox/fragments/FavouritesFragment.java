package com.create.sidhu.movbox.fragments;


import android.content.Context;
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
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.FavouritesModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements SqlDelegate {

    public static ArrayList<FavouritesModel> favouritesList;

    RecyclerView recyclerView;
    LinearLayout llContainerPlaceholder;
    Context context;
    String subTypes[] = {"follow", "watching", "review", "rating", "review_vote"};

    public FavouritesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
        toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_favourites)));
        StringHelper.changeToolbarFont(toolbar, (MainActivity)context);
        ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
        imgTitle.setVisibility(View.GONE);
        View rootview = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_Favourites);
        llContainerPlaceholder = rootview.findViewById(R.id.containerPlaceholder);
        if(favouritesList == null)
            fetchUpdates();
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(context, favouritesList, recyclerView);
            recyclerView.setAdapter(favouritesAdapter);
        }
        return rootview;
    }

    /***
     * Fetches the user updates from server
     */
    private void fetchUpdates(){
        SqlHelper sqlHelper = new SqlHelper(context, FavouritesFragment.this);
        sqlHelper.setExecutePath("get-updates.php");
        sqlHelper.setMethod("GET");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
        params.add(new BasicNameValuePair("seeker", "0"));
        params.add(new BasicNameValuePair("fragment", "favourites"));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }

    public void populateList(JSONObject jsonObject){
        favouritesList = new ArrayList<>();
        try{
            int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
            ModelHelper modelHelper = new ModelHelper(context);
            for(int i = 1; i < length ; i++){
                FavouritesModel favouritesModel = modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites");
                favouritesList.add(favouritesModel);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(context, favouritesList, recyclerView);
            recyclerView.setAdapter(favouritesAdapter);
        }catch (Exception e){
            Log.e("Favourites:Populate", e.getMessage());
        }
    }
    public void OnClick(int position, Context context, View rootview, ArrayList<FavouritesModel> favouritesList, String clickType){
        this.favouritesList = favouritesList;
        String subType = favouritesList.get(position).getSubType();
        if(subType.equals("follow") || subType.equals("review") || subType.equals("watching") || subType.equals("rating") || subType.equals("review_vote"))
        {
            switch (clickType){
                case "image":{
                    Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                    ProfileFragment fragment = new ProfileFragment();
                    ((MainActivity) context).initFragment(fragment, bundle);
                    break;
                }
                case "subject":{
                    Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                    ProfileFragment fragment = new ProfileFragment();
                    ((MainActivity) context).initFragment(fragment, bundle);
                    break;
                }
                case "object":{
                    Bundle bundle = new ModelHelper(context).buildMovieModelBundle(favouritesList.get(position).getMovie(), "ProfileFragment");
                    ProfileFragment fragment = new ProfileFragment();
                    ((MainActivity) context).initFragment(fragment, bundle);
                    break;
                }
                case "general":{
                    switch (favouritesList.get(position).getSubType()){
                        case "follow":{
                            Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                            ProfileFragment fragment = new ProfileFragment();
                            ((MainActivity) context).initFragment(fragment, bundle);
                            break;
                        }
                        case "watching":{
                            Bundle bundle = new ModelHelper(context).buildMovieModelBundle(favouritesList.get(position).getMovie(), "ProfileFragment");
                            ProfileFragment fragment = new ProfileFragment();
                            ((MainActivity) context).initFragment(fragment, bundle);
                            break;
                        }
                        case "review":
                        case "review_vote":{
                            break;
                        }
                        case "rating":{
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
            String response = jsonObject.getJSONObject("0").getString("response");
            if(response.equals(context.getString(R.string.response_success))){
                populateList(jsonObject);
            }else if(response.equals(context.getString(R.string.response_unsuccessful))){

            }else if(response.equals(context.getString(R.string.unexpected))){
                Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
