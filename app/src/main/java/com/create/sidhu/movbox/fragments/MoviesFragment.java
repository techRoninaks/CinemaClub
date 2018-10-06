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
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.adapters.RecyclerViewAdapter;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.MovieModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements SqlDelegate{


    Fragment fragment;
    public MoviesFragment() {
        // Required empty public constructor
    }
    private ArrayList<MovieModel> movieModels;
    private Boolean bClick = false;
    private Context context;
    private LinearLayout llMaster;
    View rootview;
    Bundle masterBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = this;
        context = getActivity();
        movieModels = new ArrayList<>();
        masterBundle = new Bundle();
        rootview = inflater.inflate(R.layout.fragment_movies, container, false);
        llMaster = (LinearLayout) rootview.findViewById(R.id.containerMaster);
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
        toolbar.setTitle(StringHelper.toTitleCase(context.getString(R.string.title_movies)));
        StringHelper.changeToolbarFont(toolbar, (MainActivity)context);
        populateView();
        return rootview;
    }

    private void populateView() {
        SqlHelper sqlHelper = new SqlHelper(context, MoviesFragment.this);
        sqlHelper.setMethod("GET");
        sqlHelper.setExecutePath("get-movies.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);
    }


    private void addData(JSONObject jsonObject) {
        try {
            int count = Integer.parseInt(jsonObject.getJSONObject("0").getString("count"));
            ModelHelper modelHelper = new ModelHelper(context);
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
                LinearLayout linearLayout = new LinearLayout(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setBackgroundResource((i % 2 == 0 ? R.color.colorTextDisabled : android.R.color.white));
                int padding = (int)context.getResources().getDimension(R.dimen.standard_touch_space);
                linearLayout.setPadding(padding, padding, padding, padding);
                TextView textView = new TextView(context);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                textView.setText(type);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                RecyclerView recyclerView = new RecyclerView(context);
                recyclerView.setLayoutParams(layoutParams);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels,rootview,"movie");
                recyclerView.setAdapter(adapter);
                linearLayout.addView(textView);
                linearLayout.addView(recyclerView);
                llMaster.addView(linearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Data initiated");
        //initRecyclerView();
    }

//    private void initRecyclerView(){
//        if(!bClick) {
//            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//            RecyclerView recyclerView = rootview.findViewById(R.id.recyclerView2);
//            recyclerView.setLayoutManager(layoutManager);
//            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels,rootview,"movie");
//            recyclerView.setAdapter(adapter);
//        }
//        else{
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
//            RecyclerView recyclerView = rootview.findViewById((R.id.recyclerView2));
//            recyclerView.setLayoutManager(gridLayoutManager);
//            RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, movieModels,rootview, "movie");
//            recyclerView.setAdapter(adapter);
//        }
//
//    }

    public void OnClick(int position, Context context,View rootview, ArrayList<MovieModel> movieModels) {
        //Custom code
        this.context = context;
        MainActivity mainActivity = (MainActivity) context;
        Bundle bundle = new ModelHelper(context).buildMovieModelBundle(movieModels.get(position), "ProfileFragment");
        ProfileFragment fragment2 = new ProfileFragment();
        mainActivity.initFragment(fragment2, bundle);
        Toast.makeText(context,"Inside Movies",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try {
            JSONObject jsonObject = sqlHelper.getJSONResponse().getJSONObject("movie_data");
            String response = jsonObject.getJSONObject("0").getString("response");
            if(response.equals(context.getString(R.string.response_success))){
                addData(jsonObject);
            }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }else if(response.equals(context.getString(R.string.unexpected))){
                Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("MovFrag:onResponse", e.getMessage());
        }
    }
}
