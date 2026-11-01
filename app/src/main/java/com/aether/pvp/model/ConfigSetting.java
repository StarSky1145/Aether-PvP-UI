package com.aether.pvp.model;

import java.util.Locale;

/**
 * One editable entry inside a module, mirroring Nilore's four setting kinds:
 * boolean, number, mode and multi-select. Values are local UI state only.
 */
public final class ConfigSetting {
    public static final int BOOLEAN = 0;
    public static final int NUMBER = 1;
    public static final int MODE = 2;
    public static final int MULTI = 3;

    public final String name;
    public final int type;

    public boolean bool;
    public float number;
    public final float min;
    public final float max;
    public final float step;
    public String mode;
    public final String[] options;
    public final boolean[] selected;

    private final boolean defaultBool;
    private final float defaultNumber;
    private final String defaultMode;
    private final boolean[] defaultSelected;

    private ConfigSetting(String name, int type, boolean bool, float number,
                          float min, float max, float step, String mode,
                          String[] options, boolean[] selected) {
        this.name = name;
        this.type = type;
        this.bool = bool;
        this.defaultBool = bool;
        this.number = number;
        this.defaultNumber = number;
        this.min = min;
        this.max = max;
        this.step = step;
        this.mode = mode;
        this.defaultMode = mode;
        this.options = options == null ? new String[0] : options;
        this.selected = selected == null ? new boolean[0] : selected;
        this.defaultSelected = new boolean[this.selected.length];
        System.arraycopy(this.selected, 0, this.defaultSelected, 0, this.selected.length);
    }

    public static ConfigSetting bool(String name, boolean value) {
        return new ConfigSetting(name, BOOLEAN, value, 0f, 0f, 0f, 0f,
                null, null, null);
    }

    public static ConfigSetting number(String name, float value, float min,
                                       float max, float step) {
        return new ConfigSetting(name, NUMBER, false, value, min, max, step,
                null, null, null);
    }

    public static ConfigSetting mode(String name, String value, String... options) {
        return new ConfigSetting(name, MODE, false, 0f, 0f, 0f, 0f,
                value, options, null);
    }

    public static ConfigSetting multi(String name, String[] options, String[] defaults) {
        boolean[] on = new boolean[options == null ? 0 : options.length];
        if (options != null && defaults != null) {
            for (String d : defaults) {
                for (int i = 0; i < options.length; i++) {
                    if (options[i].equals(d)) {
                        on[i] = true;
                    }
                }
            }
        }
        return new ConfigSetting(name, MULTI, false, 0f, 0f, 0f, 0f,
                null, options, on);
    }

    public void reset() {
        bool = defaultBool;
        number = defaultNumber;
        mode = defaultMode;
        System.arraycopy(defaultSelected, 0, selected, 0,
                Math.min(defaultSelected.length, selected.length));
    }

    public void toggleBool() {
        bool = !bool;
    }

    public int modeIndex() {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(mode)) {
                return i;
            }
        }
        return 0;
    }

    public void cycleMode() {
        if (options.length == 0) {
            return;
        }
        mode = options[(modeIndex() + 1) % options.length];
    }

    public void setFromRatio(float ratio) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        float raw = min + clamped * (max - min);
        if (step > 0f) {
            raw = Math.round(raw / step) * step;
        }
        number = Math.max(min, Math.min(max, raw));
    }

    public float ratio() {
        if (max <= min) {
            return 0f;
        }
        return (number - min) / (max - min);
    }

    public String numberLabel() {
        if (step >= 1f) {
            return String.format(Locale.US, "%.0f", number);
        }
        if (step >= 0.1f) {
            return String.format(Locale.US, "%.1f", number);
        }
        return String.format(Locale.US, "%.2f", number);
    }
}
