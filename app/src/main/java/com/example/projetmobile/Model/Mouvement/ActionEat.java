package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Player;

public class ActionEat implements Action<Case>{
    private Piece piece;

    public ActionEat(Piece p){
        this.piece = p;
    }

    public ComposedDrawing getGraphicVisualisation(){
        ComposedDrawing comp = new ComposedDrawing();
        return comp;
    }

    @Override
    public ActionState isValidated(Case c) {
        if(c == null) return ActionState.INVALID;
        Player p = this.piece.getPocessor();
        if(c.getPiece() != null){
            if(!p.isAlly(c.getPiece().getPocessor())){
                //We can eat
                //We add new TastyPieces on the current action eat pieces
                this.piece.getTastyPieces().add(c.getPiece());
                return ActionState.VALID;
            }else{
                //We cant eat an ally
                return ActionState.INVALID;
            }
        }else{
            //We dont know if the piece can eat because case is empty
            //But for vectorial displacement, its important to continu the traitement
            //And not stop at the first empty case because we cant eat here
            return ActionState.STILLGOOD;
        }
    }

    @Override
    public String toString() {
        return "EAT";
    }
}
