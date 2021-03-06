package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MovementPoint extends Movement<Case> {

    //Is the action is not completly performed (ex STILL GOOD) you can still perform the movement through the emplacement
    private boolean canStillMoove;

    public MovementPoint(Action<Case> action, Position start, Position incrementation, boolean canStillMoove) {
        super(action, start, incrementation);
        this.canStillMoove = canStillMoove;
    }

    public MovementPoint(Action<Case> action, Position start, Position incrementation) {
        this(action, start, incrementation, true);
    }

    @Override
    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();

        Position p = start.addAndReturn(incrementation);
        Action.ActionState a = action.isValidated(b.getACase(p.getX(),p.getY()));
        if(Action.ActionState.VALID == a || (canStillMoove && Action.ActionState.STILLGOOD == a)){
            res.add(p);
        }
        return res;
    }
}
