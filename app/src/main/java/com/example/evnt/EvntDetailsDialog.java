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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.example.evnt.adapters.EvntListAdapterCallback;
import com.squareup.picasso.Picasso;

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

    private ImageView close_button;
    private Button cancel_button;

    private Button attendance_button;
    private String cardType;

    private EvntListAdapterCallback callback;

    public EvntDetailsDialog() {
        event_name = "";
        date_string = "";
        desc = "";
        cardType = "browse";
    }

    public EvntDetailsDialog(Context context, String event_name, String date_string, String desc, String cardType, EvntListAdapterCallback callback) {
        this.event_name = event_name;
        this.date_string = date_string;
        this.desc = desc;
        this.cardType = cardType;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details_view, null);

        builder.setView(view);

        evnt_name_tv = view.findViewById(R.id.event_name_field);
        evnt_name_tv.setText(event_name);

        date_string_tv = view.findViewById(R.id.event_time);
        date_string_tv.setText(date_string);

        description_tv = view.findViewById(R.id.event_description);
        description_tv.setText(desc);

        evnt_img_civ = view.findViewById(R.id.evnt_img);
        evnt_img_civ.setImageResource(R.drawable.chika);

        map_image_iv = view.findViewById(R.id.map_image);
        loadImage("","");

        close_button = view.findViewById(R.id.close_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        attendance_button = view.findViewById(R.id.attending_button);
        String buttonType = cardType.equals(context.getString(R.string.browse)) ? context.getString(R.string.im_in) :
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

                if (cardType.equals(context.getString(R.string.browse))) {
                    cardType = context.getString(R.string.attending);
                    attendance_button.setText(context.getString(R.string.nevermind));
                    callback.addEvent();
                } else {
                    cardType = context.getString(R.string.browse);
                    attendance_button.setText(context.getString(R.string.im_in));
                    callback.removeEvent();
                }

                // do we want this to stay open once the user has chosen I'm in/nevermind?
            }
        });

        return detailsView;
    }

    private void loadImage(String latt, String longt) {
        // TODO setup loading image based on coordinates/address
        String mapImgURL = context.getString(R.string.map_url_call)
                + "markers=color:red%7C49.262271,-123.250680"
                + context.getString(R.string.zoom_setting)
                + (Integer.toString(750) + "x" + Integer.toString(550))
                + context.getString(R.string.static_api_key);
        Picasso.get().load(mapImgURL)
                     .into(map_image_iv);
    }

}
