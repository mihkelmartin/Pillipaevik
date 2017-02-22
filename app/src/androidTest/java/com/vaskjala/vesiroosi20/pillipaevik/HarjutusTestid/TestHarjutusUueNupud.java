package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusUueNupud {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestUueNupud() {

        if(!OnReaalneSeade())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        VajutaAlustaUutHarjutust();

        // Test I
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppValjasKasutusel();

        // Test II
        VajutaMikrofoni();
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppSeesKasutusel();

        // Test III
        VajutaMikrofoni();
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppValjasKasutusel();

        // Test IV
        VajutaTaimeriNuppu();
        Oota(OOTE_AEG);
        OnTaimeriNuppKatkesta();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutu();

        // Test V
        VajutaTaimeriNuppu();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();

        // Test VI
        VajutaMikrofoni();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppSeesKasutusel();

        // Test VII
        VajutaTaimeriNuppu();
        Oota(OOTE_AEG);
        OnTaimeriNuppKatkesta();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppSeesKasutu();

        // Test VII
        VajutaTaimeriNuppu();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();

        VajutaKustutaHarjutus();
        VajutaDialoogOK();
    }
}
