package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.FavouritesModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements SqlDelegate {

    private final int LOAD_INITIAL = 0;
    private final int LOAD_REFRESH = 1;
    private final int LOAD_HISTORY = 2;
    public static String seeker = "";
    public static ArrayList<FavouritesModel> favouritesList;
    private SwipeRefreshLayout swipeContainer; //Swipe down refresh

    RecyclerView recyclerView;
    LinearLayout llContainerPlaceholder;
    Context context;
    String subTypes[] = {"follow", "watching", "review", "rating", "review_vote"};
    long currentTime =0;

    public FavouritesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
       View rootview = inflater.inflate(R.layout.fragment_favourites, container, false);
        try {
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.toolbar);
            toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_favourites)));
            StringHelper.changeToolbarFont(toolbar, (MainActivity) context);
            swipeContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefresh);
            ImageView imgTitle = (ImageView) toolbar.findViewById(R.id.imgToolbarImage);
            imgTitle.setVisibility(View.GONE);
            recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_Favourites);
            llContainerPlaceholder = rootview.findViewById(R.id.containerPlaceholder);
            currentTime = System.currentTimeMillis();
            if (favouritesList == null){
                fetchUpdates("0",LOAD_INITIAL);
            }
            else {
                arrayCheck(LOAD_INITIAL);
                markRead(favouritesList);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: FavouritesFragment", e.getMessage() + "\n" + e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {
                fetchUpdates("0",LOAD_REFRESH);
            }

        }); // Configure the refreshing colors

        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorTextPrimary
        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(System.currentTimeMillis() - currentTime > 5000) {
                        if (seeker.equals(""))
                            Toast.makeText(context, "ExploreCinema Club more!!!", Toast.LENGTH_SHORT).show();
                        else
                            fetchUpdates(seeker, LOAD_HISTORY);
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        });
        return rootview;
    }

    private void markRead(ArrayList<FavouritesModel> favouritesList) {
        int length  = favouritesList.size();
        String markList="";
        for(int i= 0;i<length;i++){
            if(!favouritesList.get(i).getRead()){
                markList = markList + favouritesList.get(i).getId()+",";
            }
        }
        MainActivity mainActivity = (MainActivity) context;
        if(markList.equals(""))
            markList = null;
        if(!(markList==null)){
            if (markList.substring(markList.length() - 1).equals(","))
                markList.replace(markList.substring(markList.length() - 1), "1");
            markList = markList.substring(0, markList.length() - 1);
        }
        mainActivity.removeMarked(markList);
    }

    private void arrayCheck(int loadType) {
        if(favouritesList.size()==0){
                recyclerView.setVisibility(View.GONE);
                llContainerPlaceholder.setVisibility(View.VISIBLE);
                return;
        }
        if(loadType == LOAD_INITIAL || loadType == LOAD_REFRESH) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(context, favouritesList, recyclerView);
            recyclerView.setAdapter(favouritesAdapter);
        }else
            ((FavouritesAdapter)recyclerView.getAdapter()).updateList(favouritesList);

    }

    /***
     * Fetches the user updates from server
     */
    private void fetchUpdates(String seeker,int loadType){
        SqlHelper sqlHelper = new SqlHelper(context, FavouritesFragment.this);
        sqlHelper.setExecutePath("get-updates.php");
        sqlHelper.setMethod("GET");
        sqlHelper.setActionString("favourites:"+loadType);
        ContentValues params = new ContentValues();
        params.put("u_id", MainActivity.currentUserModel.getUserId());
        params.put("seeker", seeker);
        params.put("fragment", "favourites");
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(loadType == LOAD_INITIAL || loadType == LOAD_HISTORY);

    }

    public void populateList(JSONObject jsonObject, int loadType){
        if(loadType == LOAD_INITIAL || loadType == LOAD_REFRESH)
            favouritesList = new ArrayList<>();
        try{
            int length = Integer.parseInt(jsonObject.getJSONObject("0").getString("length"));
            ModelHelper modelHelper = new ModelHelper(context);
           for(int i = 1; i < length ; i++){
                FavouritesModel favouritesModel = modelHelper.buildFavouritesModel(jsonObject.getJSONObject("" + i), "favourites");
                favouritesList.add(favouritesModel);
            }
            if(loadType == LOAD_REFRESH)
                swipeContainer.setRefreshing(false);
            arrayCheck(loadType);
            markRead(favouritesList);
        }
        catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: FavouritesFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
    public void OnClick(int position, Context context, View rootview, ArrayList<FavouritesModel> favouritesList, String clickType){
        try {
            this.favouritesList = favouritesList;
            String subType = favouritesList.get(position).getSubType();
            if (subType.equals("follow") || subType.equals("review") || subType.equals("watching") || subType.equals("rating") || subType.equals("review_vote")) {
                switch (clickType) {
                    case "image": {
                        Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                        ProfileFragment fragment = new ProfileFragment();
                        ((MainActivity) context).initFragment(fragment, bundle);
                        break;
                    }
                    case "subject": {
                        Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                        ProfileFragment fragment = new ProfileFragment();
                        ((MainActivity) context).initFragment(fragment, bundle);
                        break;
                    }
                    case "object": {
                        Bundle bundle = new ModelHelper(context).buildMovieModelBundle(favouritesList.get(position).getMovie(), "ProfileFragment");
                        ProfileFragment fragment = new ProfileFragment();
                        ((MainActivity) context).initFragment(fragment, bundle);
                        break;
                    }
                    case "general": {
                        switch (favouritesList.get(position).getSubType()) {
                            case "follow": {
                                Bundle bundle = new ModelHelper(context).buildUserModelBundle(favouritesList.get(position).getUser(), "ProfileFragment");
                                ProfileFragment fragment = new ProfileFragment();
                                ((MainActivity) context).initFragment(fragment, bundle);
                                break;
                            }
                            case "watching": {
                                Bundle bundle = new ModelHelper(context).buildMovieModelBundle(favouritesList.get(position).getMovie(), "ProfileFragment");
                                ProfileFragment fragment = new ProfileFragment();
                                ((MainActivity) context).initFragment(fragment, bundle);
                                break;
                            }
                            case "review":
                            case "review_vote": {
                                break;
                            }
                            case "rating": {
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: FavouritesFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("data");
            String response = jsonObject.getJSONObject("0").getString("response");
            if(response.equals(context.getString(R.string.response_success))){
                seeker = jsonObject.getJSONObject("0").getString("new_seeker");
                populateList(jsonObject, Integer.parseInt(sqlHelper.getActionString().split(":")[1]));
            }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                recyclerView.setVisibility(View.GONE);
                llContainerPlaceholder.setVisibility(View.VISIBLE);
            }else if(response.equals(context.getString(R.string.unexpected))){
                Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: FavouritesFragment", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
