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

import java.util.ArrayList;
import java.util.List;


public class GameManager {

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

    public GameManager(Context context, Board b){
        this.board = b;
        this.context = context;
        this.players = new ArrayList<>();
        this.lastPossiblePositions = new ArrayList<>();

        this.playerNames = new ArrayList<>();
        this.playerCimetaries = new ArrayList<>();
    }

    public void start(){
        System.out.println("GAME MANAGER START");
        this.players = this.board.initGame_players();
        majPlayerLayout();
        startANewTurn();
        computeOnclkListener();
    }

    public Player getCurrentPlayer(){
        Player p = this.players.get(nbTurn%this.players.size());
        nbTurn++;
        return p;
    }

    public void computePossibleMvts(Player p){
        //System.out.println("COMPUTE POS MOOVE : " + p);
        p.resetPossibleMoove();
        List<Piece> pieces_player = p.getPiecesPlayer();

        for (Piece p_player: pieces_player) {
            //System.out.println("CPT : " + p_player);

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

    public void computeOnclkListener(){
        System.out.println("COMPUTE ONCLK LISTENER");
        board.initGame_onclk(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("<==============> NEW ONCLCK DETECTION <==============>");

                Case c = (Case) view;
                System.out.println("ONCLK ON : " + c);
                Piece p = c.getPiece();
                System.out.println("ONCLK PIECE : " + p);

                //If the case was selected the round just before by the player
                if(c.isPre_selected_pos()){
                    //Perform the mouvement
                    System.out.println("PERFORM MOUVEMENT");
                    mooveAPiece(lastPosOfPieceSelected,lastPosPreSelected);
                    board.setPossiblePreSelectedPos(lastPosPreSelected.getX(),lastPosPreSelected.getY(),false);
                    lastPosPreSelected = null;

                    //If the game is not finished
                    if(!isFinished()) {
                        //then start a new turn
                        startANewTurn();
                    }

                }else{
                    if(lastPosPreSelected != null) board.setPossiblePreSelectedPos(lastPosPreSelected.getX(),lastPosPreSelected.getY(),false);
                    //Then it could be a possible mouvement case
                    if(c.isPossible_pos()){
                        //Then this case is transformed on a pre-selected case
                        System.out.println("SET PRESELECTED POSITION");
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
                            System.out.println("CLICK ON A PIECE IN THE BOARD");
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

    private boolean isMenaced(Player playerCurrent){
        for (Piece piece: playerCurrent.getPiecesPlayer()) {
            if (piece.isVictoryCondition()){
                for (Player p: players) {
                    if(!playerCurrent.isAlly(p)){
                        for (Piece pieceEnnemy: p.getPiecesPlayer()) {
                            for (Piece eatable: pieceEnnemy.getTastyPieces()) {
                                if (eatable == piece){
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

    public boolean isFinished(){

        return false;
    }

    public void mooveAPiece(Position start, Position end){
        //Moove a piece form start position to end position
        Case start_case = board.getACase(start.getX(),start.getY());
        Case end_case = board.getACase(end.getX(),end.getY());

        Piece mooved = start_case.getPiece();
        Piece possibly_eaten = end_case.getPiece();
        if(possibly_eaten != null){
            //If there is a  piece at the direction of displacement, its eaten
            eatAPiece(possibly_eaten);
        }
        board.setAPieces(start_case.getColumn(),start_case.getRow(),null);
        board.setAPieces(end_case.getColumn(),end_case.getRow(),mooved);
        mooved.setMooved();
    }

    public void eatAPiece(Piece piece){
        Player p =  getPlayer(piece);
        if(p!=null){
            p.removePiece(piece);
            majCimetaries(p,piece);
        }
    }

    //Maj cimataries
    private void majCimetaries(Player p, Piece piece){
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

    //Get a player who get the Piece piece
    private Player getPlayer(Piece piece){
        for (Player p: players) {
            for (Piece pp: p.getPiecesPlayer()) {
                if(pp.equals(piece)){
                    return p;
                }
            }
        }
        return null;
    }

    //Get the ind of a player in the game
    private int getIndice(Player p){
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).equals(p)) return i;
        }
        return -1;
    }

    protected void startANewTurn(){
        this.currentPlayer = getCurrentPlayer();
        for (Player p: players) {
            computePossibleMvts(p);
        }
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
}
