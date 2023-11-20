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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class BetreuteArbeitenFragment extends Fragment {

    private RecyclerView recyclerView;
    private BetreuteArbeitenAdapter adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public BetreuteArbeitenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_betreute_arbeiten, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewBetreuteArbeiten);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arbeitenListe = new ArrayList<>();
        adapter = new BetreuteArbeitenAdapter(arbeitenListe);
        recyclerView.setAdapter(adapter);

        loadBetreuteArbeiten();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBetreuteArbeiten();
    }

    private void loadBetreuteArbeiten() {
        arbeitenListe.clear();
        String currentBetreuerUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        Query query = firestore.collection("thesis")
                .whereEqualTo("betreuerUid", currentBetreuerUid)
                .whereNotEqualTo("zustand", "offen")
                .orderBy("zustand") // zuerst nach Zustand sortieren
                .orderBy("nameDerArbeit"); // dann nach Name der Arbeit sortieren


        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                arbeitenListe.clear(); // Löschen Sie die Liste vor dem Hinzufügen neuer Elemente, um Duplikate zu vermeiden
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Arbeit arbeit = document.toObject(Arbeit.class);
                    arbeitenListe.add(arbeit);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Log.e("BetreuteArbeitenFragment", "loadBetreuteArbeiten: Fehler", e);
            Toast.makeText(getActivity(), "Fehler beim Laden der betreuten Arbeiten", Toast.LENGTH_SHORT).show();
        });
    }
}
