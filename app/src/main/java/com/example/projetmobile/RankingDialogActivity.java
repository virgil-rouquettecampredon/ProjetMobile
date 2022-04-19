package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
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
        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        for (int i = 0, size = rankings.size(); i < size; i++) {
            RankingRowFragment frag = RankingRowFragment.newInstance(rankings.get(i)[0], rankings.get(i)[0]);
            transaction.add(R.id.rankingFragmentReceiver, frag);

            if (land && i+1 < size) {
                RankingRowFragment frag2 = RankingRowFragment.newInstance(rankings.get(i)[0], rankings.get(i)[0]);
                transaction.add(R.id.rankingFragmentReceiver2, frag2);
                i++;
            }
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