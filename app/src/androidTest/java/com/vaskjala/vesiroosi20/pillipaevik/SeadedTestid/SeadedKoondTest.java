package com.vaskjala.vesiroosi20.pillipaevik.SeadedTestid;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TestSeadedSisesta.class,
TestSeadedFailis.class,
TestSeadedNaviTiitliTekst.class,
TestSeadedRaadioNupud.class,
TestSeadedEesmargid.class,
TestSeadedHeliFailiPildiOlekKalendril.class,
TestSeadedHeliFailiPildiOlekTeosel.class})

public class SeadedKoondTest {}