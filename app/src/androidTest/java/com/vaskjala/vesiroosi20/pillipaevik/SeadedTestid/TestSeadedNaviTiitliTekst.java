package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.Oota;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.VajutaTagasi;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedNaviTiitliTekst {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestNaviTiitel() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Oota(1000);
        onView(withId(R.id.navitiitli_nimevali)).
                check(ViewAssertions.matches(withText(resources.
                        getString(R.string.test_minueesnimi) + " " + resources.getString(R.string.test_minuperenimi))));
        VajutaTagasi();
    }
}
