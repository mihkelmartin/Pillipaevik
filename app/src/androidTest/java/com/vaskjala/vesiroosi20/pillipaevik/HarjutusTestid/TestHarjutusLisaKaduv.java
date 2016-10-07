package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
public class TestHarjutusLisaKaduv {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaKaduv() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos4_nimi))), click()).atPosition(0));

        onView(withId(R.id.alustauut)).perform(click());
        TestTooriistad.VajutaKodu();
        KontrolliTulemust(context);

        onView(withId(R.id.alustauut)).perform(click());
        onView(withId(R.id.harjutusuusfragment)).perform(ViewActions.closeSoftKeyboard());
        TestTooriistad.VajutaTagasi();
        KontrolliTulemust(context);

        onView(withId(R.id.alustauut)).perform(click());
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        TestTooriistad.VajutaKodu();
        KontrolliTulemust(context);

        onView(withId(R.id.alustauut)).perform(click());
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        TestTooriistad.VajutaTagasi();
        KontrolliTulemust(context);

        // Kustuta
        onView(withId(R.id.alustauut)).perform(click());
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos4_h3_nimi))))));
        onView(withId(android.R.id.button1)).perform(click());
        KontrolliTulemust(context);
    }

    private void KontrolliTulemust(Context context){
        onView(allOf(withId(R.id.harjutuslist), hasDescendant(withText("")))).check(ViewAssertions.doesNotExist());
        TestTooriistad.TeoseStatistikaRiba(context, "1", 125);
        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeosListStatistikaRiba(3, "1", 125);
            TestTooriistad.StatistikaKontroll(context);
        }
    }
}
