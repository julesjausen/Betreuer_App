package com.example.myapplication.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.ModifyWorkActivity;
import com.example.myapplication.models.Arbeit;

import java.util.List;

public class ArbeitAdapterZweitgutachter extends RecyclerView.Adapter<ArbeitAdapterZweitgutachter.ArbeitViewHolder> {

    private List<Arbeit> arbeitenListe;

    public ArbeitAdapterZweitgutachter(List<Arbeit> arbeitenListe) {
        this.arbeitenListe = arbeitenListe;
    }

    @NonNull
    @Override
    public ArbeitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arbeit_item_zweitgutachter, parent, false);
        return new ArbeitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbeitViewHolder holder, int position) {
        Arbeit arbeit = arbeitenListe.get(position);
        holder.textViewArbeitName.setText(arbeit.getNameDerArbeit());
        holder.textViewFachName.setText(arbeit.getStudienfach());
        // Implementieren Sie hier die Logik für das Anklicken eines Items
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), ModifyWorkActivity.class);
            intent.putExtra("arbeitUid", arbeit.getArbeitUid());
            v.getContext().startActivity(intent);

        });



    }

    @Override
    public int getItemCount() {
        return arbeitenListe.size();
    }

    static class ArbeitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewArbeitName, textViewFachName;

        ArbeitViewHolder(View itemView) {
            super(itemView);
            textViewArbeitName = itemView.findViewById(R.id.textViewArbeitNameZweitgutachter);
            textViewFachName = itemView.findViewById(R.id.textViewFachNameZweitgutachter);
        }
    }
}

