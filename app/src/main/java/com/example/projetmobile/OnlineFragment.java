package com.example.projetmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class OnlineFragment extends Fragment {
    ActivityResultLauncher<Intent> setFriendPseudoLauncher;
    ActivityResultLauncher<Intent> friendMatchWaitLauncher;

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

        setFriendPseudoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String resultString = data.getStringExtra(EditTextDialogActivity.resultName);
                    //TODO faire qqch du pseudo retourné
                    Toast.makeText(getActivity(), "Pseudo is : "+resultString, Toast.LENGTH_SHORT).show();

                    Intent friendMatchWaitIntent = new Intent(getActivity(), FriendMatchWaitActivity.class);
                    friendMatchWaitLauncher.launch(friendMatchWaitIntent);
                }
            });

        friendMatchWaitLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled friend match", Toast.LENGTH_SHORT).show();
                }
            });
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
            Intent intent = new Intent(getActivity(), EditTextDialogActivity.class);
            //TODO récupérer le vrai pseudo
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, getString(R.string.default_pseudo));
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.pseudo));
            setFriendPseudoLauncher.launch(intent);
        });
        
        AppCompatButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            getActivity().finish();
        });


        return view;
    }
}