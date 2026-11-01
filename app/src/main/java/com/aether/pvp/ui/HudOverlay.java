package com.aether.pvp.ui;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;

import com.aether.pvp.data.Content;
import com.aether.pvp.model.Module;
import com.aether.pvp.render.AetherSurface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The in-game layer: watermark, arraylist, notifications, target HUD and
 * movement keys. Everything is local, deterministic sample data.
 */
public final class HudOverlay {
    private final Paint pBrand = Ui.text(12f, Theme.TEXT, Theme.semibold, 0.10f);
    private final Paint pSmall = Ui.text(6f, Theme.TEXT_DIM, Theme.mono, 0.10f);
    private final Paint pTiny = Ui.text(5f, Theme.TEXT_FAINT, Theme.mono, 0.14f);
    private final Paint pRow = Ui.text(8.5f, Theme.TEXT, Theme.medium, 0.01f);
    private final Paint pName = Ui.text(9f, Theme.TEXT, Theme.medium, 0.01f);
    private final Paint pValue = Ui.text(7f, Theme.TEXT_SOFT, Theme.monoMedium, 0.04f);
    private final Paint pNoteTitle = Ui.text(8.2f, Theme.TEXT, Theme.medium, 0.01f);
    private final Paint pNoteBody = Ui.text(6.2f, Theme.TEXT_DIM, Theme.regular, 0.005f);
    private final Paint pKey = Ui.text(7.5f, Theme.TEXT_SOFT, Theme.monoMedium, 0.06f);
    private final Paint pIsland = Ui.text(9f, Theme.TEXT, Theme.medium, 0.02f);
    private final Paint pPotion = Ui.text(7f, Theme.TEXT_SOFT, Theme.medium, 0.01f);
    private final Paint pPotionTime = Ui.text(5.4f, Theme.TEXT_FAINT, Theme.mono, 0.04f);
    private final Paint pList = Ui.text(7f, Theme.TEXT_SOFT, Theme.medium, 0.01f);
    private final Paint pListPing = Ui.text(5.2f, Theme.TEXT_FAINT, Theme.mono, 0.04f);

    private final RectF temp = new RectF();
    private final List<Module> active = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();
    private final Map<String, Float> rowAnim = new HashMap<>();
    private final List<Note> notes = new ArrayList<>();
    private final SimpleDateFormat clock = new SimpleDateFormat("HH:mm:ss", Locale.US);

    private float w;
    private float h;
    private float u = 1f;
    private float safeLeft;
    private float safeTop;
    private float safeRight;
    private float safeBottom;

    public void layout(float width, float height, float unit,
                       float left, float top, float right, float bottom) {
        w = width;
        h = height;
        u = Math.max(0.5f, unit);
        safeLeft = left;
        safeTop = top;
        safeRight = right;
        safeBottom = bottom;
    }

    public void push(String title, String detail) {
        push(title, detail, Theme.LAVENDER);
    }

    public void push(String title, String detail, int accent) {
        notes.add(new Note(title, detail, SystemClock.uptimeMillis(), accent));
        while (notes.size() > 4) {
            notes.remove(0);
        }
    }

    public void tick(float dt, long now) {
        for (Map.Entry<String, Float> entry : rowAnim.entrySet()) {
            float target = 1f;
            float value = entry.getValue();
            if (value < target) {
                entry.setValue(value + (target - value) * Math.min(1f, dt * 9f));
            }
        }
        Module noteModule = Content.find("hud.notifications");
        long life = 4200L;
        if (noteModule != null && noteModule.enabled) {
            life += (long) (noteModule.configA * 4200f);
        }
        for (int i = notes.size() - 1; i >= 0; i--) {
            if (now - notes.get(i).start > life + 700L) {
                notes.remove(i);
            }
        }
    }

