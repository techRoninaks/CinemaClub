package com.create.sidhu.movbox.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;


import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.create.sidhu.movbox.R;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostStatusFragment extends BottomSheetDialogFragment {
    RelativeLayout rlmasterPostStatus;
    LinearLayout llmasterTab;
    LinearLayout llWatchTab;
    LinearLayout llReviewTab;
    TextView textViewPlacholder;
    TextView textViewWatched;
    TextView textViewReviews;
    View v;
    public PostStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.post_status_dialog,container,false);
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        rlmasterPostStatus = (RelativeLayout) v.findViewById(R.id.containerPostStatus);
        llmasterTab = (LinearLayout) v.findViewById(R.id.containerReviews);
        llWatchTab = (LinearLayout) v.findViewById(R.id.containerTabWatched);
        llReviewTab = (LinearLayout) v.findViewById(R.id.containerTabReviews);
        textViewWatched = (TextView) v.findViewById(R.id.textView_TabWatched);
        textViewReviews = (TextView) v.findViewById(R.id.textView_TabReviews);
        textViewPlacholder = (TextView) v.findViewById(R.id.textView_Placeholder);

        if(type.equalsIgnoreCase("post_status")){
            llmasterTab.setVisibility(View.GONE);
            rlmasterPostStatus.setVisibility(View.VISIBLE);
        }else {
            rlmasterPostStatus.setVisibility(View.GONE);
            llmasterTab.setVisibility(View.VISIBLE);
            View.OnClickListener tabOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.containerTabWatched:
                            toggleTab(llWatchTab, textViewWatched, llReviewTab, textViewReviews);
                            textViewPlacholder.setText("Watched");
                            break;
                        case R.id.containerTabReviews:
                            toggleTab(llReviewTab, textViewReviews, llWatchTab, textViewWatched);
                            textViewPlacholder.setText("Reviews");
                            break;
                    }
                }
            };
            llWatchTab.setOnClickListener(tabOnClickListener);
            llReviewTab.setOnClickListener(tabOnClickListener);
            if(type.equalsIgnoreCase("review")){
                toggleTab(llReviewTab, textViewReviews, llWatchTab, textViewWatched);
                textViewPlacholder.setText("Reviews");
            }else if(type.equalsIgnoreCase("watched")){
                toggleTab(llWatchTab, textViewWatched, llReviewTab, textViewReviews);
                textViewPlacholder.setText("Watched");
            }
        }
        return v;
    }
    private void toggleTab(LinearLayout llFocus, TextView tvFocus, LinearLayout llDeFocus, TextView tvDeFocus){
        llDeFocus.setBackgroundResource(R.drawable.custom_tabview);
        tvDeFocus.setTypeface(Typeface.DEFAULT);
        llFocus.setBackgroundResource(R.drawable.custom_tabview_selected);
        tvFocus.setTypeface(Typeface.DEFAULT_BOLD);
    }

}
