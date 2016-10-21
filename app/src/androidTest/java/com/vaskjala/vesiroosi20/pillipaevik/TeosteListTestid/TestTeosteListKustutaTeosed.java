package com.vaskjala.vesiroosi20.pillipaevik.TeosteListTestid;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TeosTestid.TestTeosLisa;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestTeosteListKustutaTeosed {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestKustutaTeosed() {
        Context context = InstrumentationRegistry.getTargetContext();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        VajutaKustutaTeos();
        VajutaDialoogOK();
        StatistikaKontroll(context);

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        VajutaKustutaTeos();
        VajutaDialoogOK();
        StatistikaKontroll(context);

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        VajutaKustutaTeos();
        VajutaDialoogOK();
        StatistikaKontroll(context);

        VajutaTagasi();
        AnnaUiDevice().pressHome();
        AvaPilliPaevik(context);

        TestTeosLisa testTeosLisa = new TestTeosLisa();
        testTeosLisa.TestLisaTeos();

        KeeraParemale();
        VajutaTagasi();
        KeeraVasakule();
        VabastaKeeramine();
    }
}
