package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyWorkActivity extends AppCompatActivity {
    //Für Zweitgutachter die Möglichkeit eine Arbeit zu bearbeiten, für die sie als zweitgutachter eingeteilt wurden.

    private TextView textViewWorkName, textViewDescription, textViewStatus;
    private Spinner spinnerRechnungsstatus;
    private FirebaseFirestore firestore;
    private String arbeitUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_work);

        // Toolbar-Konfiguration
        Toolbar toolbar = findViewById(R.id.toolbarAddWork);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rechnungsstatus bearbeiten");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialisierungen
        firestore = FirebaseFirestore.getInstance();
        textViewWorkName = findViewById(R.id.textViewWorkNameZweitgutachter);
        textViewDescription = findViewById(R.id.textViewDescriptionZweitgutachter);
        textViewStatus = findViewById(R.id.textViewStatusZweitgutachter);
        spinnerRechnungsstatus = findViewById(R.id.spinnerRechnungsstatusAddWork);

        Button buttonSave = findViewById(R.id.buttonSaveAddWork);
        Button buttonCancel = findViewById(R.id.buttonCancelZweitgutachter);

        arbeitUid = getIntent().getStringExtra("arbeitUid");
        loadArbeitData();

        buttonSave.setOnClickListener(v -> saveChanges());
        buttonCancel.setOnClickListener(v -> finish());
    }
    //Lädt die Daten der Arbeit
    private void loadArbeitData() {
        firestore.collection("thesis").document(arbeitUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Arbeit arbeit = documentSnapshot.toObject(Arbeit.class);
                        textViewWorkName.setText(arbeit.getNameDerArbeit());
                        textViewDescription.setText(arbeit.getBeschreibung());
                        textViewStatus.setText(arbeit.getZustand());

                        String rechnungsstatus = arbeit.getRechnungsstatusZweitgutachter() != null
                                ? arbeit.getRechnungsstatusZweitgutachter() : "N/A";
                        //der Rechnungsstatus soll im Spinner entsprechend angezeigt werden
                        setUpSpinner(rechnungsstatus);
                    } else {
                        Toast.makeText(this, "Arbeit nicht gefunden", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Fehler beim Laden der Arbeit", Toast.LENGTH_SHORT).show());
    }

    private void setUpSpinner(String selectedRechnungsstatus) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rechnungsstatus_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRechnungsstatus.setAdapter(adapter);
        int position = adapter.getPosition(selectedRechnungsstatus);
        spinnerRechnungsstatus.setSelection(position);
    }

    //Neuer Rechnungsstatus wird gespeichert
    private void saveChanges() {
        String rechnungsstatus = spinnerRechnungsstatus.getSelectedItem().toString();

        firestore.collection("thesis").document(arbeitUid)
                .update("rechnungsstatusZweitgutachter", rechnungsstatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ModifyWorkActivity.this, "Rechnungsstatus aktualisiert", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ModifyWorkActivity.this, "Fehler beim Aktualisieren des Rechnungsstatus", Toast.LENGTH_SHORT).show());
    }
    //Zur Navigation zwischen Activities
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
