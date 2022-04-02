package com.example.projetmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.GameManager;

public class GameActivity extends AppCompatActivity {
    //Fragments
    private GamePlayerOverlayFragment frag_p1;
    private GamePlayerOverlayFragment frag_p2;
    private GameBoardFragment frag_board;

    private GameManager gm;

    private final static String frgTag_player1 = "PLAYERFRAGMENT1";
    private final static String frgTag_player2 = "PLAYERFRAGMENT2";
    private final static String frgTag_gameboard = "BOARDFRAGMENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        System.out.println("=============> TEST BOARD ONCREATE");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        frag_board = (GameBoardFragment) fm.findFragmentByTag(frgTag_gameboard);
        if (frag_board == null) {
            frag_board = GameBoardFragment.newInstance();
            transaction.add(R.id.board_container, frag_board, frgTag_gameboard);
        }
        frag_p1 = (GamePlayerOverlayFragment) fm.findFragmentByTag(frgTag_player1);
        if (frag_p1 == null) {
            frag_p1 = GamePlayerOverlayFragment.newInstance();
            transaction.add(R.id.player_1, frag_p1, frgTag_player1);
        }

        frag_p2 = (GamePlayerOverlayFragment) fm.findFragmentByTag(frgTag_player2);
        if (frag_p2 == null) {
            frag_p2 = GamePlayerOverlayFragment.newInstance();
            transaction.add(R.id.player_2, frag_p2, frgTag_player2);
        }
        transaction.commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Lunch the game
        System.out.println("=============> LUNCH THE GAME");

        Board b = (Board) findViewById(R.id.board_game);
        if(b!=null) {
            gm = new GameManager(getBaseContext(), b);

            System.out.println(frag_p1.getTVPseudo());
            System.out.println(frag_p1.getLLDeadPieces());
            System.out.println(frag_p2.getTVPseudo());
            System.out.println(frag_p2.getLLDeadPieces());

            gm.addPlayerInterfaceElement(frag_p1.getTVPseudo(),frag_p1.getLLDeadPieces());
            gm.addPlayerInterfaceElement(frag_p2.getTVPseudo(),frag_p2.getLLDeadPieces());

            gm.start();
        }
    }
}