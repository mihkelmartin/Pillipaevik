package com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(Parameterized.class)
public class TestAruanneAndmedPuudu {

    private static Context context = InstrumentationRegistry.getTargetContext();
    private static Resources resources = context.getResources();
    private String eesnimi;
    private String perenimi;
    private String instrument;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"", resources.getString(R.string.test_minuperenimi), resources.getString(R.string.test_minuinstrument)},
                {resources.getString(R.string.test_minueesnimi), "", resources.getString(R.string.test_minuinstrument)},
                {resources.getString(R.string.test_minueesnimi), resources.getString(R.string.test_minuperenimi), ""}
        });
    }

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        MultiFragmentTuvastus(mActivityRule);
    }


    public TestAruanneAndmedPuudu(String eesnimi, String perenimi, String instrument){
        this.eesnimi = eesnimi;
        this.perenimi = perenimi;
        this.instrument = instrument;
    }

    @Test
    public void TestAndmedPuudu() {
        SharedPreferences sharedPref = context.getSharedPreferences(mActivityRule.
                getActivity().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("minueesnimi", eesnimi);
        editor.putString("minuperenimi", perenimi);
        editor.putString("minuinstrument", instrument);
        editor.commit();

        AvaSahtelValiAruanne();
        onView(withText(containsString(resources.getString(R.string.aruande_tegemise_keeldumise_pohjus)))).
                check(ViewAssertions.matches(isDisplayed()));

        editor.putString("minueesnimi", resources.getString(R.string.test_minueesnimi));
        editor.putString("minuperenimi", resources.getString(R.string.test_minuperenimi));
        editor.putString("minuinstrument", resources.getString(R.string.test_minuinstrument));
        editor.commit();
    }
}