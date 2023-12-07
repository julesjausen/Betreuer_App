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
import com.example.myapplication.adapters.BetreuteArbeitenAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


// Fragment zur Darstellung von betreute Arbeiten für Betreuer:innen
public class BetreuteArbeitenFragment extends Fragment {

    private RecyclerView recyclerView;
    private BetreuteArbeitenAdapter adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    //notwendiger leerer konstruktor
    public BetreuteArbeitenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_betreute_arbeiten, container, false);


        //initialisieren on firestore, auth, recyclerview, adapter etc
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewBetreuteArbeiten);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new BetreuteArbeitenAdapter(arbeitenListe);
        recyclerView.setAdapter(adapter);


        setUpRealtimeUpdates();

        return view;
    }

    //sobald sich was ändert, wird diese änderung angezeigt
    private void setUpRealtimeUpdates() {
        String currentBetreuerUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        Query query = firestore.collection("thesis")
                .whereEqualTo("betreuerUid", currentBetreuerUid)
                .whereNotEqualTo("zustand", "offen")
                .orderBy("zustand") // zuerst nach Zustand sortieren
                .orderBy("nameDerArbeit"); // dann nach Name der Arbeit sortieren

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
