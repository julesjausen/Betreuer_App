package com.example.myapplication.fragments;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ArbeitAdapterBetreutStudent;
import com.example.myapplication.models.Arbeit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// Fragment zur Darstellung von betreuten Arbeiten eines Studierenden
public class ArbeitenFragmentStudent extends Fragment {

    private RecyclerView recyclerView;
    private ArbeitAdapterBetreutStudent adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private String currentStudentUid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arbeiten_student, container, false);

        // Initialisierung von Firebase Firestore und UID des aktuellen Nutzers
        firestore = FirebaseFirestore.getInstance();
        currentStudentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //recycler view konfiguration
        recyclerView = view.findViewById(R.id.recyclerViewArbeitenBetreutStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapterBetreutStudent(arbeitenListe, getContext());
        recyclerView.setAdapter(adapter);

        // Laden der betreuten Arbeiten aus der Firestore-Datenbank
        loadArbeiten();

        return view;
    }

    private void loadArbeiten() {
        // Abfrage der Datenbank nach Arbeiten, die dem aktuell eingeloggten Studierenden zugeordnet sind
        firestore.collection("thesis")
                .whereEqualTo("studentUid", currentStudentUid)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Fehlerbehandlung
                        return;
                    }
                    arbeitenListe.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Arbeit arbeit = doc.toObject(Arbeit.class);
                            arbeitenListe.add(arbeit);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
