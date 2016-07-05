package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class TeosActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja {


    private PilliPaevikDatabase mPPManager;
    private HarjutuskorradAdapter pHarjutusedAdapter = null;
    private int teosid;
    private Teos teos;
    private int itemposition;
    private boolean bUueTeoseLoomine = false;

    // Vaate lahtrid
    private RatingBar mHinnang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.teos_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Instance ei ole salvestatud, loen Intent obektist");
            this.teosid = getIntent().getIntExtra("item_id", 0);
            this.itemposition = getIntent().getIntExtra("item_position", 0);
        } else {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Loen saveinstantsist");
            this.teosid = savedInstanceState.getInt("teoseid");
            this.itemposition = savedInstanceState.getInt("itemposition");
            this.bUueTeoseLoomine = savedInstanceState.getBoolean("uusteos");
        }
        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        this.teos = mPPManager.getTeos(teosid);
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Loen teost:" + String.valueOf(this.teosid) + " " + this.teos + " Pos:" + this.itemposition);

        FookusePassija mFP = new FookusePassija(this.teos);
        EditText mNimi = ((EditText) findViewById(R.id.nimi));
        EditText mAutor = ((EditText) findViewById(R.id.autor));
        EditText mKommentaar = ((EditText) findViewById(R.id.kommentaar));
        mHinnang = ((RatingBar) findViewById(R.id.hinnaguriba));
        RadioGroup mKasutusViis = ((RadioGroup) findViewById(R.id.kasutusviis));
        mNimi.setOnFocusChangeListener(mFP);
        mAutor.setOnFocusChangeListener(mFP);
        mKommentaar.setOnFocusChangeListener(mFP);

        // bUueTeoseloomine elab Destroy üle
        if ((this.teos == null && !KasTeosSalvestatud())) {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Uus teos");
            bUueTeoseLoomine = true;
        } else {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Taastan teost");

            mNimi.setText(this.teos.getNimi());
            this.setTitle(this.teos.getNimi());
            mAutor.setText(this.teos.getAutor());
            mKommentaar.setText(this.teos.getKommentaar());
            mHinnang.setRating(this.teos.getHinnang());
            if (this.teos.getKasutusviis() == 1)
                mKasutusViis.check(R.id.Kasutusel);
            else if (this.teos.getKasutusviis() == 2)
                mKasutusViis.check(R.id.Arhiivis);

            LooHarjutusteAdapter();
        }
        if(this.bUueTeoseLoomine)
            findViewById(R.id.kasutusviis).setVisibility(View.GONE);

        HarjutusteStatistika ();
    }

    private void LooHarjutusteAdapter(){
        List<HarjutusKord> harjutuskorrad = this.teos.getHarjustuskorrad(getApplicationContext());
        pHarjutusedAdapter=
                new HarjutuskorradAdapter(getApplicationContext(),
                        (ArrayList<HarjutusKord>) harjutuskorrad);
        ListView HarjutusteList = (ListView) findViewById(R.id.harjutuslist);
        HarjutusteList.setOnItemClickListener(new HarjutusedListiPassija());
        HarjutusteList.setAdapter(pHarjutusedAdapter);

    }
    private void AndmedTeosesse(Teos teos) {

        String nimi = ((EditText)findViewById(R.id.nimi)).getText().toString();
        String autor = ((EditText) findViewById(R.id.autor)).getText().toString();
        String kommentaar = ((EditText) findViewById(R.id.kommentaar)).getText().toString();
        short hinnang = (short) mHinnang.getRating();
        short kasutusviis = 1;
        if( ((RadioGroup)findViewById(R.id.kasutusviis)).getCheckedRadioButtonId()== R.id.Kasutusel )
            kasutusviis = 1;
        if( ((RadioGroup)findViewById(R.id.kasutusviis)).getCheckedRadioButtonId()== R.id.Arhiivis)
            kasutusviis = 2;
        if(teos.getLisatudpaevikusse() == null )
            teos.setLisatudpaevikusse(Calendar.getInstance().getTime());

        teos.setNimi(nimi);
        teos.setAutor(autor);
        teos.setKommentaar(kommentaar);
        teos.setHinnang(hinnang);
        teos.setKasutusviis(kasutusviis);

        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Andmed teosesse: " + teos.toString());

    }
    public class FookusePassija implements View.OnFocusChangeListener {
        Teos teos;
        FookusePassija(Teos teos){
            this.teos = teos;
        }

        public void onFocusChange(View v, boolean hasFocus) {

            int lahter = v.getId();
            EditText TeosNimiLahter = ((EditText)findViewById(R.id.nimi));
            boolean bNimiOlemas = KasNimiOlemas();
            boolean bNimiUnikaalne = KasNimiUnikaalne();

            if(!hasFocus){
                if(this.teos != null){
                    if(lahter != TeosNimiLahter.getId()){
                        AndmedTeosesse(teos);
                        mPPManager.SalvestaTeos(teos);
                    } else {
                        if(bNimiOlemas && bNimiUnikaalne) {
                            AndmedTeosesse(teos);
                            mPPManager.SalvestaTeos(teos);
                        } else {
                            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Fookus nimelt ära ning nimi tühi või korduv. Nimi olemas: " +
                            bNimiOlemas + " Unikaalne:" + bNimiUnikaalne);
                        }
                    }
                } else { // Uus teos
                    if(lahter != TeosNimiLahter.getId()){
                        if(BuildConfig.DEBUG) Log.e("TeosActivity", "UUS TEOS JA VÄLJUSIME MUUST LAHTRIST KUI NIMI !!!");
                    } else {
                        if(bNimiOlemas && bNimiUnikaalne) {
                            teos = new Teos();
                            AndmedTeosesse(teos);
                            mPPManager.SalvestaTeos(teos);
                            TeosActivity.this.teos = teos;
                            TeosActivity.this.teosid = teos.getId();
                            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Uus teos lisatud:  " + teosid);
                        } else {
                            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Uus teos. Nimi sobimatu. Nimi olemas: " +
                                    bNimiOlemas + " Unikaalne:" + bNimiUnikaalne);
                        }
                    }

                }
            }
            if (hasFocus && lahter != TeosNimiLahter.getId()  && (!bNimiOlemas || !bNimiUnikaalne) ) {
                if(teos != null)
                    TeosNimiLahter.setText(teos.getNimi());
                if(BuildConfig.DEBUG) Log.e("TeosActivity", "VIIME FOOKUSE TAGASI NIMELE!!!");
                v.clearFocus();
                TeosNimiLahter.requestFocus();
                if(!bNimiOlemas)
                    Snackbar.make(v, getString(R.string.snackbar_anna_teosele_nimi), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(bNimiOlemas && !bNimiUnikaalne)
                    Snackbar.make(v, R.string.snackbar_teos_juba_olemas, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        }
    }
    private class HarjutusedListiPassija implements  ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), HarjutusMuudaActivity.class);
            HarjutusKord mHarjutuskord = (HarjutusKord)parent.getItemAtPosition(position);
            intent.putExtra("teos_id", teosid);
            intent.putExtra("harjutus_id", mHarjutuskord.getId());
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Avan olemasolevat harjutust. Teosid : " + teosid +
                    " Harjutus:" + mHarjutuskord.getId());
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_MUUDA));
        }

    }
    public class HarjutuskorradAdapter extends ArrayAdapter<HarjutusKord> {
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
            if(harjutuskord.getHelifailidriveid() == null || harjutuskord.getHelifailidriveid().isEmpty())
                convertView.findViewById(R.id.harjutuslisti_pilt).setVisibility(View.GONE);
            else
                convertView.findViewById(R.id.harjutuslisti_pilt).setVisibility(View.VISIBLE);

            return convertView;
        }
    }
    private class HarjutusComparator implements Comparator<HarjutusKord>{
        @Override
        public int compare(HarjutusKord lhs, HarjutusKord rhs) {
            return rhs.getAlgusaeg().compareTo(lhs.getAlgusaeg());
        }
    }

    private void HarjutusteStatistika (){
        int[] stat = mPPManager.TeoseHarjutusKordadeStatistika(this.teosid);
        ((TextView) findViewById(R.id.teoseharjutustearv)).setText(String.valueOf(stat[1]));
        ((TextView) findViewById(R.id.teoseharjutustekestus))
                .setText(Tooriistad.KujundaHarjutusteMinutid(getApplicationContext(), stat[0]/60));
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("teoseid", this.teosid);
        savedInstanceState.putInt("itemposition", this.itemposition);
        savedInstanceState.putBoolean("uusteos", this.bUueTeoseLoomine);

        if(BuildConfig.DEBUG) Log.d("TeosActivity", "onSaveInstanceState :" + this.teosid + " " + this.itemposition );

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teosmenyy, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if(BuildConfig.DEBUG) Log.d("TeosActivity","Menüü valik vajutatud" + item.getItemId());

        if(item.getItemId() != R.id.kustutateos) {
            if (!KasNimiOlemas() || !KasNimiUnikaalne()) {
                if (KasTeosSalvestatud()) {
                    EditText TeosNimiLahter = ((EditText) findViewById(R.id.nimi));
                    TeosNimiLahter.setText(teos.getNimi());
                } else {
                    if (!KasNimiOlemas()) {
                        Snackbar.make(findViewById(R.id.nimi), getString(R.string.snackbar_nimi_tuhi), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        if (!KasNimiUnikaalne()) {
                            Snackbar.make(findViewById(R.id.nimi), getString(R.string.snackbar_teos_juba_olemas), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
                    return super.onOptionsItemSelected(item);
                }
            } else {
                if (!KasTeosSalvestatud()) {
                    if(BuildConfig.DEBUG) Log.d("TeosActivity", "Menüü valik vajutatud. Nimi olemas ja unikaalne, kuid Teos salvetamata. Loome uue ja salvestame." + item.getItemId());
                    this.teos = new Teos();
                    AndmedTeosesse(this.teos);
                    mPPManager.SalvestaTeos(this.teos);
                    this.teosid = teos.getId();
                }
            }
        }

        if(item.getItemId() == R.id.kustutateos){
            Bundle args = new Bundle();
            args.putString("kysimus",getString(R.string.dialog_kas_kustuta_teose_kusimus));
            args.putString("jahvastus",getString(R.string.jah));
            args.putString("eivastus",getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "Kustuta teos");
        }
        if(item.getItemId() == R.id.alustauut){
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Alusta uut harjutust");
            Intent intent = new Intent(this, HarjutusUusActivity.class);
            intent.putExtra("teos_id", this.teosid);
            intent.putExtra("harjutusid", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
        }
        if(item.getItemId() == R.id.lisatehtud){
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Lisa tehtud harjutus");
            Intent intent = new Intent(this, HarjutusLisaTehtudActivity.class);
            intent.putExtra("teos_id", this.teosid);
            intent.putExtra("harjutus_id", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_TEHTUD ));
        }
        if(item.getItemId() == android.R.id.home){
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Up nuppu vajutatud");
            Intent intent = NavUtils.getParentActivityIntent(this);
            int result = 0;
            if(bUueTeoseLoomine) {
                if (KasTeosSalvestatud()) {
                    AndmedTeosesse(this.teos);
                    mPPManager.SalvestaTeos(this.teos);
                    result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD);
                    if(BuildConfig.DEBUG) Log.d("TeosActivity", "Result tagasi UUS LISATUD");
                } else {
                    result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA);
                    if(BuildConfig.DEBUG) Log.d("TeosActivity", "Result tagasi UUS LOOMATA");
                }
            }
            else {
                AndmedTeosesse(this.teos);
                mPPManager.SalvestaTeos(this.teos);
                result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD);
                intent.putExtra("item_position", itemposition);
                if(BuildConfig.DEBUG) Log.d("TeosActivity", "Result tagasi MUUDETUD");
            }
            intent.putExtra("item_id", this.teosid);
            setResult(result, intent);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(pHarjutusedAdapter != null)
            pHarjutusedAdapter.notifyDataSetChanged();
        else
            LooHarjutusteAdapter();

        if(pHarjutusedAdapter != null)
            pHarjutusedAdapter.sort(new HarjutusComparator());

        HarjutusteStatistika ();

    }

    public void MuudaHinnangut(View v){
        teos.setHinnang((short)mHinnang.getRating());
    }

    // Reeglid
    private boolean KasTeosSalvestatud() {
        return this.teosid != -1;
    }
    private boolean KasNimiOlemas(){
        boolean retVal;
        EditText TeosNimiLahter = ((EditText)findViewById(R.id.nimi));
        String TeosNimiString = TeosNimiLahter.getText().toString();
        retVal = !TeosNimiString.isEmpty();
        return  retVal;
    }
    private boolean KasNimiUnikaalne(){
        boolean retVal = false;
        boolean bNimiOlemas = KasNimiOlemas();
        if(bNimiOlemas) {
            EditText TeosNimiLahter = ((EditText) findViewById(R.id.nimi));
            String TeosNimiString = TeosNimiLahter.getText().toString();
            retVal = mPPManager.NimiUnikaalne(teos, TeosNimiString);
        }
        return retVal;
    }

    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        mPPManager.KustutaTeos(this.teosid);
        Intent output = new Intent();
        output.putExtra("item_position", this.itemposition);
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Tagasi kustutamisega. Pos:" + this.itemposition);
        setResult(getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD), output);
        finish();
    }
}
