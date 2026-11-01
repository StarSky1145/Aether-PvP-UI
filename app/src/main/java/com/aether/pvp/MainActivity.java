package com.aether.pvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aether.pvp.data.Prefs;
import com.aether.pvp.overlay.OverlayService;
import com.aether.pvp.gl.AuroraView;
import com.aether.pvp.ui.MenuView;
import com.aether.pvp.ui.Theme;

public final class MainActivity extends Activity {
    private static final int REQUEST_OVERLAY = 41;
    private static final int REQUEST_NOTIFICATIONS = 42;

    private AuroraView aurora;
    private MenuView menu;
    private boolean overlayPending;

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

        menu.setOnOverlayAction(this::toggleOverlay);
        menu.setOverlayActive(OverlayService.isRunning());
        requestNotificationPermission();
    }

    private void toggleOverlay() {
        if (OverlayService.isRunning()) {
            OverlayService.stop(this);
            if (menu != null) {
                menu.setOverlayActive(false);
            }
            toast("Floating UI stopped");
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            overlayPending = true;
            toast("Allow \"Display over other apps\" for AETHER");
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } catch (Throwable error) {
                overlayPending = false;
                CrashLog.write(this, error);
                toast("Overlay settings are unavailable");
            }
            return;
        }
        startOverlay();
    }

    private void startOverlay() {
        try {
            OverlayService.start(this);
            if (menu != null) {
                menu.setOverlayActive(true);
            }
            toast("Floating ClickGUI enabled · drag the AETHER orb");
        } catch (Throwable error) {
            CrashLog.write(this, error);
            if (menu != null) {
                menu.setOverlayActive(false);
            }
            toast("Could not start floating UI");
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS);
        } catch (Throwable ignored) {
        }
    }

    private void toast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Throwable ignored) {
        }
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
        if (menu != null) {
            menu.setOverlayActive(OverlayService.isRunning());
        }
        if (overlayPending && Build.VERSION.SDK_INT >= 23
                && Settings.canDrawOverlays(this)) {
            overlayPending = false;
            startOverlay();
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
