package com.vaskjala.vesiroosi20.pillipaevik.ArhiivTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestArhiivTeosArhiivLisaTuhjad {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
        KustutaTeosedUI(InstrumentationRegistry.getTargetContext());
        LisaArhiiviTestiTeosed();
    }

    @Test
    public void TestArhiivLisaTuhjad() {

        if(!OnMultiFragment())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        Tooriistad.SeadistaNaitaArhiiviSeadeteFailis(context, true);

        // Ära näita arhiivi
        VajutaArhiivMenuu();
        VajutaLisaTeos();
        onView(allOf(withId(R.id.content), withText(""))).check(ViewAssertions.matches(isDisplayed()));
        EiOoleHarjutusMuudaFragment();

        VajutaArhiivNupp();
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText("")))).check(ViewAssertions.doesNotExist());
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos2_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos1_h1_nimi)))).check(ViewAssertions.matches(isDisplayed()));

        // Näita arhiivi
        VajutaArhiivMenuu();
        onView(allOf(withId(R.id.content), withText(""))).check(ViewAssertions.matches(isDisplayed()));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(0,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ValiTeos("");
        VajutaArhiivNupp();
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(0,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        EiOoleHarjutusMuudaFragment();

        // Ära näita arhiivi
        VajutaArhiivMenuu();
        VajutaLisaTehtudHarjutus();
        OnHarjutusLisaTehtudFragment();
        onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText("")));
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos1_h1_nimi)), closeSoftKeyboard());
        VajutaArhiivNupp();
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText("")))).check(ViewAssertions.doesNotExist());
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos2_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos1_h1_nimi)))).check(ViewAssertions.matches(isDisplayed()));


        // Näita arhiivi
        VajutaArhiivMenuu();
        onView(allOf(withId(R.id.content), withText(""))).check(ViewAssertions.matches(isDisplayed()));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(0,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        OnHarjutusMuudaFragment();
        onView(allOf(withId(R.id.harjutusekirjeldus), withText(resources.getString(R.string.test_teos1_h1_nimi)))).check(ViewAssertions.matches(isDisplayed()));

    }

    @After
    public void Lopeta_Test() {
        Tooriistad.SeadistaNaitaArhiiviSeadeteFailis(InstrumentationRegistry.getTargetContext(), true);
    }
}
