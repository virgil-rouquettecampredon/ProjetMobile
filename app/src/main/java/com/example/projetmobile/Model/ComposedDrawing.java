package com.example.projetmobile.Model;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ComposedDrawing extends Drawable {
    public class LayerDrawing {
        //For the drawing of the piece
        public Drawable d;
        public LayerDrawing(Drawable d, int color) {
            this.d = d;
            this.d.setTint(color);
        }

        public void setBounds(int left, int top, int right, int bottom){
            d.setBounds(left,top,right,bottom);
        }

        public void draw(Canvas c){
            d.draw(c);
        }

    }

    private List<LayerDrawing> layers;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public ComposedDrawing(){
        layers = new ArrayList<>();
    }

    public void draw(Canvas c){
        for (LayerDrawing l: layers) {
            l.setBounds(left,top, right,bottom);
            l.draw(c);
        }
    }

    public void setBounds(int left, int top, int right, int bottom){
        this.right = right;
        this.left = left;
        this.bottom = bottom;
        this.top = top;
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void addLayer(Drawable d, int color){
        layers.add(new LayerDrawing(d,color));
    }

    public void clear(){
        this.layers.clear();
    }

    public boolean isInstancied(){
        return this.layers.size()>0;
    }
}
