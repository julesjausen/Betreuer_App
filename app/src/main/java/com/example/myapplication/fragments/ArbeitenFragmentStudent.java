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

        firestore = FirebaseFirestore.getInstance();
        currentStudentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.recyclerViewArbeitenBetreutStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapterBetreutStudent(arbeitenListe);
        recyclerView.setAdapter(adapter);

        loadArbeiten();

        return view;
    }

    private void loadArbeiten() {
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
