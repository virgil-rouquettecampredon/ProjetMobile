package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionDeplacement;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Mouvement.MouvementPoint;
import com.example.projetmobile.Model.Mouvement.MouvementVector;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{

    public King(Player p, Context c, int color_fill, int color_plate, int color_stroke, int color_elements) {
        super(p);
        this.victory = true;
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king),color_fill);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_plate),color_plate);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_elements), color_elements);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_border), color_stroke);
    }

    @Override
    public List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Mouvement<? extends GameObject>> mvt = new ArrayList<>();
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(0,1)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(1,1)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(1,0)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(1,-1)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(0,-1)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(-1,-1)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(-1,0)));
        mvt.add(new MouvementPoint(new ActionEat(this),new Position(col,row),new Position(-1,1)));

        /*mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(0,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,0)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(0,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,0)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,1)));*/

        return mvt;
    }
}
