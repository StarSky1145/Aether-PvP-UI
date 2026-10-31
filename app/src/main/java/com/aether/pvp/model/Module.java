package com.aether.pvp.model;

/**
 * A single visual module row. This is presentation data only: nothing here hooks,
 * injects into, or communicates with Minecraft.
 */
public final class Module {
    public final String id;
    public final String name;
    public final String description;
    public final String badge;

    public boolean enabled;
    public final boolean hasSlider;
    public float value;
    public final float min;
    public final float max;
    public final float step;
    public final String suffix;

    /** Non-null for profile rows in the Config tab. */
    public final String actionLabel;

    public float toggleAnim;

    private Module(String id, String name, String description, String badge,
                   boolean enabled, boolean hasSlider, float value, float min,
                   float max, float step, String suffix, String actionLabel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.badge = badge;
        this.enabled = enabled;
        this.hasSlider = hasSlider;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
        this.actionLabel = actionLabel;
        this.toggleAnim = enabled ? 1f : 0f;
    }

    public static Module toggle(String id, String name, String description,
                                String badge, boolean enabled) {
        return new Module(id, name, description, badge, enabled,
                false, 0f, 0f, 0f, 0f, "", null);
    }

    public static Module slider(String id, String name, String description,
                                boolean enabled, float value, float min,
                                float max, float step, String suffix) {
        return new Module(id, name, description, "", enabled,
                true, value, min, max, step, suffix, null);
    }

    public static Module action(String id, String name, String description,
                                String tag, String actionLabel) {
        return new Module(id, name, description, tag, false,
                false, 0f, 0f, 0f, 0f, "", actionLabel);
    }

    public String valueLabel() {
        if (!hasSlider) {
            return "";
        }
        if (step >= 1f) {
            return String.format(java.util.Locale.US, "%.0f%s", value, suffix);
        }
        if (step >= 0.1f) {
            return String.format(java.util.Locale.US, "%.1f%s", value, suffix);
        }
        return String.format(java.util.Locale.US, "%.2f%s", value, suffix);
    }

    public float ratio() {
        if (!hasSlider || max <= min) {
            return 0f;
        }
        return (value - min) / (max - min);
    }

    public void setFromRatio(float ratio) {
        if (!hasSlider) {
            return;
        }
        float clamped = Math.max(0f, Math.min(1f, ratio));
        float raw = min + clamped * (max - min);
        if (step > 0f) {
            raw = Math.round(raw / step) * step;
        }
        value = Math.max(min, Math.min(max, raw));
    }

    public boolean isAction() {
        return actionLabel != null;
    }
}
