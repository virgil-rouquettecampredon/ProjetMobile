package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

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

public class Knight extends Piece{

    public Knight(Player p, Context c, int color_fill, int color_plate, int color_stroke) {
        super(p);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_knight),color_fill);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_knight_plate),color_plate);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_knight_border), color_stroke);
    }

    @Override
    public List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Mouvement<? extends GameObject>> mvt = new ArrayList<>();
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(2,1)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(2,-1)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(-2,1)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(-2,-1)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(1,2)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(-1,-2)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(1,-2)));
        mvt.add(new MouvementPoint(new ActionEat(this.pocessor),new Position(col,row),new Position(-1,2)));

        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(2,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(2,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-2,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-2,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,2)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,-2)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,-2)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,2)));
        return mvt;
    }
}
