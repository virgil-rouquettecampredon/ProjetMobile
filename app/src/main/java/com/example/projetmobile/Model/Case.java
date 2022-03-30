package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.projetmobile.R;

public class Case extends View {
    private Context context;
    private Paint paint;

    //Appearance DATA
    private int dimension;

    private int color_white;
    private int color_black;
    private int color_selection;

    //Model DATA
    private Piece piece;
    private boolean possible_pos;

    private boolean white;
    private int column;
    private int row;
    private int selection_color;

    public Case(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dimension = (int) GameManager.convertDpToPixel(getResources().getDimension(R.dimen.case_pieces_padding),context);
        Paint paint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Case, 0, 0);
        try {
            column = a.getInteger(R.styleable.Case_column, 0);
            row = a.getInteger(R.styleable.Case_row, 0);
            white = (0 == a.getInteger(R.styleable.Case_case_color, 0));
        } finally {
            a.recycle();
        }


        System.out.println(column);
        System.out.println(row);
        System.out.println(white);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw background color
        if(white){
            paint.setColor(color_white);
        }else{
            paint.setColor(color_black);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);


        //Draw possible pos filter
        if(possible_pos){

        }


        //Draw piece
        if(piece != null){
            Drawable d = piece.getAppearance();
            d.setBounds(dimension, dimension, getWidth()-dimension, getHeight()-dimension);
            d.draw(canvas);
        }
    }




    public void setColor_white(int color_white) {
        this.color_white = color_white;
    }
    public void setColor_black(int color_black) {
        this.color_black = color_black;
    }
    public void setSelection_color(int selection_color) {
        this.selection_color = selection_color;
    }

    public void commit_changes(){
        invalidate();
    }


    //Refresh with invalidate (from UI thread)
    //Refresh with onPostInvalidate(from non UI thread)
}
