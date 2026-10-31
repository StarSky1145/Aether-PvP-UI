package com.aether.pvp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aether.pvp.data.Prefs;
import com.aether.pvp.gl.AuroraView;
import com.aether.pvp.ui.MenuView;
import com.aether.pvp.ui.Theme;

public final class MainActivity extends Activity {
    private AuroraView aurora;
    private MenuView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            start();
        } catch (Throwable error) {
            CrashLog.write(this, error);
            showCrash(error);
        }
    }

    private void start() {
        Theme.load(getResources());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        applyImmersive();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.TRANSPARENT);

        aurora = new AuroraView(this);
        root.addView(aurora, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        menu = new MenuView(this, new Prefs(this));
        root.addView(menu, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        root.setOnApplyWindowInsetsListener((view, insets) -> {
            applySafeInsets(insets);
            return insets;
        });

        setContentView(root);
        root.requestApplyInsets();
    }

    private void showCrash(Throwable error) {
        try {
            ScrollView scroll = new ScrollView(this);
            TextView text = new TextView(this);
            text.setText("AETHER could not start\n\n" + Log.getStackTraceString(error));
            text.setTextColor(0xFFF4F2F8);
            text.setTextSize(11f);
            text.setPadding(36, 36, 36, 36);
            text.setTextIsSelectable(true);
            scroll.setBackgroundColor(0xFF07080C);
            scroll.addView(text);
            setContentView(scroll);
        } catch (Throwable ignored) {
        }
    }

    private void applySafeInsets(WindowInsets insets) {
        try {
            applySafeInsetsInner(insets);
        } catch (Throwable error) {
            CrashLog.write(this, error);
        }
    }

    private void applySafeInsetsInner(WindowInsets insets) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        if (Build.VERSION.SDK_INT >= 30) {
            Insets cutout = insets.getInsets(WindowInsets.Type.displayCutout());
            left = cutout.left;
            top = cutout.top;
            right = cutout.right;
            bottom = cutout.bottom;
        } else if (Build.VERSION.SDK_INT >= 28 && insets.getDisplayCutout() != null) {
            left = insets.getDisplayCutout().getSafeInsetLeft();
            top = insets.getDisplayCutout().getSafeInsetTop();
            right = insets.getDisplayCutout().getSafeInsetRight();
            bottom = insets.getDisplayCutout().getSafeInsetBottom();
        }
        if (menu != null) {
            menu.setSafeInsets(left, top, right, bottom);
        }
    }

    private void applyImmersive() {
        try {
            applyImmersiveInner();
        } catch (Throwable error) {
            CrashLog.write(this, error);
        }
    }

    private void applyImmersiveInner() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }

        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.systemBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(false);
            window.setStatusBarContrastEnforced(false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersive();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (aurora != null) {
            aurora.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (aurora != null) {
            aurora.onPause();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (menu != null && menu.handleBack()) {
            return;
        }
        super.onBackPressed();
    }
}
