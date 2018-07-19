package io.github.akndmr.yummio.ui;


import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.akndmr.yummio.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTabletTest {

    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);

    // This test only works with tablets
    @Test
    public void recipeActivityTabletTest() {
        onView(ViewMatchers.withId(R.id.recipe_tablet)).check(matches(isDisplayed()));
    }

}
