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

import java.util.ArrayList;
import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.adapter.KommentarRecyclerViewAdapter;

public class KommentarerFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private List<Kommentar> lstKommentarer;
    private KommentarRecyclerViewAdapter recyclerViewAdapter;

    public KommentarerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kommentarer,container,false);
        recyclerView = view.findViewById(R.id.RecyclerViewKommentarer);
        recyclerViewAdapter = new KommentarRecyclerViewAdapter(getContext(), lstKommentarer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstKommentarer = new ArrayList<>();
        lstKommentarer.add(new Kommentar("Knerten", "NANI!!!"));
        lstKommentarer.add(new Kommentar("Putin","Blyat..."));
    }
}
