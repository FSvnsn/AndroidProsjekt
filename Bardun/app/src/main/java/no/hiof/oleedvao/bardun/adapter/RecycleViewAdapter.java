package no.hiof.oleedvao.bardun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.Teltplass;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Teltplass> mData;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    final long ONE_MEGABYTE = 1024 * 1024;

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
        setImage(mData.get(position).getImageId(), holder);

    }

    private void setImage(String imageId, final MyViewHolder holder) {
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        StorageReference imageRef = mStorageReference.child("images/" + imageId);

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.iv_image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_beskrivelse;
        private ImageView iv_image;

        public MyViewHolder(View itemView){
            super(itemView);

            tv_name = itemView.findViewById(R.id.txtCardNavn);
            tv_beskrivelse = itemView.findViewById(R.id.txtCardBeskrivelse);
            iv_image = itemView.findViewById(R.id.imgTeltplassQuickview);
        }
    }
}
