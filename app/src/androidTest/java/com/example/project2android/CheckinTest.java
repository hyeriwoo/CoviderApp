package com.example.project2android;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.project2android.object.Building;
import com.example.project2android.object.User;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckinTest {

    @Mock
    User user;

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkinTest() throws NoSuchFieldException, IllegalAccessException {
        ViewInteraction textView = onView(
                allOf(withId(R.id.textEmail), withText("Email"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView.check(matches(withText("Email")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textPassword), withText("Password"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Password")));

        ViewInteraction button = onView(
                allOf(withId(R.id.loginButton), withText("LOG IN"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextTextEmailAddress),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextTextEmailAddress),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("kyma@usc.edu"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editTextTextPassword),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("testtest"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginButton), withText("LOG IN"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                5),
                        isDisplayed()));
        materialButton.perform(click());
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab),
                        withParent(withParent(withId(R.id.transitionFromLoginActivity))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.home), withContentDescription("Home"),
                        withParent(withParent(withId(R.id.navigationView))),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.classes), withContentDescription("Class"),
                        withParent(withParent(withId(R.id.navigationView))),
                        isDisplayed()));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.logoutBtn), withText("LOG OUT"),
                        withParent(withParent(withId(R.id.transitionFromLoginActivity))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
//
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction compoundButton = onView(
                allOf(withId(R.id.mapButton), withText("MAP"),
                        withParent(allOf(withId(R.id.toggleButtonGroup),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        compoundButton.check(matches(isDisplayed()));


        ViewInteraction compoundButton2 = onView(
                allOf(withId(R.id.listButton), withText("LIST"),
                        withParent(allOf(withId(R.id.toggleButtonGroup),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        compoundButton2.check(matches(isDisplayed()));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.mapButton), withText("Map"),
                        childAtPosition(
                                allOf(withId(R.id.toggleButtonGroup),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction view = onView(
                allOf(withContentDescription("Google Map"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

//        Instantiation
        List<Building> buildings = new ArrayList<>();
        List<Bundle> buildingBundles = new ArrayList<>();
        DashboardListFragment dashboardListFragment = new DashboardListFragment();
        List<Building> buildingsInSchedule = new ArrayList<>();

//        Setup for test
        ArrayList<String> hoursOfOperation = new ArrayList<>();
        hoursOfOperation.add("9am-5pm");
        Building building = new Building("test",
                "test", 0, 25,
                new ArrayList<String>(),
                new HashMap<String, String>(),
                hoursOfOperation,
                0.0, 0.0);
        buildings.add(building);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        buildingBundles.add(bundle);
        Field buildingsField = dashboardListFragment
                .getClass().getDeclaredField("buildings");
        buildingsField.setAccessible(true);
        buildingsField.set(dashboardListFragment, buildings);

        Field buildingBundlesField = dashboardListFragment
                .getClass().getDeclaredField("buildingBundles");
        buildingBundlesField.setAccessible(true);
        buildingBundlesField.set(dashboardListFragment, buildingBundles);
        Field buildingsInScheduleField = dashboardListFragment
                .getClass().getDeclaredField("buildingsInSchedule");
        buildingsInScheduleField.setAccessible(true);
        buildingsInScheduleField.set(dashboardListFragment, buildingsInSchedule);


        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.listButton), withText("List"),
                        childAtPosition(
                                allOf(withId(R.id.toggleButtonGroup),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.buildingLayout),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.buildingRecycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        MyViewAction.clickChildViewWithId(R.id.expand_collapse)));

//        ViewInteraction appCompatImageButton = onView(
//                allOf(withId(R.id.expand_collapse), withContentDescription("When clicked, the list will expand or collapse"),
//                        childAtPosition(
//                                allOf(withId(R.id.fixed_building_layout),
//                                        childAtPosition(
//                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
//                                                0)),
//                                1),
//                        isDisplayed()));
//        appCompatImageButton.perform(click());

//        ViewInteraction linearLayout2 = onView(
//                allOf(withId(R.id.hidden_building_view),
//                        withParent(withParent(withId(R.id.base_building_card))),
//                        isDisplayed()));
//        linearLayout2.check(matches(isDisplayed()));

//        ViewInteraction textView4 = onView(
//                allOf(withId(R.id.hidden_hour_of_operations), withText("OPEN: 9am-5pm"),
//                        withParent(allOf(withId(R.id.hidden_building_view),
//                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
//                        isDisplayed()));
//        textView4.check(matches(isDisplayed()));
//
//        ViewInteraction textView6 = onView(
//                allOf(withId(R.id.hidden_risk_level), withText("Risk Level: Low"),
//                        withParent(allOf(withId(R.id.hidden_building_view),
//                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
//                        isDisplayed()));
//        textView6.check(matches(isDisplayed()));

//        ViewInteraction button3 = onView(
//                allOf(withId(R.id.proceed_button), withText("PROCEED"),
//                        withParent(allOf(withId(R.id.hidden_building_view),
//                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
//                        isDisplayed()));
//        button3.check(matches(isDisplayed()));

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.buildingRecycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        MyViewAction.clickChildViewWithId(R.id.proceed_button)));

//        ViewInteraction materialButton6 = onView(
//                allOf(withId(R.id.proceed_button), withText("Proceed"),
//                        childAtPosition(
//                                allOf(withId(R.id.hidden_building_view),
//                                        childAtPosition(
//                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
//                                                1)),
//                                3),
//                        isDisplayed()));
//        materialButton6.perform(click());

        ViewInteraction linearLayout3 = onView(
                allOf(withParent(allOf(withId(R.id.entryreq_scrollview), withContentDescription("Building Information"),
                        withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.checkInButton), withText("CHECK IN"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));


        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.checkInButton), withText("CHECK IN"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                0)));
        materialButton7.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
    static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)",
                                    new Object[] { Integer.valueOf
                                            (recyclerViewId) });
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        }
                        else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }

                }
            };
        }
    }
}
