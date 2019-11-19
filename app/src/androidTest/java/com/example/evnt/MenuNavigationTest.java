package com.example.evnt;

import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.view.MenuItem;

import com.example.evnt.fragments.BrowseFragment;
import com.example.evnt.fragments.MyEventsFragment;
import com.example.evnt.fragments.PickEvntFragment;
import com.example.evnt.fragments.ProfileFragment;
import com.example.evnt.networking.ServerRequestModule;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static junit.framework.TestCase.assertNotNull;


/**
 * Assumes that we are already logged into the application
 * Walk through all the menu items in the bottom navigation menu
 */
@RunWith(AndroidJUnit4.class)
public class MenuNavigationTest {

    BottomNavigationView bottomNavigationMenu;
    private ServerRequestModule serverRequestModule;
    private IdentProvider ident;
    private Activity activity;
    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selected;
                    // This lifecycle is a bit suboptimal, as we're creating new fragments every time
                    switch (menuItem.getItemId()) {
                        case R.id.pick_evnt:  selected = new PickEvntFragment(); break;
                        case R.id.browse_evnt: selected = BrowseFragment.newInstance(serverRequestModule); break;
                        case R.id.my_events: selected = MyEventsFragment.newInstance(serverRequestModule); break;
                        case R.id.profile_evnt: selected = new ProfileFragment(); break;
                        default: selected = new PickEvntFragment(); break;
                    }

                    activityRule.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selected).commit();

                    return true;
                }
            };

    @Rule
    public ActivityTestRule<FragHostActivity> activityRule =
            new ActivityTestRule(FragHostActivity.class);

    @Before
    public void setUp() throws MalformedURLException {
        System.out.println("Start setup");
        final FragHostActivity activity = activityRule.getActivity();
        bottomNavigationMenu = activity.findViewById(R.id.bottom_nav);
        this.activity = activityRule.getActivity();
        ident = new IdentProvider(activity);
        ident.setValue(activity.getString(R.string.user_name), "Shreyans Kulshrestha");
        ident.setValue(activity.getString(R.string.user_email), "gshreyansk4@gmail.com");
        ident.setValue(activity.getString(R.string.fb_id), "1198569407002086");
        ident.setValue(activity.getString(R.string.profile_pic), "https://graph.facebook.com/1198569407002086/picture?width=250&height=250");
        serverRequestModule = ServerRequestModule.getInstance(activityRule.getActivity(), ident);
        System.out.println("Finished setup");
    }

    @Test
    public void loads_PickEvntScreen() {
        // make sure bottom navigation activity appears as this activity is loaded
        System.out.println("Checking some aspects of starting fragment");

        onView(withId(R.id.fragment_pickevnt)).check(matches(isDisplayed()));
        onView(withId(R.id.button)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_frame)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()));

        System.out.println("Starting fragment OK");
    }

    @Test
    public void loads_allFragments() {
        final Menu menu = bottomNavigationMenu.getMenu();
        assertNotNull("Menu should not be null", menu);

        BottomNavigationView.OnNavigationItemSelectedListener mockedListener = listener;
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mockedListener);

        System.out.println("We expect to be on pickevent fragment to start");

        /* Started on pickevent fragment*/
        assertTrue(bottomNavigationMenu.getMenu().findItem(R.id.pick_evnt).isChecked());

        // verify appropriate fragment is displayed
        onView(withId(R.id.fragment_pickevnt)).check(matches(isDisplayed()));
        System.out.println("On pick event fragment");

        System.out.println("Test bottom navigation itemview clicks");
        /* Browse Fragment */
        onView(
                allOf(
                        withId(R.id.browse_evnt),
                        isDescendantOfA(withId(R.id.bottom_nav)),
                        isDisplayed()))
                .perform(click());
        // verify correct item is checked
        assertTrue(bottomNavigationMenu.getMenu().findItem(R.id.browse_evnt).isChecked());

        // verify appropriate fragment is displayed
        onView(withId(R.id.fragment_browse_layout)).check(matches(isDisplayed()));
        System.out.println("On browse fragment");

        /* My Events Fragment */
        onView(
                allOf(
                        withId(R.id.my_events),
                        isDescendantOfA(withId(R.id.bottom_nav)),
                        isDisplayed()))
                .perform(click());
        // verify correct item is checked
        assertTrue(bottomNavigationMenu.getMenu().findItem(R.id.my_events).isChecked());

        // verify appropriate fragment is displayed
        onView(withId(R.id.fragment_myevents)).check(matches(isDisplayed()));
        System.out.println("On myEvents fragment");

        /* Profile Fragment */
        onView(
                allOf(
                        withId(R.id.profile_evnt),
                        isDescendantOfA(withId(R.id.bottom_nav)),
                        isDisplayed()))
                .perform(click());
        // verify correct item is checked
        assertTrue(bottomNavigationMenu.getMenu().findItem(R.id.profile_evnt).isChecked());

        // verify appropriate fragment is displayed
        onView(withId(R.id.fragment_profile)).check(matches(isDisplayed()));
        System.out.println("On profile fragment");

        /* Pick event Fragment */
        onView(
                allOf(
                        withId(R.id.pick_evnt),
                        isDescendantOfA(withId(R.id.bottom_nav)),
                        isDisplayed()))
                .perform(click());
        // verify correct item is checked
        assertTrue(bottomNavigationMenu.getMenu().findItem(R.id.pick_evnt).isChecked());

        // verify appropriate fragment is displayed
        onView(withId(R.id.fragment_pickevnt)).check(matches(isDisplayed()));
        System.out.println("On pickevent fragment");

        System.out.println("Success!");
    }

}

