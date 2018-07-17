package com.create.sidhu.movbox;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {


    public MoviesFragment() {
        // Required empty public constructor
    }


    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mRating = new ArrayList<>();
    private Boolean bClick = false;
    private Context context;
    View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context =getActivity();
        rootview = inflater.inflate(R.layout.fragment_movies, container, false);
        Button btnClick = rootview.findViewById(R.id.bclick_movies_new);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bClick = !bClick;
                initRecyclerView();
            }
        });
        addData();                                  //adding data into new releases






        return rootview;

    }


    private void addData(){
        mImage.add("https://www.topmovierankings.com/images/albums/photos/comrade-in-america-malayalam-movie-stills-poster-4503.jpg");             // Add Data here
        mNames.add("C.I.A");
        mRating.add("7");


        mImage.add("https://upload.wikimedia.org/wikipedia/ml/thumb/3/30/Parava_movie_poster.jpeg/220px-Parava_movie_poster.jpeg");
        mNames.add("Parava");
        mRating.add("9");

        mImage.add("https://madaboutmoviez.files.wordpress.com/2016/03/kali-poster-2.jpg");
        mNames.add("Kali");
        mRating.add("7");

        mImage.add("https://malayalam.samayam.com/img/64118605/Master.jpg");
        mNames.add("Naam");
        mRating.add("7");

        mImage.add("https://madaboutmoviez.files.wordpress.com/2015/12/charlie-poster-3.jpg");
        mNames.add("Charlie");
        mRating.add("9");

        Log.d(TAG, "Data initiated");
        initRecyclerView();
    }

    private void initRecyclerView(){
        if(!bClick) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            Log.d(TAG, "succes1");
            RecyclerView recyclerView = rootview.findViewById(R.id.recyclerView2);
            Log.d(TAG, "initRecyclerView: ");
            recyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, mNames, mImage, mRating,rootview);
            recyclerView.setAdapter(adapter);
        }
        else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            RecyclerView recyclerView = rootview.findViewById((R.id.recyclerView2));
            recyclerView.setLayoutManager(gridLayoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context,mNames,mImage,mRating,rootview);
            recyclerView.setAdapter(adapter);
        }

}}
