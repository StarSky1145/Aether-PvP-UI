package com.aether.pvp.data;

import com.aether.pvp.model.ConfigSetting;
import com.aether.pvp.model.Module;
import com.aether.pvp.model.Tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface catalogue. The seven Nilore categories and their modules/settings
 * are reproduced as local UI state from the public OpenNilore source; nothing
 * here hooks, injects, or talks to a game process.
 */
public final class Content {
    private static final List<Module> COMBAT = new ArrayList<>();
    private static final List<Module> MOVEMENT = new ArrayList<>();
    private static final List<Module> PLAYER = new ArrayList<>();
    private static final List<Module> RENDER = new ArrayList<>();
    private static final List<Module> WORLD = new ArrayList<>();
    private static final List<Module> MISC = new ArrayList<>();
    private static final List<Module> GHOST = new ArrayList<>();
    private static final List<Module> HUD = new ArrayList<>();
    private static final List<Module> CONFIG = new ArrayList<>();

    static {
        Module m;
        // COMBAT — COMBAT
        m = nilore("nilore.combat.antibots", "AntiBots", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Respawn Time", 2500.2f, 0.0f, 10000.0f, 100.0f));
        m.addSetting(ConfigSetting.bool("Debug", true));
        COMBAT.add(m);
        m = nilore("nilore.combat.antifireball", "AntiFireball", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        COMBAT.add(m);
        m = nilore("nilore.combat.antikb", "AntiKB", "COMBAT", "Combat module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.bool("Rotate", true));
        m.addSetting(ConfigSetting.bool("Follow Direction", true));
        m.addSetting(ConfigSetting.bool("Predict", false));
        m.addSetting(ConfigSetting.number("Rotate Ticks", 12.0f, 3.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Delay", true));
        m.addSetting(ConfigSetting.number("Max Delay Ticks", 11.9f, 1.0f, 120.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Auto Forwards", false));
        m.addSetting(ConfigSetting.bool("Require KillAura", true));
        m.addSetting(ConfigSetting.bool("Jump Reset", true));
        m.addSetting(ConfigSetting.number("Attack Amount", 5.0f, 0.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Render Bar", true));
        m.addSetting(ConfigSetting.bool("Target ESP", true));
        m.addSetting(ConfigSetting.bool("Debug Log", true));
        m.addSetting(ConfigSetting.mode("Mode", "NoXZ", "Jump Reset", "NoXZ"));
        COMBAT.add(m);
        m = nilore("nilore.combat.autooffhand", "AutoOffHand", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Range", 6.0f, 1.0f, 12.0f, 0.5f));
        m.addSetting(ConfigSetting.bool("Gapple", true));
        m.addSetting(ConfigSetting.bool("SnowBall", true));
        m.addSetting(ConfigSetting.number("Health", 10.0f, 1.0f, 20.0f, 0.5f));
        m.addSetting(ConfigSetting.bool("RevertOnRegen", true));
        COMBAT.add(m);
        m = nilore("nilore.combat.autosoup", "AutoSoup", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Health", 10.0f, 0.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Delay", 0.0f, 0.0f, 1000.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Drop", true));
        COMBAT.add(m);
        m = nilore("nilore.combat.autothrow", "AutoThrow", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Min Distance", 4.0f, 3.0f, 30.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Max Distance", 11.0f, 3.0f, 30.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Delay", 300.0f, 50.0f, 2000.0f, 50.0f));
        COMBAT.add(m);
        m = nilore("nilore.combat.backtrack", "Backtrack", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Max MS", 1000.0f, 50.0f, 3000.0f, 50.0f));
        m.addSetting(ConfigSetting.number("Delay", 120.0f, 1.0f, 1000.0f, 10.0f));
        m.addSetting(ConfigSetting.number("Max Range", 5.4f, 2.0f, 12.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Start Range", 2.5f, 0.1f, 6.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Render", true));
        COMBAT.add(m);
        m = nilore("nilore.combat.critical", "Critical", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Optimize", "Grim", "Optimize"));
        m.addSetting(ConfigSetting.bool("Auto Jump", false));
        m.addSetting(ConfigSetting.bool("Debug", false));
        COMBAT.add(m);
        m = nilore("nilore.combat.crystalaura", "CrystalAura", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Attack on Packet (Danger)", false));
        COMBAT.add(m);
        m = nilore("nilore.combat.fakelag", "FakeLag", "COMBAT", "Combat module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Range", 4.0f, 1.0f, 10.0f, 0.5f));
        m.addSetting(ConfigSetting.number("Delay", 140.0f, 50.0f, 1000.0f, 10.0f));
        m.addSetting(ConfigSetting.number("Recoil Time", 50.0f, 0.0f, 1000.0f, 10.0f));
        m.addSetting(ConfigSetting.mode("Mode", "Dynamic", "Dynamic", "Constant"));
        m.addSetting(ConfigSetting.bool("Flush On Attack", true));
        m.addSetting(ConfigSetting.bool("Flush On Interact", true));
        m.addSetting(ConfigSetting.bool("Flush On Damage", true));
        m.addSetting(ConfigSetting.bool("Render", true));
        COMBAT.add(m);
        m = nilore("nilore.combat.killaura", "KillAura", "COMBAT", "Combat module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.bool("Attack Player", true));
        m.addSetting(ConfigSetting.bool("Attack Invisible", true));
        m.addSetting(ConfigSetting.bool("Attack Animals", false));
        m.addSetting(ConfigSetting.bool("Attack Mobs", false));
        m.addSetting(ConfigSetting.bool("Multi Attack", false));
        m.addSetting(ConfigSetting.bool("Infinity Switch", false));
        m.addSetting(ConfigSetting.bool("Prefer Baby", false));
        m.addSetting(ConfigSetting.bool("More Particles", false));
        m.addSetting(ConfigSetting.bool("Keep Sprint", true));
        m.addSetting(ConfigSetting.bool("Through Walls", false));
        m.addSetting(ConfigSetting.number("Through Walls Range", 3.0f, 1.0f, 6.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Ignore skip ticks", false));
        m.addSetting(ConfigSetting.bool("Fake AutoBlock", false));
        m.addSetting(ConfigSetting.number("Reach", 3.0f, 1.0f, 4.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Max APS", 9.0f, 1.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Min APS", 8.0f, 1.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Switch Size", 1.0f, 1.0f, 5.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Switch Delay (Attack Times)", 1.0f, 1.0f, 10.0f, 1.0f));
        m.addSetting(ConfigSetting.number("FoV", 360.0f, 10.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Hurt Time", 10.0f, 0.0f, 10.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Delay Mode", "1.8", "1.8", "1.9"));
        m.addSetting(ConfigSetting.mode("Target Mode", "Single", "Single", "Switch", "Multiple"));
        m.addSetting(ConfigSetting.mode("Priority", "FoV", "Distance", "FoV", "Health", "LivingTime", "Armor", "None"));
        m.addSetting(ConfigSetting.mode("Target ESP", "Spiral", "None", "Spiral", "Box", "Tab", "NurikZapen"));
        m.addSetting(ConfigSetting.number("Rotation Speed", 110.0f, 1.0f, 180.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Drift", 0.3f, 0.0f, 3.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Jitter", 1.3f, 0.0f, 3.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Debug Gate", false));
        COMBAT.add(m);

        // MOVEMENT — MOVEMENT
        m = nilore("nilore.movement.collisionspeed", "CollisionSpeed", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Boost", 0.03f, 0.005f, 0.16f, 0.005f));
        m.addSetting(ConfigSetting.number("Speed Limit", 0.2f, 1.0f, 0.01f, 1.0f));
        m.addSetting(ConfigSetting.number("Distance", 0.6f, 0.3f, 6.0f, 0.1f));
        MOVEMENT.add(m);
        m = nilore("nilore.movement.fastweb", "FastWeb", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.fireballblink", "FireballBlink", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.fly", "Fly", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.highjump", "HighJump", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.nodelay", "NoDelay", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("No Jump Delay", true));
        MOVEMENT.add(m);
        m = nilore("nilore.movement.nopush", "NoPush", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.noslow", "NoSlow", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.scaffold", "Scaffold", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Normal", "Normal", "Telly Bridge", "Keep Y"));
        m.addSetting(ConfigSetting.number("AirTicks", 1.0f, 0.0f, 3.0f, 0.1f));
        m.addSetting(ConfigSetting.number("PlaceDelay", 1.0f, 0.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Eagle", false));
        m.addSetting(ConfigSetting.bool("Render Item Spoof", true));
        m.addSetting(ConfigSetting.bool("Clutch", false));
        m.addSetting(ConfigSetting.number("Clutch Depth", 4.0f, 2.0f, 5.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Swing", "Both", "Both", "Server"));
        m.addSetting(ConfigSetting.bool("Block Counter", true));
        m.addSetting(ConfigSetting.mode("Block Counter Style", "Nitro", "Amunix", "Modern", "Naven", "Nitro"));
        m.addSetting(ConfigSetting.number("Nitro Offset", 90.0f, 0.0f, 220.0f, 5.0f));
        m.addSetting(ConfigSetting.bool("OnTickRot", false));
        m.addSetting(ConfigSetting.number("Rotation Speed", 153.9f, 0.0f, 360.0f, 5.0f));
        m.addSetting(ConfigSetting.number("Jitter", 2.5f, 0.0f, 5.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Sync RotSpeed", false));
        m.addSetting(ConfigSetting.number("Turn Speed", 73.4f, 0.0f, 360.0f, 5.0f));
        m.addSetting(ConfigSetting.number("Return Speed", 101.8f, 0.0f, 360.0f, 5.0f));
        m.addSetting(ConfigSetting.mode("Switch Mode", "Hotbar", "Normal", "Hotbar", "Full"));
        m.addSetting(ConfigSetting.bool("Limit BPS", true));
        m.addSetting(ConfigSetting.number("BPS", 3.5f, 2.0f, 10.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Mark", true));
        m.addSetting(ConfigSetting.number("Mark Red", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Mark Green", 55.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Mark Blue", 100.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Mark Alpha", 130.0f, 0.0f, 255.0f, 1.0f));
        MOVEMENT.add(m);
        m = nilore("nilore.movement.speed", "Speed", "MOVEMENT", "Movement module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.mode("Mode", "Motion", "OnGround", "Motion"));
        MOVEMENT.add(m);
        m = nilore("nilore.movement.sprint", "Sprint", "MOVEMENT", "Movement module · Nilore settings, local preview.", true);
        MOVEMENT.add(m);
        m = nilore("nilore.movement.targetstrafe", "TargetStrafe", "MOVEMENT", "Movement module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Round", "Round", "Strict"));
        m.addSetting(ConfigSetting.bool("Void Check", true));
        m.addSetting(ConfigSetting.number("Distance", 0.5f, 3.0f, 0.05f, 1.0f));
        MOVEMENT.add(m);

        // PLAYER — PLAYER
        m = nilore("nilore.player.antitnt", "AntiTNT", "PLAYER", "Player module · Nilore settings, local preview.", false);
        PLAYER.add(m);
        m = nilore("nilore.player.antivoid", "AntiVoid", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Fall Distance", 5.0f, 1.0f, 10.0f, 0.5f));
        PLAYER.add(m);
        m = nilore("nilore.player.antiweb", "AntiWeb", "PLAYER", "Player module · Nilore settings, local preview.", false);
        PLAYER.add(m);
        m = nilore("nilore.player.automlg", "AutoMLG", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Fall distance", 3.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Predict Ticks", 2.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Solid check", true));
        m.addSetting(ConfigSetting.bool("Recorvey", true));
        PLAYER.add(m);
        m = nilore("nilore.player.autowebplace", "AutoWebPlace", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Range", 4.0f, 3.0f, 5.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Delay", 2.0f, 1.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Render", true));
        m.addSetting(ConfigSetting.bool("Ground Web", false));
        m.addSetting(ConfigSetting.bool("Lava", false));
        m.addSetting(ConfigSetting.bool("Lava Wait Water Bucket", true));
        m.addSetting(ConfigSetting.bool("Debug", true));
        PLAYER.add(m);
        m = nilore("nilore.player.cheststealer", "ChestStealer", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Delay", 140.0f, 0.0f, 1000.0f, 10.0f));
        m.addSetting(ConfigSetting.number("Open Delay", 3.0f, 0.0f, 10.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Chest", true));
        m.addSetting(ConfigSetting.bool("Ender Chest", false));
        m.addSetting(ConfigSetting.bool("Furnace", true));
        m.addSetting(ConfigSetting.bool("BrewingStand", true));
        m.addSetting(ConfigSetting.bool("PickTrash", false));
        m.addSetting(ConfigSetting.bool("Only Best", true));
        m.addSetting(ConfigSetting.bool("Random Click", true));
        m.addSetting(ConfigSetting.bool("Smart Stealing", true));
        m.addSetting(ConfigSetting.bool("Danger", false));
        PLAYER.add(m);
        m = nilore("nilore.player.ghosthand", "GhostHand", "PLAYER", "Player module · Nilore settings, local preview.", false);
        PLAYER.add(m);
        m = nilore("nilore.player.helper", "Helper", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.multi("Mode", new String[]{"Self Extinguish", "Extinguish Fire", "Block Lava", "Block Water"}, new String[]{"Block Water", "Block Lava", "Extinguish Fire", "Self Extinguish"}));
        m.addSetting(ConfigSetting.bool("Legit", false));
        m.addSetting(ConfigSetting.number("Speed", 45.0f, 2.0f, 180.0f, 1.0f));
        m.addSetting(ConfigSetting.number("FOV", 90.0f, 30.0f, 180.0f, 1.0f));
        PLAYER.add(m);
        m = nilore("nilore.player.inventorymanager", "InventoryManager", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Delay", 180.0f, 0.0f, 500.0f, 10.0f));
        m.addSetting(ConfigSetting.number("Open Delay", 2.0f, 0.0f, 10.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Drop Delay", 70.0f, 0.0f, 500.0f, 10.0f));
        m.addSetting(ConfigSetting.bool("Auto Armor", true));
        m.addSetting(ConfigSetting.bool("Throw Items", true));
        m.addSetting(ConfigSetting.mode("Offhand Items", "None", "None", "Golden Apple", "Projectile", "Fishing Rod", "Block"));
        m.addSetting(ConfigSetting.mode("Bow Priority", "Power Bow", "Crossbow", "Power Bow", "Punch Bow"));
        m.addSetting(ConfigSetting.bool("Inventory Only", true));
        m.addSetting(ConfigSetting.bool("Pause On KillAura", true));
        m.addSetting(ConfigSetting.bool("Fast Throw", false));
        m.addSetting(ConfigSetting.number("Max Eggs & Snowballs Size", 64.0f, 16.0f, 256.0f, 16.0f));
        m.addSetting(ConfigSetting.number("Max Block Size", 256.0f, 64.0f, 512.0f, 64.0f));
        m.addSetting(ConfigSetting.number("Max Food Size", 128.0f, 32.0f, 256.0f, 32.0f));
        m.addSetting(ConfigSetting.number("Max Rod Size", 1.0f, 1.0f, 16.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Sword Slot", 1.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Block Slot", 2.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Axe Slot", 4.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Pickaxe Slot", 0.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Bow Slot", 3.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Water Bucket Slot", 5.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Ender Pearl Slot", 7.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Golden Apple Slot", 6.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Eggs & Snowballs Slot", 0.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Slime Ball Slot", 0.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Crystal Slot", 0.0f, 0.0f, 9.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Functional Blocks Fix", true));
        PLAYER.add(m);
        m = nilore("nilore.player.midpearl", "MidPearl", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Button", "Middle", "Middle", "Mouse 4", "Mouse 5"));
        m.addSetting(ConfigSetting.bool("Intercept", false));
        m.addSetting(ConfigSetting.mode("Intercept Button", "Mouse 4", "Mouse 4", "Mouse 5"));
        m.addSetting(ConfigSetting.bool("On Release", false));
        m.addSetting(ConfigSetting.bool("Block Check", true));
        PLAYER.add(m);
        m = nilore("nilore.player.nofall", "NoFall", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Fall Distance", 3.0f, 0.0f, 10.0f, 0.5f));
        PLAYER.add(m);
        m = nilore("nilore.player.stuck", "Stuck", "PLAYER", "Player module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Delay", "Delay", "Packet"));
        PLAYER.add(m);

        // RENDER — RENDER
        m = nilore("nilore.render.aspectratio", "AspectRatio", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Ratio", 1.7f, 0.0f, 1.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.audiovisualizer", "AudioVisualizer", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Bars", "Bars", "Only Left", "Only Right"));
        m.addSetting(ConfigSetting.number("Bars", 48.0f, 8.0f, 128.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Height", 0.5f, 0.05f, 1.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Gap", 2.0f, 0.0f, 8.0f, 0.5f));
        m.addSetting(ConfigSetting.number("Sensitivity", 1.6f, 0.2f, 4.0f, 0.05f));
        m.addSetting(ConfigSetting.number("Smoothing", 0.95f, 0.0f, 0.95f, 0.01f));
        m.addSetting(ConfigSetting.number("Alpha", 120.0f, 30.0f, 255.0f, 5.0f));
        m.addSetting(ConfigSetting.bool("Hide In GUI", true));
        RENDER.add(m);
        m = nilore("nilore.render.betterchat", "BetterChat", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Background", true));
        m.addSetting(ConfigSetting.number("Background Alpha", 150.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Background Radius", 4.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Glow", true));
        m.addSetting(ConfigSetting.number("Glow Radius", 13.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Glow Alpha", 85.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Blur", false));
        m.addSetting(ConfigSetting.number("Blur Strength", 22.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Blur Opacity", 1.0f, 0.0f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Font", "PingFang", "Minecraft", "Minecraft", "PingFang"));
        m.addSetting(ConfigSetting.number("Y Offset", -60.0f, 0.0f, 1.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.betterfps", "BetterFPS", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Fast Math", true));
        m.addSetting(ConfigSetting.bool("Terrain LOD", false));
        m.addSetting(ConfigSetting.number("LOD Start", 96.0f, 32.0f, 512.0f, 8.0f));
        m.addSetting(ConfigSetting.number("LOD Tier 1 End", 144.0f, 48.0f, 768.0f, 8.0f));
        m.addSetting(ConfigSetting.number("LOD Tier 2 End", 192.0f, 64.0f, 1024.0f, 8.0f));
        RENDER.add(m);
        m = nilore("nilore.render.clickgui", "ClickGui", "RENDER", "Render module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.mode("Mode", "Nilore", "Old", "Panel", "New", "Neverlose", "Nilore"));
        m.addSetting(ConfigSetting.mode("Theme", "Light", "Light", "Dark"));
        RENDER.add(m);
        m = nilore("nilore.render.compass", "Compass", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Compass Only", true));
        m.addSetting(ConfigSetting.bool("No Player Only", true));
        RENDER.add(m);
        m = nilore("nilore.render.damageglow", "DamageGlow", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Color R", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Color G", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Color B", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Alpha", 50.0f, 0.0f, 255.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.entityduang", "EntityDuang", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Width", 1.0f, 0.1f, 3.0f, 0.05f));
        m.addSetting(ConfigSetting.number("Height", 1.0f, 0.1f, 3.0f, 0.05f));
        m.addSetting(ConfigSetting.bool("Always", true));
        m.addSetting(ConfigSetting.number("Idle Amplitude", 0.1f, 0.0f, 0.5f, 0.01f));
        m.addSetting(ConfigSetting.number("Idle Speed", 1.6f, 0.2f, 5.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("On Hurt", true));
        m.addSetting(ConfigSetting.number("Duang Strength", 0.35f, 0.05f, 1.0f, 0.05f));
        m.addSetting(ConfigSetting.number("Duang Speed", 9.0f, 3.0f, 20.0f, 0.5f));
        m.addSetting(ConfigSetting.bool("All Entities", true));
        m.addSetting(ConfigSetting.bool("Ignore Self", true));
        RENDER.add(m);
        m = nilore("nilore.render.entityeditor", "EntityEditor", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Target", "Pig", "Pig", "Cow", "Zombie", "Creeper"));
        m.addSetting(ConfigSetting.bool("Ignore Self", true));
        m.addSetting(ConfigSetting.bool("Better Item View", true));
        m.addSetting(ConfigSetting.bool("AllEntity", true));
        m.addSetting(ConfigSetting.bool("Exclude Drops", false));
        RENDER.add(m);
        m = nilore("nilore.render.esp", "ESP", "RENDER", "Render module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.mode("Mode", "Glow", "Glow", "Outlined 2D"));
        m.addSetting(ConfigSetting.bool("Players", true));
        m.addSetting(ConfigSetting.bool("Mobs", false));
        m.addSetting(ConfigSetting.bool("Animals", false));
        m.addSetting(ConfigSetting.bool("Items", false));
        m.addSetting(ConfigSetting.bool("Arrows", false));
        m.addSetting(ConfigSetting.bool("Skeleton", false));
        m.addSetting(ConfigSetting.bool("Show Health Bar", true));
        m.addSetting(ConfigSetting.mode("Health Bar Position", "Left", "Bottom", "Top", "Left", "Right"));
        RENDER.add(m);
        m = nilore("nilore.render.fakeantiaim", "FakeAntiAim", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Override Rotation", true));
        m.addSetting(ConfigSetting.mode("Entity Yaw", "Spin", "Spin", "Static", "NotModified"));
        m.addSetting(ConfigSetting.number("Entity Yaw Value", 54.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Entity Yaw Jitter", 0.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Entity Pitch", "NotModified", "Spin", "Static", "NotModified"));
        m.addSetting(ConfigSetting.number("Entity Pitch Value", 88.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Entity Pitch Jitter", 0.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Head Yaw", "NotModified", "Spin", "Static", "NotModified"));
        m.addSetting(ConfigSetting.number("Head Yaw Value", 0.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Head Yaw Jitter", 0.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Head Pitch", "NotModified", "Spin", "Static", "NotModified"));
        m.addSetting(ConfigSetting.number("Head Pitch Value", -114.0f, -360.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Head Pitch Jitter", 3.0f, -360.0f, 360.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.fullbright", "FullBright", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Brightness", 90.0f, 0.0f, 1.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.interface", "Interface", "RENDER", "Render module · Nilore settings, local preview.", false);
        RENDER.add(m);
        m = nilore("nilore.render.itemtags", "ItemTags", "RENDER", "Render module · Nilore settings, local preview.", false);
        RENDER.add(m);
        m = nilore("nilore.render.lyrics", "Lyrics", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Y Offset", 131.0f, -200.0f, 200.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Font Offset", 2.0f, -2.0f, 2.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.nameprotect", "NameProtect", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Hidden", "Fixed", "Random", "Hidden"));
        RENDER.add(m);
        m = nilore("nilore.render.nametags", "NameTags", "RENDER", "Render module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.mode("Style", "Simple", "Opal", "Simple", "Naven"));
        m.addSetting(ConfigSetting.number("Scale", 0.6f, 0.1f, 1.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Max Distance", 128.0f, 8.0f, 256.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Invisibles", true));
        m.addSetting(ConfigSetting.bool("Show Artifacts", false));
        m.addSetting(ConfigSetting.bool("Hide Teammates", false));
        RENDER.add(m);
        m = nilore("nilore.render.nofov", "NoFov", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Constant", "Constant", "Custom"));
        m.addSetting(ConfigSetting.number("FOV", 100.0f, 1.0f, 179.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Multiplier", 1.0f, 0.1f, 1.5f, 0.05f));
        RENDER.add(m);
        m = nilore("nilore.render.nohurtcam", "NoHurtCam", "RENDER", "Render module · Nilore settings, local preview.", false);
        RENDER.add(m);
        m = nilore("nilore.render.oldhitting", "OldHitting", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Animation", "Vanilla", "Vanilla", "Leaked", "Slide", "NewPushDown"));
        m.addSetting(ConfigSetting.number("Size", 1.0f, 0.1f, 3.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Speed", 1.0f, 0.1f, 5.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Y-Offset", 0.0f, -1.0f, 1.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Hand-X", 0.0f, -1.0f, 1.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Hand-Y", 0.0f, -1.0f, 1.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Hand-Z", 0.0f, -1.0f, 1.0f, 0.01f));
        RENDER.add(m);
        m = nilore("nilore.render.projectiles", "Projectiles", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Show Arrows", true));
        m.addSetting(ConfigSetting.bool("Show Pearls", true));
        m.addSetting(ConfigSetting.bool("Show Potions", false));
        m.addSetting(ConfigSetting.bool("Show Eggs", false));
        m.addSetting(ConfigSetting.bool("Show Snowballs", false));
        m.addSetting(ConfigSetting.number("Scale", 0.4f, 0.2f, 2.0f, 0.01f));
        RENDER.add(m);
        m = nilore("nilore.render.timeweather", "TimeWeather", "RENDER", "Render module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Time", "Custom", "Day", "Sunset", "Night", "Custom", "Vanilla"));
        m.addSetting(ConfigSetting.number("Custom Time", 15000.0f, 0.0f, 24000.0f, 100.0f));
        m.addSetting(ConfigSetting.mode("Weather", "Clear", "Clear", "Rain", "Thunder", "Vanilla"));
        RENDER.add(m);
        m = nilore("nilore.render.watermark", "Watermark", "RENDER", "Render module · Nilore settings, local preview.", true);
        m.addSetting(ConfigSetting.mode("Style", "Exhibition", "Neverlose", "DynamicIsland", "Simple", "Pharos", "Exhibition", "Logo", "Naven", "Opai"));
        m.addSetting(ConfigSetting.bool("Show FPS", true));
        m.addSetting(ConfigSetting.bool("Show MS", true));
        m.addSetting(ConfigSetting.number("Opai Accent R", 187.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Opai Accent G", 195.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Opai Accent B", 255.0f, 0.0f, 255.0f, 1.0f));
        RENDER.add(m);
        m = nilore("nilore.render.xray", "XRay", "RENDER", "Render module · Nilore settings, local preview.", false);
        RENDER.add(m);

        // WORLD — EXPLOIT
        m = nilore("nilore.exploit.disabler", "Disabler", "WORLD", "World module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Grim Bad PacketsA", true));
        m.addSetting(ConfigSetting.bool("Grim BadPacketsD", true));
        m.addSetting(ConfigSetting.bool("Grim Bad PacketY", true));
        m.addSetting(ConfigSetting.bool("Grim Duplicate RotPlace", true));
        m.addSetting(ConfigSetting.bool("Silent Inventory", true));
        m.addSetting(ConfigSetting.bool("ACA Fast Switch", true));
        m.addSetting(ConfigSetting.bool("ACA Inventory Frequency", true));
        m.addSetting(ConfigSetting.bool("ACA Aim Step", true));
        m.addSetting(ConfigSetting.bool("ACA Perfect Rotation", true));
        m.addSetting(ConfigSetting.bool("Themis Blink", true));
        m.addSetting(ConfigSetting.bool("Only Remote Server", false));
        m.addSetting(ConfigSetting.bool("Logging", false));
        WORLD.add(m);
        m = nilore("nilore.exploit.fastplace", "FastPlace", "WORLD", "World module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Delay", 0.0f, 0.0f, 500.0f, 10.0f));
        m.addSetting(ConfigSetting.multi("Item Types", new String[]{"Blocks", "Throwables"}, new String[]{"Blocks"}));
        WORLD.add(m);

        // MISC — WORLD
        m = nilore("nilore.world.antistaff", "AntiStaff", "MISC", "Misc module · Nilore settings, local preview.", false);
        MISC.add(m);
        m = nilore("nilore.world.autoplay", "AutoPlay", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Delay", 2.0f, 0.0f, 10.0f, 0.1f));
        MISC.add(m);
        m = nilore("nilore.world.autotools", "AutoTools", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Check Sword", false));
        m.addSetting(ConfigSetting.bool("Switch Back", true));
        m.addSetting(ConfigSetting.bool("Silent", true));
        MISC.add(m);
        m = nilore("nilore.world.blockin", "BlockIn", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Auto Disable", true));
        m.addSetting(ConfigSetting.mode("Place Order", "Normal", "Normal", "Normal", "Random", "BottomTop", "TopBottom"));
        m.addSetting(ConfigSetting.bool("Auto Block", true));
        m.addSetting(ConfigSetting.bool("Switch Back", true));
        m.addSetting(ConfigSetting.bool("Use Offhand", false));
        m.addSetting(ConfigSetting.number("Range", 4.5f, 1.0f, 6.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Cooldown", 1.0f, 0.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Rotation", true));
        m.addSetting(ConfigSetting.bool("Smooth", true));
        m.addSetting(ConfigSetting.number("Rotation Ticks", 2.0f, 1.0f, 6.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Roof Support", true));
        m.addSetting(ConfigSetting.bool("Diagonal Support", true));
        m.addSetting(ConfigSetting.bool("Pillar Support", true));
        m.addSetting(ConfigSetting.bool("Use Jump", true));
        m.addSetting(ConfigSetting.number("Jump Ticks", 1.0f, 1.0f, 8.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Debug", false));
        MISC.add(m);
        m = nilore("nilore.world.debugger", "Debugger", "MISC", "Misc module · Nilore settings, local preview.", false);
        MISC.add(m);
        m = nilore("nilore.world.packetbreak", "PacketBreak", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Range", 4.5f, 1.0f, 6.0f, 0.1f));
        m.addSetting(ConfigSetting.bool("Rotate", true));
        m.addSetting(ConfigSetting.number("Rotation Speed", 45.0f, 1.0f, 180.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Swing", true));
        m.addSetting(ConfigSetting.bool("Hold", true));
        m.addSetting(ConfigSetting.bool("ESP", true));
        m.addSetting(ConfigSetting.bool("Progress", true));
        m.addSetting(ConfigSetting.mode("Mode", "Up", "Up", "In", "Out"));
        m.addSetting(ConfigSetting.number("Red", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Green", 255.0f, 0.0f, 255.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Blue", 255.0f, 0.0f, 255.0f, 1.0f));
        MISC.add(m);
        m = nilore("nilore.world.protocol", "Protocol", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Hosts", "pc.bjdmc.net,*.bjdmc.net", "pc.bjdmc.net,*.bjdmc.net"));
        m.addSetting(ConfigSetting.bool("Trace Logger", false));
        m.addSetting(ConfigSetting.bool("Observe Only", false));
        m.addSetting(ConfigSetting.bool("Allow Live Send", true));
        m.addSetting(ConfigSetting.bool("Strict Provider Gate", false));
        MISC.add(m);
        m = nilore("nilore.world.teams", "Teams", "MISC", "Misc module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Mode", "Scoreboard", "Color", "Scoreboard"));
        MISC.add(m);
        m = nilore("nilore.world.webui", "WebUI", "MISC", "Misc module · Nilore settings, local preview.", false);
        MISC.add(m);

        // GHOST — MISC
        m = nilore("nilore.misc.aimassist", "AimAssist", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Random Yaw Offset", 1.6f, 0.0f, 10.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Random Pitch Offset", 0.0f, 0.0f, 1.0f, 0.01f));
        m.addSetting(ConfigSetting.number("Range", 4.0f, 3.0f, 30.0f, 0.1f));
        m.addSetting(ConfigSetting.number("Fov", 138.0f, 1.0f, 360.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Mouse down", true));
        m.addSetting(ConfigSetting.bool("Adaptive", true));
        m.addSetting(ConfigSetting.number("Adaptive Offset", 3.0f, 0.01f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Smooth amount", 3.5f, 0.1f, 1.0f, 1.0f));
        m.addSetting(ConfigSetting.bool("Break Block", true));
        GHOST.add(m);
        m = nilore("nilore.misc.autoclicker", "AutoClicker", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("CPS", 25.0f, 4.0f, 25.0f, 1.0f));
        m.addSetting(ConfigSetting.mode("Mode", "Right", "Left", "Right", "Both"));
        m.addSetting(ConfigSetting.mode("Click Mode", "Mouse", "Method", "Key", "Mouse"));
        m.addSetting(ConfigSetting.mode("CPS Mode", "Normal", "Normal", "DBC", "Human"));
        m.addSetting(ConfigSetting.bool("Break Block", true));
        GHOST.add(m);
        m = nilore("nilore.misc.autorod", "AutoRod", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Button", "Mouse 5", "Middle", "Mouse 4", "Mouse 5"));
        m.addSetting(ConfigSetting.number("Delay", 2.0f, 0.0f, 20.0f, 1.0f));
        GHOST.add(m);
        m = nilore("nilore.misc.killsay", "KillSay", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.mode("Style", "naven-style", "naven-style", "southside-style"));
        m.addSetting(ConfigSetting.number("Delay", 20.0f, 0.0f, 120.0f, 1.0f));
        GHOST.add(m);
        m = nilore("nilore.misc.musicplayer", "MusicPlayer", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.bool("Melodify", true));
        m.addSetting(ConfigSetting.number("Crossfade", 14.0f, 4.0f, 20.0f, 1.0f));
        m.addSetting(ConfigSetting.number("Volume", 73.0f, 0.0f, 100.0f, 1.0f));
        GHOST.add(m);
        m = nilore("nilore.misc.safewalk", "SafeWalk", "GHOST", "Ghost module · Nilore settings, local preview.", false);
        m.addSetting(ConfigSetting.number("Edge Distance", 0.3f, 0.1f, 0.9f, 0.05f));
        GHOST.add(m);

        // AETHER's own visual layer, kept separate from Nilore's Render category.
        m = Module.toggle("hud.watermark", "Watermark",
                "Identity, frame rate and latency, kept quiet.", "TOP LEFT", true);
        m.mode = 1;
        HUD.add(m);
        HUD.add(Module.toggle("hud.arraylist", "ArrayList",
                "Every active module, ranked by presence.", "RIGHT EDGE", true));
        HUD.add(Module.toggle("hud.notifications", "Notifications",
                "Short, legible confirmations that never shout.", "STACK", true));
        m = Module.toggle("hud.targethud", "Target HUD",
                "A single opponent, reduced to what matters.", "COMBAT", true);
        m.mode = 1;
        HUD.add(m);
        HUD.add(Module.toggle("hud.keystrokes", "Keystrokes",
                "Movement, at the edge of vision.", "WASD", false));
        HUD.add(Module.toggle("hud.coordinates", "Coordinates",
                "A precise positional readout.", "XYZ", true));
        m = Module.toggle("hud.island", "Dynamic Island",
                "One living capsule for the numbers that matter.", "TOP CENTER", true);
        m.mode = 0;
        HUD.add(m);
        m = Module.toggle("hud.potions", "Potion Effects",
                "Active effects, condensed into a quiet column.", "EFFECTS", true);
        m.mode = 0;
        HUD.add(m);
        m = Module.toggle("hud.playerlist", "Player List",
                "Nearby players, named without noise.", "ONLINE", true);
        m.mode = 0;
        HUD.add(m);

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

    private static Module nilore(String id, String name, String category,
                                 String description, boolean enabled) {
        return Module.toggle(id, name, description, category, enabled);
    }

    public static List<Module> modules(Tab tab) {
        switch (tab) {
            case COMBAT: return Collections.unmodifiableList(COMBAT);
            case MOVEMENT: return Collections.unmodifiableList(MOVEMENT);
            case PLAYER: return Collections.unmodifiableList(PLAYER);
            case RENDER: return Collections.unmodifiableList(RENDER);
            case WORLD: return Collections.unmodifiableList(WORLD);
            case MISC: return Collections.unmodifiableList(MISC);
            case GHOST: return Collections.unmodifiableList(GHOST);
            case HUD: return Collections.unmodifiableList(HUD);
            case CONFIG:
            default: return Collections.unmodifiableList(CONFIG);
        }
    }

    public static List<Module> all() {
        List<Module> out = new ArrayList<>(100);
        out.addAll(COMBAT);
        out.addAll(MOVEMENT);
        out.addAll(PLAYER);
        out.addAll(RENDER);
        out.addAll(WORLD);
        out.addAll(MISC);
        out.addAll(GHOST);
        out.addAll(HUD);
        out.addAll(CONFIG);
        return out;
    }

    public static Module find(String id) {
        for (Module m : all()) {
            if (m.id.equals(id)) {
                return m;
            }
        }
        return null;
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
