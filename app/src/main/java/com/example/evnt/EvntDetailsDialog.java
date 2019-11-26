package com.example.evnt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.evnt.adapters.EvntListAdapterCallback;
import com.example.evnt.adapters.TagChipAdapter;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.networking.VolleyAttendanceCallback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EvntDetailsDialog extends AppCompatDialogFragment {
    private Context context;

    private String event_name;

    private String date_string;

    private String desc;

    private ImageView map_image_iv;
    // nothing to initialize

    private String location;

    private ImageView close_button;
    private Button cancel_button;

    private Button attendance_button;
    private String cardType;

    private EvntListAdapterCallback callback;
    private ServerRequestModule serverRequestModule;
    private String eventId;
    private int image;
    private List<String> tags;
    private String host_name;

    public EvntDetailsDialog() {
        event_name = "";
        date_string = "";
        desc = "";
        cardType = "browse";
    }

    // Use a builder for this, probably
    public EvntDetailsDialog(
            Context context, EvntCardInfo evntCardInfo, String cardType, int image,
            List<String> tags, EvntListAdapterCallback callback) {
        this.event_name = evntCardInfo.getEvntName();
        this.host_name = evntCardInfo.getHostName();
        this.location = evntCardInfo.getLocation();
        this.eventId = evntCardInfo.getId();
        this.desc = evntCardInfo.getDescription();
        this.date_string = evntCardInfo.getDateString();
        this.cardType = cardType;
        this.callback = callback;
        this.context = context;
        this.image = image;
        this.serverRequestModule = null;
        this.tags = tags;
    }

    public EvntDetailsDialog(
            Context context, EvntCardInfo evntCardInfo,ServerRequestModule serverRequestModule,
            int image, List<String> tags) {
        this.event_name = evntCardInfo.getEvntName();
        this.host_name = evntCardInfo.getHostName();
        this.location = evntCardInfo.getLocation();
        this.eventId = evntCardInfo.getId();
        this.desc = evntCardInfo.getDescription();
        this.date_string = evntCardInfo.getDateString();
        this.cardType = "NA";
        this.context = context;
        this.image = image;
        this.serverRequestModule = serverRequestModule;
        this.tags = tags;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details_view, null);

        builder.setView(view);
        this.setViews(view);

        final AlertDialog detailsView = builder.create();

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsView.dismiss();
            }
        });

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsView.dismiss();
            }
        });

        attendance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!"NA".equals(cardType)) {
                    if (cardType.equals(context.getString(R.string.browse))) {
                        cardType = context.getString(R.string.attending);
                        attendance_button.setText(context.getString(R.string.nevermind));
                        attendance_button.setEnabled(false);
                        callback.addEvent();
                    } else {
                        cardType = context.getString(R.string.browse);
                        attendance_button.setText(context.getString(R.string.im_in));
                        attendance_button.setEnabled(false);
                        callback.removeEvent();
                    }
                } else {

                    if (serverRequestModule != null) {

                        serverRequestModule.markUserAttendance("https://api.evnt.me/events/api/add/"
                                                                        + eventId + "/",
                                new VolleyAttendanceCallback() {
                                    @Override
                                    public void onAttendanceSuccessResponse() {
                                        Toast.makeText(context.getApplicationContext(),
                                                "Added successfully, please see My Events: Attending",
                                                Toast.LENGTH_LONG).show();
                                        System.out.println("added attendance successfully");
                                        dismiss();
                                    }

                                    @Override
                                    public void onErrorResponse(String result) {
                                        Toast.makeText(context.getApplicationContext(),
                                                "Internal error",
                                                Toast.LENGTH_LONG).show();
                                        System.out.println("there was an error");
                                    }
                                });
                    }

                }

            }
        });

        return detailsView;
    }

    private void loadImage() {
        // TODO setup loading image based on coordinates/address
        String mapImgURL = context.getString(R.string.map_url_call)
                + "markers=color:red%7C"
                + location
                + context.getString(R.string.zoom_setting)
                + (Integer.toString(600) + "x" + Integer.toString(350))
                + context.getString(R.string.static_api_key);
        Picasso.get().load(mapImgURL)
                     .into(map_image_iv);
    }

    private void setViews(View view) {
        TextView evnt_name_tv = view.findViewById(R.id.event_name_field);
        evnt_name_tv.setText(event_name);

        String hostOut = "by " + host_name;
        TextView host_name_tv = view.findViewById(R.id.host_name);
        host_name_tv.setText(hostOut);

        TextView date_string_tv = view.findViewById(R.id.event_time);
        date_string_tv.setText(date_string);

        TextView description_tv = view.findViewById(R.id.event_description);
        description_tv.setText(desc);

        CircleImageView evnt_img_civ = view.findViewById(R.id.evnt_img);
        evnt_img_civ.setImageResource(image);

        map_image_iv = view.findViewById(R.id.map_image);
        loadImage();


        RecyclerView recyclerView = view.findViewById(R.id.tags_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        if (!(tags.get(0).equals(""))) {
            recyclerView.setAdapter(new TagChipAdapter(context, tags));
        }

        close_button = view.findViewById(R.id.close_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        attendance_button = view.findViewById(R.id.attending_button);
        String buttonType = (context.getString(R.string.browse).equals(cardType) ||
                "NA".equals(cardType)) ? context.getString(R.string.im_in) :
                context.getString(R.string.nevermind);
        attendance_button.setText(buttonType);

    }

}
