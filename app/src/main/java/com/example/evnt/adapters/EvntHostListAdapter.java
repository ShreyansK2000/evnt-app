package com.example.evnt.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.evnt.EvntCardInfo;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import java.util.Set;


/**
 * ViewHolder model to essentially replicate the EventCardInfo layout over and over
 * with the correct information
 *
 * TODO figure out how to show google map snippet
 * TODO figure out how to show images from server calls (also in EvntCardInfo)
 */
public class EvntHostListAdapter extends RecyclerView.Adapter<EvntHostListAdapter.EvntInfoViewHolder> {

    private Context context;
    private List<EvntCardInfo> evnt_list;
    private RequestQueue requestQueue;
    private IdentProvider ident;
    private Set<Integer> drawn;

    public EvntHostListAdapter(Context context, List<EvntCardInfo> evnt_list) {
        this.context = context;
        this.evnt_list = evnt_list;
        this.ident = new IdentProvider(context);
        this.requestQueue = Volley.newRequestQueue(context);
        this.drawn = new HashSet<>();
    }

    @NonNull
    @Override
    public EvntInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.evnt_card_host_view_holder, parent, false);
        return new EvntInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvntInfoViewHolder holder, int position) {
        EvntCardInfo evntInfo = evnt_list.get(position);

        holder.id = evntInfo.getId();
        holder.evnt_name_tv.setText(evntInfo.getEvntName());
        holder.host_name_tv.setText(evntInfo.getHostName());
        holder.descript_tv.setText(evntInfo.getDescription());
        holder.date_tv.setText(evntInfo.getDateString());
        holder.evnt_name_tv.setText(evntInfo.getEvntName());

        holder.event_img_iv.setImageDrawable(context.getDrawable(evntInfo.getImage()));
        setAnimation(holder.itemView, position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (!drawn.contains(position)) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            animation.setDuration(350);
            viewToAnimate.startAnimation(animation);
            drawn.add(position);
        }
    }

    @Override
    public int getItemCount() {
        return evnt_list.size();
    }

    class EvntInfoViewHolder extends RecyclerView.ViewHolder {

        private String id;

        private TextView evnt_name_tv;
        private TextView host_name_tv;
        private TextView descript_tv;
        private TextView date_tv;
        private CircleImageView event_img_iv;

        private Button editButton;
        private Button deleteButton;

        private EvntInfoViewHolder(@NonNull final View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            date_tv = itemView.findViewById(R.id.evnt_time);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send api call to add this event to user events.
                    markAttendance(itemView);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delEvent(itemView, evnt_name_tv);
                }
            });

        }

        private void markAttendance(final View v) {
            // TODO change this for editing
            String url = "https://api.evnt.me/events/api/remove/" + id + "/" + ident.getValue(context.getString(R.string.user_id));
            StringRequest stringBodyRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context, "event removed from your profile", Toast.LENGTH_LONG).show();
                            // Also a hack, jesus this is all bad
                            v.animate().scaleY(0).alpha(0).setDuration(120).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    ViewGroup.LayoutParams params = v.getLayoutParams();
                                    params.height = 0;
                                    v.setLayoutParams(params);
                                    v.setVisibility(View.GONE);
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                            // Fail
                        }
                    }
            );
            requestQueue.add(stringBodyRequest);
        }

        private void delEvent(final View v, final TextView tv) {

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Delete Event");
            alertDialog.setMessage("Are you sure you want to delete the following event? \n \n " + tv.getText());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO add api call to delete event
                            Toast.makeText(context, "add api call to delete", Toast.LENGTH_LONG).show();

                            // make view disappear, will come back with screen refresh
                            v.animate().scaleY(0).alpha(0).setDuration(120).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    ViewGroup.LayoutParams params = v.getLayoutParams();
                                    params.height = 0;
                                    v.setLayoutParams(params);
                                    v.setVisibility(View.GONE);
                                }
                            });

                            evnt_list.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), evnt_list.size());

                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "did not delete", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }


}


