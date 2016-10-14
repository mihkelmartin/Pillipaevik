package com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad.LooAruandeKuud;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestAruanneSaadaPraeguneKuu {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSaadaPraeguneKuu() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        AvaSahtelValiAruanne();

        onView(withText(containsString(resources.getString(R.string.vali_aruande_kuu)))).
                check(ViewAssertions.matches(is(withText(containsString(resources.getString(R.string.vali_aruande_kuu))))));

        List<String> mKuud  = LooAruandeKuud(resources.getInteger(R.integer.kuudearv));
        onView(withText(mKuud.get(0))).perform(click());
        if(OnReaalneSeade())
            AvaGmail();
        VajutaTagasi();
        VajutaTagasi();
    }
}