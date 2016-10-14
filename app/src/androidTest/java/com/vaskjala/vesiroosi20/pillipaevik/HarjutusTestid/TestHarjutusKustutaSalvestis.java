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

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusKustutaSalvestis {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestKustutaSalvestis() {

        if(!OnReaalneSeade())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        LeiaHarjutus(resources.getString(R.string.vaikimisisharjutusekirjeldus)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));

        LeiaHarjutus(resources.getString(R.string.vaikimisisharjutusekirjeldus)).perform(click());
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        VajutaKustutaSalvestus();
        VajutaDialoogTuhista();
        VajutaKustutaSalvestus();
        VajutaDialoogOK();
        TestTooriistad.Oota(100);
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));;
        VajutaTagasiKui1Fragment();
        LeiaHarjutus(resources.getString(R.string.vaikimisisharjutusekirjeldus)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.GONE)))));

    }
}
