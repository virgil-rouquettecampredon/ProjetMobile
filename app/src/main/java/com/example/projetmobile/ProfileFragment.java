package com.example.projetmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    ActivityResultLauncher<Intent> setFriendPseudoLauncher;
    ActivityResultLauncher<Intent> setBioLauncher;
    ActivityResultLauncher<Intent> warnDeleteLauncher;
    ActivityResultLauncher<Intent> setProfilePictureLauncher;
    ActivityResultLauncher<Intent> setPreferencesLauncher;

    TextView textViewPseudo;
    TextView bioTextView;
    TextView textViewPoints;
    ImageView imageViewAvatar;

    String pseudo;
    String bio;
    Boolean useAnimation;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private File localFile;

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
                        pseudo = resultString;
                        mDatabase.child("users").child(user.getUid()).child("pseudo").setValue(resultString);
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
                        bio = resultString;
                        mDatabase.child("users").child(user.getUid()).child("bio").setValue(resultString);
                    }
                });

        warnDeleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        AuthCredential credential = EmailAuthProvider.getCredential("test@gmail.com", "123456");
                        //TODO SUPPRIMER LE COMPTE POUR TOUJOURS (un très long moment)
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User account deleted.");
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(getContext(), "Deleted User Successfully,", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            deleteFile();
                            deleteUserInformation();
                        }
                    }

                });


        setProfilePictureLauncher =

                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),

                        result ->

                        {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                //TODO Changer la PP (BDD)
                                Intent data = result.getData();
                                String resultString = data.getStringExtra(EditTextDialogActivity.resultName);
                                Uri filePath = Uri.parse(resultString);
                                if (!resultString.isEmpty()) {
                                    imageViewAvatar.setImageURI(Uri.parse(resultString));
                                    Log.d("TAG", "onActivityResult: " + resultString);
                                    uploadFile(filePath);
                                }
                            }
                        });

        setPreferencesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        int resultID = data.getIntExtra(EditTextDialogActivity.resultName, 0);
                        useAnimation = resultID == 0;
                        mDatabase.child("users").child(user.getUid()).child("useAnimations").setValue(resultID);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        textViewPseudo = view.findViewById(R.id.textViewPseudo);
        textViewPoints = view.findViewById(R.id.textViewPoints);

        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetProfilePictureDialogActivity.class);
            Bundle bundle = new Bundle();
            if (localFile != null) {
                bundle.putString("imagePath", Uri.fromFile(localFile).toString());
            }
            intent.putExtras(bundle);
            setProfilePictureLauncher.launch(intent);
        });

        MaterialButton preferencesButton = view.findViewById(R.id.preferencesButton);
        preferencesButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectorDialogActivity.class);
            intent.putExtra(SelectorDialogActivity.titleName, getString(R.string.game_animation));
            ArrayList<String> selection = new ArrayList<>(2);
            selection.add(getString(R.string.yes));
            selection.add(getString(R.string.no));
            intent.putExtra(SelectorDialogActivity.choicesName, selection);
            intent.putExtra(SelectorDialogActivity.checkedIdName, (useAnimation) ? 0 : 1);
            setPreferencesLauncher.launch(intent);
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
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, pseudo);
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.pseudo));
            setFriendPseudoLauncher.launch(intent);
        });

        bioTextView = view.findViewById(R.id.bioTextView);
        bioTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditTextDialogActivity.class);
            intent.putExtra(EditTextDialogActivity.editTextPrefillName, bio);
            intent.putExtra(EditTextDialogActivity.titleName, getString(R.string.biography));
            setBioLauncher.launch(intent);
        });

        /**Database information**/

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Log.d("BDD*", "onCreateView: " + storageRef);

        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
        mDatabase = database.getReference();
        String keyId = user.getUid();

        downloadFile();

        mDatabase.child("users").child(keyId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    bio = task.getResult().getValue(User.class).getBio();
                    pseudo = task.getResult().getValue(User.class).getPseudo();
                    useAnimation = task.getResult().getValue(User.class).getUseAnimations() == 0;
                    textViewPseudo.setText(task.getResult().getValue(User.class).getPseudo());
                    bioTextView.setText(task.getResult().getValue(User.class).getBio());
                    textViewPoints.setText(task.getResult().getValue(User.class).getElo().toString());
                }
            }
        });

        return view;
    }

    private void uploadFile(Uri filePath) {
        Log.d("BDD*", "uploadFile: " + filePath);
        // Create a storage reference from our app
        if (filePath != null) {
            StorageReference storageReference = storage.getReference().child("images/" + user.getUid()+".jpg");
            System.out.println(storageReference.getPath());
            storageReference.putFile(filePath);
        }
    }

    private void downloadFile(){
        StorageReference storageReference = storage.getReference().child("images/" + user.getUid()+".jpg");
        localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            Log.d("BDD*", "downloadFile: " + localFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("BDD*", "downloadFile: " + e.getMessage());
        }

        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Log.d("BDD*", "onSuccess: " + taskSnapshot.getStorage().getPath());
                Uri uri = Uri.fromFile(localFile);
                Log.d("BDD*", "onSuccess: " + uri.toString());
                imageViewAvatar.setImageURI(Uri.parse(uri.toString()));
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("BDD*", "onFailure: " + exception.getMessage());
                // Handle any errors
                localFile = null;
            }
        });
    }

    public void deleteFile(){
        StorageReference desertRef = storage.getReference().child("images/" + user.getUid()+".jpg");

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public void deleteUserInformation(){
        database.getReference().child("users").child(user.getUid()).removeValue();
    }
}