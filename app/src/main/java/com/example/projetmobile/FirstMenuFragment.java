package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FirstMenuFragment extends Fragment {

    public FirstMenuFragment() {
        // Required empty public constructor
    }

    public static FirstMenuFragment newInstance() {
        FirstMenuFragment fragment = new FirstMenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_first_menu, container, false);

        AppCompatButton connectionButton = view.findViewById(R.id.connectionButton);
        AppCompatButton offlineButton = view.findViewById(R.id.offlineButton);

        connectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConnectionActivity.class);
            startActivity(intent);
        });

        offlineButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}