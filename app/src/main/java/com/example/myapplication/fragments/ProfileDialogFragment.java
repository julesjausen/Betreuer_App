package com.example.myapplication.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//öffnet ein kleines Fenster, hier für das Anzeigen und Anpassen des Profils eines Nutzers
public class ProfileDialogFragment extends DialogFragment {

    private TextInputEditText editTextName, editTextFach, editTextBeschreibung, editTextEmail;
    private TextView textViewUid, textViewRole;
    private FirebaseFirestore firestore;
    private String userId;


    //konstruktor
    public ProfileDialogFragment(String userId) {
        // Benötigt die User-ID, um die richtigen Daten zu laden
        this.userId = userId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_profile_dialog, null);
        editTextName = view.findViewById(R.id.editTextNameProfile);
        editTextFach = view.findViewById(R.id.editTextFachProfile);
        editTextBeschreibung = view.findViewById(R.id.editTextBeschreibungProfile);
        editTextEmail = view.findViewById(R.id.editTextEmailProfile);
        textViewUid = view.findViewById(R.id.textViewUidProfile);
        textViewRole = view.findViewById(R.id.textViewRoleProfile);

        firestore = FirebaseFirestore.getInstance();

        // Lade die aktuellen Daten aus Firestore
        firestore.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editTextName.setText(documentSnapshot.getString("name"));
                        editTextFach.setText(documentSnapshot.getString("fach"));
                        editTextBeschreibung.setText(documentSnapshot.getString("beschreibung"));
                        textViewUid.setText(documentSnapshot.getString("uid"));
                        textViewRole.setText(documentSnapshot.getString("role"));
                        editTextEmail.setText(documentSnapshot.getString("email"));
                    } else {
                        Toast.makeText(getContext(), "Benutzerdaten nicht gefunden.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Fehler beim Laden der Daten.", Toast.LENGTH_SHORT).show());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Speichern", (dialog, id) -> saveProfile())
                .setNegativeButton("Abbrechen", (dialog, id) -> ProfileDialogFragment.this.getDialog().cancel());

        return builder.create();
    }

    private void saveProfile() {
        String name = editTextName.getText().toString();
        String fach = editTextFach.getText().toString();
        String beschreibung = editTextBeschreibung.getText().toString();
        String email = editTextEmail.getText().toString();

        // Überprüfen, ob die Eingabefelder nicht leer sind
        if (name.isEmpty() || (fach.isEmpty() && editTextFach.getVisibility() == View.VISIBLE) || beschreibung.isEmpty()|| email.isEmpty())  {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_SHORT).show();
                return;
            }
            return; // Verhindert schließen des Dialogs, wenn die Felder leer sind
        }

        // Speicher die aktualisierten Daten in Firestore
        DocumentReference userRef = firestore.collection("user").document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        if (editTextFach.getVisibility() == View.VISIBLE) {
            updates.put("fach", fach);
        }
        updates.put("beschreibung", beschreibung);
        updates.put("email", email);

        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Profil aktualisiert.", Toast.LENGTH_SHORT).show();
                        dismiss(); // Schließe Dialogfenster nach Speichern
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Fehler beim Speichern der Daten.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
