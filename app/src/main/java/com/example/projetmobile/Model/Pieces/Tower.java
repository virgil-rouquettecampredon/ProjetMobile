package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Mouvement;
import com.example.projetmobile.Model.Mouvement.MouvementVector;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class Tower extends Piece{

    public Tower(Player p, Context c, int color_fill, int color_plate, int color_stroke) {
        super(p);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower),color_fill);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower_plate),color_plate);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_tower_border), color_stroke);
    }

    @Override
    public List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Mouvement<? extends GameObject>> mvt = new ArrayList<>();
        mvt.add(new MouvementVector(new ActionEat(this),new Position(col,row),new Position(0,1)));
        mvt.add(new MouvementVector(new ActionEat(this),new Position(col,row),new Position(0,-1)));
        mvt.add(new MouvementVector(new ActionEat(this),new Position(col,row),new Position(1,0)));
        mvt.add(new MouvementVector(new ActionEat(this),new Position(col,row),new Position(-1,0)));
        return mvt;
    }

}