package com.example.evnt;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class FragHostActivity extends AppCompatActivity {

    protected URL profilePicURI;
    protected String name, email, id, tokenString;
    protected AccessToken loginAccessToken;
    protected Bundle serverCommArgs;
    protected ServerRequestModule serverRequestModule;
    private final String TAG = "FragHostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_host);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(listener);

        Intent intent = getIntent();
        System.out.println(intent);
        Bundle extras = intent.getExtras();
        System.out.println(extras);

        tokenString = extras.getString("Token");
        loginAccessToken = extras.getParcelable("AccessToken");

        System.out.println(tokenString);
        System.out.println(loginAccessToken);

        serverRequestModule = new ServerRequestModule();

        GraphRequest request = GraphRequest.newMeRequest(loginAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    // retrieve relevant Facebook account information for use in the main activity,
                    // and send it in the intent
                    profilePicURI = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

                    //keep for profile page
                    name = object.getString("first_name");
                    email = object.getString("email");
                    id = object.getString("id");

                    Log.d(TAG, profilePicURI.toString());
                    Log.d(TAG, name);
                    Log.d(TAG, email);

                    // async request so we have to do this inside
                    serverCommArgs = new Bundle();
                    serverCommArgs.putString("id", id);
                    serverCommArgs.putString("profilePicURI", profilePicURI.toString());
                    serverCommArgs.putString("name", name);
                    serverCommArgs.putString("email", email);
                    serverCommArgs.putSerializable("requestModule", serverRequestModule);

                    Fragment selected = new PickEvntFragment();
                    selected.setArguments(serverCommArgs);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selected).commit();
                } catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email, first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selected = null;
                    switch (menuItem.getItemId()) {
                        case R.id.pick_evnt:  selected = new PickEvntFragment(); break;
                        case R.id.browse_evnt: selected = new BrowseFragment(); break;
                        case R.id.chat_evnt: selected = new ChatFragment(); break;
                        case R.id.another_evnt: selected = new OtherFragment(); break;
                        case R.id.profile_evnt: selected = new ProfileFragment(); break;
                        default: selected = new PickEvntFragment(); break;
                    }

                    try {
                        selected.setArguments(serverCommArgs);
                    } catch (NullPointerException e){
                        System.out.println(e.getStackTrace());
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selected).commit();

                    return true;
                }
            };
}
