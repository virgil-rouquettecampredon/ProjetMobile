package com.example.projetmobile.Model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import com.example.projetmobile.Model.Mouvement.ActionDeplacement;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Bishop;
import com.example.projetmobile.Model.Pieces.King;
import com.example.projetmobile.Model.Pieces.Knight;
import com.example.projetmobile.Model.Pieces.Pawn;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Pieces.Queen;
import com.example.projetmobile.Model.Pieces.Tower;
import com.example.projetmobile.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GameManager {
    //For console printing only
    private static boolean DEBUG_FOR_ONCLK = false;
    private static boolean DEBUG_FOR_GAME_LOGIC = true;
    private static boolean DEBUG_FOR_GAME_MENACE = false;

    //Board of the current game
    private Board board;
    private Context context;
    private List<Player> players;
    private Player currentPlayer;

    private List<Position> lastPossiblePositions;
    private Piece lastPieceSelected;
    private Position lastPosOfPieceSelected;
    private Position lastPosPreSelected;

    //For player overlay
    private List<TextView> playerNames;
    private List<LinearLayout> playerCimetaries;
    private int piece_size = -1;

    private int nbTurn = 0;

    //For getting all shot from all players
    Deque<Shot> allShots;

    public GameManager(Context context, Board b){
        this.board = b;
        this.context = context;
        this.players = new ArrayList<>();
        this.lastPossiblePositions = new ArrayList<>();

        this.playerNames = new ArrayList<>();
        this.playerCimetaries = new ArrayList<>();

        this.allShots = new ArrayDeque<>();
    }


    /** ======== For launch and play a game ======== **/
    public void start(){
        if(DEBUG_FOR_GAME_LOGIC) System.out.println("GAME MANAGER START");

        //Clear the board
        this.board.clear();

        //Init the board
        this.players = this.board.initGame_players();
        majPlayerLayout();

        if(startANewTurn()){
            onStopGame();
        }else{
            computeOnclkListener();
        }
    }
    public void computeOnclkListener(){
        if(DEBUG_FOR_ONCLK)System.out.println("COMPUTE ONCLK LISTENER");

        board.initGame_onclk(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DEBUG_FOR_ONCLK) System.out.println("<==============> NEW ONCLCK DETECTION <==============>");

                Case c = (Case) view;
                if(DEBUG_FOR_ONCLK) System.out.println("ONCLK ON : " + c);
                Piece p = c.getPiece();
                if(DEBUG_FOR_ONCLK) System.out.println("ONCLK PIECE : " + p);

                //If the case was selected the round just before by the player
                if(c.isPre_selected_pos()){
                    //Perform the mouvement
                    if(DEBUG_FOR_ONCLK) System.out.println("PERFORM MOUVEMENT");
                    mooveAPiece(lastPosOfPieceSelected,lastPosPreSelected, true);
                    board.setPossiblePreSelectedPos(lastPosPreSelected.getX(),lastPosPreSelected.getY(),false);
                    lastPosPreSelected = null;

                    //Start a new turn
                    if(startANewTurn()){
                        //If its finished, then stop the treatment
                        onStopGame();
                    }
                }else{
                    if(lastPosPreSelected != null) board.setPossiblePreSelectedPos(lastPosPreSelected.getX(),lastPosPreSelected.getY(),false);
                    //Then it could be a possible mouvement case
                    if(c.isPossible_pos()){
                        //Then this case is transformed on a pre-selected case
                        if(DEBUG_FOR_ONCLK) System.out.println("SET PRESELECTED POSITION");
                        lastPosPreSelected = new Position(c.getColumn(),c.getRow());
                        board.setPossiblePreSelectedPos(lastPosPreSelected.getX(),lastPosPreSelected.getY(),true);

                        for (Position pos: lastPossiblePositions) {
                            board.setPossiblePos(pos.getX(),pos.getY(),false);
                        }
                        lastPossiblePositions.clear();
                    }else{
                        //Then this case may just contain a piece on it
                        //Delete precedent position seen
                        for (Position pos: lastPossiblePositions) {
                            board.setPossiblePos(pos.getX(),pos.getY(),false);
                        }
                        lastPossiblePositions.clear();

                        if(p!=null){
                            if(DEBUG_FOR_ONCLK) System.out.println("CLICK ON A PIECE IN THE BOARD");
                            //Click on a piece on the board
                            //Send new positions player wanted to see
                            List<Position> posPos = currentPlayer.getPositionsPiece(p);
                            for (Position pos: posPos) {
                                lastPossiblePositions.add(pos);
                                board.setPossiblePos(pos.getX(),pos.getY(),true);
                            }

                            //For save an historic of precedents choices
                            lastPieceSelected = p;
                            lastPosOfPieceSelected = new Position(c.getColumn(),c.getRow());
                        }
                    }
                }
                //Draw the changes
                board.commitChanges();
            }
        });
    }
    protected boolean startANewTurn() {
        this.currentPlayer = getCurrentPlayer();

        //We create all the movement for all the players
        for (Player p : players) {
            computePossibleMvts(p);
        }

        //And we restrict the current player movements if he is in danger
        performMenaced(this.currentPlayer);

        return this.isFinished();
    }

    /** ======== For game stoping mecanics ======== **/
    public boolean isFinished(){
        //Detect if current player can still perform at least one movement
        for (Piece p : currentPlayer.getPiecesPlayer()) {
            if(currentPlayer.getPositionsPiece(p).size()>0) return false;
        }
        return true;
    }
    public void onStopGame(){
        if(DEBUG_FOR_GAME_LOGIC) System.out.println("GAME IS FINISHED");

        String mes_start = "";
        String mes_mid = "";
        String mes_end = "";

        //Perform the end of the game
        if(this.isMenaced(this.currentPlayer)){
            List<Player> playersWin = new ArrayList<>();
            for (Player p : players) {
                if(!p.isAlly(currentPlayer)){
                    playersWin.add(p);
                }
            }
            if(playersWin.size() == 1) {
                mes_start = this.context.getString(R.string.finish_screen_start);
                mes_mid = playersWin.get(0).getPseudo();
                mes_end = this.context.getString(R.string.finish_screen_winnerOne_end);
            }else{
                mes_start = this.context.getString(R.string.finish_screen_start);

                String res = "";
                for (Player p : playersWin) {
                    res += p.getPseudo() + "-";
                }
                StringBuffer sb = new StringBuffer(res);
                sb.deleteCharAt(sb.length()-1);

                mes_mid = sb.toString();
                mes_end = this.context.getString(R.string.finish_screen_winnerMany_end);
            }
        }else{
            if(DEBUG_FOR_GAME_LOGIC) System.out.println("EQUALITY");
            mes_start = this.context.getString(R.string.finish_screen_start);
            mes_mid = "";
            mes_end = this.context.getString(R.string.finish_screen_draw_end);
        }
        this.board.endOfGame(mes_start, mes_mid,mes_end);
    }
    private boolean isMenaced(Player playerCurrent){
        //System.out.println("  <--> IS MENACED <--> ");
        for (Piece piece: playerCurrent.getPiecesPlayer()) {
            if (piece.isVictoryCondition()){
                //System.out.println("    MENACED : " + piece);
                for (Player p: players) {
                    if(!playerCurrent.isAlly(p)){
                        //System.out.println("    PEOPLE WHO CAN MENACE ME : " + p);
                        for (Piece pieceEnnemy: p.getPiecesPlayer()) {
                            for (Piece eatable: pieceEnnemy.getTastyPieces()) {
                                if (eatable == piece){
                                    //System.out.println("    WITH THE PIECE : " + pieceEnnemy);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private void performMenaced(Player p){
        if(DEBUG_FOR_GAME_MENACE) System.out.println("JE SUIS MENACE");

        //Get all current pos of pieces on the board
        HashMap<Piece, Position> posPiecesStart = new HashMap<>();
        for (Piece piece: p.getPiecesPlayer()) {
            posPiecesStart.put(piece,this.board.getPiecePosition(piece));
        }

        if(DEBUG_FOR_GAME_MENACE) {
            System.out.println("HASHMAP");
            for (Piece phm : posPiecesStart.keySet()) {
                System.out.println(phm);
                System.out.println(posPiecesStart.get(phm));
            }
        }

        HashMap<Piece, List<Position>> posPieces = new HashMap<>();
        for (Piece piece: p.getPiecesPlayer()) {
            posPieces.put(piece,new ArrayList<>());
            for (Position pos : p.getPositionsPiece(piece)) {
                posPieces.get(piece).add(pos);
            }
        }

        if(DEBUG_FOR_GAME_MENACE) {
            System.out.println("POSITIONS A TESTER");
            for (Piece piece : posPieces.keySet()) {
                List<Position> positions = posPieces.get(piece);
                System.out.println("PIECE : " + piece);
                if (positions != null) {
                    for (Position pos : positions) {
                        System.out.println("POS : " + pos);
                    }
                }
            }
        }

        if(DEBUG_FOR_GAME_MENACE) System.out.println("<===> START THE TREATMENT");

        for (Piece piece : posPieces.keySet()) {
            List<Position> positions = posPieces.get(piece);
            if (positions != null) {
                Iterator<Position> ite = positions.iterator();
                while(ite.hasNext()){
                    Position pos = ite.next();
                    if(DEBUG_FOR_GAME_MENACE) {
                        System.out.println("=>CURRENT POS SEEN : " + pos);
                        System.out.println("FROM PIECE : " + piece);
                    }

                    //We perform the movement
                    Position startPos = posPiecesStart.get(piece);
                    if(DEBUG_FOR_GAME_MENACE) System.out.println("START AT : " + startPos);


                    this.mooveAPiece(startPos,pos,false);

                    for (Player gamePlayer : players) {
                        computePossibleMvts(gamePlayer);
                    }

                    //Now check if there is still a chess state
                    if(this.isMenaced(p)){
                        //Then we can't move this piece to this position
                        //System.out.println("REMOVE IT");
                        if(DEBUG_FOR_GAME_MENACE)System.out.println("    => STILL MENACED");
                        ite.remove();
                    }

                    Shot s = cancelAShot();
                    if(DEBUG_FOR_GAME_MENACE) System.out.println(s);
                }
            }
        }
        if(DEBUG_FOR_GAME_MENACE) {
            System.out.println("POSITIONS RESTANTES");
            for (Piece piece : posPieces.keySet()) {
                List<Position> positions = posPieces.get(piece);
                System.out.println("PIECE : " + piece);
                if (positions != null) {
                    System.out.println("ON A DES POSITIONS");
                    for (Position pos : positions) {
                        System.out.println("POS : " + pos);
                    }
                }
            }
        }

        //Now we can update all the possible move from all players
        for (Player gamePlayer : players) {
            computePossibleMvts(gamePlayer);
        }
        p.resetPossibleMoove();

        //And after for all our possible movement calculated before for our menaced player
        for (Piece piece : posPieces.keySet()) {
            List<Position> positions = posPieces.get(piece);
            if (positions != null) {
                p.setPossibleMoove(piece,positions);
            }
        }
    }


    /** ======== For movements computation ======== **/
    //For perform a movement on the board
    public void mooveAPiece(Position start, Position end, boolean majLayouts){
        //Moove a piece form start position to end position

        //System.out.println("[MOVE A PIECE]");
        Case start_case = board.getACase(start.getX(),start.getY());
        Case end_case = board.getACase(end.getX(),end.getY());

        //System.out.println("FROM THIS PLACE : " + start_case);
        //System.out.println("TO THIS PLACE : " + end_case);

        Piece mooved = start_case.getPiece();
        Piece possibly_eaten = end_case.getPiece();

        //System.out.println("PIECE START : " + mooved);
        //System.out.println("PIECE END : " + possibly_eaten);

        allShots.push(new Shot(mooved,start,end,possibly_eaten, majLayouts, mooved.isMoovedYet()));

        if(possibly_eaten != null){
            //If there is a  piece at the direction of displacement, its eaten
            Player pEated = eatAPiece(possibly_eaten);

            //We MAJ the cimetary visual after
            if(pEated !=null && majLayouts){
                addCimetaryLayout(pEated,possibly_eaten);
            }
        }
        board.setAPieces(start_case.getColumn(),start_case.getRow(),null);
        board.setAPieces(end_case.getColumn(),end_case.getRow(),mooved);
        mooved.setMooved(true);
    }
    //For canceling the last shot performed on the game
    public Shot cancelAShot(){
        Shot s = allShots.pop();
        //If the last shot was to eat a piece
        if (s.getEatedPiece()!=null){
            Player p = reviveAPiece(s.getEatedPiece());
            //If we also maj cimetary layout
            if (p!= null && s.isMajAff()){
                popCimetaryLayout(s.getEatedPiece().getPocessor());
            }
        }

        //Cancel the moove
        this.board.setAPieces(s.getStartPos().getX(),s.getStartPos().getY(),s.getPieceConcerned());
        this.board.setAPieces(s.getEndPos().getX(),s.getEndPos().getY(),s.getEatedPiece());
        s.getPieceConcerned().setMooved(s.isFirstMoove());
        return s;
    }
    //For perform the in/out piece mecanism on the board
    public Player eatAPiece(Piece piece){
        Player p =  getPlayer(piece);
        if(p!=null){
            p.killAPiece(piece);
        }
        return p;
    }
    public Player reviveAPiece(Piece piece){
        Player p =  getPlayer(piece);
        if(p!=null){
            p.reviveAPiece(piece);
        }
        return p;
    }
    //Computing the possible movement that can perform the pieces of the player
    public void computePossibleMvts(Player p){
        //System.out.println("COMPUTE POS MOOVE : " + p);
        p.resetPossibleMoove();
        List<Piece> pieces_player = p.getPiecesPlayer();

        for (Piece p_player: pieces_player) {
            //Dont forget to clear that list for reset the menace
            p_player.clearTastyPieces();

            Position piece_pos = board.getPiecePosition(p_player);
            List<Mouvement<?>> mvt_pp = p_player.getAllPossibleMvt(piece_pos.getX(),piece_pos.getY());

            List<Position> positions_piece = new ArrayList<>();
            for (Mouvement<?> m : mvt_pp) {
                //System.out.println("MVT : " + m);
                positions_piece.addAll(m.getAllPositions(this.board));
            }
            p.setPossibleMoove(p_player,positions_piece);
        }
        //System.out.println("=== END COMPUTE POS MOOVE ===");
    }


    /** ======== For launch and play a game ======== **/
    //Get the ind of a player in the game
    private int getIndice(Player p){
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).equals(p)) return i;
        }
        return -1;
    }
    //Add piece to cimatary player p layout
    private void addCimetaryLayout(Player p, Piece piece){
        //System.out.println("GM : MAJ CIMETARIES = " + p);
        //System.out.println("GM : MAJ CIMETARIES = " + piece);
        int ind = getIndice(p);
        if(ind>=0){
            //System.out.println("CREATE IMG VIEW : " + ind);

            if(piece_size<0) piece_size = this.playerCimetaries.get(ind).getWidth()/(1*p.getPiecesPlayer().size());

            ImageView img = new ImageView(this.context);
            img.setImageDrawable(piece.getAppearences());

            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(piece_size, piece_size);
            img.setLayoutParams(ll);

            this.playerCimetaries.get(ind).addView(img);
            this.playerCimetaries.get(ind).requestLayout();
        }
    }
    //Remove last elem from cimatary player p layout
    private void popCimetaryLayout(Player p){
        int ind = getIndice(p);
        if(ind>=0){
            int childcount = this.playerCimetaries.get(ind).getChildCount();
            this.playerCimetaries.get(ind).removeView(this.playerCimetaries.get(ind).getChildAt(childcount-1));
        }
    }
    public void majPlayerLayout(){
        //System.out.println("MAJ PLAYER LAYOUT");
        for (int i = 0; i < playerNames.size(); i++) {
            Player p = players.get(i);
            this.playerNames.get(i).setText(p.getPseudo());
        }
    }
    public void addPlayerInterfaceElement(TextView player_name, LinearLayout player_cimetary) {
        this.playerNames.add(player_name);
        this.playerCimetaries.add(player_cimetary);
    }


    /** ======== Utilities functions ======== **/
    //Get a player who get the Piece piece
    private Player getPlayer(Piece piece){
        /*for (Player p: players) {
            for (Piece pp: p.getPiecesPlayer()) {
                if(pp.equals(piece)){
                    return p;
                }
            }
        }
        return null;*/
        return piece.getPocessor();
    }
    //For getting the current player to play
    public Player getCurrentPlayer(){
        Player p = this.players.get(nbTurn%this.players.size());
        nbTurn++;
        return p;
    }
    //Get Pixel value of dp var (for canva drawing)
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    //Get Color attribute from reference context (for canva drawing)
    public static int getAttributeColor(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        int colorRes = typedValue.resourceId;
        int color = -1;
        try {
            color = ContextCompat.getColor(context,colorRes);
        } catch (Resources.NotFoundException e) {
            System.out.println("=====> ERROR : Not found color resource by id: " + colorRes);
        }
        return color;
    }
}
