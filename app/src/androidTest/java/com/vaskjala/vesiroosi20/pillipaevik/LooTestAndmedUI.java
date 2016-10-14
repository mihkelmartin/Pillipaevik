package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

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
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void looTeosedHarjutused() {

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
        if(OnReaalneSeade())
            editor.putBoolean("kaskasutadagoogledrive", true);
        else
            editor.putBoolean("kaskasutadagoogledrive", false);
        editor.commit();

        LisaTeosUI(resources.getString(R.string.test_teos1_nimi), resources.getString(R.string.test_teos1_autor), resources.getString(R.string.test_teos1_kommentaar));
            LisaTehtudHarjutusUI(resources.getString(R.string.test_teos1_h1_nimi), 1380, 35);
            VajutaTagasiKui1Fragment();
            LisaTehtudHarjutusUI(resources.getString(R.string.test_teos1_h2_nimi), 180, 25);
            VajutaTagasiKui1Fragment();
            LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos1_h3_nimi), 45*1000);
            VajutaTagasi();
        VajutaKoduKui1Fragment();

        LisaTeosUI(resources.getString(R.string.test_teos2_nimi), resources.getString(R.string.test_teos2_autor), resources.getString(R.string.test_teos2_kommentaar));
            LisaTehtudHarjutusUI(resources.getString(R.string.test_teos2_h1_nimi), 2880, 15);
            VajutaTagasiKui1Fragment();
            LisaTehtudHarjutusUI(resources.getString(R.string.test_teos2_h2_nimi), 50, 5);
            VajutaTagasiKui1Fragment();
            LisaUusHarjutusSalvestisega(resources.getString(R.string.test_teos2_h3_nimi), 70*1000);
            VajutaTagasi();
        VajutaKoduKui1Fragment();

        LisaTeosUI(resources.getString(R.string.test_teos3_nimi), resources.getString(R.string.test_teos3_autor), resources.getString(R.string.test_teos3_kommentaar));
        LisaTehtudHarjutusUI(resources.getString(R.string.test_teos3_h1_nimi), 1320, 10);
        VajutaTagasiKui1Fragment();
        LisaTehtudHarjutusUI(resources.getString(R.string.test_teos3_h2_nimi), 300, 40);
        VajutaKoduKui1Fragment();
    }
}
