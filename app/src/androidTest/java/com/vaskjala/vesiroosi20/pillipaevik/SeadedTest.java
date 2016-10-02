package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
public class SeadedTest {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSisesta_Andmed() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.seaded));
        TestTooriistad.Oota(1000);

        onView(withId(R.id.minueesnimi)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_minueesnimi)));
        onView(withId(R.id.minuperenimi)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_minuperenimi)));
        onView(withId(R.id.minuepost)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_minuepost)));
        onView(withId(R.id.muusikakool)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_muusikakool)));
        onView(withId(R.id.klass)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_klass)));
        onView(withId(R.id.minuinstrument)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_minuinstrument)));
        onView(withId(R.id.opetajaeesnimi)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_opetajaeesnimi)));
        onView(withId(R.id.opetajaperenimi)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_opetajaperenimi)));
        onView(withId(R.id.opetajaepost)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_opetajaepost)));
        onView(withId(R.id.paevasharjutada)).perform(scrollTo(),replaceText(mActivityRule.getActivity().getString(R.string.test_paevasharjutada)));
        TestTooriistad.VajutaTagasi();
    }

    @Test
    public void Kontrolli_Salvestumine(){
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);

        // TODO Kas on parem assert meetod ?
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
