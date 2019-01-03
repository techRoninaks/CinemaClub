package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.PostStatusFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.MovieModel;

import java.util.ArrayList;

/**
 * Adapter for Movie Recycler View
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //variables

//    private ArrayList<String> mNames = new ArrayList<>();
//    private ArrayList<String> mImage = new ArrayList<>();
//    private ArrayList<String> mRating = new ArrayList<>();
    private ArrayList<String> mTextRating = new ArrayList<>();
    private ArrayList<MovieModel> movieModels;
    private Context context;
    private View rootview;
    private String type;
    private String callType;
    private PostStatusFragment ptFragment;


    public RecyclerViewAdapter(Context context, ArrayList<MovieModel> movieModels, View rootview, String type) {
//        this.mNames = mNames;
//        this.mImage = mImage;
//        this.mRating = mRating;
        this.context = context;
        this.movieModels = movieModels;
        this.rootview = rootview;
        this.type = type;
    }

    public RecyclerViewAdapter(Context context, ArrayList<MovieModel> movieModels, View rootview, String type, String callType, PostStatusFragment ptFragment) {
//        this.mNames = mNames;
//        this.mImage = mImage;
//        this.mRating = mRating;
        this.context = context;
        this.movieModels = movieModels;
        this.rootview = rootview;
        this.type = type;
        this.callType = callType;
        this.ptFragment = ptFragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watchlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)  {
        try {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.film_placeholder)
                            .error(R.drawable.film_placeholder)
                    )
                    .asBitmap()
                    .load(movieModels.get(position).getImage())
                    .into(holder.movie_img);
            holder.movie_name.setText(movieModels.get(position).getName());
            holder.movie_rating.setText(movieModels.get(position).getRating());
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase(context.getString(R.string.profile))) {
                        ProfileFragment fragment = new ProfileFragment();
                        fragment.OnClick(position, context, rootview, movieModels);
                    } else if (type.equalsIgnoreCase(context.getString(R.string.profile_movies))) {
                        MoviesFragment fragment = new MoviesFragment();
                        fragment.OnClick(position, context, rootview, movieModels);
                    } else if (type.equalsIgnoreCase(context.getString(R.string.watched))) {
                        FollowReviewActivity followReviewActivity = (FollowReviewActivity) context;
                        followReviewActivity.OnClick(position, context, rootview, movieModels, context.getString(R.string.watched));
                    } else if (type.equalsIgnoreCase(context.getString(R.string.post_status_type))) {
                        ptFragment.OnClick(position, context, rootview, movieModels, callType);
                    }
//                ProfileFragment fragment = new ProfileFragment();
//                fragment.OnClick(position, context, rootview, movieModels);
                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RecyclerViewAdapter", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView movie_img;
        TextView movie_name;
        TextView movie_rating;
        TextView text_rating;
        RelativeLayout parentLayout;
        Typeface tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
        public ViewHolder(View itemView) {
            super(itemView);
            movie_img = itemView.findViewById(R.id.movie_poster);
            movie_name = itemView.findViewById(R.id.movie_name);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            text_rating = itemView.findViewById(R.id.text_rating);
            parentLayout = itemView.findViewById(R.id.relativeMain);
            movie_name.setTypeface(tfSemibold);
            movie_rating.setTypeface(tfRegular);
            text_rating.setTypeface(tfRegular);
        }
    }
}
