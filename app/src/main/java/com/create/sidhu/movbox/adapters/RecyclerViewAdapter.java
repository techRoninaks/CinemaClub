package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.fragments.ProfileFragment;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //variables

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mRating = new ArrayList<>();
    private ArrayList<String> mTextRating = new ArrayList<>();
    private Context context;
    private View rootview;


    public RecyclerViewAdapter(Context context, ArrayList<String> mNames, ArrayList<String> mImage, ArrayList<String> mRating,View rootview) {
        this.mNames = mNames;
        this.mImage = mImage;
        this.mRating = mRating;
        this.context = context;
        this.rootview = rootview;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watchlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)  {
        Glide.with(context)
                .asBitmap()
                .load(mImage.get(position))
                .into(holder.movie_img);
        holder.movie_name.setText(mNames.get(position));
        holder.movie_rating.setText(mRating.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = new ProfileFragment();
                fragment.OnClick(position,context,rootview);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView movie_img;
        TextView movie_name;
        TextView movie_rating;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            movie_img = itemView.findViewById(R.id.movie_poster);
            movie_name = itemView.findViewById(R.id.movie_name);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            parentLayout = itemView.findViewById(R.id.relativeMain);
        }
    }
}
