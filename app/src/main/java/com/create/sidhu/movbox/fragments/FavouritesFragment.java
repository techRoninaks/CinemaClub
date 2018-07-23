package com.create.sidhu.movbox.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.adapters.FavouritesAdapter;
import com.create.sidhu.movbox.models.FavouritesModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

RecyclerView recyclerView;
Context context;
ArrayList<FavouritesModel> favouritesList;

    public FavouritesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View rootview = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_Favourites);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favouritesList= populateList(recyclerView);
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(context, favouritesList, recyclerView);
        recyclerView.setAdapter(favouritesAdapter);
        return rootview;
    }
    public ArrayList<FavouritesModel> populateList(RecyclerView recyclerView){
        ArrayList<FavouritesModel> favouritesList = new ArrayList<>();
        for(int i = 0; i<8 ; i++){
            FavouritesModel favouritesModel = new FavouritesModel();
            favouritesModel.setTitle("Testing title" + i);
            favouritesModel.setSubtitle("Testing subtitle" + i);
            if(i<2)
                favouritesModel.setDate("22/07/2018");
            else if(i<6)
                favouritesModel.setDate("21/07/2018");
            else
                favouritesModel.setDate("20/07/2018");
            favouritesList.add(favouritesModel);
        }
        return favouritesList;
    }

    public void OnClick(int position, Context context, View rootview, ArrayList<FavouritesModel> favouritesList){
        this.favouritesList = favouritesList;
        Toast.makeText(context, "Clicked: " + favouritesList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
    }
}
