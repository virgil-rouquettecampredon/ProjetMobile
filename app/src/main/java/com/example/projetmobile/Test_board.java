package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Test_board extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_board);
        System.out.println("=============> TEST BOARD ONCREATE");
    }
}