package com.example.projetmobile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projetmobile.Model.GameManager;

public class GameFragment extends Fragment {
    //Fragments
    private GamePlayerOverlayFragment frag_p1;
    private GamePlayerOverlayFragment frag_p2;
    private GameBoardFragment frag_board;

    private GameManager gm;

    private final static String frgTag_player1 = "PLAYERFRAGMENT1";
    private final static String frgTag_player2 = "PLAYERFRAGMENT2";
    private final static String frgTag_gameboard = "BOARDFRAGMENT";

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        System.out.println("=============> TEST BOARD ONCREATE");

        FragmentManager fm = getParentFragmentManager();
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

        return view;
    }

    public GamePlayerOverlayFragment getFrag_p1() {
        return frag_p1;
    }

    public GamePlayerOverlayFragment getFrag_p2() {
        return frag_p2;
    }

    public GameBoardFragment getFrag_board() {
        return frag_board;
    }

    public GameManager getGm() {
        return gm;
    }

    public void setGm(GameManager gm) {
        this.gm = gm;
    }
}