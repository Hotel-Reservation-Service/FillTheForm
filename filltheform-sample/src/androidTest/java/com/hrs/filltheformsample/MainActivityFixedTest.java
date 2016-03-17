package com.hrs.filltheformsample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hrs.filltheformcompanion.FillTheFormCompanion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Simple test showing how to use FillTheFormCompanion with Espresso.
 * This test has defined a fixed number of profiles.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityFixedTest {

    private static final int NUMBER_OF_PROFILES = 3;

    private FillTheFormCompanion companion;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        companion = new FillTheFormCompanion(context);
    }

    @Test
    public void testWithFixedNumberOfProfiles() {
        companion.setFastMode();

        for (int i = 0; i < NUMBER_OF_PROFILES; i++) {

            onView(withId(R.id.first_name))
                    .perform(scrollTo(), i == 0 ? longClick() : click());

            onView(withId(R.id.last_name))
                    .perform(scrollTo(), click());

            onView(withId(R.id.email))
                    .perform(scrollTo(), click());

            onView(withId(R.id.city))
                    .perform(scrollTo(), click());

            onView(withId(R.id.state))
                    .perform(scrollTo(), click());

            onView(withId(R.id.country))
                    .perform(scrollTo(), click());

            onView(withId(R.id.phone))
                    .perform(scrollTo(), click());

            onView(withId(R.id.zip_code))
                    .perform(scrollTo(), click());

            onView(withId(R.id.device_model))
                    .perform(scrollTo(), click());

            onView(withId(R.id.android_version))
                    .perform(scrollTo(), click());

            onView(withId(R.id.device_ip_address))
                    .perform(scrollTo(), click());

            onView(withId(R.id.comment))
                    .perform(scrollTo(), click());

            companion.selectNextProfile();
        }

        companion.hideFillTheFormDialog();
    }
}