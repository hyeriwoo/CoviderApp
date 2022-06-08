package com.example.project2android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import androidx.test.rule.ActivityTestRule;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReportActivityTest {
    @Rule
    public ActivityScenarioRule<ReportActivity> activityRule =
            new ActivityScenarioRule<>(ReportActivity.class);

    @Test
    public void symptomsDisplay() {
        onView(withText("Do you have any of the following symptoms?")).check(matches(isDisplayed()));
        onView(withText("• Fever or chills")).check(matches(isDisplayed()));
        onView(withText("• Cough")).check(matches(isDisplayed()));
        onView(withText("• Shortness of breath")).check(matches(isDisplayed()));
        onView(withText("• Fatigue")).check(matches(isDisplayed()));
        onView(withText("• Muscle or body aches")).check(matches(isDisplayed()));
        onView(withText("• Headache")).check(matches(isDisplayed()));
        onView(withText("• New loss of taste or smell")).check(matches(isDisplayed()));
        onView(withText("• Sore throat")).check(matches(isDisplayed()));
        onView(withText("• Congestion or runny nose")).check(matches(isDisplayed()));
        onView(withText("• Nausea or vomiting")).check(matches(isDisplayed()));
        onView(withText("• Diarrhea")).check(matches(isDisplayed()));
        onView(withText("Did you take a covid test?")).check(matches(isDisplayed()));
    }

    @Test
    public void buttonsDisplay(){
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        onView(withId(R.id.submitButton)).check(matches(isDisplayed()));
    }

    @Test
    public void radioButtons(){
        onView(withId(R.id.covidTestRadio)).check(matches(isDisplayed()));
        onView(withId(R.id.covid_result_radio_group)).check(matches(not(isDisplayed())));
        onView(withId(R.id.symptomsGroup)).check(matches(isDisplayed()));
        onView(withId(R.id.yesCovid)).perform(click());
        onView(withId(R.id.covid_result_radio_group)).check(matches(isDisplayed()));
        onView(withId(R.id.noCovid)).perform(click());
        onView(withId(R.id.covid_result_radio_group)).check(matches(not(isDisplayed())));
    }

//    @Test
//    public void testSubmit(){
//        onView(withId(R.id.submitButton)).perform(click());
//        onView(withId(R.id.yesCovid)).perform(click());
//        onView(withId(R.id.radioButton8)).perform(click());
//        onView(withId(R.id.radioButton8)).perform(click());
//        onView(withId(R.id.radioButton10)).perform(click());
//        onView(withId(R.id.transitionFromLoginActivity)).check(matches(isDisplayed()));
//    }
//    @Test
//    public void testCancel(){
//        onView(withId(R.id.reportActivityLayout)).check(matches(isDisplayed()));
//        onView(withId(R.id.cancelButton)).perform(click());
//        onView(withId(R.id.reportActivityLayout)).check(matches(not(isDisplayed())));
//    }

}