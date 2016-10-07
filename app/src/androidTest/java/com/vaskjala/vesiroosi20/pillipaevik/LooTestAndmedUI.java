package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        TestTooriistad.LisaTeosUI(resources.getString(R.string.test_teos1_nimi), resources.getString(R.string.test_teos1_autor), resources.getString(R.string.test_teos1_kommentaar));
            TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos1_h1_nimi), 1380, 35);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos1_h2_nimi), 180, 25);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos1_h3_nimi), 45*1000);
            TestTooriistad.VajutaTagasi();
        TestTooriistad.VajutaKoduKui1Fragment();

        TestTooriistad.LisaTeosUI(resources.getString(R.string.test_teos2_nimi), resources.getString(R.string.test_teos2_autor), resources.getString(R.string.test_teos2_kommentaar));
            TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos2_h1_nimi), 2880, 15);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos2_h2_nimi), 50, 5);
            TestTooriistad.VajutaTagasiKui1Fragment();
            TestTooriistad.LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos2_h3_nimi), 70*1000);
            TestTooriistad.VajutaTagasi();
        TestTooriistad.VajutaKoduKui1Fragment();

        TestTooriistad.LisaTeosUI(resources.getString(R.string.test_teos3_nimi), resources.getString(R.string.test_teos3_autor), resources.getString(R.string.test_teos3_kommentaar));
        TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos3_h1_nimi), 1320, 10);
        TestTooriistad.VajutaTagasiKui1Fragment();
        TestTooriistad.LisaTehtudHarjutusUI(resources.getString(R.string.test_teos3_h2_nimi), 300, 40);
        TestTooriistad.VajutaKoduKui1Fragment();
    }
}
