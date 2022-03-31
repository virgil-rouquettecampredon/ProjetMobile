package com.example.projetmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;


public class ProfileFragment extends Fragment {
    ActivityResultLauncher<Intent> setFriendPseudoLauncher;
    ActivityResultLauncher<Intent> setBioLauncher;
    ActivityResultLauncher<Intent> warnDeleteLauncher;
    ActivityResultLauncher<Intent> setProfilePictureLauncher;

    TextView textViewPseudo;
    TextView bioTextView;
    ImageView imageViewAvatar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
                    //TODO Changer le pseudo (BDD)
                    textViewPseudo.setText(resultString);
                }
            });

        setBioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String resultString = data.getStringExtra(EditTextDialogActivity.resultName);
                    //TODO Changer la bio (BDD)
                    bioTextView.setText(resultString);
                }
            });

        warnDeleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //TODO SUPPRIMER LE COMPTE POUR TOUJOURS (un très long moment)
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

        setProfilePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //TODO Changer la PP (BDD)
                    Intent data = result.getData();
                    String resultString = data.getStringExtra(EditTextDialogActivity.resultName);
                    if (!resultString.isEmpty()) {
                        imageViewAvatar.setImageURI(Uri.parse(resultString));
                    }
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewPseudo = view.findViewById(R.id.textViewPseudo);

        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetProfilePictureDialogActivity.class);
            setProfilePictureLauncher.launch(intent);
        });

        MaterialButton preferencesButton = view.findViewById(R.id.preferencesButton);
        preferencesButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        });

        MaterialButton historyButton = view.findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryDialogActivity.class);
            startActivity(intent);
        });

        MaterialButton rankingButton = view.findViewById(R.id.rankingButton);
        rankingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RankingDialogActivity.class);
            startActivity(intent);
        });

        MaterialButton deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TextViewDialogActivity.class);
            intent.putExtra(TextViewDialogActivity.titleName, getString(R.string.warning));
            intent.putExtra(TextViewDialogActivity.textName, getString(R.string.deleteAccountWarningSpeech));
            warnDeleteLauncher.launch(intent);
        });

        MaterialButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            getActivity().finish();
        });


        View editPseudoView = view.findViewById(R.id.editPseudoView);
        editPseudoView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditTextDialogActivity.class);
            //TODO récupérer le vrai pseudo
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, getString(R.string.default_pseudo));
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.pseudo));
            setFriendPseudoLauncher.launch(intent);
        });

        bioTextView = view.findViewById(R.id.bioTextView);
        bioTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditTextDialogActivity.class);
            //TODO récupérer la vrai bio
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, getString(R.string.default_description));
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.biography));
            setBioLauncher.launch(intent);
        });

        return view;
    }
}