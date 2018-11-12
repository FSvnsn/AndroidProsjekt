package no.hiof.oleedvao.bardun.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.fragment.Kommentar;

public class KommentarRecyclerViewAdapter extends RecyclerView.Adapter<KommentarRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Kommentar> mData;

    public KommentarRecyclerViewAdapter(Context mContext, List<Kommentar> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.item_kommentar, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textViewKommentarBrukernavn.setText(mData.get(position).getBrukerNavn());
        holder.textViewKommentar.setText(mData.get(position).getKommentar());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewKommentarBrukernavn;
        private TextView textViewKommentar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewKommentarBrukernavn = itemView.findViewById(R.id.textViewKommentarBrukernavn);
            textViewKommentar = itemView.findViewById(R.id.textViewKommentar);
        }
    }
}
