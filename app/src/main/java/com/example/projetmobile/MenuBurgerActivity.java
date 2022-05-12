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
    public static final String resultTypeGameMode = "GAME_MODE";
    public static final String resultTypeTryAgain = "TRY_AGAIN";
    public static final String resultTypeQuit = "QUIT";
    public static final String resultTypeName = "RESULT_TYPE";
    public static final String gameModeName = "GAME_MODE";

    private ActivityResultLauncher<Intent> selectorLauncher;
    private ActivityResultLauncher<Intent> tryAgainWarningLauncher;
    private ActivityResultLauncher<Intent> quitWarningLauncher;
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
                    Intent data = result.getData();
                    int resultID = data.getIntExtra(EditTextDialogActivity.resultName, 0);
                    String resultMode = gameModes.get(resultID);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(resultTypeName, resultTypeGameMode);
                    resultIntent.putExtra(gameModeName, resultMode);
                    setResult(RESULT_OK, resultIntent);
                    supportFinishAfterTransition();
                }
            });

        tryAgainWarningLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(resultTypeName, resultTypeTryAgain);
                    setResult(RESULT_OK, resultIntent);
                    supportFinishAfterTransition();
                }
            });

        quitWarningLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(resultTypeName, resultTypeQuit);
                    setResult(RESULT_OK, resultIntent);
                    supportFinishAfterTransition();
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
            intent.putExtra(TextViewDialogActivity.titleName, getString(R.string.try_again));
            intent.putExtra(TextViewDialogActivity.textName, getString(R.string.try_again_warning));
            tryAgainWarningLauncher.launch(intent);
        });

        Button quitButton = findViewById(R.id.quitButton);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuBurgerActivity.this, TextViewDialogActivity.class);
            intent.putExtra(TextViewDialogActivity.titleName, getString(R.string.quit));
            intent.putExtra(TextViewDialogActivity.textName, getString(R.string.quit_warning));
            quitWarningLauncher.launch(intent);
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            supportFinishAfterTransition();
        });

        View menuBurgerToggle = findViewById(R.id.menuBurgerToggle);
        menuBurgerToggle.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            supportFinishAfterTransition();
        });
    }
}