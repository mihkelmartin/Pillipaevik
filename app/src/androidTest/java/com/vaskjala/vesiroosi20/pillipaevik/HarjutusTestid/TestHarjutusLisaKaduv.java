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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusLisaKaduv {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaKaduv() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));

        VajutaAlustaUutHarjutust();
        VajutaKodu();
        KontrolliTulemust(context);

        VajutaAlustaUutHarjutust();
        TestTooriistad.VajutaTagasi();
        KontrolliTulemust(context);

        VajutaAlustaUutHarjutust();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        VajutaKodu();
        KontrolliTulemust(context);

        VajutaAlustaUutHarjutust();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        VajutaTagasi();
        KontrolliTulemust(context);

        // Kustuta
        VajutaAlustaUutHarjutust();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        VajutaKustutaHarjutus();
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus))))
                .check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos4_h3_nimi))))));
        VajutaDialoogOK();
        KontrolliTulemust(context);
    }

    private void KontrolliTulemust(Context context){
        HarjutusPuudub("");
        TestTooriistad.TeoseStatistikaRiba(context, "1", 25);
        if(TestTooriistad.OnMultiFragment()) {
            TestTooriistad.TeosListStatistikaRiba(3, "1", 25);
            TestTooriistad.StatistikaKontroll(context);
        }
    }
}
