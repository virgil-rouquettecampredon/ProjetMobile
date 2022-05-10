package com.example.projetmobile;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private GameFragment gameFragment;
    private static GameManagerOnline gm;

    FirebaseDatabase database;
    DatabaseReference roomRef;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_background);

        ImageView imageView = findViewById(R.id.menuBurgerToggle);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(GameOnlineActivity.this, MenuBurgerActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

            String gameName = intent.getStringExtra("gameName");
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
        roomRef.child("player2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().equals("")){
                    System.out.println("=============> ONDATA CHANGE");
                    gm.setPlayer2(dataSnapshot.getValue().toString());
                    gm.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Nothing to do
            }

        });
    }
}