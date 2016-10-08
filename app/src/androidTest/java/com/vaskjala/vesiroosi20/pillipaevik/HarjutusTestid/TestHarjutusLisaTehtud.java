package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.widget.NumberPicker;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
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
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTehtud() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos3_nimi))), click()).atPosition(0));
        onView(withId(R.id.lisatehtud)).perform(click());
        Calendar c0 = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance(); c1.setTime(c0.getTime()); c1.add(Calendar.MINUTE, -120);
        Calendar c2 = Calendar.getInstance(); c2.setTime(c0.getTime()); c2.add(Calendar.MINUTE, -1560);
        Calendar c3 = Calendar.getInstance(); c3.setTime(c0.getTime()); c3.add(Calendar.MINUTE, -60);
        Calendar c4 = Calendar.getInstance(); c4.setTime(c0.getTime()); c4.add(Calendar.MINUTE, 2880);

        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeoseStatistikaRiba(context, "3", 2400 + 600);
            TestTooriistad.TeosListStatistikaRiba(2, "3", 2400 + 600);
            TestTooriistad.StatistikaKontroll(context);
        }
        TestTooriistad.KontrolliAjad(c0);

        onView(withId(R.id.harjutusekirjeldus))
                .perform(ViewActions.replaceText(resources.getString(R.string.test_teos3_h3_nimi)), ViewActions.closeSoftKeyboard());


        TestTooriistad.SeaKellaaeg(c1, R.id.lopukellaaeg);
        onView(withId(android.R.id.button2)).perform(click());
        TestTooriistad.KontrolliAjad(c0);

        TestTooriistad.SeaKellaaeg(c1, R.id.lopukellaaeg);
        onView(withId(android.R.id.button1)).perform(click());
        TestTooriistad.KontrolliAjad(c1);

        TestTooriistad.SeaKuupaev(c2, R.id.lopukuupaev);
        onView(withId(android.R.id.button2)).perform(click());
        TestTooriistad.KontrolliAjad(c1);

        TestTooriistad.SeaKuupaev(c2, R.id.lopukuupaev);
        onView(withId(android.R.id.button1)).perform(click());
        TestTooriistad.KontrolliAjad(c2);

        TestTooriistad.SeaKellaaeg(c0, R.id.lopukellaaeg);
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.pikkusminutites)).check(ViewAssertions.matches(withText("120")));

        if(TestTooriistad.OnMultiFragment()){
            onView(withId(R.id.harjutuslist))
                    .check(ViewAssertions.
                            matches(hasDescendant(allOf(withId(R.id.harjutuslist_harjutusekirjeldus), withText(resources.getString(R.string.test_teos3_h3_nimi))))));
        }

        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeoseStatistikaRiba(context, "3", 2400 + 600 + 7200);
            TestTooriistad.TeosListStatistikaRiba(2, "3", 2400 + 600 + 7200);
            TestTooriistad.StatistikaKontroll(context);
        }


        onView(withId(R.id.pikkusminutites)).perform(click());
        onView(withClassName(Matchers.equalTo(NumberPicker.class.getName()))).perform(TestTooriistad.setNumber(110));
        onView(withId(android.R.id.button1)).perform(click());

        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeoseStatistikaRiba(context, "3", 2400 + 600 + 6600);
            TestTooriistad.TeosListStatistikaRiba(2, "3", 2400 + 600 + 6600);
            TestTooriistad.StatistikaKontroll(context);
        }

        TestTooriistad.SeaKuupaev(c4, R.id.lopukuupaev);
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText(containsString(resources.getString(R.string.ajamuutmine_aeg_tulevikus)))).
                check(ViewAssertions.matches(isDisplayed()));
        onView(withId(android.R.id.button3)).perform(click());

        TestTooriistad.SeaKellaaeg(c3, R.id.lopukellaaeg);
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.pikkusminutites)).check(ViewAssertions.matches(withText("60")));

        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeoseStatistikaRiba(context, "3", 2400 + 600 + 3600);
            TestTooriistad.TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
            TestTooriistad.StatistikaKontroll(context);
        }

        TestTooriistad.SeaKuupaev(c4, R.id.alguskuupaev);
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText(containsString(resources.getString(R.string.ajamuutmine_aeg_tulevikus)))).
                check(ViewAssertions.matches(isDisplayed()));
        onView(withId(android.R.id.button3)).perform(click());

        TestTooriistad.VajutaTagasiKui1Fragment();
        TestTooriistad.TeoseStatistikaRiba(context, "3", 2400 + 600 + 3600);
        TestTooriistad.VajutaKoduKui1Fragment();
        TestTooriistad.TeosListStatistikaRiba(2, "3", 2400 + 600 + 3600);
        TestTooriistad.StatistikaKontroll(context);
    }
}
