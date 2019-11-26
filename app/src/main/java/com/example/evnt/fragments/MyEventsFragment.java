package com.example.evnt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evnt.R;
import com.example.evnt.networking.ServerRequestModule;
import com.example.evnt.adapters.MyEventsTabAdapter;
import com.google.android.material.tabs.TabLayout;

public class MyEventsFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private HostingEventsFragment hostingEventsFragment;
    private AttendingEventsFragment attendingEventsFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        View mFragment = view;

        mViewPager = mFragment.findViewById(R.id.m_view_pager);
        mTabLayout = mFragment.findViewById(R.id.my_events_tab_layout);
        ServerRequestModule mServerRequestModule = ServerRequestModule.getInstance();
        if (mServerRequestModule == null) {
            Toast.makeText(getContext(), "serverProblem", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
    }

    private void setUpViewPager(ViewPager mViewPager) {
        MyEventsTabAdapter myEventsTabAdapter = new MyEventsTabAdapter(getChildFragmentManager());

        if (attendingEventsFragment == null) {
            attendingEventsFragment = new AttendingEventsFragment();
        }

        if (hostingEventsFragment == null) {
            hostingEventsFragment = new HostingEventsFragment();
        }

        myEventsTabAdapter.addFragment(hostingEventsFragment, "HOSTING");
        myEventsTabAdapter.addFragment(attendingEventsFragment, "ATTENDING");

        mViewPager.setAdapter(myEventsTabAdapter);

    }


}
