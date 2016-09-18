package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.vaskjala.vesiroosi20.pillipaevik.*;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;

/**
 * Created by mihkel on 18.09.2016.
 */
public class HarjutusFragment extends Fragment implements LihtsaKusimuseKuulaja,
        View.OnClickListener, HarjutusFragmendiKutsuja {

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutuskord;

    private HarjutusFragmendiKuulaja harjutusFragmendiKuulaja;

    public int getTeosid() {
        return teosid;
    }

    public void setTeosid(int teosid) {
        this.teosid = teosid;
    }

    public int getHarjutusid() {
        return harjutusid;
    }

    public void setHarjutusid(int harjutusid) {
        this.harjutusid = harjutusid;
    }

    public HarjutusKord getHarjutuskord() {
        return harjutuskord;
    }

    public void setHarjutuskord(HarjutusKord harjutuskord) {
        this.harjutuskord = harjutuskord;
    }

    public HarjutusFragmendiKuulaja getHarjutusFragmendiKuulaja() {
        return harjutusFragmendiKuulaja;
    }

    public void setHarjutusFragmendiKuulaja(HarjutusFragmendiKuulaja harjutusFragmendiKuulaja) {
        this.harjutusFragmendiKuulaja = harjutusFragmendiKuulaja;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            harjutusFragmendiKuulaja = (HarjutusFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusFragmendiKuulaja");
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            harjutusFragmendiKuulaja = (HarjutusFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusFragmendiKuulaja");
        }
    }

    public void onPause() {
        if(BuildConfig.DEBUG) Log.d("HarjutusFragment","onPause");
        super.onPause();
        SalvestaHarjutus();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("teos_id", getTeosid());
        savedInstanceState.putInt("harjutus_id", getHarjutusid());
        if(BuildConfig.DEBUG) Log.d("HarjutusFragment", "onSaveInstanceState: " + getTeosid() + " " + getHarjutusid());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.harjutusmenyy, menu);
        if(BuildConfig.DEBUG) Log.d("HarjutusFragment", "onCreateOptionsMenu");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(BuildConfig.DEBUG) Log.d("HarjutusFragment", "onOptionsItemSelected");
        if (item.getItemId() == R.id.kustutaharjutus) {
            Bundle args = new Bundle();

            String kysimys = getString(R.string.dialog_kas_kustuta_harjutuse_kusimus);
            String Harjutusekirjeldus = ((EditText)getView().findViewById(R.id.harjutusekirjeldus)).getText().toString();
            if(Harjutusekirjeldus != null && !Harjutusekirjeldus.isEmpty())
                kysimys = kysimys + " \"" + Harjutusekirjeldus + "\"";
            kysimys = kysimys + " ?";

            args.putString("kysimus", kysimys);
            args.putString("jahvastus", getString(R.string.jah));
            args.putString("eivastus", getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getChildFragmentManager(), "KustutaHarjutus");
        }
        return super.onOptionsItemSelected(item);
    }

    public void AndmedHarjutusse() {

    }

    public void SuleHarjutus(){
        if(AndmedHarjutuses()) {
            EditText Harjutusekirjeldus = (EditText) getView().findViewById(R.id.harjutusekirjeldus);
            String kirjeldus = Harjutusekirjeldus.getText().toString();
            if (kirjeldus.isEmpty())
                Harjutusekirjeldus.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
            SalvestaHarjutus();
        } else {
            KustutaHarjutus();
        }
    }

    public void SalvestaHarjutus (){
        if(KasHarjutusOlemas()) {
            AndmedHarjutusse();
            harjutuskord.Salvesta(getActivity().getApplicationContext());
            harjutusFragmendiKuulaja.VarskendaHarjutusteList();
        }
    }

    public void KustutaHarjutus(){
        harjutuskord.Kustuta(getActivity().getApplicationContext());
        this.harjutuskord = null;
        harjutusFragmendiKuulaja.VarskendaHarjutusteList();
    }

    private boolean AndmedHarjutuses(){
        return harjutuskord.getPikkussekundites() != 0 || !harjutuskord.getAlgusaeg().equals(harjutuskord.getLopuaeg());
    }

    private boolean KasHarjutusOlemas(){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
        return mPPManager.getHarjutus(this.teosid, this.harjutusid) != null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {

    }

    @Override
    public void kuiEiVastus(DialogFragment dialog) {

    }
}
