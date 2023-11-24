package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Arbeit;
import com.example.myapplication.R;

import java.util.List;

public class ArbeitAdapterBetreutStudent extends RecyclerView.Adapter<ArbeitAdapterBetreutStudent.ArbeitViewHolder> {

    private final List<Arbeit> arbeitenListe;

    public ArbeitAdapterBetreutStudent(List<Arbeit> arbeitenListe) {
        this.arbeitenListe = arbeitenListe;
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
            // Implementieren Sie Ihre Logik beim Klicken auf ein Item
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
}
