package com.create.sidhu.movbox.adapters;

/**
 * Adapter for Favourites list
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.models.FavouritesModel;
import com.create.sidhu.movbox.models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

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
            //TODO: Setting proper values
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
                            FavouritesFragment fragment = new FavouritesFragment();
                            fragment.OnClick(position, context, rootview, favouritesList, "general");
                        }
                    });
                    holder.time.setText(favouritesList.get(position).getTime());
                    holder.date.setText(favouritesList.get(position).getDate());
                    switch (favouritesList.get(position).getSubType()){
                        case "follow": {
                            holder.title.setText(context.getString(R.string.favourites_following));
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setVisibility(View.GONE);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setVisibility(View.GONE);
                            break;
                        }
                        case "watching":{
                            holder.title.setText(context.getString(R.string.favourites_watching));
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "rating":{
                            holder.title.setText(context.getString(R.string.favourites_rating));
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "review":{
                            holder.title.setText(context.getString(R.string.favourites_review));
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                            break;
                        }
                        case "review_vote":{
                            holder.title.setText(context.getString(R.string.favourites_vote));
                            holder.subject.setText(favouritesList.get(position).getUser().getName());
                            holder.object.setText(favouritesList.get(position).getMovie().getName());
                            Glide.with(context)
                                    .asBitmap()
                                    .load(favouritesList.get(position).getUser().getImage())
                                    .into(holder.list_img);
                            holder.subtitle.setText(favouritesList.get(position).getSubtitle());
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
                    return;
                case "followers":
                    holder.title.setText(favouritesList.get(position).getTitle());
                    holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                    holder.date.setText(favouritesList.get(position).getDate());
                    holder.time.setText(favouritesList.get(position).getTime());
                    return;
                case "following":
                    holder.title.setText(favouritesList.get(position).getTitle());
                    holder.subtitle.setText(favouritesList.get(position).getSubtitle());
                    holder.date.setText(favouritesList.get(position).getDate());
                    holder.time.setText(favouritesList.get(position).getTime());
                    return;
                case "user":
                    holder.title.setText(favouritesList.get(position).getUser().getName());
                    holder.subtitle.setText(favouritesList.get(position).getUser().getTotalWatched() + " Movies Watched");
                    Glide.with(context).asBitmap()
                            .load(favouritesList.get(position).getUser().getImage())
                            .into(holder.list_img);
                    holder.llDateTime.setVisibility(View.GONE);
                    holder.llButton.setVisibility(View.VISIBLE);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView list_img;
        TextView title;
        TextView subject;
        TextView object;
        TextView subtitle;
        TextView superTitle;
        TextView date;
        TextView time;
        LinearLayout llmasterLayout;
        LinearLayout llDateTime;
        LinearLayout llButton;
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
            date = itemView.findViewById(R.id.textView_Date);
            time = itemView.findViewById(R.id.textView_Time);
            llmasterLayout = itemView.findViewById(R.id.containerMaster);
            llDateTime = itemView.findViewById(R.id.containerDateTime);
            llButton = itemView.findViewById(R.id.containerButton);
            subject.setTypeface(tfSemibold);
            object.setTypeface(tfSemibold);
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
