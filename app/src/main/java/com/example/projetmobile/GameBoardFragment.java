package com.example.projetmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.GameManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameBoardFragment extends Fragment {
    private GameManager gm;

    public GameBoardFragment() {}

    public static GameBoardFragment newInstance() {
        GameBoardFragment fragment = new GameBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_board, container, false);
        return v;
    }
}