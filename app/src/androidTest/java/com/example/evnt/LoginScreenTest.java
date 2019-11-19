package com.example.evnt;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    /**
     * Note that this test only works when have already logged out of the application
     * @throws Exception
     */
    @Test
    public void loginButton_logsYouIn() throws Exception {
        onView(withId(R.id.login_button)).perform(click());

        System.out.println("Putting thread to sleep for a short while to get server response");
        Thread.sleep(2000);
        onView(withId(R.id.fragment_pickevnt)).check(matches(isDisplayed()));
        System.out.println("We see a fragment from the application");

        System.out.println("Success!");
    }
}
