package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusJagaSalvestist {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestJagaSalvestist() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos4_nimi))), click()).atPosition(0));
        TestTooriistad.LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos4_h2_nimi), 75 * 1000);
        TestTooriistad.VajutaKodu();

        TestTooriistad.TeoseStatistikaRiba(context,"2", (125+75));
        onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).
                check(ViewAssertions.matches(hasDescendant(withText(resources.getString(R.string.test_teos4_h2_nimi)))));
        onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).
                check(ViewAssertions.
                        matches(hasDescendant(allOf(withId(R.id.harjutuslisti_pilt),withEffectiveVisibility(Visibility.VISIBLE)))));

        TestTooriistad.VajutaKoduKui1Fragment();

        // Teose statistika kontroll
        TestTooriistad.TeosListStatistikaRiba(3, "2", (125+75));

        if(TestTooriistad.OnMultiFragment())
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos4_h2_nimi))));

        TestTooriistad.StatistikaKontroll(context);

        // Oota, et link jõuaks Google Draivist Pillipäevikusse
        TestTooriistad.Oota(10000);
        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos4_nimi))), click()).atPosition(0));
        onView(allOf(withId(R.id.harjutuslistrida), hasDescendant(withText(R.string.test_teos4_h2_nimi)))).perform(click());
        onView(withId(R.id.jaga)).perform(click());
        TestTooriistad.AvaGmail();
        TestTooriistad.VajutaTagasi();
        TestTooriistad.VajutaTagasi();


    }
}
