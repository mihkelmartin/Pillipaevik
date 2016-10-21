package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;

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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestKalenderHarjutuseKustutamine {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHarjutuseKustutamine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        // Vali harjutus mille hiljem kustutame
        if(OnMultiFragment()){
            ValiTeos(resources.getString(R.string.test_teos2_nimi));
            onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).perform(click());
        }

        AvaSahtelValiKalender();

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.kalendri_tabel))
                .perform(RecyclerViewActions
                        .actionOnItem(hasDescendant(withText(containsString(resources.getString(R.string.test_teos2_h3_nimi)))), click()));
        VajutaKustutaHarjutus();
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus)))).
                check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos2_h3_nimi))))));
        VajutaDialoogTuhista();
        VajutaKustutaHarjutus();
        VajutaDialoogOK();

        KalendriStatistikaKontroll("4", "01:10");
        VajutaKodu();
        TeosListStatistikaRiba(1, "2", 1200);
        StatistikaKontroll(context);

        if(OnMultiFragment()){
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos2_h2_nimi))));

            // Liigu väheke teoste vahel
            ValiTeos(resources.getString(R.string.test_teos1_nimi));
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos1_h3_nimi))));
            ValiTeos(resources.getString(R.string.test_teos3_nimi));
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos3_h2_nimi))));
        }
    }
}