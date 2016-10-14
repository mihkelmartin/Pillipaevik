package com.vaskjala.vesiroosi20.pillipaevik.HarjutusLisaTehtudTestid;

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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestLisaTehtudTuhiLisaUusKustuta {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTehtudTuhiLisaUusKustuta() {
        if (OnMultiFragment()) {
            Context context = InstrumentationRegistry.getTargetContext();
            Resources resources = context.getResources();

            ValiTeos(resources.getString(R.string.test_teos3_nimi));
            VajutaLisaTehtudHarjutus();
            VajutaAlustaUutHarjutust();
            onView(withId(R.id.harjutusekirjeldus))
                    .perform(ViewActions.replaceText(resources.getString(R.string.test_h_ei_salvestu)), ViewActions.closeSoftKeyboard());

            VajutaMikrofoni();
            VajutaTaimeriNuppu();
            TestTooriistad.Oota(3*1000);

            VajutaKustutaHarjutus();
            VajutaDialoogOK();

            LeiaHarjutus("").check(ViewAssertions.matches(isDisplayed()));
            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText(resources.getString(R.string.test_h_ei_salvestu)))))
                    .check(ViewAssertions.doesNotExist());

            KeeraVasakule();
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText("")));
            OnHarjutusLisaTehtudFragment();

            ValiTeos(resources.getString(R.string.test_teos3_nimi));
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText("")));
            OnHarjutusLisaTehtudFragment();

            KeeraParemale();
            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText("")))).perform(click());
            onView(withId(R.id.harjutusekirjeldus)).check(ViewAssertions.matches(withText("")));
            OnHarjutusLisaTehtudFragment();

            ValiTeos(resources.getString(R.string.test_teos1_nimi));
            ValiTeos(resources.getString(R.string.test_teos3_nimi));

            onView(Matchers.allOf(withId(R.id.harjutuslistrida), hasDescendant(withText(""))))
                    .check(ViewAssertions.doesNotExist());

            OnHarjutusMuudaFragment();
            VabastaKeeramine();
        }
    }
}
