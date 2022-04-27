package com.example.projetmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameDataForListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private int ranking;
    private String gameName;
    private String gameMode;

    public GameDataForListFragment() {
        // Required empty public constructor
    }

    public static GameDataForListFragment newInstance(GameListFragment.GameData gameData) {
        return newInstance(gameData.getRanking(), gameData.getGameName(), gameData.getGameMode());
    }

    public static GameDataForListFragment newInstance(int param1, String param2, String param3) {
        GameDataForListFragment fragment = new GameDataForListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ranking = getArguments().getInt(ARG_PARAM1);
            gameName = getArguments().getString(ARG_PARAM2);
            gameMode = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_data_for_list, container, false);

        TextView rankingTextView = view.findViewById(R.id.rankingTextView);
        String rankingString = ""+ranking;
        rankingTextView.setText(rankingString);

        TextView gameNameTextView = view.findViewById(R.id.gameNameTextView);
        gameNameTextView.setText(gameName);

        TextView gameModeTextView = view.findViewById(R.id.gameModeTextView);
        gameModeTextView.setText(gameMode);

        LinearLayout topLinearLayout = view.findViewById(R.id.topLinearLayout);
        topLinearLayout.setOnClickListener(v -> {
            //TODO Join game
            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}