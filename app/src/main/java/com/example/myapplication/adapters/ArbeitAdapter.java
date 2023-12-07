package com.example.myapplication.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;
import com.example.myapplication.activities.AddWorkActivity;

import java.util.List;

public class ArbeitAdapter extends RecyclerView.Adapter<ArbeitAdapter.ArbeitViewHolder> {
    // Adapter für die Darstellung von Arbeit-Objekten in einer RecyclerView

    private List<Arbeit> arbeitenListe;

    //Konstruktor
    public ArbeitAdapter(List<Arbeit> arbeitenListe) {
        this.arbeitenListe = arbeitenListe;
    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellt ein neues ViewHolder-Objekt für jedes Element in der RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_betreuer, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        // Setzt die Daten eines Arbeit-Objekts in die Ansicht eines ViewHolder

        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewFachName.setText(arbeit.getStudienfach());

        // Event-Listener für Klicks auf jedes Element der RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddWorkActivity.class);
                intent.putExtra("arbeitUid", arbeit.getArbeitUid()); // Arbeit UID als Extra übergeben
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Gibt die Anzahl der Elemente in der Liste zurück
        return arbeitenListe.size();
    }
    // ViewHolder-Klasse für die Darstellung eines einzelnen Elements in der RecyclerView
    public static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewArbeitName;
        public TextView textViewFachName;

        public ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitNameZweitgutachter);
            textViewFachName = itemView.findViewById(R.id.textViewFachNameZweitgutachter);
        }
    }
}
