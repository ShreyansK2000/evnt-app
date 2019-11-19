package com.example.evnt;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MoveEventsTest {
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
    public void move_events() throws Exception {
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

        // We will differentiate between the tabs using the kinds of buttons used

        Thread.sleep(1000);
        onView(withId(R.id.evnt_list_recycler)).check(new RecyclerViewItemCountAssertion(2));
        System.out.println("Initial amount, set using current hosted events for user.");

        // Hosting tab has edit and delete buttons, so check for delete button when there are events
        onView(withIndex(withId(R.id.in_button),0)).check(matches(isDisplayed()));
        System.out.println("We see an I'm in button, we click it");
        onView(withIndex(withId(R.id.in_button),0)).perform(click());

        onView(withIndex(withId(R.id.evnt_list_recycler),0)).check(new RecyclerViewItemCountAssertion(1));
        System.out.println("When item is added to your attending events, the adapter size should be reduced by 1");

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
        System.out.println("On my events fragment");

        // Hosting tab has edit and delete buttons, so check for delete button when there are events
        onView(withIndex(withId(R.id.delete_button),0)).check(matches(isDisplayed()));
        System.out.println("We see a delete button, in hosting tab");

        // swipe left and see that details buttons can be seen ("MORE")
        onView(withIndex(withId(R.id.fragment_hosting),0)).perform(ViewActions.swipeLeft());

        Thread.sleep(1000);
        onView(withIndex(withId(R.id.evnt_list_recycler),1)).check(new RecyclerViewItemCountAssertion(2));

        onView(withIndex(withId(R.id.in_button),1)).check(matches(isDisplayed()));
        System.out.println("We see a nevermind button, in attending tab");
        onView(withIndex(withId(R.id.in_button),1)).perform(click());

        onView(withIndex(withId(R.id.evnt_list_recycler),1)).check(new RecyclerViewItemCountAssertion(1));
        System.out.println("Clicking nevermind reduces number of attending events which we checked");

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

        // We will differentiate between the tabs using the kinds of buttons used

        Thread.sleep(1000);
        onView(withId(R.id.evnt_list_recycler)).check(new RecyclerViewItemCountAssertion(2));
        System.out.println("An event was moved to attending, and an event was moved to browse!");

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

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }
}
