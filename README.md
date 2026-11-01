# AETHER

**Minecraft PvP interface concept · Android · Java · OpenGL ES + Skia**

A quiet, modern take on the high-end game-client menu: smoked glass, hairline
borders, a lavender-to-mint accent, editorial typography and a living GPU
backdrop. Everything is drawn in Java with the Android framework — no AndroidX,
no image assets, no external UI library.

> **Scope / 边界**
>
> This repository is an **interface concept only**. It does not read, inject
> into, patch, or otherwise interact with Minecraft; it contains no packet,
> memory, anti-cheat or network code. Module names and toggle states are local
> mock data used to demonstrate the UI. Every setting is stored on-device.

## Download

[**AETHER-1.1.0-debug.apk**](https://github.com/StarSky1145/Aether-PvP-UI/releases/download/v1.1.0/AETHER-1.1.0-debug.apk)
— 319 KB, debug-signed, minSdk 26 / targetSdk 35.

```
SHA-256  164b06dd67e3f5994e6127d14c3839e0537f40a36cae1bc2b70daab83249935c
```

> The release is debug-signed for direct sideloading. Android may ask you to
> allow installs from your browser or file manager. If an older AETHER build
> was signed with a different debug key, uninstall it once; this release pins a
> stable debug key for subsequent updates.

## Design

- **Palette** — obsidian `#07080C`, smoked panel `#0B0D13`, lavender `#C9B8FF`, mint `#9BE8DD`, warm white `#F4F2F8`.
- **Type** — Sora (200/400/500/600) for interface, IBM Plex Mono for metadata.
- **Motion** — 150–450 ms cubic easing, page fade/rise, animated toggle caps,
  clipped inertial scrolling, a soft launch capsule when the menu is closed.
- **Backdrop** — a procedural GLSL aurora with layered value noise, floating
  dust, grain and vignette; low-poly, quantised ridges sit over it for a subtle
  Minecraft silhouette.
- **Layout** — responsive virtual 800 × 360 dp grid, safe-area aware, locked to
  landscape. Two-column module grid with a scrollable viewport.

The renderer is deliberately split:

| Layer | Technology | Responsibility |
| --- | --- | --- |
| Backdrop | `GLSurfaceView` + GLSL ES 2.0 | Aurora, dust, grain, vignette |
| Interface | Android `Canvas` (Skia) | Glass panel, cards, type, controls |

## Features

- Nine sections: Combat, Movement, Player, Render, World, Misc, Ghost, HUD,
  Config. The first seven follow Nilore's category model, including its
  alphabetical module order.
- **Nilore module catalogue** — 76 modules and 343 internal settings reproduced
  from the public OpenNilore source as local interface state. Boolean, number,
  mode and multi-select controls use Nilore's labels, defaults, ranges and
  options; `FakeAntiAim`'s constant-backed modes are normalised to readable
  names.
- **HUD layer** — glass watermark with FPS, ping and clock; animated ArrayList;
  notification stack; Target HUD; keystrokes; coordinates; Dynamic Island;
  potion effects; player list. Every component has its own toggle and
  appearance settings. Choose `PREVIEW HUD` in the HUD tab to close the menu and
  inspect the layer.
- **Module configuration sheet** — tap a card outside its toggle to edit its
  internal settings, keybind, mode, opacity or accent, or reset the module.
  The sheet scrolls for long Nilore modules such as KillAura (28 settings).
  Modules with no settings show an explicit empty state. Changes save
  immediately.
- Persistent local state via `SharedPreferences`.
- Config profiles, JSON export to the system clipboard, reset.
- Close the menu to a minimal floating launch capsule; back closes/reopens.
- **Floating overlay** — a foreground service hosts the HUD touch-through and
  the ClickGUI in a focusable `TYPE_APPLICATION_OVERLAY` window, with a
  draggable glass orb and persisted position. Requires the system
  "display over other apps" permission.
- Immersive fullscreen, display-cutout safe insets, 60 fps custom rendering.

## Build

### GitHub Actions (recommended)

Push to `main` or run **Actions → Build AETHER APK → Run workflow**. The debug
APK is attached to the run as the `aether-ui-debug` artifact.

The workflow uses:

- Temurin JDK 17
- Android SDK 35 / Build Tools 35.0.0
- Gradle 8.9 + AGP 8.7.3

### Local

```bash
# with Gradle 8.9 and Android SDK 35 installed
gradle :app:assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Controls

| Gesture | Action |
| --- | --- |
| Tap a tab | Navigate |
| Tap a card body | Open the module configuration sheet |
| Tap a card toggle | Enable / disable the module |
| Drag vertically in the module area | Inertial scroll |
| Drag a slider | Adjust value (saved on release) |
| Tap the close glyph | Collapse to the AETHER capsule |
| Back | Collapse the menu, or exit when collapsed |

## Project map

```
app/src/main/java/com/aether/pvp/
├── MainActivity.java          immersive host + safe insets
├── gl/
│   ├── AuroraView.java        GLSurfaceView shell
│   └── AuroraRenderer.java    GLSL aurora backdrop
├── model/
│   ├── Module.java            UI-only module model
│   ├── ConfigSetting.java     boolean / number / mode / multi-select
│   └── Tab.java               navigation destinations
├── data/
│   ├── Content.java           mock catalogue
│   └── Prefs.java             local persistence
├── overlay/
│   ├── OverlayService.java    foreground overlay host
│   ├── OverlayHudView.java    touch-through HUD window
│   └── LauncherView.java      draggable glass orb
└── ui/
    ├── MenuView.java          Skia chrome, config sheet and touch
    ├── HudOverlay.java        in-game HUD layer
    ├── Theme.java             palette and type scale
    ├── Icons.java             hand-drawn vector glyphs
    └── Ui.java                drawing helpers
```

## Typography licence

Sora and IBM Plex Mono are distributed under the SIL Open Font License 1.1.
See [`NOTICE.md`](NOTICE.md). The application code is MIT licensed.

## Nilore attribution

The module taxonomy and setting definitions are referenced from
[`Pickbowen/OpenNilore`](https://github.com/Pickbowen/OpenNilore), a public
Minecraft client project, so this interface can mirror its structure. AETHER is
a standalone Android UI concept: it does not include, link, inject, or interact
with that client or Minecraft. No OpenNilore code is copied into this
repository; only interface metadata is reproduced.
