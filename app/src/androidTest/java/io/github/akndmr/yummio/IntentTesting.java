package io.github.akndmr.yummio;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static org.hamcrest.Matchers.not;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.akndmr.yummio.ui.RecipeActivity;
import io.github.akndmr.yummio.utils.ConstantsUtil;

import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class IntentTesting {

    /**
     * A JUnit {@link Rule @Rule} to init and release Espresso Intents before and after each
     * test run.
     * <p>
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link Before @Before} method.
     * <p>
     * This rule is based on {@link ActivityTestRule} and will create and launch the activity
     * for you and also expose the activity under test.
     */
    @Rule
    public IntentsTestRule<RecipeActivity> mActivityRule = new IntentsTestRule<>(
            RecipeActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void intentTest(){
        //Recyclerview click action
        onView(ViewMatchers.withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0,ViewActions.click()));

        //Check if intent (RecipeActivity to RecipeDetailsActivity) has RECIPE_INTENT_EXTRA
        intended(hasExtraWithKey(ConstantsUtil.RECIPE_INTENT_EXTRA));
    }
}
