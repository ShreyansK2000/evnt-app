package com.example.evnt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class MyEventsFragment extends Fragment {

    View mFragment;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    private ServerRequestModule mServerRequestModule;

    public MyEventsFragment() {}

//    public static MyEventsFragment getInstance() { return new MyEventsFragment() ; }

    public static MyEventsFragment newInstance(ServerRequestModule serverRequestModule) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("server_module", serverRequestModule);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        mFragment = view;

        mViewPager = mFragment.findViewById(R.id.m_view_pager);
        mTabLayout = mFragment.findViewById(R.id.my_events_tab_layout);
        mServerRequestModule = (ServerRequestModule) getArguments().getSerializable("server_module");
        System.out.println(mServerRequestModule);


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

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager mViewPager) {
        MyEventsTabAdapter myEventsTabAdapter = new MyEventsTabAdapter(getChildFragmentManager());

        myEventsTabAdapter.addFragment(HostingEventsFragment.newInstance(mServerRequestModule), "HOSTING");
        myEventsTabAdapter.addFragment(AttendingEventsFragment.newInstance(mServerRequestModule), "ATTENDING");

        mViewPager.setAdapter(myEventsTabAdapter);

    }


}
