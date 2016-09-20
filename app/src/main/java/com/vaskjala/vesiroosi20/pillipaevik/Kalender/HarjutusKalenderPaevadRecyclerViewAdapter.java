package com.vaskjala.vesiroosi20.pillipaevik.kalender;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */

public class HarjutusKalenderPaevadRecyclerViewAdapter
        extends RecyclerView.Adapter<HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder> {

    private final List<KalendriKirje> mValues;
    private HarjutusteKalenderFragmendiKuulaja harjutusteKalenderFragmendiKuulaja;
    public HarjutusKalenderPaevadRecyclerViewAdapter(List<KalendriKirje> items, HarjutusteKalenderFragmendiKuulaja harjutusteKalenderFragmendiKuulaja) {
        mValues = items;
        this.harjutusteKalenderFragmendiKuulaja = harjutusteKalenderFragmendiKuulaja;
    }

    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position).KasPaev())
            return R.layout.kalender_paev_rida;
        else
            return R.layout.kalender_paev_harjutus_rida;
    }

    @Override
    public HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder retVal = null;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        if(viewType == R.layout.kalender_paev_rida) retVal = new HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolderPaev(view, viewType);
        if(viewType == R.layout.kalender_paev_harjutus_rida) retVal = new HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolderHarjutus(view, viewType);
        return retVal;
    }

    public class ListiKuulaja implements View.OnClickListener {

        private HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder;

        public ListiKuulaja(HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder){
            this.holder = holder;
        }
        public void onClick(View v) {
            holder.onClick(v);
        }
    }
    @Override
    public void onBindViewHolder(final HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.AndmedVaatele();
        HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja pLK =
                new HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja(holder);
        holder.mView.setOnClickListener(pLK);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public KalendriKirje mItem;
        public View mView;

        public ViewHolder(View view, int viewType) {
            super(view);
        }
        public void AndmedVaatele(){

        }
        public void onClick(View v){

        }
    }
    public class ViewHolderPaev extends ViewHolder {

        public TextView mKuupaev = null;
        public TextView mHarjutusteArv = null;
        public TextView mHarjutusteKestus = null;


        public ViewHolderPaev(View view, int viewType) {
            super(view, viewType);
            mView = view;
            mKuupaev = (TextView) view.findViewById(R.id.kuupaev);
            mHarjutusteArv = (TextView) view.findViewById(R.id.paevakalenderharjutustearv);
            mHarjutusteKestus = (TextView) view.findViewById(R.id.paevakalenderharjutustekestus);
        }

        @Override
        public void AndmedVaatele() {
            super.AndmedVaatele();
            mKuupaev.setText(Tooriistad.KujundaKuupaevSonalineLuhike(((PaevaKirje)mItem).kuupaev));
            mHarjutusteArv.setText(String.valueOf(((PaevaKirje) mItem).kordadearv));
            mHarjutusteKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(((PaevaKirje) mItem).pikkussekundites/60));
        }

        @Override
        public void onClick(View v) {
            PaevaKirje mPK = (PaevaKirje)mItem;
            if (mPK.bAndmebaasistLaetud) {
                if (BuildConfig.DEBUG) Log.d("ListiKuulaja", "Juba laetud baasist :" + mPK.kuupaev);
            }
            else {
                PilliPaevikDatabase pilliPaevikDatabase = new PilliPaevikDatabase(v.getContext().getApplicationContext());
                pilliPaevikDatabase.KuupaevaHarjutusKorrad(mPK);
            }
            if(mPK.Harjutused != null && !mPK.Harjutused.isEmpty()){
                if(mPK.bHarjutusedAvatud) {
                    if(BuildConfig.DEBUG) Log.d("ListiKuulaja", "Eemaldan ja teavitan" );
                    mValues.removeAll(mPK.Harjutused);
                    mPK.bHarjutusedAvatud = false;
                    notifyItemRangeRemoved(getAdapterPosition() + 1, mPK.Harjutused.size() );
                } else {
                    if(BuildConfig.DEBUG) Log.d("ListiKuulaja", "Lisan ja teavitan" );
                    mValues.addAll(getAdapterPosition() + 1, mPK.Harjutused);
                    mPK.bHarjutusedAvatud = true;
                    notifyItemRangeInserted(getAdapterPosition() + 1, mPK.Harjutused.size() );
                }
            }
            harjutusteKalenderFragmendiKuulaja.PaevValitud();
            super.onClick(v);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mKuupaev.getText() + "'";
        }
    }
    public class ViewHolderHarjutus extends ViewHolder {

        public TextView mTeoseNimi = null;
        public TextView mHarjutuseKirjeldus = null;
        public ImageView mHeliFailiPilt = null;
        public TextView mHarjutuseKestus = null;


        public ViewHolderHarjutus(View view, int viewType) {
            super(view, viewType);
            mView = view;
            mTeoseNimi = (TextView) view.findViewById(R.id.kalender_paev_harjutus_teosenimi);
            mHeliFailiPilt = (ImageView) view.findViewById(R.id.kalender_paev_harjutus_helifaili_pilt);
            mHarjutuseKestus = (TextView) view.findViewById(R.id.kalender_paev_harjutus_harjutuse_kestus);
            mHarjutuseKirjeldus = (TextView) view.findViewById(R.id.kalender_paev_harjutus_harjutuse_kirjeldus);
        }

        @Override
        public void AndmedVaatele() {
            HarjutuskordKirje hHKK = (HarjutuskordKirje)mItem;
            super.AndmedVaatele();
            mTeoseNimi.setText(hHKK.getTiitel());
            mHarjutuseKirjeldus.setText(hHKK.harjutusKord.getHarjutusekirjeldus());
            if(hHKK.harjutusKord.KasKuvadaSalvestusePilt(mView.getContext().getApplicationContext()))
                mHeliFailiPilt.setVisibility(View.VISIBLE);
            else
                mHeliFailiPilt.setVisibility(View.GONE);
            mHarjutuseKestus.setText(Tooriistad.KujundaAeg(hHKK.harjutusKord.getPikkussekundites()*1000));
        }

        @Override
        public void onClick(View v) {
            HarjutuskordKirje mHKK = (HarjutuskordKirje)mItem;
            harjutusteKalenderFragmendiKuulaja.
                    HarjutusValitud(mHKK.harjutusKord.getTeoseid(),mHKK.harjutusKord.getId(), getLayoutPosition());
            if(BuildConfig.DEBUG) Log.d("HK RecyclerView", "Avan olemasolevat harjutust. Teosid : " + mHKK.harjutusKord.getTeoseid() +
                    " Harjutus:" + mHKK.harjutusKord.getId());
            super.onClick(v);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTeoseNimi.getText() + "'";
        }
    }

}