package no.hiof.oleedvao.bardun.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.teltplass.Teltplass;
import no.hiof.oleedvao.bardun.adapter.RecycleViewAdapter;

public class MineFavoritterFragment extends Fragment {

    View view;
    private RecyclerView myRecyclerView;
    private List<Teltplass> listTeltplass;
    private RecycleViewAdapter recycleAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    public MineFavoritterFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mine_favoritter, container, false);
        myRecyclerView = view.findViewById(R.id.mine_favoritter_recyclerview);
        recycleAdapter = new RecycleViewAdapter(getContext(), listTeltplass);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(recycleAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        try
        {
            UID = CUser.getUid();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        listTeltplass = new ArrayList<Teltplass>();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getMineTeltplasser(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMineTeltplasser(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.child("mineFavoritter").child(UID).getChildren()){
            String latLng = ds.child("latLng").getValue(String.class);
            String navn = ds.child("navn").getValue(String.class);
            String beskrivelse = ds.child("beskrivelse").getValue(String.class);
            String imageID = ds.child("imageId").getValue(String.class);

            listTeltplass.add(new Teltplass(latLng, navn, beskrivelse, imageID));
            recycleAdapter.notifyDataSetChanged();
        }
    }
}