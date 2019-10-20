package com.example.evnt;

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

        String hostname = context.getResources().getString(R.string.by_browse_nuance) + " " + evntInfo.getHost_name();
        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());
        holder.host_name_tv.setText(hostname);
        holder.descript_tv.setText(evntInfo.getDescription());
        holder.evnt_name_tv.setText(evntInfo.getEvnt_name());

        holder.event_img_iv.setImageDrawable(context.getDrawable(evntInfo.getImage()));

        final Button toggleButton = holder.inButton;

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send api call to add this event to user events.
                if (toggleButton.getText().equals("I'M IN!")) {
                    // appropriate server api call
                    toggleButton.setText("NEVERMIND!");
                    Toast.makeText(context, "event added to your profile", Toast.LENGTH_LONG).show();
                } else {
                    // appropriate server api call
                    toggleButton.setText("I'M IN!");
                    Toast.makeText(context, "removed from your profile", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return evnt_list.size();
    }

    class _EvntInfoViewHolder extends RecyclerView.ViewHolder {

        TextView evnt_name_tv, host_name_tv, descript_tv;
        CircleImageView event_img_iv;

        Button inButton;


        public _EvntInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            inButton = itemView.findViewById(R.id.in_button);
        }
    }


}

