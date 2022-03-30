package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

public class GameManager {


    //Get Pixel value of dp var (for canva drawing)
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    //Get Color attribute from reference context (for canva drawing)
    public static int getAttributeColor(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        int colorRes = typedValue.resourceId;
        int color = -1;
        try {
            color = ContextCompat.getColor(context,colorRes);
        } catch (Resources.NotFoundException e) {
            System.out.println("=====> ERROR : Not found color resource by id: " + colorRes);
        }
        return color;
    }
}
