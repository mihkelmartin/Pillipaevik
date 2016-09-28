package com.vaskjala.vesiroosi20.pillipaevik;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class LooTestAndmedUI {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void looTeosedHarjutused() {
        TestTooriistad.LisaTeosUI("Testteos I", "Mihkel Martin", "Minu loodud lugu");
            TestTooriistad.LisaHarjutusUI("Testteos I Harjutus I", 1380, 35);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaHarjutusUI("Testteos I Harjutus II", 180, 25);
            TestTooriistad.VajutaTagasiKui1Fragment();
        TestTooriistad.VajutaTagasiKui1Fragment();

        TestTooriistad.LisaTeosUI("Testteos II", "Oskar Martin", "Oskari loodud lugu");
            TestTooriistad.LisaHarjutusUI("Testteos II Harjutus I", 2880, 15);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaHarjutusUI("Testteos II Harjutus II", 50, 5);
            TestTooriistad.VajutaTagasiKui1Fragment();
        TestTooriistad.VajutaTagasiKui1Fragment();

        TestTooriistad.LisaTeosUI("Testteos III", "Annika Martin", "Annika loodud lugu");
        TestTooriistad.LisaHarjutusUI("Testteos III Harjutus I", 300, 40);
        TestTooriistad.VajutaTagasiKui1Fragment();
        TestTooriistad.LisaHarjutusUI("Testteos III Harjutus II", 1320, 10);
        TestTooriistad.VajutaTagasiKui1Fragment();
    }
}
