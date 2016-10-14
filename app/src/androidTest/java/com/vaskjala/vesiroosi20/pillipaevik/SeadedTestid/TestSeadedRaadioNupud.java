package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(Parameterized.class)
public class TestSeadedRaadioNupud {

    private static Context context = InstrumentationRegistry.getTargetContext();
    private boolean bSalgolek;
    private boolean bGalgolek;
    private boolean bSsiire;
    private boolean bGsiire;
    private boolean bStulemus;
    private boolean bGtulemus;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // S algolek G algolek, S siire, G siire, S tulemus, G tulemus
                {false, false, true, false, true, false},
                {false, false, false, true, true, true},
                {true, false, true, true, true, true},
                {true, false, false, false, false, false},
                {false, true, true, true, true, true},
                {false, true, false, false, false, false},
                {true, true, true,false, true, false},
                {true, true, false, true, false, false}
        });
    }

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }


    public TestSeadedRaadioNupud(boolean bSalgolek, boolean bGalgolek,
                                 boolean bSsiire, boolean bGsiire,
                                 boolean bStulemus, boolean bGtulemus){
        this.bSalgolek = bSalgolek;
        this.bGalgolek = bGalgolek;
        this.bSsiire= bSsiire;
        this.bGsiire= bGsiire;
        this.bStulemus= bStulemus;
        this.bGtulemus= bGtulemus;
    }

    @Test
    public void TestRaadioNupud() {

        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String googlekonto = sharedPref.getString("googlekonto", "");
        editor.putBoolean("kaslubadamikrofonigasalvestamine", bSalgolek);
        editor.putBoolean("kaskasutadagoogledrive", bGalgolek);
        editor.commit();

        AvaSahtelValiSeaded();

        onView(withId(R.id.kasKasutadaGoogleDrive)).perform(ViewActions.scrollTo());

        if(bSalgolek != bSsiire){
            onView(withId(R.id.kasLubadaMikrofonigaSalvestamine)).perform(ViewActions.click());
        }
        if(bGalgolek != bGsiire){
            onView(withId(R.id.kasKasutadaGoogleDrive)).perform(ViewActions.click());
        }

        if(bStulemus)
            onView(withId(R.id.kasLubadaMikrofonigaSalvestamine)).check(ViewAssertions.matches(isChecked()));
        else
            onView(withId(R.id.kasLubadaMikrofonigaSalvestamine)).check(ViewAssertions.matches(not(isChecked())));

        if(bGtulemus)
            onView(withId(R.id.kasKasutadaGoogleDrive)).check(ViewAssertions.matches(isChecked()));
        else
            onView(withId(R.id.kasKasutadaGoogleDrive)).check(ViewAssertions.matches(not(isChecked())));

        VajutaKodu();

        assertEquals(bStulemus, sharedPref.getBoolean("kaslubadamikrofonigasalvestamine", true ));
        assertEquals(bGtulemus, sharedPref.getBoolean("kaskasutadagoogledrive", true));

        editor.putString("googlekonto", googlekonto);
        editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
        if(OnReaalneSeade())
            editor.putBoolean("kaskasutadagoogledrive", true);
        else
            editor.putBoolean("kaskasutadagoogledrive", false);

        editor.commit();
    }
}