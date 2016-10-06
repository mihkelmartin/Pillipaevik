package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedSisesta {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSisesta_Andmed() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        TestTooriistad.AvaSahtelValiSeaded();

        onView(withId(R.id.minueesnimi)).perform(scrollTo(),replaceText(resources.getString(R.string.test_minueesnimi)));
        onView(withId(R.id.minuperenimi)).perform(scrollTo(),replaceText(resources.getString(R.string.test_minuperenimi)));
        onView(withId(R.id.minuepost)).perform(scrollTo(),replaceText(resources.getString(R.string.test_minuepost)));
        onView(withId(R.id.muusikakool)).perform(scrollTo(),replaceText(resources.getString(R.string.test_muusikakool)));
        onView(withId(R.id.klass)).perform(scrollTo(),replaceText(resources.getString(R.string.test_klass)));
        onView(withId(R.id.minuinstrument)).perform(scrollTo(),replaceText(resources.getString(R.string.test_minuinstrument)));
        onView(withId(R.id.opetajaeesnimi)).perform(scrollTo(),replaceText(resources.getString(R.string.test_opetajaeesnimi)));
        onView(withId(R.id.opetajaperenimi)).perform(scrollTo(),replaceText(resources.getString(R.string.test_opetajaperenimi)));
        onView(withId(R.id.opetajaepost)).perform(scrollTo(),replaceText(resources.getString(R.string.test_opetajaepost)));
        onView(withId(R.id.paevasharjutada)).perform(scrollTo(),replaceText(resources.getString(R.string.test_paevasharjutada)));
        TestTooriistad.VajutaTagasi();
    }
}
