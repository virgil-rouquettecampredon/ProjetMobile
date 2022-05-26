package com.example.projetmobile;

import static java.lang.Integer.parseInt;

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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.ChangePieceScreen;
import com.example.projetmobile.Model.GameManager;
import com.example.projetmobile.Model.GameManagerOnline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameOnlineActivity extends AppCompatActivity {
    public final static String fragmentTag = "GAMEFRAGMENT";
    ActivityResultLauncher<Intent> animationActivityLauncher;

    private static boolean isManagerComputedYet = false;

    private GameFragment gameFragment;
    private static GameManagerOnline gm;
    private String gameName;

    private ActivityResultLauncher<Intent> menuBurgerLauncher;

    //https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
    ProgressBar progressBar;

    ValueEventListener wait2PlayerListener;

    //https://stackoverflow.com/questions/6413700/android-proper-way-to-use-onbackpressed-with-toast
    private long backPressedTime = 0;
    private long backPressedTimeResetDelay = 2000;

    FirebaseDatabase database;
    DatabaseReference roomRef;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private void OnMenuBurgerGameModeSelect(String gameMode) {
        Toast.makeText(this, gameMode, Toast.LENGTH_SHORT).show();
    }

    private void OnMenuBurgerTryAgain() {
        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
    }

    private void OnMenuBurgerQuit() {
        Toast.makeText(this, "Quit", Toast.LENGTH_SHORT).show();
        gm.onFFGame();
        //onDestroy();
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

        //look rotation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ImageView imageView = findViewById(R.id.menuBurgerToggle);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(GameOnlineActivity.this, MenuBurgerActivity.class);
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
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Lunch the game
        System.out.println("=============> LUNCH THE GAME");

        //Board b = (Board) findViewById(R.id.board_game);
        Board b = gameFragment.getFrag_board().getBoard();
        ChangePieceScreen wrapper = gameFragment.getFrag_board().getScreenGameWrapper();
        b.setOnScreenView(wrapper);

        if(b!=null) {

            Intent intent = this.getIntent();

            gameName = intent.getStringExtra("gameName");
            int player = parseInt(intent.getStringExtra("player"));

            Log.d("GameOnlineActivity", "gameName: " + gameName);
            Log.d("GameOnlineActivity", "player: " + player);

            database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
            roomRef = database.getReference("rooms").child(gameName);

            gm = new GameManagerOnline(getBaseContext(), b);

            System.out.println("FR Player 1 pseudo : "  + gameFragment.getFrag_p1().getTVPseudo());
            System.out.println("FR Player 1 dead : "    + gameFragment.getFrag_p1().getLLDeadPieces());
            System.out.println("FR Player 2 pseudo : "  + gameFragment.getFrag_p2().getTVPseudo());
            System.out.println("FR Player 2 dead : "    + gameFragment.getFrag_p2().getLLDeadPieces());

            gm.addPlayerInterfaceElement(gameFragment.getFrag_p1(), gameFragment.getFrameLayout_p1());
            gm.addPlayerInterfaceElement(gameFragment.getFrag_p2(), gameFragment.getFrameLayout_p2());
            gm.setNameRoomRef(gameName);
            gm.setPlayerIndex(player);

            if (player == 0) {
                System.out.println("J attends le second joueur");
                gm.setPlayer1(gameName);
                wait2Player();
            }
            else {
                gm.setPlayer1(gameName);
                gm.setPlayer2(user.getUid());
                gm.start();
            }
        }
    }

    private void wait2Player() {
        wait2PlayerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().equals("")){
                    System.out.println("=============> ONDATA CHANGE");
                    gm.setPlayer2(dataSnapshot.getValue().toString());
                    gm.start();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Nothing to do
            }

        };
        roomRef.child("player2").removeEventListener(wait2PlayerListener);
        roomRef.child("player2").addValueEventListener(wait2PlayerListener);

        progressBar = new ProgressBar(GameOnlineActivity.this);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(GameManager.getAttributeColor(GameOnlineActivity.this, R.attr.colorTertiaryVariant)));
        LinearLayout layout = gameFragment.getFrag_p2().getView().findViewById(R.id.dead_pieces);
        layout.addView(progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomRef.child("player2").removeEventListener(wait2PlayerListener);
        if (gm.getPlayer2() == null || gm.getPlayer2().isEmpty()) {
            deleteRoomsInformation();
        }
    }
    @Override
    public void onBackPressed() {
        long time = System.currentTimeMillis();
        if (time - backPressedTime > backPressedTimeResetDelay) {
            backPressedTime = time;
            Toast.makeText(this, getString(R.string.confirm_back_press), Toast.LENGTH_SHORT).show();
        } else {
            gm.onFFGame();
            onDestroy();
            super.onBackPressed();
        }
    }

    public void deleteRoomsInformation(){
        database.getReference().child("rooms").child(gameName).removeValue();
    }
}