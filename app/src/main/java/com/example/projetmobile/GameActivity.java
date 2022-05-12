package com.example.projetmobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.ChangePieceScreen;
import com.example.projetmobile.Model.GameManager;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    public final static String fragmentTag = "GAMEFRAGMENT";

    private GameFragment gameFragment;
    private static GameManager gm;

    private ActivityResultLauncher<Intent> menuBurgerLauncher;

    //Detection de la secousse
    private final double jerkRequired = 100.0;
    private SensorManager sensorManager;
    private Sensor accelerometter;
    private TriggerEventListener triggerEventListener;
    private float[] previousAcceleration;


    private void OnMenuBurgerGameModeSelect(String gameMode) {
        Toast.makeText(this, gameMode, Toast.LENGTH_SHORT).show();
    }

    private void OnMenuBurgerTryAgain() {
        restartGame();
    }

    private void OnMenuBurgerQuit() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_background);

        menuBurgerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String resultType = data.getStringExtra(MenuBurgerActivity.resultTypeName);
                        switch (resultType) {
                            case MenuBurgerActivity.resultTypeGameMode:
                                OnMenuBurgerGameModeSelect(data.getStringExtra(MenuBurgerActivity.gameModeName));
                                break;
                            case MenuBurgerActivity.resultTypeTryAgain:
                                OnMenuBurgerTryAgain();
                                break;
                            case MenuBurgerActivity.resultTypeQuit:
                                OnMenuBurgerQuit();
                                break;
                        }
                    }
                });

        ImageView imageView = findViewById(R.id.menuBurgerToggle);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MenuBurgerActivity.class);
            menuBurgerLauncher.launch(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this));
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        gameFragment = (GameFragment) fm.findFragmentByTag(fragmentTag);

        if (gameFragment == null) {
            gameFragment = GameFragment.newInstance();
            transaction.add(R.id.fragment_container, gameFragment, fragmentTag);
            transaction.commit();
        }


        //look rotation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometter = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometter == null) {
            Toast.makeText(this, "Pas d'accelerometre :(", Toast.LENGTH_SHORT).show();
        }

        previousAcceleration = new float[3];
        previousAcceleration[0] = 0;
        previousAcceleration[1] = 0;
        previousAcceleration[2] = 0;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Lunch the game
        System.out.println("=============> LUNCH THE GAME");

        createGame();
    }


    private void createGame() {
        //Board b = (Board) findViewById(R.id.board_game);
        Board b = gameFragment.getFrag_board().getBoard();
        ChangePieceScreen wrapper = gameFragment.getFrag_board().getScreenGameWrapper();
        b.setOnScreenView(wrapper);

        if(b!=null) {
            b.redrawBoard();

            gm = new GameManager(getBaseContext(), b);

            System.out.println("FR Player 1 pseudo : "  + gameFragment.getFrag_p1().getTVPseudo());
            System.out.println("FR Player 1 dead : "    + gameFragment.getFrag_p1().getLLDeadPieces());
            System.out.println("FR Player 2 pseudo : "  + gameFragment.getFrag_p2().getTVPseudo());
            System.out.println("FR Player 2 dead : "    + gameFragment.getFrag_p2().getLLDeadPieces());

            gm.addPlayerInterfaceElement(gameFragment.getFrag_p1(), gameFragment.getFrameLayout_p1());
            gm.addPlayerInterfaceElement(gameFragment.getFrag_p2(), gameFragment.getFrameLayout_p2());

            gm.start();
        }
    }

    private void restartGame() {
        createGame();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x, y, z;
        x = sensorEvent.values[0] - previousAcceleration[0];
        y = sensorEvent.values[1] - previousAcceleration[1];
        z = sensorEvent.values[2] - previousAcceleration[2];
        double length = Math.sqrt(x*x+y*y+z*z);

        boolean greatJerk = length > jerkRequired;
        if (greatJerk) {
            restartGame();
        }

        previousAcceleration[0] = sensorEvent.values[0];
        previousAcceleration[1] = sensorEvent.values[1];
        previousAcceleration[2] = sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(GameActivity.this, accelerometter, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}