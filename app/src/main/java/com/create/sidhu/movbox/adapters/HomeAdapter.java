package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import com.create.sidhu.movbox.helpers.StringHelper;
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
    private ActorAdapter actorAdapter;
    private Context context;
    private View rootview;
    private String type;

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
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment fragment = new HomeFragment();
                fragment.OnClick(position, context, rootview, homeModels, view, "home");
            }
        };
        switch (homeModels.get(position).getFavourites().getSubType()){
            case "new_releases":{
                Glide.with(context)
                        .asBitmap()
                        .load(context.getDrawable(R.drawable.ic_reel_filled))
                        .into(holder.imgDefinition);
                holder.textViewDefinitionTitleSubject.setText(context.getString(R.string.home_new_releases));
                holder.textViewDefinitionTitle.setVisibility(View.GONE);
                holder.textViewDefinitionSubtitle.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewWatched.setText(StringHelper.formatTextCount(homeModels.get(position).getFavourites().getMovie().getTotalWatched()));
                holder.textViewRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.textViewTotalRating.setText("(" + homeModels.get(position).getFavourites().getMovie().getTotalRatings() + ")");
                holder.textViewReviews.setText("" + homeModels.get(position).getFavourites().getMovie().getTotalReviews());
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                populateActorView(holder.recyclerViewActors, homeModels.get(position).getCast());
                break;
            }
            case "recommendations":{
                holder.llDefinitionImage.setVisibility(View.GONE);
                holder.textViewDefinitionTitleSubject.setText(context.getString(R.string.home_recommendation));
                holder.textViewDefinitionTitle.setVisibility(View.GONE);
                holder.textViewDefinitionSubtitle.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewWatched.setText(StringHelper.formatTextCount(homeModels.get(position).getFavourites().getMovie().getTotalWatched()));
                holder.textViewRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.textViewTotalRating.setText("(" + homeModels.get(position).getFavourites().getMovie().getTotalRatings() + ")");
                holder.textViewReviews.setText("" + homeModels.get(position).getFavourites().getMovie().getTotalReviews());
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                populateActorView(holder.recyclerViewActors, homeModels.get(position).getCast());
                break;
            }
            case "review_watched":{
                break;
            }
            case "watching":{
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getUser().getImage())
                        .into(holder.imgDefinition);
                holder.textViewDefinitionTitleSubject.setText(homeModels.get(position).getFavourites().getUser().getName());
                holder.textViewDefinitionTitle.setText(context.getString(R.string.favourites_watching));
                holder.textViewDefinitionSubtitle.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewWatched.setText(StringHelper.formatTextCount(homeModels.get(position).getFavourites().getMovie().getTotalWatched()));
                holder.textViewRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.textViewTotalRating.setText("(" + homeModels.get(position).getFavourites().getMovie().getTotalRatings() + ")");
                holder.textViewReviews.setText("" + homeModels.get(position).getFavourites().getMovie().getTotalReviews());
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                populateActorView(holder.recyclerViewActors, homeModels.get(position).getCast());
                break;
            }
            case "review":{
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getUser().getImage())
                        .into(holder.imgDefinition);
                holder.textViewDefinitionTitleSubject.setText(homeModels.get(position).getFavourites().getUser().getName());
                holder.textViewDefinitionTitle.setText(context.getString(R.string.favourites_review));
                holder.textViewDefinitionSubtitle.setText(homeModels.get(position).getFavourites().getSubtitle());
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewWatched.setText(StringHelper.formatTextCount(homeModels.get(position).getFavourites().getMovie().getTotalWatched()));
                holder.textViewRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.textViewTotalRating.setText("(" + homeModels.get(position).getFavourites().getMovie().getTotalRatings() + ")");
                holder.textViewReviews.setText("" + homeModels.get(position).getFavourites().getMovie().getTotalReviews());
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                populateActorView(holder.recyclerViewActors, homeModels.get(position).getCast());
                break;
            }
            case "rating":{
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getUser().getImage())
                        .into(holder.imgDefinition);
                holder.textViewDefinitionTitleSubject.setText(homeModels.get(position).getFavourites().getUser().getName());
                holder.textViewDefinitionTitle.setText(context.getString(R.string.favourites_review));
                holder.textViewDefinitionSubtitle.setText(homeModels.get(position).getFavourites().getSubtitle());
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewWatched.setText(StringHelper.formatTextCount(homeModels.get(position).getFavourites().getMovie().getTotalWatched()));
                holder.textViewRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.textViewTotalRating.setText("(" + homeModels.get(position).getFavourites().getMovie().getTotalRatings() + ")");
                holder.textViewReviews.setText("" + homeModels.get(position).getFavourites().getMovie().getTotalReviews());
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                                context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                                context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                populateActorView(holder.recyclerViewActors, homeModels.get(position).getCast());
                break;
            }
            case "watchlist_reminder":{
                holder.imgDefinition.setImageDrawable(context.getDrawable(R.drawable.ic_eye_filled));
                holder.textViewDefinitionTitleSubject.setText(context.getString(R.string.home_watchlist_reminder));
                holder.textViewDefinitionTitle.setVisibility(View.GONE);
                holder.textViewDefinitionSubtitle.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewTitleRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.llContainerTitleRating.setVisibility(View.VISIBLE);
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                holder.llContainerDetails.setVisibility(View.GONE);
                holder.llContainerCast.setVisibility(View.GONE);
                break;
            }
            case "review_reminder":{
                holder.imgDefinition.setImageDrawable(context.getDrawable(R.drawable.ic_pencil_black));
                holder.textViewDefinitionTitleSubject.setText(context.getString(R.string.home_review_reminder));
                holder.textViewDefinitionTitle.setVisibility(View.GONE);
                holder.textViewDefinitionSubtitle.setVisibility(View.GONE);
                Glide.with(context)
                        .asBitmap()
                        .load(homeModels.get(position).getFavourites().getMovie().getImage().replace("portrait","landscape"))
                        .into(holder.imgMasterPoster);
                holder.textViewTitleName.setText(homeModels.get(position).getFavourites().getMovie().getName());
                holder.textViewSubTextGenre.setText(homeModels.get(position).getFavourites().getMovie().getGenre());
                holder.textViewSubTextDuration.setText(""+ homeModels.get(position).getFavourites().getMovie().getDuration() + "min");
                holder.textViewSubTextDimension.setText(homeModels.get(position).getFavourites().getMovie().getDisplayDimension());
                holder.textViewTitleRating.setText("" + homeModels.get(position).getFavourites().getMovie().getRating() + "/10");
                holder.llContainerTitleRating.setVisibility(View.VISIBLE);
                holder.imgWatched.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsWatched() ?
                        context.getDrawable(R.drawable.ic_eye_filled) : context.getDrawable(R.drawable.ic_eye));
                holder.imgRating.setImageDrawable(homeModels.get(position).getFavourites().getMovie().getIsRated() ?
                        context.getDrawable(R.drawable.ic_star_filled) : context.getDrawable(R.drawable.ic_star));
                holder.llContainerDetails.setVisibility(View.GONE);
                holder.llContainerCast.setVisibility(View.GONE);
                break;
            }
        }
        holder.textViewDefinitionTitleSubject.setOnClickListener(onClickListener);
        holder.textViewTitleName.setOnClickListener(onClickListener);
        holder.imgDefinition.setOnClickListener(onClickListener);
        holder.imgMasterPoster.setOnClickListener(onClickListener);
        holder.imgRating.setOnClickListener(onClickListener);
        holder.imgWatched.setOnClickListener(onClickListener);
        holder.imgReview.setOnClickListener(onClickListener);
        holder.llContainerDefinition.setOnClickListener(onClickListener);
        holder.llContainerWatched.setOnClickListener(onClickListener);
        holder.llContainerReview.setOnClickListener(onClickListener);
        holder.llContainerRating.setOnClickListener(onClickListener);
    }


    @Override
    public int getItemCount() {
        return homeModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgMasterPoster;
        ImageView imgWatched;
        ImageView imgRating;
        ImageView imgReview;
        TextView textViewTitleName;
        TextView textViewRating;
        TextView textViewTitleRating;
        TextView textViewTotalRating;
        TextView textViewSubTextGenre;
        TextView textViewSubTextDuration;
        TextView textViewSubTextDimension;
        RecyclerView recyclerViewActors;
        CircleImageView imgDefinition;
        TextView textViewDefinitionTitle;
        TextView textViewDefinitionTitleSubject;
        TextView textViewDefinitionSubtitle;
        TextView textViewWatched;
        TextView textViewReviews;
        RelativeLayout parentLayout;
        LinearLayout llDefinitionImage;
        LinearLayout llMoviePoster;
        LinearLayout llMovieMainText;
        LinearLayout llContainerTitleRating;
        LinearLayout llContainerCast;
        LinearLayout llContainerDetails;
        LinearLayout llContainerDefinition;
        LinearLayout llContainerWatched;
        LinearLayout llContainerRating;
        LinearLayout llContainerReview;
        Typeface tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
        public ViewHolder(View itemView) {
            super(itemView);
            imgMasterPoster = itemView.findViewById(R.id.imageViewMasterPoster);
            imgRating = itemView.findViewById(R.id.img_Rating);
            imgReview = itemView.findViewById(R.id.img_Review);
            imgWatched = itemView.findViewById(R.id.img_Watched);
            textViewTitleName = itemView.findViewById(R.id.textView_TitleText);
            textViewTitleRating = itemView.findViewById(R.id.textView_TitleRatings);
            textViewRating = itemView.findViewById(R.id.textView_Ratings);
            textViewTotalRating = itemView.findViewById(R.id.textView_TotalRatings);
            textViewSubTextGenre = itemView.findViewById(R.id.textView_SubTextGenre);
            textViewSubTextDuration = itemView.findViewById(R.id.textView_SubTextDuration);
            textViewSubTextDimension = itemView.findViewById(R.id.textView_SubTextDimension);
            recyclerViewActors = itemView.findViewById(R.id.recyclerView_Cast);
            imgDefinition = itemView.findViewById(R.id.definition_image);
            textViewDefinitionTitle = itemView.findViewById(R.id.textView_TitleTypeText);
            textViewDefinitionTitleSubject = itemView.findViewById(R.id.textView_TitleTypeSubject);
            textViewDefinitionSubtitle = itemView.findViewById(R.id.textView_SubTypeText);
            textViewWatched = itemView.findViewById(R.id.textView_TotalWatched);
            textViewReviews = itemView.findViewById(R.id.textView_TotalReviewed);
            llDefinitionImage = itemView.findViewById(R.id.containerTypeImage);
            llMoviePoster = itemView.findViewById(R.id.containerImage);
            llMovieMainText = itemView.findViewById(R.id.containerMainText);
            llContainerTitleRating = itemView.findViewById(R.id.containerCensorRating);
            llContainerCast = itemView.findViewById(R.id.containerCast);
            llContainerDetails = itemView.findViewById(R.id.containerDetails);
            llContainerDefinition = itemView.findViewById(R.id.containerTypeDefinition);
            llContainerRating = itemView.findViewById(R.id.containerRating);
            llContainerReview = itemView.findViewById(R.id.containerReviews);
            llContainerWatched = itemView.findViewById(R.id.containerWatched);
            textViewDefinitionTitleSubject.setTypeface(tfSemibold);
            textViewTitleName.setTypeface(tfSemibold);
            textViewWatched.setTypeface(tfSemibold);
            textViewReviews.setTypeface(tfSemibold);
            textViewRating.setTypeface(tfSemibold);
        }
    }
    private void populateActorView(RecyclerView rootview, ArrayList<ActorModel> actorModels){
//        actorModels = new ArrayList<>();
//        for(int i=0;i<8;i++){
//            ActorModel actorModel = new ActorModel();
//            actorModel.setName("Actor " + i);
//            actorModel.setImage("https://hubbis.com/img/individual/cropped/65488531cd272c3357a3e0fabb4dfc3cde7181d4.jpg");
//            actorModels.add(actorModel);
//        }
        ActorAdapter actorAdapter = new ActorAdapter(context, actorModels, rootview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rootview.setLayoutManager(layoutManager);
        rootview.setAdapter(actorAdapter);
    }
}
