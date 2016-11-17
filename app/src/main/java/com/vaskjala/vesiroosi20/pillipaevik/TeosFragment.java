package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class TeosFragment extends Fragment implements LihtsaKusimuseKuulaja, View.OnFocusChangeListener,
        View.OnClickListener {


    private PilliPaevikDatabase mPPManager;
    public HarjutuskorradAdapter pHarjutusedAdapter = null;
    private int teosid;
    private Teos teos;
    private boolean bUueTeoseLoomine = false;
    private TeosFragmendiKuulaja teosFragmendiKuulaja;

    // Vaate lahtrid
    private CheckBox mArhiivis;
    private EditText mNimi;
    private EditText mAutor;
    private EditText mKommentaar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            teosFragmendiKuulaja = (TeosFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama TeosFragmendiKuulaja");
        }
    }
    @SuppressWarnings("deprecation")
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            teosFragmendiKuulaja = (TeosFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama TeosFragmendiKuulaja");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("TeosFragment", "Instance ei ole salvestatud, loen Bundle objektist");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                this.teosid = algargumendid.getInt("item_id", 0);
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    this.teosid = getActivity().getIntent().getIntExtra("item_id", 0);
                }
            }
        } else {
            if(BuildConfig.DEBUG) Log.d("TeosFragment", "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teoseid");
            this.bUueTeoseLoomine = savedInstanceState.getBoolean("uusteos");
        }
        this.teos = mPPManager.getTeos(teosid);
        if(this.teosid == -1 && this.teos == null){
            this.bUueTeoseLoomine = true;
            this.teos = new Teos();
            teos.Salvesta(getActivity().getApplicationContext());
            this.teosid = teos.getId();
            teosFragmendiKuulaja.SeaTeosid(this.teosid);
        }
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "Loen teost:" + String.valueOf(this.teosid) + " " + this.teos);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_teos, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNimi = (EditText) getView().findViewById(R.id.nimi);
        mAutor = (EditText) getView().findViewById(R.id.autor);
        mKommentaar = (EditText) getView().findViewById(R.id.kommentaar);
        mArhiivis = ((CheckBox) getView().findViewById(R.id.arhiivis));

        mNimi.setText(this.teos.getNimi());
        mAutor.setText(this.teos.getAutor());
        mKommentaar.setText(this.teos.getKommentaar());
        mArhiivis.setChecked(this.teos.getKasutusviis() == 0);

        mNimi.setOnFocusChangeListener(this);
        mArhiivis.setOnClickListener(this);

        LooHarjutusteAdapter();
        HarjutusteStatistika ();
    }
    @Override
    public void onPause() {
        if(BuildConfig.DEBUG) Log.d("TeosFragment","onPause");
        super.onPause();
        if(mPPManager.getTeos(this.teosid) != null) {
            SalvestaTeos();
        } else {
            if (BuildConfig.DEBUG) Log.d("TeosFragment", "onPause: teos == null");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG) Log.d("TeosFragment", "onSaveInstanceState :" + this.teosid);

        savedInstanceState.putInt("teoseid", this.teosid);
        savedInstanceState.putBoolean("uusteos", this.bUueTeoseLoomine);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.teosmenyy, menu);
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "onCreateOptionsMenu");
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if(BuildConfig.DEBUG) Log.d("TeosFragment","Menüü valik vajutatud");

        if(item.getItemId() == R.id.kustutateos){
            Bundle args = new Bundle();
            args.putString("pealkiri",getString(R.string.dialog_kas_kustuta_teose_pealkiri));

            String kysimys = getString(R.string.dialog_kas_kustuta_teose_kusimus_osa1);
            String nimi = mNimi.getText().toString();
            if(nimi != null && !nimi.isEmpty())
                kysimys = kysimys + " \"" + nimi + "\"";
            kysimys = kysimys + " " + getString(R.string.dialog_kas_kustuta_teose_kusimus_osa2);


            args.putString("kysimus",kysimys);
            args.putString("jahvastus",getString(R.string.jah));
            args.putString("eivastus",getString(R.string.ei));
            android.app.DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getChildFragmentManager(), "Kustuta teos");
        }
        if(item.getItemId() == R.id.alustauut){
            SalvestaTeos();
            teosFragmendiKuulaja.AlustaHarjutust(this.teosid);
        }
        if(item.getItemId() == R.id.lisatehtud){
            SalvestaTeos();
            teosFragmendiKuulaja.LisaTehtudHarjutus(this.teosid);
        }
        return super.onOptionsItemSelected(item);
    }

    private void LooHarjutusteAdapter(){
        List<HarjutusKord> harjutuskorrad = this.teos.getHarjustuskorrad(getActivity().getApplicationContext());
        pHarjutusedAdapter=
                new HarjutuskorradAdapter(getActivity().getApplicationContext(),
                        (ArrayList<HarjutusKord>) harjutuskorrad);
        ListView HarjutusteList = (ListView) getView().findViewById(R.id.harjutuslist);
        HarjutusteList.setOnItemClickListener(new HarjutusedListiPassija());
        HarjutusteList.setAdapter(pHarjutusedAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arhiivis:
                AndmedTeosesse(teos);
                teosFragmendiKuulaja.MuudaTeos(teos);
                break;
            default:
                break;
        }
    }

    private class HarjutuskorradAdapter extends ArrayAdapter<HarjutusKord> {
        private ArrayList<HarjutusKord> harjutuskorrad;
        public HarjutuskorradAdapter(Context context, ArrayList<HarjutusKord> harjutuskorrad) {
            super(context, 0, harjutuskorrad);
            this.harjutuskorrad = harjutuskorrad;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            HarjutusKord harjutuskord = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.harjutus_list_rida, parent, false);
            }
            TextView kirjeldus = (TextView) convertView.findViewById(R.id.harjutuslist_harjutusekirjeldus);
            TextView kestus = (TextView) convertView.findViewById(R.id.harjutuslist_kestus);
            TextView kuupaev = (TextView) convertView.findViewById(R.id.harjutuslist_kuupaev);

            kirjeldus.setText(harjutuskord.getHarjutusekirjeldus());
            String pikkus = Tooriistad.KujundaAeg(harjutuskord.getPikkussekundites()*1000);
            kestus.setText(pikkus);
            kuupaev.setText(Tooriistad.KujundaKuupaevSonaline(harjutuskord.getAlgusaeg()));
            if(harjutuskord.KasKuvadaSalvestusePilt(getActivity().getApplicationContext()))
                convertView.findViewById(R.id.harjutuslisti_pilt).setVisibility(View.VISIBLE);
            else
                convertView.findViewById(R.id.harjutuslisti_pilt).setVisibility(View.GONE);

            return convertView;
        }
    }
    private class HarjutusedListiPassija implements  ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            HarjutusKord mHarjutuskord = (HarjutusKord)parent.getItemAtPosition(position);
            teosFragmendiKuulaja.HarjutusValitud(teosid, mHarjutuskord.getId());
        }

    }
    private class HarjutusComparator implements Comparator<HarjutusKord>{
        @Override
        public int compare(HarjutusKord lhs, HarjutusKord rhs) {
            return rhs.getAlgusaeg().compareTo(lhs.getAlgusaeg());
        }
    }

    public void VarskendaHarjutusteJaStatistika() {
        if (pHarjutusedAdapter != null){
            pHarjutusedAdapter.notifyDataSetChanged();
            pHarjutusedAdapter.sort(new TeosFragment.HarjutusComparator());
        }
        HarjutusteStatistika ();
    }

    private void SalvestaTeos(){
        boolean bnimiMuutunud = TeoseNimiMuutunud();
        AndmedTeosesse(this.teos);
        teos.Salvesta(getActivity().getApplicationContext());
        if(bnimiMuutunud)
            teosFragmendiKuulaja.MuudaTeos(teos);
    }

    private void AndmedTeosesse(Teos teos) {

        teos.setNimi(mNimi.getText().toString());
        teos.setAutor(mAutor.getText().toString());
        teos.setKommentaar(mKommentaar.getText().toString());
        teos.setKasutusviis( (short)(mArhiivis.isChecked() ? 0 : 1));

        if (BuildConfig.DEBUG) Log.d("TeosFragment", "Andmed teosesse: " + teos.toString());

    }

    public boolean TeoseNimiMuutunud(){
        return teos.getNimi() != null ?
                !teos.getNimi().equals(mNimi.getText().toString()) : mNimi.getText().toString() != null;
    }

    public void KustutaTeos() {
        teos.Kustuta(getActivity().getApplicationContext());
        teosFragmendiKuulaja.KustutaTeos(this.teos, 0);
    }

    private void HarjutusteStatistika (){
        int[] stat = mPPManager.TeoseHarjutusKordadeStatistika(this.teosid);
        ((TextView) getView().findViewById(R.id.teoseharjutustearv)).setText(String.valueOf(stat[1]));
        ((TextView) getView().findViewById(R.id.teoseharjutustekestus))
                .setText(Tooriistad.KujundaHarjutusteMinutid(getActivity().getApplicationContext(), stat[0]/60));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "onFocusChange: " + v + " " + hasFocus);
        if(!hasFocus && v == mNimi && TeoseNimiMuutunud())
            SalvestaTeos();
    }

    // Dialoogi vastused kustutamise korral
    @Override
    public void kuiEiVastus(android.app.DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "Kustutamine katkestatud:" + this.teosid);
    }

    @Override
    public void kuiJahVastus(android.app.DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "Kustutamise kuiJahVastus");
        KustutaTeos();
    }
}
