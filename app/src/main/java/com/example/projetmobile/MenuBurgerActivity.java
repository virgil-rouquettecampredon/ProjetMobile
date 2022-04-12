package com.example.projetmobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MenuBurgerActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> selectorLauncher;
    private ActivityResultLauncher<Intent> tryAgainWarningLauncher;
    private ArrayList<String> gameModes;

    private void populateGameMode() {
        gameModes = new ArrayList<>();
        gameModes.add("Normal");
        gameModes.add("Fog of war");
        gameModes.add("Chess à 4");
        gameModes.add("King of the hill");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_burger);

        populateGameMode();

        selectorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //TODO Changer le mode de jeu
                    Intent data = result.getData();
                    int resultId = data.getIntExtra(EditTextDialogActivity.resultName, 0);
                    Toast.makeText(this, gameModes.get(resultId), Toast.LENGTH_SHORT).show();
                }
            });

        tryAgainWarningLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
                }
            });

        Button gameModeButton = findViewById(R.id.gameModeButton);
        gameModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuBurgerActivity.this, SelectorDialogActivity.class);
            intent.putExtra(SelectorDialogActivity.titleName, getString(R.string.game_mode));
            intent.putExtra(SelectorDialogActivity.choicesName, gameModes);
            intent.putExtra(SelectorDialogActivity.checkedIdName, 0);
            selectorLauncher.launch(intent);
        });

        Button tryAgainButton = findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuBurgerActivity.this, TextViewDialogActivity.class);
            intent.putExtra(TextViewDialogActivity.titleName, getString(R.string.warning));
            intent.putExtra(TextViewDialogActivity.textName, getString(R.string.try_again_warning));
            tryAgainWarningLauncher.launch(intent);
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });

        View menuBurgerToggle = findViewById(R.id.menuBurgerToggle);
        menuBurgerToggle.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });
    }
}