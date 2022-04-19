package com.example.projetmobile.Model.Pieces;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.ComposedDrawing;
import com.example.projetmobile.Model.GameObject;
import com.example.projetmobile.Model.Mouvement.ActionEat;
import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.MovementComplex;
import com.example.projetmobile.Model.Mouvement.MovementPiece;
import com.example.projetmobile.Model.Mouvement.MovementPoint;
import com.example.projetmobile.Model.Mouvement.MovementRock;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Player;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{

    private List<Piece> piecesToRockWith;
    private List<Position> incrementationPieceToRockWith;

    public King(Player p, Context c, int color_fill, int color_plate, int color_stroke, int color_elements) {
        super(p);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king),color_fill);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_plate),color_plate);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_elements), color_elements);
        this.appearance.addLayer(ContextCompat.getDrawable(c, R.drawable.piece_king_border), color_stroke);
        piecesToRockWith = new ArrayList<>();
        incrementationPieceToRockWith = new ArrayList<>();
    }
    public King(boolean deapCloneOnGame, Piece p, ComposedDrawing appearance) {
        super(deapCloneOnGame, p);
        this.appearance = appearance;
        piecesToRockWith = new ArrayList<>();
    }

    public void addPieceToRockWith(Piece p, Position pos){
        this.piecesToRockWith.add(p);
        this.incrementationPieceToRockWith.add(pos);
    }

    @Override
    public List<Movement<? extends GameObject>> getAllPossibleMvt(int col, int row){
        List<Movement<? extends GameObject>> mvt = new ArrayList<>();
        /*mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(0,1)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(1,1)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(1,0)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(1,-1)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(0,-1)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(-1,-1)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(-1,0)));
        mvt.add(new MovementPoint(new ActionEat(this),new Position(col,row),new Position(-1,1)));*/
        /*mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(0,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,0)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(1,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(0,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,-1)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,0)));
        mvt.add(new MouvementPoint(new ActionDeplacement(),new Position(col,row),new Position(-1,1)));*/

        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(0,1),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(1,1),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(1,0),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(1,-1),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(0,-1),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(-1,-1),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(-1,0),1,true));
        mvt.add(new MovementPiece(new ActionEat(this),new Position(col,row),new Position(-1,1),1,true));

        /*if(!movedYet){
            for (int i = 0; i < piecesToRockWith.size(); i++) {
                Piece curP = piecesToRockWith.get(i);
                Position curPosInc = incrementationPieceToRockWith.get(i);

                mvt.add(new MovementRock(curP,new Position(col,row),curPosInc));
            }
        }*/
        return mvt;
    }

    @Override
    public List<MovementComplex> getAllPossibleComplexMvt(int col, int row) {
        List<MovementComplex> mComp = new ArrayList<>();

        if(!movedYet){
            for (int i = 0; i < piecesToRockWith.size(); i++) {
                Piece curP = piecesToRockWith.get(i);
                Position curPosInc = incrementationPieceToRockWith.get(i);
                mComp.add(new MovementRock(curP,new Position(col,row),curPosInc));
            }
        }
        return mComp;
    }

    @Override
    public boolean isVictoryCondition() {
        return true;
    }
}
