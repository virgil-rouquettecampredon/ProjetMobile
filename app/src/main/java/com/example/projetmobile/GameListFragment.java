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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GameListFragment extends Fragment {

    public class GameData {
        private long ranking;
        private String gameName;
        private String gameMode;
        private String player1;
        private String piece1;
        private String piece2;

        public GameData() {
        }

        public GameData(long ranking, String gameName, String gameMode, String player1) {
            this.ranking = ranking;
            this.gameName = gameName;
            this.gameMode = gameMode;
            this.player1 = player1;
            piece1 = "";
            piece2 = "";

        }

        public String getPiece1() {
            return piece1;
        }

        public String getPiece2() {
            return piece2;
        }

        public long getRanking() {
            return ranking;
        }

        public String getPlayer1(){ return player1;}

        public String getGameName() {
            return gameName;
        }

        public String getGameMode() {
            return gameMode;
        }

        public void setRanking(long ranking) {
            this.ranking = ranking;
        }

        public void setPlayer1(String player1) {
            this.player1 = player1;
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

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference mDatabase;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long elo;

    ValueEventListener roomListener;


    private void fetch(ArrayList<GameData> roomsList) {
        //TODO BDD
        //TODO Bonus : animation faire tourner l'iconne de syncronisation
        //GAME DATA RECUP INFO BDD TO FILL THE LIST
        data.addAll(roomsList);
    }

    private void populate() {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        System.out.println(data.size());
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
        //fetch();
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
        //List of game modes available
        //For the moment, only one game mode that is Normal
        gameModes = new ArrayList<>();
        gameModes.add("Normal");
        gameModes.add("Fog of war");
        gameModes.add("Chess à 4");
        gameModes.add("King of the hill");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Moment où on choisi le mode de jeu
        //When pressed OK
        setGameModeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentData = result.getData();
                        int resultId = intentData.getIntExtra(EditTextDialogActivity.resultName, 0);
                        inCreationGameData.setGameMode(gameModes.get(resultId));
                        inCreationGameData.setRanking(elo);
                        inCreationGameData.setPlayer1(user.getUid());

                        //TODO BDD Créer la partie (ajouter à la liste des parties)
                        roomRef.child(user.getUid()).setValue(inCreationGameData);
                        roomRef.child(user.getUid()).child("player2").setValue("");
                        roomRef.child(user.getUid()).child("turn").setValue(1);
                        //TODO Lancer la salle de jeu en attente
                        Intent intent = new Intent(getActivity(), GameOnlineActivity.class);
                        intent.putExtra("gameName", user.getUid());
                        intent.putExtra("player", "0");
                        roomRef.removeEventListener(roomListener);
                        startActivity(intent);
                        //TODO donc supprimer ça
                        /*data.add(inCreationGameData);
                        clearGameDataFragment();
                        populate();*/
                    }
                });

        //Moment où on insère le nom de la room
        //When pressed OK
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

        /**DATABASE Information**/
        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
        roomRef = database.getReference("rooms");
        mDatabase = database.getReference();
        String keyId = user.getUid();

        mDatabase.child("users").child(keyId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    elo = task.getResult().getValue(User.class).getElo();
                }
            }
        });

        data = new ArrayList<>();
        gameModes = new ArrayList<>();
        /**END DATABASE Information**/

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
        syncButton.setOnClickListener(v -> updateList());

        //update();
        updateList();
        populateGameMode();


        return view;
    }


    public void updateList(){
        clearGameDataFragment();
        updateRoomsAvailable();
        populate();
    }

    public void updateRoomsAvailable(){
        roomListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                ArrayList<GameData> roomsList = new ArrayList<>();
                for (DataSnapshot snapshot : children) {
                    Object value = snapshot.child("player2").getValue();
                    if (value != null && value.equals("")) {
                        roomsList.add(new GameData((long) snapshot.child("ranking").getValue(), (String) snapshot.child("gameName").getValue(), (String) snapshot.child("gameMode").getValue(), (String) snapshot.child("player1").getValue()));
                    }
                }

                fetch(roomsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Nothing to do
            }

        };
        roomRef.removeEventListener(roomListener);
        roomRef.addValueEventListener(roomListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roomRef.removeEventListener(roomListener);
    }
}