package com.vaskjala.vesiroosi20.pillipaevik;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.HarjutusteKalenderActivity;
import com.vaskjala.vesiroosi20.pillipaevik.aruanded.Kuuaruanne;
import com.vaskjala.vesiroosi20.pillipaevik.aruanded.ValiAruandeKuu;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.*;

import java.util.*;

public class PeaActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja,
        TeosListFragmendiKuulaja, TeosFragmendiKuulaja, HarjutusFragmendiKuulaja {

    private ActionBarDrawerToggle mDrawerToggle;
    private boolean bEsimeneAvamine = true;
    private boolean bMitmeFragmendiga = false;
    private int teoseid = -1;
    private int harjutuseid = -1;
    private int lisatehtudharjutuseid = -1;
    private int iSahtliValik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreate");
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null ){
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "savedInstanceState != null");
            bEsimeneAvamine = false;
            teoseid = savedInstanceState.getInt("teoseid");
            harjutuseid = savedInstanceState.getInt("harjutuseid");
            lisatehtudharjutuseid = savedInstanceState.getInt("lisatehtudharjutuseid");
        }

        setContentView(R.layout.activity_teos_list);
        if (findViewById(R.id.teos_hoidja) != null && findViewById(R.id.harjutus_hoidja) != null) {
            if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreate. Mitme fragmendiga vaade");
            bMitmeFragmendiga = true;
        }
        this.setTitle(getString(R.string.rakenduse_pealkiri));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayShowHomeEnabled(true);
        mAction.setDisplayHomeAsUpEnabled(true);
        mAction.setHomeButtonEnabled(true);

        SeadistaNaviVaade(toolbar);
    }

    @Override
    protected void onStart() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onStart");
        super.onStart();
        Tooriistad.KorraldaLoad(this);

        if(Tooriistad.KasLubadaSalvestamine(getApplicationContext()) &&
                Tooriistad.kasKasutadaGoogleDrive(getApplicationContext())) {
            if (!Tooriistad.KasGoogleKontoOlemas(getApplicationContext()) || bEsimeneAvamine) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alusta Drive ühenduse loomisega");
                GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), this);
                mGDU.LooDriveUhendus();
                Intent intent = new Intent(this, KorrastaDraivFailidTeenus.class);
                startService(intent);
            }
        }

        NavigationView navivaade = (NavigationView) findViewById(R.id.sahtli_navivaade);
        View header = navivaade.getHeaderView(0);
        TextView mOpilane = (TextView)header.findViewById(R.id.navitiitli_nimevali);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        String nimi = sharedPref.getString("minueesnimi", "");
        if(!nimi.isEmpty()) {
            String perenimi = sharedPref.getString("minuperenimi", "");
            if (!perenimi.isEmpty()) {
                nimi = nimi + " " + perenimi;
            }
        }
        mOpilane.setText(nimi);
        if(bMitmeFragmendiga) {
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            Teos teos = mPPManager.getTeos(teoseid);

            List<Teos> teosList = AnnaTeosListFragment().AnnaTeosList();
            boolean bTeosTeosListis = teos != null && teosList.contains(teos);

            // Pillipäevikut avades teosid = -1 või kui Teosed puuduvad
            if(!bTeosTeosListis) {
                if(!teosList.isEmpty()){
                    teos = teosList.get(0);
                    if(teos != null) {
                        TeosValitud(teos);
                        HarjutusValitud(teos.getId(), harjutuseid);
                    }
                }
            }
            // Kui Harjutus on mõnes teises Activitys kustutatud - Kalender
            if(teos != null && mPPManager.getHarjutus(teoseid, harjutuseid) == null) {
                HarjutusValitud(teoseid, harjutuseid);
            }
        }
    }

    @Override
    protected void onStop() {
        bEsimeneAvamine = false;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("teoseid", this.teoseid);
        outState.putInt("harjutuseid", this.harjutuseid);
        outState.putInt("lisatehtudharjutuseid", this.lisatehtudharjutuseid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState :" + this.teoseid + " " + this.harjutuseid );
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    private void SeadistaNaviVaade(Toolbar toolbar){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar, R.string.ava_sahtel,
                R.string.sule_sahtel
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {

                switch (getiSahtliValik()) {
                    case R.id.harjutuste_kalender :
                            Intent i = new Intent(view.getContext(), HarjutusteKalenderActivity.class);
                            startActivityForResult(i,getResources().getInteger(R.integer.KALENDER_ACTIVITY_START));
                        break;
                    case R.id.saada_aruanne :
                        if(Tooriistad.kasNimedEpostOlemas(getApplicationContext())) {
                            Bundle args = new Bundle();
                            DialogFragment valiAruandeKuu = new ValiAruandeKuu();
                            valiAruandeKuu.setArguments(args);
                            valiAruandeKuu.show(getFragmentManager(), "ValiAruandeKuu");
                        } else {
                            Tooriistad.NaitaHoiatust((Activity) view.getContext(),
                                    getString(R.string.aruande_tegemise_hoiatuse_pealkiri),
                                    getString(R.string.aruande_tegemise_keeldumise_pohjus));

                        }
                        break;
                    case R.id.seaded :
                        if(BuildConfig.DEBUG) Log.d("PeaActivity", "Seaded vajutatud");
                        Intent intentSeaded = new Intent(view.getContext(), SeadedActivity.class);
                        startActivityForResult(intentSeaded,getResources().getInteger(R.integer.SEADED_ACTIVITY_START));
                        break;
                    case R.id.teave :
                        if(BuildConfig.DEBUG) Log.d("PeaActivity", "Seaded vajutatud");
                        Intent intentTeave = new Intent(view.getContext(), TeaveActivity.class);
                        startActivity(intentTeave);
                        break;
                    default:
                        break;
                }
                setiSahtliValik(0);
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navivaade = (NavigationView) findViewById(R.id.sahtli_navivaade);
        navivaade.setItemIconTintList(null);
        navivaade.setNavigationItemSelectedListener(new NaviMenyyKuulaja(navivaade, mDrawerLayout));
    }

    public class NaviMenyyKuulaja implements NavigationView.OnNavigationItemSelectedListener
    {
        private NavigationView navigationView;
        private DrawerLayout drawerLayout;
        NaviMenyyKuulaja(NavigationView navigationView, DrawerLayout drawerLayout){
            this.navigationView = navigationView;
            this.drawerLayout = drawerLayout;
        }
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            setiSahtliValik(item.getItemId());
            drawerLayout.closeDrawer(navigationView);
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if(BuildConfig.DEBUG) Log.d("Peaaken", "Sahtel valitud");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onActivityResult");
        TeosListFragment teosListFragment = AnnaTeosListFragment();
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                int teoseid = data.getIntExtra("item_id",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                teosListFragment.UuendaTeosList();
                teosListFragment.KeriLisTeoseid(teoseid);
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Kustutatud. Pos:" + itemposition);
                teosListFragment.EemaldaPositsioonListist(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                int teoseid = data.getIntExtra("item_id",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisatud:" + teoseid);
                teosListFragment.UuendaTeosList();
                teosListFragment.KeriLisTeoseid(teoseid);
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamisel kustutati");
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamist ei viidud lõpule");
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS)){
            int uueharjutuseid = data.getIntExtra("harjutus_id",0);
            HarjutusMuudetud(teoseid, uueharjutuseid, 0);
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            if(bMitmeFragmendiga && mPPManager.getHarjutus(teoseid, uueharjutuseid) != null) {
                HarjutusValitud(teoseid, uueharjutuseid);
            }
            if(data != null) {
                int kustutamisealge = data.getIntExtra("kustutamisealge", 0);
                if (kustutamisealge == Tooriistad.TUHIHARJUTUS_KUSTUTA)
                    Tooriistad.KuvaAutomaatseKustutamiseTeade(this);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.KALENDER_ACTIVITY_START)) {
            if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusteKalenderActivity");
            teosListFragment.UuendaTeosList();
        }
        if (requestCode == getResources().getInteger(R.integer.SEADED_ACTIVITY_START)) {
            if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi SeadedActivity");
            teosListFragment.UuendaTeosList();
        }
        if( requestCode == Tooriistad.PEAMINE_KONTO_VALIMINE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) != null) {
                    Tooriistad.SalvestGoogleKonto(getApplicationContext(), data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                    Tooriistad.SeadistaGoogleDriveOlekSeadeteFailis(getApplicationContext(), true);
                    GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), this);
                    mGDU.LooDriveUhendus();
                } else {
                    Tooriistad.SalvestGoogleKonto(getApplicationContext(), null);
                    Tooriistad.SeadistaGoogleDriveOlekSeadeteFailis(getApplicationContext(), false);
                    Tooriistad.NaitaHoiatust(this, getString(R.string.konto_valimise_vea_pealkiri),
                            getString(R.string.konto_valimise_vea_tekst));
                    if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Drive configureerimine ei õnnestunud, resultCode != RESULT_OK: " + resultCode);
                }
            } else {
                Tooriistad.SalvestGoogleKonto(getApplicationContext(), null);
                Tooriistad.SeadistaGoogleDriveOlekSeadeteFailis(getApplicationContext(), false);
                Tooriistad.NaitaHoiatust(this, getString(R.string.konto_valimise_vea_pealkiri),
                        getString(R.string.konto_valimise_vea_tekst));
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Drive configureerimine katkestati: " + resultCode);
            }

        }
        if( requestCode == Tooriistad.GOOGLE_DRIVE_KONTO_VALIMINE) {
            if (resultCode == RESULT_OK) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Drive configureerimine õnnestus: " + resultCode);
                GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), this);
                mGDU.LooDriveUhendus();
            } else {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Drive configureerimine katkestati: " + resultCode);
            }

        }
        if( requestCode == Tooriistad.GOOGLE_DRIVE_REST_KONTO_VALIMINE) {
            if (resultCode == RESULT_OK && data != null &&
                    data.getExtras() != null) {
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    Tooriistad.SalvestGoogleKonto(getApplicationContext(), accountName);
                    if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Valitud konto: " + accountName +
                            ". Loome ühenduse uuesti, nüüd saab getSharedPreferences-dest");
                    GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), this);
                    mGDU.SeadistaDriveRestUhendus(false);
                } else {
                    if(BuildConfig.DEBUG) Log.e(getLocalClassName(), "GOOGLE_DRIVE_REST_KONTO_VALIMINE Valitud konto accountName != null ");
                }
            } else {
                if(BuildConfig.DEBUG) Log.e(getLocalClassName(), "GOOGLE_DRIVE_REST_KONTO_VALIMINE resultCode ei ole OK või data == null");
            }
        }
        if( requestCode == Tooriistad.GOOGLE_DRIVE_REST_UHENDUSE_LUBA){
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onActivityResult. REST API loa küsimine" + resultCode);
        }
    }

    @Override
    public void UusTeos() {
        if(bMitmeFragmendiga){
            SuleHarjutusFragment();
            LooTeosFragment(new TeosFragment(), null);
            VarskendaTeosList();
        }
        else {
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
        }
    }
    @Override
    public void TeosValitud(Teos teos) {

        if(bMitmeFragmendiga) {
            if (teoseid != teos.getId()) {
                SuleHarjutusFragment();
                LooTeosFragment(new TeosFragment(), teos);
                ValiEsimeneHarjutusKord(teos.getId());
            } else {
                TeosFragment teosFragment = (TeosFragment)(getFragmentManager().findFragmentById(R.id.teos_hoidja));
                if(teosFragment == null || teosFragment.TeoseNimiMuutunud()) {
                    LooTeosFragment(new TeosFragment(), teos);
                    // Teoslisti värksendus Arhiivimenüü kaudu
                    if(teosFragment == null)
                        ValiEsimeneHarjutusKord(teos.getId());
                }
            }
        }
        else {
            int itemposition = AnnaTeosListFragment().AnnaTeosList().indexOf(teos);
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", teos.getId());
            intent.putExtra("item_position", itemposition);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
            if (BuildConfig.DEBUG) Log.d("PeaActivity", "Teos valitud : " + teos.getId() + " Holder position: " +
                    itemposition + " Intent: " +
                    getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
        }
    }

    @Override
    public void MuudaTeos(Teos teos) {
        int vanaasukoht = AnnaTeosListFragment().AnnaTeosList().indexOf(teos);
        VarskendaTeosList();
        VarskendaListJaFragment(teos, vanaasukoht);
    }

    @Override
    public void KustutaTeos(Teos teos, int itemposition) {
        int pos = AnnaTeosListFragment().EemaldaTeosListist(teos);
        VarskendaProgressid();
        ValiTeineTeos(pos);
    }

    @Override
    public void AlustaHarjutust(int teosid) {
        if(bMitmeFragmendiga) {
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            Teos teos = mPPManager.getTeos(teosid);
            TeosValitud(teos);
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Teos valitud");
        } else {
            // Kui pika vajutuse ja ühe-fragmendi vaates minnakse nö otse uut harjutust tegema
            // siis võib teoseid instance muutuja valeks jääda, sest ühe-fragmendi juhtumil seda
            // praktiliselt ei kasutata
            teoseid = teosid;
        }

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alusta uut harjutust");
        Intent intent = new Intent(this, HarjutusUusActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", -1);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
    }

    @Override
    public void LisaTehtudHarjutus(int teosid) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lisa tehtud harjutus");
        SuleHarjutusFragment();
        LooHarjutusFragment(new HarjutusLisaTehtudFragment(), teosid, -1);
        this.lisatehtudharjutuseid = harjutuseid;
    }

    @Override
    public void HarjutusLisatud(int teosid, int harjutusid) {
        VarskendaTeoseVaated(teosid);
    }

    @Override
    public void HarjutusValitud(int teosid, int harjutusid) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Avan olemasolevat harjutust. Teosid : " + teosid +
                " Harjutus:" + harjutusid);

        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        boolean bHarjutusOlemas = mPPManager.getHarjutus(teosid, harjutusid) != null;

        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if (harjutusfragment != null && bHarjutusOlemas &&
                ((HarjutusFragment)harjutusfragment).getHarjutuskord().getId() != harjutusid)  {
            SuleHarjutusFragment();
        }

        if(bHarjutusOlemas) {
            if(lisatehtudharjutuseid == harjutusid) {
                LooHarjutusFragment(new HarjutusLisaTehtudFragment(), teosid, harjutusid);
            } else {
                LooHarjutusFragment(new HarjutusMuudaFragment(), teosid, harjutusid);
                lisatehtudharjutuseid = -1;
            }
        } else {
            ValiEsimeneHarjutusKord(teosid);
        }
    }

    @Override
    public void HarjutusKustutatud(int teosid, int harjutusid, int itemposition, int kustutamisealge) {
        VarskendaTeoseVaated(teosid);
        EemaldaHarjutusFragment();
        ValiEsimeneHarjutusKord(teosid);
        if(kustutamisealge == Tooriistad.TUHIHARJUTUS_KUSTUTA)
            Tooriistad.KuvaAutomaatseKustutamiseTeade(this);
    }

    @Override
    public void HarjutusMuudetud(int teosid, int harjutusid, int position) {
        VarskendaTeoseVaated(teosid);
    }

    private void VarskendaTeosList() {
        AnnaTeosListFragment().UuendaTeosList();
    }

    private void VarskendaListJaFragment(Teos teos, int vanaasukoht) {
        List<Teos> teosList = AnnaTeosListFragment().AnnaTeosList();
        if(!teosList.contains(teos))
            ValiTeineTeos(vanaasukoht);
        else
            AnnaTeosListFragment().KeriLisTeosele(teos);
    }

    private void VarskendaTeosListiElement(Teos teos) {
        AnnaTeosListFragment().UuendaTeosListis(teos);
    }

    @Override
    public void SeaTeosid(int teosid) {
        this.teoseid = teosid;
    }

    @Override
    public void SeaHarjutusid(int harjutuseid) {
        this.harjutuseid = harjutuseid;
    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("ValiAruandeKuu")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Aruande kuu valimisel OK vajutatud");
            String kuujaaastastr= dialog.getArguments().getString("kuujaaasta");
            Date kuujaaasta = Tooriistad.KuupaevKuuJaAastaSonalineStringist(kuujaaastastr);
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), kuujaaastastr);

            Kuuaruanne ka = new Kuuaruanne(getApplicationContext());
            ka.setAruandeperioodinimi(kuujaaastastr);
            ka.setPerioodialgus(Tooriistad.MoodustaKuuAlgusKuupaev(kuujaaasta));
            ka.setPerioodilopp(Tooriistad.MoodustaKuuLopuKuupaev(kuujaaasta));
            String aruandekogutekst = ka.AruandeKoguTekst();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{ka.getOpetajaepost()});
            i.putExtra(Intent.EXTRA_SUBJECT, ka.Teema());
            i.putExtra(Intent.EXTRA_TEXT, aruandekogutekst);
            try {
                startActivity(Intent.createChooser(i, getString(R.string.aruanne_saada)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getParent(), getString(R.string.aruanne_eposti_rakendus_puudub), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("ValiAruandeKuu")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Aruande kuu valimisel Loobu vajutatud");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Tooriistad.SeadistaSalvestamiseOlek(getApplicationContext());
        Tooriistad.SeadistaGoogleDriveOlek(getApplicationContext());
    }

    private int getiSahtliValik() {
        return iSahtliValik;
    }
    private void setiSahtliValik(int iSahtliValik) {
        this.iSahtliValik = iSahtliValik;
    }

    private void LooTeosFragment(Fragment fragment, Teos teos){

        teoseid = teos == null ? -1 : teos.getId();

        Bundle args = new Bundle();
        args.putInt("item_id", teoseid);
        args.putInt("item_position", 0);
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.teos_hoidja, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        getFragmentManager().executePendingTransactions();

        List<Teos> teosList = AnnaTeosListFragment().AnnaTeosList();
        int itemposition = teosList.contains(teos) ? teosList.indexOf(teos) : 0;
        AnnaTeosListFragment().KeriTeosListKohale(itemposition);
    }
    private void SuleTeosFragment(){
        Fragment teosfragment = getFragmentManager().findFragmentById(R.id.teos_hoidja);
        if (teosfragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(teosfragment);
            ft.commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    private void LooHarjutusFragment(Fragment fragment, int teoseid, int harjutuseid){
        this.harjutuseid = harjutuseid;
        Bundle argsharjutus = new Bundle();
        argsharjutus.putInt("teos_id", teoseid);
        argsharjutus.putInt("harjutus_id", harjutuseid);
        fragment.setArguments(argsharjutus);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.harjutus_hoidja, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        getFragmentManager().executePendingTransactions();
    }
    private void SuleHarjutusFragment(){
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if (harjutusfragment != null) {
            ((HarjutusFragmendiKutsuja) harjutusfragment).SuleHarjutus();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(harjutusfragment);
            ft.commit();
            getFragmentManager().executePendingTransactions();
        }
    }
    private void EemaldaHarjutusFragment(){
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if(harjutusfragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(harjutusfragment);
            ft.commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    public void ValiTeineTeos(int vanaasukoht){
        if(bMitmeFragmendiga) {
            EemaldaHarjutusFragment();
            SuleTeosFragment();
            int uusasukoht = vanaasukoht == 0 ? vanaasukoht : vanaasukoht - 1;
            List<Teos> teosed = AnnaTeosListFragment().AnnaTeosList();
            if (!teosed.isEmpty()) {
                Teos teos = teosed.get(uusasukoht);
                if (teos != null) {
                    TeosValitud(teos);
                }
            }
        }
    }
    private void ValiEsimeneHarjutusKord(int teosid){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(teosid);
        List<HarjutusKord> harjutusKorrad = teos.getHarjustuskorrad(getApplicationContext());
        if(!harjutusKorrad.isEmpty()) {
            HarjutusKord harjutusKord = harjutusKorrad.get(0);
            LooHarjutusFragment(new HarjutusMuudaFragment(), teos.getId(), harjutusKord.getId());
        }
        lisatehtudharjutuseid = -1;
    }
    private void VarskendaTeoseVaated(int teosid){
        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teos_hoidja);
        if(teosFragment != null) {
            teosFragment.VarskendaHarjutusteJaStatistika();
        }

        VarskendaProgressid();

        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        VarskendaTeosListiElement(mPPManager.getTeos(teosid));

    }

    private void VarskendaProgressid(){
        TeosListFragment teosListFragment = (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
        if(teosListFragment != null){
            teosListFragment.VarskendaProgressid();
        }
    }

    private TeosListFragment AnnaTeosListFragment(){
        return (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
    }
}
