package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextViewDialogActivity extends AppCompatActivity {
    public static final String titleName = "TITLE";
    public static final String textName = "TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_dialog);

        Intent intent = getIntent();

        String title = intent.getStringExtra(titleName);
        String text = intent.getStringExtra(textName);


        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        TextView textTextView = findViewById(R.id.textTextView);
        textTextView.setText(text);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent resultintent = new Intent();
            setResult(RESULT_CANCELED, resultintent);
            finish();
        });

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            Intent resultintent = new Intent();
            setResult(RESULT_OK, resultintent);
            finish();
        });
    }
}