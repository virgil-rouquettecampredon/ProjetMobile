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


    public Piece(Player p){
        this.moovedYet = false;
        this.pocessor = p;
        appearence = new ComposedDrawing();
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
}

