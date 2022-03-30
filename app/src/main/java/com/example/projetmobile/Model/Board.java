package com.example.projetmobile.Model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import com.example.projetmobile.R;


public class Board extends TableLayout {
    private int nb_col;
    private int nb_row;

    private Context context;
    private AttributeSet attributeSet;

    private int white_color;
    private int black_color;
    private int selection_color;

    public Board(Context context) {
        super(context);
        this.context = context;
        this.attributeSet = null;
        setcolors();
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attributeSet = attrs;
        setcolors();
    }

    public void setcolors() {
        white_color = GameManager.getAttributeColor(context,R.attr.white_case_color);
        black_color = GameManager.getAttributeColor(context,R.attr.black_case_color);
        selection_color = GameManager.getAttributeColor(context,R.attr.selection_case_color);

        System.out.println("BOARD COUL BLANCHE : " + white_color);
        System.out.println("BOARD COUL NOIRE : " + black_color);
        System.out.println("BOARD SELECTION CASE COUL : " + selection_color);
    }
    /*public void setdimensions() {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.Board);
        nb_col = a.getInt(R.styleable.Board_nb_column, 0);
        nb_row = a.getInt(R.styleable.Board_nb_row, 0);
        a.recycle();
    }*/
}
