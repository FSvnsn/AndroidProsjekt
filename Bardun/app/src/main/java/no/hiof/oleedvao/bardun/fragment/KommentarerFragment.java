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

import no.hiof.oleedvao.bardun.teltplass.Kommentar;
import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.adapter.KommentarRecyclerViewAdapter;

//(Aws Rh, 2018)
public class KommentarerFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Kommentar> lstKommentarer;
    private KommentarRecyclerViewAdapter recyclerViewAdapter;

    private String teltplassId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    //konstruktor
    public KommentarerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout
        view = inflater.inflate(R.layout.fragment_kommentarer,container,false);

        //setter opp recycler view
        recyclerView = view.findViewById(R.id.RecyclerViewKommentarer);
        recyclerViewAdapter = new KommentarRecyclerViewAdapter(getContext(), lstKommentarer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo : sjekk internett
        //instansierer database variabler
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();

        try{
            UID = CUser.getUid();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        if (getArguments() != null){
            teltplassId = getArguments().getString("teltplassId");
        }

        lstKommentarer = new ArrayList<>();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getKommentarer(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getKommentarer(DataSnapshot dataSnapshot){
        //todo : sjekk internett
        //henter kommentarer fra databasen og legger dem til i recycler view
        for(DataSnapshot ds : dataSnapshot.child("teltplassKommentarer").child(teltplassId).getChildren()){

            Kommentar kommentar = new Kommentar();

            kommentar.setDate(ds.child("date").getValue().toString());
            kommentar.setBrukernavn(ds.child("brukernavn").getValue().toString());
            kommentar.setKommentar(ds.child("kommentar").getValue().toString());
            lstKommentarer.add(kommentar);
            recyclerViewAdapter.notifyDataSetChanged();

        }
    }
}
