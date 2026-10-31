package com.aether.pvp.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

/** Small, allocation-conscious drawing helpers shared by the custom surface. */
public final class Ui {
    private Ui() {
    }

    public static Paint text(float size, int color, Typeface face, float tracking) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        p.setColor(color);
        p.setTextSize(size);
        p.setTypeface(face);
        p.setLetterSpacing(tracking);
        return p;
    }

    public static Paint fill(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        return p;
    }

    public static Paint stroke(int color, float width) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(width);
        p.setColor(color);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        return p;
    }

    public static int alpha(int color, float a) {
        return Color.argb((int) (Color.alpha(color) * clamp(a, 0f, 1f)),
                Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int mix(int a, int b, float t) {
        t = clamp(t, 0f, 1f);
        return Color.argb(
                (int) (Color.alpha(a) + (Color.alpha(b) - Color.alpha(a)) * t),
                (int) (Color.red(a) + (Color.red(b) - Color.red(a)) * t),
                (int) (Color.green(a) + (Color.green(b) - Color.green(a)) * t),
                (int) (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * t));
    }

    public static Shader horizontal(int start, int end, float x0, float x1) {
        return new LinearGradient(x0, 0f, x1, 0f, start, end, Shader.TileMode.CLAMP);
    }

    public static Shader vertical(int start, int end, float y0, float y1) {
        return new LinearGradient(0f, y0, 0f, y1, start, end, Shader.TileMode.CLAMP);
    }

    public static Shader diagonal(int start, int end, RectF r) {
        return new LinearGradient(r.left, r.top, r.right, r.bottom,
                start, end, Shader.TileMode.CLAMP);
    }

    public static void roundRect(Canvas c, RectF r, float radius, Paint p) {
        c.drawRoundRect(r, radius, radius, p);
    }

    public static void border(Canvas c, RectF r, float radius, float width, Shader shader) {
        Paint p = stroke(Color.WHITE, width);
        p.setShader(shader);
        c.drawRoundRect(r, radius, radius, p);
    }

    public static void text(Canvas c, String value, float x, float y, Paint p) {
        c.drawText(value, x, y, p);
    }

    public static void textRight(Canvas c, String value, float right, float y, Paint p) {
        c.drawText(value, right - p.measureText(value), y, p);
    }

    public static void textCenter(Canvas c, String value, float cx, float y, Paint p) {
        c.drawText(value, cx - p.measureText(value) * 0.5f, y, p);
    }

    public static String ellipsize(String value, Paint paint, float maxWidth) {
        if (paint.measureText(value) <= maxWidth) {
            return value;
        }
        TextPaint tp = new TextPaint(paint);
        CharSequence out = TextUtils.ellipsize(value, tp, maxWidth, TextUtils.TruncateAt.END);
        return out.toString();
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float easeOutCubic(float t) {
        t = clamp(t, 0f, 1f);
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }

    public static float easeInOutCubic(float t) {
        t = clamp(t, 0f, 1f);
        return t < 0.5f
                ? 4f * t * t * t
                : 1f - (float) Math.pow(-2f * t + 2f, 3f) / 2f;
    }

    public static void dot(Canvas c, float cx, float cy, float radius, int color) {
        Paint p = fill(color);
        c.drawCircle(cx, cy, radius, p);
    }

    public static void glowDot(Canvas c, float cx, float cy, float radius, int color) {
        for (int i = 3; i >= 1; i--) {
            float rr = radius * (1f + i * 1.5f);
            c.drawCircle(cx, cy, rr, fill(alpha(color, 0.06f / i)));
        }
        c.drawCircle(cx, cy, radius, fill(color));
    }

    public static void hairline(Canvas c, float x0, float y0, float x1, float y1,
                                int colorStart, int colorEnd, float width) {
        Paint p = stroke(Color.WHITE, width);
        p.setShader(horizontal(colorStart, colorEnd, x0, x1));
        c.drawLine(x0, y0, x1, y1, p);
    }

    public static Path sparkle(float cx, float cy, float radius) {
        Path p = new Path();
        p.moveTo(cx, cy - radius);
        p.quadTo(cx + radius * 0.16f, cy - radius * 0.16f, cx + radius, cy);
        p.quadTo(cx + radius * 0.16f, cy + radius * 0.16f, cx, cy + radius);
        p.quadTo(cx - radius * 0.16f, cy + radius * 0.16f, cx - radius, cy);
        p.quadTo(cx - radius * 0.16f, cy - radius * 0.16f, cx, cy - radius);
        p.close();
        return p;
    }
}
