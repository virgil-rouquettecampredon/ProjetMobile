package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MenuBurgerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_burger);

        MaterialButton gameModeButton = findViewById(R.id.gameModeButton);
        gameModeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        MaterialButton tryAgainButton = findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(v -> {
            Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });
    }
}