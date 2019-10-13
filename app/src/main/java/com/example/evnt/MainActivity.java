package com.example.evnt;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
                final String token = loginAccessToken.getToken();

                Log.d(TAG, token);
                Log.d(TAG, "User has successfully logged in");

                Intent intent = new Intent(MainActivity.this, FragHostActivity.class);
                intent.putExtra("Token", token);
                intent.putExtra("AccessToken", loginAccessToken);
                startActivity(intent);
                MainActivity.this.finish();
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
            intent.putExtra("Token", acctkn.getToken());
            intent.putExtra("AccessToken", acctkn);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }
}
