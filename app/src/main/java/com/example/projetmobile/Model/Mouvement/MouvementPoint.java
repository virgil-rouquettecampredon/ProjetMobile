package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.GameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MouvementPoint extends Mouvement<Case> {

    public MouvementPoint(Action<Case> action, Position start, Position incrementation) {
        super(action, start, incrementation);
    }

    @Override
    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();

        Position p = start.addAndReturn(incrementation);
        Action.ActionState a = action.isValidated(b.getACase(p.getX(),p.getY()));
        if(Action.ActionState.VALID == a){
            res.add(p);
        }
        return res;
    }
}
