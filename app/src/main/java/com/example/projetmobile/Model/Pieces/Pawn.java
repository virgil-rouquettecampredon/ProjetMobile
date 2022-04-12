package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.Case;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionDeplacement;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Mouvement.MouvementPoint;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    private DIRECTION direction;

    public Pawn(Player p, Context c, int color_fill, int color_plate, int color_stroke, DIRECTION direction) {
        super(p);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn),color_fill);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn_plate),color_plate);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_pawn_border), color_stroke);
        this.direction = direction;
    }

    @Override
    public List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Mouvement<? extends GameObject>> mvt = new ArrayList<>();

        Position start= new Position(col,row);

        int addx = 0;
        int addy = 0;

        List<Position> pos_eat = new ArrayList<>();

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

        mvt.add(new MouvementPoint(new ActionDeplacement(),start,new Position(addx,addy)));
        if(!moovedYet) mvt.add(new MouvementPoint(new ActionDeplacement(),start,new Position(2*addx,2*addy)));

        for (Position p: pos_eat) {
            mvt.add(new MouvementPoint(new ActionEat(this),start,p));
        }

        return mvt;
    }
}
