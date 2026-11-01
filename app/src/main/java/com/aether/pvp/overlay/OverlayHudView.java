package com.aether.pvp.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowInsets;

import com.aether.pvp.ui.HudOverlay;

/**
 * Transparent, non-touchable window that paints the HUD above other apps.
 * The mock telemetry stays local; this view never reads another process.
 */
public final class OverlayHudView extends View {
    private final HudOverlay hud = new HudOverlay();
    private float safeLeft;
    private float safeTop;
    private float safeRight;
    private float safeBottom;
    private long lastFrame;
    private boolean laidOut;

    public OverlayHudView(Context context) {
        super(context);
        setWillNotDraw(false);
        setBackgroundColor(0x00000000);
        setOnApplyWindowInsetsListener((view, insets) -> {
            readInsets(insets);
            return insets;
        });
    }

    public void setSafeInsets(int left, int top, int right, int bottom) {
        safeLeft = left;
        safeTop = top;
        safeRight = right;
        safeBottom = bottom;
        laidOut = false;
        invalidate();
    }

    public void push(String title, String detail, int accent) {
        hud.push(title, detail, accent);
        invalidate();
    }

    private void readInsets(WindowInsets insets) {
        if (insets == null) {
            return;
        }
        int left = safeLeft > 0 ? (int) safeLeft : 0;
        int top = safeTop > 0 ? (int) safeTop : 0;
        int right = safeRight > 0 ? (int) safeRight : 0;
        int bottom = safeBottom > 0 ? (int) safeBottom : 0;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets cutout = insets.getInsets(WindowInsets.Type.displayCutout());
                left = cutout.left;
                top = cutout.top;
                right = cutout.right;
                bottom = cutout.bottom;
            } else if (android.os.Build.VERSION.SDK_INT >= 28 && insets.getDisplayCutout() != null) {
                android.view.DisplayCutout cut = insets.getDisplayCutout();
                left = cut.getSafeInsetLeft();
                top = cut.getSafeInsetTop();
                right = cut.getSafeInsetRight();
                bottom = cut.getSafeInsetBottom();
            }
        } catch (Throwable ignored) {
        }
        setSafeInsets(left, top, right, bottom);
    }

    private void ensureLayout() {
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        float u = Math.min(w / 800f, h / 360f);
        hud.layout(w, h, u <= 0f ? 1f : u, safeLeft, safeTop, safeRight, safeBottom);
        laidOut = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        laidOut = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!laidOut) {
            ensureLayout();
        }
        long now = SystemClock.uptimeMillis();
        float dt = lastFrame == 0L ? 0.016f : Math.min(0.05f, (now - lastFrame) / 1000f);
        lastFrame = now;
        hud.tick(dt, now);
        hud.draw(canvas, 1f, now);
        postInvalidateOnAnimation();
    }
}