    public void draw(Canvas canvas, float alpha, long now) {
        if (alpha <= 0.008f || w <= 0f || h <= 0f) {
            return;
        }
        int a = Math.round(255f * Ui.clamp(alpha, 0f, 1f));
        boolean layer = a < 255;
        if (layer) {
            canvas.saveLayerAlpha(null, a);
        } else {
            canvas.save();
        }
        drawCrosshair(canvas);
        drawWatermark(canvas, now);
        drawIsland(canvas, now);
        drawPotions(canvas);
        drawPlayerList(canvas);
        drawArrayList(canvas);
        drawTarget(canvas);
        drawNotes(canvas, now);
        drawCoordinates(canvas);
        drawKeys(canvas, now);
        canvas.restore();
    }

    private void drawCrosshair(Canvas canvas) {
        float cx = w * 0.5f;
        float cy = h * 0.5f;
        Paint p = Ui.stroke(0x8CFFFFFF, Math.max(1f, 0.9f * u));
        float gap = 4f * u;
        float len = 4.5f * u;
        canvas.drawLine(cx - gap - len, cy, cx - gap, cy, p);
        canvas.drawLine(cx + gap, cy, cx + gap + len, cy, p);
        canvas.drawLine(cx, cy - gap - len, cx, cy - gap, p);
        canvas.drawLine(cx, cy + gap, cx, cy + gap + len, p);
        canvas.drawCircle(cx, cy, 0.9f * u, Ui.fill(0x8CFFFFFF));
    }

