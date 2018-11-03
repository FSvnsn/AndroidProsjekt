package no.hiof.oleedvao.bardun.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.Teltplass;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    Context mContext;
    List<Teltplass> mData;

    public RecycleViewAdapter(Context mContext, List<Teltplass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_teltplass, viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_name.setText(mData.get(position).getNavn());
        holder.tv_beskrivelse.setText(mData.get(position).getBeskrivelse());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_beskrivelse;

        public MyViewHolder(View itemView){
            super(itemView);

            tv_name = itemView.findViewById(R.id.txtCardNavn);
            tv_beskrivelse = itemView.findViewById(R.id.txtCardBeskrivelse);
        }
    }
}
