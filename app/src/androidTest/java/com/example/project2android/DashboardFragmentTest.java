package com.example.project2android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.PositionAssertions.isLeftOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotSelected;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.project2android.dashboardTestActivity.BlankDashboardActivity;
import com.example.project2android.manager.DashboardManager;
import com.google.android.material.button.MaterialButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashboardFragmentTest {

    private DashboardFragment dashboardFragment;

    private MaterialButton materialButton;

    @Rule
    public ActivityScenarioRule<BlankDashboardActivity> activityActivityScenarioRule =
            new ActivityScenarioRule<>(BlankDashboardActivity.class);

    @Before
    public void setup() {
        activityActivityScenarioRule.getScenario().onActivity(activity -> {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            dashboardFragment = new DashboardFragment();
            fragmentTransaction.replace(R.id.blankDashboardFrame, dashboardFragment);
            fragmentTransaction.commit();
        });
    }

    @Test
    public void testMap_ButtonClicked() {
        // ensure firebase is called
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        activityActivityScenarioRule.getScenario().onActivity(activity -> {
            materialButton = (MaterialButton)activity.findViewById(R.id.mapButton);

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        });

        onView(withId(R.id.mapButton)).perform(click())
                .check(matches(isChecked()));
    }

    @Test
    public void testInitialButtonGroup() {
        onView(withId(R.id.toggleButtonGroup)).check(matches(isNotSelected()));
        onView(withId(R.id.mapButton)).check(matches(isNotSelected()));
        onView(withId(R.id.listButton)).check(matches(isNotSelected()));

        onView(withId(R.id.toggleButtonGroup)).check(matches(withChild(withId(R.id.mapButton))));
        onView(withId(R.id.toggleButtonGroup)).check(matches(withChild(withId(R.id.listButton))));
    }

    @Test
    public void testPositions() {
        onView(withId(R.id.toggleButtonGroup)).check(isCompletelyAbove(withId(R.id.dashboard_display)));
    }

    @Test
    public void testButtonTexts() {
        onView(withId(R.id.mapButton)).check(matches(withText("Map")));
        onView(withId(R.id.listButton)).check(matches(withText("List")));
    }

    @Test
    public void testMap() {
        onView(withId(R.id.mapButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testList() {
        onView(withId(R.id.listButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testList_ButtonClicked() {
        // ensure firebase is called
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // override original on click listener to avoid dynamically
        // building the list because we just want to test the button
        activityActivityScenarioRule.getScenario().onActivity(activity -> {
            materialButton = (MaterialButton)activity.findViewById(R.id.listButton);

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        });

        onView(withId(R.id.listButton)).perform(click());
    }
}
