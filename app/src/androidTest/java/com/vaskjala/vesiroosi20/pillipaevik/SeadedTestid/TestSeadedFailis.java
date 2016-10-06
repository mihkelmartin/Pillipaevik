package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
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
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedFailis {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }


    @Test
    public void Kontrolli_Salvestumine(){
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.seadete_fail), MODE_PRIVATE);

        assertEquals(resources.getString(R.string.test_minueesnimi), sharedPref.getString("minueesnimi", ""));
        assertEquals(resources.getString(R.string.test_minuperenimi), sharedPref.getString("minuperenimi", ""));
        assertEquals(resources.getString(R.string.test_minuepost), sharedPref.getString("minuepost", ""));
        assertEquals(resources.getString(R.string.test_muusikakool), sharedPref.getString("muusikakool", ""));
        assertEquals(resources.getString(R.string.test_klass), sharedPref.getString("klass", ""));
        assertEquals(resources.getString(R.string.test_minuinstrument), sharedPref.getString("minuinstrument", ""));
        assertEquals(resources.getString(R.string.test_opetajaeesnimi), sharedPref.getString("opetajaeesnimi", ""));
        assertEquals(resources.getString(R.string.test_opetajaperenimi), sharedPref.getString("opetajaperenimi", ""));
        assertEquals(resources.getString(R.string.test_opetajaepost), sharedPref.getString("opetajaepost", ""));
        assertEquals(resources.getString(R.string.test_paevasharjutada), String.valueOf(sharedPref.getInt("paevasharjutada", 0)));
    }
}
