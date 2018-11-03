package no.hiof.oleedvao.bardun.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.Teltplass;
import no.hiof.oleedvao.bardun.adapter.RecycleViewAdapter;

public class MineTeltplasserFragment extends Fragment {

    View view;
    private RecyclerView myRecyclerView;
    private List<Teltplass> listTeltplass;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser CUser;
    private String UID;

    public MineTeltplasserFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //Source: https://www.youtube.com/watch?v=T_QfRU-A3s4

        view = inflater.inflate(R.layout.fragment_mine_teltplasser, container, false);
        myRecyclerView = view.findViewById(R.id.mine_teltplasser_recyclerview);
        RecycleViewAdapter recycleAdapter = new RecycleViewAdapter(getContext(), listTeltplass);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(recycleAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        CUser = mAuth.getCurrentUser();
        UID = CUser.getUid();
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

        //listTeltplass = new ArrayList<>();
        //listTeltplass.add(new Teltplass("Hallo", "Hei"));
    }

    private void getMineTeltplasser(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.child("mineTeltplasser").child(UID).getChildren()){
            Teltplass teltplass1 = new Teltplass();
            teltplass1.setNavn(ds.getValue(Teltplass.class).getNavn());
            teltplass1.setBeskrivelse(ds.getValue(Teltplass.class).getBeskrivelse());

            listTeltplass.add(teltplass1);
        }
        Toast.makeText(getActivity(), String.valueOf(dataSnapshot.child("mineTeltplasser").child(UID).getChildrenCount()), Toast.LENGTH_SHORT).show();
    }
}
