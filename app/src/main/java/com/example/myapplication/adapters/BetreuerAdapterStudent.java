package com.example.myapplication.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activities.BetreuerDetailsStudentActivity;
import com.example.myapplication.models.Betreuer; // Ihr Betreuer-Modell
import com.example.myapplication.R;

import java.util.List;
// Adapter für die Darstellung von Betreuern für Studenten in einer RecyclerView
public class BetreuerAdapterStudent extends RecyclerView.Adapter<BetreuerAdapterStudent.BetreuerViewHolder> {

    private List<Betreuer> betreuerListe;

    // Konstruktor des Adapters
    public BetreuerAdapterStudent(List<Betreuer> betreuerListe) {
        this.betreuerListe = betreuerListe;
    }

    @NonNull
    @Override
    public BetreuerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellt ein neues ViewHolder-Objekt für jedes Element in der RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_betreuer_student, parent, false);
        return new BetreuerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BetreuerViewHolder holder, int position) {
        // Setzt die Daten eines betreuer-Objekts in die Ansicht eines ViewHolder
        Betreuer betreuer = betreuerListe.get(position);
        Log.d("BetreuerAdapter", "Betreuer Name: " + betreuer.getName());
        Log.d("BetreuerAdapter", "Betreuer UID: " + betreuer.getBetreuerUid());

        holder.textViewNameBetreuerStudent.setText(betreuer.getName());
        holder.textViewFachBetreuerStudent.setText(betreuer.getFach());

        // Event-Listener für Klicks auf jedes Element der RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BetreuerDetailsStudentActivity.class);
                intent.putExtra("betreuerUid", betreuer.getBetreuerUid()); // Betreuer UID als Extra übergeben
                Log.d("TAG", "onClick: betreuer id"+ betreuer.getBetreuerUid());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Gibt die Anzahl der Elemente in der Liste zurück
        return betreuerListe.size();
    }

    // ViewHolder-Klasse für die Darstellung eines einzelnen Elements in der RecyclerView
    public static class BetreuerViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameBetreuerStudent;
        public TextView textViewFachBetreuerStudent;

        public BetreuerViewHolder(View itemView) {
            super(itemView);
            textViewNameBetreuerStudent = itemView.findViewById(R.id.name_betreuer_student);
            textViewFachBetreuerStudent = itemView.findViewById(R.id.name_fach_betreuer_student);

        }
    }
}
