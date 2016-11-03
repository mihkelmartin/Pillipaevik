package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;

import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad.*;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestHarjutusUusAutoStart {

    private boolean bAutoStart;

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
        bAutoStart = kasStopperiAutoStart(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void TestUusAutoStart() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SeadistaStopperiAutoStartSeadeteFailis(context, true);

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        VajutaAlustaUutHarjutust();
        Oota(2*1000);
        OnUuelHarjutuselKestus();
        VajutaKustutaHarjutus();
        VajutaDialoogOK();
    }
    @After
    public void Lopeta_Test() {
        SeadistaStopperiAutoStartSeadeteFailis(InstrumentationRegistry.getTargetContext(), bAutoStart);
    }
}
