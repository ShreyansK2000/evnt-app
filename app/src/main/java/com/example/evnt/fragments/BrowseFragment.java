package com.example.evnt.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evnt.EvntCardInfo;
import com.example.evnt.FragHostActivity;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.networking.VolleyEventListCallback;
import com.example.evnt.adapters.EvntListAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private ServerRequestModule mServerRequestModule;
    private IdentProvider ident;
    private List<EvntCardInfo> evntlist;
    private Fragment ctx;

    public static BrowseFragment newInstance(ServerRequestModule serverRequestModule) {
        BrowseFragment fragment = new BrowseFragment();
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
        ctx = this;
        ident = new IdentProvider(getContext());
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_browse,
                container, false);

        SwipeRefreshLayout swipeView = view.findViewById(R.id.main_content);
        swipeView.setOnRefreshListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.evnt_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        EvntListAdapter evntListAdapter = new EvntListAdapter(context, evntlist, getString(R.string.browse), getActivity().getSupportFragmentManager(), mServerRequestModule);
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
        evntlist.clear();
        mServerRequestModule.getEventsRequest(getString(R.string.event_get_avail), new VolleyEventListCallback() {
            @Override
            public void onEventsListSuccessResponse(JSONArray data) {
                String you = ident.getValue(getString(R.string.user_id));
                try {
                    ((FragHostActivity) getActivity()).sortResponseToList(evntlist, data, you, false);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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
