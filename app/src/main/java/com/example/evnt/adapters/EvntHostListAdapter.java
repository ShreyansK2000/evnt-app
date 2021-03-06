package com.example.evnt.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.evnt.EvntCardInfo;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;

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
    private IdentProvider ident;
    private Set<Integer> drawn;
    private ServerRequestModule serverRequestModule;

    public EvntHostListAdapter(Context context, List<EvntCardInfo> evnt_list, ServerRequestModule serverRequestModule) {
        this.context = context;
        this.evnt_list = evnt_list;
        this.ident = new IdentProvider(context);
        this.drawn = new HashSet<>();
        this.serverRequestModule = serverRequestModule;
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
        if (evntInfo.getHostId().equals(ident.getValue(context.getString(R.string.user_id)))) {
            holder.host_name_tv.setText(context.getString(R.string.you_the_host));
        } else {
            String hostOut = "by " + evntInfo.getHostName();
            holder.host_name_tv.setText(hostOut);
        }
        holder.descript_tv.setText(evntInfo.getDescription());
        holder.date_tv.setText(evntInfo.getDateString());
        holder.evnt_name_tv.setText(evntInfo.getEvntName());
        holder.tag_list = evntInfo.getTagList();
        if (!(holder.tag_list.get(0).equals(""))) {
            holder.tagRecycler.setAdapter(new TagChipAdapter(context, holder.tag_list));
        } else {
            holder.tagRecycler.setAdapter(null);
        }

        holder.event_img_iv.setImageResource(evntInfo.getImage());
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
        private Button chatButton;
        private Button deleteButton;
        private RecyclerView tagRecycler;
        private List<String> tag_list;

        private EvntInfoViewHolder(@NonNull final View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            date_tv = itemView.findViewById(R.id.evnt_time);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            chatButton = itemView.findViewById(R.id.chat_button);
            tagRecycler = itemView.findViewById(R.id.tags_recycler);
            LinearLayoutManager horizontalManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false);
            tagRecycler.setLayoutManager(horizontalManager);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send api call to add this event to user events.
                    editEvent();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delEvent(itemView, evnt_name_tv);
                }
            });

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatEvent();
                }
            });
        }

        private void editEvent() {
            // TODO change this for editing
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            WebView wv = new WebView(context) {
                @Override
                protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                    super.onFocusChanged(true, direction, previouslyFocusedRect);
                }

                @Override
                public boolean onCheckIsTextEditor() {
                    return true;
                }
            };

            HashMap<String, String> params = new HashMap<>();
            params.put("Content-Type", "application/json; charset=UTF-8");
            params.put("accessToken", ident.getValue(context.getString(R.string.access_token)));
            params.put("userId", ident.getValue(context.getString(R.string.user_id)));
            wv.getSettings().setJavaScriptEnabled(true);
            wv.loadUrl(context.getString(R.string.event_edit) + evnt_list.get(getAdapterPosition()).getId(), params);
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
            builder.setView(wv);
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialog.show();
        }

        private void delEvent(final View v, final TextView tv) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Delete Event");
            alertDialog.setMessage("Are you sure you want to delete the following event? \n \n " + tv.getText());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO add api call to delete event
                            final int position = getAdapterPosition();
                            String eventId = evnt_list.get(position).getId();
                            String url = "https://api.evnt.me/events/api/delete/" + eventId;
                            serverRequestModule.deleteEventsRequest(url, new EvntListAdapterCallback() {

                                @Override
                                public void removeEvent() {
                                    // nothing
                                }

                                @Override
                                public void addEvent() {
                                    // nothing
                                }

                                @Override
                                public void deleteEvent() {
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
                                    Toast.makeText(context, "deleted successfully", Toast.LENGTH_LONG).show();
                                    evnt_list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, evnt_list.size());
                                }
                            });

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

        private void chatEvent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            WebView wv = new WebView(context) {
                @Override
                protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                    super.onFocusChanged(true, direction, previouslyFocusedRect);
                }

                @Override
                public boolean onCheckIsTextEditor() {
                    return true;
                }
            };

            HashMap<String, String> params = new HashMap<>();
            params.put("Content-Type", "application/json; charset=UTF-8");
            params.put("accessToken", ident.getValue(context.getString(R.string.access_token)));
            params.put("userId", ident.getValue(context.getString(R.string.user_id)));
            wv.getSettings().setJavaScriptEnabled(true);
            wv.loadUrl(context.getString(R.string.event_chat) + evnt_list.get(getAdapterPosition()).getId(), params);
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
            builder.setView(wv);
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialog.show();
        }
    }


}


