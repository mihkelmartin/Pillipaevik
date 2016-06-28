package com.vaskjala.vesiroosi20.pillipaevik;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */

public class HarjutusKalenderPaevadRecyclerViewAdapter
        extends RecyclerView.Adapter<HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder> {

    private final List<PaevaKirje> mValues;
    public HarjutusKalenderPaevadRecyclerViewAdapter(List<PaevaKirje> items) {
        mValues = items;
    }

    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position).bPeaKirje)
            return R.layout.kalender_paev_rida;
        else
            return R.layout.kalender_paev_harjutus_rida;
    }

    @Override
    public HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder(view, viewType);
    }

    public class ListiKuulaja implements View.OnClickListener {

        private HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder;

        public ListiKuulaja(HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder){
            this.holder = holder;
        }
        public void onClick(View v) {
            if(holder.mItem.bPeaKirje) {
                if (holder.mItem.bAndmebaasistLaetud) {
                    if (BuildConfig.DEBUG) Log.e("ListiKuulaja", "Juba laetud baasist :" + holder.mItem.kuupaev);
                }
                else {
                    PilliPaevikDatabase pilliPaevikDatabase = new PilliPaevikDatabase(v.getContext());
                    pilliPaevikDatabase.KuupaevaHarjutusKorrad(holder.mItem);
                }
                if(holder.mItem.Harjutused != null && !holder.mItem.Harjutused.isEmpty()){
                    if(holder.mItem.bHarjutusedAvatud) {
                        if(BuildConfig.DEBUG) Log.e("ListiKuulaja", "Eemaldan ja teavitan" );
                        mValues.removeAll(holder.mItem.Harjutused);
                        holder.mItem.bHarjutusedAvatud = false;
                        notifyItemRangeRemoved(holder.getAdapterPosition() + 1, holder.mItem.Harjutused.size() );
                    } else {
                        if(BuildConfig.DEBUG) Log.e("ListiKuulaja", "Lisan ja teavitan" );
                        mValues.addAll(holder.getAdapterPosition() + 1, holder.mItem.Harjutused);
                        holder.mItem.bHarjutusedAvatud = true;
                        notifyItemRangeInserted(holder.getAdapterPosition() + 1, holder.mItem.Harjutused.size() );
                    }
                }
            }
        }
    }
    @Override
    public void onBindViewHolder(final HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if(holder.mItem.bPeaKirje) {
            holder.mKuupaev.setText(Tooriistad.KujundaKuupaevSonalineLuhike(holder.mItem.kuupaev));
            holder.mHarjutusteArv.setText(String.valueOf(holder.mItem.kordadearv));
            holder.mHarjutusteKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(holder.mItem.pikkussekundites/60));

            HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja pLK =
                    new HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja(holder);
            holder.mView.setOnClickListener(pLK);
        } else {
            holder.mTeoseNimi.setText(holder.mItem.Teos);
            if(holder.mItem.DriveId == null || holder.mItem.DriveId.isEmpty())
                holder.mHeliFailiPilt.setVisibility(View.GONE);
            else
                holder.mHeliFailiPilt.setVisibility(View.VISIBLE);
            holder.mHarjutuseKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(holder.mItem.harjutusepikkus/60));
        }
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public int mviewType;
        public TextView mKuupaev = null;
        public TextView mHarjutusteArv = null;
        public TextView mHarjutusteKestus = null;

        public TextView mTeoseNimi = null;
        public ImageView mHeliFailiPilt = null;
        public TextView mHarjutuseKestus = null;


        public PaevaKirje mItem;

        public ViewHolder(View view, int viewType) {
            super(view);
            mviewType = viewType;
            mView = view;
            if(viewType == R.layout.kalender_paev_rida) {
                mKuupaev = (TextView) view.findViewById(R.id.kuupaev);
                mHarjutusteArv = (TextView) view.findViewById(R.id.paevakalenderharjutustearv);
                mHarjutusteKestus = (TextView) view.findViewById(R.id.paevakalenderharjutustekestus);
            } else if(viewType == R.layout.kalender_paev_harjutus_rida){
                mTeoseNimi = (TextView) view.findViewById(R.id.kalender_paev_harjutus_teosenimi);
                mHeliFailiPilt = (ImageView) view.findViewById(R.id.kalender_paev_harjutus_helifaili_pilt);
                mHarjutuseKestus = (TextView) view.findViewById(R.id.kalender_paev_harjutus_harjutuse_kestus);
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mKuupaev.getText() + "'";
        }
    }
}