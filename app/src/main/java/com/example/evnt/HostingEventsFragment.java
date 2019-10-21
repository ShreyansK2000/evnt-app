package com.example.evnt;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HostingEventsFragment extends Fragment {

    private final String TAG = "BrowseFragment";
    private Context context;
    private RecyclerView recyclerView;
    private EvntHostListAdapter evntHostListAdapter;
    private FloatingActionButton create_event_button;

    List<EvntCardInfo> evntlist;

    private IdentProvider ident;
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
        ident = new IdentProvider(getContext());
        ctx = this;

        // TODO server call for user's hosted/ing events here
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
        final View view = inflater.inflate(R.layout.fragment_hosting_events,
                container, false);

        create_event_button = view.findViewById(R.id.create_event);
        create_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                WebView wv = new WebView(getContext()) {
                    @Override
                    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                        super.onFocusChanged(true, direction, previouslyFocusedRect);
                    }

                    @Override
                    public boolean onCheckIsTextEditor() {
                        return true;
                    }
                };
                wv.getSettings().setJavaScriptEnabled(true);
                wv.loadUrl(getString(R.string.event_create) + ident.getValue(getString(R.string.user_id)));
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
                        loadList();
                        dialog.dismiss();
                    }
                });
                builder.show();
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
        evntlist.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = getString(R.string.event_get_in) + ident.getValue(getString(R.string.user_id));
        StringRequest stringBodyRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                EvntCardInfo evnt = new EvntCardInfo.Builder()
                                        .withName((String)obj.get("name"))
                                        .withDescription((String)obj.get("description"))
                                        .withTime((String)obj.get("start_time"))
                                        .build();

                                evntlist.add(evnt);
                            }

                            // TODO This is a hack to refresh the view, so we redraw the list
                            getFragmentManager().beginTransaction().detach(ctx).attach(ctx).commit();
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
}
