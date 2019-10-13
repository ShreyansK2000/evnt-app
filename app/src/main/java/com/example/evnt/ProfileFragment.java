package com.example.evnt;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

public class ProfileFragment extends Fragment {

    private CallbackManager callbackManager;
    private final String TAG = "ProfileFragment";
    private String name, id, email, profilePicURI;
    private LoginButton logoutButton;
    private ProfilePictureView PPView;
    private AppCompatTextView NameTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profilePicURI = getArguments().getString("profilePicURI");
        id = getArguments().getString("id");
        name = getArguments().getString("name");
        email = getArguments().getString("email");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile,
                container, false);
        PPView = (ProfilePictureView) view.findViewById(R.id.profilePictureView);
        PPView.setProfileId(id);

        NameTV = (AppCompatTextView) view.findViewById(R.id.name_tv);
        NameTV.setText(name);

        callbackManager = CallbackManager.Factory.create();
        logoutButton = (LoginButton) view.findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
