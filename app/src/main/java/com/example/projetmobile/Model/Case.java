package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.R;

public class Case extends View implements GameObject {
    //=== Rendering information
    private Paint paint;
    private int desiredDim;
    private int stroke_width;
    private int dimension;
    private int color_white;
    private int color_black;

    //=== Model data
    //View data
    private Context context;

    //Model data
    private Piece piece;
    private Association_rock rock_elem;
    private boolean possible_pos;
    private boolean pre_selected_pos;
    private boolean is_end_case;
    private boolean is_case_with_menace_on_it;

    private boolean white;
    private int column;
    private int row;

    //cpt for drawing count
    private int nb_draw = 0;

    public Case(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        dimension = (int) GameManager.convertDpToPixel(getResources().getDimension(R.dimen.case_pieces_padding), context);
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
        is_end_case = false;
        is_case_with_menace_on_it = false;
        rock_elem = null;
        piece = null;
    }


    /**
     * ======== View Methods ========
     **/
    @Override
    protected void onDraw(Canvas canvas) {
       if (white) {
            canvas.drawColor(color_white);
        } else {
            canvas.drawColor(color_black);
        }

        //Try first to draw the possibly pre_selected information that a case can contain
        if (pre_selected_pos) {
            //Draw pre selected pos
            Board.appearance_confirmation.setBounds(0, 0, getWidth(), getHeight());
            Board.appearance_confirmation.draw(canvas);
        } else {

            //Draw possible pos filter
            if (possible_pos) {
                //Try first to draw if a case with possible position contain a case
                if (piece != null) {
                    Board.appearance_possiblepos_eat.setBounds(0, 0, getWidth(), getHeight());
                    Board.appearance_possiblepos_eat.draw(canvas);
                } else {
                    //Then try to see if a case is a possible movement and contain a rock structure
                    if(rock_elem != null){
                        Board.appearance_rock.setBounds(0, 0, getWidth(), getHeight());
                        Board.appearance_rock.draw(canvas);
                    }else {
                        Board.appearance_possiblepos.setBounds(0, 0, getWidth(), getHeight());
                        Board.appearance_possiblepos.draw(canvas);
                    }
                }
            } else {
                //Next try to draw the possible menace that a case can possibly contain
                if (is_case_with_menace_on_it) {
                    Board.appearance_menaced.setBounds(0, 0, getWidth(), getHeight());
                    Board.appearance_menaced.draw(canvas);
                }
            }
        }

        //Draw piece
        if (piece != null) {
            ComposedDrawing drawable = piece.getAppearances();
            drawable.setBounds(dimension, dimension, getWidth() - dimension, getHeight() - dimension);
            drawable.draw(canvas);
        }
        nb_draw++;

        super.onDraw(canvas);
    }

    @Override
    //Can be used to stop animations and clean up resources used by the view
    protected void onDetachedFromWindow() {
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


    /**
     * ======== SETTERS ========
     **/
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

    public void setDessiredDimention(int desiredDim) {
        this.stroke_width = desiredDim / 8;
        this.desiredDim = desiredDim;
    }

    public void setEndCase(boolean is_end_case) {
        this.is_end_case = is_end_case;
    }

    public void set_rock_elem(Association_rock rock_data) {
        this.rock_elem = rock_data;
    }


    /**
     * ======== GETTERS ========
     **/
    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean is_pre_selected_pos() {
        return pre_selected_pos;
    }

    public boolean is_possible_pos() {
        return possible_pos;
    }

    public boolean is_end_case() {
        return is_end_case;
    }

    public Association_rock get_rock_position() {
        return rock_elem;
    }

    public Piece getPiece() {
        return piece;
    }


    /**
     * ======== UTILITIES FUNCTIONS ========
     **/
    public void commit_changes() {
        invalidate();
    }

    public void clear() {
        this.piece = null;
        this.possible_pos = false;
        this.pre_selected_pos = false;
    }

    @Override
    public String toString() {
        return "Case{" +
                "paint=" + paint +
                ", desiredDim=" + desiredDim +
                ", stroke_width=" + stroke_width +
                ", dimension=" + dimension +
                ", color_white=" + color_white +
                ", color_black=" + color_black +
                ", context=" + context +
                ", piece=" + piece +
                ", rock_elem=" + rock_elem +
                ", possible_pos=" + possible_pos +
                ", pre_selected_pos=" + pre_selected_pos +
                ", is_end_case=" + is_end_case +
                ", is_case_with_menace_on_it=" + is_case_with_menace_on_it +
                ", white=" + white +
                ", column=" + column +
                ", row=" + row +
                ", nb_draw=" + nb_draw +
                '}';
    }
}
