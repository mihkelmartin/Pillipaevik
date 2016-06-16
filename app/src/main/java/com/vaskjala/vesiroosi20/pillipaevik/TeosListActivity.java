package com.vaskjala.vesiroosi20.pillipaevik;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.vaskjala.vesiroosi20.pillipaevik.aruanded.Kuuaruanne;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.GoogleDriveRestUhendus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.GoogleDriveUhendus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.*;

public class TeosListActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleItemRecyclerViewAdapter mMainAdapter;
    private PilliPaevikDatabase mPPManager;
    private boolean bEsimeneAvamine = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("Peaaken", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teos_list);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        String minueesnimi = sharedPref.getString("minueesnimi", "");
        String tiitel = getString(R.string.rakenduse_pealkiri);
        if(!minueesnimi.isEmpty()) {
            tiitel = tiitel + " - " + minueesnimi;
        }

        this.setTitle(tiitel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayShowHomeEnabled(true);
        mAction.setDisplayHomeAsUpEnabled(true);
        mAction.setHomeButtonEnabled(true);

        mPPManager = new PilliPaevikDatabase(getApplicationContext());

        setupDrawer(toolbar);

        View recyclerView = findViewById(R.id.harjutua_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if(savedInstanceState != null )
            bEsimeneAvamine = false;

    }

    @Override
    protected void onStart() {
        Log.d(getLocalClassName(), "onStart");
        super.onStart();
        // TODO Asünkroonselt
        PaevaHarjutusteProgress();
        NadalaHarjutusteProgress();
        KuuHarjutusteProgress();
        // https://developers.google.com/android/guides/api-client#handle_connection_failures

        GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
        if(bEsimeneAvamine) {
            Log.d(getLocalClassName(), "Alusta Drive ühenduse loomisega");
            mGDU.LooDriveUhendus(this);
            Log.d(getLocalClassName(), "Drive ühenduse loomine läbi");
        }
        mGDU.setmDriveActivity(this);
    }

    @Override
    protected void onStop() {
        bEsimeneAvamine = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
        mGDU.KatkestaUhnedus();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teoslistmenyy, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        Log.d("cek", "item selected");
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("Peaaken", "Sahtel valitud");
            return true;
        }
        if(item.getItemId()==R.id.seaded){
            Log.d("TeosListActivity", "Seaded vajutatud");
            Intent intent = new Intent(this, SeadedActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.lisateos){
            Log.d("TeosListActivity", "Lisateos vajutatud");
            Intent intent = new Intent(this, TeosActivity.class);
            intent.putExtra("item_id", -1);
            startActivityForResult(intent,getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                Log.d(getLocalClassName(), "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                Log.d(getLocalClassName(), "Tagasi TeosActivityst. Pos:" + itemposition);
                mMainAdapter.notifyItemRemoved(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisatud");
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamisel kustutati");
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA)) {
                Log.d(getLocalClassName(), "Tagasi TeosActivityst. Lisamist ei viidud lõpule");
            }
        }
        if( requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                Log.d(getLocalClassName(), "Drive configureerimine õnnestus: " + resultCode);
                GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
                GoogleApiClient mGoogleApiClient = mGDU.GoogleApiKlient();
                if(mGoogleApiClient != null)
                    mGoogleApiClient.connect();
            } else {
                Log.d(getLocalClassName(), "Drive configureerimine katkestati: " + resultCode);
            }

        }
        if( requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null &&
                    data.getExtras() != null) {
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    SharedPreferences settings =
                            getSharedPreferences(getString(R.string.seadete_fail), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("googledrivekonto", accountName);
                    editor.apply();

                    GoogleDriveRestUhendus mGDRU = GoogleDriveRestUhendus.getInstance();
                    GoogleAccountCredential mCredential = mGDRU.GoogleApiCredential();
                    mCredential.setSelectedAccountName(accountName);
                    Log.d(getLocalClassName(), "Valitud konto: " + accountName);
                    mGDRU.Uhendu();
                }
            }
        }
        if( requestCode == 1004){
            Log.d(getLocalClassName(), "See tuli REST API tagasidiena on ActivityResulti: " + resultCode);
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
                Log.d("TeosListActivity", "Teos valitud : " + holder.mItem.getId() + " Holder position: " +
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
    private void setupDrawer (Toolbar toolbar) {
        DrawerLayout mDrawerLayout;
        ListView mDrawerList;

        final String[][] mSahtliValikud = new String[2][];
        mSahtliValikud[0] = getResources().getStringArray(R.array.sahtli_valikud_ikoonid);
        mSahtliValikud[1] = getResources().getStringArray(R.array.sahtli_valikud);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(mDrawerLayout,mDrawerList));

        mDrawerList.setAdapter(new ListAdapter() {

            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return mSahtliValikud[0].length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View returnview = null;
                if(convertView != null)
                    returnview = convertView;
                if(returnview == null) {
                    if(position == 0){
                        returnview = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.sahtli_esimene_rida, parent, false);
                    } else {
                        returnview = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.drawer_list_item, parent, false);
                        ImageView imageView = (ImageView) (returnview.findViewById(R.id.drawerimageid));
                        imageView.setImageResource(getResources().getIdentifier(mSahtliValikud[0][position], null, null));
                        TextView textView = (TextView) (returnview.findViewById(R.id.drawertextid));
                        textView.setText(mSahtliValikud[1][position]);
                        Log.d("mDrawerList.setAdapter", mSahtliValikud[0][position]);
                        Log.d("mDrawerList.setAdapter", mSahtliValikud[1][position]);
                    }
                }
                return returnview;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar, R.string.ava_sahtel,
                R.string.sule_sahtel
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }
    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        DrawerLayout mDrawerLayout;
        ListView mDrawerList;

        DrawerItemClickListener(DrawerLayout dL, ListView lV) {
            mDrawerLayout = dL;
            mDrawerList = lV;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            Intent i;
            switch (position) {
                case 1 :
                    i = new Intent(view.getContext(), HarjutusteKalenderActivity.class);
                    startActivity(i);
                    break;
                case 2 :
                    Kuuaruanne ka = new Kuuaruanne(getApplicationContext());
                    ka.setAruandeperioodinimi("2016 juuni");
                    Date now = new Date();
                    ka.setPerioodialgus(Tooriistad.MoodustaKuuAlgusKuupaev(now));
                    ka.setPerioodilopp(Tooriistad.MoodustaKuuLopuKuupaev(now));
                    i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{ka.getOpetajaepost()});
                    i.putExtra(Intent.EXTRA_SUBJECT, ka.Teema(getApplicationContext()));
                    i.putExtra(Intent.EXTRA_TEXT, ka.AruandeKoguTekst(getApplicationContext()));
                    try {
                        startActivity(Intent.createChooser(i, "Saada aruanne..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getParent(), "E-posti äppi ei paista olevat ....", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }

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
}
