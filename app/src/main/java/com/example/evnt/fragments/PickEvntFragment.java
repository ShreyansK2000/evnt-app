package com.example.evnt.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

// TODO see if we need to add more specific information to include in a search profile (date, time, tag)
public class PickEvntFragment extends Fragment {

    // save the result from the selected item in the spinner
    private String spinnerString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Fragment needs its root view before we can actually do stuff
        final View view = inflater.inflate(R.layout.fragment_pickevnt,
                container, false);

        final Button setItem = (Button) view.findViewById(R.id.button);

        setItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getContext(), spinnerString, Toast.LENGTH_LONG).show();
                // TODO server calls, open new fragment with event information.
                // get most likely even logic here, maybe even create a new fragment on top
                // of this one to show the result

                // LOGIC
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner eventSpinner = (Spinner) view.findViewById(R.id.spinner);

        // list of attributes we can choose from
        String[] options = {"party", "relax", "hang out", "eat"};

        // logic for drop down list choice
        ArrayAdapter adapter = new ArrayAdapter(
                getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        eventSpinner.setAdapter(adapter);

        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerString = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // default string selected is options[0], don't have to put anything here
            }
        });

    }
}
