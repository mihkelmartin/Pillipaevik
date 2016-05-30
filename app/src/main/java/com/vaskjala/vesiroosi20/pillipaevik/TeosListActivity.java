package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import java.util.*;

public class TeosListActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleItemRecyclerViewAdapter mMainAdapter;
    static final int TEOS_MUUTMINE_TEGU = 0;

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

        setupDrawer(toolbar);

        View recyclerView = findViewById(R.id.harjutua_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Peaaken", "Läbin stoppi");
    }

    @Override
    protected void onStart() {
        super.onStart();
        PaevaHarjutusteProgress();
        NadalaHarjutusteProgress();
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
                Log.d("TeosListActivity", "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                Log.d("TeosListActivity", "Tagasi TeosActivityst. Pos:" + itemposition);
                mMainAdapter.notifyItemRemoved(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                Log.d("TeosListActivity", "Tagasi TeosActivityst. Lisatud");
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                Log.d("TeosListActivity", "Tagasi TeosActivityst. Lisamisel kustutati");
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA)) {
                Log.d("TeosListActivity", "Tagasi TeosActivityst. Lisamist ei viidud lõpule");
            }
        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
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
            public Teos mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
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
                    returnview = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.drawer_list_item, parent, false);
                    ImageView imageView = (ImageView) (returnview.findViewById(R.id.drawerimageid));
                    imageView.setImageResource(getResources().getIdentifier(mSahtliValikud[0][position], null, null));
                    TextView textView = (TextView) (returnview.findViewById(R.id.drawertextid));
                    textView.setText(mSahtliValikud[1][position]);
                    Log.d("cek", mSahtliValikud[0][position]);
                    Log.d("cek", mSahtliValikud[1][position]);
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
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

    private void PaevaHarjutusteProgress(){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        int harjutatud = mPPManager.ArvutaKuupaevaMinutid("'now'");
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) findViewById(R.id.paevasharjutatud)).setText(szharjutatud );

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0);
        ((TextView) findViewById(R.id.paevanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) findViewById(R.id.paevasharjutatudtulp));
        int color = (harjutatud >= vajaharjutada) ? Color.GREEN : Color.MAGENTA;
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        pPHT.getProgressDrawable().setColorFilter(
                color, android.graphics.PorterDuff.Mode.SRC_IN);

    }

    private void NadalaHarjutusteProgress(){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());
        // Calendri nädalad on 1- 54, SQLite 0 - 53, seega - 1
        int harjutatud = mPPManager.ArvutaNadalaMinutid(c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR));
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) findViewById(R.id.nadalasharjutatud)).setText(szharjutatud );

        int paevakordaja = (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? 7 : c.get(Calendar.DAY_OF_WEEK) -1;
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0) * paevakordaja;
        ((TextView) findViewById(R.id.nadalanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) findViewById(R.id.nadalasharjutatudtulp));
        int color = (harjutatud >= vajaharjutada) ? Color.GREEN : Color.MAGENTA;
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        pPHT.getProgressDrawable().setColorFilter(
                color, android.graphics.PorterDuff.Mode.SRC_IN);

    }
}
