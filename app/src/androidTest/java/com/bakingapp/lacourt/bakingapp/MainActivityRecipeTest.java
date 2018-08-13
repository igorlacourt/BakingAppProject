package com.bakingapp.lacourt.bakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityRecipeTest {
    public static final String RECIPE_NAME = "Nutella Pie";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickGridViewItem() {

        onView(withRecyclerView(R.id.recycler_view).atPosition(0))
            .check(matches(isDisplayed()));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.tv_recipe_name))
                .check(matches(isDisplayed()))
                .check(matches(withText(RECIPE_NAME)));

        onView(withRecyclerView(R.id.recycler_view).atPosition(0))
                .perform(click());

    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

}

