package com.vaskjala.vesiroosi20.pillipaevik;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
    private int iSahtliValik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreate");

        super.onCreate(savedInstanceState);
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

        if(savedInstanceState != null ){
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "savedInstanceState != null");
            bEsimeneAvamine = false;
            this.teoseid = savedInstanceState.getInt("teoseid");
            this.harjutuseid = savedInstanceState.getInt("harjutuseid");
        }
    }

    @Override
    protected void onStart() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onStart");
        super.onStart();
        Tooriistad.KorraldaLoad(this);

        if(Tooriistad.KasLubadaSalvestamine(getApplicationContext()) &&
                Tooriistad.kasKasutadaGoogleDrive(getApplicationContext())) {
            if (bEsimeneAvamine) {
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
        if(bMitmeFragmendiga && teoseid == -1) {
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            Teos teos = mPPManager.getAllTeosed().get(0);
            if(teos != null)
                TeosValitud(teos.getId(), 0);
        }
    }
    @Override
    protected void onStop() {
        bEsimeneAvamine = false;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("teoseid", this.teoseid);
        outState.putInt("harjutuseid", this.harjutuseid);
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
                            startActivity(i);
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
                        startActivity(intentSeaded);
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
        TeosListFragment teosListFragment = (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                teosListFragment.mMainAdapter.SordiTeosed();
                teosListFragment.mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Kustutatud. Pos:" + itemposition);
                teosListFragment.mMainAdapter.notifyItemRemoved(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisatud");
                teosListFragment.mMainAdapter.SordiTeosed();
                teosListFragment.mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamisel kustutati");
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamist ei viidud lõpule");
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS)){
            VarskendaHarjutusteList();
            int uueharjutuseid = data.getIntExtra("harjutus_id",0);
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            if(mPPManager.getHarjutus(teoseid, uueharjutuseid) != null) {
                HarjutusValitud(teoseid, uueharjutuseid);
            } else {
                Teos teos = mPPManager.getTeos(teoseid);
                List<HarjutusKord> harjutusKorrad = teos.getHarjustuskorrad(getApplicationContext());
                if(!harjutusKorrad.isEmpty()) {
                    HarjutusKord harjutusKord = harjutusKorrad.get(0);
                    if (harjutusKord != null) {
                        HarjutusValitud(teoseid, harjutusKord.getId());
                    }
                }
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
                    SharedPreferences settings =
                            getSharedPreferences(getString(R.string.seadete_fail), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("googlekonto", accountName);
                    editor.apply();
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
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
            if(harjutusfragment != null) {
                ((HarjutusFragmendiKutsuja) harjutusfragment).SuleHarjutus();
                ft.remove(harjutusfragment);
            }

            TeosFragment teosfragment = new TeosFragment();
            Bundle args = new Bundle();
            args.putInt("item_id", -1);
            teosfragment.setArguments(args);
            ft.replace(R.id.teos_hoidja, teosfragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        else {
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
        }
    }
    @Override
    public void TeosValitud(int teoseid, int asukoht) {

        if(bMitmeFragmendiga){
            if(this.teoseid != teoseid) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
                if (harjutusfragment != null) {
                    ((HarjutusFragmendiKutsuja) harjutusfragment).SuleHarjutus();
                    ft.remove(harjutusfragment);
                }
                PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
                Teos teos = mPPManager.getTeos(teoseid);

                List harjutuskorrad = teos.getHarjustuskorrad(getApplicationContext());
                if(!harjutuskorrad.isEmpty()) {
                    HarjutusKord harjutusKord = teos.getHarjustuskorrad(getApplicationContext()).get(0);
                    if (harjutusKord != null) {
                        Fragment harjutusMuudaFragment = new HarjutusMuudaFragment();
                        Bundle argsharjutus = new Bundle();
                        argsharjutus.putInt("teos_id", teoseid);
                        argsharjutus.putInt("harjutus_id", harjutusKord.getId());
                        harjutusMuudaFragment.setArguments(argsharjutus);
                        ft.replace(R.id.harjutus_hoidja, harjutusMuudaFragment);
                        this.harjutuseid = harjutusKord.getId();
                    }
                }

                Fragment teosfragment = new TeosFragment();
                Bundle args = new Bundle();
                args.putInt("item_id", teoseid);
                args.putInt("item_position", asukoht);
                teosfragment.setArguments(args);
                ft.replace(R.id.teos_hoidja, teosfragment);
                this.teoseid = teoseid;

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else {
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", teoseid);
            intent.putExtra("item_position", asukoht);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
            if (BuildConfig.DEBUG) Log.d("PeaActivity", "Teos valitud : " + teoseid + " Holder position: " +
                    asukoht + " Intent: " +
                    getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
        }
    }

    @Override
    public void KustutaTeos(int teosid, int itemposition) {
        TeosListFragment teosListFragment = (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
        teosListFragment.mMainAdapter.notifyItemRemoved(itemposition);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if (harjutusfragment != null) {
            ft.remove(harjutusfragment);
        }
        Fragment teosfragment = getFragmentManager().findFragmentById(R.id.teos_hoidja);
        if (teosfragment != null) {
            ft.remove(teosfragment);
        }
        ft.commit();
        int newitemposition = itemposition == 0 ? itemposition : itemposition - 1;
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        List<Teos> teosed = mPPManager.getAllTeosed();
        if (!teosed.isEmpty()) {
            Teos teos = teosed.get(newitemposition);
            if (teos != null) ;
                TeosValitud(teos.getId(), newitemposition);
        }
    }
    @Override
    public void AlustaHarjutust(int teosid) {

        if(bMitmeFragmendiga && false) {
            if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alusta uut harjutust");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
            if (harjutusfragment != null) {
                ((HarjutusFragmendiKutsuja) harjutusfragment).SuleHarjutus();
                ft.remove(harjutusfragment);
            }

            Fragment harjutusUusFragment = new HarjutusUusFragment();
            Bundle args = new Bundle();
            args.putInt("teos_id", teosid);
            args.putInt("harjutus_id", -1);
            harjutusUusFragment.setArguments(args);
            ft.replace(R.id.harjutus_hoidja, harjutusUusFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alusta uut harjutust");
            Intent intent = new Intent(this, HarjutusUusActivity.class);
            intent.putExtra("teos_id", teosid);
            intent.putExtra("harjutus_id", -1);
            startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
        }
    }

    @Override
    public void LisaTehtudHarjutus(int teosid) {

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lisa tehtud harjutus");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if(harjutusfragment != null) {
            ((HarjutusFragmendiKutsuja)harjutusfragment).SuleHarjutus();
            ft.remove(harjutusfragment);
        }

        Fragment harjutusLisaTehtudFragment = new HarjutusLisaTehtudFragment();
        Bundle args = new Bundle();
        args.putInt("teos_id", teosid);
        args.putInt("harjutus_id", -1);
        harjutusLisaTehtudFragment.setArguments(args);
        ft.replace(R.id.harjutus_hoidja, harjutusLisaTehtudFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void HarjutusValitud(int teosid, int harjutusid) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Avan olemasolevat harjutust. Teosid : " + teosid +
                " Harjutus:" + harjutusid);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if(harjutusfragment != null) {
            ((HarjutusFragmendiKutsuja)harjutusfragment).SuleHarjutus();
            ft.remove(harjutusfragment);
        }

        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        if(mPPManager.getHarjutus(teosid, harjutusid) != null) {
            Fragment harjutusMuudaFragment = new HarjutusMuudaFragment();
            Bundle args = new Bundle();
            args.putInt("teos_id", teosid);
            args.putInt("harjutus_id", harjutusid);
            harjutusMuudaFragment.setArguments(args);
            ft.replace(R.id.harjutus_hoidja, harjutusMuudaFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            this.harjutuseid = harjutusid;
        }

        ft.commit();
    }

    @Override
    public void KustutaHarjutus(int harjutusid) {

        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teos_hoidja);
        if(teosFragment != null) {
            teosFragment.VarskendaHarjutusteJaStatistika();
        }
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(teoseid);
        VarskendaTeosListiElement(mPPManager.getAllTeosed().indexOf(teos));

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if(harjutusfragment != null) {
            ft.remove(harjutusfragment);
        }
        List<HarjutusKord> harjutusKorrad = teos.getHarjustuskorrad(getApplicationContext());
        if(!harjutusKorrad.isEmpty()) {
            HarjutusKord harjutusKord = harjutusKorrad.get(0);
            Fragment harjutusMuudaFragment = new HarjutusMuudaFragment();
            Bundle argsharjutus = new Bundle();
            argsharjutus.putInt("teos_id", teoseid);
            argsharjutus.putInt("harjutus_id", harjutusKord.getId());
            harjutusMuudaFragment.setArguments(argsharjutus);
            ft.replace(R.id.harjutus_hoidja, harjutusMuudaFragment);
            this.harjutuseid = harjutusKord.getId();
        }
        ft.commit();

    }

    @Override
    public void VarskendaHarjutusteList() {
        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teos_hoidja);
        if(teosFragment != null) {
            teosFragment.VarskendaHarjutusteJaStatistika();
        }
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        int listindex = mPPManager.getAllTeosed().indexOf(mPPManager.getTeos(teoseid));
        VarskendaTeosListiElement(listindex);
    }
    @Override
    public void VarskendaHarjutusteListiElement(int position) {

    }

    @Override
    public void VarskendaTeosList() {
        TeosListFragment teosListFragment = (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
        teosListFragment.mMainAdapter.SordiTeosed();
        teosListFragment.mMainAdapter.notifyDataSetChanged();
    }

    @Override
    public void VarskendaTeosListiElement(int position) {
        TeosListFragment teosListFragment = (TeosListFragment) getFragmentManager().findFragmentById(R.id.teoslistfragment);
        teosListFragment.mMainAdapter.notifyItemChanged(position);
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
}
