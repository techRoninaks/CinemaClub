package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.ReviewsActivity;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.ReviewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private ArrayList<ReviewModel> reviewModels;
    private Context context;
    private View rootview;
    private RequestOptions requestOptions;


    public ReviewAdapter(Context context, ArrayList<ReviewModel> reviewModels, View rootview) {
        this.context = context;
        this.reviewModels = reviewModels;
        this.rootview = rootview;
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_placeholder);
        requestOptions.error(R.drawable.ic_placeholder);
    }


    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_individual_listitem,parent,false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewAdapter.ViewHolder holder, final int position)  {
        try {
            final ReviewsActivity reviewsActivity = (ReviewsActivity) context;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.textView_ReplyView:{
                            if(holder.tvReplyView.getText().toString().contains("View")){
                                holder.llReplies.setVisibility(View.VISIBLE);
                                holder.tvReplyView.setText(context.getString(R.string.review_reply_hide));
                            }else{
                                holder.llReplies.setVisibility(View.GONE);
                                holder.tvReplyView.setText(context.getString(R.string.review_reply_view).replace("!#!",reviewModels.get(position).getReplies().substring(1)));
                            }
                            break;
                        }
                        case R.id.textView_UserName:
                        case R.id.img_User:{
                            String type = reviewModels.get(position).getType();
                            if(type.equals("movie") || type.equals("reply")) {
                                reviewsActivity.getUserDetails(reviewModels.get(position).getUserId());
                            }else if(type.equals("user")){
                                reviewsActivity.getMovieDetails(reviewModels.get(position).getMovieId());
                            }
                            break;
                        }
                        case R.id.textView_Reply:{
                            if(holder.llWriteReplies.getVisibility() == View.VISIBLE)
                                holder.llWriteReplies.setVisibility(View.GONE);
                            else
                                holder.llWriteReplies.setVisibility(View.VISIBLE);
                            break;
                        }
                        case R.id.textView_ReviewRemove:{
                            reviewsActivity.updateReview(reviewModels.get(position).getReviewId(), "", "remove", reviewModels.get(position).getReplies());
                            break;
                        }
                        case R.id.textView_ReviewEdit:{
                            holder.llReviewInfo.setVisibility(View.GONE);
                            holder.tvReviewText.setVisibility(View.GONE);
                            holder.btnSubmitReply.setVisibility(View.GONE);
                            holder.llWriteReplies.setVisibility(View.VISIBLE);
                            holder.btnReviewEdit.setVisibility(View.VISIBLE);
                            holder.btnReviewCancel.setVisibility(View.VISIBLE);
                            String reviewText = reviewModels.get(position).getReviewText();
                            if(reviewText.startsWith("*~"))
                                reviewText = reviewText.substring(reviewText.indexOf("~*") + 2);
                            holder.etReplyText.setText(reviewText);
                            break;
                        }
                        case R.id.btn_ReviewCancel:{
                            holder.etReplyText.setText("");
                            holder.llReviewInfo.setVisibility(View.VISIBLE);
                            holder.tvReviewText.setVisibility(View.VISIBLE);
                            holder.btnSubmitReply.setVisibility(View.VISIBLE);
                            holder.llWriteReplies.setVisibility(View.GONE);
                            holder.btnReviewEdit.setVisibility(View.GONE);
                            holder.btnReviewCancel.setVisibility(View.GONE);
                            break;
                        }
                        case R.id.btn_ReviewEdit:{
                            String reviewText = reviewModels.get(position).getReviewText();
                            String updatedText = holder.etReplyText.getText().toString();
                            if(reviewText.startsWith("*~")){
                                if(!reviewText.substring(reviewText.indexOf("~*") + 2).equals(updatedText)) {
                                    reviewsActivity.updateReview(reviewModels.get(position).getReviewId(), reviewText.substring(0, reviewText.indexOf("~*") + 2).concat(updatedText), "update", reviewModels.get(position).getReplies());
                                    reviewModels.get(position).setReviewText(reviewText.substring(0, reviewText.indexOf("~*") + 2).concat(updatedText));
                                    notifyItemChanged(position);
                                }
                            }else{
                                if(!reviewText.equals(updatedText)) {
                                    reviewsActivity.updateReview(reviewModels.get(position).getReviewId(), updatedText, "update", reviewModels.get(position).getReplies());
                                    reviewModels.get(position).setReviewText(updatedText);
                                    notifyItemChanged(position);
                                }
                            }
                            holder.etReplyText.setText("");
                            holder.llReviewInfo.setVisibility(View.VISIBLE);
                            holder.tvReviewText.setVisibility(View.VISIBLE);
                            holder.btnSubmitReply.setVisibility(View.VISIBLE);
                            holder.llWriteReplies.setVisibility(View.GONE);
                            holder.btnReviewEdit.setVisibility(View.GONE);
                            holder.btnReviewCancel.setVisibility(View.GONE);
                            break;
                        }
                        case R.id.img_LikeButton:{
                            reviewsActivity.updateReviewLike(position, reviewModels.get(position).getUserId(),
                                    reviewModels.get(position).getMovieId(),
                                    reviewModels.get(position).getReviewId(),
                                    reviewModels.get(position).getLiked());
                            reviewModels.get(position).setLiked(!reviewModels.get(position).getLiked());
                            holder.imgLike.setImageDrawable(reviewModels.get(position).getLiked() ? context.getDrawable(R.drawable.ic_heart_filled) : context.getDrawable(R.drawable.ic_heart));
                            break;
                        }
                        case R.id.btn_ReplySubmit:{
                            if(!holder.etReplyText.getText().toString().isEmpty()) {
                                String reply = reviewModels.get(position).getReplies();
                                String parentId = "";
                                String reviewText = "";
                                if (reply.startsWith("#") || reply.isEmpty() || reply == null || reply.equals("null")) {
                                    parentId = reviewModels.get(position).getReviewId();
                                    reviewText = holder.etReplyText.getText().toString();
                                }
                                else {
                                    parentId = reply;
                                    reviewText = "*~" + reviewModels.get(position).getUserName() + "~*" + holder.etReplyText.getText().toString();
                                }
                                reviewsActivity.submitReview("reply", parentId, reviewText);
                            }
                            break;
                        }
                    }
                    //reviewsActivity.OnClick(context, reviewModels, position, rootview, v);
                }
            };
            String imageUrl = reviewModels.get(position).getType().equals("movie") || reviewModels.get(position).getType().equals("reply")?
                    context.getString(R.string.master_url) + context.getString(R.string.profile_image_url) + reviewModels.get(position).getUserId() + ".jpg" :
                    context.getString(R.string.master_url) + context.getString(R.string.movie_image_portrait_url) + reviewModels.get(position).getMovieId() + ".jpg";
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .asBitmap()
                    .load(imageUrl)
                    .into(holder.imgUser);
            holder.tvReviewUserName.setText(reviewModels.get(position).getType().equals("movie") || reviewModels.get(position).getType().equals("reply")? reviewModels.get(position).getUserName() : reviewModels.get(position).getMovieName());
            if(reviewModels.get(position).getUserId().equals(MainActivity.currentUserModel.getUserId())){
                holder.tvReviewEdit.setVisibility(View.VISIBLE);
                holder.tvReviewRemove.setVisibility(View.VISIBLE);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String atReference = reviewModels.get(position).getReviewText().contains("*~") ?
                   "@" + reviewModels.get(position).getReviewText().substring(2, reviewModels.get(position).getReviewText().indexOf("~*"))
                    : "";
            String reviewText = reviewModels.get(position).getReviewText().contains("*~") ?
                    " " + reviewModels.get(position).getReviewText().substring(reviewModels.get(position).getReviewText().indexOf("~*") + 2) : reviewModels.get(position).getReviewText();
            SpannableString spannableString = new SpannableString(atReference);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorTextReply)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(spannableString);
            stringBuilder.append(reviewText);
            holder.tvReviewText.setText(stringBuilder);
            Calendar calendar = Calendar.getInstance();
            TimeZone timeZone = calendar.getTimeZone();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
            long time = format.parse(reviewModels.get(position).getTime()).getTime() + timeZone.getRawOffset();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.tvTime.setText(ago);
            holder.tvLike.setText(reviewModels.get(position).getLikes() + " Likes");
            if(reviewModels.get(position).getType().equals("user"))
                holder.imgLike.setVisibility(View.GONE);
            else
                holder.imgLike.setImageDrawable(reviewModels.get(position).getLiked() ? context.getDrawable(R.drawable.ic_heart_filled) : context.getDrawable(R.drawable.ic_heart));
            String replies = reviewModels.get(position).getReplies();
            if(replies.startsWith("#")){
                if(!reviewModels.get(position).getType().equals("user")) {
                    holder.tvReplyView.setVisibility(View.VISIBLE);
                    holder.tvReplyView.setText(context.getString(R.string.review_reply_view).replace("!#!", reviewModels.get(position).getReplies().substring(1)));
                }
                reviewsActivity.attachAdapter(holder.recyclerView, reviewModels.get(position).getRepliesList());
            }
            if(reviewModels.get(position).getUserPrivacy().charAt(0) == '0'){
                holder.tvReply.setVisibility(View.GONE);
            }
            holder.tvReplyView.setOnClickListener(onClickListener);
            holder.imgUser.setOnClickListener(onClickListener);
            holder.tvReply.setOnClickListener(onClickListener);
            holder.imgLike.setOnClickListener(onClickListener);
            holder.btnSubmitReply.setOnClickListener(onClickListener);
            holder.tvReviewUserName.setOnClickListener(onClickListener);
            holder.tvReviewEdit.setOnClickListener(onClickListener);
            holder.tvReviewRemove.setOnClickListener(onClickListener);
            holder.btnReviewCancel.setOnClickListener(onClickListener);
            holder.btnReviewEdit.setOnClickListener(onClickListener);
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ReviewAdapter", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }


    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgUser;
        TextView tvReviewText;
        TextView tvTime;
        TextView tvLike;
        TextView tvReply;
        TextView tvReplyView;
        TextView tvReviewUserName;
        TextView tvReviewEdit;
        TextView tvReviewRemove;
        EditText etReplyText;
        ImageView imgLike;
        LinearLayout llReplies;
        LinearLayout llWriteReplies;
        LinearLayout llReviewInfo;
        RecyclerView recyclerView;
        Button btnSubmitReply;
        Button btnReviewEdit;
        Button btnReviewCancel;
        Typeface tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");

        public ViewHolder(View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_User);
            tvReviewText = itemView.findViewById(R.id.textView_ReviewText);
            tvTime = itemView.findViewById(R.id.textView_TimeInfo);
            tvLike = itemView.findViewById(R.id.textView_Like);
            tvReply = itemView.findViewById(R.id.textView_Reply);
            tvReplyView = itemView.findViewById(R.id.textView_ReplyView);
            tvReviewEdit = itemView.findViewById(R.id.textView_ReviewEdit);
            tvReviewRemove = itemView.findViewById(R.id.textView_ReviewRemove);
            tvReviewUserName = itemView.findViewById(R.id.textView_UserName);
            etReplyText = itemView.findViewById(R.id.editText_ReviewTextReply);
            imgLike = itemView.findViewById(R.id.img_LikeButton);
            recyclerView = itemView.findViewById(R.id.recyclerViewReplies);
            llReplies = itemView.findViewById(R.id.containerReplies);
            llWriteReplies = itemView.findViewById(R.id.containerWriteReply);
            llReviewInfo = itemView.findViewById(R.id.containerReviewInfo);
            btnSubmitReply = itemView.findViewById(R.id.btn_ReplySubmit);
            btnReviewCancel = itemView.findViewById(R.id.btn_ReviewCancel);
            btnReviewEdit = itemView.findViewById(R.id.btn_ReviewEdit);
            tvLike.setTypeface(tfSemibold);
            tvReply.setTypeface(tfSemibold);
            tvReviewUserName.setTypeface(tfSemibold);

        }
    }
}
