package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.Pieces.Piece;

public class ActionRock implements Action<Case>{
    private Piece p;

    public ActionRock(Piece p) {
        this.p = p;
    }

    @Override
    public ActionState isValidated(Case c) {
        if(c == null) return ActionState.INVALID;
        if (c.getPiece() == null){
            return ActionState.STILLGOOD;
        }else{
            if(c.getPiece() != p){
                return ActionState.INVALID;
            }else{
                if(p.isMovedYet()){
                    return ActionState.INVALID;
                }else {
                    return ActionState.VALID;
                }
            }
        }
    }

}
