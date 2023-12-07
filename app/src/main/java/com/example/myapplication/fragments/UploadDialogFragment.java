package com.example.myapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

// DialogFragment für das Hochladen einer Datei und Anzeigen von Arbeit-Details.
public class UploadDialogFragment extends DialogFragment {

    private String tutorName;
    private String tutorEmail;
    private String workName;
    private String workDescription;
    private String thesisUid;

    private Uri fileUri;

    private static final int FILE_SELECT_CODE = 0;

    // Konstruktor für das DialogFragment, empfängt notwendige Daten über Parameter
    public UploadDialogFragment(String tutorName, String tutorEmail, String workName, String workDescription, String thesisUid) {
        this.tutorName = tutorName;
        this.tutorEmail = tutorEmail;
        this.workName = workName;
        this.workDescription = workDescription;
        this.thesisUid = thesisUid;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Layout-Inflation für Dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_upload_dialog, null);

        TextView tutorNameTextView = view.findViewById(R.id.textViewTutorNameDialog);
        TextView tutorEmailTextView = view.findViewById(R.id.textViewTutorEmailDialog);
        TextView workDescriptionTextView = view.findViewById(R.id.textViewWorkDescriptionDialog);
        Button selectFileButton = view.findViewById(R.id.buttonSelectFile);
        Button uploadFileButton = view.findViewById(R.id.uploadFileButton);

        tutorNameTextView.setText(tutorName);
        tutorEmailTextView.setText(tutorEmail);
        workDescriptionTextView.setText(workDescription);

        selectFileButton.setOnClickListener(v -> openFileSelector());

        // Event-Listener für Dateiauswahl und Upload-Buttons
        uploadFileButton.setOnClickListener(v -> {
            if (fileUri != null) {
                // Hochladen der ausgewählten Datei
                uploadFileToFirebaseStorage(fileUri, thesisUid);
            } else {
                // Benutzer darauf hinweisen, dass zuerst eine Datei ausgewählt werden muss
                Toast.makeText(getContext(), "Bitte wählen Sie zuerst eine Datei aus.", Toast.LENGTH_SHORT).show();
            }
        });

        // Titel des Dialogs setzen
        builder.setTitle(workName);

        builder.setView(view)
                .setPositiveButton("Schließen", (dialog, id) -> dismiss());

        return builder.create();
    }

    // Methode zum Öffnen eines Dateiauswahlfensters
    private void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Wählen Sie eine Datei"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Benutzer informieren, falls kein Dateimanager verfügbar ist
            Toast.makeText(getContext(), "Bitte installieren Sie einen Dateimanager.", Toast.LENGTH_SHORT).show();
        }
    }

    // Methode zur Behandlung der Dateiauswahl
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();
            Toast.makeText(getContext(), "Datei ausgewählt: " + fileUri.getPath(), Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Methode zum Hochladen der Datei zu Firebase Storage
    private void uploadFileToFirebaseStorage(Uri fileUri, String thesisUid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Festlegen des Pfads und Namens für die Datei in Firebase Storage
        StorageReference fileRef = storageRef.child("uploaded_files/" + fileUri.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Nach erfolgreichem Upload, die URL der Datei abrufen
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Speichern der URL in Firestore unter thesis/uid/storageRef
                saveFileReferenceInFirestore(thesisUid, uri.toString());
            });
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Fehler beim Hochladen der Datei", Toast.LENGTH_SHORT).show());
    }

    // Methode zum Speichern der Dateireferenz in Firestore
    private void saveFileReferenceInFirestore(String thesisUid, String fileUrl) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference thesisRef = firestore.collection("thesis").document(thesisUid);

        thesisRef.update("storageRef", fileUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Dateireferenz erfolgreich gespeichert.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Fehler beim Speichern der Dateireferenz.", Toast.LENGTH_SHORT).show());
    }
}
