package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BetreuteArbeitenAdapter extends RecyclerView.Adapter<BetreuteArbeitenAdapter.ArbeitViewHolder> {

    private List<Arbeit> arbeitenListe;

    public BetreuteArbeitenAdapter(List<Arbeit> arbeitenListe) {
        this.arbeitenListe = arbeitenListe;
    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_betreuer_betreut, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewStatus.setText(arbeit.getZustand()); // Hier setzen wir den Status statt des Fachs

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
        return arbeitenListe.size();
    }

    public static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewArbeitName;
        public TextView textViewStatus;

        public ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitNameBetreut);
            textViewStatus = itemView.findViewById(R.id.textViewStatusBetreut);
        }
    }
}
