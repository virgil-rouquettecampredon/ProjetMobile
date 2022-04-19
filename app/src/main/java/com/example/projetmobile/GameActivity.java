package com.example.projetmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.ChangePieceScreen;
import com.example.projetmobile.Model.GameManager;

public class GameActivity extends AppCompatActivity {
    public final static String fragmentTag = "GAMEFRAGMENT";

    private GameFragment gameFragment;
    private static GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_background);

        ImageView imageView = findViewById(R.id.menuBurgerToggle);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MenuBurgerActivity.class);
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
}