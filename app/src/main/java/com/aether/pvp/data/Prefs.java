package com.aether.pvp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.aether.pvp.model.Module;
import com.aether.pvp.model.Tab;

/**
 * Tiny local persistence layer. No network, no telemetry, no game process.
 */
public final class Prefs {
    private static final String FILE = "aether_interface";
    private static final String ACTIVE_TAB = "active_tab";
    private static final String MENU_OPEN = "menu_open";
    private static final String LAUNCHER_X = "launcher_x";
    private static final String LAUNCHER_Y = "launcher_y";
    private final SharedPreferences sp;

    public Prefs(Context context) {
        sp = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public void load() {
        for (Module m : Content.all()) {
            m.enabled = sp.getBoolean(m.id + ".on", m.enabled);
            m.value = sp.getFloat(m.id + ".value", m.value);
            m.keybind = sp.getString(m.id + ".key", m.keybind);
            m.mode = sp.getInt(m.id + ".mode", m.mode);
            m.accent = sp.getInt(m.id + ".accent", m.accent);
            m.configA = sp.getFloat(m.id + ".a", m.configA);
            m.configB = sp.getFloat(m.id + ".b", m.configB);
            m.toggleAnim = m.enabled ? 1f : 0f;
        }
    }

    public void save(Module m) {
        sp.edit()
                .putBoolean(m.id + ".on", m.enabled)
                .putFloat(m.id + ".value", m.value)
                .apply();
    }

    public void saveValue(Module m) {
        sp.edit().putFloat(m.id + ".value", m.value).apply();
    }

    public void saveConfig(Module m) {
        sp.edit()
                .putString(m.id + ".key", m.keybind)
                .putInt(m.id + ".mode", m.mode)
                .putInt(m.id + ".accent", m.accent)
                .putFloat(m.id + ".a", m.configA)
                .putFloat(m.id + ".b", m.configB)
                .apply();
    }

    public int activeTab() {
        return sp.getInt(ACTIVE_TAB, 0);
    }

    public void setActiveTab(int index) {
        sp.edit().putInt(ACTIVE_TAB, index).apply();
    }

    public boolean menuOpen() {
        return sp.getBoolean(MENU_OPEN, true);
    }

    public void setMenuOpen(boolean open) {
        sp.edit().putBoolean(MENU_OPEN, open).apply();
    }

    /** Persisted orb position; -1 means "place at the default right edge". */
    public int launcherX() {
        return sp.getInt(LAUNCHER_X, -1);
    }

    public int launcherY() {
        return sp.getInt(LAUNCHER_Y, -1);
    }

    public void setLauncherPosition(int x, int y) {
        sp.edit().putInt(LAUNCHER_X, x).putInt(LAUNCHER_Y, y).apply();
    }

    public void reset() {
        sp.edit().clear().apply();
        for (Module m : Content.all()) {
            m.enabled = m.id.equals("combat.aura")
                    || m.id.equals("combat.reach")
                    || m.id.equals("combat.velocity")
                    || m.id.equals("move.sprint")
                    || m.id.equals("move.speed")
                    || m.id.equals("move.noslow")
                    || m.id.equals("visual.esp")
                    || m.id.equals("visual.nametags")
                    || m.id.equals("visual.ambience")
                    || m.id.equals("visual.hud")
                    || m.id.equals("hud.watermark")
                    || m.id.equals("hud.arraylist")
                    || m.id.equals("hud.notifications")
                    || m.id.equals("hud.targethud")
                    || m.id.equals("hud.coordinates")
                    || m.id.equals("hud.island")
                    || m.id.equals("hud.potions")
                    || m.id.equals("hud.playerlist")
                    || m.id.equals("player.friends")
                    || m.id.equals("player.teams")
                    || m.id.equals("config.autosave");
            m.toggleAnim = m.enabled ? 1f : 0f;
            m.value = defaultValue(m.id, m.value);
            m.keybind = "NONE";
            m.mode = 0;
            m.accent = 0;
            m.configA = 0.5f;
            m.configB = 0.65f;
        }
    }

    private float defaultValue(String id, float fallback) {
        switch (id) {
            case "move.speed": return 3.2f;
            case "move.step": return 0.6f;
            case "move.timer": return 1f;
            case "visual.nametags": return 1f;
            case "visual.ambience": return 0.8f;
            default: return fallback;
        }
    }

    public int savedTab(Tab[] tabs) {
        int index = activeTab();
        return index >= 0 && index < tabs.length ? index : 0;
    }
}
