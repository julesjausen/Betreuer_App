package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.fragments.UploadDialogFragment;
import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ArbeitAdapterBetreutStudent extends RecyclerView.Adapter<ArbeitAdapterBetreutStudent.ArbeitViewHolder> {

    private final List<Arbeit> arbeitenListe;
    private Context context;


    public ArbeitAdapterBetreutStudent(List<Arbeit> arbeitenListe, Context context) {
        this.arbeitenListe = arbeitenListe;
        this.context = context;

    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_betreut_student, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewArbeitZustand.setText(arbeit.getZustand());

        holder.itemView.setOnClickListener(v -> {
            fetchTutorAndOpenDialog(arbeit);
        });
    }

    @Override
    public int getItemCount() {
        return arbeitenListe.size();
    }

    static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewArbeitName, textViewArbeitZustand;

        ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitNameBetreutStudent);
            textViewArbeitZustand = itemView.findViewById(R.id.textViewArbeitZustandBetreutStudent);
        }
    }
    private void fetchTutorAndOpenDialog(Arbeit arbeit) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String betreuerUid = arbeit.getBetreuerUid();

        firestore.collection("user").document(betreuerUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tutorName = documentSnapshot.getString("name");
                        String tutorEmail = documentSnapshot.getString("email");

                        // Öffne das DialogFragment mit den geholten Daten
                        UploadDialogFragment dialogFragment = new UploadDialogFragment(
                                tutorName,
                                tutorEmail,
                                arbeit.getNameDerArbeit(),
                                arbeit.getBeschreibung(),
                                arbeit.getArbeitUid());

                        dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "thesisDetails");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Fehler beim Laden der Betreuerdaten.", Toast.LENGTH_SHORT).show();
                });
    }



}
