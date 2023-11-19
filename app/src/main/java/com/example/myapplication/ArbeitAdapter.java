package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArbeitAdapter extends RecyclerView.Adapter<ArbeitAdapter.ArbeitViewHolder> {

    private List<Arbeit> arbeitenListe;

    public ArbeitAdapter(List<Arbeit> arbeitenListe) {
        this.arbeitenListe = arbeitenListe;
    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_betreuer, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewFachName.setText(arbeit.getZustand());

        // Setzen Sie hier andere Eigenschaften der Arbeit
    }

    @Override
    public int getItemCount() {
        return arbeitenListe.size();
    }

    public static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewArbeitName;
        public TextView textViewFachName;

        public ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitName);
            textViewFachName = itemView.findViewById(R.id.textViewFachName);

            // Initialisieren Sie hier andere Views des Arbeit-Items
        }
    }
}
