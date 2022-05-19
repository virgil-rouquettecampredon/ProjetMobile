package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionDeplacement;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.MovementPiece;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    private DIRECTION direction;

    public Pawn(Player p, Context c, int color_fill, int color_plate, int color_stroke, DIRECTION direction) {
        super(p);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn),color_fill);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn_plate),color_plate);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn_border), color_stroke);
        this.direction = direction;
    }
    public Pawn(boolean deapCloneOnGame, Piece p, ComposedDrawing appearance, DIRECTION direction) {
        super(deapCloneOnGame, p);
        this.appearance = appearance;
        this.direction = direction;
    }

    @Override
    public List<Movement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Movement<? extends GameObject>> mvt = new ArrayList<>();

        Position start= new Position(col,row);
        List<Position> pos_eat = new ArrayList<>();

        int addx = 0;
        int addy = 0;
        switch (direction) {
            case UP:
                addy--;
                pos_eat.add(new Position(-1,-1));
                pos_eat.add(new Position(+1,-1));
                break;
            case DOWN:
                addy++;
                pos_eat.add(new Position(-1,+1));
                pos_eat.add(new Position(+1,+1));
                break;
            case LEFT:
                addx--;
                pos_eat.add(new Position(-1,+1));
                pos_eat.add(new Position(-1,-1));
                break;
            case RIGHT:
                addx++;
                pos_eat.add(new Position(+1,+1));
                pos_eat.add(new Position(+1,-1));
                break;
        }

        /*OLD DISPLACEMENT
        mvt.add(new MovementPoint(new ActionDeplacement(),start,new Position(addx,addy)));
        if(!movedYet) mvt.add(new MovementPoint(new ActionDeplacement(),start,new Position(2*addx,2*addy)));

        for (Position p: pos_eat) {
            mvt.add(new MovementPoint(new ActionEat(this),start,p, false));
        }*/

        /*NEW DISPLACEMENT*/
        mvt.add(new MovementPiece(new ActionDeplacement(),start,new Position(addx,addy),((movedYet)? 1 : 2),true));
        for (Position p: pos_eat) {
            mvt.add(new MovementPiece(new ActionEat(this),start,p, 1,false));
        }
        return mvt;
    }

    @Override
    public boolean canBeTransformed(){
        return true;
    }
}
