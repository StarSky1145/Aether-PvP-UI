package com.aether.pvp.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.aether.pvp.render.AetherSurface;
import com.aether.pvp.ui.Theme;
import com.aether.pvp.ui.Ui;

/** Small draggable orb that opens the ClickGUI overlay. */
public final class LauncherView extends View {
    public interface Listener {
        void onTap();

        void onMove(float rawX, float rawY);

        void onRelease();
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = Ui.text(13f, Theme.TEXT, Theme.semibold, 0.02f);
    private final RectF plate = new RectF();
    private final int slop;
    private Listener listener;
    private float downRawX;
    private float downRawY;
    private float startX;
    private float startY;
    private boolean dragging;
    private boolean pressed;
    private final float density;

    public LauncherView(Context context) {
        super(context);
        density = getResources().getDisplayMetrics().density;
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setClickable(true);
        setContentDescription("AETHER floating controls");
        setBackgroundColor(0x00000000);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setWindowPosition(float x, float y) {
        startX = x;
        startY = y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.round(58f * density);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float s = Math.min(getWidth(), getHeight());
        float cx = s * 0.5f;
        float cy = s * 0.5f;
        float r = s * 0.5f - 3f * density;

        AetherSurface.shadow(canvas, new RectF(cx - r, cy - r, cx + r, cy + r),
                r, density, 4);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new android.graphics.LinearGradient(cx - r, cy - r, cx + r, cy + r,
                0xEE10131C, 0xF207090E, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(0x40C9B8FF);
        canvas.drawCircle(cx, cy, r, paint);
        paint.setColor(pressed ? 0x55FFFFFF : 0x18FFFFFF);
        canvas.drawCircle(cx, cy, r - 3.5f * density, paint);

        AetherSurface.glow(canvas, cx, cy, r * 1.55f, Theme.LAVENDER);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Theme.TEXT);
        float mark = r * 0.36f;
        canvas.drawPath(Ui.sparkle(cx, cy - 1f * density, mark), paint);
        paint.setColor(Theme.MINT);
        canvas.drawCircle(cx + r * 0.44f, cy + r * 0.42f, 2f * density, paint);

        text.setTextSize(5.5f * density);
        text.setTypeface(Theme.monoMedium);
        text.setColor(Theme.TEXT_FAINT);
        text.setLetterSpacing(0.12f);
        Ui.textCenter(canvas, "AETHER", cx, cy + r * 0.62f, text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                dragging = false;
                downRawX = event.getRawX();
                downRawY = event.getRawY();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - downRawX;
                float dy = event.getRawY() - downRawY;
                if (!dragging && Math.hypot(dx, dy) > slop) {
                    dragging = true;
                }
                if (dragging && listener != null) {
                    listener.onMove(startX + dx, startY + dy);
                }
                return true;
            case MotionEvent.ACTION_UP:
                pressed = false;
                if (dragging) {
                    if (listener != null) {
                        listener.onRelease();
                    }
                } else {
                    performClick();
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                dragging = false;
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (listener != null) {
            listener.onTap();
        }
        return true;
    }
}
