package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameDataForListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private int ranking;
    private String gameName;
    private String gameMode;
    private String player1;

    private DatabaseReference roomRef;
    private FirebaseDatabase database;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public GameDataForListFragment() {
        // Required empty public constructor
    }

    public static GameDataForListFragment newInstance(GameListFragment.GameData gameData) {
        return newInstance((int) gameData.getRanking(), gameData.getGameName(), gameData.getGameMode(), gameData.getPlayer1());
    }

    public static GameDataForListFragment newInstance(int param1, String param2, String param3, String param4) {
        GameDataForListFragment fragment = new GameDataForListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
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
            player1 = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_data_for_list, container, false);

        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");

        TextView rankingTextView = view.findViewById(R.id.rankingTextView);
        String rankingString = "" + ranking;
        rankingTextView.setText(rankingString);

        TextView gameNameTextView = view.findViewById(R.id.gameNameTextView);
        gameNameTextView.setText(gameName);

        TextView gameModeTextView = view.findViewById(R.id.gameModeTextView);
        gameModeTextView.setText(gameMode);

        LinearLayout topLinearLayout = view.findViewById(R.id.topLinearLayout);
        topLinearLayout.setOnClickListener(v -> {
            //TODO Join game
            //JOIN THE GAME WHEN CLICKED
            //ENTER THE INFORMATION INTO DATABASE
            roomRef = database.getReference("rooms").child(player1).child("player2");
            roomRef.setValue(user.getUid());
            eventListener();

            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void eventListener() {
        Intent intent = new Intent(getActivity(), GameOnlineActivity.class);
        intent.putExtra("gameName", player1);
        intent.putExtra("player", "1");
        startActivity(intent);
    }
}