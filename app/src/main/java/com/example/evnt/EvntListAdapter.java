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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private RequestQueue requestQueue;
    private IdentProvider ident;

    public EvntListAdapter(Context context, List<EvntCardInfo> evnt_list) {
        this.context = context;
        this.evnt_list = evnt_list;
        this.ident = new IdentProvider(context);
        this.requestQueue = Volley.newRequestQueue(context);
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
        holder.id = evntInfo.getId();
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

        String id;

        TextView evnt_name_tv, host_name_tv, descript_tv;
        CircleImageView event_img_iv;

        Button inButton;


        public _EvntInfoViewHolder(@NonNull final View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            inButton = itemView.findViewById(R.id.in_button);
//            final Button toggleButton = holder.inButton;

            inButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send api call to add this event to user events.
                    markAttendance(itemView);
                }
            });
        }

        private void markAttendance(final View v) {
            String url = "https://api.evnt.me/events/api/add/" + id + "/" + ident.getValue(context.getString(R.string.user_id));
            StringRequest stringBodyRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context, "event added to your profile", Toast.LENGTH_LONG).show();
                            // Also a hack, jesus this is all bad
                            ViewGroup.LayoutParams params = v.getLayoutParams();
                            params.height = 0;
                            v.setLayoutParams(params);
                            v.setVisibility(View.GONE);
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
    }


}

