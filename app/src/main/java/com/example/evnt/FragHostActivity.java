package com.example.evnt;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.evnt.fragments.BrowseFragment;
import com.example.evnt.fragments.MyEventsFragment;
import com.example.evnt.fragments.PickEvntFragment;
import com.example.evnt.fragments.ProfileFragment;
import com.example.evnt.networking.ServerRequestModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FragHostActivity extends AppCompatActivity {

    private IdentProvider ident;
    private PickEvntFragment pickEvntFragment;
    private BrowseFragment browseFragment;
    private MyEventsFragment myEventsFragment;
    private ProfileFragment profileFragment;
    private Fragment current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickEvntFragment = null;
        browseFragment = null;
        myEventsFragment = null;
        profileFragment = null;

//        if (savedInstanceState != null) {
//            pickEvntFragment = (PickEvntFragment) getSupportFragmentManager().getFragment(savedInstanceState, "pickEvntFrag");
//            browseFragment = (BrowseFragment) getSupportFragmentManager().getFragment(savedInstanceState, "browseFrag");
//            myEventsFragment = (MyEventsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myEventsFrag");
//            profileFragment = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, "profileFrag");
//        }

        setContentView(R.layout.activity_frag_host);
        ident = new IdentProvider(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(listener);
        bottomNav.setSelectedItemId(R.id.pick_evnt);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (intent.hasExtra("accessToken")) {
            final AccessToken loginAccessToken = extras.getParcelable("accessToken");
            retrieveFBUserDetails(loginAccessToken);
        }
        ServerRequestModule serverRequestModule = ServerRequestModule.getInstance(getApplicationContext(), ident);

        if (serverRequestModule != null) {
            pickEvntFragment = new PickEvntFragment();
            browseFragment = new BrowseFragment();
            myEventsFragment = new MyEventsFragment();
            profileFragment = new ProfileFragment();
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction().add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, browseFragment, "browse").hide(browseFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, myEventsFragment, "myEvents").hide(myEventsFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, pickEvntFragment, "pick").commit();
            current = pickEvntFragment;
        } else {
            Toast.makeText(this, "Could not create server request module", Toast.LENGTH_LONG).show();
        }
    }
    private void retrieveFBUserDetails(final AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    // retrieve relevant Facebook account information for use in the main activity,
                    // and send it in the intent
                    String id;
                    String name;
                    String email;

                    id = object.getString("id");
                    name = object.getString("name");
                    email = object.getString("email");
                    URL profilePicURI = new URL("https://graph.facebook.com/"+ object.getString("id")+"/picture?width=250&height=250");
                    //keep for profile page
                    ident.setValue(getString(R.string.user_name), name);
                    ident.setValue(getString(R.string.user_email), email);
                    ident.setValue(getString(R.string.fb_id), id);
                    ident.setValue(getString(R.string.profile_pic), profilePicURI.toString());
                    if (profileFragment != null) {
                        profileFragment.updateIdent(ident);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selected;

                if (current == null) {
                    pickEvntFragment = new PickEvntFragment();
                    current = pickEvntFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            pickEvntFragment);
                }
                // This lifecycle is a bit suboptimal, as we're creating new fragments every time
                switch (menuItem.getItemId()) {
                    case R.id.pick_evnt:
                        if (pickEvntFragment == null) selected = new PickEvntFragment();
                        else selected = pickEvntFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    case R.id.browse_evnt:
                        if (browseFragment == null) selected = new BrowseFragment();
                        else selected = browseFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    case R.id.my_events:
                        if (myEventsFragment == null) selected = new MyEventsFragment();
                        else selected = myEventsFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    case R.id.profile_evnt:
                        if (profileFragment == null) selected = new ProfileFragment();
                        else selected = profileFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    default:
                        if (pickEvntFragment == null) selected = new PickEvntFragment();
                        else selected = pickEvntFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;
                }

                return true;
            }
        };

    public void sortResponseToList(List<EvntCardInfo> evntlist, JSONArray data, String you, boolean host){
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);

                EvntCardInfo evnt = new EvntCardInfo.Builder()
                        .withName(obj.get("name").toString())
                        .withDescription((String) obj.get("description"))
                        .withStartTime((String) obj.get("startTime"))
                        .withEndTime((String) obj.get("endTime"))
                        .withLocation((String) obj.get("location"))
                        .withId((String) obj.get("_id"))
                        .withHostId(obj.get("host").toString())
                        .withHostName((obj.get("hostname")).toString())
                        .withTagList((obj.get("tagList").toString().replace("[","")
                                .replace("]","").replace("\"", "")).split(","))
                        .build();

                // if being requested by HostingEventsFragment, get list of events user is hosting
                // else return events they are attending or can browse
                if (host) {
                    if (obj.get("host").equals(you)) {
                        evntlist.add(evnt);
                    }
                } else {
                    if (!obj.get("host").equals(you)) {
                        evntlist.add(evnt);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        getSupportFragmentManager().putFragment(outState, "pickEvntFrag", pickEvntFragment);
//        getSupportFragmentManager().putFragment(outState, "browseFrag", browseFragment);
//        getSupportFragmentManager().putFragment(outState, "myEventsFrag", myEventsFragment);
//        getSupportFragmentManager().putFragment(outState, "profileFrag", profileFragment);
//
//    }
}
