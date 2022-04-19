package com.example.projetmobile.Model.Mouvement;

import androidx.annotation.NonNull;

import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.ComposedDrawing;

public class ActionDeplacement implements Action<Case>{

    @Override
    public ActionState isValidated(Case c) {
        if(c == null) return ActionState.INVALID;
        if (c.getPiece() == null){
            return ActionState.STILLGOOD;
        }
        return ActionState.INVALID;
    }

    public ComposedDrawing getGraphicVisualisation(){
        ComposedDrawing comp = new ComposedDrawing();
        return comp;
    }

    @Override
    public String toString() {
        return "DEPLACEMENT";
    }
}
