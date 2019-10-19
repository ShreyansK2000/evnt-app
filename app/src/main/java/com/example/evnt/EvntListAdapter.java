package com.example.evnt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//public class EvntListAdapter extends ArrayAdapter<EvntCardInfo> {
public class EvntListAdapter extends RecyclerView.Adapter<EvntListAdapter._EvntInfoViewHolder> {

    private Context context;
    private List<EvntCardInfo> evnt_list;

    public EvntListAdapter(Context context, List<EvntCardInfo> evnt_list) {
        this.context = context;
        this.evnt_list = evnt_list;
    }

    @NonNull
    @Override
    public _EvntInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.evnt_card_view_holder, parent, false);
        return new _EvntInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull _EvntInfoViewHolder holder, int position) {
        EvntCardInfo evntInfo = evnt_list.get(position);

        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());
        holder.host_name_tv.setText(evntInfo.getHost_name());
        holder.descript_tv.setText(evntInfo.getDescription());
        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());

        holder.event_img_iv.setImageDrawable(context.getDrawable(evntInfo.getImage()));
    }

    @Override
    public int getItemCount() {
        return evnt_list.size();
    }

    class _EvntInfoViewHolder extends RecyclerView.ViewHolder {

        TextView evnt_name_tv, host_name_tv, descript_tv;
        CircleImageView event_img_iv;


        public _EvntInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
        }
    }





}

