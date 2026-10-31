package com.aether.pvp.ui;

import android.content.res.Resources;
import android.graphics.Typeface;

import com.aether.pvp.R;

/**
 * Single source of truth for AETHER's quiet, nocturnal palette and type.
 */
public final class Theme {
    public static final int VOID = 0xFF07080C;
    public static final int PANEL = 0xFF0B0D13;
    public static final int PANEL_DEEP = 0xFF090B10;
    public static final int SURFACE = 0xFFFFFFFF;
    public static final int TEXT = 0xFFF4F2F8;
    public static final int TEXT_SOFT = 0xFFD6D2E0;
    public static final int TEXT_DIM = 0xFF9693A3;
    public static final int TEXT_FAINT = 0xFF666472;
    public static final int LAVENDER = 0xFFC9B8FF;
    public static final int MINT = 0xFF9BE8DD;
    public static final int VIOLET = 0xFF8B7BFF;
    public static final int ROSE = 0xFFF0A8B8;

    public static Typeface light;
    public static Typeface regular;
    public static Typeface medium;
    public static Typeface semibold;
    public static Typeface mono;
    public static Typeface monoMedium;

    private Theme() {
    }

    public static void load(Resources resources) {
        light = resources.getFont(R.font.sora_light);
        regular = resources.getFont(R.font.sora_regular);
        medium = resources.getFont(R.font.sora_medium);
        semibold = resources.getFont(R.font.sora_semibold);
        mono = resources.getFont(R.font.plex_mono_regular);
        monoMedium = resources.getFont(R.font.plex_mono_medium);
    }
}
