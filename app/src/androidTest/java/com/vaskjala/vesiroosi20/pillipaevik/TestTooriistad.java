package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.*;
import android.support.test.espresso.action.OpenLinkAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.core.deps.guava.io.Resources;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import android.support.test.espresso.contrib.PickerActions;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by mihkel on 28.09.2016.
 */
 public final class TestTooriistad {

    private static boolean bMultiFragmentTuvastatud = false;
    private static boolean bMultiFragment = false;
    private static UiDevice ui = null;

    public static void LisaTeosUI(String nimi, String autor, String kommentaar) {
        VajutaLisaTeos();
        TestTooriistad.Oota(100);
        onView(withId(R.id.nimi)).perform(ViewActions.replaceText(nimi));
        onView(withId(R.id.autor)).perform(ViewActions.replaceText(autor));
        onView(withId(R.id.kommentaar)).perform(ViewActions.replaceText(kommentaar), closeSoftKeyboard());
    }

    public static void LisaTehtudHarjutusUI(String nimi, int minuteidmaha, int pikkus) {

        VajutaLisaTehtudHarjutus();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(nimi), closeSoftKeyboard());

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(Tooriistad.MoodustaNihkegaKuupaev(minuteidmaha - pikkus));
        c2.setTime(Tooriistad.MoodustaNihkegaKuupaev(minuteidmaha));

        SeaKuupaev(c1, R.id.lopukuupaev);
        onView(withId(android.R.id.button1)).perform(click());

        SeaKellaaeg(c1, R.id.lopukellaaeg);
        onView(withId(android.R.id.button1)).perform(click());

        SeaKuupaev(c2, R.id.alguskuupaev);
        onView(withId(android.R.id.button1)).perform(click());

        SeaKellaaeg(c2, R.id.alguskellaaeg);
        onView(withId(android.R.id.button1)).perform(click());
    }

    public static void SeaKuupaev(Calendar c, int ressursiid){
        onView(withId(ressursiid)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).
                perform(PickerActions.setDate(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
    }

    public static void SeaKellaaeg(Calendar c, int ressursiid){
        onView(withId(ressursiid)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).
                perform(PickerActions.setTime(c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE)));
    }

    public static void KontrolliAjad(Calendar calendar){
        onView(withId(R.id.alguskuupaev)).check(matches(withText(Tooriistad.KujundaKuupaev(calendar.getTime()))));
        onView(withId(R.id.alguskellaaeg)).check(matches(withText(Tooriistad.KujundaKellaaeg(calendar.getTime()))));
        onView(withId(R.id.lopukuupaev)).check(matches(withText(Tooriistad.KujundaKuupaev(calendar.getTime()))));
        onView(withId(R.id.lopukellaaeg)).check(matches(withText(Tooriistad.KujundaKellaaeg(calendar.getTime()))));

    }

    public static void LisaUusHarjutusSalvestisega(String nimi, int salvetisepikkus) {
        VajutaAlustaUutHarjutust();
        onView(withId(R.id.harjutusekirjeldus)).
                perform(ViewActions.replaceText(nimi), closeSoftKeyboard());
        VajutaMikrofoni();
        VajutaTaimeriNuppu();

        Oota(salvetisepikkus);

        VajutaTaimeriNuppu();
    }

    public static void StatistikaKontroll(Context context){
        // Üldstatistika kontroll
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context.getApplicationContext());
        Date now = new Date();
        int paevasharjutatud = mPPManager.ArvutaPerioodiMinutid(now, now);
        int nadalasharjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaNädalaAlgusKuupaev(now),
                Tooriistad.MoodustaNädalaLopuKuupaev(now));
        int kuusharjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaKuuAlgusKuupaev(now),
                Tooriistad.MoodustaKuuLopuKuupaev(now));

        onView(withId(R.id.paevasharjutatud)).check(matches(withText(String.valueOf(paevasharjutatud) + " m" )));
        onView(withId(R.id.nadalasharjutatud)).check(matches(withText(String.valueOf(nadalasharjutatud) + " m" )));
        onView(withId(R.id.kuusharjutatud)).check(matches(withText(String.valueOf(kuusharjutatud) + " m" )));;
    }

    public static void TeoseStatistikaRiba(Context context, String arv, int minutid){
        onView(withId(R.id.teoseharjutustearv)).check(matches(withText(arv)));
        onView(withId(R.id.teoseharjutustekestus)).
                check(matches(withText(Tooriistad.KujundaHarjutusteMinutid(context, minutid/60))));
    }

    public static void TeosListStatistikaRiba(int pos, String arv, int minutid){
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(pos,R.id.teoslistteoseharjutustearv)).
                check(matches(withText(arv)));
        onView(TestTooriistad.withRecyclerView(R.id.harjutua_list).
                atPositionOnView(pos,R.id.teoslistteoseharjutustekestus)).
                check(matches(withText(Tooriistad.KujundaHarjutusteMinutidTabloo(minutid/60))));
    }

    public static void KalendriStatistikaKontroll(String arv, String minutid){
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(0,R.id.paevakalenderharjutustearv)).
                check(matches(withText(arv)));
        onView(TestTooriistad.withRecyclerView(R.id.kalendri_tabel).
                atPositionOnView(0,R.id.paevakalenderharjutustekestus)).
                check(matches(withText(minutid)));
    }

    public static void AvaSahtelValiKalender() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.harjutuste_kalender));
        TestTooriistad.Oota(1000);
    }

    public static void AvaSahtelValiAruanne() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.saada_aruanne));
        TestTooriistad.Oota(1000);
    }

    public static void AvaSahtelValiSeaded() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.sahtli_navivaade)).perform(NavigationViewActions.navigateTo(R.id.seaded));
        TestTooriistad.Oota(1000);
    }

    public static void VajutaTagasiKui1Fragment() {
        if (!TestTooriistad.OnMultiFragment()) {
            if (BuildConfig.DEBUG) Log.d("VajutaTagasiKui1Frag", "");
            AnnaUiDevice().pressBack();
        }
    }

    public static void VajutaTagasi() {
        if (BuildConfig.DEBUG) Log.d("VajutaTagasi", "");
        AnnaUiDevice().pressBack();
    }

    public static void VajutaKodu() {
        onView(withContentDescription(R.string.test_kodunupp)).perform(click());
    }

    public static DataInteraction LeiaHarjutus(String nimi){
        return onData(harjutusNimega(nimi)).inAdapterView(withId(R.id.harjutuslist));
    }

    public static void ValiTeos(String nimi){
        onView(withId(R.id.harjutua_list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(nimi)), click()).atPosition(0));

    }

    public static void VajutaKoduKui1Fragment() {
        if (!TestTooriistad.OnMultiFragment()) {
            if (BuildConfig.DEBUG) Log.d("VajutaTagasiKui1Frag", "");
            onView(Matchers.anyOf(withContentDescription(R.string.test_kodunupp),
                    withContentDescription(R.string.test_kodunupp2))).perform(click());
        }
    }

    public static void VajutaDialoogOK(){
        onView(withId(android.R.id.button1)).perform(click());
    }
    public static void VajutaDialoogTuhista(){
        onView(withId(android.R.id.button2)).perform(click());
    }

    public static void VajutaLisaTeos(){
        onView(withId(R.id.lisateos)).perform(click());
    }
    public static void VajutaKustutaTeos(){
        onView(withId(R.id.kustutateos)).perform(click());
    }
    public static void VajutaKustutaHarjutus(){
        onView(withId(R.id.kustutaharjutus)).perform(click());
    }
    public static void VajutaAlustaUutHarjutust(){
        onView(withId(R.id.alustauut)).perform(click());
    }
    public static void VajutaLisaTehtudHarjutus(){
        onView(withId(R.id.lisatehtud)).perform(click());
    }
    public static void VajutaJagaSalvestist() {
        onView(withId(R.id.jaga)).perform(click());
    }
    public static void VajutaKustutaSalvestus(){
        onView(withId(R.id.kustutasalvestus)).perform(click());
    }
    public static void VajutaMikrofoni(){
        if(OnReaalneSeade())
            onView(withId(R.id.mikrofoniluliti)).perform(click());
    }
    public static void VajutaTaimeriNuppu(){
        onView(withId(R.id.kaivitataimernupp)).perform(click());
    }
    public static void OnHarjutusMuudaFragment(){
        onView(withId(R.id.HarjutusTabel)).check(matches(isDisplayed()));
    }
    public static void EiOoleHarjutusMuudaFragment(){
        onView(withId(R.id.HarjutusTabel)).check(ViewAssertions.doesNotExist());
    }

    public static void OnHarjutusLisaTehtudFragment(){
        onView(withId(R.id.harjutuseandmed)).check(matches(isDisplayed()));
    }

    public static UiDevice AnnaUiDevice() {
        if (ui == null) {
            ui = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        }
        return ui;
    }

    public static void KeeraVasakule() {
        try {
            TestTooriistad.AnnaUiDevice().setOrientationLeft();
        } catch (Exception e) {
        }
        ;
    }
    public static void KeeraParemale() {
        try {
            TestTooriistad.AnnaUiDevice().setOrientationRight();
        } catch (Exception e) {
        }
        ;
    }
    public static void VabastaKeeramine() {
        try {
            TestTooriistad.AnnaUiDevice().unfreezeRotation();
        } catch (Exception e) {
        }
        ;
    }

    public static void AvaGmail() {
        UiObject allAppsButton = TestTooriistad.AnnaUiDevice().findObject(new UiSelector().text("Gmail"));
        try {
            allAppsButton.clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            assertEquals("Gmail", "Ei leitud");
        }
    }

    public static void AvaPilliPaevik(Context context) {
        UiObject allAppsButton = TestTooriistad.AnnaUiDevice()
                .findObject(new UiSelector().text(context.getResources().getString(R.string.rakenduse_pealkiri)));
        try {
            allAppsButton.clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            assertEquals(context.getResources().getString(R.string.rakenduse_pealkiri), "Ei leitud");
        }
    }
    public static void MultiFragmentTuvastus(ActivityTestRule activityTestRule) {
        if (!bMultiFragmentTuvastatud) {
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

    public static boolean OnMultiFragment() {
        return bMultiFragment;
    }

    public static void Oota(int aeg) {
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES);
        // Ootamine
        EemaldaOotajad();
        IdlingResource idlingResource = new OoteTaimer(aeg);
        Espresso.registerIdlingResources(idlingResource);
    }

    public static boolean OnReaalneSeade(){
        Context context = InstrumentationRegistry.getTargetContext();
        android.content.res.Resources resources = context.getResources();
        return resources.getBoolean(R.bool.reaalne_seade);
    }

    public static void SeadistaSalvestamine(String googlekonto, boolean SS, boolean GDS){
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("googlekonto", googlekonto);
        editor.putBoolean("kaslubadamikrofonigasalvestamine", SS);
        editor.putBoolean("kaskasutadagoogledrive", GDS);
        editor.commit();
    }

    public  static void EemaldaOotajad(){
        for( IdlingResource res : Espresso.getIdlingResources() ){
            Espresso.unregisterIdlingResources(res);
        }
    }


    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {

        return new RecyclerViewMatcher(recyclerViewId);
    }
    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);

            }
            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }

    public static Matcher<Object> harjutusNimega(final String nimi) {
        return new BoundedMatcher<Object, HarjutusKord>(HarjutusKord.class){
            @Override
            public boolean matchesSafely(HarjutusKord harjutusKord){
                return harjutusKord.getHarjutusekirjeldus() !=
                        null ? harjutusKord.getHarjutusekirjeldus() .equalsIgnoreCase(nimi) :
                            nimi.isEmpty() ? true : false;
            }
            @Override
            public void describeTo(Description description){
                description.appendText("with content '" + nimi + "'");
            }
        };
    }

    private static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static void HarjutusPuudub(String harjutusenimi){
        onView(withId(R.id.harjutuslist))
                .check(matches(not(withAdaptedData(harjutusNimega(harjutusenimi)))));
    }
}