package com.example.projetmobile.Model;

import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Player implements GameObject{

    private String pseudo;
    private Map<Piece, List<Position>> piecesPlayer;

    private Map<Piece, List<Association_rock>> rockPieces;

    private List<Piece> cimetary;
    private int color;


    public Player(String pseudo,int color){
        this.piecesPlayer = new HashMap<>();
        this.rockPieces = new HashMap<>();

        this.pseudo = pseudo;
        this.color = color;
        this.cimetary = new ArrayList<>();
    }

    public boolean isAlly(Player p){
        return p == this;
    }
    public int getColor() {
        return color;
    }
    public String getPseudo() {
        return pseudo;
    }


    /** ======== Pieces management ======== **/
    public List<Piece> getPiecesPlayer() {
        return new ArrayList<>(piecesPlayer.keySet());
    }
    public void addPiece(Piece p){
        this.piecesPlayer.put(p, new ArrayList<>());
    }
    public void removePiece(Piece p){
        this.piecesPlayer.remove(p);
    }
    public void killAPiece(Piece p){
        this.removePiece(p);
        this.cimetary.add(p);
    }
    public void destroyAPiece(Piece p){
        this.piecesPlayer.remove(p);
        this.cimetary.remove(p);
    }
    public void reviveAPiece(Piece p){
        System.out.println("REVIVE A PIECE : " + p);

        Iterator<Piece> ite = cimetary.iterator();
        while (ite.hasNext()){
            Piece pDead = ite.next();
            if(pDead == p){
                ite.remove();
                addPiece(pDead);
            }
        }
    }
    public boolean isAlive(Piece p){
        for (Piece pdead :cimetary) {
            if(pdead == p) return false;
        }
        return true;
    }


    /** ======== Pieces Movement ======== **/
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


    /** ======== Rock Management ======== **/
    public void addRockPieces(Piece rock, Association_rock as){
        List<Association_rock> list = this.rockPieces.get(rock);
        if(list!=null){
            list.add(as);
        }else{
            List<Association_rock> l = new ArrayList<>();
            l.add(as);
            this.rockPieces.put(rock,l);
        }
    }
    public Set<Piece> getPiecesToRock(){
        return this.rockPieces.keySet();
    }
    public List<Association_rock> getAssoToRockWithPiece(Piece p){
        return this.rockPieces.get(p);
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
    @Override
    public String toString() {
        String res = "Player{ pseudo='" + pseudo + '\'' + ", piecesPlayer=";
        for (Piece p : piecesPlayer.keySet()) {
            res+= p + " : " + piecesPlayer.get(p).size() + ", ";
        }
        return  res + '}';
    }
}
