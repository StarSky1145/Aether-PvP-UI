package com.aether.pvp.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/** Hand-tuned vector glyphs, drawn in a normalised 1 x 1 box. */
public final class Icons {
    public static final int SWORD = 1;
    public static final int WIND = 2;
    public static final int EYE = 3;
    public static final int USER = 4;
    public static final int SLIDERS = 5;
    public static final int CLOSE = 6;
    public static final int CHEVRON_DOWN = 7;
    public static final int SEARCH = 8;
    public static final int CHECK = 9;
    public static final int ARROW_RIGHT = 10;
    public static final int LOCK = 11;

    private Icons() {
    }

    public static void draw(Canvas c, int icon, float cx, float cy, float size, Paint source) {
        Paint p = new Paint(source);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(1.15f, size * 0.065f));
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setShader(null);

        float s = size * 0.5f;
        switch (icon) {
            case SWORD: {
                Path path = new Path();
                path.moveTo(cx - s * 0.66f, cy + s * 0.72f);
                path.lineTo(cx + s * 0.68f, cy - s * 0.68f);
                path.moveTo(cx + s * 0.28f, cy - s * 0.82f);
                path.lineTo(cx + s * 0.82f, cy - s * 0.28f);
                path.moveTo(cx - s * 0.52f, cy + s * 0.18f);
                path.lineTo(cx - s * 0.14f, cy + s * 0.56f);
                c.drawPath(path, p);
                c.drawCircle(cx - s * 0.72f, cy + s * 0.78f, s * 0.09f, p);
                break;
            }
            case WIND: {
                Path path = new Path();
                path.moveTo(cx - s * 0.78f, cy - s * 0.2f);
                path.cubicTo(cx - s * 0.2f, cy - s * 0.42f, cx + s * 0.18f, cy - s * 0.08f,
                        cx + s * 0.78f, cy - s * 0.34f);
                path.moveTo(cx - s * 0.62f, cy + s * 0.16f);
                path.cubicTo(cx - s * 0.06f, cy - s * 0.02f, cx + s * 0.18f, cy + s * 0.28f,
                        cx + s * 0.62f, cy + s * 0.06f);
                path.moveTo(cx - s * 0.36f, cy + s * 0.52f);
                path.cubicTo(cx - s * 0.05f, cy + s * 0.38f, cx + s * 0.12f, cy + s * 0.56f,
                        cx + s * 0.4f, cy + s * 0.44f);
                c.drawPath(path, p);
                break;
            }
            case EYE: {
                Path path = new Path();
                path.moveTo(cx - s * 0.82f, cy);
                path.quadTo(cx, cy - s * 0.62f, cx + s * 0.82f, cy);
                path.quadTo(cx, cy + s * 0.62f, cx - s * 0.82f, cy);
                c.drawPath(path, p);
                c.drawCircle(cx, cy, s * 0.18f, p);
                break;
            }
            case USER: {
                c.drawCircle(cx, cy - s * 0.22f, s * 0.24f, p);
                Path path = new Path();
                path.moveTo(cx - s * 0.6f, cy + s * 0.66f);
                path.cubicTo(cx - s * 0.52f, cy + s * 0.12f, cx - s * 0.2f, cy + s * 0.05f, cx, cy + s * 0.05f);
                path.cubicTo(cx + s * 0.2f, cy + s * 0.05f, cx + s * 0.52f, cy + s * 0.12f,
                        cx + s * 0.6f, cy + s * 0.66f);
                c.drawPath(path, p);
                break;
            }
            case SLIDERS: {
                float[] ys = {-0.34f, 0f, 0.34f};
                float[] xs = {0.16f, -0.22f, 0.04f};
                for (int i = 0; i < 3; i++) {
                    float y = cy + s * ys[i];
                    c.drawLine(cx - s * 0.72f, y, cx + s * 0.72f, y, p);
                    c.drawCircle(cx + s * xs[i], y, s * 0.12f, p);
                }
                break;
            }
            case CLOSE: {
                c.drawLine(cx - s * 0.48f, cy - s * 0.48f, cx + s * 0.48f, cy + s * 0.48f, p);
                c.drawLine(cx + s * 0.48f, cy - s * 0.48f, cx - s * 0.48f, cy + s * 0.48f, p);
                break;
            }
            case CHEVRON_DOWN: {
                Path path = new Path();
                path.moveTo(cx - s * 0.4f, cy - s * 0.16f);
                path.lineTo(cx, cy + s * 0.24f);
                path.lineTo(cx + s * 0.4f, cy - s * 0.16f);
                c.drawPath(path, p);
                break;
            }
            case SEARCH: {
                c.drawCircle(cx - s * 0.16f, cy - s * 0.16f, s * 0.44f, p);
                c.drawLine(cx + s * 0.18f, cy + s * 0.18f, cx + s * 0.62f, cy + s * 0.62f, p);
                break;
            }
            case CHECK: {
                Path path = new Path();
                path.moveTo(cx - s * 0.52f, cy);
                path.lineTo(cx - s * 0.12f, cy + s * 0.36f);
                path.lineTo(cx + s * 0.56f, cy - s * 0.42f);
                c.drawPath(path, p);
                break;
            }
            case ARROW_RIGHT: {
                c.drawLine(cx - s * 0.52f, cy, cx + s * 0.46f, cy, p);
                Path path = new Path();
                path.moveTo(cx + s * 0.08f, cy - s * 0.38f);
                path.lineTo(cx + s * 0.48f, cy);
                path.lineTo(cx + s * 0.08f, cy + s * 0.38f);
                c.drawPath(path, p);
                break;
            }
            case LOCK: {
                RectF r = new RectF(cx - s * 0.48f, cy - s * 0.04f, cx + s * 0.48f, cy + s * 0.62f);
                c.drawRoundRect(r, s * 0.12f, s * 0.12f, p);
                Path path = new Path();
                path.moveTo(cx - s * 0.3f, cy - s * 0.02f);
                path.lineTo(cx - s * 0.3f, cy - s * 0.28f);
                path.quadTo(cx, cy - s * 0.76f, cx + s * 0.3f, cy - s * 0.28f);
                path.lineTo(cx + s * 0.3f, cy - s * 0.02f);
                c.drawPath(path, p);
                break;
            }
            default:
                break;
        }
    }
}
