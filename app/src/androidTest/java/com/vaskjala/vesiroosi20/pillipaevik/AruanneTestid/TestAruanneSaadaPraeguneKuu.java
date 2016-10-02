package com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestAruanneSaadaPraeguneKuu {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSaadaPraeguneKuu() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        TestTooriistad.AvaSahtelValiAruanne();

        onView(withText(containsString(resources.getString(R.string.vali_aruande_kuu)))).
                check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.vali_aruande_kuu))))));

        List<String> mKuud  = Tooriistad.LooAruandeKuud(resources.getInteger(R.integer.kuudearv));
        onView(withText(mKuud.get(0))).perform(click());
        TestTooriistad.AvaGmail();
        TestTooriistad.VajutaTagasi();
        TestTooriistad.VajutaTagasi();
    }
}