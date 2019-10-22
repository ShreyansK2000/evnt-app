package com.example.evnt;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class FragHostActivity extends AppCompatActivity {

    protected Bundle serverCommArgs;
    private final String TAG = "FragHostActivity";

    private IdentProvider ident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void retrieveFBUserDetails(final AccessToken token) {
//        final ServerRequestModule serverRequestModule = new ServerRequestModule();
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    // retrieve relevant Facebook account information for use in the main activity,
                    // and send it in the intent
                    String id, name, email;
                    id = object.getString("id");
                    name = object.getString("name");
                    email = object.getString("email");
                    URL profilePicURI = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
                    //keep for profile page
                    ident.setValue(getString(R.string.user_name), name);
                    ident.setValue(getString(R.string.user_email), email);
                    ident.setValue(getString(R.string.fb_id), id);
                    ident.setValue(getString(R.string.profile_pic), profilePicURI.toString());
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
                // This lifecycle is a bit suboptimal, as we're creating new fragments every time
                switch (menuItem.getItemId()) {
                    case R.id.pick_evnt:  selected = new PickEvntFragment(); break;
                    case R.id.browse_evnt: selected = new BrowseFragment(); break;
//                    case R.id.chat_evnt: selected = new ChatFragment(); break;
                    case R.id.another_evnt: selected = new HostingEventsFragment(); break;
                    case R.id.profile_evnt: selected = new ProfileFragment(); break;
                    default: selected = new PickEvntFragment(); break;
                }

                try {
                    selected.setArguments(serverCommArgs);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selected).commit();

                return true;
            }
        };
}
