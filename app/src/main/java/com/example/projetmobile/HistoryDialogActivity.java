package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class HistoryDialogActivity extends AppCompatActivity {

    public static class HistoryData {
        public boolean isAWin;
        public String eloChangeAmount;
        public String date;
        public String adversaryPseudo;
        public String gameTime;
        public String gameTurnCount;
        public String winType;

        public HistoryData(boolean isAWin, String eloChangeAmount, String date, String adversaryPseudo, String gameTime, String gameTurnCount, String winType) {
            this.isAWin = isAWin;
            this.eloChangeAmount = eloChangeAmount;
            this.date = date;
            this.adversaryPseudo = adversaryPseudo;
            this.gameTime = gameTime;
            this.gameTurnCount = gameTurnCount;
            this.winType = winType;
        }
    }

    private ArrayList<HistoryData> allHistoryData;

    private void retreiveRanking() {
        //TODO BDD
        for (int i = 0; i < 50; i++) {
            HistoryData data = new HistoryData(Math.random() < 0.5, ""+ (int) (Math.random() * 20), "Il y a "+i+" heures", "DarkVirgil"+(int) (Math.random() * 50), (int) (Math.random() * 2)+"h"+(int) (Math.random() * 60), ""+((int) (Math.random() * 100)+50), "Echec et mat");
            allHistoryData.add(data);
        }
    }

    private void populate() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        for (int i = 0, size = allHistoryData.size(); i < size; i++) {
            HistoryRowFragment frag = HistoryRowFragment.newInstance(allHistoryData.get(i));
            transaction.add(R.id.historyFragmentReceiver, frag);

            if (land && i+1 < size) {
                HistoryRowFragment frag2 = HistoryRowFragment.newInstance(allHistoryData.get(i+1));
                transaction.add(R.id.historyFragmentReceiver2, frag2);
                i++;
            }
        }
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_dialog);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        allHistoryData = new ArrayList<>();
        retreiveRanking();
        populate();
    }
}