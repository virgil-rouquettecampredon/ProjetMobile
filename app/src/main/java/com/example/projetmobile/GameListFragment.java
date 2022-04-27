package com.example.projetmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class GameListFragment extends Fragment {

    public class GameData {
        private int ranking;
        private String gameName;
        private String gameMode;

        public GameData() {
        }

        public GameData(int ranking, String gameName, String gameMode) {
            this.ranking = ranking;
            this.gameName = gameName;
            this.gameMode = gameMode;
        }

        public int getRanking() {
            return ranking;
        }

        public String getGameName() {
            return gameName;
        }

        public String getGameMode() {
            return gameMode;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public void setGameMode(String gameMode) {
            this.gameMode = gameMode;
        }
    }

    private final static String fragmentTag = "GAMEDATAFORLISTFRAGMENT";

    ActivityResultLauncher<Intent> setGameNameLauncher;
    ActivityResultLauncher<Intent> setGameModeLauncher;

    private ArrayList<GameData> data;
    private GameData inCreationGameData;

    private ArrayList<String> gameModes;

    private LinearLayout gameDescriptionReceiver;

    private void fetch() {
        //TODO BDD
        //TODO Bonus : animation faire tourner l'iconne de syncronisation
        int nbGames = (int)(Math.random()*9 + 1);
        for (int i = 0; i < nbGames; i++) {
            data.add(new GameData((int)(Math.random()*1000+1000), getString(R.string.default_game_name), getString(R.string.default_game_mode)));
        }
    }

    private void populate() {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (GameData d : data) {
            GameDataForListFragment gData = GameDataForListFragment.newInstance(d);
            transaction.add(R.id.gameDescriptionReceiver, gData, fragmentTag);
        }
        transaction.commit();
    }

    private void clearGameDataFragment() {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (Fragment fragment : fm.getFragments()) {
            if (fragment.getTag() != null && fragment.getTag().equals(fragmentTag)) {
                transaction.remove(fragment);
            }
        }
        transaction.commit();
    }

    public void update() {
        data.clear();
        clearGameDataFragment();
        fetch();
        populate();
    }


    public GameListFragment() {
        // Required empty public constructor
    }

    public static GameListFragment newInstance() {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void populateGameMode() {
        gameModes = new ArrayList<>();
        gameModes.add("Normal");
        gameModes.add("Fog of war");
        gameModes.add("Chess à 4");
        gameModes.add("King of the hill");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new ArrayList<>();
        gameModes = new ArrayList<>();
        populateGameMode();

        setGameModeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentData = result.getData();
                        int resultId = intentData.getIntExtra(EditTextDialogActivity.resultName, 0);
                        inCreationGameData.setGameMode(gameModes.get(resultId));


                        //TODO BDD Créer la partie (ajouter à la liste des parties)
                        inCreationGameData.setRanking((int)(Math.random()*1000+1000));
                        //TODO Lancer la salle de jeu en attente

                        //TODO donc supprimer ça
                        data.add(inCreationGameData);
                        clearGameDataFragment();
                        populate();
                    }
                });

        setGameNameLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String resultString = data.getStringExtra(EditTextDialogActivity.resultName);

                        inCreationGameData.setGameName(resultString);

                        Intent intent = new Intent(getActivity(), SelectorDialogActivity.class);
                        intent.putExtra(SelectorDialogActivity.titleName, getString(R.string.game_mode));
                        intent.putExtra(SelectorDialogActivity.choicesName, gameModes);
                        intent.putExtra(SelectorDialogActivity.checkedIdName, 0);
                        setGameModeLauncher.launch(intent);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        gameDescriptionReceiver = view.findViewById(R.id.gameDescriptionReceiver);

        ImageButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            inCreationGameData = new GameData();
            Intent intent = new Intent(getActivity(), EditTextDialogActivity.class);
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, getString(R.string.default_game_name));
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.game_name));
            setGameNameLauncher.launch(intent);
        });

        ImageButton syncButton = view.findViewById(R.id.syncButton);
        syncButton.setOnClickListener(v -> update());

        update();


        return view;
    }
}