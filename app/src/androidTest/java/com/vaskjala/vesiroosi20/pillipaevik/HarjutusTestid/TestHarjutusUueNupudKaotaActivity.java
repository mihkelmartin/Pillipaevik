package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusUueNupudKaotaActivity {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestUueNupudKaotaActivity() {

        if(!OnReaalneSeade())
            return;

        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos1_nimi));
        VajutaAlustaUutHarjutust();

        // Test I
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST I"));
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppValjasKasutusel();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppValjasKasutusel();

        // Test II
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST II"));
        VajutaMikrofoni();
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppSeesKasutusel();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppAlusta();
        OnUueHarjutuseKestusNull();
        OnMikrofoniNuppValjasKasutusel();

        // Test III
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST III"));
        VajutaTaimeriNuppu();
        Oota(1000);
        OnTaimeriNuppKatkesta();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutu();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppKatkesta();
        Oota(1000);
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutu();

        // Test IV
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST IV"));
        VajutaTaimeriNuppu();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppJatka();
        Oota(1000);
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();


        // Test V
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST V"));
        VajutaMikrofoni();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppSeesKasutusel();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppJatka();
        Oota(1000);
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();


        // Test VI
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST VI"));
        VajutaMikrofoni();
        OnMikrofoniNuppSeesKasutusel();
        VajutaTaimeriNuppu();
        Oota(1000);
        OnTaimeriNuppKatkesta();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppSeesKasutu();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppKatkesta();
        Oota(1000);
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutu();

        // Test VII
        onView(withId(R.id.harjutusekirjeldus)).perform(ViewActions.replaceText("TEST VII"));
        VajutaTaimeriNuppu();
        OnTaimeriNuppJatka();
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();

        VajutaKoduAvaPilliPaevik(context);
        VajutaTagasi();
        OnTaimeriNuppJatka();
        Oota(1000);
        OnUuelHarjutuselKestus();
        OnMikrofoniNuppValjasKasutusel();

        VajutaKustutaHarjutus();
        VajutaDialoogOK();
    }
}
