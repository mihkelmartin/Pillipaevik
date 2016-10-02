package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TeaveTest {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestAvaTeave() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.teave));
        TestTooriistad.Oota(1000);

        TestTooriistad.VajutaTagasi();
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.teave));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        TestTooriistad.VajutaKodu();
    }

}
