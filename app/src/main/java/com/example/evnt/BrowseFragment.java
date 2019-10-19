package com.example.evnt;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment {

    private final String TAG = "BrowseFragment";
    private static final String DESCRIBABLE_KEY = "describable_key";
    private ServerRequestModule mServerRequestModule;
    private Context context;
    private RecyclerView recyclerView;
    private EvntListAdapter evntListAdapter;

    List<EvntCardInfo> evntlist;


    // set up server request module for this fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mServerRequestModule = (ServerRequestModule) getArguments().getSerializable("serverRequestModule");
        } catch (NullPointerException e) {
            e.getStackTrace();
        }



    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_browse,
                container, false);

//        LinearLayout scrollList = (LinearLayout) view.findViewById(R.id.event_card_scroll);

        // do server request for list of events to create card ite

        loadList();
        recyclerView = (RecyclerView) view.findViewById(R.id.evnt_list_recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));


//                CardView cv = new CardView(context);

        evntListAdapter = new EvntListAdapter(context, evntlist);
        recyclerView.setAdapter(evntListAdapter);

        return view;
    }

    private void loadList() {
        evntlist = new ArrayList<>();
        evntlist.add(new EvntCardInfo("here", "This is the event name", "Shrek", "11-1", "desc - doing stuff", R.drawable.shreyans_profile));
        evntlist.add(new EvntCardInfo("there", "This is the event also", "Tito", "3-5", "321 assignment", R.drawable.tito_profile));
        evntlist.add(new EvntCardInfo("my place", "This is the event tooo", "Leslie", "8-10", "chillin", R.drawable.leslie_profile));
    }
}
