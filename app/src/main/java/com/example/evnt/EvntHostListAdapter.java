package com.example.evnt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * ViewHolder model to essentially replicate the EventCardInfo layout over and over
 * with the correct information
 *
 * TODO figure out how to show google map snippet
 * TODO figure out how to show images from server calls (also in EvntCardInfo)
 */
public class EvntHostListAdapter extends RecyclerView.Adapter<EvntHostListAdapter._EvntInfoViewHolder> {

    private Context context;
    private List<EvntCardInfo> evnt_list;

    public EvntHostListAdapter(Context context, List<EvntCardInfo> evnt_list) {
        this.context = context;
        this.evnt_list = evnt_list;
    }

    @NonNull
    @Override
    public _EvntInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.evnt_card_host_view_holder, parent, false);
        return new _EvntInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull _EvntInfoViewHolder holder, int position) {
        EvntCardInfo evntInfo = evnt_list.get(position);

        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());
        holder.host_name_tv.setText(context.getResources().getString(R.string.you_the_host));
        holder.descript_tv.setText(evntInfo.getDescription());
        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());

        holder.event_img_iv.setImageDrawable(context.getDrawable(evntInfo.getImage()));

//        final Button editButton = holder.editButton;

//        editButton.setOnClickListener(HostingEventsFragment.editButtonClicked);


    }

    @Override
    public int getItemCount() {
        return evnt_list.size();
    }

    class _EvntInfoViewHolder extends RecyclerView.ViewHolder {

        TextView evnt_name_tv, host_name_tv, descript_tv;
        CircleImageView event_img_iv;

        Button editButton;


        public _EvntInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }


}


