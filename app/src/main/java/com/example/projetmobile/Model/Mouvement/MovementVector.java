package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MovementVector extends Movement<Case> {

    public MovementVector(Action<Case> action, Position start, Position incrementation) {
        super(action, start, incrementation);
    }

    @Override
    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();
        Position p = start.addAndReturn(new Position());

        //System.out.println("==============> GET ALL GOOD POS VECTOR");
        //System.out.println("POS VUE : " + p);
        while (b.isGoodPos(p.getX(),p.getY())){
            p = p.addAndReturn(incrementation);
            Action.ActionState ac = action.isValidated(b.getACase(p.getX(),p.getY()));
            //System.out.println("RES : " + ac);
            if(Action.ActionState.VALID == ac || ac == Action.ActionState.STILLGOOD){
                 res.add(p);
                 if(Action.ActionState.VALID == ac){
                     break;
                 }
            }else{
                break;
            }
        }
        //System.out.println("==============> END GET ALL GOOD POS VECTOR");
        return res;
    }
}
