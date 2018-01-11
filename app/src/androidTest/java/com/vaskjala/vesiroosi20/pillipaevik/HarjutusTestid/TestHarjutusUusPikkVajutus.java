package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad.*;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestHarjutusUusPikkVajutus {

    private boolean bpikkVajutusAlustabHarjutuse;
    private boolean bAutoStart;

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
        bpikkVajutusAlustabHarjutuse = kaspikkVajutusAlustabHarjutuse(InstrumentationRegistry.getTargetContext());
        bAutoStart = kasStopperiAutoStart(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void TestHarjutusUusPikkVajutus() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SeadistapikkVajutusAlustabHarjutuseSeadeteFailis(context, true);
        SeadistaStopperiAutoStartSeadeteFailis(context, true);

        if(OnMultiFragment()) {
            ValiTeos(resources.getString(R.string.test_teos3_nimi));
            onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
            onView(allOf(withId(R.id.nimi), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        }

        ValiTeosPikkVajutus(resources.getString(R.string.test_teos4_nimi));
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_h_pikk_vajutus)), closeSoftKeyboard());
        Oota(2*1000);
        OnUuelHarjutuselKestus();
        VajutaTagasi();

        if(OnMultiFragment()) {
            onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));
            onView(allOf(withId(R.id.nimi), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));
            LeiaHarjutus(resources.getString(R.string.test_h_pikk_vajutus)).check(ViewAssertions.matches(isDisplayed()));
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_h_pikk_vajutus))));
        }
        TeosListStatistikaRiba(3, "3", (25+40));

    }
    @After
    public void Lopeta_Test() {
        SeadistapikkVajutusAlustabHarjutuseSeadeteFailis(InstrumentationRegistry.getTargetContext(), bpikkVajutusAlustabHarjutuse);
        SeadistaStopperiAutoStartSeadeteFailis(InstrumentationRegistry.getTargetContext(), bAutoStart);
    }
}
