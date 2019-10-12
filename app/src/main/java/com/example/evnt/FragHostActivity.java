package com.example.evnt;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class FragHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_host);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(listener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new PickEvntFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selected = null;
                    switch (menuItem.getItemId()) {
                        case R.id.pick_evnt:
                            selected = new PickEvntFragment();
                            break;

                        case R.id.browse_evnt:
                            selected = new BrowseFragment();
                            break;

                        case R.id.chat_evnt:
                            selected = new ChatFragment();
                            break;

                        case R.id.another_evnt:
                            selected = new OtherFragment();
                            break;

                        case R.id.profile_evnt:
                            selected = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selected).commit();

                    return true;
                }
            };
}
