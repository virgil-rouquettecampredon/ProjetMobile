package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.example.projetmobile.R;

public class Board extends ViewGroup {
    private Context context;

    private int white_color;
    private int black_color;
    private int selection_color;

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setcolors();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }

    public void setcolors() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.white_case_color, typedValue, true);
        white_color = typedValue.data;

        typedValue = new TypedValue();
        theme = context.getTheme();
        theme.resolveAttribute(R.attr.black_case_color, typedValue, true);
        black_color = typedValue.data;

        typedValue = new TypedValue();
        theme = context.getTheme();
        theme.resolveAttribute(R.attr.selection_case_color, typedValue, true);
        selection_color = typedValue.data;


        System.out.println(white_color);
        System.out.println(black_color);
        System.out.println(selection_color);
    }
}
