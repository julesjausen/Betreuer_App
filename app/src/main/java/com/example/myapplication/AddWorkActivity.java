package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddWorkActivity extends AppCompatActivity {

    private TextInputEditText editTextWorkName, editTextSubject, editTextDescription;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);

        // Toolbar-Konfiguration
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Neue Arbeit hinzufügen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Firebase-Instanzen
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // EditTexts initialisieren
        editTextWorkName = findViewById(R.id.editTextWorkName);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextDescription = findViewById(R.id.editTextDescription);

        // Speichern-Button
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWork();
            }
        });

        // Abbrechen-Button
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Schließe die Aktivität
                finish();
            }
        });
    }

    private void saveWork() {
        String workName = editTextWorkName.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validierung der Eingaben
        if (workName.isEmpty() || subject.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase UID des aktuellen Benutzers
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "Kein Benutzer angemeldet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Daten für die Firestore-Dokument
        Map<String, Object> work = new HashMap<>();
        work.put("betreuerUid", uid);
        work.put("nameDerArbeit", workName);
        work.put("studienfach", subject);
        work.put("beschreibung", description);
        work.put("zustand", "offen");
        work.put("rechnungsstatusBetreuer", "N/A");
        work.put("rechnungsstatusZweitgutachter", "N/A");

        // Speichere die neue Arbeit in Firestore
        firestore.collection("thesis").add(work)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddWorkActivity.this, "Arbeit gespeichert", Toast.LENGTH_SHORT).show();
                    // Schließe die Aktivität
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Speichern der Arbeit", Toast.LENGTH_SHORT).show());
    }

    // Reagiere auf die Auswahl der Home/Up-Taste in der Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
