package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ActorModel;
import com.create.sidhu.movbox.models.RatingsModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder>{
    private RequestOptions requestOptions;
    private ArrayList<ActorModel> actorModels;
    private ArrayList<RatingsModel> ratingsModels;
    private Context context;
    private View rootview;
    private String type;
    private boolean isIdentity;


    public RatingsAdapter(Context context, ArrayList<?> models, View rootview, String type, boolean isIdentity) {
        this.context = context;
        this.isIdentity = isIdentity;
        if(type.equals("cast"))
            this.actorModels = (ArrayList<ActorModel>) models;
        else if(type.equals("list"))
            this.ratingsModels = (ArrayList<RatingsModel>) models;
        this.rootview = rootview;
        this.type = type;
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_placeholder);
        requestOptions.error(R.drawable.ic_placeholder);
    }


    @NonNull
    @Override
    public RatingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ratings_individual_listitem_cast,parent,false);
        return new RatingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RatingsAdapter.ViewHolder holder, final int position)  {
        try {
            if(type.equals("cast")) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .asBitmap()
                        .load(actorModels.get(position).getImage())
                        .into(holder.imgCast);
                holder.imgCastType.setImageDrawable(actorModels.get(position).getType().equalsIgnoreCase("director") ?
                        context.getDrawable(R.drawable.ic_director) : context.getDrawable(R.drawable.ic_actor));
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(actorModels.get(position).getName());
                spannableStringBuilder.setSpan(new CalligraphyTypefaceSpan(holder.tfSemibold), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString spannableString = new SpannableString(" (" + actorModels.get(position).getRating() + "/10)");
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorTextSecondary)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(spannableString);
                holder.tvTitle.setText(spannableStringBuilder);
                holder.rbRating.setRating(actorModels.get(position).getRating());
                if(!isIdentity)
                    holder.rbRating.setIsIndicator(true);
                holder.rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        actorModels.get(position).setTempRating(rating);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(actorModels.get(position).getName());
                        spannableStringBuilder.setSpan(new CalligraphyTypefaceSpan(holder.tfSemibold), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString spannableString = new SpannableString(" (" + actorModels.get(position).getTempRating() + "/10)");
                        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorTextSecondary)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableString);
                        holder.tvTitle.setText(spannableStringBuilder);
                    }
                });
            }else if(type.equals("list")){
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .asBitmap()
                        .load(context.getString(R.string.master_url) + context.getString(R.string.profile_image_url) + ratingsModels.get(position).getUserId() + ".jpg")
                        .into(holder.imgCast);
                holder.imgCastType.setVisibility(View.GONE);
                holder.tvTitle.setText(ratingsModels.get(position).getUserName());
                holder.tvTitle.setTypeface(holder.tfSemibold);
                holder.rbRating.setVisibility(View.GONE);
                holder.tvRating.setText(ratingsModels.get(position).getUserRating() + "/10");
                holder.llContainerUserRating.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: RatingsAdapter", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }


    @Override
    public int getItemCount() {
        switch (type) {
            case "cast":
                return actorModels.size();
            case "list":
                return ratingsModels.size();
            default:
                return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgCast;
        CircleImageView imgCastType;
        TextView tvTitle;
        RatingBar rbRating;
        TextView tvRating;
        LinearLayout llContainerUserRating;
        Typeface tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");

        public ViewHolder(View itemView) {
            super(itemView);
            imgCast = itemView.findViewById(R.id.img_Cast);
            imgCastType = itemView.findViewById(R.id.img_CastType);
            tvTitle = itemView.findViewById(R.id.textView_CastTitle);
            rbRating = itemView.findViewById(R.id.rb_CastRating);
            llContainerUserRating = itemView.findViewById(R.id.containerUserRatings);
            tvRating = itemView.findViewById(R.id.textView_UserRating);
        }
    }
}
