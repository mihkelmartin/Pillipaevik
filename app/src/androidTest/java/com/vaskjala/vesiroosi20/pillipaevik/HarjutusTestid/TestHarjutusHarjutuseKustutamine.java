package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestHarjutusHarjutuseKustutamine {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHarjutuseKustutamine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
            perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos4_nimi))), click()));
        onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).perform(click());

        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos4_h2_nimi))))));
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // Teose statistika kontroll teosel
        TestTooriistad.TeoseStatistikaRiba(context, "1", 125);
        onView(withText(resources.getString(R.string.test_teos4_h2_nimi))).check(ViewAssertions.doesNotExist());

        TestTooriistad.VajutaKoduKui1Fragment();

        // Teose statistika kontroll teoste listis
        TestTooriistad.TeosListStatistikaRiba(3, "1", 125);

        TestTooriistad.StatistikaKontroll(context);

        if(TestTooriistad.OnMultiFragment()){
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));
        }
    }
}