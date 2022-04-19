package com.example.projetmobile.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.projetmobile.Model.Pieces.Piece;

import java.util.function.Function;

public class PieceView extends View {
    private Piece p;

    public PieceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPiece(Piece p){
        this.p = p;
    }

    public void setOnClickPerformedFunction(Function<Piece, Void> onclk_performedFunction) {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclk_performedFunction.apply(p);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(p!=null) {
            ComposedDrawing cp = p.getAppearances();
            cp.setBounds(0, 0, getWidth(), getHeight());
            cp.draw(canvas);
        }
    }
}
