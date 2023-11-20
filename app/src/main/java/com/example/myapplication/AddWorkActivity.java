package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AddWorkActivity extends AppCompatActivity {

    private TextInputEditText editTextWorkName, editTextSubject, editTextDescription;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Spinner spinnerZweitgutachter, spinnerZustand, spinnerRechnungsstatus;
    private Map<String, String> zweitgutachterMap = new HashMap<>();

    private String originalZustand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);

        // Toolbar-Konfiguration
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        // Firebase-Instanzen
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // EditTexts initialisieren
        editTextWorkName = findViewById(R.id.editTextWorkName);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerZweitgutachter = findViewById(R.id.spinnerZweitgutachter);
        spinnerZustand = findViewById(R.id.spinnerZustand);
        spinnerRechnungsstatus = findViewById(R.id.spinnerRechnungsstatus);

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

        // Überprüfen, ob eine arbeitUid übergeben wurde
        String arbeitUid = getIntent().getStringExtra("arbeitUid");
        if (arbeitUid != null && !arbeitUid.isEmpty()) {
            // Arbeit wird bearbeitet
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Arbeit bearbeiten");
            }
            loadArbeitData(arbeitUid); // Lade die bestehende Arbeit
        } else {
            // Neue Arbeit hinzufügen
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Neue Arbeit hinzufügen");
            }
        }

        setUpSpinners();
    }

    private void saveWork() {
        String workName = editTextWorkName.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String rechnungsstatusBetreuer = spinnerRechnungsstatus.getSelectedItem().toString();
        String zweitgutachterName = spinnerZweitgutachter.getSelectedItem() != null ? spinnerZweitgutachter.getSelectedItem().toString() : "N/A";

        String zustand = originalZustand; // Standardwert ist der ursprüngliche Zustand
        if (spinnerZustand.getVisibility() == View.VISIBLE) {
            zustand = spinnerZustand.getSelectedItem().toString(); // Zustand aus dem Spinner, falls sichtbar
        }

        // Validierung der Eingaben
        if (workName.isEmpty() || subject.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        String betreuerUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (betreuerUid == null) {
            Toast.makeText(this, "Kein Benutzer angemeldet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Die arbeitUid, die beim Bearbeiten verwendet wird

        Map<String, Object> work = new HashMap<>();
        work.put("betreuerUid", betreuerUid);
        work.put("nameDerArbeit", workName);
        work.put("studienfach", subject);
        work.put("beschreibung", description);
        work.put("zustand", zustand);
        work.put("rechnungsstatusBetreuer", rechnungsstatusBetreuer);

        // Die arbeitUid, die beim Bearbeiten verwendet wird
        String arbeitUid = getIntent().getStringExtra("arbeitUid");

        if (!zweitgutachterName.equals("N/A")) {
            String zweitgutachterUid = zweitgutachterMap.get(zweitgutachterName);
            if (zweitgutachterUid != null) {
                work.put("zweitgutachterUid", zweitgutachterUid);
                saveOrUpdateWork(work, arbeitUid);
            } else {
                Toast.makeText(this, "Zweitgutachter nicht gefunden", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (arbeitUid != null && !arbeitUid.isEmpty()) {
                // Lösche das Feld "zweitgutachterUid" aus dem bestehenden Dokument
                firestore.collection("thesis").document(arbeitUid).update("zweitgutachterUid", FieldValue.delete())
                        .addOnSuccessListener(aVoid -> {
                            // Aktualisiere den Rest der Arbeit
                            saveOrUpdateWork(work, arbeitUid);
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Entfernen des Zweitgutachters", Toast.LENGTH_SHORT).show());
            } else {
                // Für neue Arbeit, kein Feld zum Löschen, füge einfach das Dokument hinzu
                firestore.collection("thesis").add(work)
                        .addOnSuccessListener(documentReference -> Toast.makeText(AddWorkActivity.this, "Arbeit gespeichert", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Speichern der Arbeit", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void getZweitgutachterUidByName(String name, UidConsumer uidConsumer) {
        firestore.collection("user")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String uid = queryDocumentSnapshots.getDocuments().get(0).getId();
                        uidConsumer.accept(uid);
                    } else {
                        uidConsumer.accept("N/A");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Fehler beim Laden des Zweitgutachters", Toast.LENGTH_SHORT).show());
    }

    private void saveOrUpdateWork(Map<String, Object> work, String arbeitUid) {
        if (arbeitUid != null && !arbeitUid.isEmpty()) {
            firestore.collection("thesis").document(arbeitUid).update(work)
                    .addOnSuccessListener(aVoid -> Toast.makeText(AddWorkActivity.this, "Arbeit aktualisiert", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Aktualisieren der Arbeit", Toast.LENGTH_SHORT).show());
        } else {
            firestore.collection("thesis").add(work)
                    .addOnSuccessListener(documentReference -> Toast.makeText(AddWorkActivity.this, "Arbeit gespeichert", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Speichern der Arbeit", Toast.LENGTH_SHORT).show());
        }
    }


    private void loadArbeitData(String arbeitUid) {
        firestore.collection("thesis").document(arbeitUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Arbeit arbeit = documentSnapshot.toObject(Arbeit.class);
                        originalZustand = arbeit.getZustand();
                        editTextWorkName.setText(arbeit.getNameDerArbeit());
                        editTextSubject.setText(arbeit.getStudienfach());
                        editTextDescription.setText(arbeit.getBeschreibung());

                        if (!arbeit.getZustand().equals("offen")) {
                            spinnerZweitgutachter.setVisibility(View.VISIBLE);
                            spinnerZustand.setVisibility(View.VISIBLE);
                            spinnerRechnungsstatus.setVisibility(View.VISIBLE);

                            // Prüfe, ob eine Zweitgutachter-UID vorhanden ist, und lade entsprechend die Zweitgutachter-Daten
                            if (arbeit.getZweitgutachterUid() != null && !arbeit.getZweitgutachterUid().isEmpty()) {
                                loadZweitgutachterData(arbeit.getZweitgutachterUid());
                            } else {
                                loadZweitgutachterData(null);
                            }

                            // Vorauswahl für Zustand und Rechnungsstatus
                            setSpinnerSelection(spinnerZustand, arbeit.getZustand());
                            setSpinnerSelection(spinnerRechnungsstatus, arbeit.getRechnungsstatusBetreuer());
                        }
                    } else {
                        Toast.makeText(this, "Arbeit nicht gefunden", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Fehler beim Laden der Daten", Toast.LENGTH_SHORT).show());
    }


    private void setUpSpinners() {
        // Initialisiere den Adapter für den Zustand-Spinner
        ArrayAdapter<CharSequence> zustandAdapter = ArrayAdapter.createFromResource(this,
                R.array.zustand_options, android.R.layout.simple_spinner_item);
        zustandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZustand.setAdapter(zustandAdapter);

        // Initialisiere den Adapter für den Rechnungsstatus-Spinner
        ArrayAdapter<CharSequence> rechnungsstatusAdapter = ArrayAdapter.createFromResource(this,
                R.array.rechnungsstatus_options, android.R.layout.simple_spinner_item);
        rechnungsstatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRechnungsstatus.setAdapter(rechnungsstatusAdapter);

        // Lade die Zweitgutachter-Daten
        loadZweitgutachterData();
    }
    private void loadZweitgutachterData() {
        loadZweitgutachterData(null);
    }

    private void loadZweitgutachterData(String selectedZweitgutachterUid) {
        firestore.collection("user")
                .whereEqualTo("role", "Zweitgutachter")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> names = new ArrayList<>();
                    names.add("N/A");
                    String selectedName = "N/A"; // Standardwert, falls kein Zweitgutachter gefunden wird
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        String name = snapshot.getString("name");
                        String uid = snapshot.getId();
                        names.add(name);
                        zweitgutachterMap.put(name, uid);
                        if (uid.equals(selectedZweitgutachterUid)) {
                            selectedName = name; // Setze den Namen, wenn die UID übereinstimmt
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerZweitgutachter.setAdapter(adapter);
                    int spinnerPosition = adapter.getPosition(selectedName);
                    spinnerZweitgutachter.setSelection(spinnerPosition);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Fehler beim Laden der Zweitgutachter", Toast.LENGTH_SHORT).show());
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (value != null && adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }



    // Reagiere auf die Auswahl der Home/Up-Taste in der Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public interface UidConsumer {
        void accept(String uid);
    }
}
