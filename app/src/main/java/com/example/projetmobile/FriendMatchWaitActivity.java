package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class FriendMatchWaitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_match_wait);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent resultintent = new Intent();
            setResult(RESULT_CANCELED, resultintent);
            finish();
        });

        //TODO attendre réponse serveur et lancer la partie
    }
}