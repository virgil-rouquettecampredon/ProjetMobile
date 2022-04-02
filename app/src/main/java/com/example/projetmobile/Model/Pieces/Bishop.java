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

public class Bishop extends Piece{

    public Bishop(Player p, Context c, int color_fill, int color_plate, int color_stroke, int color_elements) {
        super(p);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_bishop),color_fill);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_bishop_plate),color_plate);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_bishop_elements), color_elements);
        this.appearence.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_bishop_border), color_stroke);
    }

    @Override
    public List<Mouvement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Mouvement<? extends GameObject>> mvt = new ArrayList<>();
        mvt.add(new MouvementVector(new ActionEat(this.pocessor),new Position(col,row),new Position(1,1)));
        mvt.add(new MouvementVector(new ActionEat(this.pocessor),new Position(col,row),new Position(-1,-1)));
        mvt.add(new MouvementVector(new ActionEat(this.pocessor),new Position(col,row),new Position(1,-1)));
        mvt.add(new MouvementVector(new ActionEat(this.pocessor),new Position(col,row),new Position(-1,1)));
        return mvt;
    }
}
