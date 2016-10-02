package com.vaskjala.vesiroosi20.pillipaevik.AruanneTestid;


import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.TestKalenderHarjutuseKustutamine;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.TestKalenderMuudaEsimeneHarjutus;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.TestKalenderRidaSulgubFragmentKaob;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid.TestKalenderSalvestiseKustutamine;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TestAruanneLoobuPraeguneKuu.class,
        TestAruanneSaadaPraeguneKuu.class})

public class AruanneKoondTest {}