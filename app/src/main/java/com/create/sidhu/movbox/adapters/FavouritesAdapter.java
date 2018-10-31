package com.create.sidhu.movbox.adapters;

/**
 * Adapter for Favourites list
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.FollowReviewActivity;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.activities.ReviewsActivity;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.UserModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

import static com.create.sidhu.movbox.helpers.StringHelper.formatTextCount;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> implements SqlDelegate {

    //Global member variables

    private ArrayList<FavouritesModel> favouritesList = new ArrayList<>();
    private ArrayList<UserModel> usersList = new ArrayList<>();
    private Context context;
    private View rootview;
    private Date today;
    private Date currentDate;
    String dateStatus;

    public FavouritesAdapter(Context context, ArrayList<FavouritesModel> favouritesList, View rootview) {
        this.favouritesList = favouritesList;
        this.context = context;
        this.rootview = rootview;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(Calendar.getInstance().getTime());
            today = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentDate = today;
        dateStatus = new String();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_individual_listitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)  {
        LinearLayout llSuperTitle = (LinearLayout) holder.llmasterLayout.findViewById(R.id.containerSuperHeading);
        try {
            switch (favouritesList.get(position).getType()) {
                case "favourites":
                    currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(favouritesList.get(position).getDate());
                    int decision = today.compareTo(currentDate);
                    if (decision == 0) {
                        String day = context.getString(R.string.date_today);
                        holder.superTitle.setText(day);
                        if (!dateStatus.equals(day)) {
                            llSuperTitle.setVisibility(View.VISIBLE);
                            dateStatus = day;
                        }
                    } else {
                        if (currentDate.equals(yesterday())) {
                            String day = context.getString(R.string.date_yesterday);
                            holder.superTitle.setText(day);
                            if (!dateStatus.equals(day)) {
                                llSuperTitle.setVisibility(View.VISIBLE);
                                dateStatus = day;
                            }
                        } else {
                            String day = context.getString(R.string.date_earlier);
                            holder.superTitle.setText(day);
                            if (!dateStatus.equals(day)) {
                                llSuperTitle.setVisibility(View.VISIBLE);
                                dateStatus = day;
                            }
                        }
                    }
                    holder.llmasterLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(favouritesList.get(position).getSubType().equals("review") || favouritesList.get(position).getSubType().equals("review_vote")){
                                Bundle bundle = new ModelHelper(context).buildReviewModelBundle(favouritesList.get(position).getMovie(), "PostStatusFragment");
                                Intent intent = new Intent(context, ReviewsActivity.class);
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            }
                            FavouritesFragment fragment = new FavouritesFragment();
                            fragment.OnClick(position, context, rootview, favouritesList, "general");
                        }
                    });
                    holder.time.setText(favouritesList.get(position).getTime());
                    holder.date.setText(favouritesList.get(position).getDate());
                    Calendar calendar = Calendar.getInstance();
                    TimeZone timeZone = calendar.getTimeZone();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                    long time = format.parse(favouritesList.get(position).getDateTime()).getTime() + timeZone.getRawOffset();
                    long now = System.currentTimeMillis();
                    CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
                    switch (favouritesList.get(position).getSubType()){
                        case "follow": {
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(context.getString(R.string.favourites_following) + ". ");
                            holder.title.setText(stringBuilder);
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setVisibility(View.GONE);
                            holder.dateTitle.setText(ago);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setVisibility(View.GONE);
                            break;
                        }
                        case "watching":{
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(context.getString(R.string.favourites_watching));
                            holder.title.setText(stringBuilder);
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            holder.dateTitle.setText("." + ago);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "rating":{
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(context.getString(R.string.favourites_rating));
                            holder.title.setText(stringBuilder);
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            holder.dateTitle.setText("." + ago);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "review":{
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(context.getString(R.string.favourites_review));
                            holder.title.setText(stringBuilder);
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            holder.dateTitle.setText("." + ago);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "review_vote":{
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(context.getString(R.string.favourites_vote));
                            holder.title.setText(stringBuilder);
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            holder.dateTitle.setText("." + ago);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setVisibility(View.GONE);
                            break;
                        }
                    }
                    holder.subject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FavouritesFragment fragment = new FavouritesFragment();
                            fragment.OnClick(position, context, rootview, favouritesList, "subject");
                        }
                    });
                    holder.object.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FavouritesFragment fragment = new FavouritesFragment();
                            fragment.OnClick(position, context, rootview, favouritesList, "object");
                        }
                    });
                    holder.list_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FavouritesFragment fragment = new FavouritesFragment();
                            fragment.OnClick(position, context, rootview, favouritesList, "image");
                        }
                    });
                    return;
                case "review":
                    holder.title.setText(favouritesList.get(position).getTitle());
                    holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                    holder.date.setText(favouritesList.get(position).getDate());
                    holder.date.setTextColor(Color.GREEN);
                    holder.time.setText(favouritesList.get(position).getTime());
                    holder.time.setTextColor(Color.RED);
                    holder.dateTitle.setVisibility(View.GONE);
                    return;
                case "followers": {
                    holder.dateTitle.setVisibility(View.GONE);
                    holder.subject.setText(favouritesList.get(position).getTitle());
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    SpannableString spannableString = new SpannableString(StringHelper.formatTextCount(Integer.parseInt(favouritesList.get(position).getSubtitle())));
                    spannableString.setSpan(new CalligraphyTypefaceSpan(holder.tfSemibold), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.append(spannableString);
                    stringBuilder.append(" movies watched");
                    Glide.with(context)
                            .asBitmap()
                            .load(context.getString(R.string.master_url) + context.getString(R.string.profile_image_url) + favouritesList.get(position).getUserId() + ".jpg")
                            .into(holder.list_img);
                    holder.subtitle.setText(stringBuilder);
                    holder.date.setText(favouritesList.get(position).getDate());
                    holder.time.setText(favouritesList.get(position).getTime());
                    holder.title.setVisibility(View.GONE);
                    holder.object.setVisibility(View.GONE);
                    holder.llmasterLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FollowReviewActivity followReviewActivity = (FollowReviewActivity) context;
                            followReviewActivity.OnClick(position, context, rootview, favouritesList, "followers");
                        }
                    });
                    return;
                }
                case "following":{
                    holder.dateTitle.setVisibility(View.GONE);
                    holder.subject.setText(favouritesList.get(position).getTitle());
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    SpannableString spannableString = new SpannableString(StringHelper.formatTextCount(Integer.parseInt(favouritesList.get(position).getSubtitle())));
                    spannableString.setSpan(new CalligraphyTypefaceSpan(holder.tfSemibold), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.append(spannableString);
                    stringBuilder.append(" movies watched");
                    Glide.with(context)
                            .asBitmap()
                            .load(context.getString(R.string.master_url) + context.getString(R.string.profile_image_url) + favouritesList.get(position).getUserId() + ".jpg")
                            .into(holder.list_img);
                    holder.subtitle.setText(stringBuilder);
                    holder.date.setText(favouritesList.get(position).getDate());
                    holder.time.setText(favouritesList.get(position).getTime());
                    holder.title.setVisibility(View.GONE);
                    holder.object.setVisibility(View.GONE);
                    holder.llmasterLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FollowReviewActivity followReviewActivity = (FollowReviewActivity) context;
                            followReviewActivity.OnClick(position, context, rootview, favouritesList, "following");
                        }
                    });
                    return;
                }
                case "user":
                    holder.dateTitle.setVisibility(View.GONE);
                    holder.title.setVisibility(View.GONE);
                    holder.object.setVisibility(View.GONE);
                    holder.subject.setText(favouritesList.get(position).getUser().getName());
                    holder.subtitle.setText(favouritesList.get(position).getUser().getTotalWatched() + " Movies Watched");
                    Glide.with(context).asBitmap()
                            .load(favouritesList.get(position).getUser().getImage())
                            .into(holder.list_img);
                    holder.llDateTime.setVisibility(View.GONE);
                    if(!favouritesList.get(position).getUser().getUserId().equals(MainActivity.currentUserModel.getUserId()))
                        holder.llButton.setVisibility(View.VISIBLE);
                    else
                        holder.llButton.setVisibility(View.GONE);
                    holder.button.setText(favouritesList.get(position).getUser().getIsFollowing() ? "Unfollow" : "Follow");
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()){
                                case R.id.btn_Follow:
                                case R.id.containerButton:{
                                    SqlHelper sqlHelper = new SqlHelper(context, FavouritesAdapter.this);
                                    sqlHelper.setExecutePath("update-following.php");
                                    sqlHelper.setActionString("follow");
                                    sqlHelper.setMethod("GET");
                                    ArrayList<NameValuePair> params = new ArrayList<>();
                                    params.add(new BasicNameValuePair("c_id", MainActivity.currentUserModel.getUserId()));
                                    params.add(new BasicNameValuePair("u_id", favouritesList.get(position).getUser().getUserId()));
                                    params.add(new BasicNameValuePair("is_following", "" + favouritesList.get(position).getUser().getIsFollowing()));
                                    sqlHelper.setParams(params);
                                    HashMap<String, String> extras = new HashMap<>();
                                    extras.put("position", "" + position);
                                    extras.put("viewId", "" + v.getId());
                                    sqlHelper.setExtras(extras);
                                    sqlHelper.executeUrl(true);
                                    break;
                                }
                                case R.id.containerMaster:
                                case R.id.list_image:{
                                    if(context.getClass().getSimpleName().equals("MainActivity")){
                                        ((MainActivity) context).OnClick(position, context, rootview, favouritesList, "user");
                                    }else if(context.getClass().getSimpleName().equals("FollowReviewActivity")){
                                        ((FollowReviewActivity) context).OnClick(position, context, rootview, favouritesList, "user");
                                    }
                                    break;
                                }
                            }
                        }
                    };
                    holder.button.setOnClickListener(onClickListener);
                    holder.llButton.setOnClickListener(onClickListener);
                    holder.llmasterLayout.setOnClickListener(onClickListener);
                    holder.list_img.setOnClickListener(onClickListener);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Glide.with(context)
//                .asBitmap()
//                .load(favouritesList.get(position).getImageLocation())
//                .into(holder.list_img);
//        holder.superTitle.setText("Testing Super Title");
//        holder.title.setText(favouritesList.get(position).getTitle());
//        holder.subtitle.setText(favouritesList.get(position).getSubtitle());
//        holder.date.setText(favouritesList.get(position).getDate());
//        holder.time.setText(favouritesList.get(position).getTime());


    }


    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {
        try{
            switch (sqlHelper.getActionString()){
                case "follow":{
                    String response = sqlHelper.getJSONResponse().getJSONObject("data").getString("response");
                    int position = Integer.parseInt(sqlHelper.getExtras().get("position"));
                    if(response.equals(context.getString(R.string.response_success))){
                        if(favouritesList.get(position).getUser().getIsFollowing()){
                            MainActivity.currentUserModel.setFollowing(MainActivity.currentUserModel.getFollowing() - 1);
                            favouritesList.get(position).getUser().setIsFollowing(false);
                            favouritesList.get(position).getUser().setFollowers(favouritesList.get(position).getUser().getFollowers() - 1);
                        }else{
                            MainActivity.currentUserModel.setFollowing(MainActivity.currentUserModel.getFollowing() + 1);
                            favouritesList.get(position).getUser().setIsFollowing(true);
                            favouritesList.get(position).getUser().setFollowers(favouritesList.get(position).getUser().getFollowers() + 1);
                        }
                        notifyItemChanged(position);
                    }else if(response.equals(context.getString(R.string.response_unsuccessful))){
                        if(favouritesList.get(position).getUser().getIsFollowing())
                            Toast.makeText(context, "Could not remove from following. Please try later", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Could not follow. Please try later", Toast.LENGTH_SHORT).show();
                    }else if(response.equals(context.getString(R.string.unexpected))){
                        Toast.makeText(context, context.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }catch (Exception e){

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView list_img;
        TextView title;
        TextView subject;
        TextView object;
        TextView subtitle;
        TextView superTitle;
        TextView dateTitle;
        TextView date;
        TextView time;
        LinearLayout llmasterLayout;
        LinearLayout llDateTime;
        LinearLayout llButton;
        Button button;
        Typeface tfSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf");
        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/myriadpro.otf");
        public ViewHolder(View itemView) {
            super(itemView);
            list_img = itemView.findViewById(R.id.list_image);
            title = itemView.findViewById(R.id.textView_TitleText);
            subject = itemView.findViewById(R.id.textView_TitleSubject);
            object = itemView.findViewById(R.id.textView_TitleObject);
            subtitle = itemView.findViewById(R.id.textView_SubText);
            superTitle = itemView.findViewById(R.id.textView_SuperText);
            dateTitle = itemView.findViewById(R.id.textView_DateObject);
            date = itemView.findViewById(R.id.textView_Date);
            time = itemView.findViewById(R.id.textView_Time);
            llmasterLayout = itemView.findViewById(R.id.containerMaster);
            llDateTime = itemView.findViewById(R.id.containerDateTime);
            llButton = itemView.findViewById(R.id.containerButton);
            button = itemView.findViewById(R.id.btn_Follow);
            subject.setTypeface(tfSemibold);
            object.setTypeface(tfSemibold);
            superTitle.setTypeface(tfSemibold);
        }
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(cal.getTime());
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
