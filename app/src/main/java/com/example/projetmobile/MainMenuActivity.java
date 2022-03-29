package com.example.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainMenuActivity extends AppCompatActivity {
    public final static String fragmentTag = "MAINMENUFRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        MainMenuFragment frag = (MainMenuFragment) fm.findFragmentByTag(fragmentTag);

        if (frag == null) {
            frag = MainMenuFragment.newInstance();
            transaction.add(R.id.fragment_container, frag, fragmentTag);
            transaction.commit();
        }
    }
}