package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;

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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestKalenderSalvestiseKustutamine {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSalvestiseKustutamine() {

        if(!OnReaalneSeade())
            return;

        AvaSahtelValiKalender();

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        onView(withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(1,R.id.kalender_paev_harjutus_helifaili_pilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        VajutaKustutaSalvestus();
        VajutaDialoogTuhista();
        VajutaKustutaSalvestus();
        VajutaDialoogOK();
        Oota(100);
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));;
        VajutaTagasiKui1Fragment();
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(1,R.id.kalender_paev_harjutus_helifaili_pilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));
    }


}
