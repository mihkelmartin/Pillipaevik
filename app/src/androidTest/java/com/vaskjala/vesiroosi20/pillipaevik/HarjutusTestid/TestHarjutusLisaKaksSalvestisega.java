package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaKaksSalvestisega {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaKaksSalvestisega() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        // Lisa I
        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos4_h2_nimi), 40 * 1000);
        VajutaTagasi();

        TeoseStatistikaRiba(context, "2", (25+40));
        LeiaHarjutus(resources.getString(R.string.test_teos4_h2_nimi)).check(ViewAssertions.matches(isDisplayed()));
        if(OnReaalneSeade()) {
            LeiaHarjutus(resources.getString(R.string.test_teos4_h2_nimi)).
                    check(ViewAssertions.
                            matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));
        }
        VajutaKoduKui1Fragment();

        // Teoslisti statistika kontroll
        TeosListStatistikaRiba(3, "2", (25+40));
        StatistikaKontroll(context);

        if(OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos4_h2_nimi))));

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        // Lisa II
        LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos4_h3_nimi), 10 * 1000);
        VajutaTagasi();

        TeoseStatistikaRiba(context, "3", (25+40+10));
        LeiaHarjutus(resources.getString(R.string.test_teos4_h3_nimi)).check(ViewAssertions.matches(isDisplayed()));
        if(OnReaalneSeade()) {
            LeiaHarjutus(resources.getString(R.string.test_teos4_h3_nimi)).
                    check(ViewAssertions.
                            matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));
        }
        VajutaKoduKui1Fragment();

        // Teoslisti statistika kontroll
        TeosListStatistikaRiba(3, "3", (25+40+10));
        StatistikaKontroll(context);

        if(OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos4_h3_nimi))));

    }
}
