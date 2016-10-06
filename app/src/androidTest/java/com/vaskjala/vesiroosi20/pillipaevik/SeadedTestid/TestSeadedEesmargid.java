package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.PeaActivity;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.TestTooriistad;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestSeadedEesmargid {

    @Rule
    public ActivityTestRule<PeaActivity> mActivityRule = new ActivityTestRule(
            PeaActivity.class);

    @Before
    public void Seadista_Test() {
        TestTooriistad.MultiFragmentTuvastus(mActivityRule);
    }

    @Test
    public void TestSeadedEesmargid() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();

        SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.seadete_fail), MODE_PRIVATE);
        Integer paevasharjutada = sharedPref.getInt("paevasharjutada", 0);

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());
        int paevanadalkordaja = (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? 7 : c.get(Calendar.DAY_OF_WEEK) -1;
        int paevakuukordaja = c.get(Calendar.DAY_OF_MONTH);

        String vajaharjutadapaev = String.valueOf(paevasharjutada + " m");
        String vajaharjutadanadal = String.valueOf(paevasharjutada * paevanadalkordaja + " m");
        String vajaharjutadakuu = String.valueOf(paevasharjutada * paevakuukordaja + " m");

        onView(withId(R.id.paevanorm)).check(ViewAssertions.matches(withText(vajaharjutadapaev)));
        onView(withId(R.id.nadalanorm)).check(ViewAssertions.matches(withText(vajaharjutadanadal)));
        onView(withId(R.id.kuunorm)).check(ViewAssertions.matches(withText(vajaharjutadakuu)));

    }
}
