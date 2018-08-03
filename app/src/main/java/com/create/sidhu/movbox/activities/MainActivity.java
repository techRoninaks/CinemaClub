package com.create.sidhu.movbox.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.PostStatusFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

   //public static MainActivity mainActivity;
    public String username = "Test User";        //TODO: Get user name

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            try {
                switch (item.getItemId()) {
                    //fragments are selected based on the item clicked
                    case R.id.navigation_home: //home fragment
                    {
                        HomeFragment fragment = new HomeFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_movies://movies fragment
                    {
                        MoviesFragment fragment = new MoviesFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_post_status: //post status fragment
                        PostStatusFragment bottomSheet = new PostStatusFragment();
                        bottomSheet.show(getSupportFragmentManager(), "BottomSheet");
                        return true;
                    case R.id.navigation_favourites: //favourites fragment
                    {
                        FavouritesFragment fragment = new FavouritesFragment();
                        initFragment(fragment);
                    }
                    return true;
                    case R.id.navigation_profile: //profile fragment
                    {
                        ProfileFragment fragment = new ProfileFragment(); //TODO: Get user info
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "user");
                        bundle.putBoolean("isIdentity", true);
                        bundle.putString("name", username);
                        bundle.putString("image", "https://hubbis.com/img/individual/cropped/65488531cd272c3357a3e0fabb4dfc3cde7181d4.jpg");
                        initFragment(fragment, bundle);
                    }
                    return true;
                }
            }catch (Exception e) {
                Log.e("Main:BottomSheet", e.getMessage());
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        try {
            BottomNavigationViewEx navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            // Navigation Bar Customization
            navigation.enableAnimation(false);
            navigation.enableShiftingMode(false);
            navigation.enableItemShiftingMode(false);
            navigation.setTextVisibility(false);

            // Load Home fragment on start
            if(bundle == null || bundle.isEmpty()){
                HomeFragment fragment1 = new HomeFragment();
                FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                fragmentTransaction1.replace(android.R.id.content, fragment1, "FragmentName");
                fragmentTransaction1.commit();
            }else{
                String returnPath = bundle.getString("return_path");
                switch (returnPath){
                    case "ProfileFragment":
                        ProfileFragment fragment = new ProfileFragment();
                        initFragment(fragment, bundle);
                        break;
                }
            }
        }catch(Exception e){
            Log.e("Main:onCreate",e.getMessage());
        }

    }

    private void initFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }
    private void initFragment(Fragment fragment, Bundle args){
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
