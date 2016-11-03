package com.vaskjala.vesiroosi20.pillipaevik.HarjutusTestid;


import com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TestHarjutusLisaSalvestisega.class,
        TestHarjutusSalvestiseLinkAruandele.class,
        TestHarjutusKustutaSalvestis.class,
        TestHarjutusJagaSalvestist.class,
        TestHarjutusHarjutuseKustutamine.class,
        TestHarjutusLisaKaduv.class,
        TestHarjutusLisaKaksSalvestisega.class,
        TestHarjutusKustutaMangivSalvestis.class,
        TestHarjutusKustutaSalvestav.class,
        TestHarjutusUusAutoStart.class,
        TestHarjutusKustutaTeos.class,
        TestHarjutusUueNupud.class,
        TestHarjutusUueNupudKaotaActivity.class})

public class HarjutusKoondTest {}