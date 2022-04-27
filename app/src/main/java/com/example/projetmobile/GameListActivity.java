package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class GameListActivity extends AppCompatActivity {

    public final static String fragmentTag = "GAMELISTFRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_background);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        GameListFragment frag = (GameListFragment) fm.findFragmentByTag(fragmentTag);

        if (frag == null) {
            frag = GameListFragment.newInstance();
            transaction.add(R.id.fragment_container, frag, fragmentTag);
            transaction.commit();
        }
    }
}