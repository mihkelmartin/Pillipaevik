package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

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
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSalvestiseKustutamine() {
        TestTooriistad.AvaSahtelValiKalender();

        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItem(withChild(withClassName(is(LinearLayout.class.getName()))),click()).atPosition(0));
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.kustutasalvestus)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.kustutasalvestus)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        TestTooriistad.Oota(100);
        onView(withId(R.id.SalvestuseRiba)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)));;
        TestTooriistad.VajutaTagasiKui1Fragment();
    }


}
