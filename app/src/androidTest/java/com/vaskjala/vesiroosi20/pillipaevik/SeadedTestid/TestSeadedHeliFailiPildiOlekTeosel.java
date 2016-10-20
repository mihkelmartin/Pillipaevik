package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedHeliFailiPildiOlekTeosel {

    private String googlekonto;
    private boolean bTestiSAlgolek;
    private boolean bTestiGAlgolek;

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
        SharedPreferences sharedPref = InstrumentationRegistry
                .getTargetContext()
                .getSharedPreferences(mActivityRule.getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        this.googlekonto = sharedPref.getString("googlekonto", "");
        this.bTestiSAlgolek = sharedPref.getBoolean("kaslubadamikrofonigasalvestamine", true );
        this.bTestiGAlgolek = sharedPref.getBoolean("kaskasutadagoogledrive", true );
    }

    @Test
    public void TestHeliFailiOlekTeosel() {

        if(!OnReaalneSeade())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        googlekonto = sharedPref.getString("googlekonto", "");
        SeadistaSalvestamine(this.googlekonto, false, false);

        // Vaja värskendada olukord, seetõttu vali teine teos enne
        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaKoduKui1Fragment();
        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.GONE)))));

        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).perform(click());
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));
        VajutaKoduKui1Fragment();
        VajutaTagasiKui1Fragment();

        SeadistaSalvestamine(this.googlekonto, true, true);

        // Vaja värskendada olukord, seetõttu vali teine teos enne
        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaKoduKui1Fragment();
        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));

        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).perform(click());
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        VajutaKoduKui1Fragment();
        VajutaTagasiKui1Fragment();
    }
    @After
    public void Lopeta_Test() {
        SeadistaSalvestamine(this.googlekonto, this.bTestiSAlgolek, this.bTestiGAlgolek);
    }
}
