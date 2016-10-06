package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaSalvestisega {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaSalvestisega() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos4_nimi))), click()).atPosition(0));
        TestTooriistad.LisaUusHarjutusSalvestisega("", 15000);
        TestTooriistad.VajutaKodu();

        onView(withId(R.id.teoseharjutustearv)).check(ViewAssertions.matches(withText("1")));
        onView(withId(R.id.teoseharjutustekestus)).
                check(ViewAssertions.matches(withText(Tooriistad.KujundaHarjutusteMinutid(context, 15/60))));
        onView(withId(R.id.harjutuslist_harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));
        onView(withId(R.id.harjutuslisti_pilt)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)));;

        if(TestTooriistad.OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.vaikimisisharjutusekirjeldus))));


    }
}
