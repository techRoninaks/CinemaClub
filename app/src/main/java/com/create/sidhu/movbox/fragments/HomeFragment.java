package com.create.sidhu.movbox.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.HomeAdapter;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;
import com.create.sidhu.movbox.models.UserModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    ArrayList<HomeModel> homeModels;
    Context context;
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
        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView(){
        homeModels = new ArrayList<>();
        for(int i=0;i<5;i++){
            HomeModel homeModel = new HomeModel();
            UserModel userModel = new UserModel();
            MovieModel movieModel = new MovieModel();
            homeModel.setImage("https://cdn1.thr.com/sites/default/files/imagecache/landscape_928x523/2016/06/similar_posters-main-image-h_2016.jpg");
            homeModel.setMovieName("Title: " + i);
            homeModel.setRating(i);
            homeModel.setGenre("Comedy");
            homeModel.setDuration(140 + i);
            homeModel.setDisplayDimension("" + i + "D");
            homeModel.setDefinitionImage("https://hubbis.com/img/individual/cropped/65488531cd272c3357a3e0fabb4dfc3cde7181d4.jpg");
            homeModel.setDefinitionTitle("Definition Title " + i);
            homeModel.setDefinitionSubtitle("Definition Subtitle " + i);
            homeModel.setTotalWatched(103 + i);
            homeModel.setTotalReviews(60 + i);
            homeModels.add(homeModel);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        homeAdapter = new HomeAdapter(context, homeModels, recyclerView);
        recyclerView.setAdapter(homeAdapter);
    }

    public void OnClick(int position, Context context, View rootView, ArrayList<?> model, String type){
        MainActivity mainActivity = (MainActivity) context;
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (type){
            case "poster": {
                Toast.makeText(context, "Inside Poster", Toast.LENGTH_SHORT).show();
                ArrayList<HomeModel> homeModels = (ArrayList<HomeModel>) model;
                fragment = new ProfileFragment();
                bundle.putString("type", "movie");
                bundle.putBoolean("isIdentity", false);
                bundle.putString("name", homeModels.get(position).getMovieName());
                bundle.putString("image", homeModels.get(position).getImage());
                mainActivity.initFragment(fragment, bundle);
            }
                break;
            case "review_get": {
                PostStatusFragment bottomSheet = new PostStatusFragment();
                bundle.putString("type", "review");
                mainActivity.initFragment(bottomSheet, bundle);
                Toast.makeText(context, "Inside Review", Toast.LENGTH_SHORT).show();
            }
                break;
            case "watched_get": {
                PostStatusFragment bottomSheet = new PostStatusFragment();
                bundle.putString("type", "watched");
                mainActivity.initFragment(bottomSheet, bundle);
                Toast.makeText(context, "Inside Watch", Toast.LENGTH_SHORT).show();
            }
                break;
            case "review_set": {
                PostStatusFragment bottomSheet = new PostStatusFragment();
                bundle.putString("type", "review");
                mainActivity.initFragment(bottomSheet, bundle);
                Toast.makeText(context, "Inside Write Review", Toast.LENGTH_SHORT).show();
            }
                break;
            case "cast": {
                ArrayList<ActorModel> actorModels = (ArrayList<ActorModel>) model;
                fragment = new ProfileFragment();
                bundle.putString("type", "cast");
                bundle.putBoolean("isIdentity", false);
                bundle.putString("name", actorModels.get(position).getName());
                bundle.putString("image", actorModels.get(position).getImage());
                bundle.putString("gender", actorModels.get(position).getGender());
                bundle.putString("cast_type", actorModels.get(position).getType());
                bundle.putInt("rating", actorModels.get(position).getRating());
                mainActivity.initFragment(fragment, bundle);
                Toast.makeText(context, "Inside Cast", Toast.LENGTH_SHORT).show();
            }
                break;
            case "definition_image": {
                Toast.makeText(context, "Inside Definition Image", Toast.LENGTH_SHORT).show();
                ArrayList<HomeModel> homeModels = (ArrayList<HomeModel>) model;
                fragment = new ProfileFragment();
                bundle.putString("type", "user");
                bundle.putBoolean("isIdentity", false);
                bundle.putString("name", homeModels.get(position).getMovieName());
                bundle.putString("image", homeModels.get(position).getImage());
                fragment.setArguments(bundle);
                mainActivity.initFragment(fragment, bundle);
            }
                break;
        }
    }


}
