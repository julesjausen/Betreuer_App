package com.example.myapplication.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// Adapter für die Darstellung von offenen Arbeit-Objekten in einer RecyclerView für studenten

public class ArbeitAdapterOffenStudent extends RecyclerView.Adapter<ArbeitAdapterOffenStudent.ArbeitViewHolder> {

    private List<Arbeit> arbeitenListe;
    private Context context; // Kontext hinzufügen



    //Konstruktor
    public ArbeitAdapterOffenStudent(List<Arbeit> arbeitenListe, Context context) {
        this.arbeitenListe = arbeitenListe;
        this.context = context; // Kontext speichern

    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellt ein neues ViewHolder-Objekt für jedes Element in der RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_offen_student, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        // Setzt die Daten eines Arbeit-Objekts in die Ansicht eines ViewHolder
        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewFachName.setText(arbeit.getStudienfach());
        holder.textViewArbeitBeschreibung.setText(arbeit.getBeschreibung());

        // Event-Listener für Klicks auf jedes Element der RecyclerView
        holder.itemView.setOnClickListener(v -> {
            showConfirmationDialog(v.getContext(), arbeit); // Kontext wird übergeben, Funktion zum bestätigen der buchung
        });
    }

    @Override
    public int getItemCount() {
        return arbeitenListe.size();
    }

    static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewArbeitName, textViewFachName, textViewArbeitBeschreibung;

        ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitNameOffenStudent);
            textViewFachName = itemView.findViewById(R.id.textViewArbeitFachOffenStudent);
            textViewArbeitBeschreibung = itemView.findViewById(R.id.textViewArbeitBeschreibungOffenStudent);

        }
    }

    private void showConfirmationDialog(Context context, Arbeit arbeit) {
        new AlertDialog.Builder(context)
                .setTitle("Arbeit verbindlich buchen?")
                .setMessage("Möchten Sie die Arbeit " + arbeit.getNameDerArbeit() + " verbindlich buchen?")
                .setPositiveButton("Ja", (dialog, which) -> bucheArbeit(context, arbeit)) // Kontext an bucheArbeit weitergeben
                .setNegativeButton("Nein", null)
                .show();
    }

    private void bucheArbeit(Context context, Arbeit arbeit) { // Methode um den Kontext erweitern
        String studentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Status auf "in Abstimmung" setzen und studentUid speichern
        firestore.collection("thesis").document(arbeit.getArbeitUid())
                .update("zustand", "in Abstimmung", "studentUid", studentUid)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Arbeit erfolgreich gebucht.", Toast.LENGTH_SHORT).show();
                    // Schließe die aktuelle Activity und kehre zurück
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Fehler beim Buchen der Arbeit: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
