package com.vaskjala.vesiroosi20.pillipaevik;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TeosListFragment extends Fragment {

    public SimpleItemRecyclerViewAdapter mMainAdapter;
    private PilliPaevikDatabase mPPManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TeosListFragment", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TeosListFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_teos_list, container, false);

        View recyclerView = view.findViewById(R.id.harjutua_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return view;
    }


    @Override
    public void onStart() {
        if(BuildConfig.DEBUG) Log.d("TeosListFragment", "onStart");
        super.onStart();

        // TODO Asünkroonselt
        // TODO Fragmendi puhul peab seda värksendama ka siis kui Teos kustutatakse
        PaevaHarjutusteProgress();
        NadalaHarjutusteProgress();
        KuuHarjutusteProgress();
        // https://developers.google.com/android/guides/api-client#handle_connection_failures

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.teoslistmenyy, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        if(item.getItemId()==R.id.lisateos){
            if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Lisa teos vajutatud");
            Intent intent = new Intent(getActivity(), TeosActivity.class);
            intent.putExtra("item_id", -1);
            startActivityForResult(intent,getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Tagasi TeosActivityst. Teos muudetud Pos:" + itemposition);
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                int itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Tagasi TeosActivityst. Kustutatud. Pos:" + itemposition);
                mMainAdapter.notifyItemRemoved(itemposition);
            }
        }
        if (requestCode == getResources().getInteger(R.integer.TEOSLIST_ACTIVITY_INTENT_LISA)) {
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD)) {
                if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Tagasi TeosActivityst. Lisatud");
                mMainAdapter.SordiTeosed();
                mMainAdapter.notifyDataSetChanged();
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD)) {
                if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Tagasi TeosActivityst. Lisamisel kustutati");
            }
            if (resultCode == getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_UUS_LOOMATA)) {
                if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Tagasi TeosActivityst. Lisamist ei viidud lõpule");
            }
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
                if(BuildConfig.DEBUG) Log.d("PeaActivity", "Teos valitud : " + holder.mItem.getId() + " Holder position: " +
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


    private void PaevaHarjutusteProgress(){

        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(now, now);
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) getView().findViewById(R.id.paevasharjutatud)).setText(szharjutatud );

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0);
        ((TextView) getView().findViewById(R.id.paevanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) getView().findViewById(R.id.paevasharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }
    private void NadalaHarjutusteProgress(){

        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaNädalaAlgusKuupaev(now),
                Tooriistad.MoodustaNädalaLopuKuupaev(now));
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) getView().findViewById(R.id.nadalasharjutatud)).setText(szharjutatud );

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());
        int paevakordaja = (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? 7 : c.get(Calendar.DAY_OF_WEEK) -1;
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0) * paevakordaja;
        ((TextView) getView().findViewById(R.id.nadalanorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) getView().findViewById(R.id.nadalasharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }
    private void KuuHarjutusteProgress(){

        Date now = new Date();
        int harjutatud = mPPManager.ArvutaPerioodiMinutid(Tooriistad.MoodustaKuuAlgusKuupaev(now),
                Tooriistad.MoodustaKuuLopuKuupaev(now));
        String szharjutatud = String.valueOf(harjutatud)+" m";
        ((TextView) getView().findViewById(R.id.kuusharjutatud)).setText(szharjutatud );

        Calendar c = Calendar.getInstance();
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());        int paevakordaja = c.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        int vajaharjutada = sharedPref.getInt("paevasharjutada", 0) * paevakordaja;
        ((TextView) getView().findViewById(R.id.kuunorm)).setText(String.valueOf(vajaharjutada+" m"));

        ProgressBar pPHT = ((ProgressBar) getView().findViewById(R.id.kuusharjutatudtulp));
        pPHT.setMax(vajaharjutada);
        pPHT.setProgress(harjutatud);
        if(harjutatud >= vajaharjutada)
            pPHT.getProgressDrawable().setColorFilter(0xff009900, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pPHT.getProgressDrawable().setColorFilter(null);

    }

}
