package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.Shot;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MovementEnPassant extends MovementComplex{

    public MovementEnPassant(Action<Case> action, Position start, Position incrementation) {
        super(action, start, incrementation);
    }

    @Override
    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();
        Position p = start.addAndReturn(incrementation);
        Action.ActionState ac = action.isValidated(b.getACase(p.getX(),p.getY()));
        if(Action.ActionState.VALID == ac){
            res.add(p);
        }

        return res;
    }

    @Override
    public List<Shot> onPostMovement(Board b) {
        List<Shot> res = new ArrayList<>();

        return res;
    }
}
