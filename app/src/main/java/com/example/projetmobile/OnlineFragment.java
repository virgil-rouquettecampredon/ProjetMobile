package com.example.projetmobile;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class OnlineFragment extends Fragment {

    public OnlineFragment() {
        // Required empty public constructor
    }

    public static OnlineFragment newInstance() {
        OnlineFragment fragment = new OnlineFragment();
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
        View view = inflater.inflate(R.layout.fragment_online, container, false);

        AppCompatButton rankedButton = view.findViewById(R.id.rankedButton);
        rankedButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        AppCompatButton friendButton = view.findViewById(R.id.friendButton);
        friendButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });
        
        AppCompatButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            getActivity().finish();
        });

        return view;
    }
}