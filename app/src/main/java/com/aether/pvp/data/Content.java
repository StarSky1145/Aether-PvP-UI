package com.aether.pvp.data;

import com.aether.pvp.model.Module;
import com.aether.pvp.model.Tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mock catalogue used by the interface concept. Every entry is a local UI state;
 * no game process is read or modified.
 */
public final class Content {
    private static final List<Module> COMBAT = new ArrayList<>();
    private static final List<Module> MOVEMENT = new ArrayList<>();
    private static final List<Module> VISUALS = new ArrayList<>();
    private static final List<Module> PLAYER = new ArrayList<>();
    private static final List<Module> CONFIG = new ArrayList<>();

    static {
        COMBAT.add(Module.toggle("combat.aura", "KillAura",
                "Silent rotations, humanised cadence.", "RMB", true));
        COMBAT.add(Module.toggle("combat.reach", "Reach",
                "Interaction distance, precisely held.", "3.12 m", true));
        COMBAT.add(Module.toggle("combat.velocity", "Velocity",
                "Knockback retention with soft easing.", "72 %", true));
        COMBAT.add(Module.toggle("combat.clicker", "AutoClicker",
                "Deterministic cadence, subtle jitter.", "CPS 12", false));
        COMBAT.add(Module.toggle("combat.criticals", "Criticals",
                "Packeted critical timing.", "SPACE", false));
        COMBAT.add(Module.toggle("combat.hitbox", "HitBox",
                "A micro-expansion of the hit volume.", "0.12", false));

        MOVEMENT.add(Module.toggle("move.sprint", "Sprint",
                "Permanent forward impulse.", "SPACE", true));
        MOVEMENT.add(Module.slider("move.speed", "Speed",
                "Grounded acceleration curve.", true, 3.2f, 1f, 5f, 0.1f, "x"));
        MOVEMENT.add(Module.toggle("move.noslow", "NoSlow",
                "Preserves momentum while using items.", "ITEMS", true));
        MOVEMENT.add(Module.slider("move.step", "Step",
                "Sub-block ascent, softly clamped.", false, 0.6f, 0.1f, 1.5f, 0.1f, "m"));
        MOVEMENT.add(Module.toggle("move.flight", "Flight",
                "Vertical glide with minimal drift.", "V", false));
        MOVEMENT.add(Module.slider("move.timer", "Timer",
                "Client tick multiplier.", false, 1f, 0.5f, 2f, 0.05f, "x"));

        VISUALS.add(Module.toggle("visual.esp", "ESP",
                "Restrained player silhouette overlay.", "PLAYERS", true));
        VISUALS.add(Module.slider("visual.nametags", "Nametags",
                "Typographic player labels.", true, 1f, 0.5f, 2f, 0.1f, "x"));
        VISUALS.add(Module.toggle("visual.tracers", "Tracers",
                "Hairline directional cues.", "ARROWS", false));
        VISUALS.add(Module.slider("visual.ambience", "Ambience",
                "Atmospheric tone mapping.", true, 0.8f, 0f, 1f, 0.05f, ""));
        VISUALS.add(Module.toggle("visual.hud", "HUD",
                "A modular combat readout.", "LAYOUT", true));
        VISUALS.add(Module.toggle("visual.target", "TargetHUD",
                "Single-target telemetry.", "STYLE", false));

        PLAYER.add(Module.toggle("player.friends", "Friends",
                "Trusted identities, kept in sync.", "SYNC", true));
        PLAYER.add(Module.toggle("player.teams", "Teams",
                "Colour-aware grouping.", "COLOR", true));
        PLAYER.add(Module.toggle("player.antibot", "AntiBot",
                "Filters synthetic entities.", "PATTERN", false));
        PLAYER.add(Module.toggle("player.inventory", "Inventory",
                "A compact inventory surface.", "CUSTOM", false));
        PLAYER.add(Module.toggle("player.middleclick", "MiddleClick",
                "One gesture to add a friend.", "MOUSE", false));
        PLAYER.add(Module.toggle("player.scaffold", "Scaffold",
                "Placement assistance preview.", "TOWER", false));

        CONFIG.add(Module.action("config.default", "Default",
                "The balanced baseline for everyday play.", "ACTIVE", "LOAD"));
        CONFIG.add(Module.action("config.ranked", "Ranked",
                "Quiet timings for competitive queues.", "2.4 KB", "LOAD"));
        CONFIG.add(Module.action("config.vanilla", "Vanilla",
                "Visual-only, completely understated.", "1.8 KB", "LOAD"));
        CONFIG.add(Module.toggle("config.autosave", "Auto Save",
                "Persist every adjustment on this device.", "LOCAL", true));
        CONFIG.add(Module.action("config.export", "Export Profile",
                "Write a portable .aether configuration.", "JSON", "EXPORT"));
        CONFIG.add(Module.action("config.reset", "Reset Interface",
                "Return all modules to their initial state.", "SAFE", "RESET"));
    }

    private Content() {
    }

    public static List<Module> modules(Tab tab) {
        switch (tab) {
            case COMBAT:
                return Collections.unmodifiableList(COMBAT);
            case MOVEMENT:
                return Collections.unmodifiableList(MOVEMENT);
            case VISUALS:
                return Collections.unmodifiableList(VISUALS);
            case PLAYER:
                return Collections.unmodifiableList(PLAYER);
            case CONFIG:
            default:
                return Collections.unmodifiableList(CONFIG);
        }
    }

    public static List<Module> all() {
        List<Module> out = new ArrayList<>(36);
        out.addAll(COMBAT);
        out.addAll(MOVEMENT);
        out.addAll(VISUALS);
        out.addAll(PLAYER);
        out.addAll(CONFIG);
        return out;
    }

    public static int activeCount(Tab tab) {
        int count = 0;
        for (Module m : modules(tab)) {
            if (m.enabled) {
                count++;
            }
        }
        return count;
    }
}
