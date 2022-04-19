package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.MovementPiece;
import com.example.projetmobile.Model.Mouvement.MovementVector;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class Tower extends Piece{

    public Tower(Player p, Context c, int color_fill, int color_plate, int color_stroke) {
        super(p);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower),color_fill);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower_plate),color_plate);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower_border), color_stroke);
    }
    public Tower(boolean deapCloneOnGame, Piece p, ComposedDrawing appearance) {
        super(deapCloneOnGame,p);
        this.appearance = appearance;
    }

    @Override
    public List<Movement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Movement<? extends GameObject>> mvt = new ArrayList<>();
        /*mvt.add(new MovementVector(new ActionEat(this),new Position(col,row),new Position(0,1)));
        mvt.add(new MovementVector(new ActionEat(this),new Position(col,row),new Position(0,-1)));
        mvt.add(new MovementVector(new ActionEat(this),new Position(col,row),new Position(1,0)));
        mvt.add(new MovementVector(new ActionEat(this),new Position(col,row),new Position(-1,0)));*/

        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(0,1)));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(0,-1)));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(1,0)));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(-1,0)));
        return mvt;
    }

}