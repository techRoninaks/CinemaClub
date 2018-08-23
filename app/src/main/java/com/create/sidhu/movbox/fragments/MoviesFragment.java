package com.create.sidhu.movbox.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.models.MovieModel;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {


    Fragment fragment;
    public MoviesFragment() {
        // Required empty public constructor
    }
    private ArrayList<MovieModel> movieModels;
    private Boolean bClick = false;
    private Context context;
    View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = this;
        context = getActivity();
        movieModels = new ArrayList<>();
        rootview = inflater.inflate(R.layout.fragment_movies, container, false);
        Button btnClick = rootview.findViewById(R.id.bclick_movies_new);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bClick is used for switching between layout types
                bClick = !bClick;
                initRecyclerView();                 //Expands recyclerView on click
            }
        });
        addData();                                  //adding data into new releases
        return rootview;

    }


    private void addData() {
        MovieModel model = new MovieModel();
        model.setImage("https://www.topmovierankings.com/images/albums/photos/comrade-in-america-malayalam-movie-stills-poster-4503.jpg");
        model.setName("C.I.A");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://upload.wikimedia.org/wikipedia/ml/thumb/3/30/Parava_movie_poster.jpeg/220px-Parava_movie_poster.jpeg");
        model.setName("Parava");
        model.setRating("9");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2016/03/kali-poster-2.jpg");
        model.setName("Kali");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://malayalam.samayam.com/img/64118605/Master.jpg");
        model.setName("Naam");
        model.setRating("7");
        movieModels.add(model);

        model = new MovieModel();
        model.setImage("https://madaboutmoviez.files.wordpress.com/2015/12/charlie-poster-3.jpg");
        model.setName("Charlie");
        model.setRating("9");
        movieModels.add(model);

        Log.d(TAG, "Data initiated");
        initRecyclerView();
    }

    private void initRecyclerView(){
        if(!bClick) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            RecyclerView recyclerView = rootview.findViewById(R.id.recyclerView2);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels,rootview,"movie");
            recyclerView.setAdapter(adapter);
        }
        else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            RecyclerView recyclerView = rootview.findViewById((R.id.recyclerView2));
            recyclerView.setLayoutManager(gridLayoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels,rootview, "movie");
            recyclerView.setAdapter(adapter);
        }

    }

    //TODO: Onclick functionality
    public void OnClick(int position, Context context,View rootview, ArrayList<MovieModel> movieModels) {
        //Custom code
        this.context = context;
        MainActivity mainActivity = (MainActivity) context;
        Bundle bundle = new Bundle();
        bundle.putString("type",context.getString(R.string.profile_movies));
        bundle.putString("name", movieModels.get(position).getName());
        bundle.putString("image", movieModels.get(position).getImage());
        bundle.putBoolean("isIdentity", false);
        ProfileFragment fragment2 = new ProfileFragment();
//        fragment2.setArguments(bundle);
//        FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.RelLayout1, fragment2, "fragmentdetails");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//        fragmentTransaction.show(fragment2);
        mainActivity.initFragment(fragment2, bundle);
        Toast.makeText(context,"Inside Movies",Toast.LENGTH_SHORT).show();

    }
}
