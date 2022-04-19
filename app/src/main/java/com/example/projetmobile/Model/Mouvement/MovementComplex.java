package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.Shot;

import java.util.List;

public abstract class MovementComplex extends Movement<Case> {

    public MovementComplex(Action<Case> action, Position start, Position incrementation) {
        super(action, start, incrementation);
    }

    @Override
    public abstract List<Position> getAllPositions(Board b);

    /**Function to be called  when the movement is chose by the user**/
    public abstract List<Shot> onPostMovement(Board b);
}
