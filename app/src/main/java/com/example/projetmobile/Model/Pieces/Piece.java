package com.example.projetmobile.Model.Pieces;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements GameObject{
    public enum DIRECTION{
        UP,DOWN,LEFT,RIGHT
    }

    protected boolean moovedYet;
    protected ComposedDrawing appearence;
    protected Player pocessor;
    protected boolean victory;
    private List<Piece> tastyPieces;

    public Piece(Player p){
        this.moovedYet = false;
        this.pocessor = p;
        this.victory = false;
        appearence = new ComposedDrawing();
        tastyPieces = new ArrayList<>();
    }

    /**Return the visual appearence of a piece
     * @return return all Drawables needed to be drawn**/
    public ComposedDrawing getAppearences(){
        return appearence;
    }

    /**Get all the possible mouvement that a piece can perform
     * @param col  : column since the piece start to compute mvt
     * @param row  : row since the piece start to compute mvt
     * @return list of mouvements that the piece can achieve**/
    public abstract List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row);

    /**Set that a piece has mooved
     * @param b : value to set to the current moving detector of the piece**/
    public void setMooved(boolean b){
        this.moovedYet = b;
    }

    /**Get the pocessor of a piece
     * @return : Player that control the piece this**/
    public Player getPocessor() {
        return pocessor;
    }

    /**Get if the piece is a victory condition piece
     * @return : Player that control the piece this**/
    public boolean isVictoryCondition(){
        return this.victory;
    }

    /**Get all the piece that this can eat currently
     * @return List of tasty pieces**/
    public List<Piece> getTastyPieces() {
        return tastyPieces;
    }

    /**Get the value of the displacement of a piece**/
    public boolean isMoovedYet() {
        return moovedYet;
    }

    /**Clear all the pieces that this can eat currently**/
    public void clearTastyPieces(){
        tastyPieces = new ArrayList<>();
    }
}

