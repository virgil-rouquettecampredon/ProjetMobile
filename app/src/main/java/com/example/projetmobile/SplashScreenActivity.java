package com.example.projetmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.projetmobile.Model.GameManager;

public class SplashScreenActivity extends AppCompatActivity {

    public static final long GLOBAL_DELAY                   =   100L;
    public static final long LOGO_STARTUP_DELAY_DROPDOWN    =   GLOBAL_DELAY+ 250L;
    public static final long BG_STARTUP_DELAY_ALPHA         =   GLOBAL_DELAY+400L;
    public static final long LOGO_ANIM_DURATION_DROPDOWN    =   400L;
    public static final long BG_ANIM_DURATION_ALPHA         =   250L;
    public static final long MAX_DURATION                   =   GLOBAL_DELAY+700;

    //https://stackoverflow.com/questions/27872324/how-can-i-animate-the-color-change-of-the-statusbar-and-toolbar-like-the-new-ca
    // Initial colors of each system bar.
    private int statusBarColor;
    private int toolbarColor;
    // Desired final colors of each bar.
    private int statusBarToColor;
    private int toolbarToColor;

    public static boolean animationStarted = false;

    private ImageView logoImageView;
    private View backgroundView;

    private boolean land;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        System.out.println("LAUNCH SPLASH ACTIVITY SCREEN");
        System.out.println("LOGO_STARTUP_DELAY_DROPDOWN : " + LOGO_STARTUP_DELAY_DROPDOWN);
        System.out.println("LOGO_ANIM_DURATION_DROPDOWN : " + LOGO_ANIM_DURATION_DROPDOWN);
        System.out.println("BG_STARTUP_DELAY_ALPHA : " + BG_STARTUP_DELAY_ALPHA);
        System.out.println("BG_ANIM_DURATION_ALPHA : " + BG_ANIM_DURATION_ALPHA);
        System.out.println("MAX_DURATION : " + MAX_DURATION);

        logoImageView = (ImageView) findViewById(R.id.logo);
        backgroundView = findViewById(R.id.background);
        backgroundView.setAlpha(1.0f);

        statusBarColor   = GameManager.getAttributeColor(SplashScreenActivity.this, R.attr.colorSecondary);
        toolbarColor     = GameManager.getAttributeColor(SplashScreenActivity.this, R.attr.colorSecondary);
        statusBarToColor = GameManager.getAttributeColor(SplashScreenActivity.this, R.attr.colorPrimary);
        toolbarToColor   = getColor(R.color.black);

        animationStarted = false;

        /*land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (land) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        //At the end of all the timer, we launch the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(startActivity);
                overridePendingTransition(0, 0);
                finish();
            }
        }, MAX_DURATION);
    }


    /**For the animation on the start of the application**/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus || animationStarted) {
            return;
        }
        logoImageView.setTranslationY(backgroundView.getHeight()/2.0f - logoImageView.getHeight()/2.0f);
        animate();
        super.onWindowFocusChanged(hasFocus);
    }

    private void animate() {
        animationStarted = true;
        ViewCompat.animate(backgroundView)
                .alpha(0.0f)
                .setStartDelay(BG_STARTUP_DELAY_ALPHA)
                .setDuration(BG_ANIM_DURATION_ALPHA).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(animation -> {
            // Use animation position to blend colors.
            float position = animation.getAnimatedFraction();

            // Apply blended color to the status bar.
            int blended = blendColors(statusBarColor, statusBarToColor, position);
            getWindow().setStatusBarColor(blended);

            // Apply blended color to the ActionBar.
            blended = blendColors(toolbarColor, toolbarToColor, position);
            getWindow().setNavigationBarColor(blended);
            //getSupportActionBar().setBackgroundDrawable(background);
        });

        anim.setStartDelay(BG_STARTUP_DELAY_ALPHA);
        anim.setDuration(BG_ANIM_DURATION_ALPHA);
        anim.start();

        ViewCompat.animate(logoImageView)
                .translationY(0)
                .setStartDelay(LOGO_STARTUP_DELAY_DROPDOWN)
                .setDuration(LOGO_ANIM_DURATION_DROPDOWN).setInterpolator(
                new AccelerateDecelerateInterpolator()).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}