package com.example.evnt;

import com.example.evnt.fragments.BrowseFragment;
import com.example.evnt.fragments.MyEventsFragment;
import com.example.evnt.fragments.PickEvntFragment;
import com.example.evnt.fragments.ProfileFragment;
import com.example.evnt.networking.ServerRequestModule;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.annotation.NonNull;

import android.content.Intent;
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

        /* Setup the main app navigation */
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

            /* Initialize instances for all the fragments we will be using and switching between*/
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
            /*
             * If we're unable to create server comm module, app cannot work.
             * Ideally, we don't come here
             */
            Toast.makeText(this, "Could not create server request module", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method populates the IdentProvider object for this application
     * using GraphRequest to retrieve information from Facebook
     * which allows us to cache the user's details and, in turn,
     * facilitate communication with the server
     *
     * @param token The access token of the user to validate the GraphRequest call
     */
    private void retrieveFBUserDetails(final AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String id;
                    String name;
                    String email;

                    id = object.getString("id");
                    name = object.getString("name");
                    email = object.getString("email");
                    URL profilePicURI = new URL("https://graph.facebook.com/"+
                                        object.getString("id")+"/picture?width=250&height=250");

                    //keep for profile page
                    ident.setValue(getString(R.string.user_name), name);
                    ident.setValue(getString(R.string.user_email), email);
                    ident.setValue(getString(R.string.fb_id), id);
                    ident.setValue(getString(R.string.profile_pic), profilePicURI.toString());

                    /*
                     * At login the IdentProvider settings are not properly set so
                     * profile fragment gets null values. Call this method to send new
                     * ident values when they are received.
                     */
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

    /*
     * This listener is used to detect events on the bottom navigation menu
     * to correctly switch between fragments.
     *
     * On switch, the active fragment is switched and the one corresponding with
     * the menu option is shown. We do this in order to avoid creating new fragments
     * everytime we switch, and instead save an instance of each fragment in this
     * activity.
     */
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

                    /*
                     * By default, i.e. at the start of the activity, select the complex logic/
                     * pick event fragment
                     */
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

    /**
     * This method parses the JSON response from the server and constructs EvntCardInfo objects
     * which will be displayed using adapters in tha appropriate views.
     *
     * @param evntlist The list to populate with the resulting EvntCardInfo objects
     * @param data server JSONArray response to traverse and construct objects
     * @param you gets user_id string from cache,
     *            equivalent to ident.getValue(getString(R.string.user_id))
     * @param host A boolean to indicate if the list is for the HostingEventsFragment
     *             in which case the events with host id = user id are added and others excluded
     *             and vice versa
     */
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

                /*
                 * if being requested by HostingEventsFragment, get list of events that user
                 * is hosting, else return events they are attending or can browse
                 */
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
