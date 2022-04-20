package com.example.projetmobile.Model;

import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

public class Association_rock {
    Position p1;
    Position p2;
    Piece pieceToRockWith;
    Position posPieceToRockWith;

    public Association_rock(Piece p, Position posPieceToRockWith, Position p1, Position p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.posPieceToRockWith = posPieceToRockWith;
        this.pieceToRockWith = p;
    }


    @Override
    public String toString() {
        return "Association_rock{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", pieceToROckWith=" + pieceToRockWith +
                '}';
    }
}
