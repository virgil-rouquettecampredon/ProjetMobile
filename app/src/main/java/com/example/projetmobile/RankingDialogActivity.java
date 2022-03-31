package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

public class RankingDialogActivity extends AppCompatActivity {

    // Liste des rang dans l'ordre décroissant:
    //      premier  : elo
    //      deuxième : pseudo
    private ArrayList<String[]> rankings;

    private void retreiveRanking() {
        //TODO BDD
        for (int i = 0; i < 50; i++) {
            String[] rank2 = {"1234", "Joueur 1234"};
            rankings.add(rank2);
        }
        for (int i = 0; i < 50; i++) {
            String[] rank2 = {"123", "Joueur 123"};
            rankings.add(rank2);
        }
    }

    private void populate() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (String[] rank :
                rankings) {
            RankingRowFragment frag = RankingRowFragment.newInstance(rank[0], rank[1]);
            transaction.add(R.id.rankingFragmentReceiver, frag);
        }
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_dialog);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        rankings = new ArrayList<>();
        retreiveRanking();
        populate();
    }
}