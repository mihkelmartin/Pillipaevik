package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.GoogleDriveUhendus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class HarjutusMuudaFragment extends Fragment implements LihtsaKusimuseKuulaja, View.OnClickListener,
        HarjutusFragmendiKutsuja{

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutuskord;
    private int itemposition;
    private AvaFailMangimiseks mAFM;

    private HarjutusFragmendiKuulaja harjutusFragmendiKuulaja;

    // Vaate lahtrid
    private EditText harjutusekirjelduslahter;
    private TextView alguskuupaevlahter;
    private TextView alguskellaaeglahter;
    private TextView pikkusminutiteslahter;
    private CheckBox weblinkaruandele;

    private MediaPlayer mPlayer = null;
    private FileDescriptor mHeliFail = null;

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
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "savedInstanceState == null");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                this.teosid = algargumendid.getInt("teos_id", 0);
                this.harjutusid = algargumendid.getInt("harjutus_id", 0);
                this.itemposition = algargumendid.getInt("item_position", 0);
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    this.teosid = getActivity().getIntent().getIntExtra("teos_id", 0);
                    this.harjutusid = getActivity().getIntent().getIntExtra("harjutus_id", 0);
                    this.itemposition = getActivity().getIntent().getIntExtra("item_position", 0);
                }
            }
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        } else {
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
            this.itemposition = savedInstanceState.getInt("item_position");
        }
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
        this.harjutuskord = mPPManager.getHarjutus(this.teosid, this.harjutusid);
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Harjutus : " + this.harjutuskord);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_harjutusmuuda, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        harjutusekirjelduslahter = (EditText) getView().findViewById(R.id.harjutusekirjeldus);
        alguskuupaevlahter = (TextView) getView().findViewById(R.id.alguskuupaev);
        alguskellaaeglahter = (TextView) getView().findViewById(R.id.alguskellaaeg);
        pikkusminutiteslahter = (TextView) getView().findViewById(R.id.pikkusminutites);
        weblinkaruandele = (CheckBox) getView().findViewById(R.id.weblinkaruandele);

        getView().findViewById(R.id.mangi).setOnClickListener(this);
        getView().findViewById(R.id.stopp).setOnClickListener(this);;
        getView().findViewById(R.id.jaga).setOnClickListener(this);;
        getView().findViewById(R.id.kustutasalvestus).setOnClickListener(this);;

        AndmedHarjutuskorrastVaatele();
    }
    @Override
    public void onStart() {

        mAFM = null;
        if(Tooriistad.kasKasutadaGoogleDrive(getActivity().getApplicationContext())) {
            if (harjutuskord != null && harjutuskord.getHelifailidriveid() != null &&
                    !harjutuskord.getHelifailidriveid().isEmpty()) {
                mAFM = new AvaFailMangimiseks();
                mAFM.execute(harjutuskord);
            }
        } else if(Tooriistad.KasLubadaSalvestamine(getActivity().getApplicationContext())){
            if (harjutuskord != null && harjutuskord.getHelifail() != null &&
                    !harjutuskord.getHelifail().isEmpty()) {
                try {
                    FileInputStream in = new FileInputStream(getActivity().getFilesDir().getPath() + "/" + harjutuskord.getHelifail());
                    mHeliFail = in.getFD();
                    SeadistaSalvestiseRiba();
                } catch (IOException e){
                    if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Helifaili avamise viga: " + e.toString());
                }
            }
        }
        super.onStart();
    }
    @Override
    public void onPause() {
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment","onPause");
        super.onPause();
        SalvestaHarjutus();
    }
    @Override
    public void onStop() {
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "onStop");

        if(mAFM != null)
            mAFM.cancel(false);

        if (mPlayer != null) {
            if(mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Lõpetasin mahamängimise");
        }
        super.onStop();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        savedInstanceState.putInt("item_position", this.itemposition);
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "onSaveInstanceState: " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.harjutusmenyy, menu);
        if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "onCreateOptionsMenu");
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.kustutaharjutus) {
            Bundle args = new Bundle();
            args.putString("kysimus", getString(R.string.dialog_kas_kustuta_harjutuse_kusimus));
            args.putString("jahvastus", getString(R.string.jah));
            args.putString("eivastus", getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getChildFragmentManager(), "KustutaHarjutus");
        }
        return super.onOptionsItemSelected(item);
    }

    public void SuleHarjutus(){
        if(AndmedHarjutuses()) {
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
        int uWebLinkAruandele = weblinkaruandele.isChecked() ? 1 : 0;
        this.harjutuskord.setWeblinkaruandele(uWebLinkAruandele);
    }
    private void AndmedHarjutuskorrastVaatele() {
        harjutusekirjelduslahter.setText(harjutuskord.getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaevSonaline(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        pikkusminutiteslahter.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(harjutuskord.ArvutaPikkusminutitesUmardaUles()));
        weblinkaruandele.setChecked(harjutuskord.getWeblinkaruandele()==1);
    }
    private void SalvestaHarjutus (){
        if(KasHarjutusOlemas()) {
            AndmedHarjutusse();
            harjutuskord.Salvesta(getActivity().getApplicationContext());
            harjutusFragmendiKuulaja.VarskendaHarjutusteList();
        }
    }
    private void KustutaHarjutus(){
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
        switch (v.getId()) {
            case R.id.mangi:
            case R.id.stopp:
                MangiLugu(v);
                break;
            case R.id.jaga:
                JagaLugu(v);
                break;
            case R.id.kustutasalvestus:
                KustutaSalvestusKlikk(v);
                break;
            default:
                break;
        }
    }
    public void MangiLugu(View v){

        if(v.getId() == R.id.stopp){
            if (mPlayer != null) {
                if(mPlayer.isPlaying())
                    mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
                if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Lõpetasin mahamängimise");
            }
        } else {
            if (mPlayer != null) {
                mPlayer.stop();
                try {
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Log.e("HarjutusMuudaFragment", "Viga uuesti algusest alustamise" + e.toString());
                    mPlayer = null;
                }
                if (BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "Alustasin mängimist algusest");
            } else {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(mHeliFail);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Log.e("HarjutusMuudaFragment", "Viga mahamängimisel" + e.toString());
                    mPlayer = null;
                }
            }
        }
    }
    public void JagaLugu(View v){
        if(Tooriistad.kasNimedEpostOlemas(getActivity().getApplicationContext())) {
            Bundle args = new Bundle();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, MoodustaJagamiseTeema());
            i.putExtra(Intent.EXTRA_TEXT, MoodustaJagamiseTekst());
            try {
                startActivity(Intent.createChooser(i, getString(R.string.aruanne_saada)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity().getParent(), getString(R.string.aruanne_eposti_rakendus_puudub), Toast.LENGTH_SHORT).show();
            }
        } else {
            Tooriistad.NaitaHoiatust(getActivity(),
                    getString(R.string.jagamise_keeldumise_pealkiri),
                    getString(R.string.jagamise_keeldumise_pohjus));
        }
    }
    public void KustutaSalvestusKlikk(View v){
        Bundle args = new Bundle();
        args.putString("kysimus", getString(R.string.dialog_kas_kustuta_salvestuse_kusimus));
        args.putString("jahvastus", getString(R.string.jah));
        args.putString("eivastus", getString(R.string.ei));
        DialogFragment newFragment = new LihtneKusimus();
        newFragment.setArguments(args);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getChildFragmentManager(), "KustutaSalvestus");
    }

    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "KustutaHarjutus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else
        if(dialog.getTag().equals("KustutaSalvestus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "KustutaSalvestus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        }
        else {
            if(BuildConfig.DEBUG) Log.e("HarjutusMuudaFragment", "kuiEiVastus. Tundmatust kohast tuldud !");
        }
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus();
            harjutusFragmendiKuulaja.KustutaHarjutus(this.harjutusid);
        } else
        if(dialog.getTag().equals("KustutaSalvestus")) {
            if(BuildConfig.DEBUG) Log.d("HarjutusMuudaFragment", "KustutaSalvestus vastus Jah.");
            KustutaSalvestus();
        }
        else {
            if(BuildConfig.DEBUG) Log.e("HarjutusMuudaFragment", "kuiJahVastus. Tundmatust kohast tuldud !");
        }
    }

    private void KustutaSalvestus(){
        harjutuskord.KustutaFailid(getActivity().getApplicationContext());
        harjutuskord.TuhjendaSalvestuseValjad();
        mHeliFail = null;
        SalvestaHarjutus();
        SeadistaSalvestiseRiba();
    }

    private class AvaFailMangimiseks extends AsyncTask<HarjutusKord, Void, DriveContents> {

        @Override
        protected DriveContents doInBackground(HarjutusKord... harjutusKords) {
            DriveContents dFC = null;
            if(!isCancelled()) {
                GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getActivity().getApplicationContext(), null);
                if (!isCancelled() && mGDU.LooDriveUhendusAsunkroonselt()) {
                    DriveId dID;
                    dID = mGDU.AnnaDriveID(harjutusKords[0].getHelifailidriveid());
                    try {
                        if (!isCancelled())
                            dFC = mGDU.AvaDriveFail(dID, DriveFile.MODE_READ_ONLY);
                    } catch (FileNotFoundException e) {
                        if (BuildConfig.DEBUG)
                            Log.e("AvaFailMangimiseks", "Faili ei leitud. Tühjendame väljad. " + e.toString());
                        harjutusKords[0].TuhjendaSalvestuseValjad();
                        harjutusKords[0].Salvesta(getActivity().getApplicationContext());
                    }
                }
                mGDU.KatkestaDriveUhendus();
            }
            return dFC;
        }

        protected void onPostExecute(DriveContents dFC) {
            if(dFC != null){
                mHeliFail = dFC.getParcelFileDescriptor().getFileDescriptor();
            }
            SeadistaSalvestiseRiba();
            super.onPostExecute(dFC);
        }
    }

    public void SeadistaSalvestiseRiba() {
        RelativeLayout mangilugu = (RelativeLayout) getView().findViewById(R.id.SalvestuseRiba);
        if (mHeliFail == null) {
            mangilugu.setVisibility(RelativeLayout.GONE);
        } else {
            mangilugu.setVisibility(RelativeLayout.VISIBLE);
            CheckBox mLinkAruandele = (CheckBox)getView().findViewById(R.id.weblinkaruandele);
            ImageButton mJaga = (ImageButton)getView().findViewById(R.id.jaga);
            if(harjutuskord.getHelifailidriveweblink() == null ||
                    harjutuskord.getHelifailidriveweblink().isEmpty()){
                mLinkAruandele.setVisibility(CheckBox.GONE);
                mJaga.setVisibility(ImageButton.GONE);
            }
        }
    }

    private String MoodustaJagamiseTeema(){
        String retVal;
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        retVal = teos.getNimi() + " " + getString(R.string.jagamise_teema_harjutus) + ", " +
                Tooriistad.KujundaKuupaevSonaline(harjutuskord.getAlgusaeg());
        return retVal;
    }
    private String MoodustaJagamiseTekst(){
        String retVal;
        String ReaVahetus = System.getProperty("line.separator");
        retVal = getString(R.string.tere) + "!" + ReaVahetus + ReaVahetus;
        retVal = retVal + getString(R.string.jagamise_sisu_link_harjutusele) + ": " +
                harjutuskord.getHelifailidriveweblink() + ReaVahetus + ReaVahetus;

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        retVal = retVal + getString(R.string.aruanne_tervitades) + ReaVahetus +
                sharedPref.getString("minueesnimi","") + " " + sharedPref.getString("minuperenimi","") + " " +
                getString(R.string.aruanne_pillipaeviku_vahendusel);
        return retVal;
    }
}
