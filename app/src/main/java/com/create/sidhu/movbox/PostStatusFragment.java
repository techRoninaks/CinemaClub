package com.create.sidhu.movbox;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostStatusFragment extends BottomSheetDialogFragment {


    public PostStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.post_status_dialog,content,true);
        return v;


    }

}
