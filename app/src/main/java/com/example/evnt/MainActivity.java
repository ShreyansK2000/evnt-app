package com.example.evnt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CallbackManager callbackManager;
    private IdentProvider ident;
    private Toast statusToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ident = new IdentProvider(this);
        statusToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]

        /*
         * FCM makes it so that any data added to the notification can be made available
         * in the intent associated with launching the application, which we can check for here.
         */
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        VideoView videoView;

        String userId = ident.getValue(getString(R.string.user_id));
        if (userId == null) {

            /*
             * If we don't have a cached user, setup the background video on the login screen
             */
            videoView = findViewById(R.id.videoView);

            Objects.requireNonNull(getSupportActionBar()).hide();
            Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg);
            videoView.setVideoURI(uri);
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

            /* Setup authentication handler (facebook login) */
            beginAuthenticationChain();

        } else {

            /*
             * If we have a cached user identity, open FragHostActivity
             */
            Intent intent = new Intent(MainActivity.this, FragHostActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    /**
     * This method sets up the required components for Facebook login and
     * if the login succeeds, it moves down the authentication chain to
     * register the current device id for push notifications.
     */
    private void beginAuthenticationChain() {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                statusToast.setText("Authenticated with Facebook");
                statusToast.show();
                AccessToken loginAccessToken = loginResult.getAccessToken();
                final String loginToken = loginAccessToken.getToken();
                Log.d(TAG, loginToken);
                Log.d(TAG, "User has successfully logged in");

                /* Authenticate with our service itself */
                registerDevice(loginAccessToken);
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

    /**
     * If the Facebook login succeeds, we retrieve the device id to send to our
     * backend authentication service so that we can register the device for
     * push notifications.
     *
     * @param accessToken the access token provided by a successful Facebook login
     */
    private void registerDevice(final AccessToken accessToken) {
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                String token = task.getResult().getToken();
                Log.d(TAG, token);

                serviceAuthentication(accessToken, token);
                }
            });
    }

    /**
     * If we are able to succesfully login with Facebook and retrieve the device Firebase
     * instance id, we are able to send it to our back end authentication service
     * and either create a new user or log in an existing user, and then launch the
     * FragHost activity where all of our use cases other than login live.
     *
     * @param accessToken the access token provided by a successful Facebook login
     * @param registrationToken the firebase instance registration token if successfully retrieved
     */
    private void serviceAuthentication(final AccessToken accessToken, final String registrationToken) {
        statusToast.setText("Authenticating with Evnt Service");
        statusToast.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.oauth_endpoint);

        StringRequest jsonBody = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            ident.setValue(getString(R.string.user_id), (String)res.get(getString(R.string.user_id)));
                            ident.setValue(getString(R.string.access_token), (String)res.get(getString(R.string.access_token)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        statusToast.setText("Success!");
                        statusToast.show();

                        // If successful, open FragHostActivity for regular app operation
                        Intent intent = new Intent(getApplicationContext(), FragHostActivity.class);
                        // Pass this token into the next intent
                        intent.putExtra("accessToken", accessToken);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        statusToast.setText("Error authenticating with Evnt");
                        statusToast.show();
                        // Fail
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String,String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("access_token", accessToken.getToken());
                headers.put("registration_token", registrationToken);
                return headers;
            }
        };

        // Add a retry policy in case of timeouts for any reason
        jsonBody.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Toast.makeText(getApplication(), "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsonBody);
    }
}
