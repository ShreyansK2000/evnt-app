package com.example.evnt.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

// TODO see if we need to add more specific information to include in a search profile (date, time, tag)
public class PickEvntFragment extends Fragment {

    // save the result from the selected item in the spinner
    private String spinnerString;
    private Fragment ctx;
    private Context context;
    private IdentProvider ident;
    private FusedLocationProviderClient fusedLocationClient;
    private int locationPermissionGranted;
    private ServerRequestModule mServerRequestModule;

    public static PickEvntFragment newInstance(ServerRequestModule serverRequestModule) {
        PickEvntFragment fragment = new PickEvntFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("server_module", serverRequestModule);
        fragment.setArguments(bundle);

        return fragment;
    }
    /**
     * This is where we will be opening the saved state of the fragmend (if available)
     * and also passing in the serverrequestmodule to be able to fetch events from the server
     *
     * TODO need to add funcionality to save instances
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        ctx = this;
        ident = new IdentProvider(context);
        mServerRequestModule = (ServerRequestModule) getArguments().getSerializable("server_module");
//        mServerRequestModule = ServerRequestModule.getInstance(context.getApplicationContext(), ident);

        setHasOptionsMenu(true);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_pickevnt,
                container, false);

        CircleImageView imageView = view.findViewById(R.id.evnt_button);
        imageView.setOnClickListener(mainClickListener);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private View.OnClickListener mainClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            getLocation();
        }
    };

    private void getLocation() {
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
                statusCheck();
            }

        } else {
            System.out.println("reach?");
            System.out.println(fusedLocationClient);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            System.out.println("success?");
                            System.out.println(location);
                            if (location != null) {
                                System.out.println("waht the hell");
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
