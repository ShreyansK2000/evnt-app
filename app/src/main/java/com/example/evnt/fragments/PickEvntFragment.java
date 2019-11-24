package com.example.evnt.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.evnt.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import de.hdodenhof.circleimageview.CircleImageView;

// TODO see if we need to add more specific information to include in a search profile (date, time, tag)
public class PickEvntFragment extends Fragment {

    // save the result from the selected item in the spinner
    private String spinnerString;
    private FusedLocationProviderClient fusedLocationClient;
    private int locationPermissionGranted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_pickevnt,
                container, false);

        CircleImageView imageView = view.findViewById(R.id.evnt_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermissionGranted);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }

                } else {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object

                                        String latt = Double.toString(location.getLatitude());
                                        String longt = Double.toString(location.getLongitude());

                                        System.out.println(latt);
                                        System.out.println(longt);
                                    }
                                }
                            });
                }
            }
        });





//        final Button setItem = (Button) view.findViewById(R.id.button);
//
//        setItem.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Toast.makeText(getContext(), spinnerString, Toast.LENGTH_LONG).show();
//                // TODO server calls, open new fragment with event information.
//                // get most likely even logic here, maybe even create a new fragment on top
//                // of this one to show the result
//
//                // LOGIC
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
