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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedHeliFailiPildiOlekKalendril {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHeliFailiPildiOlekKalendril() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String googlekonto = sharedPref.getString("googlekonto", "");
        editor.putBoolean("kaslubadamikrofonigasalvestamine", false);
        editor.putBoolean("kaskasutadagoogledrive", false);
        editor.commit();

        TestTooriistad.AvaSahtelValiKalender();
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItem(withChild(withClassName(is(LinearLayout.class.getName()))),click()).atPosition(0));
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(1,R.id.kalender_paev_harjutus_helifaili_pilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));
        TestTooriistad.VajutaTagasi();

        editor.putString("googlekonto", googlekonto);
        editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
        editor.putBoolean("kaskasutadagoogledrive", true);
        editor.commit();

        TestTooriistad.AvaSahtelValiKalender();
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItem(withChild(withClassName(is(LinearLayout.class.getName()))),click()).atPosition(0));
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(1,R.id.kalender_paev_harjutus_helifaili_pilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        TestTooriistad.VajutaTagasi();


    }
}
