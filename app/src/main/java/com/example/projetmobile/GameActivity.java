package com.example.projetmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.GameManager;

public class GameActivity extends AppCompatActivity {
    public final static String fragmentTag = "GAMEFRAGMENT";

    private GameFragment gameFragment;

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

        Board b = (Board) findViewById(R.id.board_game);
        if(b!=null) {
            gameFragment.setGm(new GameManager(getBaseContext(), b));

            System.out.println(gameFragment.getFrag_p1().getTVPseudo());
            System.out.println(gameFragment.getFrag_p1().getLLDeadPieces());
            System.out.println(gameFragment.getFrag_p2().getTVPseudo());
            System.out.println(gameFragment.getFrag_p2().getLLDeadPieces());

            gameFragment.getGm().addPlayerInterfaceElement(gameFragment.getFrag_p1().getTVPseudo(),gameFragment.getFrag_p1().getLLDeadPieces());
            gameFragment.getGm().addPlayerInterfaceElement(gameFragment.getFrag_p2().getTVPseudo(),gameFragment.getFrag_p2().getLLDeadPieces());

            gameFragment.getGm().start();
        }
    }
}