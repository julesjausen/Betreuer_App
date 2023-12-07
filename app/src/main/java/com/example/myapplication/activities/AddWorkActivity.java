package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddWorkActivity extends AppCompatActivity {

    private TextInputEditText editTextWorkName, editTextSubject, editTextDescription;
    private TextView textViewHeaderZweitgutachter, textViewHeaderZustand, textViewHeaderRechnungsstatus;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Spinner spinnerZweitgutachter, spinnerZustand, spinnerRechnungsstatus;
    private Map<String, String> zweitgutachterMap = new HashMap<>();

    private String originalZustand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);

        // Initialisierung der Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAddWork);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Firebase-Instanzen
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // UI-Komponenten initialisieren
        editTextWorkName = findViewById(R.id.editTextWorkNameAddWork);
        editTextSubject = findViewById(R.id.editTextSubjectAddWork);
        editTextDescription = findViewById(R.id.editTextDescriptionAddWork);
        spinnerZweitgutachter = findViewById(R.id.spinnerZweitgutachterAddWork);
        spinnerZustand = findViewById(R.id.spinnerZustandAddWork);
        spinnerRechnungsstatus = findViewById(R.id.spinnerRechnungsstatusAddWork);
        textViewHeaderZustand = findViewById(R.id. textViewZustandHeader);
        textViewHeaderRechnungsstatus = findViewById(R.id.textViewRechnungsstatusHeader);
        textViewHeaderZweitgutachter = findViewById(R.id.textViewZweitgutachterHeader);

        // Speichern-Button
        Button buttonSave = findViewById(R.id.buttonSaveAddWork);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWork();
            }
        });

        // Abbrechen-Button
        Button buttonCancel = findViewById(R.id.buttonCancelZweitgutachter);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Schließe die Aktivität
                finish();
            }
        });

        // Überprüfen, ob eine arbeitUid übergeben wurde, davon abhängig wird der titel der toolbar angepasst
        // und die Arbeit geladen
        String arbeitUid = getIntent().getStringExtra("arbeitUid");
        if (arbeitUid != null && !arbeitUid.isEmpty()) {
            loadAndDisplayLink(arbeitUid);   //Funktion zum Anzeigen des Links, falls ein Student eine Datei hochgeladen hat
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


    // Funktion zum Speichern der Arbeit
    private void saveWork() {
        String workName = editTextWorkName.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String zustand = "offen"; // Standardzustand für neue Arbeiten


        if (spinnerZustand.getVisibility() == View.VISIBLE) {
            zustand = spinnerZustand.getSelectedItem().toString();
        }

        String rechnungsstatusBetreuer = spinnerRechnungsstatus.getSelectedItem().toString();
        String zweitgutachterName = spinnerZweitgutachter.getSelectedItem() != null ? spinnerZweitgutachter.getSelectedItem().toString() : "N/A";

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

        Map<String, Object> work = new HashMap<>();
        work.put("betreuerUid", betreuerUid);
        work.put("nameDerArbeit", workName);
        work.put("studienfach", subject);
        work.put("beschreibung", description);
        work.put("zustand", zustand);
        work.put("rechnungsstatusBetreuer", rechnungsstatusBetreuer);

        String arbeitUid = getIntent().getStringExtra("arbeitUid");

        // Wenn ein Zweitgutachter ausgewählt wurde, speichere seine UID, sonst lösche das Feld
        if (!zweitgutachterName.equals("N/A")) {
            String zweitgutachterUid = zweitgutachterMap.get(zweitgutachterName);
            if (zweitgutachterUid != null) {
                work.put("zweitgutachterUid", zweitgutachterUid);
            } else {
                Toast.makeText(this, "Zweitgutachter nicht gefunden", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (arbeitUid != null && !arbeitUid.isEmpty()) {
            firestore.collection("thesis").document(arbeitUid)
                    .update("zweitgutachterUid", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> Toast.makeText(AddWorkActivity.this, "Zweitgutachter entfernt", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Entfernen des Zweitgutachters", Toast.LENGTH_SHORT).show());
        }

        // Speichere oder aktualisiere die Arbeit
        if (arbeitUid != null && !arbeitUid.isEmpty()) {
            firestore.collection("thesis").document(arbeitUid).update(work)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddWorkActivity.this, "Arbeit aktualisiert", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Aktualisieren der Arbeit", Toast.LENGTH_SHORT).show());
        } else {
            firestore.collection("thesis").add(work)
                    .addOnSuccessListener(documentReference -> {
                        String newArbeitUid = documentReference.getId();
                        firestore.collection("thesis").document(newArbeitUid)
                                .update("arbeitUid", newArbeitUid)
                                .addOnSuccessListener(aVoid -> Toast.makeText(AddWorkActivity.this, "Arbeit gespeichert und arbeitUid aktualisiert.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Aktualisieren der arbeitUid.", Toast.LENGTH_SHORT).show());
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddWorkActivity.this, "Fehler beim Speichern der Arbeit.", Toast.LENGTH_SHORT).show());
        }
    }


    // Funktion zur Darstellung des Links, um die Datei herunterzuladen, die der Student hochgeladen hat.
    private void loadAndDisplayLink(String arbeitUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        TextView linkTextView = findViewById(R.id.linkTextView); // ID entsprechend Ihrer Layout-Datei

        firestore.collection("thesis").document(arbeitUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fileRef = documentSnapshot.getString("storageRef");
                        if (fileRef != null && !fileRef.isEmpty()) {
                            linkTextView.setVisibility(View.VISIBLE);
                            linkTextView.setText("Hochgeladene Datei ansehen");
                            linkTextView.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(fileRef));
                                startActivity(intent);
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Fehler beim Laden der Datei-URL", Toast.LENGTH_SHORT).show());
    }

    //Die Daten der Arbeit werden aus Firebase geladen
    private void loadArbeitData(String arbeitUid) {
        firestore.collection("thesis").document(arbeitUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Arbeit arbeit = documentSnapshot.toObject(Arbeit.class);
                        originalZustand = arbeit.getZustand();
                        editTextWorkName.setText(arbeit.getNameDerArbeit());
                        editTextSubject.setText(arbeit.getStudienfach());
                        editTextDescription.setText(arbeit.getBeschreibung());

                        if (!arbeit.getZustand().equals("offen")) { //Die Spinner sollen nur angezeigt werden, wenn ein Student sich für die Arbeit verbindlich gebucht hat.
                            spinnerZweitgutachter.setVisibility(View.VISIBLE);
                            spinnerZustand.setVisibility(View.VISIBLE);
                            spinnerRechnungsstatus.setVisibility(View.VISIBLE);
                            textViewHeaderZweitgutachter.setVisibility(View.VISIBLE);
                            textViewHeaderZustand.setVisibility(View.VISIBLE);
                            textViewHeaderRechnungsstatus.setVisibility(View.VISIBLE);



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


}
