package com.example.projetmobile.Model;

import android.content.Context;

import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

import java.util.ArrayList;
import java.util.List;

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

    public GameManagerOnline(Context context, Board b) {
        super(context, b);

        shotsToPush = new ArrayList<>();
        shotsToPerform = new ArrayList<>();
    }

    //Call once before game start
    public void setStartingPlayer(){
        playerIndex = 0;
    }

    //Function for changing player name
    public void setPlayerDatas(int id, String pseudo){
        Player p = players.get(id);
        p.setPseudo(pseudo);
        this.playersUI.get(id).getTVPseudo().setText(p.getPseudo());
    }

    public void setPlayerIndex(int id){
        playerIndex = id;
    }

    @Override
    public void start() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME MANAGER START");

        //Clear the board
        this.board.clear();

        //Init the board
        this.players = this.board.initGame_players();
        majPlayerLayout();

        this.currentPlayer = players.get(playerIndex);


        if (startANewTurn()) {
            onEndingGame();
        } else {
            computeOnclkListener();
        }
    }

    @Override
    protected boolean startANewTurn() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("<===> START A NEW TURN <===>");
        //if we were the last player to play, then MAJ db first with our play
        if(isMyTurn) {
            for (Shot s : this.allShots.peek()) {
                onCurrentPlayerPlay(new DB_Shots(s.getStartPos(),s.getEndPos(), s.getIDTransformedPiece()));
            }
            onFinishTurn();
        }


        //MAJ turn to play
        isMyTurn = (playerIndex == (nbTurn%this.players.size()));
        //Stop game if needed
        gameStopped = !isMyTurn;

        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME STATE : " + ((gameStopped)? "STOPPED" : "YOUR TURN !"));

        //Maj UI for better understanding in interface of current player
        majLayoutPlayerTurn(getCurrentPlayer());

        //if its not your turn, don't try to compute anything
        if(isMyTurn) {
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
        }

        //MAJ NUMBER OF TURNS
        nbTurn++;

        return this.isFinished();
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
                }
            }
        } else {
            if (DEBUG_FOR_GAME_LOGIC) System.out.println("+NO SHOT TO PERFORM+");
        }

        shotsToPerform.clear();
        //Start a new turn
        if (startANewTurn()) {
            //If its finished, then stop the treatment
            onEndingGame();
        }
    }

    //Function called when current enemy player play a turn
    public void onEnemyPlayerPlay(Position startPosPlayerPlay, Position endPosPlayerPlay, int IDTransformedPiece){
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("ENEMY PLAYER PLAY A SHOT");
        shotsToPerform.add(new DB_Shots(startPosPlayerPlay,endPosPlayerPlay,IDTransformedPiece));
    }

    //Function called when our current player play a turn
    public void onCurrentPlayerPlay(DB_Shots d){
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("PLAYER PLAY A TURN");

        //Perform DATA BASES LOGIC
        //Inform DB that a shot has been played

        shotsToPush.add(d);
    }

    //Function called to end a turn and sync datas to other players
    public void onFinishTurn(){
        //Perform DATA BASES LOGIC on shotsToPush
        //Inform all other players that the current player end his turn

        shotsToPush.clear();
    }

    //Function called when game over (victory if our player win)
    public void onGameFinished(boolean victory){
        // inform other player that this.currentPlayer ((victory)? won : loose)
    }
}
