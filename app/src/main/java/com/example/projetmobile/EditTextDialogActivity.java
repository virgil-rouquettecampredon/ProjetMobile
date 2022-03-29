package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextDialogActivity extends AppCompatActivity {
    public static final String titleName = "TITLE";
    public static final String editTextPrefillName = "EDITTEXTPREFIL";
    public static final String resultName = "RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_dialog);

        Intent intent = getIntent();

        String title = intent.getStringExtra(titleName);
        String editTextPrefill = intent.getStringExtra(editTextPrefillName);


        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        EditText editPseudo = findViewById(R.id.editTextTextPseudo);
        editPseudo.setText(editTextPrefill);

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
            resultintent.putExtra(resultName, result);
            setResult(RESULT_OK, resultintent);
            finish();
        });
    }
}