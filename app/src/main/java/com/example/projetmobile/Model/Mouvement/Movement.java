package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.Board;
import com.example.projetmobile.Model.GameObject;

import java.util.List;

public abstract class Movement<T extends GameObject> {
    protected Action<T> action;
    protected Position start;
    protected Position incrementation;

    //ComposedDrawing graphic_element;

    public Movement(Action<T> action, Position start, Position incrementation) {
        this.action = action;
        this.start = start;
        this.incrementation = incrementation;

        //this.graphic_element = action.getGraphicVisualisation();
    }

    /**Compute all the possible position that a Mouvement can perform
     * @param b : Board of the current game
     * @return a list of Position computed by this mouvement**/
    public abstract List<Position> getAllPositions(Board b);

    /**Indicate the visual indentification (if needed) of a specific mouvement performed on the board
    public ComposedDrawing getGraphicVisualisation(){
        return graphic_element;
    }**/

    @Override
    public String toString() {
        return "Mouvement{" +
                "action=" + action +
                ", start=" + start +
                ", incrementation=" + incrementation +
                '}';
    }
}
