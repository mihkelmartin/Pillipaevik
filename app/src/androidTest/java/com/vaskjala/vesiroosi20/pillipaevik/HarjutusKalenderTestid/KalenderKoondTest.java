package com.vaskjala.vesiroosi20.pillipaevik.HarjutusKalenderTestid;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TestKalenderMuudaEsimeneHarjutus.class,
        TestKalenderRidaSulgubFragmentKaob.class,
        TestKalenderSalvestiseKustutamine.class,
        TestKalenderHarjutuseKustutamine.class,
        TestKalenderJagaSalvestust.class,
        TestKalenderJagaAndmedPuudu.class})

public class KalenderKoondTest {}