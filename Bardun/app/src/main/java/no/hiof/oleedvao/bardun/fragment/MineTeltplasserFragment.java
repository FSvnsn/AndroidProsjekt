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

import java.util.ArrayList;
import java.util.List;

import no.hiof.oleedvao.bardun.R;
import no.hiof.oleedvao.bardun.Teltplass;
import no.hiof.oleedvao.bardun.adapter.RecycleViewAdapter;

public class MineTeltplasserFragment extends Fragment {

    View view;
    private RecyclerView myRecyclerView;
    List<Teltplass> listTeltplass;

    public MineTeltplasserFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        listTeltplass = new ArrayList<>();
        listTeltplass.add(new Teltplass("Et Sted", "Ett sted hvor ting skjer"));
        listTeltplass.add(new Teltplass("Et annet sted", "Ett sted hvor ingenting skjer"));
        listTeltplass.add(new Teltplass("Der borte", "Noe skjer der"));
        listTeltplass.add(new Teltplass("Her borte", "Kom hit for å få ting til å skje"));
        listTeltplass.add(new Teltplass("Hvorhen", "Vi vet ikke om ting skjer"));
        listTeltplass.add(new Teltplass("Alle steder", "Masse skjer samtidig"));

    }
}
