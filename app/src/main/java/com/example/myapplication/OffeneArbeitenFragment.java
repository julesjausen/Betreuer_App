package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Arbeit;
import com.example.myapplication.ArbeitAdapter;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OffeneArbeitenFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArbeitAdapter adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public OffeneArbeitenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offene_arbeiten, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewOffeneArbeiten);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapter(arbeitenListe);
        recyclerView.setAdapter(adapter);

        loadArbeiten();

        return view;
    }

    private void loadArbeiten() {
        String currentBetreuerUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        Toast.makeText(getActivity(), "load AA", Toast.LENGTH_SHORT).show();
        
        Query query = firestore.collection("thesis")
                .whereEqualTo("betreuerUid", currentBetreuerUid)
                .whereEqualTo("zustand", "offen")
                .orderBy("nameDerArbeit");

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Arbeit arbeit = document.toObject(Arbeit.class);
                    arbeitenListe.add(arbeit);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {

            Log.d("TAG", "loadArbeiten:fehler  " + e);


            // Behandlung von Fehlern, z. B. Anzeige einer Fehlermeldung
        });
    }
}
