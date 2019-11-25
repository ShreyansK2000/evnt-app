package com.example.evnt;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.evnt.adapters.EvntListAdapterCallback;
import com.example.evnt.adapters.TagChipAdapter;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.networking.VolleyAttendanceCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EvntDetailsDialog extends AppCompatDialogFragment {
    private Context context;

    private TextView evnt_name_tv;
    private String event_name;

    private TextView date_string_tv;
    private String date_string;

    private TextView description_tv;
    private String desc;

    private CircleImageView evnt_img_civ;
    // TODO init with user img

    private ImageView map_image_iv;
    // nothing to initialize

    private String location;

    private ImageView close_button;
    private Button cancel_button;

    private TextView host_name_tv;

    private Button attendance_button;
    private String cardType;

    private EvntListAdapterCallback callback;
    private ServerRequestModule serverRequestModule;
    private String eventId;
    private int image;
    private List<String> tags;
    private RecyclerView recyclerView;
    private String host_name;

    public EvntDetailsDialog() {
        event_name = "";
        date_string = "";
        desc = "";
        cardType = "browse";
    }

    // Use a builder for this, probably
    public EvntDetailsDialog(Context context, String event_name, String date_string, String desc, String cardType, String location, int image, List<String> tags, String host_name, EvntListAdapterCallback callback) {
        this.event_name = event_name;
        this.location = location;
        this.date_string = date_string;
        this.desc = desc;
        this.cardType = cardType;
        this.callback = callback;
        this.context = context;
        this.image = image;
        this.serverRequestModule = null;
        this.tags = tags;
        this.host_name = host_name;
    }

    public EvntDetailsDialog(Context context, String event_name, String date_string, String desc, String location, ServerRequestModule serverRequestModule, String eventId, int image, List<String> tags, String host_name) {
        this.event_name = event_name;
        this.location = location;
        this.date_string = date_string;
        this.desc = desc;
        this.cardType = "NA";
        this.callback = callback;
        this.context = context;
        this.image = image;
        this.serverRequestModule = serverRequestModule;
        this.eventId = eventId;
        this.tags = tags;
        this.host_name = host_name;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details_view, null);

        builder.setView(view);

        evnt_name_tv = view.findViewById(R.id.event_name_field);
        evnt_name_tv.setText(event_name);

        String hostOut = "by " + host_name;
        host_name_tv = view.findViewById(R.id.host_name);
        host_name_tv.setText(hostOut);

        date_string_tv = view.findViewById(R.id.event_time);
        date_string_tv.setText(date_string);

        description_tv = view.findViewById(R.id.event_description);
        description_tv.setText(desc);

        evnt_img_civ = view.findViewById(R.id.evnt_img);
        evnt_img_civ.setImageResource(image);

        map_image_iv = view.findViewById(R.id.map_image);
        loadImage("","");


        recyclerView = view.findViewById(R.id.tags_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        if (!(tags.get(0).equals(""))) {
            recyclerView.setAdapter(new TagChipAdapter(context, tags));
        }

        close_button = view.findViewById(R.id.close_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        attendance_button = view.findViewById(R.id.attending_button);
        String buttonType = (cardType.equals(context.getString(R.string.browse)) || cardType.equals("NA")) ? context.getString(R.string.im_in) :
                context.getString(R.string.nevermind);
        attendance_button.setText(buttonType);

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

                if (!cardType.equals("NA")) {
                    if (cardType.equals(context.getString(R.string.browse))) {
                        cardType = context.getString(R.string.attending);
                        attendance_button.setText(context.getString(R.string.nevermind));
                        callback.addEvent();
                    } else {
                        cardType = context.getString(R.string.browse);
                        attendance_button.setText(context.getString(R.string.im_in));
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

                // do we want this to stay open once the user has chosen I'm in/nevermind?
            }
        });

        return detailsView;
    }

    private void loadImage(String latt, String longt) {
        // TODO setup loading image based on coordinates/address
        String mapImgURL = context.getString(R.string.map_url_call)
                + "markers=color:red%7C"
                + location
                + context.getString(R.string.zoom_setting)
                + (Integer.toString(600) + "x" + Integer.toString(350))
                + context.getString(R.string.static_api_key);
        System.out.println(mapImgURL);
        Picasso.get().load(mapImgURL)
                     .into(map_image_iv);
    }

}
