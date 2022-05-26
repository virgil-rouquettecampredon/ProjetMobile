package com.example.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class HistoryDialogActivity extends AppCompatActivity {

    public static class HistoryData {
        public boolean isAWin;
        public String eloChangeAmount;
        public String adversaryPseudo;
        public String gameTurnCount;
        public String winType;

        public HistoryData(boolean isAWin, String eloChangeAmount, String adversaryPseudo, String gameTurnCount, String winType) {
            this.isAWin = isAWin;
            this.eloChangeAmount = eloChangeAmount;
            this.adversaryPseudo = adversaryPseudo;
            this.gameTurnCount = gameTurnCount;
            this.winType = winType;
        }
    }

    private ArrayList<HistoryData> allHistoryData;

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ValueEventListener roomListener;

    private void retreiveRanking(ArrayList<HistoryData> allhistoryFromDB) {
        //TODO BDD
        allHistoryData.clear();
        allHistoryData.addAll(allhistoryFromDB);
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

        /**DATABASE Information**/
        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
        mDatabase = database.getReference();
        String keyId = user.getUid();

        mDatabase.child("users").child(keyId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                }
            }
        });
        /**END DATABASE Information**/

        allHistoryData = new ArrayList<>();
        updateHistory();
    }

    public void updateHistory(){
        roomListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                ArrayList<HistoryData> historyList = new ArrayList<>();
                for (DataSnapshot snapshot : children) {
                    HistoryData data;
                    if (snapshot.child("haveWin").getValue().toString().equals("win")) {
                        data = new HistoryData(true, snapshot.child("eloDiff").getValue().toString(), (String)snapshot.child("opponent").getValue(), snapshot.child("nbCoup").getValue().toString(), snapshot.child("TypeVictoire").getValue().toString());
                    }
                    else{
                        data = new HistoryData(false, snapshot.child("eloDiff").getValue().toString(), (String)snapshot.child("opponent").getValue(), snapshot.child("nbCoup").getValue().toString(), snapshot.child("TypeVictoire").getValue().toString());
                    }
                    historyList.add(data);
                }

                retreiveRanking(historyList);
                populate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Nothing to do
            }

        };
        mDatabase.child("history").child(user.getUid()).addValueEventListener(roomListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.child("history").child(user.getUid()).removeEventListener(roomListener);
    }
}