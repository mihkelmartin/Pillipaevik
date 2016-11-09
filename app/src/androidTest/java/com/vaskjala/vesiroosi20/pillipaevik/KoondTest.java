package com.vaskjala.vesiroosi20.pillipaevik;


import com.vaskjala.vesiroosi20.pillipaevik.ArhiivTestid.ArhiivKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid.AruanneKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.KalenderKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusLisaTehtudTestid.HarjutusLisaTehtudKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid.HarjutusKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid.SeadedKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid.TestSeadedGoogleKontoValimine;
import com.vaskjala.vesiroosi20.pillipaevik.TeosTestid.TeosKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.TeosteListTestid.TestTeosteListKustutaTeosed;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestPuhastaEnneTeste.class,
        TestSeadedGoogleKontoValimine.class,
        LooTestAndmedUI.class,
        SeadedKoondTest.class,
        TeaveTest.class,
        KalenderKoondTest.class,
        AruanneKoondTest.class,
        TeosKoondTest.class,
        HarjutusKoondTest.class,
        HarjutusLisaTehtudKoondTest.class,
        TestTeosteListKustutaTeosed.class,
        ArhiivKoondTest.class})

public class KoondTest  {}