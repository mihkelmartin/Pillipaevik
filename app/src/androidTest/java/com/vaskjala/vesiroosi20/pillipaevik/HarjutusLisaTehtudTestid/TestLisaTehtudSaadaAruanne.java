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
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestLisaTehtudSaadaAruanne {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }


    @Test
    public void TestLisaTehtudSaadaAruanne() {
        if(TestTooriistad.OnMultiFragment()) {
            Context context = InstrumentationRegistry.getTargetContext();
            Resources resources = context.getResources();

            onView(withId(R.id.harjutua_list)).
                    perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos3_nimi))), click()).atPosition(0));
            onView(withId(R.id.lisatehtud)).perform(click());

            onView(withId(R.id.harjutuseandmed)).check(ViewAssertions.matches(isDisplayed()));

            onView(withId(R.id.harjutusekirjeldus))
                    .perform(ViewActions.replaceText(resources.getString(R.string.test_teos3_h7_nimi)), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.harjutuseandmed)).check(ViewAssertions.matches(isDisplayed()));

            Calendar c0 = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance(); c1.setTime(c0.getTime()); c1.add(Calendar.MINUTE, -120);
            TestTooriistad.SeaKellaaeg(c1, R.id.alguskellaaeg);
            onView(withId(android.R.id.button1)).perform(click());

            TestTooriistad.AvaSahtelValiAruanne();

            onView(withText(containsString(resources.getString(R.string.vali_aruande_kuu)))).
                    check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.vali_aruande_kuu))))));

            List<String> mKuud  = Tooriistad.LooAruandeKuud(resources.getInteger(R.integer.kuudearv));
            onView(withText(mKuud.get(0))).perform(click());
            TestTooriistad.AvaGmail();
            TestTooriistad.VajutaTagasi();
            TestTooriistad.VajutaTagasi();

            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText(resources.getString(R.string.test_teos3_h7_nimi))))).perform(click());
            onView(withId(R.id.harjutuseandmed)).check(ViewAssertions.matches(isDisplayed()));
            
            onView(withId(R.id.kustutaharjutus)).perform(click());
            onView(withId(android.R.id.button1)).perform(click());

        }
    }
}
