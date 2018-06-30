package com.create.sidhu.movbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //variables

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mRating = new ArrayList<>();
    private ArrayList<String> mTextRating = new ArrayList<>();
    private Context context;


    public RecyclerViewAdapter( Context context,ArrayList<String> mNames, ArrayList<String> mImage, ArrayList<String> mRating) {
        this.mNames = mNames;
        this.mImage = mImage;
        this.mRating = mRating;
        this.mTextRating = mTextRating;
        //this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watchlist,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(MainActivity.mainActivity)
                .asBitmap()
                .load(mImage.get(position))
                .into(holder.movie_img);


        holder.movie_name.setText(mNames.get(position));
        holder.movie_rating.setText(mRating.get(position));

        //define OnClick if required
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView movie_img;
        TextView movie_name;
        TextView movie_rating;
        //TextView rating_text;

        public ViewHolder(View itemView) {
            super(itemView);
            movie_img = itemView.findViewById(R.id.movie_poster);
            movie_name = itemView.findViewById(R.id.movie_name);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            //rating_text = itemView.findViewById(R.id.text_rating);
        }
    }
}
