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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestArhiivTeosArhiiviMuutmine {
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
    public void TestArhiiviMuutmine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        Tooriistad.SeadistaNaitaArhiiviSeadeteFailis(context, true);

        if(OnMultiFragment()) {
            // Arhiivimenüü 2x vajutamine
            ValiTeos(resources.getString(R.string.test_teos1_nimi));
            VajutaArhiivMenuu();
            VajutaArhiivMenuu();
            onView(allOf(withId(R.id.nimi), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText(resources.getString(R.string.test_teos1_h1_nimi))));
        }

        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));


        // Arhiivis olevaid asju ei näidata edaspidi
        VajutaArhiivMenuu();
        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos2_nimi)))))
                    .check(ViewAssertions.doesNotExist());
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));


        // Näita arvhiiv
        VajutaArhiivMenuu();
        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaTagasiKui1Fragment();
        // Keela arhiiv
        VajutaArhiivMenuu();

        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos2_nimi)))))
                .check(ViewAssertions.doesNotExist());
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));

        VajutaLisaTeos();
        onView(withId(R.id.nimi)).perform(typeText(resources.getString(R.string.test_teos5_nimi)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.autor)).perform(typeText(resources.getString(R.string.test_teos5_autor)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.kommentaar)).perform(replaceText(resources.getString(R.string.test_teos5_kommentaar)), ViewActions.closeSoftKeyboard());
        VajutaTagasiKui1Fragment();
        ValiTeos(resources.getString(R.string.test_teos5_nimi));
        VajutaKustutaTeos();
        VajutaDialoogOK();


        // Pane kõik arvhiiv
        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        ValiTeos(resources.getString(R.string.test_teos3_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        // Kõik kadunud
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos1_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos2_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos3_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos4_nimi)))))
                .check(ViewAssertions.doesNotExist());

        EiOoleTeosFragment();
        EiOoleHarjutusMuudaFragment();

        VajutaLisaTeos();
        onView(withId(R.id.nimi)).perform(typeText(resources.getString(R.string.test_teos5_nimi)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.autor)).perform(typeText(resources.getString(R.string.test_teos5_autor)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.kommentaar)).perform(replaceText(resources.getString(R.string.test_teos5_kommentaar)), ViewActions.closeSoftKeyboard());
        VajutaTagasiKui1Fragment();
        ValiTeos(resources.getString(R.string.test_teos5_nimi));
        VajutaKustutaTeos();
        VajutaDialoogOK();

        // Kõik muud jätkuvalt kadunud
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos1_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos2_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos3_nimi)))))
                .check(ViewAssertions.doesNotExist());
        // Mõlemas kadunud
        onView(AllOf.allOf(withId(R.id.harjutua_list), hasDescendant(withText(resources.getString(R.string.test_teos4_nimi)))))
                .check(ViewAssertions.doesNotExist());

        EiOoleTeosFragment();
        EiOoleHarjutusMuudaFragment();

        VajutaArhiivMenuu();
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(0,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(2,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(3,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Võta kõik arviivist tagasi
        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        ValiTeos(resources.getString(R.string.test_teos2_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        ValiTeos(resources.getString(R.string.test_teos3_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        VajutaArhiivNupp();
        VajutaTagasiKui1Fragment();

        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos1_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos2_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos3_nimi)))).check(ViewAssertions.matches(isDisplayed()));
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).check(ViewAssertions.matches(isDisplayed()));

        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(0,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(2,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(3,R.id.arhiivipilt)).
                check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

    }

    @After
    public void Lopeta_Test() {
        Tooriistad.SeadistaNaitaArhiiviSeadeteFailis(InstrumentationRegistry.getTargetContext(), true);
    }
}
