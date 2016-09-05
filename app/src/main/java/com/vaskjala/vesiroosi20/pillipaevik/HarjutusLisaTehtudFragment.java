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
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.*;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class HarjutusLisaTehtudFragment extends Fragment implements AjaMuutuseTeavitus, LihtsaKusimuseKuulaja, View.OnClickListener {

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutuskord;
    private HarjutusLisaTehtudFragmendiKuulaja harjutusLisaTehtudFragmendiKuulaja;

    // Vaate lahtrid
    private EditText harjutusekirjelduslahter;
    private TextView alguskuupaevlahter;
    private TextView alguskellaaeglahter;
    private TextView lopukuupaevlahter;
    private TextView lopukellaaeglahter;
    private TextView pikkusminutiteslahter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            harjutusLisaTehtudFragmendiKuulaja = (HarjutusLisaTehtudFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusLisaTehtudFragmendiKuulaja");
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            harjutusLisaTehtudFragmendiKuulaja = (HarjutusLisaTehtudFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusLisaTehtudFragmendiKuulaja");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "savedInstanceState == null");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                this.teosid = algargumendid.getInt("teos_id", 0);
                this.harjutusid = algargumendid.getInt("harjutus_id", 0);
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    this.teosid = getActivity().getIntent().getIntExtra("teos_id", 0);
                    this.harjutusid = getActivity().getIntent().getIntExtra("harjutus_id", 0);
                }
            }
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        } else {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
        }
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getActivity().getApplicationContext());
        this.harjutuskord = harjutuskorradmap.get(this.harjutusid);
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Harjutus : " + this.harjutuskord);

        if (this.harjutuskord == null && this.harjutusid == -1) {
            this.harjutuskord = new HarjutusKord(this.teosid);
            harjutuskord.Salvesta(getActivity().getApplicationContext());
            this.harjutusid = this.harjutuskord.getId();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_harjutuslisatehtud, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        harjutusekirjelduslahter = (EditText) getView().findViewById(R.id.harjutusekirjeldus);
        alguskuupaevlahter = (TextView) getView().findViewById(R.id.alguskuupaev);
        alguskellaaeglahter = (TextView) getView().findViewById(R.id.alguskellaaeg);
        lopukuupaevlahter = (TextView) getView().findViewById(R.id.lopukuupaev);
        lopukellaaeglahter = (TextView) getView().findViewById(R.id.lopukellaaeg);
        pikkusminutiteslahter = (TextView) getView().findViewById(R.id.pikkusminutites);

        alguskuupaevlahter.setOnClickListener(this);
        alguskellaaeglahter.setOnClickListener(this);
        lopukuupaevlahter.setOnClickListener(this);
        lopukellaaeglahter.setOnClickListener(this);
        pikkusminutiteslahter.setOnClickListener(this);

        AndmedHarjutuskorrastVaatele();
    }
    @Override
    public void onPause() {

        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm","onPause");
        super.onPause();
        if(this.harjutuskord != null) {
            SalvestaHarjutus();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "onSaveInstanceState " + this.teosid + " " + this.harjutusid);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.harjutusmenyy, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.kustutaharjutus) {
            Bundle args = new Bundle();
            args.putString("kysimus", "Kustutad Harjutuse ?");
            args.putString("jahvastus", "Jah");
            args.putString("eivastus", "Ei");
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getChildFragmentManager(), "KustutaHarjutus");
        }
        return super.onOptionsItemSelected(item);
    }

    public void SuleHarjutus(){
        if(AndmedHarjutuses()) {
            // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
            String kirjeldus = harjutusekirjelduslahter.getText().toString();
            if (kirjeldus.isEmpty())
                harjutusekirjelduslahter.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
            SalvestaHarjutus();
        } else {
            KustutaHarjutus();
        }
    }
    private void AndmedHarjutusse() {
        this.harjutuskord.setHarjutusekirjeldus(harjutusekirjelduslahter.getText().toString());
    }
    private void AndmedHarjutuskorrastVaatele() {
        harjutusekirjelduslahter.setText(harjutuskord.getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        lopukuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getLopuaeg()));
        lopukellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getLopuaeg()));
        pikkusminutiteslahter.setText(String.valueOf(harjutuskord.ArvutaPikkusminutitesUmardaUles()));
    }
    private void SalvestaHarjutus (){
        AndmedHarjutusse();
        harjutuskord.Salvesta(getActivity().getApplicationContext());
    }
    private void KustutaHarjutus(){
        harjutuskord.Kustuta(getActivity().getApplicationContext());
        this.harjutuskord = null;
    }
    private boolean AndmedHarjutuses(){
        return harjutuskord.getPikkussekundites() != 0 || !harjutuskord.getAlgusaeg().equals(harjutuskord.getLopuaeg());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alguskuupaev:
            case R.id.lopukuupaev:
            case R.id.alguskellaaeg:
            case R.id.lopukellaaeg:
                MuudaKuupaeva(v);
                break;
            case R.id.pikkusminutites:
                MuudaPikkust(v);
                break;
            default:
                break;
        }
    }
    public void MuudaKuupaeva(View v) {

        // Salvesta harjutuse kirjeldus
        AndmedHarjutusse();

        Bundle args = new Bundle();
        DialogFragment muudaFragment = null;
        switch (v.getId()) {
            case R.id.alguskuupaev:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getAlgusaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.lopukuupaev:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLopuaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.alguskellaaeg:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getAlgusaeg()));
                muudaFragment = new ValiKellaaeg();
                break;
            case R.id.lopukellaaeg:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLopuaeg()));
                muudaFragment = new ValiKellaaeg();
                break;
            default:
                break;
        }
        assert muudaFragment != null;
        muudaFragment.setArguments(args);
        muudaFragment.show(getChildFragmentManager(), "Ajamuutus");

    }
    @Override
    public void AegMuudetud(Date kuupaev, boolean muudaalgust) {

        String VeaTeade;
        if (!(VeaTeade = AjaMuutusKeelatud(kuupaev)).isEmpty()) {
            Tooriistad.NaitaHoiatust(getActivity(), "", VeaTeade);
        } else {
            if (muudaalgust) {
                harjutuskord.setAlgusaeg(kuupaev);
            } else {
                harjutuskord.setLopuaeg(kuupaev);
            }
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
        }
    }
    public void MuudaPikkust(View v) {

        Bundle args = new Bundle();
        DialogFragment muudaKestustFragment = new ValiHarjutuseKestus();
        args.putInt("maksimum",this.harjutuskord.ArvutaPikkusMinutites());
        args.putInt("kestus", this.harjutuskord.ArvutaPikkusminutitesUmardaUles());
        // Kui nimi on antud siis tuleb see harjutuskord objekti viia sest
        // kui tagasi tullakse siis viikse andmed objektist vaatele
        AndmedHarjutusse();
        muudaKestustFragment.setArguments(args);
        muudaKestustFragment.show(getChildFragmentManager(),"Kestusemuutus");

    }
    // Abimeetod. Kasutusel meetodis MuudetudKuupaev
    private String AjaMuutusKeelatud(Date kuupaev) {
        String retVal = "";
        if (kuupaev.after(Calendar.getInstance().getTime()))
            retVal = getString(R.string.ajamuutmine_aeg_tulevikus);
        return retVal;
    }


    // Dialoogi vastused
    public void kuiEiVastus(DialogFragment dialog) {

        if (dialog.getTag().equals("KustutaHarjutus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kustutamine katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else if (dialog.getTag().equals("Kestusemuutus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kestuse muutus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e("HarjutusLisaTehtudFragm", "kuiEiVastus. Tundmatust kohast tuldud !");
        }

    }
    public void kuiJahVastus(DialogFragment dialog) {

        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus();
            harjutusLisaTehtudFragmendiKuulaja.KustutaHarjutus(this.harjutusid);
        } else  if (dialog.getTag().equals("Kestusemuutus")) {
            int uuskestus = dialog.getArguments().getInt("kestus");
            if(harjutuskord.ArvutaPikkusminutitesUmardaUles() != uuskestus)
                this.harjutuskord.setPikkussekundites(uuskestus * 60);
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kestuse muutus, uus pikkus:" + uuskestus + " min. " + this.harjutusid + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e("HarjutusLisaTehtudFragm", "kuiJahVastus. Tundmatust kohast tuldud !");
        }

    }

}
