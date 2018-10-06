package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.fragments.HomeFragment;
import com.create.sidhu.movbox.fragments.MoviesFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.MovieModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for Actor Recycler View
 */

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder>{
    private ArrayList<ActorModel> actorModels;
    private Context context;
    private View rootview;


    public ActorAdapter(Context context, ArrayList<ActorModel> actorModels, View rootview) {
        this.context = context;
        this.actorModels = actorModels;
        this.rootview = rootview;
    }


    @NonNull
    @Override
    public ActorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_individual_listitem,parent,false);
        return new ActorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.ViewHolder holder, final int position)  {
        Glide.with(context)
                .asBitmap()
                .load(actorModels.get(position).getImage())
                .into(holder.imgDefinitionImage);
        //holder.imgDefinitionImageType.setImageResource(actorModels.get(position).getType() == null || actorModels.get(position).getType().equalsIgnoreCase("director")? R.drawable.ic_director: R.drawable.ic_actor);
        holder.textViewDefinitionName.setText(actorModels.get(position).getName());
        holder.llCastMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.OnClick(position, context, rootview, actorModels, view, "cast");
            }
        });

    }


    @Override
    public int getItemCount() {
        return actorModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgDefinitionImage;
        CircleImageView imgDefinitionImageType;
        TextView textViewDefinitionName;
        LinearLayout llCastMaster;

        public ViewHolder(View itemView) {
            super(itemView);
            imgDefinitionImage = itemView.findViewById(R.id.indvDefinition_image);
            //imgDefinitionImageType = itemView.findViewById(R.id.indvDefinition_image_btn);
            textViewDefinitionName = itemView.findViewById(R.id.textView_indvDefinitionName);
            llCastMaster = itemView.findViewById(R.id.containerIndvCastMaster);
        }
    }
}
