package com.example.evnt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.evnt.EvntCardInfo;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.networking.VolleyCallback;
import com.example.evnt.adapters.EvntListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AttendingEventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeView;
    private EvntListAdapter evntListAdapter;
    private ServerRequestModule mServerRequestModule;

    List<EvntCardInfo> evntlist;

    private IdentProvider ident;
    Fragment ctx;

    public static AttendingEventsFragment newInstance(ServerRequestModule serverRequestModule) {
        AttendingEventsFragment fragment = new AttendingEventsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("server_module", serverRequestModule);
        fragment.setArguments(bundle);

        return fragment;
    }

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
        ident = new IdentProvider(getContext());
        ctx = this;
        mServerRequestModule = (ServerRequestModule) getArguments().getSerializable("server_module");

        evntlist = new ArrayList<>();
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
        final View view = inflater.inflate(R.layout.fragment_attending_events,
                container, false);
        swipeView = view.findViewById(R.id.main_content);
        swipeView.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.evnt_list_recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        evntListAdapter = new EvntListAdapter(context, evntlist, "attending");
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

        // TODO need to filter non-hosting events
        evntlist.clear();
        mServerRequestModule.getEventsRequest(getString(R.string.event_get_in), new VolleyCallback() {
            @Override
            public void onEventsListSuccessResponse(JSONArray data) {
                String you = ident.getValue(getString(R.string.user_id));

                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);

                        EvntCardInfo evnt = new EvntCardInfo.Builder()
                                .withName(obj.get("tagList").toString().replace("\"", "") + " " + obj.get("name"))
                                .withDescription((String) obj.get("description"))
                                .withStartTime((String) obj.get("startTime"))
                                .withEndTime((String) obj.get("endTime"))
                                .withId((String) obj.get("_id"))
                                .withHost(obj.get("host").equals(you) ? "you" : "Anonymous")
                                .build();

                        if (!obj.get("host").equals(you)) {
                            evntlist.add(evnt);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // TODO This is a hack to refresh the view, so we redraw the list
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().detach(ctx).attach(ctx).commit();
                }
            }

            @Override
            public void onErrorResponse(String result) {
                Toast.makeText(context, "unable to load events currently", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onRefresh() {
        loadList();
    }
}
