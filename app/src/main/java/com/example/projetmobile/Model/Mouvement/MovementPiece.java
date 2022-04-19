package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;

import java.util.ArrayList;
import java.util.List;

public class MovementPiece extends Movement<Case>{

    protected int cpt_mvt_performed;
    protected boolean canStillMove;

    public MovementPiece(Action<Case> action, Position start, Position incrementation, int cpt_mvt_performed, boolean canStillMove) {
        super(action, start, incrementation);
        this.cpt_mvt_performed = cpt_mvt_performed;
        this.canStillMove = canStillMove;
    }

    public MovementPiece(Action<Case> action, Position start, Position incrementation) {
        this(action,start,incrementation,-1,true);
    }

    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();
        int cpt_dep = this.cpt_mvt_performed;
        Position p = start.addAndReturn(new Position());

        while (b.isGoodPos(p.getX(),p.getY()) && (cpt_dep != 0)){
            p = p.addAndReturn(incrementation);
            Action.ActionState ac = action.isValidated(b.getACase(p.getX(),p.getY()));

            if(Action.ActionState.VALID == ac || (canStillMove && ac == Action.ActionState.STILLGOOD)){
                res.add(p);
                if(Action.ActionState.VALID == ac){
                    break;
                }
            }else{
                break;
            }
            cpt_dep --;
        }
        return res;
    }
}
