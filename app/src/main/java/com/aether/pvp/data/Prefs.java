package com.aether.pvp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.aether.pvp.model.ConfigSetting;
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
            loadSettings(m);
        }
    }

    /** Restores every generic Nilore-parity setting for one module. */
    public void loadSettings(Module m) {
        for (int i = 0; i < m.settings.size(); i++) {
            ConfigSetting s = m.settings.get(i);
            String base = m.id + ".s." + i + ".";
            s.reset();
            if (s.type == ConfigSetting.BOOLEAN) {
                s.bool = sp.getBoolean(base + "b", s.bool);
            } else if (s.type == ConfigSetting.NUMBER) {
                s.number = sp.getFloat(base + "n", s.number);
            } else if (s.type == ConfigSetting.MODE) {
                int index = sp.getInt(base + "m", s.modeIndex());
                if (index >= 0 && index < s.options.length) {
                    s.mode = s.options[index];
                }
            } else if (s.type == ConfigSetting.MULTI) {
                String stored = sp.getString(base + "x", "");
                if (!stored.isEmpty()) {
                    String[] parts = stored.split(",");
                    for (int k = 0; k < s.selected.length; k++) {
                        s.selected[k] = k < parts.length && "1".equals(parts[k]);
                    }
                }
            }
        }
    }

    public void saveSettings(Module m) {
        SharedPreferences.Editor edit = sp.edit();
        for (int i = 0; i < m.settings.size(); i++) {
            ConfigSetting s = m.settings.get(i);
            String base = m.id + ".s." + i + ".";
            if (s.type == ConfigSetting.BOOLEAN) {
                edit.putBoolean(base + "b", s.bool);
            } else if (s.type == ConfigSetting.NUMBER) {
                edit.putFloat(base + "n", s.number);
            } else if (s.type == ConfigSetting.MODE) {
                edit.putInt(base + "m", s.modeIndex());
            } else if (s.type == ConfigSetting.MULTI) {
                StringBuilder sb = new StringBuilder();
                for (boolean on : s.selected) {
                    sb.append(on ? '1' : '0').append(',');
                }
                edit.putString(base + "x", sb.toString());
            }
        }
        edit.apply();
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
            m.enabled = m.defaultEnabled;
            m.toggleAnim = m.enabled ? 1f : 0f;
            m.value = defaultValue(m.id, m.value);
            m.keybind = "NONE";
            m.mode = 0;
            m.accent = 0;
            m.configA = 0.5f;
            m.configB = 0.65f;
            for (ConfigSetting setting : m.settings) {
                setting.reset();
            }
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
