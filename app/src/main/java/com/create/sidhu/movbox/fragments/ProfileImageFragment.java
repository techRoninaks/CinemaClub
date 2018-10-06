package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileImageFragment extends BottomSheetDialogFragment {
    View rootView;
    ImageView imageView;
    Button buttonExit;
    Context context;
    Bundle bundle;
    public ProfileImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_image_dialog,container,false);
        context = getActivity();
        bundle = getArguments();
        imageView = rootView.findViewById(R.id.imageView);
        buttonExit = rootView.findViewById(R.id.btn_exit);
        String image = bundle.getString("image");
        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(imageView);
        return rootView;
    }

}
