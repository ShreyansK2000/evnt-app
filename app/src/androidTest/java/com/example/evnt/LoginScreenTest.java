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

@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginButton_logsYouIn() throws Exception {
        onView(withId(R.id.login_button)).perform(click());

        onView(withId(R.layout.fragment_pickevnt)).check(matches(isDisplayed()));
    }
}
