package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeciderActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendUserToLogin();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            Intent intent;
                            switch (role) {
                                case "Betreuer":
                                    intent = new Intent(DeciderActivity.this, BetreuerActivity.class);
                                    break;
                                case "Zweitgutachter":
                                    intent = new Intent(DeciderActivity.this, BetreuerActivity.class);
                                    break;
                                case "Student":
                                    intent = new Intent(DeciderActivity.this, StudentActivity.class);
                                    break;
                                default:
                                    intent = new Intent(DeciderActivity.this, LoginActivity.class);
                                    break;
                            }
                            startActivity(intent);
                            finish(); // Beendet die LoginActivity
                        } else {
                            // Dokument existiert nicht, leiten Sie den Benutzer zur LoginActivity um
                            Intent intent = new Intent(DeciderActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                    }
                }
            });
        }



    }

    private void sendUserToLogin() {
        Intent intent = new Intent(DeciderActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}