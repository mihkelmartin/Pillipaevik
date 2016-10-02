package com.vaskjala.vesiroosi20.pillipaevik.TeosTestid;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
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
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mihkel on 1.10.2016.
 */
public class TestTeosLisa {
    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestLisaTeos() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        onView(withId(R.id.lisateos)).perform(click());
        if(TestTooriistad.OnMultiFragment())
            onView(allOf(withId(R.id.content), withText(""))).
                    check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.nimi)).perform(typeText(resources.getString(R.string.test_teos4_nimi)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.autor)).perform(typeText(resources.getString(R.string.test_teos4_autor)), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.kommentaar)).
                perform(replaceText(resources.getString(R.string.test_teos4_kommentaar)), ViewActions.closeSoftKeyboard());

        if(TestTooriistad.OnMultiFragment())
            onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).
                    check(ViewAssertions.matches(isDisplayed()));

        TestTooriistad.VajutaKoduKui1Fragment();

        onView(allOf(withId(R.id.content), withText(resources.getString(R.string.test_teos4_nimi)))).
                check(ViewAssertions.matches(isDisplayed()));


    }
}
