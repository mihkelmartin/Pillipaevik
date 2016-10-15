package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
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
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHarjutuseKustutamine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).perform(click());

        VajutaKustutaHarjutus();
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos4_h2_nimi))))));
        VajutaDialoogTuhista();
        VajutaKustutaHarjutus();
        VajutaDialoogOK();

        // Teose statistika kontroll teosel
        TeoseStatistikaRiba(context, "1", 125);
        HarjutusPuudub(resources.getString(R.string.test_teos4_h2_nimi));
        VajutaKoduKui1Fragment();

        // Teose statistika kontroll teoste listis
        TeosListStatistikaRiba(3, "1", 125);
        StatistikaKontroll(context);

        if(OnMultiFragment()){
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));
        }
    }
}