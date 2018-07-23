package com.create.sidhu.movbox.adapters;

/**
 * Created by nihalpradeep on 22/07/18.
 */

import android.content.Context;
import android.icu.util.DateInterval;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.fragments.FavouritesFragment;
import com.create.sidhu.movbox.fragments.ProfileFragment;
import com.create.sidhu.movbox.models.FavouritesModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    //Global member variables

    private ArrayList<FavouritesModel> favouritesList = new ArrayList<>();
    private Context context;
    private View rootview;
    private Date today;
    private Date currentDate;
    private Date previousDate;

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
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_individual_listitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)  {
        try {
            currentDate = new SimpleDateFormat("dd/MM/yyyy").parse(favouritesList.get(position).getDate());
            int decision = today.compareTo(currentDate);
            if(decision == 0)
                holder.superTitle.setText(R.string.date_today);
            else{
                if(currentDate.equals(yesterday()))
                    holder.superTitle.setText(R.string.date_yesterday);
                else
                    holder.superTitle.setText(R.string.date_earlier);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(position == 0 || !previousDate.equals(currentDate)){
            LinearLayout llSuperTitle = (LinearLayout) holder.masterLayout.findViewById(R.id.containerSuperHeading);
            llSuperTitle.setVisibility(View.VISIBLE);
            previousDate = currentDate;
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
        holder.masterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouritesFragment fragment = new FavouritesFragment();
                fragment.OnClick(position,context,rootview,favouritesList);
            }
        });

    }


    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView list_img;
        TextView title;
        TextView subtitle;
        TextView superTitle;
        TextView date;
        TextView time;
        LinearLayout masterLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            list_img = itemView.findViewById(R.id.list_image);
            title = itemView.findViewById(R.id.textView_TitleText);
            subtitle = itemView.findViewById(R.id.textView_SubText);
            superTitle = itemView.findViewById(R.id.textView_SuperText);
            date = itemView.findViewById(R.id.textView_Date);
            time = itemView.findViewById(R.id.textView_Time);
            masterLayout = itemView.findViewById(R.id.containerMaster);
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
