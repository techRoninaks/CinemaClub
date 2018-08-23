package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import com.create.sidhu.movbox.models.HomeModel;
import com.create.sidhu.movbox.models.MovieModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for Home Cards
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{
    private ArrayList<HomeModel> homeModels;
    private ArrayList<ActorModel> actorModels;
    private ActorAdapter actorAdapter;
    private Context context;
    private View rootview;


    public HomeAdapter(Context context, ArrayList<HomeModel> homeModels, View rootview) {
        this.context = context;
        this.homeModels = homeModels;
        this.rootview = rootview;
    }


    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_individual_listitem,parent,false);
        return new HomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, final int position)  {
        Glide.with(context)
                .asBitmap()
                .load(homeModels.get(position).getImage())
                .into(holder.imgMasterPoster);
        holder.textViewTitleName.setText(homeModels.get(position).getMovieName());
        holder.textViewRating.setText("" + homeModels.get(position).getRating());
        holder.textViewSubTextGenre.setText(homeModels.get(position).getGenre());
        holder.textViewSubTextDuration.setText("" + homeModels.get(position).getDuration());
        holder.textViewSubTextDimension.setText(homeModels.get(position).getDisplayDimension());
        Glide.with(context)
                .asBitmap()
                .load(homeModels.get(position).getDefinitionImage())
                .into(holder.imgDefinition);
        holder.textViewDefinitionTitle.setText(homeModels.get(position).getDefinitionTitle());
        holder.textViewDefinitionSubtitle.setText(homeModels.get(position).getDefinitionSubtitle());
        SpannableStringBuilder tempStr = new SpannableStringBuilder("" + homeModels.get(position).getTotalWatched());
        tempStr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, tempStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tempStr.append(" Watched");
        holder.textViewWatched.setText(tempStr);
        tempStr = new SpannableStringBuilder("" + homeModels.get(position).getTotalReviews());
        tempStr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, tempStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tempStr.append(" Reviews");
        holder.textViewReviews.setText(tempStr);
        populateActorView(holder.recyclerViewActors);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "";
                switch (view.getId()){
                    case R.id.containerMainText:
                    case R.id.containerImage:
                        type = "poster";
                        break;
                    case R.id.textView_TotalWatched:
                        type = "watched_get";
                        break;
                    case R.id.textView_TotalReviewed:
                    case R.id.textView_ViewReviews:
                        type = "review_get";
                        break;
                    case R.id.textView_WriteReview:
                    case R.id.containerWriteReview:
                        type = "review_set";
                        break;
                    case R.id.containerTypeImage:
                    case R.id.definition_image:
                        type = "definition_image";
                        break;
                }
                HomeFragment fragment = new HomeFragment();
                fragment.OnClick(position, context, rootview, homeModels, type);
            }
        };
        holder.llMovieMainText.setOnClickListener(onClickListener);
        holder.llMoviePoster.setOnClickListener(onClickListener);
        holder.llDefinitionImage.setOnClickListener(onClickListener);
        holder.llWriteReview.setOnClickListener(onClickListener);
        holder.textViewWriteReview.setOnClickListener(onClickListener);
        holder.textViewWatched.setOnClickListener(onClickListener);
        holder.textViewReviews.setOnClickListener(onClickListener);
        holder.textViewGetReviews.setOnClickListener(onClickListener);
        holder.llDefinitionImage.setOnClickListener(onClickListener);
        holder.imgDefinition.setOnClickListener(onClickListener);
    }


    @Override
    public int getItemCount() {
        return homeModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgMasterPoster;
        TextView textViewTitleName;
        TextView textViewRating;
        TextView textViewSubTextGenre;
        TextView textViewSubTextDuration;
        TextView textViewSubTextDimension;
        RecyclerView recyclerViewActors;
        CircleImageView imgDefinition;
        TextView textViewDefinitionTitle;
        TextView textViewDefinitionSubtitle;
        TextView textViewWatched;
        TextView textViewReviews;
        TextView textViewGetReviews;
        TextView textViewWriteReview;
        RelativeLayout parentLayout;
        LinearLayout llDefinitionImage;
        LinearLayout llMoviePoster;
        LinearLayout llMovieMainText;
        LinearLayout llWriteReview;

        public ViewHolder(View itemView) {
            super(itemView);
            imgMasterPoster = itemView.findViewById(R.id.imageViewMasterPoster);
            textViewTitleName = itemView.findViewById(R.id.textView_TitleText);
            textViewRating = itemView.findViewById(R.id.textView_TitleRatings);
            textViewSubTextGenre = itemView.findViewById(R.id.textView_SubTextGenre);
            textViewSubTextDuration = itemView.findViewById(R.id.textView_SubTextDuration);
            textViewSubTextDimension = itemView.findViewById(R.id.textView_SubTextDimension);
            recyclerViewActors = itemView.findViewById(R.id.recyclerView_Cast);
            imgDefinition = itemView.findViewById(R.id.definition_image);
            textViewDefinitionTitle = itemView.findViewById(R.id.textView_TitleTypeText);
            textViewDefinitionSubtitle = itemView.findViewById(R.id.textView_SubTypeText);
            textViewWatched = itemView.findViewById(R.id.textView_TotalWatched);
            textViewReviews = itemView.findViewById(R.id.textView_TotalReviewed);
            textViewGetReviews = itemView.findViewById(R.id.textView_ViewReviews);
            textViewWriteReview = itemView.findViewById(R.id.textView_WriteReview);
            llDefinitionImage = itemView.findViewById(R.id.containerTypeImage);
            llMoviePoster = itemView.findViewById(R.id.containerImage);
            llMovieMainText = itemView.findViewById(R.id.containerMainText);
            llWriteReview = itemView.findViewById(R.id.containerWriteReview);

        }
    }
    private void populateActorView(RecyclerView rootview){
        actorModels = new ArrayList<>();
        for(int i=0;i<8;i++){
            ActorModel actorModel = new ActorModel();
            actorModel.setName("Actor " + i);
            actorModel.setImage("https://hubbis.com/img/individual/cropped/65488531cd272c3357a3e0fabb4dfc3cde7181d4.jpg");
            actorModels.add(actorModel);
        }
        ActorAdapter actorAdapter = new ActorAdapter(context, actorModels, rootview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rootview.setLayoutManager(layoutManager);
        rootview.setAdapter(actorAdapter);
    }
}
