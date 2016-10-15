package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestHarjutusKustutaSalvestav {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestKustutaSalvestav() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        ValiTeos(resources.getString(R.string.test_teos4_nimi));
        VajutaAlustaUutHarjutust();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(resources.getString(R.string.test_teos4_h3_nimi)), closeSoftKeyboard());
        VajutaMikrofoni();
        VajutaTaimeriNuppu();
        Oota(10*1000);

        VajutaKustutaHarjutus();
        VajutaDialoogOK();
        HarjutusPuudub(resources.getString(R.string.test_teos4_h3_nimi));
    }
}
