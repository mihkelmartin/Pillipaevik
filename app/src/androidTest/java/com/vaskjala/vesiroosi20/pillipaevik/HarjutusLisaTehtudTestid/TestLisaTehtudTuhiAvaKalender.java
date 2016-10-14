package com.vaskjala.vesiroosi20.pillipaevik.HarjutusLisaTehtudTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestLisaTehtudTuhiAvaKalender {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }


    @Test
    public void TestLisaTehtudTuhiAvaKalender() {
        if(OnMultiFragment()) {
            Context context = InstrumentationRegistry.getTargetContext();
            Resources resources = context.getResources();

            ValiTeos(resources.getString(R.string.test_teos3_nimi));
            VajutaLisaTehtudHarjutus();
            OnHarjutusLisaTehtudFragment();

            onView(withId(R.id.harjutusekirjeldus))
                    .perform(ViewActions.replaceText(resources.getString(R.string.test_teos3_h8_nimi)), ViewActions.closeSoftKeyboard());
            OnHarjutusLisaTehtudFragment();

            AvaSahtelValiKalender();
            onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

            onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos3_h8_nimi))))
                    .check(ViewAssertions.matches(isDisplayed()));
            onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
            onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos3_h8_nimi))))
                    .check(ViewAssertions.doesNotExist());
            VajutaKodu();
            onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos3_h8_nimi))))
                    .check(ViewAssertions.doesNotExist());
        }
    }
}
