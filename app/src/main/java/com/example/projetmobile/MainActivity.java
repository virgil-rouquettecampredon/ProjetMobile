package com.example.projetmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //For the animation on the cold white screen start
    public static final long FL_STARTUP_DELAY_DROPDOWN     =   50L;
    public static final long FL_STARTUP_DELAY_ALPHA        =   0L;
    public static final long FL_ANIM_DURATION_DROPDOWN     =   200L;
    public static final long FL_ANIM_DURATION_ALPHA        =   250L;
    public static final int FL_TRANSLATION                 =   20;
    private static boolean animationStarted                =   false;

    public final static String fragmentTag = "FIRSTMENUFRAGMENT";
    private View frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Reset style before everything
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.fragment_container);
        frameLayout.setTranslationY(-FL_TRANSLATION);
        frameLayout.setAlpha(0.0f);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        FirstMenuFragment frag = (FirstMenuFragment) fm.findFragmentByTag(fragmentTag);

        if (frag == null) {
            frag = FirstMenuFragment.newInstance();
            transaction.add(R.id.fragment_container, frag, fragmentTag);
            transaction.commit();
        }
    }


    /**For the animation on the start of the application**/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus || animationStarted) {
            frameLayout.setTranslationY(FL_TRANSLATION);
            frameLayout.setAlpha(1.0f);
            return;
        }
        animate();
        super.onWindowFocusChanged(hasFocus);
    }

    private void animate() {
        animationStarted = true;
        ViewCompat.animate(frameLayout)
                .alpha(1.0f)
                .setStartDelay(FL_STARTUP_DELAY_ALPHA)
                .setDuration(FL_ANIM_DURATION_ALPHA).setInterpolator(
                new DecelerateInterpolator(.2f)).start();

        ViewCompat.animate(frameLayout)
                .translationY(FL_TRANSLATION)
                .setStartDelay(FL_STARTUP_DELAY_DROPDOWN)
                .setDuration(FL_ANIM_DURATION_DROPDOWN).setInterpolator(
                new LinearInterpolator()).start();
    }
}