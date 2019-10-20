package com.example.evnt;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {

    private final String TAG = "BrowseFragment";
    private static final String DESCRIBABLE_KEY = "describable_key";
    private ServerRequestModule mServerRequestModule;
    private Context context;
    private RecyclerView recyclerView;
    private EvntListAdapter evntListAdapter;

    List<EvntCardInfo> evntlist;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * This is where we will be opening the saved state of the fragment (if available)
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

        // TODO server call for user's registered events here
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_my_events,
                container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.evnt_list_recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        evntListAdapter = new EvntListAdapter(context, evntlist);
        recyclerView.setAdapter(evntListAdapter);

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
}
