package com.example.project2android;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.project2android.adaptor.CourseAdaptor;
import com.example.project2android.dashboardTestActivity.BlankDashboardActivity;
import com.example.project2android.object.Building;
import com.example.project2android.object.User;
import com.google.android.material.button.MaterialButton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashboardListFragmentTest {
    private DashboardFragment dashboardFragment;
    private DashboardListFragment dashboardListFragment;

    private ArrayList<Building> buildings;
    private ArrayList<Bundle> buildingBundles;
    private ArrayList<Building> buildingsInSchedule;

    @Mock
    User user;

    @Rule
    public ActivityScenarioRule<BlankDashboardActivity> activityActivityScenarioRule =
            new ActivityScenarioRule<>(BlankDashboardActivity.class);

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        dashboardListFragment = new DashboardListFragment();
        buildings = new ArrayList<>();
        buildingBundles = new ArrayList<>();
        buildingsInSchedule = new ArrayList<>();

        activityActivityScenarioRule.getScenario().onActivity(activity -> {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            dashboardFragment = new DashboardFragment();
            fragmentTransaction.replace(R.id.blankDashboardFrame, dashboardFragment);
            fragmentTransaction.commit();
        });
    }

    @Test
    public void testEmptyList() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        buildingBundles.add(bundle);

        setListFragment();

        onView(withId(R.id.listButton)).perform(click())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.dashboard_display)).check(matches(hasChildCount(1)));
    }

    @Test
    public void testList() {
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

        setListFragment();

        onView(withId(R.id.listButton)).perform(click())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList_InSchedule() {
        ArrayList<String> hoursOfOperation = new ArrayList<>();
        hoursOfOperation.add("9am-5pm");
        Building building = new Building("test",
                "test", 0, 25,
                new ArrayList<String>(),
                new HashMap<String, String>(),
                hoursOfOperation,
                0.0, 0.0);
        buildings.add(building);
        buildingsInSchedule.add(building);

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        buildingBundles.add(bundle);

        setListFragment();

        onView(withId(R.id.listButton)).perform(click())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList_FreqVisited() {
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

        when(user.isFrequentlyVisited(building)).thenReturn(true);

        setListFragment();

        onView(withId(R.id.listButton)).perform(click())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList_Expand_Collapse() {
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

        setListFragment();

        onView(withId(R.id.listButton)).perform(click())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);

            onView(withId(R.id.buildingRecycler))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            MyViewAction.clickChildViewWithId(R.id.expand_collapse)));


            Thread.sleep(2000);

            onView(withId(R.id.building_name_header)).check(matches(isDisplayed()));
            onView(withId(R.id.expand_collapse)).check(matches(isDisplayed()));
            onView(withId(R.id.hidden_building_view)).check(matches(isDisplayed()));
            onView(withId(R.id.hidden_hour_of_operations)).check(matches(isDisplayed()));
            onView(withId(R.id.hidden_risk_level)).check(matches(isDisplayed()));
            onView(withId(R.id.proceed_button)).check(matches(isDisplayed()));

            Thread.sleep(2000);

            onView(withId(R.id.buildingRecycler))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            MyViewAction.clickChildViewWithId(R.id.expand_collapse)));

            Thread.sleep(2000);

            onView(withId(R.id.building_name_header)).check(matches(isDisplayed()));
            onView(withId(R.id.expand_collapse)).check(matches(isDisplayed()));
            onView(withId(R.id.hidden_building_view)).check(matches(not(isDisplayed())));
            onView(withId(R.id.hidden_hour_of_operations)).check(matches(not(isDisplayed())));
            onView(withId(R.id.hidden_risk_level)).check(matches(not(isDisplayed())));
            onView(withId(R.id.proceed_button)).check(matches(not(isDisplayed())));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setBundlesField() throws NoSuchFieldException, IllegalAccessException {
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
    }

    private void setListFragment() {
        activityActivityScenarioRule.getScenario().onActivity(activity -> {
            MaterialButton listButton =
                    (MaterialButton)activity.findViewById(R.id.listButton);
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashboardListFragment =
                            new DashboardListFragment();
                    try {
                        setBundlesField();
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.dashboard_display, dashboardListFragment);
                    fragmentTransaction.commit();
                }
            });
        });
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
