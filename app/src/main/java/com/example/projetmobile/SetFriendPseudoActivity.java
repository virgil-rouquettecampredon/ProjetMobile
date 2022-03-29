package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SetFriendPseudoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_friend_pseudo);

        Intent intent = getIntent();

        String previousPseudo = intent.getStringExtra("currentText");

        EditText editPseudo = findViewById(R.id.editTextTextPseudo);
        editPseudo.setText(previousPseudo);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent resultintent = new Intent();
            setResult(RESULT_CANCELED, resultintent);
            finish();
        });

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            String result = editPseudo.getText().toString();
            Intent resultintent = new Intent();
            resultintent.putExtra("result", result);
            setResult(RESULT_OK, resultintent);
            finish();
        });
    }
}