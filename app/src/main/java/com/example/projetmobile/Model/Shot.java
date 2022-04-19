package com.example.projetmobile.Model;

import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Piece;

public class Shot {

    private Piece pieceConcerned;
    private Position startPos;
    private Position endPos;
    private Piece eatedPiece;
    private boolean firstMoove;

    private boolean majAff;

    public Shot(Piece pieceConcerned, Position startPos, Position endPos, Piece eatedPiece, boolean majAff, boolean firstMoove) {
        this.pieceConcerned = pieceConcerned;
        this.startPos = startPos;
        this.endPos = endPos;
        this.eatedPiece = eatedPiece;

        this.firstMoove = firstMoove;
        this.majAff = majAff;
    }

    public Shot(Piece pieceConcerned, Position startPos, Position endPos, boolean majAff, boolean firstMoove) {
        this(pieceConcerned,startPos,endPos,null, majAff, firstMoove);
    }

    /** ======== GETTERS ======== **/
    public Piece getPieceConcerned() {
        return pieceConcerned;
    }
    public Position getStartPos() {
        return startPos;
    }
    public Position getEndPos() {
        return endPos;
    }
    public Piece getEatedPiece() {
        return eatedPiece;
    }
    public boolean isMajAff() {
        return majAff;
    }
    public boolean isFirstMoove() {
        return firstMoove;
    }

    @Override
    public String toString() {
        return "Shot{" +
                "pieceConcerned=" + pieceConcerned +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", eatedPiece=" + eatedPiece +
                ", firstMoove=" + firstMoove +
                ", majAff=" + majAff +
                '}';
    }
}
