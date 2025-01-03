package com.vaskjala.vesiroosi20.pillipaevik;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.*;

import static android.content.Context.MODE_PRIVATE;

public class TeosListFragment extends Fragment {

    private SimpleItemRecyclerViewAdapter mMainAdapter;
    private PilliPaevikDatabase mPPManager;
    private TeosListFragmendiKuulaja teosListFragmendiKuulaja;
    private RecyclerView mTeosList;
    private boolean bNaitaTeoseTanastStatistikat = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            teosListFragmendiKuulaja = (TeosListFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama TeosListFragmendiKuulaja");
        }
    }

    @SuppressWarnings("deprecation")
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            teosListFragmendiKuulaja = (TeosListFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama TeosListFragmendiKuulaja");
        }
    }

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

        mTeosList = (RecyclerView) view.findViewById(R.id.harjutua_list);
        assert mTeosList != null;
        setupRecyclerView(mTeosList);

        return view;
    }

    @Override
    public void onStart() {
        if(BuildConfig.DEBUG) Log.d("TeosListFragment", "onStart");
        super.onStart();
        bNaitaTeoseTanastStatistikat = Tooriistad.kasNaitaTeoseTanastStatistikat(getActivity().getApplicationContext());
        VarskendaProgressid();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.teoslistmenyy, menu);
        menu.findItem(R.id.naitaarhiivi).setChecked(Tooriistad.kasNaitaArhiivi(getActivity().getApplicationContext()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.lisateos){
            if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Lisa teos vajutatud");
            teosListFragmendiKuulaja.UusTeos();
        }
        if(item.getItemId()==R.id.naitaarhiivi){
            if(BuildConfig.DEBUG) Log.d("TeosListFragment", "Näita arhiivi vajutatud");
            Context context = getActivity().getApplicationContext();
            Tooriistad.SeadistaNaitaArhiiviSeadeteFailis(context, !Tooriistad.kasNaitaArhiivi(context));
            item.setChecked(Tooriistad.kasNaitaArhiivi(context));
            mMainAdapter.FiltreeriTeosed();
            mMainAdapter.notifyDataSetChanged();
            teosListFragmendiKuulaja.ValiTeineTeos(0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<Teos> teosed = mPPManager.getAllTeosed();
        mMainAdapter = new SimpleItemRecyclerViewAdapter(teosed);
        recyclerView.setAdapter(mMainAdapter);
    }
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Teos> teoslist;
        private final List<Teos> mValues = new ArrayList<Teos>();

        public SimpleItemRecyclerViewAdapter(List<Teos> items) {
            teoslist = items;
            FiltreeriTeosed();
        }

        public void SordiTeosed(){
            Collections.sort(teoslist);
            Collections.sort(mValues);
        }
        public void FiltreeriTeosed(){
            mValues.clear();
            if(Tooriistad.kasNaitaArhiivi(getActivity().getApplicationContext())) {
                mValues.addAll(teoslist);
            } else {
                for(Teos teos : teoslist){
                    if(teos.getKasutusviis() == 1)
                        mValues.add(teos);
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.teos_list_rida, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(holder.mItem.getNimi());

            if(holder.mItem.getKasutusviis() == 1)
                holder.mArhiiviOsa.setVisibility(View.GONE);
            else
                holder.mArhiiviOsa.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                public void run() {
                    final int[] stat = mPPManager.TeoseHarjutusKordadeStatistika(holder.mItem.getId());
                    holder.mHarjutusteArv.post(new Runnable() {
                        public void run() {
                            holder.mHarjutusteArv.setText(String.valueOf(stat[1]));
                            holder.mHarjutuseKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(stat[0]/60));
                            if(bNaitaTeoseTanastStatistikat){
                                holder.mHarjutusteArvTana.setVisibility(View.VISIBLE);
                                holder.mHarjutuseKestusTana.setVisibility(View.VISIBLE);
                                holder.mHarjutusteArvTana.setText("(" + String.valueOf(stat[3]) + ")");
                                holder.mHarjutuseKestusTana.setText("(" + Tooriistad.KujundaHarjutusteMinutidTabloo(stat[2]/60) + ")");
                            } else {
                                holder.mHarjutusteArvTana.setVisibility(View.GONE);
                                holder.mHarjutuseKestusTana.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
            ListiKlikiKuulaja pLK = new ListiKlikiKuulaja(holder);
            ListiPikaKlikiKuulaja pLPK = new ListiPikaKlikiKuulaja(holder);
            holder.mView.setOnClickListener(pLK);
            holder.mView.setOnLongClickListener(pLPK);
        }

        public class ListiKlikiKuulaja implements View.OnClickListener {

            private ViewHolder holder;

            public ListiKlikiKuulaja(ViewHolder holder){
                this.holder = holder;
            }
            public void onClick(View v) {
                teosListFragmendiKuulaja.TeosValitud(holder.mItem);
            }
        }

        public class ListiPikaKlikiKuulaja implements View.OnLongClickListener {

            private ViewHolder holder;

            public ListiPikaKlikiKuulaja(ViewHolder holder){
                this.holder = holder;
            }
            public boolean onLongClick(View v) {
                if(Tooriistad.kaspikkVajutusAlustabHarjutuse(getActivity().getApplicationContext())) {
                    teosListFragmendiKuulaja.AlustaHarjutust(holder.mItem.getId());
                }
                return true;
            }
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
            public final TextView mHarjutusteArvTana;
            public final TextView mHarjutuseKestusTana;
            public final LinearLayout mArhiiviOsa;

            public Teos mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mHarjutusteArv = (TextView) view.findViewById(R.id.teoslistteoseharjutustearv);
                mHarjutuseKestus = (TextView) view.findViewById(R.id.teoslistteoseharjutustekestus);
                mHarjutusteArvTana = (TextView) view.findViewById(R.id.teoslistteoseharjutustearvtana);
                mHarjutuseKestusTana = (TextView) view.findViewById(R.id.teoslistteoseharjutustekestustana);
                mArhiiviOsa = (LinearLayout) view.findViewById(R.id.arhiiviosa);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public List<Teos> AnnaTeosList() {
        return mMainAdapter.mValues;
    }
    public void UuendaTeosList(){
        mMainAdapter.FiltreeriTeosed();
        mMainAdapter.SordiTeosed();
        mMainAdapter.notifyDataSetChanged();
    }
    public int EemaldaTeosListist(Teos teos){
        int pos = mMainAdapter.mValues.indexOf(teos);
        mMainAdapter.notifyItemRemoved(pos);
        mMainAdapter.FiltreeriTeosed();
        return pos;
    }
    public void UuendaTeosListis(Teos teos){
        mMainAdapter.notifyItemChanged(mMainAdapter.mValues.indexOf(teos));
    }
    public void EemaldaPositsioonListist(int pos){
        mMainAdapter.FiltreeriTeosed();
        mMainAdapter.notifyItemRemoved(pos);
    }
    public void KeriTeosListKohale(int asukoht){
        mTeosList.scrollToPosition(asukoht);
    }

    public void KeriLisTeosele(Teos teos){
        KeriTeosListKohale(AnnaTeosList().indexOf(teos));
    }

    public void KeriLisTeoseid(int teoseid){
       KeriLisTeosele(mPPManager.getTeos(teoseid));
    }

    public void VarskendaProgressid(){
        PaevaHarjutusteProgress();
        NadalaHarjutusteProgress();
        KuuHarjutusteProgress();
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
        c.setTime(Tooriistad.HetkeKuupaevNullitudSekunditega());
        int paevakordaja = c.get(Calendar.DAY_OF_MONTH);
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
