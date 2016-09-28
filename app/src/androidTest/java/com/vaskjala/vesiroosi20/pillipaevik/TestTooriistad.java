package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.widget.DatePicker;
import org.hamcrest.Matchers;
import android.support.test.espresso.contrib.PickerActions;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by mihkel on 28.09.2016.
 */
 public final class TestTooriistad {

    private static boolean bMultiFragmentTuvastatud = false;
    private static boolean bMultiFragment = false;
    private static UiDevice ui = null;

    public static void LisaTeosUI(String nimi, String autor, String kommentaar) {
        onView(withId(R.id.lisateos)).perform(click());
        onView(withId(R.id.nimi)).perform(ViewActions.typeText(nimi));
        onView(withId(R.id.autor)).perform(ViewActions.typeText(autor));
        onView(withId(R.id.kommentaar)).perform(ViewActions.
                typeText(kommentaar), closeSoftKeyboard());
    }

    public static void LisaHarjutusUI(String nimi, int minuteidmaha, int pikkus) {
        onView(withId(R.id.lisatehtud)).perform(click());
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.typeText(nimi), closeSoftKeyboard());

        onView(withId(R.id.alguskuupaev)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).
                perform(PickerActions.setDate(2016, 9, 27));
        onView(withId(android.R.id.button1)).perform(click());

        //onData(withId(R.id.alguskellaaeg)).perform(ViewActions.typeText("08:26"));
        //onData(withId(R.id.alguskuupaev)).perform(ViewActions.typeText("2016-09-28"));
        //onData(withId(R.id.alguskellaaeg)).perform(ViewActions.typeText("08:36"));
    }

    public static void VajutaTagasiKui1Fragment(){
        if(!TestTooriistad.OnMultiFragment()) {
            if (BuildConfig.DEBUG) Log.d("VajutaTagasiKui1Frag", "");
            UiDevice ui = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            AnnaUiDevice().pressBack();
        }
    }

    private static UiDevice AnnaUiDevice(){
        if(ui == null){
            ui = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        }
        return ui;
    }

    public static void MultiFragmentTuvastus(ActivityTestRule activityTestRule){
        if(!bMultiFragmentTuvastatud) {
            if (BuildConfig.DEBUG) Log.d("TestTooriistad", "Tuvastame mitmefragmentsust");
            Activity activity = activityTestRule.getActivity();
            if (activity.findViewById(R.id.teos_hoidja) != null &&
                    activity.findViewById(R.id.harjutus_hoidja) != null) {
                if (BuildConfig.DEBUG) Log.d("TestTooriistad", "OnMultiFragment. Mitme fragmendiga vaade");
                bMultiFragment = true;
            }
            bMultiFragmentTuvastatud = true;
        }
    }

    public static boolean OnMultiFragment(){
        return bMultiFragment;
    }

}
