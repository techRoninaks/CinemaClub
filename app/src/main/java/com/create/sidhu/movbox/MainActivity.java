package com.create.sidhu.movbox;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.ConditionProviderService;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

   //public static MainActivity mainActivity;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //home fragment

                    HomeFragment fragment1 = new HomeFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content,fragment1,"FragmentName");
                    fragmentTransaction1.commit();

                    return true;
                case R.id.navigation_movies:
                    //movies fragment

                    MoviesFragment fragment2 = new MoviesFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content,fragment2,"FragmentName");
                    fragmentTransaction2.commit();


                    return true;
                case R.id.navigation_post_status:
                    //post status fragment

                    PostStatusFragment fragment3 = new PostStatusFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content,fragment3,"FragmentName");
                    fragmentTransaction3.commit();


                    return true;
                case R.id.navigation_favourites:
                    //favourites fragment

                    FavouritesFragment fragment4 = new FavouritesFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.content,fragment4,"FragmentName");
                    fragmentTransaction4.commit();

                    return true;
                case R.id.navigation_profile:
                    //profile fragment
                    ProfileFragment fragment5 = new ProfileFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction5 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction5.replace(R.id.content,fragment5,"FragmentName");
                    fragmentTransaction5.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mainActivity = this;
        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Navigation Bar properties
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setTextVisibility(false);


        // Load Home fragment on start
        HomeFragment fragment1 = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(android.R.id.content,fragment1,"FragmentName");
        fragmentTransaction1.commit();

    }

}
