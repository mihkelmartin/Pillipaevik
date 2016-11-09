package com.vaskjala.vesiroosi20.pillipaevik.ArhiivTestid;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({TestArhiivTeosArhiiviMuutmine.class,
TestArhiivTeosArhiivKustutamine.class,
TestArhiivTeosArhiiviMuutmineKalender.class,
TestArhiivTeosArhiivUueLisamine.class,
TestArhiivTeosArhiivLisaTuhjad.class})

public class ArhiivKoondTest {}