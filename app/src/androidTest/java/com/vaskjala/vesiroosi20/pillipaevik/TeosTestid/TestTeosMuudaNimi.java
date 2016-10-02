package com.vaskjala.vesiroosi20.pillipaevik.TeosTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.widget.LinearLayout;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestTeosMuudaNimi {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestMuudaNimi() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resources.getString(R.string.test_teos2_nimi))), click()).atPosition(0));
        onView(withId(R.id.nimi)).perform(typeText(" MUUDETUD"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.autor)).perform(typeText(" MUUDETUD"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.kommentaar)).
                perform(replaceText(resources.getString(R.string.test_teos2_kommentaar) + " MUUDETUD"), ViewActions.closeSoftKeyboard());
        TestTooriistad.VajutaKoduKui1Fragment();
        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos2_nimi) + " MUUDETUD"))).
                check(ViewAssertions.matches(isDisplayed()));
    }
}
