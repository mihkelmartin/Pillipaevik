package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaTehtudKaduv {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTehtudKaduv() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos2_nimi))), click()).atPosition(0));
        onView(withId(R.id.lisatehtud)).perform(click());
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos2_h4_nimi)), closeSoftKeyboard());
        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeoseStatistikaRiba(context, "4", 300 + 900 + 3600);
            TestTooriistad.TeosListStatistikaRiba(1, "4", 300 + 900 + 3600);
            TestTooriistad.StatistikaKontroll(context);
        }

        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions
                        .matches(withText(containsString(resources.getString(R.string.test_teos2_h4_nimi)))));
        onView(withId(android.R.id.button2)).perform(click());

        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        TestTooriistad.TeoseStatistikaRiba(context, "3", 300 + 900 + 3600);

        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeosListStatistikaRiba(1, "3", 300 + 900 + 3600);
            TestTooriistad.StatistikaKontroll(context);
        }

        onView(withId(R.id.lisatehtud)).perform(click());
        if(TestTooriistad.OnMultiFragment())
            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText(""))))
                    .check(ViewAssertions.matches(isDisplayed()));

        TestTooriistad.VajutaKoduKui1Fragment();

        if(TestTooriistad.OnMultiFragment())
            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText("")))).perform(click());

        onView(Matchers.allOf(withId(R.id.harjutuslistrida), withText(""))).check(ViewAssertions.doesNotExist());

        TestTooriistad.VajutaKoduKui1Fragment();
        TestTooriistad.TeosListStatistikaRiba(1, "3", 300 + 900 + 3600);
        TestTooriistad.StatistikaKontroll(context);

    }
}
