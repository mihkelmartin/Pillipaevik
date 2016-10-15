package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.DialogFragment;
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


public class HarjutusLisaTehtudFragment extends HarjutusFragment implements AjaMuutuseTeavitus {

    // Vaate lahtrid
    private TextView alguskuupaevlahter;
    private TextView alguskellaaeglahter;
    private TextView lopukuupaevlahter;
    private TextView lopukellaaeglahter;
    private TextView pikkusminutiteslahter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "savedInstanceState == null");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                setTeosid(algargumendid.getInt("teos_id", 0));
                setHarjutusid(algargumendid.getInt("harjutus_id", 0));
                setItemposition(algargumendid.getInt("item_position", 0));
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    setTeosid(getActivity().getIntent().getIntExtra("teos_id", 0));
                    setHarjutusid(getActivity().getIntent().getIntExtra("harjutus_id", 0));
                    setItemposition(getActivity().getIntent().getIntExtra("item_position", 0));
                }
            }
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Teos : " + getTeosid() + " Harjutus:" + getHarjutusid());
        } else {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Loen savedInstanceState");
            setTeosid(savedInstanceState.getInt("teos_id"));
            setHarjutusid(savedInstanceState.getInt("harjutus_id"));
            setItemposition(savedInstanceState.getInt("item_position", 0));
        }
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
        Teos teos = mPPManager.getTeos(getTeosid());
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getActivity().getApplicationContext());
        setHarjutuskord(harjutuskorradmap.get(getHarjutusid()));
        if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Harjutus : " + getHarjutuskord());

        if (getHarjutuskord() == null && getHarjutusid() == -1) {
            setHarjutuskord(new HarjutusKord(getTeosid()));
            getHarjutuskord().Salvesta(getActivity().getApplicationContext());
            setHarjutusid(getHarjutuskord().getId());
            getHarjutusFragmendiKuulaja().SeaHarjutusid(getHarjutusid());
            getHarjutusFragmendiKuulaja().HarjutusLisatud(getTeosid(), getHarjutusid());
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
        if(getHarjutuskord() != null) {
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
    }

    public void AndmedHarjutusse() {
        getHarjutuskord().setHarjutusekirjeldus(getHarjutusekirjelduslahter().getText().toString());
    }
    private void AndmedHarjutuskorrastVaatele() {
        getHarjutusekirjelduslahter().setText(getHarjutuskord().getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaev(getHarjutuskord().getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(getHarjutuskord().getAlgusaeg()));
        lopukuupaevlahter.setText(Tooriistad.KujundaKuupaev(getHarjutuskord().getLopuaeg()));
        lopukellaaeglahter.setText(Tooriistad.KujundaKellaaeg(getHarjutuskord().getLopuaeg()));
        pikkusminutiteslahter.setText(String.valueOf(getHarjutuskord().ArvutaPikkusminutitesUmardaUles()));
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

        Bundle args = new Bundle();
        DialogFragment muudaFragment = null;
        switch (v.getId()) {
            case R.id.alguskuupaev:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(getHarjutuskord().getAlgusaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.lopukuupaev:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(getHarjutuskord().getLopuaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.alguskellaaeg:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(getHarjutuskord().getAlgusaeg()));
                muudaFragment = new ValiKellaaeg();
                break;
            case R.id.lopukellaaeg:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(getHarjutuskord().getLopuaeg()));
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
                getHarjutuskord().setAlgusaeg(kuupaev);
            } else {
                getHarjutuskord().setLopuaeg(kuupaev);
            }
            // Harjutuse kirjeldus, kui see on antud, tuleb eelnevalt harjutusse kirjutada
            AndmedHarjutusse();
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
        }
    }
    public void MuudaPikkust(View v) {

        Bundle args = new Bundle();
        DialogFragment muudaKestustFragment = new ValiHarjutuseKestus();
        args.putInt("maksimum",getHarjutuskord().ArvutaPikkusMinutites());
        args.putInt("kestus", getHarjutuskord().ArvutaPikkusminutitesUmardaUles());
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
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kustutamine katkestatud:" + getHarjutusid() + " Dialog :" + dialog.getTag());
        } else if (dialog.getTag().equals("Kestusemuutus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kestuse muutus katkestatud:" + getHarjutusid() + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e("HarjutusLisaTehtudFragm", "kuiEiVastus. Tundmatust kohast tuldud !");
        }

    }
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus(Tooriistad.KASUTAJA_KUSTUTAS);
        } else  if (dialog.getTag().equals("Kestusemuutus")) {
            int uuskestus = dialog.getArguments().getInt("kestus");
            if(getHarjutuskord().ArvutaPikkusminutitesUmardaUles() != uuskestus)
                getHarjutuskord().setPikkussekundites(uuskestus * 60);
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
            if(BuildConfig.DEBUG) Log.d("HarjutusLisaTehtudFragm", "Kestuse muutus, uus pikkus:" + uuskestus + " min. " + getHarjutusid() + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e("HarjutusLisaTehtudFragm", "kuiJahVastus. Tundmatust kohast tuldud !");
        }
    }
}
