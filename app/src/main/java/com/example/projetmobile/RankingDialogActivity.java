package com.example.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
import java.util.Comparator;

public class RankingDialogActivity extends AppCompatActivity {

    // Liste des rang dans l'ordre décroissant:
    //      premier  : elo
    //      deuxième : pseudo
    private ArrayList<String[]> rankings;

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ValueEventListener roomListener;

    private void retreiveRanking(ArrayList<String[]> nonSortedRankings) {
        nonSortedRankings.sort((data1, data2) -> Integer.parseInt(data2[0]) - Integer.parseInt(data1[0]));
        rankings = nonSortedRankings;
    }

    private void populate() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        for (int i = 0, size = rankings.size(); i < size; i++) {
            RankingRowFragment frag = RankingRowFragment.newInstance(rankings.get(i)[0], rankings.get(i)[1]);
            transaction.add(R.id.rankingFragmentReceiver, frag);

            if (land && i+1 < size) {
                RankingRowFragment frag2 = RankingRowFragment.newInstance(rankings.get(i)[0], rankings.get(i)[1]);
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

        rankings = new ArrayList<>();
        updateRanking();
    }

    public void updateRanking(){
        roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                ArrayList<String[]> rankingList = new ArrayList<>();
                for (DataSnapshot snapshot : children) {
                    String[] data = new String[2];
                    data[0] = snapshot.child("elo").getValue(Long.class).toString();
                    data[1] = snapshot.child("pseudo").getValue(String.class);
                    rankingList.add(data);
                }

                retreiveRanking(rankingList);
                populate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Nothing to do
            }

        };
        mDatabase.child("users").addValueEventListener(roomListener);
    }
}