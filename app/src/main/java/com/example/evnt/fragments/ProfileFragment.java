package com.example.evnt.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evnt.IdentProvider;
import com.example.evnt.MainActivity;
import com.example.evnt.R;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

// TODO decide what items we need to show here and what functionality.
public class ProfileFragment extends Fragment {

    private String name;
    private String id;
    private String email;
    private IdentProvider ident;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ident = new IdentProvider(getContext());

        id = ident.getValue(getString(R.string.fb_id));
        name = ident.getValue(getString(R.string.user_name));
        email = ident.getValue(getString(R.string.user_email));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile,
                container, false);
        ProfilePictureView PPView = (ProfilePictureView) view.findViewById(R.id.profilePictureView);
        PPView.setProfileId(id);

        AppCompatTextView NameTV = (AppCompatTextView) view.findViewById(R.id.name_tv);
        NameTV.setText(name);

        AppCompatTextView EmailTV = (AppCompatTextView) view.findViewById(R.id.email_tv);
        NameTV.setText(email);

        LoginButton logoutButton = (LoginButton) view.findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                ident.setValue(getString(R.string.profile_pic), null);
                ident.setValue(getString(R.string.fb_id), null);
                ident.setValue(getString(R.string.user_name), null);
                ident.setValue(getString(R.string.user_email), null);
                ident.setValue(getString(R.string.user_id), null);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
