package com.aether.pvp.render;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import com.aether.pvp.ui.Theme;
import com.aether.pvp.ui.Ui;

import java.util.Random;

/**
 * Android adaptation of the layered glass surface used by OpenNilore's
 * immediate-mode renderer. The desktop client draws an SDF rounded rectangle,
 * a blurred drop shadow and a gradient border in a single GL pass; Android's
 * Canvas has no custom fragment stage here, so the same result is composed from
 * a rounded path, a soft nine-ring shadow, a clipped noise plate, a specular
 * top edge and a gradient hairline.
 *
 * <p>This is an original Canvas renderer, not a copy of the LWJGL renderer.
 */
public final class AetherSurface {
    private static final Paint FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint STROKE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint NOISE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path CLIP = new Path();
    private static final RectF TMP = new RectF();
    private static final Matrix MATRIX = new Matrix();
    private static Bitmap noiseBitmap;
    private static BitmapShader noiseShader;

    private AetherSurface() {
    }

    /** Full floating window: shadow, smoked glass, grain, specular edge and gradient rim. */
    public static void panel(Canvas c, RectF r, float radius, float u, int accent) {
        shadow(c, r, radius, u, 9);
        int top = Color.argb(238, 13, 15, 22);
        int bottom = Color.argb(249, 6, 7, 11);
        FILL.setStyle(Paint.Style.FILL);
        FILL.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                top, bottom, Shader.TileMode.CLAMP));
        c.drawRoundRect(r, radius, radius, FILL);
        FILL.setShader(null);

        // A cool interior wash keeps the surface from reading as flat black.
        int save = c.save();
        CLIP.reset();
        CLIP.addRoundRect(r, radius, radius, Path.Direction.CW);
        c.clipPath(CLIP);
        FILL.setShader(new LinearGradient(r.left, r.top, r.right, r.top + r.height() * 0.58f,
                Ui.alpha(accent, 0.075f), 0x00000000, Shader.TileMode.CLAMP));
        c.drawRect(r, FILL);
        FILL.setShader(null);
        noise(c, r, 0.055f);
        c.restoreToCount(save);

        // Specular top edge: brightest at the left shoulder, gone before the middle.
        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setStrokeWidth(1f);
        STROKE.setShader(new LinearGradient(r.left + radius, r.top, r.right - radius, r.top,
                Ui.alpha(accent, 0.62f), 0x14FFFFFF, Shader.TileMode.CLAMP));
        c.drawLine(r.left + radius, r.top + 0.5f, r.right - radius, r.top + 0.5f, STROKE);

        STROKE.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                Ui.alpha(accent, 0.34f), 0x1FFFFFFF, Shader.TileMode.CLAMP));
        c.drawRoundRect(r, radius, radius, STROKE);
        STROKE.setShader(null);

        // Hairline inner rim, inset by one pixel, catches the glass thickness.
        TMP.set(r);
        TMP.inset(1f, 1f);
        STROKE.setShader(new LinearGradient(TMP.left, TMP.top, TMP.right, TMP.bottom,
                0x18FFFFFF, 0x05FFFFFF, Shader.TileMode.CLAMP));
        c.drawRoundRect(TMP, radius - 1f, radius - 1f, STROKE);
        STROKE.setShader(null);
    }

    /** Smaller HUD plate: same material, thinner shadow so it never muddies gameplay. */
    public static void hud(Canvas c, RectF r, float radius, float u, int accent, float alpha) {
        shadow(c, r, radius, u * 0.6f, 4);
        FILL.setStyle(Paint.Style.FILL);
        FILL.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                Ui.alpha(0xFF0D1017, alpha), Ui.alpha(0xFF07090E, alpha), Shader.TileMode.CLAMP));
        c.drawRoundRect(r, radius, radius, FILL);
        FILL.setShader(null);

        int save = c.save();
        CLIP.reset();
        CLIP.addRoundRect(r, radius, radius, Path.Direction.CW);
        c.clipPath(CLIP);
        FILL.setShader(new LinearGradient(r.left, r.top, r.right, r.top + r.height(),
                Ui.alpha(accent, 0.06f), 0x00000000, Shader.TileMode.CLAMP));
        c.drawRect(r, FILL);
        FILL.setShader(null);
        noise(c, r, 0.04f);
        c.restoreToCount(save);

        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setStrokeWidth(1f);
        STROKE.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                Ui.alpha(accent, 0.28f), 0x14FFFFFF, Shader.TileMode.CLAMP));
        c.drawRoundRect(r, radius, radius, STROKE);
        STROKE.setShader(null);
    }

    /** Soft multi-ring shadow: the portable equivalent of a gaussian drop shadow. */
    public static void shadow(Canvas c, RectF r, float radius, float u, int rings) {
        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setShader(null);
        for (int i = rings; i >= 1; i--) {
            float grow = i * 2.1f * u;
            TMP.set(r);
            TMP.inset(-grow, -grow + 1.2f * u);
            STROKE.setStrokeWidth(2.2f * u);
            STROKE.setColor(Color.argb((int) (13f * (rings - i + 1) / rings), 0, 0, 0));
            c.drawRoundRect(TMP, radius + grow, radius + grow, STROKE);
        }
    }

    /** Accent bloom behind a title or island. */
    public static void glow(Canvas c, float cx, float cy, float radius, int color) {
        FILL.setStyle(Paint.Style.FILL);
        FILL.setShader(new RadialGradient(cx, cy, radius,
                Ui.alpha(color, 0.22f), 0x00000000, Shader.TileMode.CLAMP));
        c.drawCircle(cx, cy, radius, FILL);
        FILL.setShader(null);
    }

    /** Faint diagonal hairline grid used inside the sidebar / empty states. */
    public static void grid(Canvas c, RectF r, float step, int color) {
        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setStrokeWidth(0.6f);
        STROKE.setColor(color);
        STROKE.setShader(null);
        for (float x = r.left - r.height(); x < r.right; x += step) {
            c.drawLine(x, r.bottom, x + r.height(), r.top, STROKE);
        }
    }

    private static void noise(Canvas c, RectF r, float alpha) {
        if (noiseShader == null) {
            noiseShader = buildNoise();
            NOISE.setShader(noiseShader);
        }
        MATRIX.setTranslate(r.left, r.top);
        noiseShader.setLocalMatrix(MATRIX);
        NOISE.setAlpha(Math.round(255f * alpha));
        c.drawRect(r, NOISE);
    }

    private static BitmapShader buildNoise() {
        int size = 96;
        noiseBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[size * size];
        Random random = new Random(0xA37E2L);
        for (int i = 0; i < pixels.length; i++) {
            int v = random.nextInt(255);
            int a = random.nextFloat() < 0.62f ? 0 : 5 + random.nextInt(20);
            pixels[i] = Color.argb(a, v, v, v);
        }
        noiseBitmap.setPixels(pixels, 0, size, 0, 0, size, size);
        return new BitmapShader(noiseBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }
}
