package com.vaskjala.vesiroosi20.pillipaevik;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
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
        TeosListFragmendiKuulaja {

    private ActionBarDrawerToggle mDrawerToggle;
    private boolean bEsimeneAvamine = true;
    private int iSahtliValik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teos_list);

        this.setTitle(getString(R.string.rakenduse_pealkiri));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayShowHomeEnabled(true);
        mAction.setDisplayHomeAsUpEnabled(true);
        mAction.setHomeButtonEnabled(true);

        SeadistaNaviVaade(toolbar);

        if(savedInstanceState != null ) {
            if(BuildConfig.DEBUG) Log.e(getLocalClassName(), "savedInstanceState != null");
            bEsimeneAvamine = false;
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
        Intent intent = new Intent(this, TeosActivity.class);
        intent.putExtra("item_id", -1);
        startActivityForResult(intent,getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
    }

    @Override
    public void TeosValitud(int teoseid, int asukoht) {
         Intent intent = new Intent(this, TeosActivity.class);
        intent.putExtra("item_id", teoseid);
        intent.putExtra("item_position", asukoht);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
        if(BuildConfig.DEBUG) Log.d("PeaActivity", "Teos valitud : " + teoseid + " Holder position: " +
                asukoht + " Intent: " +
                getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
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
