package com.aether.pvp.model;

import com.aether.pvp.ui.Icons;

/**
 * Primary navigation destinations. The first seven mirror Nilore's categories;
 * HUD and Config remain AETHER's own interface surfaces.
 */
public enum Tab {
    COMBAT("Combat", "COMBAT / 01", "Precision, distilled for the duel meta.", Icons.SWORD),
    MOVEMENT("Movement", "MOVEMENT / 02", "Momentum with a lighter footprint.", Icons.WIND),
    PLAYER("Player", "PLAYER / 03", "Identity, inventory and quiet utilities.", Icons.USER),
    RENDER("Render", "RENDER / 04", "Atmosphere and information, restrained.", Icons.EYE),
    WORLD("World", "WORLD / 05", "Exploits framed as environmental control.", Icons.LOCK),
    MISC("Misc", "MISC / 06", "The small systems that keep a session tidy.", Icons.SLIDERS),
    GHOST("Ghost", "GHOST / 07", "Signals that leave no visible trace.", Icons.SEARCH),
    HUD("HUD", "INTERFACE / 08", "What remains when the menu is gone.", Icons.LAYOUT),
    CONFIG("Config", "CONFIG / 09", "Profiles for every tempo of play.", Icons.SLIDERS);

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
