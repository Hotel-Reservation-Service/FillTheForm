package com.hrs.filltheformsample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hrs.filltheformcompanion.ConfigurationStatusIdlingResource;
import com.hrs.filltheformcompanion.FillTheFormCompanion;
import com.hrs.filltheformcompanion.FillTheFormCompanionException;
import com.hrs.filltheformcompanion.NumberOfProfilesIdlingResource;

import org.junit.After;
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
 * This test has a dynamic number of profiles which he gets from FillTheFormCompanion.
 * Configuration file is selected in the test itself using FillTheFormCompanion.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityDynamicTest {

    private FillTheFormCompanion companion;
    private NumberOfProfilesIdlingResource numberOfProfilesIdlingResource;
    private ConfigurationStatusIdlingResource configurationStatusIdlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        companion = new FillTheFormCompanion(context);
        registerFillTheFormCompanionIdlingResources();
        companion.configureFillTheForm(FillTheFormCompanion.SOURCE_ASSETS, "sample_app_config.xml");
        // Set FillTheForm to fast mode.
        companion.setFastMode();
    }

    @After
    public void tearDown() throws Exception {
        companion.hideFillTheFormDialog();
    }

    private void registerFillTheFormCompanionIdlingResources() {
        configurationStatusIdlingResource = new ConfigurationStatusIdlingResource(companion);
        numberOfProfilesIdlingResource = new NumberOfProfilesIdlingResource(companion);
        Espresso.registerIdlingResources(configurationStatusIdlingResource, numberOfProfilesIdlingResource);
    }

    @After
    public void unregisterFillTheFormCompanionIdlingResources() {
        Espresso.unregisterIdlingResources(configurationStatusIdlingResource, numberOfProfilesIdlingResource);
    }

    private void clickOnEveryEditTextField(boolean performLongClickOnFirstField) {
        onView(withId(R.id.first_name))
                .perform(scrollTo(), performLongClickOnFirstField ? longClick() : click());

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
    }

    @Test
    public void testWithOneProfile() {
        clickOnEveryEditTextField(true);
    }

    @Test
    public void testWithAllProfiles() throws FillTheFormCompanionException {
        // We are doing view related action to trigger waiting.
        // The test will resume when configuration file is loaded and number of profiles is available.
        onView(withId(R.id.first_name)).perform(scrollTo());

        int numberOfProfiles = companion.getNumberOfProfiles();
        if (numberOfProfiles == 0) {
            throw new FillTheFormCompanionException("No profiles available. Please configure FillTheForm.");
        }

        for (int i = 0; i < numberOfProfiles; i++) {
            clickOnEveryEditTextField(i == 0);
            companion.selectNextProfile();
        }
    }

    @Test
    public void testWithMixedProfiles() {
        for (int i = 0; i < 5; i++) {
            companion.selectNextProfile();
            onView(withId(R.id.first_name))
                    .perform(scrollTo(), i == 0 ? longClick() : click());

            onView(withId(R.id.last_name))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.email))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.city))
                    .perform(scrollTo(), click());

            onView(withId(R.id.state))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.country))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.phone))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.zip_code))
                    .perform(scrollTo(), click());

            onView(withId(R.id.device_model))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.android_version))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.device_ip_address))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
            onView(withId(R.id.comment))
                    .perform(scrollTo(), click());
            companion.selectNextProfile();
        }
    }
}