package com.vaskjala.vesiroosi20.pillipaevik;


import com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid.AruanneKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.KalenderKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid.HarjutusKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid.SeadedKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.TeosTestid.TeosKoondTest;
import com.vaskjala.vesiroosi20.pillipaevik.TeosteListTestid.TestTeosteListKustutaTeosed;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({LooTestAndmedUI.class,
        SeadedKoondTest.class,
        TeaveTest.class,
        KalenderKoondTest.class,
        AruanneKoondTest.class,
        TeosKoondTest.class,
        HarjutusKoondTest.class,
        TestTeosteListKustutaTeosed.class})

public class KoondTest  {}