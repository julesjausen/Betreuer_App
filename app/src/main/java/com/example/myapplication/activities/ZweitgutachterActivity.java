package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ArbeitAdapterZweitgutachter;
import com.example.myapplication.fragments.ProfileDialogFragment;
import com.example.myapplication.models.Arbeit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ZweitgutachterActivity extends AppCompatActivity {
    //Für Zweitgutachter, zeigt die Liste der Arbeiten, für die der Zweitgutachter vorgesehen ist.

    private RecyclerView recyclerView;
    private ArbeitAdapterZweitgutachter adapter;
    private List<Arbeit> arbeitenListe;
    private FirebaseFirestore firestore;
    private String currentZweitgutachterUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zweitgutachter);

        firestore = FirebaseFirestore.getInstance();
        currentZweitgutachterUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclerViewZweitgutachter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        arbeitenListe = new ArrayList<>();
        adapter = new ArbeitAdapterZweitgutachter(arbeitenListe);
        recyclerView.setAdapter(adapter); //der adapter der recycler view

        Toolbar toolbar = findViewById(R.id.toolbarZweitgutachter);
        setSupportActionBar(toolbar);

        loadArbeiten();
    }
    //Arbeiten werden geladen
    private void loadArbeiten() {
        firestore.collection("thesis")
                .whereEqualTo("zweitgutachterUid", currentZweitgutachterUid)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Fehlerbehandlung
                        return;
                    }
                    arbeitenListe.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Arbeit arbeit = doc.toObject(Arbeit.class);
                            arbeitenListe.add(arbeit);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    //Menü wird inflated
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Die Optionen des Menüs werden beschrieben und die Funktionen hinterlegt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Implementieren Sie hier die Logik für Logout
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ZweitgutachterActivity.this, DeciderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_profile) {

            openProfileDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Hintergrundfarbe Menü
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(menu.getItem(i).getTitle());
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            item.setTitle(s);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    //dialogfenster zum anpassen des profils
    private void openProfileDialog() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfileDialogFragment profileDialog = new ProfileDialogFragment(userId);
        profileDialog.show(getSupportFragmentManager(), "ProfileDialogFragment");
    }

}
