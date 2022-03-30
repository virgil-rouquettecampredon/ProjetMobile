package com.example.projetmobile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SetProfilePictureDialogActivity extends AppCompatActivity {
    public static final String resultName = "RESULT";

    private Uri result;
    private ImageView previewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_picture_dialog);


        previewImageView = findViewById(R.id.previewImageView);
        //TODO mettre la vraie image de profil (BDD ?)
        ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                resultURI -> {
                    if (resultURI != null) {
                        result = resultURI;
                        previewImageView.setImageURI(result);
                    }
                });

        ImageButton setFileButton = findViewById(R.id.setFileButton);
        setFileButton.setOnClickListener(v -> {
            getImage.launch("image/*");
        });

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
            if (result != null) {
                resultintent.putExtra(resultName, result.toString());
            }
            else {
                resultintent.putExtra(resultName, "");
            }
            finish();
        });
    }
}