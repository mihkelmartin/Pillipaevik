package com.vaskjala.vesiroosi20.pillipaevik.HarjutusLisaTehtudTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaTehtudKaduv {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTehtudKaduv() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos3_nimi));
        VajutaLisaTehtudHarjutus();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos3_h4_nimi)), closeSoftKeyboard());
        if(OnMultiFragment()) {
            TeoseStatistikaRiba(context, "4", 2400 + 600 + 3600);
            TeosListStatistikaRiba(2, "4", 2400 + 600 + 3600);
            StatistikaKontroll(context);
        }

        VajutaKustutaHarjutus();
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions
                        .matches(withText(containsString(resources.getString(R.string.test_teos3_h4_nimi)))));
        VajutaDialoogTuhista();
        VajutaKustutaHarjutus();
        VajutaDialoogOK();

        TeoseStatistikaRiba(context, "3", 2400 + 600 + 3600);

        if(OnMultiFragment()) {
            TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
            StatistikaKontroll(context);
        }

        VajutaLisaTehtudHarjutus();
        if(OnMultiFragment())
            LeiaHarjutus("").check(ViewAssertions.matches(isDisplayed()));

        VajutaKoduKui1Fragment();

        if(OnMultiFragment())
            LeiaHarjutus(resources.getString(R.string.test_teos3_h1_nimi)).perform(click());

        HarjutusPuudub("");

        VajutaKoduKui1Fragment();
        TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
        StatistikaKontroll(context);
    }
}
