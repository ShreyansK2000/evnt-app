package com.example.evnt;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.evnt.fragments.BrowseFragment;
import com.example.evnt.fragments.MyEventsFragment;
import com.example.evnt.fragments.PickEvntFragment;
import com.example.evnt.fragments.ProfileFragment;
import com.example.evnt.networking.ServerRequestModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.AllOf.allOf;


/**
 * Assumes that we are already logged into the application
 * Try to open the event details dialog and close it
 */
@RunWith(AndroidJUnit4.class)
public class EventDetailsTest {
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
    public void opens_details_dialog() {
        BottomNavigationView.OnNavigationItemSelectedListener mockedListener = listener;
        bottomNavigationMenu.setOnNavigationItemSelectedListener(mockedListener);

        System.out.println("Switching to browse events fragment");
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
        System.out.println("On Browse Fragment");

        System.out.println("Opening the details dialog for first event in browse events list");
        // Open the dialog and check that it appears
        onView(withIndex(withId(R.id.details_button), 0)).perform(click());
        onView(withId(R.id.details_dialog)).check(matches(isDisplayed()));

        System.out.println("Closing the details dialog for first event in browse events list");
        // press the close button (x) and check that it vanishes
        onView(withId(R.id.close_button)).perform(click());
        onView(withId(R.id.details_dialog)).check(doesNotExist());

        System.out.println("Success!");
    }

    /**
     * Stolen this code from stackoverflow
     * Credit to second answer at
     * https://stackoverflow.com/questions/29378552/in-espresso-how-to-avoid-ambiguousviewmatcherexception-when-multiple-views-matc
     *
     * @param matcher
     * @param index
     * @return
     */
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }


}


