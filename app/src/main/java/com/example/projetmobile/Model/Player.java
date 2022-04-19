package com.example.projetmobile.Model;

import android.util.Pair;

import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.MovementComplex;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Player implements GameObject{
    public class Association{
        Boolean isAlive;
        Map<MovementComplex,List<Position>> positionsForAComplexMovement;

        public Association(Boolean isAlive, Map<MovementComplex, List<Position>> positionsForAComplexMovement) {
            this.isAlive = isAlive;
            this.positionsForAComplexMovement = positionsForAComplexMovement;
        }
    }

    private String pseudo;
    private Map<Piece, List<Position>> piecesPlayer;

    private Map<Piece, Association> piecesComplexPlayer;

    private List<Piece> cimetary;
    private int color;


    public Player(String pseudo,int color){
        this.piecesPlayer = new HashMap<>();
        this.piecesComplexPlayer = new HashMap<>();

        this.pseudo = pseudo;
        this.color = color;
        this.cimetary = new ArrayList<>();
    }

    public boolean isAlly(Player p){
        return p == this;
    }

    /** ======== Pieces management (Movements) ======== **/
    public List<Piece> getPiecesPlayer() {
        return new ArrayList<>(piecesPlayer.keySet());
    }
    public void addPiece(Piece p){
        this.piecesPlayer.put(p, new ArrayList<>());
        Association a = this.piecesComplexPlayer.get(p);
        if(a!=null){
            a.isAlive = true;
        }else{
            this.piecesComplexPlayer.put(p,new Association(true,new HashMap<>()));
        }
    }
    public void removePiece(Piece p){
        this.piecesPlayer.remove(p);
        Association a = this.piecesComplexPlayer.get(p);
        if(a!=null){
            a.isAlive = false;
        }
    }
    public void killAPiece(Piece p){
        this.removePiece(p);
        this.cimetary.add(p);
    }
    public void destroyAPiece(Piece p){
        this.piecesPlayer.remove(p);
        this.piecesComplexPlayer.remove(p);
        this.cimetary.remove(p);
    }
    public void reviveAPiece(Piece p){
        Iterator<Piece> ite = cimetary.iterator();
        while (ite.hasNext()){
            Piece pDead = ite.next();
            if(pDead == p){
                ite.remove();
                addPiece(pDead);
            }
        }
    }
    public void resetPossibleMove(){
        for (Piece p:piecesPlayer.keySet()) {
            piecesPlayer.get(p).clear();
        }
    }
    public void setPossibleMove(Piece p, List<Position> pos){
        piecesPlayer.replace(p,pos);
    }
    public List<Position> getPositionsPiece(Piece p){
        List<Position> res = piecesPlayer.get(p);
        return (res == null)? new ArrayList<>() : res;
    }
    public int getColor() {
        return color;
    }

    //For complex movements structure
    public void resetPossibleComplexMove(){
        for (Piece p : piecesComplexPlayer.keySet()) {
            piecesComplexPlayer.get(p).positionsForAComplexMovement.clear();
        }
    }
    public void setPossibleMoveComplex(Piece p,MovementComplex m, List<Position> pos){
        Association a = piecesComplexPlayer.get(p);
        if(a!=null){
            a.positionsForAComplexMovement.replace(m,pos);
        }
    }
    public List<Pair<MovementComplex, List<Position>>> getPositionsPieceComplexMovement(Piece p){
        List<Pair<MovementComplex, List<Position>>> res = new ArrayList<>();
        Association a = piecesComplexPlayer.get(p);
        if(a!=null){
            for (MovementComplex mv: a.positionsForAComplexMovement.keySet()) {
                res.add(new Pair<>(mv,a.positionsForAComplexMovement.get(mv)));
            }
        }
        return res;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }

        Player p = (Player) o;
        return this.pseudo.equals(p.pseudo);
    }
    public String getPseudo() {
        return pseudo;
    }
    @Override
    public String toString() {
        String res = "Player{ pseudo='" + pseudo + '\'' + ", piecesPlayer=";
        for (Piece p : piecesPlayer.keySet()) {
            res+= p + " : " + piecesPlayer.get(p).size() + ", ";
        }
        return  res + '}';
    }
}
