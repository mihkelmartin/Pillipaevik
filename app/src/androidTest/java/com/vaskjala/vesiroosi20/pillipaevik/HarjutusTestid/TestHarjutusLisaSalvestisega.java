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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaSalvestisega {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaSalvestisega() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        LisaUusHarjutusSalvestisega("", 25 * 1000);
        VajutaKodu();

        TeoseStatistikaRiba(context, "1", 25);
        onView(withId(R.id.harjutuslist_harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));

        if(OnReaalneSeade())
            onView(withId(R.id.harjutuslisti_pilt)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));

        VajutaKoduKui1Fragment();

        // Teose statistika kontroll
        TeosListStatistikaRiba(3, "1", 25);

        if(OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));

        StatistikaKontroll(context);

    }
}
