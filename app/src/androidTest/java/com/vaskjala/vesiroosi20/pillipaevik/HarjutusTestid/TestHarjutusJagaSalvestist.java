package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
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
public class TestHarjutusJagaSalvestist {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestJagaSalvestist() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos4_h2_nimi), 75 * 1000);
        VajutaKodu();

        TeoseStatistikaRiba(context,"2", (125+75));
        onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).
                check(ViewAssertions.matches(hasDescendant(withText(resources.getString(R.string.test_teos4_h2_nimi)))));
        if(OnReaalneSeade()) {
            onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).
                    check(ViewAssertions.
                            matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));
        }
        VajutaKoduKui1Fragment();

        // Teose statistika kontroll
        TeosListStatistikaRiba(3, "2", (125+75));

        if(OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos4_h2_nimi))));

        StatistikaKontroll(context);

        // Oota, et link jõuaks Google Draivist Pillipäevikusse
        if(OnReaalneSeade()) {
            Oota(10000);
            ValiTeos(resources.getString(R.string.test_teos4_nimi));
            LeiaHarjutus(resources.getString(R.string.test_teos4_h2_nimi)).perform(click());
            VajutaJagaSalvestist();
            TestTooriistad.AvaGmail();
            TestTooriistad.VajutaTagasi();
            TestTooriistad.VajutaTagasi();
        }
    }
}
