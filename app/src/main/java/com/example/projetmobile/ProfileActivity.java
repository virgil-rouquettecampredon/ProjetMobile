package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {
    public final static String fragmentTag = "PROFILEFRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_background);

        ImageView imageView = findViewById(R.id.menuBurgerToggle);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MenuBurgerActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        ProfileFragment frag = (ProfileFragment) fm.findFragmentByTag(fragmentTag);

        if (frag == null) {
            frag = ProfileFragment.newInstance();
            transaction.add(R.id.fragment_container, frag, fragmentTag);
            transaction.commit();
        }
    }
}