    private void drawWatermark(Canvas canvas, long now) {
        Module m = Content.find("hud.watermark");
        if (m == null || !m.enabled) {
            return;
        }
        float scale = 0.88f + m.configA * 0.24f;
        int accent = accent(m);
        canvas.saveLayerAlpha(null, moduleAlpha(m));
        float x = safeLeft + 22f * u;
        float y = safeTop + 24f * u;
        pBrand.setTextSize(12.5f * u * scale);
        pBrand.setColor(Theme.TEXT);
        pSmall.setTextSize(5.7f * u * scale);
        pTiny.setTextSize(5f * u * scale);
        String time = clock.format(new Date(now));

        if (m.mode == 2) {
            pBrand.setTextSize(18f * u * scale);
            canvas.drawText("AETHER", x, y, pBrand);
            Paint line = Ui.fill(Ui.alpha(accent, 0.8f));
            canvas.drawRect(x, y + 5f * u * scale, x + 38f * u * scale, y + 6f * u * scale, line);
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("PVP INTERFACE", x, y + 16f * u * scale, pTiny);
            drawStats(canvas, x + 112f * u * scale, y - 1f * u * scale, time, scale);
            canvas.restore();
            return;
        }

        String brand = "AETHER";
        String version = "1.0";
        float brandW = pBrand.measureText(brand);
        pSmall.setColor(Theme.TEXT_DIM);
        String stats = "238 FPS   ·   12 MS   ·   " + time;
        String meta = version + "  ·  INTERFACE";
        float metaW = pTiny.measureText(meta);
        float contentW = Math.max(brandW + 12f * u * scale + metaW,
                154f * u * scale) + 28f * u * scale;
        float width = Math.max(contentW, 218f * u * scale);
        float height = 32f * u * scale;
        RectF pill = temp;
        pill.set(x, y, x + width, y + height);

        if (m.mode == 1) {
            canvas.drawRoundRect(pill, 16f * u * scale, 16f * u * scale,
                    Ui.fill(0xC40A0C12));
            Paint border = Ui.stroke(0xFFFFFFFF, 1f);
            border.setShader(new LinearGradient(pill.left, pill.top, pill.right, pill.bottom,
                    Ui.alpha(accent, 0.38f), 0x16FFFFFF, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(pill, 16f * u * scale, 16f * u * scale, border);
            drawLogo(canvas, pill.left + 17f * u * scale, pill.centerY(), 13f * u * scale, accent);
            canvas.drawText(brand, pill.left + 31f * u * scale, pill.centerY() - 0.5f * u * scale, pBrand);
            canvas.drawText(version, pill.left + 31f * u * scale + brandW + 5f * u * scale,
                    pill.centerY() + 1f * u * scale, pTiny);
            pSmall.setColor(Theme.TEXT_FAINT);
            Ui.textRight(canvas, stats, pill.right - 13f * u * scale,
                    pill.centerY() + 2.2f * u * scale, pSmall);
            canvas.restore();
            return;
        }

        // Minimal: no chrome, just a quiet signature and a hairline.
        drawLogo(canvas, x + 6f * u * scale, y - 3f * u * scale, 10f * u * scale, accent);
        canvas.drawText(brand, x + 17f * u * scale, y, pBrand);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText(meta, x + 17f * u * scale + brandW + 7f * u * scale,
                y, pTiny);
        Paint line = Ui.fill(Ui.alpha(accent, 0.55f));
        canvas.drawRect(x + 17f * u * scale, y + 4f * u * scale,
                x + 17f * u * scale + 34f * u * scale, y + 5f * u * scale, line);
        drawStats(canvas, x, y + 16f * u * scale, time, scale);
        canvas.restore();
    }

    private void drawStats(Canvas canvas, float x, float y, String time, float scale) {
        pSmall.setColor(Theme.TEXT_DIM);
        String stats = "238 FPS   ·   12 MS   ·   " + time;
        canvas.drawText(stats, x, y + 9f * u * scale, pSmall);
    }

    private void drawLogo(Canvas canvas, float cx, float cy, float radius, int accent) {
        Paint glow = Ui.fill(Ui.alpha(accent, 0.16f));
        canvas.drawCircle(cx, cy, radius * 1.45f, glow);
        canvas.drawPath(Ui.sparkle(cx, cy, radius),
                Ui.fill(Ui.alpha(accent, 0.95f)));
    }


    /**
     * Top-centre capsule inspired by OpenNilore's DynamicIsland: one surface
     * that changes what it says instead of adding another badge to the screen.
     */
    private void drawIsland(Canvas canvas, long now) {
        Module m = Content.find("hud.island");
        if (m == null || !m.enabled) {
            return;
        }
        int accent = accent(m);
        int alpha = moduleAlpha(m);
        boolean expanded = m.mode == 2;
        float pillW = (expanded ? 268f : 214f) * u;
        float pillH = (expanded ? 34f : 27f) * u;
        float cx = w * 0.5f;
        float top = safeTop + 9f * u;
        RectF pill = new RectF(cx - pillW * 0.5f, top, cx + pillW * 0.5f, top + pillH);

        canvas.saveLayerAlpha(null, alpha);
        AetherSurface.hud(canvas, pill, pillH * 0.5f, u, accent, alpha / 255f);

        int phase = (int) ((now / 3600L) % 3L);
        String title;
        String value;
        if (m.mode == 1) {
            title = "AETHER";
            value = "LIVE";
        } else if (expanded) {
            title = "AETHER  /  SESSION";
            value = "238 FPS   ·   12 MS   ·   " + clock.format(new Date(now));
        } else if (phase == 1) {
            title = "SCAFFOLD";
            value = "64 / 64";
        } else if (phase == 2) {
            title = "EVENT";
            value = "SKY 04:12";
        } else {
            title = "AETHER";
            value = "238 FPS   ·   12 MS";
        }

        drawLogo(canvas, pill.left + 14f * u, pill.centerY(), 6.4f * u, accent);
        pIsland.setTextSize((expanded ? 8.8f : 8.1f) * u);
        pIsland.setColor(Theme.TEXT);
        canvas.drawText(title, pill.left + 25f * u,
                pill.centerY() + 2.7f * u, pIsland);

        pSmall.setTextSize(5.5f * u);
        pSmall.setColor(expanded ? Theme.MINT : Theme.TEXT_DIM);
        Ui.textRight(canvas, value, pill.right - 13f * u,
                pill.centerY() + 2.2f * u, pSmall);

        // A tiny breathing status dot anchors the capsule without a glow.
        float pulse = 0.72f + 0.28f * (float) Math.sin(now / 380.0);
        canvas.drawCircle(pill.left + 10.5f * u, pill.centerY(),
                1.7f * u, Ui.fill(Ui.alpha(Theme.MINT, pulse)));
        if (expanded) {
            Paint hair = Ui.stroke(0x18FFFFFF, 1f);
            canvas.drawLine(pill.left + 12f * u, pill.bottom - 6f * u,
                    pill.right - 12f * u, pill.bottom - 6f * u, hair);
        }
        canvas.restore();
    }

    private void drawPotions(Canvas canvas) {
        Module m = Content.find("hud.potions");
        if (m == null || !m.enabled) {
            return;
        }
        int accent = accent(m);
        int alpha = moduleAlpha(m);
        float x = safeLeft + 22f * u;
        float top = safeTop + 61f * u;
        String[] names = {"Speed II", "Strength I", "Regeneration", "Resistance"};
        String[] times = {"2:14", "1:02", "0:38", "1:51"};

        canvas.saveLayerAlpha(null, alpha);
        if (m.mode == 2) {
            pPotion.setTextSize(6.4f * u);
            pPotion.setColor(Theme.TEXT_DIM);
            canvas.drawText("SPD II   ·   STR I   ·   REG I", x, top + 8f * u, pPotion);
            canvas.restore();
            return;
        }
        float width = 138f * u;
        float header = 22f * u;
        float rowH = 16f * u;
        int shown = m.mode == 1 ? 2 : 4;
        float height = header + shown * rowH + 7f * u;
        RectF card = new RectF(x, top, x + width, top + height);
        AetherSurface.hud(canvas, card, 9f * u, u, accent, alpha / 255f);

        pTiny.setTextSize(4.9f * u);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText("EFFECTS", x + 11f * u, top + 13f * u, pTiny);
        pTiny.setColor(Theme.MINT);
        Ui.textRight(canvas, shown + " ACTIVE", x + width - 11f * u, top + 13f * u, pTiny);

        for (int i = 0; i < shown; i++) {
            float rowY = top + header + i * rowH;
            int color = i == 0 ? Theme.MINT : i == 1 ? Theme.ROSE : Theme.LAVENDER;
            canvas.drawCircle(x + 12f * u, rowY + rowH * 0.45f, 1.7f * u,
                    Ui.fill(Ui.alpha(color, 0.9f)));
            pPotion.setTextSize(6.7f * u);
            pPotion.setColor(Theme.TEXT_SOFT);
            canvas.drawText(names[i], x + 19f * u, rowY + 10f * u, pPotion);
            pPotionTime.setTextSize(5.2f * u);
            pPotionTime.setColor(Theme.TEXT_FAINT);
            Ui.textRight(canvas, times[i], x + width - 11f * u, rowY + 10f * u, pPotionTime);
        }
        canvas.restore();
    }

    private void drawPlayerList(Canvas canvas) {
        Module m = Content.find("hud.playerlist");
        if (m == null || !m.enabled) {
            return;
        }
        Module potions = Content.find("hud.potions");
        boolean potionsOn = potions != null && potions.enabled;
        float top = safeTop + 61f * u;
        if (potionsOn) {
            int shown = potions.mode == 1 ? 2 : 4;
            float potionH = potions.mode == 2 ? 10f * u : 22f * u + shown * 16f * u + 7f * u;
            top += potionH + 8f * u;
        }
        int accent = accent(m);
        int alpha = moduleAlpha(m);
        float x = safeLeft + 22f * u;
        float width = 146f * u;
        float header = 21f * u;
        float rowH = 15f * u;
        int shown = m.mode == 1 ? 3 : 5;
        float height = header + shown * rowH + 6f * u;
        top = Math.min(top, h - safeBottom - height - 92f * u);
        RectF card = new RectF(x, top, x + width, top + height);

        canvas.saveLayerAlpha(null, alpha);
        AetherSurface.hud(canvas, card, 9f * u, u, accent, alpha / 255f);
        pTiny.setTextSize(4.9f * u);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText("PLAYERS", x + 11f * u, top + 13f * u, pTiny);
        pTiny.setColor(Theme.TEXT_DIM);
        Ui.textRight(canvas, "05", x + width - 11f * u, top + 13f * u, pTiny);

        String[] names = {"Sora_", "Vale", "Mira", "Kite", "Nyx"};
        String[] pings = {"12", "24", "31", "48", "67"};
        int[] colors = {Theme.MINT, Theme.LAVENDER, Theme.ROSE, Theme.TEXT_SOFT, Theme.TEXT_DIM};
        for (int i = 0; i < shown; i++) {
            float rowY = top + header + i * rowH;
            canvas.drawCircle(x + 12f * u, rowY + rowH * 0.42f, 1.6f * u,
                    Ui.fill(Ui.alpha(colors[i], 0.9f)));
            pList.setTextSize(6.6f * u);
            pList.setColor(i == 0 ? Theme.TEXT : Theme.TEXT_SOFT);
            canvas.drawText(names[i], x + 19f * u, rowY + 9.5f * u, pList);
            pListPing.setTextSize(5f * u);
            pListPing.setColor(Theme.TEXT_FAINT);
            Ui.textRight(canvas, pings[i] + " MS", x + width - 11f * u,
                    rowY + 9.5f * u, pListPing);
        }
        canvas.restore();
    }

    private void drawArrayList(Canvas canvas) {
        Module config = Content.find("hud.arraylist");
        if (config == null || !config.enabled) {
            return;
        }
        active.clear();
        rows.clear();
        for (Module m : Content.all()) {
            if (!m.enabled || m.isAction() || m.id.startsWith("hud.")
                    || m.id.startsWith("config.")) {
                continue;
            }
            pRow.setTextSize(8.5f * u);
            rows.add(new Row(m, pRow.measureText(m.name), category(m.id)));
            if (!rowAnim.containsKey(m.id)) {
                rowAnim.put(m.id, 0.55f);
            }
        }
        int mode = config.mode;
        if (mode == 1) {
            Collections.sort(rows, (a, b) -> a.module.name.compareToIgnoreCase(b.module.name));
        } else if (mode == 2) {
            Collections.sort(rows, (a, b) -> {
                int c = Integer.compare(a.category, b.category);
                return c != 0 ? c : Float.compare(b.width, a.width);
            });
        } else {
            Collections.sort(rows, (a, b) -> Float.compare(b.width, a.width));
        }
        canvas.saveLayerAlpha(null, moduleAlpha(config));
        int maxRows = Math.min(9, rows.size());
        float right = w - safeRight - 22f * u;
        float y = safeTop + 25f * u;
        float rowH = 13.6f * u;
        float gap = 1.6f * u;
        float pad = (14f + config.configA * 10f) * u;
        int accent = accent(config);
        for (int i = 0; i < maxRows; i++) {
            Row row = rows.get(i);
            float anim = rowAnim.getOrDefault(row.module.id, 1f);
            float width = (row.width + pad) * anim;
            if (width < 2f) {
                y += rowH + gap;
                continue;
            }
            RectF plate = temp;
            plate.set(right - width, y, right, y + rowH);
            canvas.drawRoundRect(plate, 2.5f * u, 2.5f * u, Ui.fill(0x7A090B11));
            float textAlpha = Ui.clamp(anim * 1.25f, 0f, 1f);
            int categoryColor = categoryColor(row.category);
            canvas.drawRect(right - 2.2f * u, y + 2.5f * u, right,
                    y + rowH - 2.5f * u, Ui.fill(Ui.alpha(accent, 0.8f * textAlpha)));
            canvas.drawCircle(right - width + 7f * u, y + rowH * 0.5f, 1.5f * u,
                    Ui.fill(Ui.alpha(categoryColor, 0.75f * textAlpha)));
            pRow.setColor(Ui.alpha(Theme.TEXT, textAlpha));
            pRow.setTextSize(8.5f * u);
            Ui.textRight(canvas, row.module.name, right - 8f * u,
                    y + rowH * 0.5f + 3f * u, pRow);
            y += rowH + gap;
        }
        canvas.restore();
    }

    private void drawTarget(Canvas canvas) {
        Module m = Content.find("hud.targethud");
        if (m == null || !m.enabled) {
            return;
        }
        float scale = 0.9f + m.configA * 0.16f;
        float width = 188f * u * scale;
        float height = 50f * u * scale;
        float centerX = w * 0.5f;
        float top = h - safeBottom - 72f * u - height;
        RectF card = temp;
        card.set(centerX - width * 0.5f, top, centerX + width * 0.5f, top + height);
        int accent = accent(m);
        canvas.saveLayerAlpha(null, moduleAlpha(m));
        if (m.mode >= 1) {
            canvas.drawRoundRect(card, 11f * u, 11f * u, Ui.fill(0xB80A0C12));
            Paint border = Ui.stroke(m.mode == 2 ? Ui.alpha(accent, 0.42f)
                    : Ui.alpha(Theme.TEXT, 0.13f), 1f);
            canvas.drawRoundRect(card, 11f * u, 11f * u, border);
        }
        float avatarX = card.left + 17f * u * scale;
        float avatarY = card.top + 17f * u * scale;
        canvas.drawCircle(avatarX, avatarY, 10f * u * scale, Ui.fill(0x1AFFFFFF));
        Paint avatar = Ui.stroke(Ui.alpha(accent, 0.9f), 1.2f * u);
        canvas.drawCircle(avatarX, avatarY, 5f * u * scale, avatar);
        canvas.drawCircle(avatarX, avatarY - 2.5f * u * scale, 2f * u * scale, avatar);
        pName.setTextSize((m.mode == 2 ? 10.5f : 9f) * u * scale);
        pName.setColor(Theme.TEXT);
        canvas.drawText("Sora_", card.left + 33f * u * scale, card.top + 15f * u * scale, pName);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText("3.4 M   ·   NETHERITE", card.left + 33f * u * scale,
                card.top + 25f * u * scale, pTiny);
        pValue.setTextSize(6.7f * u * scale);
        pValue.setColor(Theme.TEXT_SOFT);
        Ui.textRight(canvas, "18.4", card.right - 10f * u * scale,
                card.top + 15f * u * scale, pValue);
        // Health rail.
        float railL = card.left + 33f * u * scale;
        float railR = card.right - 11f * u * scale;
        float railY = card.top + 33f * u * scale;
        canvas.drawRoundRect(railL, railY, railR, railY + 2.6f * u,
                1.3f * u, 1.3f * u, Ui.fill(0x14FFFFFF));
        float health = 0.78f;
        Paint bar = Ui.fill(0xFFFFFFFF);
        bar.setShader(new LinearGradient(railL, 0f, railR, 0f,
                Ui.alpha(Theme.MINT, 0.9f), Ui.alpha(accent, 0.9f), Shader.TileMode.CLAMP));
        canvas.drawRoundRect(railL, railY, railL + (railR - railL) * health,
                railY + 2.6f * u, 1.3f * u, 1.3f * u, bar);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText("ARMOR 20   ·   DIST 3.4", card.left + 33f * u * scale,
                card.top + 44f * u * scale, pTiny);
        canvas.restore();
    }

    private void drawNotes(Canvas canvas, long now) {
        Module config = Content.find("hud.notifications");
        if (config == null || !config.enabled || notes.isEmpty()) {
            return;
        }
        long life = 4200L + (long) (config.configA * 4200f);
        canvas.saveLayerAlpha(null, moduleAlpha(config));
        int maxNotes = config.mode == 1 ? 1 : (config.mode == 2 ? 2 : 3);
        float width = Math.min(252f * u, w * 0.31f);
        float height = (config.mode == 2 ? 34f : 39f) * u;
        float gap = 6f * u;
        float right = w - safeRight - 22f * u;
        float bottom = h - safeBottom - 20f * u;
        for (int i = 0; i < Math.min(maxNotes, notes.size()); i++) {
            Note note = notes.get(notes.size() - 1 - i);
            long age = now - note.start;
            float in = Ui.clamp(age / 170f, 0f, 1f);
            float out = Ui.clamp((life + 500L - age) / 380f, 0f, 1f);
            float a = Math.min(Ui.easeOutCubic(in), Ui.easeOutCubic(out));
            if (a <= 0.01f) {
                continue;
            }
            float top = bottom - height - i * (height + gap);
            float slide = (1f - Ui.easeOutCubic(in)) * 12f * u;
            RectF card = temp;
            card.set(right - width + slide, top, right + slide, top + height);
            int alpha = Math.round(a * 255f);
            canvas.saveLayerAlpha(null, alpha);
            canvas.drawRoundRect(card, 9f * u, 9f * u, Ui.fill(0xE60A0C12));
            Paint border = Ui.stroke(0xFFFFFFFF, 1f);
            border.setShader(new LinearGradient(card.left, card.top, card.right, card.bottom,
                    Ui.alpha(note.accent, 0.30f), 0x12FFFFFF, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(card, 9f * u, 9f * u, border);
            canvas.drawRect(card.left, card.top + 7f * u, card.left + 2.2f * u,
                    card.bottom - 7f * u, Ui.fill(note.accent));
            Paint dot = Ui.fill(Ui.alpha(note.accent, 0.9f));
            canvas.drawCircle(card.left + 15f * u, card.centerY(), 2.4f * u, dot);
            pNoteTitle.setTextSize(8.2f * u);
            pNoteTitle.setColor(Theme.TEXT);
            canvas.drawText(note.title, card.left + 24f * u, card.top + 15f * u, pNoteTitle);
            pNoteBody.setTextSize(6.2f * u);
            pNoteBody.setColor(Theme.TEXT_DIM);
            canvas.drawText(note.detail, card.left + 24f * u, card.top + 26f * u, pNoteBody);
            pTiny.setColor(Theme.TEXT_FAINT);
            Ui.textRight(canvas, age < 900L ? "NOW" : (age / 1000L) + "S",
                    card.right - 10f * u, card.top + 15f * u, pTiny);
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawCoordinates(Canvas canvas) {
        Module m = Content.find("hud.coordinates");
        if (m == null || !m.enabled) {
            return;
        }
        Module keys = Content.find("hud.keystrokes");
        boolean keyOn = keys != null && keys.enabled;
        canvas.saveLayerAlpha(null, moduleAlpha(m));
        float x = safeLeft + 22f * u;
        float baseline = h - safeBottom - (keyOn ? 84f * u : 22f * u);
        pTiny.setTextSize(5.2f * u);
        pTiny.setColor(Theme.TEXT_FAINT);
        canvas.drawText("POSITION", x, baseline - 10f * u, pTiny);
        pSmall.setTextSize(6.2f * u);
        pSmall.setColor(Theme.TEXT_SOFT);
        canvas.drawText("XYZ", x, baseline, pSmall);
        pValue.setTextSize(6.2f * u);
        pValue.setColor(Theme.TEXT);
        canvas.drawText("128  /  64  /  -302", x + 22f * u, baseline, pValue);
        if (m.mode == 2) {
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("DIM 0   ·   CHUNK 8 / -19", x, baseline + 9f * u, pTiny);
        } else if (m.mode == 1) {
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("OVERWORLD", x, baseline + 9f * u, pTiny);
        }
        canvas.restore();
    }

    private void drawKeys(Canvas canvas, long now) {
        Module m = Content.find("hud.keystrokes");
        if (m == null || !m.enabled) {
            return;
        }
        canvas.saveLayerAlpha(null, moduleAlpha(m));
        float key = (15f + m.configA * 8f) * u;
        float gap = 3f * u;
        float x = safeLeft + 22f * u;
        float bottom = h - safeBottom - 20f * u;
        int pressed = (int) ((now / 240L) % 4L);
        pTiny.setColor(Theme.TEXT_FAINT);
        if (m.mode == 2) {
            pKey.setTextSize(8f * u);
            pKey.setColor(Theme.TEXT_DIM);
            canvas.drawText("WASD", x, bottom, pKey);
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("MOVE", x + 27f * u, bottom, pTiny);
        } else if (m.mode == 1) {
            float top = bottom - key;
            drawKey(canvas, x, top, key, "W", pressed == 0);
            drawKey(canvas, x + key + gap, top, key, "A", pressed == 1);
            drawKey(canvas, x + (key + gap) * 2f, top, key, "S", pressed == 2);
            drawKey(canvas, x + (key + gap) * 3f, top, key, "D", pressed == 3);
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("MOVE", x, top - 6f * u, pTiny);
        } else {
            float top = bottom - key * 2f - gap;
            drawKey(canvas, x + key + gap, top, key, "W", pressed == 0);
            drawKey(canvas, x, top + key + gap, key, "A", pressed == 1);
            drawKey(canvas, x + key + gap, top + key + gap, key, "S", pressed == 2);
            drawKey(canvas, x + (key + gap) * 2f, top + key + gap, key, "D", pressed == 3);
            pTiny.setColor(Theme.TEXT_FAINT);
            canvas.drawText("MOVE", x, top - 6f * u, pTiny);
        }
        canvas.restore();
    }

    private void drawKey(Canvas canvas, float x, float y, float size, String label, boolean on) {
        RectF r = temp;
        r.set(x, y, x + size, y + size);
        canvas.drawRoundRect(r, 4f * u, 4f * u, Ui.fill(on ? 0x2AFFFFFF : 0x140A0C12));
        canvas.drawRoundRect(r, 4f * u, 4f * u, Ui.stroke(on ? 0x66FFFFFF : 0x1FFFFFFF, 1f));
        pKey.setTextSize(7.5f * u);
        pKey.setColor(on ? Theme.TEXT : Theme.TEXT_DIM);
        Ui.textCenter(canvas, label, r.centerX(), r.centerY() + 2.5f * u, pKey);
    }

    private int moduleAlpha(Module m) {
        return Math.round(255f * (0.58f + m.configB * 0.42f));
    }

    private int accent(Module m) {
        switch (m.accent) {
            case 1:
                return Theme.MINT;
            case 2:
                return Theme.ROSE;
            case 3:
                return Theme.TEXT;
            default:
                return Theme.LAVENDER;
        }
    }

    private int category(String id) {
        if (id.startsWith("nilore.combat.")) {
            return 0;
        }
        if (id.startsWith("nilore.movement.")) {
            return 1;
        }
        if (id.startsWith("nilore.render.")) {
            return 2;
        }
        if (id.startsWith("nilore.player.")) {
            return 3;
        }
        if (id.startsWith("nilore.ghost.")) {
            return 5;
        }
        return 4;
    }

    private int categoryColor(int category) {
        switch (category) {
            case 0:
                return Theme.ROSE;
            case 1:
                return Theme.MINT;
            case 2:
                return Theme.LAVENDER;
            case 3:
                return Theme.TEXT_SOFT;
            case 5:
                return Theme.MINT;
            default:
                return Theme.TEXT_FAINT;
        }
    }

    private static final class Row {
        final Module module;
        final float width;
        final int category;

        Row(Module module, float width, int category) {
            this.module = module;
            this.width = width;
            this.category = category;
        }
    }

    private static final class Note {
        final String title;
        final String detail;
        final long start;
        final int accent;

        Note(String title, String detail, long start, int accent) {
            this.title = title;
            this.detail = detail;
            this.start = start;
            this.accent = accent;
        }
    }
}
