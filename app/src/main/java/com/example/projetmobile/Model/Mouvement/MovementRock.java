package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Shot;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MovementRock extends MovementComplex{

    private Piece pToWatchFor;

    public MovementRock(Piece p, Position start, Position incrementation) {
        super(new ActionRock(p), start, incrementation);
        pToWatchFor = p;
    }

    @Override
    public List<Position> getAllPositions(Board b) {
        List<Position> res = new ArrayList<>();
        Position p = start.addAndReturn(new Position());

        while (b.isGoodPos(p.getX(),p.getY())){
            p = p.addAndReturn(incrementation);
            Action.ActionState ac = action.isValidated(b.getACase(p.getX(),p.getY()));

            if(ac == Action.ActionState.STILLGOOD){
                continue;
            }
            if(ac == Action.ActionState.VALID){
                res.add(p);
            }else{
                break;
            }
        }
        return res;
    }


    @Override
    public List<Shot> onPostMovement(Board b) {
        List<Shot> res = new ArrayList<>();

        //Perform the rest of the movement
        Position pElemConcerned = start;
        Position pPieceToWatch = b.getPiecePosition(pToWatchFor);

        //Now we assume the movement to be good, so we cant eat a piece by making this movement
        //We also cant destroy one of our piece during this movement


        //try to make the first move


        return res;
    }
}
