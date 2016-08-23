package com.vaskjala.vesiroosi20.pillipaevik;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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

public class TeosListActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja {

    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleItemRecyclerViewAdapter mMainAdapter;
    private PilliPaevikDatabase mPPManager;
    private boolean bEsimeneAvamine = true;
    private int iSahtliValik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG) Log.d("Peaaken", "onCreate");

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

        mPPManager = new PilliPaevikDatabase(getApplicationContext());

        SeadistaNaviVaade(toolbar);

        View recyclerView = findViewById(R.id.harjutua_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

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

        // TODO Asünkroonselt
        PaevaHarjutusteProgress();
        NadalaHarjutusteProgress();
        KuuHarjutusteProgress();
        // https://developers.google.com/android/guides/api-client#handle_connection_failures

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
                            valiAruandeKuu.show(getSupportFragmentManager(), "ValiAruandeKuu");
                        } else {
                            Tooriistad.NaitaHoiatust((Activity) view.getContext(),
                                    getString(R.string.aruande_tegemise_hoiatuse_pealkiri),
                                    getString(R.string.aruande_tegemise_keeldumise_pohjus));

                        }
                        break;
                    case R.id.seaded :
                        if(BuildConfig.DEBUG) Log.d("TeosListActivity", "Seaded vajutatud");
                        Intent intentSeaded = new Intent(view.getContext(), SeadedActivity.class);
                        startActivity(intentSeaded);
                        break;
                    case R.id.teave :
                        if(BuildConfig.DEBUG) Log.d("TeosListActivity", "Seaded vajutatud");
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teoslistmenyy, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if(BuildConfig.DEBUG) Log.d("Peaaken", "Sahtel valitud");
            return true;
        }
        if(item.getItemId()==R.id.lisateos){
            if(BuildConfig.DEBUG) Log.d("TeosListActivity", "Lisateos vajutatud");
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", -1);
            startActivityForResult(intent,getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Pos:" + itemposition);
                mMainAdapter.notifyItemRemoved(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisatud");
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<Teos> teosed = mPPManager.getAllTeosed();
        mMainAdapter = new SimpleItemRecyclerViewAdapter(teosed);
        recyclerView.setAdapter(mMainAdapter);
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Teos> mValues;

        public SimpleItemRecyclerViewAdapter(List<Teos> items) {
            mValues = items;
        }

        public void SordiTeosed(){
            Collections.sort(mValues);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.teos_list_rida, parent, false);
            return new ViewHolder(view);
        }

        public class ListiKuulaja implements View.OnClickListener {

            private ViewHolder holder;

            public ListiKuulaja(ViewHolder holder){
                this.holder = holder;
            }
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TeosActivity.class);
                intent.putExtra("item_id", holder.mItem.getId());
                intent.putExtra("item_position", holder.getLayoutPosition());
                startActivityForResult(intent, getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
                if(BuildConfig.DEBUG) Log.d("TeosListActivity", "Teos valitud : " + holder.mItem.getId() + " Holder position: " +
                        holder.getLayoutPosition() + " Intent: " +
                        getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA));
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(holder.mItem.getNimi());

            new Thread(new Runnable() {
                public void run() {
                    final int[] stat = mPPManager.TeoseHarjutusKordadeStatistika(holder.mItem.getId());
                    holder.mHarjutusteArv.post(new Runnable() {
                        public void run() {
                            holder.mHarjutusteArv.setText(String.valueOf(stat[1]));
                            holder.mHarjutuseKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(stat[0]/60));
                        }
                    });
                }
            }).start();
            ListiKuulaja pLK = new ListiKuulaja(holder);
            holder.mView.setOnClickListener(pLK);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public final TextView mHarjutusteArv;
            public final TextView mHarjutuseKestus;

            public Teos mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mHarjutusteArv = (TextView) view.findViewById(R.id.teoslistteoseharjutustearv);
                mHarjutuseKestus = (TextView) view.findViewById(R.id.teoslistteoseharjutustekestus);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
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

    private void PaevaHarjutusteProgress(){

        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(now, now);
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) findViewById(R.id.paevasharjutatud)).setText(szharjutatud );

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0);
        ((TextView) findViewById(R.id.paevanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) findViewById(R.id.paevasharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }
    private void NadalaHarjutusteProgress(){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());

        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaNädalaAlgusKuupaev(now),
                Tooriistad.MoodustaNädalaLopuKuupaev(now));
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) findViewById(R.id.nadalasharjutatud)).setText(szharjutatud );

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());
        int paevakordaja = (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? 7 : c.get(Calendar.DAY_OF_WEEK) -1;
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0) * paevakordaja;
        ((TextView) findViewById(R.id.nadalanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) findViewById(R.id.nadalasharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }
    private void KuuHarjutusteProgress(){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());

        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaKuuAlgusKuupaev(now),
                Tooriistad.MoodustaKuuLopuKuupaev(now));
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) findViewById(R.id.kuusharjutatud)).setText(szharjutatud );

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());        int paevakordaja = c.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0) * paevakordaja;
        ((TextView) findViewById(R.id.kuunorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) findViewById(R.id.kuusharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }

    private int getiSahtliValik() {
        return iSahtliValik;
    }
    private void setiSahtliValik(int iSahtliValik) {
        this.iSahtliValik = iSahtliValik;
    }
}
