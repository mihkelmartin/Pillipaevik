package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedHeliFailiPildiOlekTeosel {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHeliFailiOlekTeosel() {

        if(!OnReaalneSeade())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String googlekonto = sharedPref.getString("googlekonto", "");
        editor.putBoolean("kaslubadamikrofonigasalvestamine", false);
        editor.putBoolean("kaskasutadagoogledrive", false);
        editor.commit();

        onView(withId(R.id.harjutua_list)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.test_teos1_nimi)),click()));
        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.GONE)))));

        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).perform(click());
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));
        VajutaKoduKui1Fragment();
        VajutaTagasiKui1Fragment();

        editor.putString("googlekonto", googlekonto);
        editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
        editor.putBoolean("kaskasutadagoogledrive", true);
        editor.commit();

        onView(withId(R.id.harjutua_list)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.test_teos1_nimi)),click()));
        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt), withEffectiveVisibility(Visibility.VISIBLE)))));

        LeiaHarjutus(resources.getString(R.string.test_teos1_h3_nimi)).perform(click());
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        VajutaKoduKui1Fragment();
        VajutaTagasiKui1Fragment();
    }
}
