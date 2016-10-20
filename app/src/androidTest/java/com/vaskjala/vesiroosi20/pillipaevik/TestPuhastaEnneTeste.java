package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static junit.framework.Assert.assertEquals;

/**
 * Created by mihkel on 28.09.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestPuhastaEnneTeste {

    @Test
    public void Puhasta(){
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        SharedPreferences sharedPref = context.getSharedPreferences(resources.getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("minueesnimi", "");
        editor.putString("minuperenimi", "");
        editor.putString("minuepost", "");
        editor.putString("muusikakool", "");
        editor.putString("klass", "");
        editor.putString("minuinstrument", "");
        editor.putString("opetajaeesnimi", "");
        editor.putString("opetajaperenimi", "");
        editor.putString("opetajaepost", "");
        editor.putInt("paevasharjutada", 0);
        if(TestTooriistad.OnReaalneSeade()){
            editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
            editor.putBoolean("kaskasutadagoogledrive", true);
        } else {
            editor.putBoolean("kaslubadamikrofonigasalvestamine", true);
            editor.putBoolean("kaskasutadagoogledrive", false);
            editor.putString("googlekonto", "");
        }
        editor.commit();

        PilliPaevikDatabase pilliPaevikDatabase = new PilliPaevikDatabase(context);
        List<Teos> teosList =  new ArrayList<Teos>(pilliPaevikDatabase.getAllTeosed());
        for( Teos teos : teosList ){
            teos.Kustuta(context);
        }
    }
}
