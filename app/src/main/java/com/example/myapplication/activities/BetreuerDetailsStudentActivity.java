package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ArbeitAdapterOffenStudent;
import com.example.myapplication.models.Arbeit;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BetreuerDetailsStudentActivity extends AppCompatActivity {

    private TextView textViewBetreuerName, textViewBetreuerBeschreibung;
    private RecyclerView recyclerViewArbeiten;
    private ArbeitAdapterOffenStudent adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betreuer_details_student);

        // Initialisierungen
        firestore = FirebaseFirestore.getInstance();
        textViewBetreuerName = findViewById(R.id.textViewNameBetreuerStudent);
        textViewBetreuerBeschreibung = findViewById(R.id.textViewBeschreibungBetreuerStudent);
        recyclerViewArbeiten = findViewById(R.id.recyclerViewArbeitenStudent);
        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapterOffenStudent(arbeitenListe, this); // this ist der Context der Activity

        // RecyclerView Setup
        recyclerViewArbeiten.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArbeiten.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar_betreuer_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Betreuerdetails"); // Setzen Sie hier den gewünschten Titel
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Laden der Daten
        loadBetreuerDetails();
        loadBetreuerArbeiten();
    }

    private void loadBetreuerDetails() {
        // Angenommen, Sie erhalten die Betreuer-UID über einen Intent
        String betreuerUid = getIntent().getStringExtra("betreuerUid");

        firestore.collection("user").document(betreuerUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    String beschreibung = documentSnapshot.getString("beschreibung");

                    textViewBetreuerName.setText(name);
                    textViewBetreuerBeschreibung.setText(beschreibung);
                })
                .addOnFailureListener(e -> {
                    // Fehlerbehandlung
                });
    }

    private void loadBetreuerArbeiten() {
        String betreuerUid = getIntent().getStringExtra("betreuerUid");

        firestore.collection("thesis")
                .whereEqualTo("betreuerUid", betreuerUid)
                .whereEqualTo("zustand", "offen") // Nur Arbeiten mit Zustand "Offen"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    arbeitenListe.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Arbeit arbeit = document.toObject(Arbeit.class);
                        arbeitenListe.add(arbeit);
                    }
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BetreuerDetailsStudentActivity.this, "Fehler beim Laden der offenen Arbeiten: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
