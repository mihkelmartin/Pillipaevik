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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teos);

        Spinner spinner = (Spinner) findViewById(R.id.hinnang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hinnangud, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.teos_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Log.d("TeosActivity", "Instance ei ole salvestatud, loen Intent obektist");
            this.teosid = getIntent().getIntExtra("item_id", 0);
            this.itemposition = getIntent().getIntExtra("item_position", 0);
        } else {
            Log.d("TeosActivity", "Loen saveinstantsist");
            this.teosid = savedInstanceState.getInt("teoseid");
            this.itemposition = savedInstanceState.getInt("itemposition");
            this.bUueTeoseLoomine = savedInstanceState.getBoolean("uusteos");
        }
        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        this.teos = mPPManager.getTeos(teosid);
        Log.d("TeosActivity", "Loen teost:" + String.valueOf(this.teosid) + " " + this.teos + " Pos:" + this.itemposition);

        FookusePassija mFP = new FookusePassija(this.teos);
        EditText mNimi = ((EditText) findViewById(R.id.nimi));
        EditText mAutor = ((EditText) findViewById(R.id.autor));
        EditText mKommentaar = ((EditText) findViewById(R.id.kommentaar));
        RadioGroup mKasutusViis = ((RadioGroup) findViewById(R.id.kasutusviis));
        mNimi.setOnFocusChangeListener(mFP);
        mAutor.setOnFocusChangeListener(mFP);
        mKommentaar.setOnFocusChangeListener(mFP);

        // bUueTeoseloomine elab Destroy üle
        if ((this.teos == null && !KasTeosSalvestatud())) {
            Log.d("TeosActivity", "Uus teos");
            bUueTeoseLoomine = true;
        } else {
            Log.d("TeosActivity", "Taastan teost");

            mNimi.setText(this.teos.getNimi());
            this.setTitle(this.teos.getNimi());
            mAutor.setText(this.teos.getAutor());
            mKommentaar.setText(this.teos.getKommentaar());
            spinner.setSelection(this.teos.getHinnang());
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

    public void LooHarjutusteAdapter(){
        List<HarjutusKord> harjutuskorrad = this.teos.getHarjustuskorrad(getApplicationContext());
        pHarjutusedAdapter=
                new HarjutuskorradAdapter(getApplicationContext(),
                        (ArrayList<HarjutusKord>) harjutuskorrad);
        ListView HarjutusteList = (ListView) findViewById(R.id.harjutuslist);
        HarjutusteList.setOnItemClickListener(new HarjutusedListiPassija());
        HarjutusteList.setAdapter(pHarjutusedAdapter);

    }
    public void AndmedTeosesse (Teos teos) {

        String nimi = ((EditText)findViewById(R.id.nimi)).getText().toString();
        String autor = ((EditText) findViewById(R.id.autor)).getText().toString();
        String kommentaar = ((EditText) findViewById(R.id.kommentaar)).getText().toString();
        short hinnang = (short)((Spinner) findViewById(R.id.hinnang)).getSelectedItemId();
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

        Log.d("TeosActivity", "Andmed teosesse: " + teos.toString());

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
                            Log.d("TeosActivity", "Fookus nimelt ära ning nimi tühi või korduv. Nimi olemas: " +
                            bNimiOlemas + " Unikaalne:" + bNimiUnikaalne);
                        }
                    }
                } else { // Uus teos
                    if(lahter != TeosNimiLahter.getId()){
                        Log.e("TeosActivity", "UUS TEOS JA VÄLJUSIME MUUST LAHTRIST KUI NIMI !!!");
                    } else {
                        if(bNimiOlemas && bNimiUnikaalne) {
                            teos = new Teos();
                            AndmedTeosesse(teos);
                            mPPManager.SalvestaTeos(teos);
                            TeosActivity.this.teos = teos;
                            TeosActivity.this.teosid = teos.getId();
                            Log.d("TeosActivity", "Uus teos lisatud:  " + teosid);
                        } else {
                            Log.d("TeosActivity", "Uus teos. Nimi sobimatu. Nimi olemas: " +
                                    bNimiOlemas + " Unikaalne:" + bNimiUnikaalne);
                        }
                    }

                }
            }
            if (hasFocus && lahter != TeosNimiLahter.getId()  && (!bNimiOlemas || !bNimiUnikaalne) ) {
                if(teos != null)
                    TeosNimiLahter.setText(teos.getNimi());
                Log.e("TeosActivity", "VIIME FOOKUSE TAGASI NIMELE!!!");
                v.clearFocus();
                TeosNimiLahter.requestFocus();
                if(!bNimiOlemas)
                    Snackbar.make(v, "Anna teosele nimi", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(bNimiOlemas && !bNimiUnikaalne)
                    Snackbar.make(v, "Sellise nimega teos juba on", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        }
    }
    public class HarjutusedListiPassija implements  ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), HarjutusMuudaActivity.class);
            HarjutusKord mHarjutuskord = (HarjutusKord)parent.getItemAtPosition(position);
            intent.putExtra("teos_id", teosid);
            intent.putExtra("harjutus_id", mHarjutuskord.getId());
            Log.d("TeosActivity", "Avan olemasolevat harjutust. Teosid : " + teosid +
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
            TextView kirjeldus = (TextView) convertView.findViewById(R.id.harjutusekirjeldus);
            TextView lisamiseaeg = (TextView) convertView.findViewById(R.id.harjutuslist_kestus);

            kirjeldus.setText(harjutuskord.getHarjutusekirjeldus());
            String pikkus = Tooriistad.formatElapsedTime(harjutuskord.getPikkussekundites()*1000);
            lisamiseaeg.setText(pikkus);
            return convertView;
        }
    }
    public class HarjutusComparator implements Comparator<HarjutusKord>{
        @Override
        public int compare(HarjutusKord lhs, HarjutusKord rhs) {
            return rhs.getAlgusaeg().compareTo(lhs.getAlgusaeg());
        }
    }

    private void HarjutusteStatistika (){
        int[] stat = mPPManager.TeoseHarjutusKordadeStatistika(this.teosid);
        ((TextView) findViewById(R.id.teoseharjutustearv)).setText(String.valueOf(stat[1]));
        ((TextView) findViewById(R.id.teoseharjutustekestus)).setText(Tooriistad.KujundaHarjutusteMinutid(stat[0]/60));
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("teoseid", this.teosid);
        savedInstanceState.putInt("itemposition", this.itemposition);
        savedInstanceState.putBoolean("uusteos", this.bUueTeoseLoomine);

        Log.d("TeosActivity", "onSaveInstanceState :" + this.teosid + " " + this.itemposition );

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teosmenyy, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("TeosActivity","Menüü valik vajutatud" + item.getItemId());

        if(item.getItemId() != R.id.kustutateos) {
            if (!KasNimiOlemas() || !KasNimiUnikaalne()) {
                if (KasTeosSalvestatud()) {
                    EditText TeosNimiLahter = ((EditText) findViewById(R.id.nimi));
                    TeosNimiLahter.setText(teos.getNimi());
                } else {
                    if (!KasNimiOlemas()) {
                        Snackbar.make(findViewById(R.id.nimi), "Nimi tühi", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        if (!KasNimiUnikaalne()) {
                            Snackbar.make(findViewById(R.id.nimi), "Selline nimi juba olemas", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
                    return super.onOptionsItemSelected(item);
                }
            } else {
                if (!KasTeosSalvestatud()) {
                    Log.d("TeosActivity", "Menüü valik vajutatud. Nimi olemas ja unikaalne, kuid Teos salvetamata. Loome uue ja salvestame." + item.getItemId());
                    this.teos = new Teos();
                    AndmedTeosesse(this.teos);
                    mPPManager.SalvestaTeos(this.teos);
                    this.teosid = teos.getId();
                }
            }
        }

        if(item.getItemId() == R.id.kustutateos){
            Bundle args = new Bundle();
            args.putString("kysimus","Kustutad teose ja kõik selle teose harjutused ?");
            args.putString("jahvastus","Jah");
            args.putString("eivastus","Ei");
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "Kustuta teos");
        }
        if(item.getItemId() == R.id.alustauut){
            Log.d("TeosActivity", "Alusta uut harjutust");
            Intent intent = new Intent(this, HarjutusUusActivity.class);
            intent.putExtra("teos_id", this.teosid);
            intent.putExtra("harjutusid", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
        }
        if(item.getItemId() == R.id.lisatehtud){
            Log.d("TeosActivity", "Lisa tehtud harjutus");
            Intent intent = new Intent(this, HarjutusLisaTehtudActivity.class);
            intent.putExtra("teos_id", this.teosid);
            intent.putExtra("harjutus_id", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_TEHTUD ));
        }
        if(item.getItemId() == android.R.id.home){
            Log.d("TeosActivity", "Up nuppu vajutatud");
            Intent intent = NavUtils.getParentActivityIntent(this);
            int result = 0;
            if(bUueTeoseLoomine) {
                if (KasTeosSalvestatud()) {
                    AndmedTeosesse(this.teos);
                    mPPManager.SalvestaTeos(this.teos);
                    result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD);
                    Log.d("TeosActivity", "Result tagasi UUS LISATUD");
                } else {
                    result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA);
                    Log.d("TeosActivity", "Result tagasi UUS LOOMATA");
                }
            }
            else {
                AndmedTeosesse(this.teos);
                mPPManager.SalvestaTeos(this.teos);
                result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD);
                intent.putExtra("item_position", itemposition);
                Log.d("TeosActivity", "Result tagasi MUUDETUD");
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

    // Reeglid
    boolean KasTeosSalvestatud() {
        return this.teosid != -1;
    }
    boolean KasNimiOlemas(){
        boolean retVal;
        EditText TeosNimiLahter = ((EditText)findViewById(R.id.nimi));
        String TeosNimiString = TeosNimiLahter.getText().toString();
        retVal = !TeosNimiString.isEmpty();
        return  retVal;
    }
    boolean KasNimiUnikaalne(){
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
        Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        mPPManager.KustutaTeos(this.teosid);
        Intent output = new Intent();
        output.putExtra("item_position", this.itemposition);
        Log.d("TeosActivity", "Tagasi kustutamisega. Pos:" + this.itemposition);
        setResult(getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD), output);
        finish();
    }
}
