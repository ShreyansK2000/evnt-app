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

/**
 * EvntDetailsDialog class/objects represent the AlertDialogs that
 * appear when the user clicks on the MORE button corresponding to any
 * event.
 */
public class EvntDetailsDialog extends AppCompatDialogFragment {

    private Context context;

    // Event card information to be extracted
    private String eventId;
    private String event_name;
    private String host_name;
    private String date_string;
    private String desc;
    private String location;
    private int image;
    private List<String> tags;

    private String cardType;

    private ImageView map_image_iv;
    private ImageView close_button;
    private Button cancel_button;
    private Button attendance_button;

    private EvntListAdapterCallback callback;
    private ServerRequestModule serverRequestModule;

    /**
     * Constructor for detailed view for events from the Event list adapters.
     * It registers a callback and which sends calls to the REST apis to
     * add or remove users from events through the appropriate adapter class
     *
     * @param context The context to draw the dialog
     * @param evntCardInfo The eventCardInfo object corresponding to this details view
     * @param cardType The cardType indicates the kind of
     *                 adapter, thus the kind of button on the dialog,
     *                 which dicatates the callback method used
     * @param callback The registered callback which returns to the list adapters
     *                 to either add the user to the event remove them from the event
     */
    public EvntDetailsDialog(Context context, EvntCardInfo evntCardInfo,
                             String cardType, EvntListAdapterCallback callback) {
        attachVariables(context, evntCardInfo);
        this.cardType = cardType;
        this.callback = callback;
        this.serverRequestModule = null;
    }

    /**
     * Constructor for detailed view for events from the Pick Event Fragment.
     * it does not register a callback and allows the dialog to directly interact
     * with the serverCommunicationModule.
     *
     * @param context The context to draw the dialog
     * @param evntCardInfo The eventCardInfo object corresponding to this details view
     * @param serverRequestModule Server communication module for requests if not from adapters
     */
    public EvntDetailsDialog(Context context, EvntCardInfo evntCardInfo,
                             ServerRequestModule serverRequestModule) {
        attachVariables(context, evntCardInfo);
        this.cardType = "NA";
        this.serverRequestModule = serverRequestModule;
    }

    /**
     * Constructor helper to extract information and attach to field instances
     * @param context The context to draw the dialog
     * @param evntCardInfo The eventCardInfo object corresponding to this details view
     */
    private void attachVariables(Context context, EvntCardInfo evntCardInfo) {
        this.event_name = evntCardInfo.getEvntName();
        this.host_name = evntCardInfo.getHostName();
        this.location = evntCardInfo.getLocation();
        this.eventId = evntCardInfo.getId();
        this.desc = evntCardInfo.getDescription();
        this.date_string = evntCardInfo.getDateString();
        this.image = evntCardInfo.getImage();
        this.tags = evntCardInfo.getTagList();
        this.context = context;
    }

    /**
     * Create a new event details dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details_view, null);

        builder.setView(view);
        this.setViews(view);

        final AlertDialog detailsView = builder.create();

        /*
         * The cancel button and close button simply close the dialog
         * The close button is added to more intuitive understanding of the UI
         */
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

        /*
         * The attendance button is used to add or remove the user from the
         * event depending on the adapter in which the MORE button was clicked.
         */
        attendance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * browse corresponds to the cards in the browse/search view
                 * attending corresponds to the my events view
                 * NA corresponds to the complex logic function call
                 */
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

    /**
     * Use this method to load the image returned by the Google Static Maps api
     * into the image view in the details dialog.
     */
    private void loadImage() {

        String mapImgURL = context.getString(R.string.map_url_call)
                + "markers=color:red%7C"
                + location
                + context.getString(R.string.zoom_setting)
                + (Integer.toString(600) + "x" + Integer.toString(350))
                + context.getString(R.string.static_api_key);
        Picasso.get().load(mapImgURL)
                     .into(map_image_iv);

    }

    /**
     * Setup all the views on the dialog
     * @param view The inflated alert dialog views where we will place other items
     */
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
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
