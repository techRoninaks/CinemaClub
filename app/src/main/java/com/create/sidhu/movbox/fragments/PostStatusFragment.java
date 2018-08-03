package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.os.Bundle;


import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.create.sidhu.movbox.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostStatusFragment extends BottomSheetDialogFragment {


    public PostStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.post_status_dialog,container,false);
        return v;
    }

}
