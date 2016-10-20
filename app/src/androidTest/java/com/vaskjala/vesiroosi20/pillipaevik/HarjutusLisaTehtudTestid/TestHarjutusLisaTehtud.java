package com.vaskjala.vesiroosi20.pillipaevik.HarjutusLisaTehtudTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.widget.NumberPicker;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaTehtud {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTehtud() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos3_nimi));
        VajutaLisaTehtudHarjutus();
        Calendar c0 = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance(); c1.setTime(c0.getTime()); c1.add(Calendar.MINUTE, -120);
        Calendar c2 = Calendar.getInstance(); c2.setTime(c0.getTime()); c2.add(Calendar.MINUTE, -1560);
        Calendar c3 = Calendar.getInstance(); c3.setTime(c0.getTime()); c3.add(Calendar.MINUTE, -60);
        Calendar c4 = Calendar.getInstance(); c4.setTime(c0.getTime()); c4.add(Calendar.MINUTE, 2880);

        if(OnMultiFragment()) {
            TeoseStatistikaRiba(context, "3", 2400 + 600);
            TeosListStatistikaRiba(2, "3", 2400 + 600);
            StatistikaKontroll(context);
        }
        KontrolliAjad(c0);

        onView(withId(R.id.harjutusekirjeldus))
                .perform(ViewActions.replaceText(resources.getString(R.string.test_teos3_h3_nimi)), ViewActions.closeSoftKeyboard());


        if(android.os.Build.VERSION.SDK_INT > 19) {
            SeaKellaaeg(c1, R.id.lopukellaaeg);
            VajutaDialoogTuhista();
            KontrolliAjad(c0);
        }

        SeaKellaaeg(c1, R.id.lopukellaaeg);
        VajutaDialoogOK();
        KontrolliAjad(c1);

        if(android.os.Build.VERSION.SDK_INT > 19) {
            SeaKuupaev(c2, R.id.lopukuupaev);
            VajutaDialoogTuhista();
            KontrolliAjad(c1);
        }

        SeaKuupaev(c2, R.id.lopukuupaev);
        VajutaDialoogOK();
        KontrolliAjad(c2);

        SeaKellaaeg(c0, R.id.lopukellaaeg);
        VajutaDialoogOK();

        onView(withId(R.id.pikkusminutites)).check(ViewAssertions.matches(withText("120")));

        if(OnMultiFragment()){
            LeiaHarjutus(resources.getString(R.string.test_teos3_h3_nimi)).check(ViewAssertions.matches(isDisplayed()));
        }

        if(OnMultiFragment()) {
            TeoseStatistikaRiba(context, "3", 2400 + 600 + 7200);
            TeosListStatistikaRiba(2, "3", 2400 + 600 + 7200);
            StatistikaKontroll(context);
        }

        onView(withId(R.id.pikkusminutites)).perform(click());
        onView(withClassName(Matchers.equalTo(NumberPicker.class.getName()))).perform(TestTooriistad.setNumber(110), closeSoftKeyboard());
        VajutaDialoogOK();

        if(OnMultiFragment()) {
            TeoseStatistikaRiba(context, "3", 2400 + 600 + 6600);
            TeosListStatistikaRiba(2, "3", 2400 + 600 + 6600);
            StatistikaKontroll(context);
        }

        SeaKuupaev(c4, R.id.lopukuupaev);
        VajutaDialoogOK();
        onView(withText(containsString(resources.getString(R.string.ajamuutmine_aeg_tulevikus)))).
                check(ViewAssertions.matches(isDisplayed()));
        onView(withId(android.R.id.button3)).perform(click());
        if(android.os.Build.VERSION.SDK_INT <= 19) {
            onView(withId(android.R.id.button3)).perform(click());
        }

        SeaKellaaeg(c3, R.id.lopukellaaeg);
        VajutaDialoogOK();
        onView(withId(R.id.pikkusminutites)).check(ViewAssertions.matches(withText("60")));

        if(OnMultiFragment()) {
            TeoseStatistikaRiba(context, "3", 2400 + 600 + 3600);
            TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
            StatistikaKontroll(context);
        }

        SeaKuupaev(c4, R.id.alguskuupaev);
        VajutaDialoogOK();
        onView(withText(containsString(resources.getString(R.string.ajamuutmine_aeg_tulevikus)))).
                check(ViewAssertions.matches(isDisplayed()));
        onView(withId(android.R.id.button3)).perform(click());
        if(android.os.Build.VERSION.SDK_INT <= 19) {
            onView(withId(android.R.id.button3)).perform(click());
        }

        VajutaTagasiKui1Fragment();
        TeoseStatistikaRiba(context, "3", 2400 + 600 + 3600);
        VajutaKoduKui1Fragment();
        TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
        StatistikaKontroll(context);
    }
}
