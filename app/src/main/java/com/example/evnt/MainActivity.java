package com.example.evnt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private final String TAG = "MainActivity";

    private CallbackManager callbackManager;

    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken loginAccessToken = loginResult.getAccessToken();
                String token = loginAccessToken.getToken();

                Log.d(TAG, token);
                Log.d(TAG, "User has successfully logged in");


                GraphRequest request = GraphRequest.newMeRequest(loginAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            // retrieve relevant Facebook account information for use in the main activity,
                            // and send it in the intent
                            URL profile_pic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");



                            //keep for home page
                            String profile_pic_url = profile_pic.toString();
                            String name = object.getString("first_name");
                            String email = object.getString("email");

                            Log.d(TAG, profile_pic_url);
                            Log.d(TAG, name);
                            Log.d(TAG, email);

                            Intent intent = new Intent(MainActivity.this, FragHostActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();


//                            startActivity(intent);
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

            @Override
            public void onCancel() {
                Log.d(TAG, "User has cancelled the login process");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Oh no. You have no network or some other problem");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {

        super.onStart();
//
        AccessToken acctkn = AccessToken.getCurrentAccessToken();

        if (acctkn != null && acctkn.isExpired() == false) {
            Intent intent = new Intent(MainActivity.this, FragHostActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }
}
