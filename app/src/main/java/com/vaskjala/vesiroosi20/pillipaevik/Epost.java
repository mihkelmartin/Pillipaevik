package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by mihkel on 4.06.2016.
 */
public class Epost {

    private String saaja;
    private String teema;
    private String sisu;


    public Epost(String saaja, String teema, String sisu) {
        this.saaja = saaja;
        this.teema = teema;
        this.sisu = sisu;
    }

    public void Saada(Context context){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{saaja});
        i.putExtra(Intent.EXTRA_SUBJECT, teema);
        i.putExtra(Intent.EXTRA_TEXT   , sisu);
        try {
            context.startActivity(Intent.createChooser(i, "Saada aruanne..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}
