package com.example.evnt;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HostingEventsFragment extends Fragment {

    private final String TAG = "BrowseFragment";
    private static final String DESCRIBABLE_KEY = "describable_key";
    private ServerRequestModule mServerRequestModule;
    private Context context;
    private RecyclerView recyclerView;
    private EvntHostListAdapter evntHostListAdapter;
    private Button create_event_button;

    List<EvntCardInfo> evntlist;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * This is where we will be opening the saved state of the fragmend (if available)
     * and also passing in the serverrequestmodule to be able to fetch events from the server
     *
     * TODO need to add funcionality to save instances
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mServerRequestModule = (ServerRequestModule) getArguments().getSerializable("serverRequestModule");
        } catch (NullPointerException e) {
            e.getStackTrace();
        }

        // TODO server call for user's hosted/ing events here
        loadList();
    }

    /**
     * THis is where we inflate the view and create the cards we need to store using the
     * recyclerview functionality.
     *
     * TODO need to see if changes needed here for search implementation.
     * TODO make GET api request to get list of events
     * @param savedInstanceState
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_hosting_events,
                container, false);

        create_event_button = (Button) view.findViewById(R.id.create_event);
        create_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater1 = getLayoutInflater();
                View layout = inflater1.inflate(R.layout.create_event_layout, container);
                final EditText nameBox = (EditText) layout.findViewById(R.id.event_name_field);
                final EditText descBox = (EditText) layout.findViewById(R.id.event_desc_field);
                final EditText start = (EditText) layout.findViewById(R.id.start_time_field);
                final EditText end = (EditText) layout.findViewById(R.id.end_time_field);
                final EditText tags = (EditText) layout.findViewById(R.id.tags_name_field);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Create an Event")
                        .setMessage("Please fill in the following information")
                        .setView(layout)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                // TODO api call to save from here
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // don't do anything
                                dialog.dismiss();
                            }
                        }).show();

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.evnt_list_recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        evntHostListAdapter = new EvntHostListAdapter(context, evntlist);
        recyclerView.setAdapter(evntHostListAdapter);

        return view;
    }

    /**
     * so this is function should be used to reload the event list
     * in case of searches, by passing in a list of events with info
     *
     * TODO need to modify the params and use them to build arraylist
     */
    private void loadList() {
        evntlist = new ArrayList<>();
        evntlist.add(new EvntCardInfo("here", "This is the event name", "Shrek", "11-1", "desc - doing stuff", R.drawable.shreyans_profile));
        evntlist.add(new EvntCardInfo("there", "This is the event also", "Tito", "3-5", "321 assignment", R.drawable.tito_profile));
        evntlist.add(new EvntCardInfo("my place", "This is the event tooo", "Leslie", "8-10", "chillin", R.drawable.leslie_profile));
    }

//    public static View.OnClickListener editButtonClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            getActivity().getLayoutInflater();
//        }
//    };
}
