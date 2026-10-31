package com.aether.pvp.model;

import com.aether.pvp.ui.Icons;

/**
 * Primary navigation destinations. Each tab owns an independent module collection.
 */
public enum Tab {
    COMBAT("Combat", "COMBAT / 01", "Precision, distilled for the 1.8 duel meta.", Icons.SWORD),
    MOVEMENT("Movement", "MOVEMENT / 02", "Momentum with a lighter footprint.", Icons.WIND),
    VISUALS("Visuals", "VISUALS / 03", "Atmosphere and information, restrained.", Icons.EYE),
    PLAYER("Player", "PLAYER / 04", "Identity, teams and quiet utilities.", Icons.USER),
    CONFIG("Config", "CONFIG / 05", "Profiles for every tempo of play.", Icons.SLIDERS);

    public final String label;
    public final String kicker;
    public final String subtitle;
    public final int icon;

    Tab(String label, String kicker, String subtitle, int icon) {
        this.label = label;
        this.kicker = kicker;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public java.util.List<Module> modules() {
        return com.aether.pvp.data.Content.modules(this);
    }
}
