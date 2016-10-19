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

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestGoogleKontoValimine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String googlekonto = sharedPref.getString("googlekonto", "");
        boolean bkaskasutadagoogledrive = sharedPref.getBoolean("kaskasutadagoogledrive", false);
        editor.putString("googlekonto", "");
        editor.putBoolean("kaskasutadagoogledrive", true);
        editor.commit();

        AvaSahtelValiSeaded();
        VajutaKodu();

        if(Tooriistad.isGooglePlayServicesAvailable(mActivityRule.getActivity())){
            VajutaTagasi();
            onView(withText(R.string.konto_valimise_vea_pealkiri)).check(ViewAssertions.matches(isDisplayed()));
            onView(withId(android.R.id.button3)).perform(click());
        } else {
            onView(withText(R.string.google_play_teenused_puuduvad_vea_pealkiri)).check(ViewAssertions.matches(isDisplayed()));
            onView(withId(android.R.id.button3)).perform(click());
        }

        editor.putBoolean("kaskasutadagoogledrive", bkaskasutadagoogledrive);
        editor.putString("googlekonto", googlekonto);
        editor.commit();
    }
}
