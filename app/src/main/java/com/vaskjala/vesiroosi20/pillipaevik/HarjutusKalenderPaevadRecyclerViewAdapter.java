package com.vaskjala.vesiroosi20.pillipaevik;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kalender_paev_rida, parent, false);
        return new HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder(view);
    }

    public class ListiKuulaja implements View.OnClickListener {

        private HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder;

        public ListiKuulaja(HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder){
            this.holder = holder;
        }
        public void onClick(View v) {

        }
    }
    @Override
    public void onBindViewHolder(final HarjutusKalenderPaevadRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mKuupaev.setText(Tooriistad.KujundaKuupaevSonaline(holder.mItem.kuupaev));
        holder.mHarjutusteArv.setText(String.valueOf(holder.mItem.kordadearv));
        holder.mHarjutuseKestus.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(holder.mItem.pikkussekundites/60));

        HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja pLK =
                new HarjutusKalenderPaevadRecyclerViewAdapter.ListiKuulaja(holder);
        holder.mView.setOnClickListener(pLK);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKuupaev;
        public final TextView mHarjutusteArv;
        public final TextView mHarjutuseKestus;

        public PaevaKirje mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mKuupaev = (TextView) view.findViewById(R.id.kuupaev);
            mHarjutusteArv = (TextView) view.findViewById(R.id.paevakalenderharjutustearv);
            mHarjutuseKestus = (TextView) view.findViewById(R.id.paevakalenderharjutustekestus);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mKuupaev.getText() + "'";
        }
    }
}