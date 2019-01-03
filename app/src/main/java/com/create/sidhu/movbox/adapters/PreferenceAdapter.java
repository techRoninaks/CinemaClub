package com.create.sidhu.movbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.activities.MainActivity;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.StringHelper;
import com.create.sidhu.movbox.models.PreferenceModel;

import java.util.ArrayList;

/**
 * Adapter for Preference Recycler View
 */

public class PreferenceAdapter extends RecyclerView.Adapter<PreferenceAdapter.ViewHolder>{
    private ArrayList<PreferenceModel> preferenceModels;
    private Context context;
    private View rootview;
    private String type;


    public PreferenceAdapter(Context context, ArrayList<PreferenceModel> preferenceModels, View rootview, String type) {
        this.context = context;
        this.preferenceModels = preferenceModels;
        this.rootview = rootview;
        this.type = type;
    }


    @NonNull
    @Override
    public PreferenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checklist_individual_listitem,parent,false);
        return new PreferenceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceAdapter.ViewHolder holder, final int position)  {
        try {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    preferenceModels.get(position).setChecked(isChecked);
                }
            });
            holder.checkBox.setText(preferenceModels.get(position).getName());
            if (type.equals("settings")) {
                String preference = preferenceModels.get(position).getType().equals("language") ?
                        MainActivity.currentUserModel.getLanguagePreference() : MainActivity.currentUserModel.getGenrePreference();
                if (preference.equals("NULL")) {
                    holder.checkBox.setChecked(false);
                    preferenceModels.get(position).setChecked(false);
                } else {
                    holder.checkBox.setChecked(preference.charAt(Integer.parseInt(preferenceModels.get(position).getId()) - 1) == '1');
                    preferenceModels.get(position).setChecked(preference.charAt(Integer.parseInt(preferenceModels.get(position).getId()) - 1) == '1');
                }
            } else if (type.equals("filter")) {
                holder.checkBox.setChecked(false);
                preferenceModels.get(position).setChecked(false);
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ActorAdapter", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }


    @Override
    public int getItemCount() {
        return preferenceModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_Preference);

        }
    }
}
