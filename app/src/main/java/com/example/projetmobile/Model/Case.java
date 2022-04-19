package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.projetmobile.Model.Mouvement.MovementComplex;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.R;

public class Case extends View implements GameObject {
    private Context context;
    private Paint paint;

    private int desiredDim;
    private int stroke_width;

    //Appearance DATA
    private int dimension;

    private int color_white;
    private int color_black;

    //Model DATA
    private Piece piece;
    private boolean possible_pos;
    private boolean pre_selected_pos;
    private boolean is_end_case;
    private boolean is_case_with_menace_on_it;
    private MovementComplex mv_complex;

    private boolean white;
    private int column;
    private int row;

    private int nb_draw = 0;

    public Case(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        dimension = (int) GameManager.convertDpToPixel(getResources().getDimension(R.dimen.case_pieces_padding),context);
        paint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Case, 0, 0);
        try {
            column = a.getInteger(R.styleable.Case_column, 0);
            row = a.getInteger(R.styleable.Case_row, 0);
            white = (0 == a.getInteger(R.styleable.Case_case_color, 0));
            possible_pos = a.getBoolean(R.styleable.Case_possible_pos, false);
        } finally {
            a.recycle();
        }

        color_black = Color.BLACK;
        color_white = Color.WHITE;
        pre_selected_pos = false;
        mv_complex = null;
    }


    /** ======== View Methods ======== **/
    @Override
    protected void onDraw(Canvas canvas) {
        //System.out.println("JE DESSINE : " + this);
        //Draw background color
        if(white){
            //paint.setColor(color_white);
            canvas.drawColor(color_white);
        }else{
            //paint.setColor(color_black);
            canvas.drawColor(color_black);
        }
        //paint.setStyle(Paint.Style.FILL);
        //canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        //Try first to draw the possible menace that a case can possibly contain

            if (pre_selected_pos) {
                //System.out.println("DRAW PRE SELECTED POS");
                //Draw pre selected pos
                Board.appearence_confirmation.setBounds(0, 0, getWidth(), getHeight());
                Board.appearence_confirmation.draw(canvas);
            } else {
                //Draw possible pos filter
                if (possible_pos) {
                    //System.out.println("DRAW POSSIBLE POS");
                    if (piece != null) {
                        Board.appearence_possiblepos_eat.setBounds(0, 0, getWidth(), getHeight());
                        Board.appearence_possiblepos_eat.draw(canvas);
                    } else {
                        Board.appearence_possiblepos.setBounds(0, 0, getWidth(), getHeight());
                        Board.appearence_possiblepos.draw(canvas);
                    }

                } else {
                    if (is_case_with_menace_on_it) {
                        Board.appearence_menaced.setBounds(0, 0, getWidth(), getHeight());
                        Board.appearence_menaced.draw(canvas);
                    }
                }
            }


        //Draw piece
        if(piece != null){
            //System.out.println("DRAW A PIECE : " + piece);
            ComposedDrawing drawable = piece.getAppearances();
            drawable.setBounds(dimension, dimension, getWidth()-dimension, getHeight()-dimension);
            drawable.draw(canvas);
        }
        nb_draw++;

        super.onDraw(canvas);
    }
    @Override
    //Can be used to stop animations and to clean up resources used by the view
    protected void onDetachedFromWindow() {
        //can be used to stop animations and to clean up resources used by the view
        super.onDetachedFromWindow();
    }
    @Override
    //Called once the window is available
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //System.out.println("JE SUIS ATTACHE : " + this);
        setWillNotDraw(false);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredDim, widthSize);
        } else {
            //Be whatever you want
            width = desiredDim;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredDim, heightSize);
        } else {
            //Be whatever you want
            height = desiredDim;
        }

        setMeasuredDimension(width, height);
    }


    /** ======== SETTERS ======== **/
    public void setColor_white(int color_white) {
        this.color_white = color_white;
    }
    public void setColor_black(int color_black) {
        this.color_black = color_black;
    }
    public void setPossible_pos(boolean possible_pos) {
        this.possible_pos = possible_pos;
    }
    public void setPre_selected_pos(boolean pre_selected_pos) {
        this.pre_selected_pos = pre_selected_pos;
    }
    public void setWith_menace_on_it(boolean is_case_with_menace_on_it) {
        this.is_case_with_menace_on_it = is_case_with_menace_on_it;
    }
    public void setWhite(boolean white) {
        this.white = white;
    }
    public void setColumn(int column) {
        this.column = column;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public Piece getPiece() {
        return piece;
    }
    public void setDessiredDimention(int desiredDim) {
        this.stroke_width = desiredDim/8;
        this.desiredDim = desiredDim;
    }
    public void setEndCase(boolean is_end_case) {
        this.is_end_case = is_end_case;
    }
    public void setMv_complex(MovementComplex mv_complex) {
        this.mv_complex = mv_complex;
    }


    /** ======== GETTERS ======== **/
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }
    public boolean isPre_selected_pos() {
        return pre_selected_pos;
    }
    public boolean isPossible_pos() {
        return possible_pos;
    }
    public boolean is_end_case() {
        return is_end_case;
    }
    public MovementComplex getMv_complex() {
        return mv_complex;
    }


    /** ======== UTILITIES FUNCTION ======== **/
    public void commit_changes(){
        //System.out.println("CASE : COMMIT CHANGES ON : " + this);
        //Refresh with invalidate (from UI thread)
        //Refresh with onPostInvalidate(from non UI thread)
        invalidate();
    }
    public void clear(){
        this.piece = null;
        this.possible_pos = false;
        this.pre_selected_pos = false;
    }
    @Override
    public String toString() {
        return "Case{" +
                "desiredDim=" + desiredDim +
                ", stroke_width=" + stroke_width +
                ", dimension=" + dimension +
                ", color_white=" + color_white +
                ", color_black=" + color_black +
                ", piece=" + piece +
                ", possible_pos=" + possible_pos +
                ", pre_selected_pos=" + pre_selected_pos +
                ", white=" + white +
                ", column=" + column +
                ", row=" + row +
                ", nb_draw=" + nb_draw +
                ",GetWidth="+ getWidth()+
                ",GetHeight="+ getHeight()+
                '}';
    }
}
