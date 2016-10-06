package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestKalenderHarjutuseKustutamine {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestHarjutuseKustutamine() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        if(TestTooriistad.OnMultiFragment()){
            onView(withId(R.id.harjutua_list)).
                    perform(RecyclerViewActions.actionOnItem(withText(resources.getString(R.string.test_teos2_nimi)), click()));
            onData(anything()).inAdapterView(withId(R.id.harjutuslist)).atPosition(0).perform(click());
        }

        TestTooriistad.AvaSahtelValiKalender();


        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItem(withChild(withClassName(is(LinearLayout.class.getName()))), click()).atPosition(0));
        onView(withId(R.id.kalendri_tabel)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withText(containsString(resources.getString(R.string.dialog_kas_kustuta_harjutuse_kusimus)))).
                check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.test_teos2_h3_nimi))))));
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.kustutaharjutus)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(0,R.id.paevakalenderharjutustearv)).
                check(ViewAssertions.matches(withText("4")));
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(0,R.id.paevakalenderharjutustekestus)).
                check(ViewAssertions.matches(withText("01:10")));

        TestTooriistad.VajutaKodu();

        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.teoslistteoseharjutustearv)).
                check(ViewAssertions.matches(withText("2")));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(1,R.id.teoslistteoseharjutustekestus)).
                check(ViewAssertions.matches(withText("00:20")));


    }
}