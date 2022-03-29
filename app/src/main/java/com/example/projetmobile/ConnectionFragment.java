package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionFragment extends Fragment {
    public ConnectionFragment() {
        // Required empty public constructor
    }

    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        Button connectButton = view.findViewById(R.id.connectButton);
        connectButton.setOnClickListener(v -> {
            //TODO upload and save data
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            startActivity(intent);
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            getActivity().finish();
        });
        return view;
    }
}