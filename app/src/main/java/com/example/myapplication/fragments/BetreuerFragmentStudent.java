package com.example.myapplication.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.BetreuerAdapterStudent; // Angepasster Adaptername
import com.example.myapplication.models.Betreuer; // Ihre Betreuer-Modellklasse
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class BetreuerFragmentStudent extends Fragment {

    private RecyclerView recyclerView;
    private BetreuerAdapterStudent adapter; // Angepasster Adaptername
    private List<Betreuer> betreuerListe;
    private FirebaseFirestore firestore;

    public BetreuerFragmentStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_betreuer_student, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewBetreuterStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        betreuerListe = new ArrayList<>();
        adapter = new BetreuerAdapterStudent(betreuerListe); // Ihr angepasster Adapter
        recyclerView.setAdapter(adapter);

        setUpRealtimeUpdates();

        return view;
    }

    private void setUpRealtimeUpdates() {
        firestore.collection("user")
                .whereEqualTo("role", "Betreuer")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("BetreuerFragment", "SnapshotListener Fehler", e);
                        return;
                    }
                    betreuerListe.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Betreuer betreuer = doc.toObject(Betreuer.class);
                        betreuer.setBetreuerUid(doc.getId()); // Stellen Sie sicher, dass Sie die UID hier setzen
                        betreuerListe.add(betreuer);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

}
