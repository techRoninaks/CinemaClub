package com.create.sidhu.movbox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mRating = new ArrayList<>();
    private Boolean bClick = true;
    private Context context;
    View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout for this fragment
        context = getActivity();

        Log.d(TAG, "data added");
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        addData();


        Button button_img = rootview.findViewById(R.id.btn_profile_image);                          // onClickListener for Profile Image
        button_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProfileImage.class);
                startActivity(intent);
            }
        });

        TextView text_reviews = rootview.findViewById(R.id.profile_review_count);                   // onClickListener for Review count
        text_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProfileReviews.class);
                startActivity(intent);
            }
        });

        TextView text_followers = rootview.findViewById(R.id.profile_followers_count);              // onClickListener for Followers
        text_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProfileFollowers.class);
                startActivity(intent);
            }
        });

        TextView text_following = rootview.findViewById(R.id.profile_following_count);              // onClickListener for Followers
        text_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context,ProfileFollowing.class);                       //TODO : Add the layout for Intent (try by setting visibility)
//                startActivity(intent);
            }
        });

        Button settings = rootview.findViewById(R.id.btn_settings);                                 // Hide layout on condition.
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = rootview.findViewById(R.id.RelLayout1);
                relativeLayout.setVisibility(View.INVISIBLE);

            }
        });





        final Button btn_click = rootview.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bClick = !bClick;       //achieves the toggle functionality
                initRecyclerView();
            }
        });


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
        if(bClick) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);            // Calling the RecyclerView Adapter with a layout
            RecyclerView recyclerView = rootview.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, mNames, mImage, mRating);
            recyclerView.setAdapter(adapter);
        }
        else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            RecyclerView recyclerView = rootview.findViewById((R.id.recyclerView));
            recyclerView.setLayoutManager(gridLayoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context,mNames,mImage,mRating);
            recyclerView.setAdapter(adapter);
        }


    }


    private void hideAll(){

        rootview.findViewById(R.id.relative1);
        rootview.setVisibility(View.GONE);
    }

}
