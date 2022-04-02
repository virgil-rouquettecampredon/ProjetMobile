package com.example.projetmobile.Model;

import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player implements GameObject{
    private String pseudo;
    private Map<Piece, List<Position>> piecesPlayer;

    private List<Mouvement<?>> possibleMouvements;

    public Player(String pseudo){
        this.piecesPlayer = new HashMap<>();
        this.pseudo = pseudo;
        possibleMouvements = new ArrayList<>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public List<Piece> getPiecesPlayer() {
        return new ArrayList<>(piecesPlayer.keySet());
    }

    public void addPiece(Piece p){
        this.piecesPlayer.put(p, new ArrayList<>());
    }

    public void removePiece(Piece p){
        this.piecesPlayer.remove(p);
    }
    
    public void resetPossibleMoove(){
        for (Piece p:piecesPlayer.keySet()) {
            piecesPlayer.get(p).clear();
        }
    }

    public void playATurn(){}

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

    public boolean isAlly(Player p){
        return p == this;
    }

    public void setPossibleMoove(Piece p, List<Position> pos){
        piecesPlayer.replace(p,pos);
    }

    public List<Position> getPositionsPiece(Piece p){
        List<Position> res = piecesPlayer.get(p);
        return (res == null)? new ArrayList<>() : res;
    }

    @Override
    public String toString() {
        String res = "Player{ pseudo='" + pseudo + '\'' + ", piecesPlayer=";
        for (Piece p : piecesPlayer.keySet()) {
            res+= p + " : " + piecesPlayer.get(p).size() + ", ";
        }
        res+=", possibleMouvements=";
        for (Mouvement<?> mvt :possibleMouvements) {
            res+= ""+ mvt + ", ";
        }
        return  res + '}';
    }
}
