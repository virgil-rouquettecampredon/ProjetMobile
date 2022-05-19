package com.example.projetmobile.Model;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projetmobile.GameListFragment;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Rooms;
import com.example.projetmobile.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameManagerOnline extends GameManager{
    class DB_Shots{

        Position startPosPlayerPlay;
        Position endPosPlayerPlay;
        int IDTransformedPiece;

        public DB_Shots(Position startPosPlayerPlay, Position endPosPlayerPlay, int IDTransformedPiece) {
            this.startPosPlayerPlay = startPosPlayerPlay;
            this.endPosPlayerPlay = endPosPlayerPlay;
            this.IDTransformedPiece = IDTransformedPiece;
        }
    }

    private List<DB_Shots> shotsToPush;
    private List<DB_Shots> shotsToPerform;


    private boolean isMyTurn;
    private int playerIndex;

    private String nameRoomRef;

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference mDatabase;
    private DatabaseReference playerDatabase;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private File localFile;
    private File localFile2;

    private String piece1;
    private String piece2;
    private String loose;
    private String player1;
    private String player2;
    private String pseudoPlayer2;
    private String pseudoPlayer1;
    private long eloPlayer1;
    private long eloPlayer2;

    public GameManagerOnline(Context context, Board b) {
        super(context, b);

        shotsToPush = new ArrayList<>();
        shotsToPerform = new ArrayList<>();
    }

    //Call once before game start
    public void setStartingPlayer() {
        playerIndex = 0;
    }

    //Function for changing player name
    public void setPlayerDatas(int id, String pseudo) {
        Player p = players.get(id);
        p.setPseudo(pseudo);
        this.playersUI.get(id).getTVPseudo().setText(p.getPseudo());
    }

    public void setPlayerIndex(int id) {
        playerIndex = id;
    }

    @Override
    public void start() {

        //Initialise all DB structures for online managing
        initialiseDataBase();

        //Loading Pseudo player
        initialiseDataPlayer();

        //Loading Avatar Image
        downloadFilePlayer1(player1);
        downloadFilePlayer2(player2);

        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME MANAGER START");

        //Clear the board
        this.board.clear();

        //Init the board
        this.players = this.board.initGame_players();
        majPlayerLayout();

        this.currentPlayer = players.get(playerIndex);

        turnPlayerListerner();

        if (startANewTurn()) {
            onEndingGame();
        } else {
            computeOnclkListener();
        }
    }

    private void initialiseDataPlayer() {
        playerDatabase.child(player1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    setPlayerDatas(0, task.getResult().getValue(User.class).getPseudo());
                    eloPlayer1 = task.getResult().getValue(User.class).getElo();
                    pseudoPlayer1 = task.getResult().getValue(User.class).getPseudo();
                }
            }
        });
        playerDatabase.child(player2).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    setPlayerDatas(1, task.getResult().getValue(User.class).getPseudo());
                    eloPlayer2 = task.getResult().getValue(User.class).getElo();
                    pseudoPlayer2 = task.getResult().getValue(User.class).getPseudo();
                }
            }
        });
    }

    @Override
    protected boolean startANewTurn() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("<===> START A NEW TURN <===>");
        //if we were the last player to play, then MAJ db first with our play
        if (isMyTurn && nbTurn != 0) {
            for (Shot s : this.allShots.peek()) {
                onCurrentPlayerPlay(new DB_Shots(s.getStartPos(), s.getEndPos(), s.getIDTransformedPiece()));
            }
            SyncToDB();
        }


        //MAJ turn to play
        isMyTurn = (playerIndex == (nbTurn % this.players.size()));
        //Stop game if needed
        gameStopped = !isMyTurn;

        if (DEBUG_FOR_GAME_LOGIC)
            System.out.println("GAME STATE : " + ((gameStopped) ? "STOPPED" : "YOUR TURN !"));

        //Maj UI for better understanding in interface of current player
        majLayoutPlayerTurn(getCurrentPlayer());

        //if its not your turn, don't try to compute anything
        if (isMyTurn) {
            //Next we can compute the special rock movement
            computeRockInGame(this.currentPlayer);
            if (DEBUG_FOR_GAME_ROCK) {
                System.out.println("RES ROCK");
                for (Piece p : rockPiecePositons.keySet()) {
                    System.out.println("ROCK P : " + p);
                    for (Association_rock assoc : this.rockPiecePositons.get(p)) {
                        System.out.println("ASSOC : " + assoc);
                    }
                }
                System.out.println("RES ROCK END");
            }

            //We create all the movement for all the players
            for (Player p : players) {
                computePossibleMvts(p);
            }
            //And we restrict the current player movements if he is in danger
            performMenaced(this.currentPlayer);

            //We can independently compute the dangerous Case by calculating the possible position for each dangerous enemy neighbour
            performDanger(this.currentPlayer);

            //MAJ NUMBER OF TURNS
            //nbTurn++;
            return this.isFinished();
        }

        //MAJ NUMBER OF TURNS
        //nbTurn++;
        //Not your turn, so you are still able to play in theory
        return false;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }
    public String getPlayer2() {
        return this.player2;
    }

    //Function to perform enemy's shots
    private void playAllEnemyShots() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("PLAY ALL ENEMY'S SHOTS");
        if (!shotsToPerform.isEmpty()) {
            if (shotsToPerform.size() == 1) {

                Position startPos = shotsToPerform.get(0).startPosPlayerPlay;
                Position endPos = shotsToPerform.get(0).endPosPlayerPlay;
                int idTransform = shotsToPerform.get(0).IDTransformedPiece;

                Case c = board.getACase(startPos.getX(), startPos.getY());
                //Perform move
                if (DEBUG_FOR_GAME_LOGIC) System.out.println("SIMPLE MOVE");

                //We need to perform a variant of the shot with move animation
                if (ANIMATION_PIECE) {
                    if (DEBUG_FOR_GAME_LOGIC) System.out.println("ANIMATION");
                    //Animation
                    moveAPiece_animated(startPos, endPos, false);
                } else {
                    if (DEBUG_FOR_GAME_LOGIC) System.out.println("NO ANIMATION");

                    //No animation
                    Piece pMoved = moveAPiece(startPos, endPos, false);
                    nbTurn++;
                    //Start a new turn
                    if (startANewTurn()) {
                        //If its finished, then stop the treatment
                        onEndingGame();
                    }
                }
                //Perform transformation (if needed)
            } else {
                //rock move
                if (DEBUG_FOR_GAME_LOGIC) System.out.println("ROCK MOVE");
                Position startPos_king = shotsToPerform.get(0).startPosPlayerPlay;
                Position endPos_king = shotsToPerform.get(0).endPosPlayerPlay;

                Position startPos_tower = shotsToPerform.get(1).startPosPlayerPlay;
                Position endPos_tower = shotsToPerform.get(1).endPosPlayerPlay;

                Case start_case_tower = board.getACase(startPos_tower.getX(), startPos_tower.getY());

                Piece tower = start_case_tower.getPiece();

                Association_rock as = new Association_rock(tower, startPos_tower, endPos_king, endPos_tower);

                //We need to perform a variant of the shot with move animation
                if (ANIMATION_PIECE) {
                    //Animation
                    moveAPiece_animated_rock(startPos_king, as);
                } else {
                    //No animation
                    moveAPiece_rock(startPos_king, as);
                    nbTurn++;
                    //Start a new turn
                    if (startANewTurn()) {
                        //If its finished, then stop the treatment
                        onEndingGame();
                    }
                }
            }
        } else {
            if (DEBUG_FOR_GAME_LOGIC) System.out.println("+NO SHOT TO PERFORM+");
            //This meaning that the player that need to play didn't play anything
            //Because : Loose
            //          Pat
            //          FF

            this.currentPlayer = players.get(1 - playerIndex);
            System.out.println(this.currentPlayer);

            for (Player p : players) {
                computePossibleMvts(p);
            }

            //Compute local winner
            super.onEndingGame();
            if (playerIndex == 1) {
                //Set the data in DB for all players
                long eloDiff = eloInflated(eloPlayer2, eloPlayer1, player2, true);
                addHistoryGame(player2, nbTurn, "win", pseudoPlayer1, eloDiff);

                long eloDiff2 = eloInflated(eloPlayer2, eloPlayer1, player1, false);
                addHistoryGame(player1, nbTurn, "loose", pseudoPlayer2, eloDiff2);
            }
            else {
                //Set the data in DB for all players
                long eloDiff = eloInflated(eloPlayer2, eloPlayer1, player1, true);
                addHistoryGame(player1, nbTurn, "win", pseudoPlayer2, eloDiff);

                long eloDiff2 = eloInflated(eloPlayer2, eloPlayer1, player2, false);
                addHistoryGame(player2, nbTurn, "loose", pseudoPlayer1, eloDiff2);
            }

            //wait 10 seconds before going to the next step

            //deleteRoomsInformation();

        }
        shotsToPerform.clear();
    }

    //Function called when current enemy player play a turn
    public void onEnemyPlayerPlay() {
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    loose = task.getResult().getValue(Rooms.class).getLoose();
                    if (loose != null) {
                        Log.d("Firebase", "the enemy player has loose");
                        playAllEnemyShots();
                    } else {

                        piece1 = task.getResult().getValue(Rooms.class).getPiece1();

                        String[] move = piece1.split("/");
                        String[] posStart = move[0].split("_");
                        String[] posEnd = move[1].split("_");
                        shotsToPerform.add(new DB_Shots(new Position(parseInt(posStart[0]), parseInt(posStart[1])), new Position(parseInt(posEnd[0]), parseInt(posEnd[1])), parseInt(move[2])));

                        piece2 = task.getResult().getValue(Rooms.class).getPiece2();
                        if (!piece2.equals("")) {
                            String[] move2 = piece2.split("/");
                            String[] posStart2 = move2[0].split("_");
                            String[] posEnd2 = move2[1].split("_");
                            shotsToPerform.add(new DB_Shots(new Position(parseInt(posStart2[0]), parseInt(posStart2[1])), new Position(parseInt(posEnd2[0]), parseInt(posEnd2[1])), -1));

                        }
                        piece1 = "";
                        piece2 = "";
                        playAllEnemyShots();
                    }
                }
            }
        });
        //Information take from database to perfom the movement for the others player
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("ENEMY PLAYER PLAY A SHOT");
        System.out.println("PIECE 1 : " + piece1);
        System.out.println("PIECE 2 : " + piece2);

    }

    //Function called when our current player play a turn
    public void onCurrentPlayerPlay(DB_Shots d) {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("PLAYER PLAY A TURN");

        shotsToPush.add(d);
    }

    //Function called to end a turn and sync datas to other players
    public void SyncToDB() {
        //Perform DATA BASES LOGIC on shotsToPush
        //Inform all other players that the current player end his turn
        if (shotsToPush.size() > 0) {
            roomRef.child("piece1").setValue("" + shotsToPush.get(0).startPosPlayerPlay.getX() + "_" + shotsToPush.get(0).startPosPlayerPlay.getY() + "/" + shotsToPush.get(0).endPosPlayerPlay.getX() + "_" + shotsToPush.get(0).endPosPlayerPlay.getY() + "/" + shotsToPush.get(0).IDTransformedPiece);
            if (shotsToPush.size() > 1) {
                roomRef.child("piece2").setValue("" + shotsToPush.get(1).startPosPlayerPlay.getX() + "_" + shotsToPush.get(1).startPosPlayerPlay.getY() + "/" + shotsToPush.get(1).endPosPlayerPlay.getX() + "_" + shotsToPush.get(1).endPosPlayerPlay.getY());
            } else {
                roomRef.child("piece2").setValue("");
            }
        }
        if (playerIndex == 0) {
            roomRef.child("turn").setValue(2);
        } else {
            roomRef.child("turn").setValue(1);
        }

        shotsToPush.clear();
    }

    //Function called when game over (victory if our player win)
    public void onGameFinished(boolean victory) {
        // inform other player that this.currentPlayer ((victory)? won : loose)
    }

    public void setNameRoomRef(String nameRoomRef) {
        this.nameRoomRef = nameRoomRef;
    }

    public void initialiseDataBase() {
        Log.d("firebase", nameRoomRef);
        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
        roomRef = database.getReference("rooms").child(nameRoomRef);
        mDatabase = database.getReference("rooms").child(nameRoomRef);
        playerDatabase = database.getReference("users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void turnPlayerListerner() {
        roomRef.child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("TURN CHANGED");
                System.out.println(dataSnapshot.getValue());
                System.out.println("NB TURN : " + nbTurn);
                if ((long) dataSnapshot.getValue() == playerIndex + 1 && nbTurn + playerIndex > 0) {
                    onEnemyPlayerPlay();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onEndingGame() {
        super.onEndingGame();
        roomRef.child("loose").setValue("yes");
        roomRef.child("turn").setValue(2 - playerIndex);
    }

    public void setImage(int id, Uri uri){
        this.playersUI.get(id).getImgPlayer().setImageURI(uri);
    }

    private void downloadFilePlayer1(String player){
        StorageReference storageReference = storage.getReference().child("images/" + player+".jpg");
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
                setImage(0,Uri.parse(uri.toString()));
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

    private void downloadFilePlayer2(String player){
        StorageReference storageReference = storage.getReference().child("images/" + player+".jpg");
        localFile2 = null;
        try {
            localFile2 = File.createTempFile("images", "jpg");
            Log.d("BDD*", "downloadFile: " + localFile2.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("BDD*", "downloadFile: " + e.getMessage());
        }

        storageReference.getFile(localFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Log.d("BDD*", "onSuccess: " + taskSnapshot.getStorage().getPath());
                Uri uri = Uri.fromFile(localFile2);
                Log.d("BDD*", "onSuccess: " + uri.toString());
                setImage(1,Uri.parse(uri.toString()));
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("BDD*", "onFailure: " + exception.getMessage());
                // Handle any errors
                localFile2 = null;
            }
        });
    }

    public void addHistoryGame(String player, long nbCoup, String haveWin, String opponent, long eloDiff){
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHH:mm:ss");
        Date date = new Date();
        String dateFormatted = formatter.format(date).toString();

        database.getReference().child("history").child(player).child(dateFormatted).child("nbCoup").setValue(nbCoup);
        database.getReference().child("history").child(player).child(dateFormatted).child("haveWin").setValue(haveWin);
        database.getReference().child("history").child(player).child(dateFormatted).child("opponent").setValue(opponent);
        database.getReference().child("history").child(player).child(dateFormatted).child("eloDiff").setValue(eloDiff);
    }

    public void deleteRoomsInformation(){
        database.getReference().child("rooms").child(nameRoomRef).removeValue();
    }

    //Return the elo loose or win for the history game
    public long eloInflated(long eloWinner, long eloLooser, String player, boolean winner){
        //Do random number between 5 and 15
        Random random = new Random();
        int randomNumber = random.nextInt(10) + 5;

        int eloWin = 0;
        int eloLoose = 0;

        if(eloWinner < eloLooser){
            eloWin = (int)(((int)eloLooser - (int)eloWinner)/3) + randomNumber * 2;
            eloLoose = (int)(((int)eloLooser - (int)eloWinner)/3) + (int)((long)randomNumber * 1.5);
        }
        else {
            eloWin = (int)(((int)eloWinner - (int)eloLooser)/5) + (int)((long)randomNumber * 0.5);
            eloLoose = (int)(((int)eloWinner - (int)eloLooser)/5) + (int)((long)randomNumber * 0.25);
        }

        if(winner) {
            database.getReference().child("users").child(player).child("elo").setValue(eloWinner + eloWin);
            return eloWin;
        }
        else {
            database.getReference().child("users").child(player).child("elo").setValue(eloLooser - eloLoose);
            return - eloLoose;
        }
    }
}
