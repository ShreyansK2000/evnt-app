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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evnt.EvntCardInfo;
import com.example.evnt.EvntDetailsDialog;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.networking.VolleyAttendanceCallback;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * ViewHolder model to essentially replicate the EventCardInfo layout over and over
 * with the correct information
 *
 * TODO figure out how to show google map snippet
 * TODO figure out how to show images from server calls (also in EvntCardInfo)
 */
public class EvntListAdapter extends RecyclerView.Adapter<EvntListAdapter.EvntInfoViewHolder> {

    private Context context;
    private List<EvntCardInfo> evnt_list;
    private String cardType;
    private FragmentManager supportFragmentManager;
    private ServerRequestModule serverRequestModule;
    private IdentProvider ident;

    private Set<Integer> drawn;

    public EvntListAdapter(Context context, List<EvntCardInfo> evnt_list, String type,
                           FragmentManager supportFragmentManager, ServerRequestModule module,
                           OnItemRemovedListener callBack) {
        this.context = context;
        this.evnt_list = evnt_list;
        this.drawn = new HashSet<>();
        this.cardType = type;
        this.ident = new IdentProvider(context);
        this.supportFragmentManager  = supportFragmentManager;
        this.serverRequestModule = module;
    }

    @NonNull
    @Override
    public EvntInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.evnt_card_view_holder, parent, false);
        return new EvntInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EvntInfoViewHolder holder, int position) {
        final EvntCardInfo evntInfo = evnt_list.get(position);

        String buttonType = cardType.equals(context.getString(R.string.browse)) ? context.getString(R.string.im_in) :
                                                        context.getString(R.string.nevermind);
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
        holder.inButton.setText(buttonType);
        holder.tag_list = evntInfo.getTagList();
        if (!(holder.tag_list.get(0).equals(""))) {
            holder.tagRecycler.setAdapter(new TagChipAdapter(context, holder.tag_list));
        } else {
            holder.tagRecycler.setAdapter(null);
        }

        holder.event_img_iv.setImageResource(evntInfo.getImage());
        setAnimation(holder.itemView, position);

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(evntInfo.getEvntName(), evntInfo.getDateString(), evntInfo.getLocation(), evntInfo.getDescription(), evntInfo.getImage(), evntInfo.getTagList(), evntInfo.getHostName(),
                    new EvntListAdapterCallback() {
                        @Override
                        public void removeEvent() { holder.markAttendance(holder.holderView, false); }

                        @Override
                        public void addEvent() {
                            holder.markAttendance(holder.holderView, true);
                        }

                        @Override
                        public void deleteEvent() {
                            // nothing
                        }
                    });
            }
        });

    }

    private void openDialog(String event_name, String date_string, String location, String desc, int image, List<String> tags, String host_name, EvntListAdapterCallback callback) {
        EvntDetailsDialog detailsDialog = new EvntDetailsDialog(context, event_name, date_string, desc, cardType, location, image, tags,host_name, callback);
        detailsDialog.show(supportFragmentManager, "");
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
        private View holderView;

        private TextView evnt_name_tv;
        private TextView host_name_tv;
        private TextView descript_tv;
        private TextView date_tv;
        private CircleImageView event_img_iv;

        private Button inButton;
        private Button moreButton;
        private Button chatButton;

        private RecyclerView tagRecycler;
        private List<String> tag_list;

        private EvntInfoViewHolder(@NonNull final View itemView) {
            super(itemView);
            event_img_iv = itemView.findViewById(R.id.evnt_img);
            evnt_name_tv = itemView.findViewById(R.id.evnt_name);
            host_name_tv = itemView.findViewById(R.id.host_name);
            descript_tv = itemView.findViewById(R.id.evnt_descript);
            date_tv = itemView.findViewById(R.id.evnt_time);
            inButton = itemView.findViewById(R.id.in_button);
            moreButton = itemView.findViewById(R.id.details_button);
            chatButton = itemView.findViewById(R.id.chat_button);
            tagRecycler = itemView.findViewById(R.id.tags_recycler);
            LinearLayoutManager horizontalManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false);
            tagRecycler.setLayoutManager(horizontalManager);

            inButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send api call to add this event to user events.
                    if (cardType.equals(context.getString(R.string.browse))) {
                        markAttendance(itemView, true);
                    } else {
                        markAttendance(itemView, false);
                    }
                    inButton.setEnabled(false);
                }
            });

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatEvent(itemView);
                }
            });

            holderView = itemView;

        }

        private void markAttendance(final View v, final boolean attending) {
            String reqURL = "https://api.evnt.me/events/api/";
            if (attending) {
                reqURL = reqURL + "add/" + id + "/";
            } else {
                reqURL = reqURL + "remove/" + id + "/";
            }
            serverRequestModule.markUserAttendance(reqURL,
                new VolleyAttendanceCallback() {

                    @Override
                    public void onAttendanceSuccessResponse() {
                        if (attending) {
                            Toast.makeText(context, "event added to your profile", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "event removed from your profile", Toast.LENGTH_LONG).show();
                        }
                        v.animate().scaleY(0).alpha(0).setDuration(120).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ViewGroup.LayoutParams params = v.getLayoutParams();
                                params.height = 0;
                                v.setLayoutParams(params);
                                v.setVisibility(View.GONE);
                            }
                        });

                        int position = getAdapterPosition();

                        evnt_list.remove(position);

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, evnt_list.size());
                    }

                    @Override
                    public void onErrorResponse(String result) {
                        Toast.makeText(context, "There was an error", Toast.LENGTH_LONG).show();
                    }
                });
        }

        private void chatEvent(final View v) {
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

