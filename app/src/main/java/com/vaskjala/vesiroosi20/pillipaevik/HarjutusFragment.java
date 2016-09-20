package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
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
    private int itemposition;
    private HarjutusKord harjutuskord;
    private EditText harjutusekirjelduslahter;

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

    public int getItemposition() {
        return itemposition;
    }

    public void setItemposition(int itemposition) {
        this.itemposition = itemposition;
    }

    public HarjutusKord getHarjutuskord() {
        return harjutuskord;
    }

    public void setHarjutuskord(HarjutusKord harjutuskord) {
        this.harjutuskord = harjutuskord;
    }

    public EditText getHarjutusekirjelduslahter() {
        return harjutusekirjelduslahter;
    }

    public void setHarjutusekirjelduslahter(EditText harjutusekirjelduslahter) {
        this.harjutusekirjelduslahter = harjutusekirjelduslahter;
    }

    public HarjutusFragmendiKuulaja getHarjutusFragmendiKuulaja() {
        return harjutusFragmendiKuulaja;
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHarjutusekirjelduslahter((EditText) getView().findViewById(R.id.harjutusekirjeldus));
    }

    public void onPause() {
        if(BuildConfig.DEBUG) Log.d("HarjutusFragment","onPause");
        super.onPause();
        SalvestaHarjutus();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("teos_id", getTeosid());
        savedInstanceState.putInt("harjutus_id", getHarjutusid());
        savedInstanceState.putInt("item_position", getItemposition());
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

    public boolean SuleHarjutus(){
        boolean retVal = true;
        if(AndmedHarjutuses()) {
            String kirjeldus = getHarjutusekirjelduslahter().getText().toString();
            if (kirjeldus.isEmpty())
                getHarjutusekirjelduslahter().setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
            SalvestaHarjutus();
        } else {
            retVal = false;
            KustutaHarjutus();
        }
        return retVal;
    }

    public void SalvestaHarjutus (){
        if(KasHarjutusOlemas()) {
            AndmedHarjutusse();
            harjutuskord.Salvesta(getActivity().getApplicationContext());
            harjutusFragmendiKuulaja.HarjutusMuudetud(getTeosid(), getHarjutusid(), getItemposition());
        }
    }

    public void KustutaHarjutus(){
        harjutuskord.Kustuta(getActivity().getApplicationContext());
        this.harjutuskord = null;
        harjutusFragmendiKuulaja.HarjutusKustutatud(getTeosid(), getHarjutusid(), getItemposition());
    }

    private boolean AndmedHarjutuses(){
        return harjutuskord.getPikkussekundites() != 0 || !harjutuskord.getAlgusaeg().equals(harjutuskord.getLopuaeg());
    }

    private boolean KasHarjutusOlemas(){
        // Peab nii küsima, sest teose kustutamisel kustutatakse otse andmebaasist
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
