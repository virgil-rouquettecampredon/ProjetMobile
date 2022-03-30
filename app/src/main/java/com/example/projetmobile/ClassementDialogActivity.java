package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ClassementDialogActivity extends AppCompatActivity {

    // Liste des rang :
    //      premier  : elo
    //      deuxième : pseudo
    private ArrayList<String[]> rankings;

    private void retreiveRanking() {
        //TODO BDD
        String[] rank = {"123", "NullJoueur34"};
        rankings.add(rank);

        for (int i = 0; i < 50; i++) {
            String[] rank2 = {"1234", "UnPeuMeilleurJoueur"};
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
        setContentView(R.layout.activity_classement_dialog);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        rankings = new ArrayList<>();
        retreiveRanking();
        populate();
    }
}