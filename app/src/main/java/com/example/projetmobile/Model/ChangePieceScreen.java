package com.example.projetmobile.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChangePieceScreen extends LinearLayout {

    //List of Pieces for performing the choice
    private List<Piece> piecesForChange;
    //If we need to draw the screen
    private boolean drawScreen;
    //Elements for the drawing part
    private Paint paint_background;
    //Function performed when user click on a Piece in the ChangePieceScreen
    private Function<Piece, Void> clk_pieces;

    public ChangePieceScreen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.drawScreen = getVisibility() == VISIBLE;

        paint_background = new Paint();
        paint_background.setColor(GameManager.getAttributeColor(context, R.attr.changePiece_bgColorScreen));
        piecesForChange = new ArrayList<>();
        //paint_background.setColor(ContextCompat.getColor(context, R.color.blackTransparent));
    }


    /** ======== View Methods ======== **/
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        drawScreen = visibility == VISIBLE;
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(drawScreen) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint_background);
        }
        super.dispatchDraw(canvas);
    }


    /** ======== View manipulation Methods (Model behavior) ======== **/
    public void setOclkPerformedFunctionPieces(Function<Piece, Void> onclk_performedFunction) {
        this.clk_pieces = onclk_performedFunction;
    }

    public void addElementForChoice(Piece p){
        piecesForChange.add(p);
    }

    public void clearChoices(){
        piecesForChange.clear();
    }

    public void commitChanges(){
        System.out.println("CHANGE PIECE SCREEN COMMIT CHANGES");
        //Clear UI of layout
        clearAllPieceViews();

        //Set the good dimensions of our img children
        int dim = Math.min(getWidth()/piecesForChange.size(), getHeight()/3);
        //We will add all pieces as a children of the layout
        System.out.println("DIM : " + dim);

        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,2.0f));
        ll.setOrientation(HORIZONTAL);
        ll.setGravity(Gravity.CENTER);

        for (int i = 0; i < piecesForChange.size(); i++) {
            Piece p = piecesForChange.get(i);
            System.out.println("->piece for change : " + p);

            PieceView pv = new PieceView(getContext(), null);
            pv.setLayoutParams(new LayoutParams(dim,dim));
            pv.setPiece(p);
            pv.setOnClickPerformedFunction(this.clk_pieces);
            ll.addView(pv);
        }
        this.addView(ll);
        System.out.println("NB CHILD : " + getChildCount());
        requestLayout();
    }

    private void clearAllPieceViews(){
        List<Integer> viewsToDel = new ArrayList<>();

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if(!(v instanceof TextView)){
                System.out.println("->clear the view : " + v);
                viewsToDel.add(i);
            }
        }
        for (Integer i : viewsToDel) {
            removeViewAt(i);
        }
    }

    @Override
    public String toString() {
        return "onBoardScreen{}";
    }
}
