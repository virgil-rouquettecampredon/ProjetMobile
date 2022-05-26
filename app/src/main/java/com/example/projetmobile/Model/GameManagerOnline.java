package com.example.projetmobile.Model;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projetmobile.GameListFragment;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Bishop;
import com.example.projetmobile.Model.Pieces.Knight;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Pieces.Queen;
import com.example.projetmobile.Model.Pieces.Tower;
import com.example.projetmobile.R;
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
import java.util.Collections;
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
                    GameManagerOnline.ANIMATION_PIECE = task.getResult().getValue(User.class).getUseAnimations() == 0;
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
        this.board.commitChanges();
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

        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME STATE : " + ((gameStopped) ? "STOPPED" : "YOUR TURN !"));

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

            return this.isFinished();
        }else {
            for (Position pos : positionWithDanger) {
                if (board.isGoodPos(pos.getX(), pos.getY())) {
                    this.board.setPossibleCaseWithMenaceOnIt(pos.getX(), pos.getY(), false);
                }
            }
            positionWithDanger.clear();

            //===== For menace treatment
            //For just a MAJ for the player who get menaced, no need to recompute all possible movements
            for (Player p : this.players) {
                this.computePossibleMvts(p);
            }
            for (Player p : this.players) {
                //Then cpt the new menace
                cptMenace(p);
            }

            //And finally maj the UI
            for (Position pos : positionWithDanger) {
                if (board.isGoodPos(pos.getX(), pos.getY())) {
                    this.board.setPossibleCaseWithMenaceOnIt(pos.getX(), pos.getY(), true);
                }
            }
            this.board.commitChanges();
        }

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

                Position startPos   = shotsToPerform.get(0).startPosPlayerPlay;
                Position endPos     = shotsToPerform.get(0).endPosPlayerPlay;
                int idTransform     = shotsToPerform.get(0).IDTransformedPiece;

                //Log.d("PLAY ALL ENEMY SHOT", "PS(" + startPos.getX() + "," + startPos.getY() + ")");
                //Log.d("PLAY ALL ENEMY SHOT", "PE(" + endPos.getX() + "," + endPos.getY() + ")");
                //Log.d("PLAY ALL ENEMY SHOT", "ID(" + idTransform +")");

                Case c = board.getACase(startPos.getX(), startPos.getY());
                Piece pieceToMove = c.getPiece();

                //Perform move
                if (DEBUG_FOR_GAME_LOGIC) System.out.println("SIMPLE MOVE");
                //We need to perform a variant of the shot with move animation
                if (ANIMATION_PIECE) {
                    if (DEBUG_FOR_GAME_LOGIC) System.out.println("ANIMATION");
                    //Animation
                    moveAnEnnemyPiece_animated(startPos, endPos, false, idTransform);
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
                if(idTransform>=0){
                    Player ennemy = this.players.get((1 - this.playerIndex));
                    this.transformEnnemyPiece(ennemy,idTransform,pieceToMove,endPos);
                }

                this.board.commitChanges();

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
            this.performHistoryWinner();

            //wait 10 seconds before going to the next step
            //deleteRoomsInformation();
        }

        shotsToPerform.clear();
    }

    private void performHistoryWinner(){
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
    }

    private void performHistoryLooser(){
        if (playerIndex == 0) {
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
    }

    public void transformEnnemyPiece(Player player,int id,Piece oldP,Position pos){
        //Log.d("MOVE AN ENNEMY PIECE", "ID : " + id);
        //Log.d("MOVE AN ENNEMY PIECE", "POS(" + pos.getX() + "," + pos.getY() + ")");

        Piece newP = null;
        switch (id) {
            case  Board.TOWER :
                newP = new Tower(true, oldP, player.getAppearance(Board.TOWER));
                break;
            case Board.QUEEN :
                newP = new Queen(true, oldP, player.getAppearance(Board.QUEEN));
                break;
            case Board.KNIGHT :
                newP = new Knight(true, oldP, player.getAppearance(Board.KNIGHT));
                break;
            case Board.BISHOP :
                newP = new Bishop(true, oldP, player.getAppearance(Board.BISHOP));
                break;
        }

        this.board.setAPieces(pos.getX(), pos.getY(), newP);
        //player piece destruction
        oldP.getPossessor().destroyAPiece(oldP);
        //Maj last shot Piece Transformation
        this.allShots.peek().get(0).setIDTransformedPiece(id);
    }

    protected void moveAnEnnemyPiece_animated(Position start, Position end, boolean lightMove, int idTransform) {
        //Move a piece form start position to end position with animation
        //Log.d("MOVE AN ANIMATED PIECE ENNEMY", "START(" + start.getX() + "," + start.getY() +")");
        //Log.d("MOVE AN ANIMATED PIECE ENNEMY", "END(" + end.getX() + "," + end.getY() +")");
        //Log.d("MOVE AN ANIMATED PIECE ENNEMY", "ID TRANS(" + idTransform +")");

        gameStopped = true;

        Case start_case = board.getACase(start.getX(), start.getY());
        Case end_case = board.getACase(end.getX(), end.getY());

        Piece moved = start_case.getPiece();
        Piece possibly_eaten = end_case.getPiece();

        Shot s = new Shot(moved, start, end, possibly_eaten, !lightMove, !moved.isMovedYet());
        allShots.push(Collections.singletonList(s));

        //Animation function to call for perform a smooth displacement of the piece and not a teleportation
        board.animatedDisplacement(new ArrayList<>(Collections.singletonList(start_case)), new ArrayList<>(Collections.singletonList(end_case)), 1, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                board.setAPieces(start_case.getColumn(), start_case.getRow(), null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Visual animation ending
                board.restart_no_animation_context();

                //All the game ending, and also mechanism to preform another round
                //Piece ate mechanism
                if (possibly_eaten != null) {
                    //If there is a  piece at the direction of displacement, its eaten
                    Player pAte = eatAPiece(possibly_eaten);

                    //We MAJ the cimetary visual after
                    if (pAte != null && !lightMove) {
                        addCimetaryLayout(pAte, possibly_eaten);
                    }
                }

                //Perform the complete movement
                board.setAPieces(end_case.getColumn(), end_case.getRow(), moved);
                moved.setMoved(true);


                //Cancel pause moment
                gameStopped = false;

                //Log.d("MOVE AN ANIMATED PIECE ENEMY (END)", "" + possibly_eaten);

                //Perform transformation (if needed)
                if(idTransform>=0){
                    Player ennemy = players.get((1 - playerIndex));
                    transformEnnemyPiece(ennemy,idTransform,moved,end);
                }

                onFinishedTurn();
                //Else go through a normal treatment
                //Go through another round and start a new turn
                if (startANewTurn()) {
                    //If its finished, then stop the treatment
                    board.commitChanges();
                    onEndingGame();
                }
                board.commitChanges();
            }
        });
    }

    //Function called when current enemy player play a turn
    public void onEnemyPlayerPlay() {
        System.out.println("=======================");
        System.out.println(" ON ENNEMY PLAYER PLAY ");

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
                        winByFF();
                    } else {
                        piece1 = task.getResult().getValue(Rooms.class).getPiece1();
                        if(!piece1.equals("")) {

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
        System.out.println("SYNC TO DB");

        //Perform DATA BASES LOGIC on shotsToPush
        //Inform all other players that the current player end his turn
        if (shotsToPush.size() > 0) {
            roomRef.child("piece1").setValue("" + shotsToPush.get(0).startPosPlayerPlay.getX() + "_" + shotsToPush.get(0).startPosPlayerPlay.getY() + "/" + shotsToPush.get(0).endPosPlayerPlay.getX() + "_" + shotsToPush.get(0).endPosPlayerPlay.getY() + "/" + shotsToPush.get(0).IDTransformedPiece);
            if (shotsToPush.size() > 1) {
                roomRef.child("piece2").setValue("" + shotsToPush.get(1).startPosPlayerPlay.getX() + "_" + shotsToPush.get(1).startPosPlayerPlay.getY() + "/" + shotsToPush.get(1).endPosPlayerPlay.getX() + "_" + shotsToPush.get(1).endPosPlayerPlay.getY());
            } else {
                roomRef.child("piece2").setValue("");
            }
        }else{
            System.out.println("0 SHOT");
            roomRef.child("piece1").setValue("");
            roomRef.child("piece2").setValue("");
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
                System.out.println("Snapshot VAL    : " + dataSnapshot.getValue());
                System.out.println("NB TURN         : " + nbTurn);
                System.out.println("PLAYER INDEX    : " + playerIndex);

                if ((long) dataSnapshot.getValue() == playerIndex + 1 && nbTurn + playerIndex > 0) {
                    onEnemyPlayerPlay();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onEndingGame() {
        super.onEndingGame();
        //roomRef.child("loose").setValue("yes");
        roomRef.child("piece1").setValue("");
        roomRef.child("piece2").setValue("");
        roomRef.child("turn").setValue(2 - playerIndex);
    }

    @Override
    public void onFFGame() {
        //Need to inform other players that we loose
        String mes_start    = "Vous avez abandonné";
        String mes_mid      = "";
        String mes_end      = "";

        List<Player> playersWin = new ArrayList<>();
        for (Player p : players) {
            if (!p.isAlly(currentPlayer)) {
                playersWin.add(p);
            }
        }

        if (playersWin.size() == 1) {
            //mes_start = this.context.getString(R.string.finish_screen_start);
            mes_mid = playersWin.get(0).getPseudo();
            mes_end = this.context.getString(R.string.finish_screen_winnerOne_end);
        } else {
            //mes_start = this.context.getString(R.string.finish_screen_start);

            String res = "";
            for (Player p : playersWin) {
                res += p.getPseudo() + "-";
            }
            StringBuffer sb = new StringBuffer(res);
            sb.deleteCharAt(sb.length() - 1);

            mes_mid = sb.toString();
            mes_end = this.context.getString(R.string.finish_screen_winnerMany_end);
        }
        this.gameStopped = true;
        this.board.onEndOfGame(mes_start, mes_mid, mes_end);

        roomRef.child("loose").setValue("yes");
        roomRef.child("turn").setValue(2 - playerIndex);
        this.performHistoryLooser();
    }

    public void winByFF(){
        String mes_start    = "Vous avez gagné";
        String mes_mid      = "";
        String mes_end      = "";

        List<Player> playersWin = new ArrayList<>();
        for (Player p : players) {
            if (!p.isAlly(currentPlayer)) {
                playersWin.add(p);
            }
        }

        if (playersWin.size() == 1) {
            //mes_start = this.context.getString(R.string.finish_screen_start);
            mes_mid = playersWin.get(0).getPseudo();
            mes_end = "a abandonné";
        } else {
            //mes_start = this.context.getString(R.string.finish_screen_start);

            String res = "";
            for (Player p : playersWin) {
                res += p.getPseudo() + "-";
            }
            StringBuffer sb = new StringBuffer(res);
            sb.deleteCharAt(sb.length() - 1);

            mes_mid = sb.toString();
            mes_end = "ont abandonné";
        }
        this.gameStopped = true;
        this.board.onEndOfGame(mes_start, mes_mid, mes_end);
        this.performHistoryWinner();
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
