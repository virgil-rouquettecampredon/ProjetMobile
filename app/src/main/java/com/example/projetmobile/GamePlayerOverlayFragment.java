package com.example.projetmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class GamePlayerOverlayFragment extends Fragment {
    private LinearLayout LLDeadPieces;
    private TextView TVPseudo;
    private ImageView imgPlayer;

    public GamePlayerOverlayFragment() {}


    public static GamePlayerOverlayFragment newInstance() {
        GamePlayerOverlayFragment fragment = new GamePlayerOverlayFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_player_overlay, container, false);
        LLDeadPieces = v.findViewById(R.id.dead_pieces);
        TVPseudo = v.findViewById(R.id.textViewPseudo);
        imgPlayer = v.findViewById(R.id.imageViewAvatar);
        return v;
    }

    public LinearLayout getLLDeadPieces() {
        return LLDeadPieces;
    }

    public TextView getTVPseudo() {
        return TVPseudo;
    }

    public ImageView getImgPlayer(){return imgPlayer;}
}