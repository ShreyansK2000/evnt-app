package com.example.evnt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = "BrowseFragment";
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeView;
    private EvntListAdapter evntListAdapter;

    private IdentProvider ident;

    List<EvntCardInfo> evntlist;
    Fragment ctx;

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

        swipeView = view.findViewById(R.id.main_content);
        swipeView.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.evnt_list_recycler);
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
        evntlist.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = getString(R.string.event_get_avail) + ident.getValue(getString(R.string.user_id));
        StringRequest stringBodyRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("data");
                            String you = ident.getValue(getString(R.string.user_id));

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                EvntCardInfo evnt = new EvntCardInfo.Builder()
                                        .withName(obj.get("tag_list").toString().replace("\"", "") + " " + obj.get("name"))
                                        .withDescription((String)obj.get("description"))
                                        .withStartTime((String)obj.get("start_time"))
                                        .withEndTime((String)obj.get("end_time"))
                                        .withId((String)obj.get("_id"))
                                        .withHost(obj.get("host").equals(you) ? "you" : "Anonymous")
                                        .build();

                                evntlist.add(evnt);
                            }

                            // TODO This is a hack to refresh the view, so we redraw the list
                            if (getFragmentManager() != null) {
                                getFragmentManager().beginTransaction().detach(ctx).attach(ctx).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Fail
                    }
                }
        );
        queue.add(stringBodyRequest);
    }

    @Override
    public void onRefresh() {
        loadList();
    }
}
