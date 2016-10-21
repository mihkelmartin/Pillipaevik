package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedGoogleKontoValimine {

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
    public void TestGoogleKontoValimine() {

        if(!OnReaalneSeade())
            return;

        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SeadistaSalvestamine("", bTestiSAlgolek,true);
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.seaded));
        TestTooriistad.Oota(1000);
        VajutaKodu();
        TestTooriistad.Oota(3000);

        if(!Tooriistad.isGooglePlayServicesAvailable(mActivityRule.getActivity())){
            onView(withText(R.string.google_play_teenused_puuduvad_vea_pealkiri)).check(ViewAssertions.matches(isDisplayed()));
            onView(withId(android.R.id.button3)).perform(click());
        } else {
            if(OnReaalneSeade()) {
                onView(withText(R.string.konto_valimise_pealkiri)).check(ViewAssertions.matches(isDisplayed()));
                VajutaTagasi();
                onView(withText(R.string.konto_valimise_vea_pealkiri)).check(ViewAssertions.matches(isDisplayed()));
                onView(withId(android.R.id.button3)).perform(click());
            } else{
                VajutaTagasi();
                VajutaTagasi();
                VajutaTagasi();
            }
        }
    }

    @After
    public void Lopeta_Test() {
        SeadistaSalvestamine(this.googlekonto, this.bTestiSAlgolek, this.bTestiGAlgolek);
    }
}
