package com.example.projetmobile.Model.Pieces;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.MovementComplex;
import com.example.projetmobile.Model.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements GameObject{
    public enum DIRECTION{
        UP,DOWN,LEFT,RIGHT
    }

    protected boolean movedYet;
    protected ComposedDrawing appearance;
    protected Player possessor;
    private List<Piece> tastyPieces;

    protected Piece lastShape;
    private boolean deapCloneOnGame;

    public Piece(Player p){
        this.movedYet = false;
        this.possessor = p;
        appearance = new ComposedDrawing();
        tastyPieces = new ArrayList<>();
    }
    public Piece(boolean deapCloneOnGame, Piece p){
        this.movedYet = p.movedYet;
        this.possessor = p.possessor;

        appearance = new ComposedDrawing();
        tastyPieces = new ArrayList<>();

        lastShape = p;
        this.deapCloneOnGame = deapCloneOnGame;

        //This case is commonly used to create another piece based on a precedent one
        //The transform mechanism for example
        if(deapCloneOnGame) {
            //this take the place of p for the player
            p.possessor.destroyAPiece(p);
            p.possessor.addPiece(this);
        }
    }

    /**Return the visual appearance of a piece
     * @return return all Drawables needed to be drawn**/
    public ComposedDrawing getAppearances(){
        return appearance;
    }

    /**Get all the possible movement that a piece can perform
     * @param col  : column since the piece start to compute mvt
     * @param row  : row since the piece start to compute mvt
     * @return list of movements that the piece can achieve**/
    public abstract List<Movement<? extends GameObject>> getAllPossibleMvt(int col, int row);

    /**Get all the possible complex movement that a piece can perform
     * @param col  : column since the piece start to compute mvt
     * @param row  : row since the piece start to compute mvt
     * @return list of complex movements that the piece can achieve**/
    public List<MovementComplex> getAllPossibleComplexMvt(int col, int row){
        return new ArrayList<MovementComplex>();
    }


    /**Set that a piece has moved
     * @param b : value to set to the current moving detector of the piece**/
    public void setMoved(boolean b){
        this.movedYet = b;
    }

    /**Get the possessor of a piece
     * @return : Player that control the piece this**/
    public Player getPossessor() {
        return possessor;
    }

    /**Get if the piece is a victory condition piece**/
    public boolean isVictoryCondition(){
        return false;
    }

    /**Get all the piece that this can eat currently
     * @return List of tasty pieces**/
    public List<Piece> getTastyPieces() {
        return tastyPieces;
    }

    /**Get the value of the displacement of a piece**/
    public boolean isMovedYet() {
        return movedYet;
    }

    /**Clear all the pieces that this can eat currently**/
    public void clearTastyPieces(){
        tastyPieces = new ArrayList<>();
    }

    /**Say if a piece can be transformed or not**/
    public boolean canBeTransformed(){
        return false;
    }

    /**Get the last shape of a piece, just before its get cloned**/
    public Piece getLastShape() {
        return lastShape;
    }

    /**Get back to the precedent shape for a piece**/
    public void getBackPrecedentShape(){
        if(lastShape != null) {
            //This case is commonly used to create another piece based on a precedent one
            //The transform mechanism for example
            if (this.deapCloneOnGame) {
                //this take the place of p for the player
                this.possessor.destroyAPiece(this);
                this.possessor.addPiece(lastShape);
            }

            //Then we transform this to its last shape
            this.movedYet = lastShape.movedYet;
            this.possessor = lastShape.possessor;
            this.appearance = lastShape.appearance;
            this.tastyPieces = new ArrayList<>();
            this.lastShape = lastShape.lastShape;
            this.deapCloneOnGame = lastShape.deapCloneOnGame;
        }
    }
}