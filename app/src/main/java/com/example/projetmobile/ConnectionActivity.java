package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class ConnectionActivity extends AppCompatActivity {
    public final static String fragmentTag = "CONNECTIONFRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        ConnectionFragment frag = (ConnectionFragment) fm.findFragmentByTag(fragmentTag);

        if (frag == null) {
            frag = ConnectionFragment.newInstance();
            transaction.add(R.id.fragment_container, frag, fragmentTag);
            transaction.commit();
            System.out.println(frag);
        }
    }
}