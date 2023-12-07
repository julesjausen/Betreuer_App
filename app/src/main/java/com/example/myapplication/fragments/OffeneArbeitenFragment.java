package com.example.myapplication.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.example.myapplication.adapters.ArbeitAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

//fragment für das anzeigen von offenen arbeiten für betreuer
public class OffeneArbeitenFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArbeitAdapter adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    //notwendiger leerer Konstruktor
    public OffeneArbeitenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offene_arbeiten, container, false);
        //initialisieren der komponenten
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewOffeneArbeiten);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapter(arbeitenListe);
        recyclerView.setAdapter(adapter);

        setUpRealtimeUpdates();

        return view;
    }


    //sobald es eine änderung gibt, ändert sich die recycler view
    private void setUpRealtimeUpdates() {
        String currentBetreuerUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        Query query = firestore.collection("thesis")
                .whereEqualTo("betreuerUid", currentBetreuerUid)
                .whereEqualTo("zustand", "offen")
                .orderBy("nameDerArbeit");

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                Toast.makeText(getContext(), "Fehler beim Laden der Daten", Toast.LENGTH_SHORT).show();
                return;
            }

            arbeitenListe.clear();
            for (DocumentSnapshot document : snapshots.getDocuments()) {
                Arbeit arbeit = document.toObject(Arbeit.class);
                arbeitenListe.add(arbeit);
            }
            adapter.notifyDataSetChanged();
        });
    }


}
