package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;


import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
public class TestKalenderRidaSulgubFragmentKaob {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestRidaSulgubFragmentKaob() {

        AvaSahtelValiKalender();

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));

        if(TestTooriistad.OnMultiFragment())
            OnHarjutusMuudaFragment();

        VajutaKoduKui1Fragment();

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        EiOoleHarjutusMuudaFragment();
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
    }
}
