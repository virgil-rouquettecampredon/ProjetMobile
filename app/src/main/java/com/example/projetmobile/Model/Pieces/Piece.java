package com.example.projetmobile.Model.Pieces;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements GameObject, Cloneable{
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

    public Object clone()  {
        Piece p = null;
        try {
            p = (Piece) super.clone();
            p.moovedYet = this.moovedYet;
            p.pocessor = this.pocessor;
            p.victory = this.victory;
            p.appearence = this.appearence;
            p.tastyPieces = this.tastyPieces;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
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


    /**Set that a piece has mooved**/
    public void setMooved(){
        this.moovedYet = true;
    }

    public Player getPocessor() {
        return pocessor;
    }

    public boolean isVictoryCondition(){
        return this.victory;
    }

    /** TASTYPIECES Gestion **/
    public List<Piece> getTastyPieces() {
        return tastyPieces;
    }

    public void clearTastyPieces(){
        tastyPieces = new ArrayList<>();
    }
}

