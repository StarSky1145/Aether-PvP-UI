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

[**AETHER-1.0.0-debug.apk**](https://github.com/StarSky1145/Aether-PvP-UI/releases/download/v1.0.0/AETHER-1.0.0-debug.apk)
— 264 KB, debug-signed, minSdk 26 / targetSdk 35.

```
SHA-256  62508d780f796d290958a18885ef21ff9e60531d7d61034fcf29299ff30bb8e2
```

> The release is debug-signed for direct sideloading. Android may ask you to
> allow installs from your browser or file manager.

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

- Five sections: Combat, Movement, Visuals, Player, Config.
- 30 mock modules with toggles, sliders, key hints, and quick filters
  (`ALL / ON / OFF`).
- Persistent local state via `SharedPreferences`.
- Config profiles, JSON export to the system clipboard, reset.
- Close the menu to a minimal floating launch capsule; back closes/reopens.
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
| Tap a tab / card | Navigate / toggle module |
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
│   └── Tab.java               navigation destinations
├── data/
│   ├── Content.java           mock catalogue
│   └── Prefs.java             local persistence
└── ui/
    ├── MenuView.java          all Skia drawing + touch
    ├── Theme.java             palette and type scale
    ├── Icons.java             hand-drawn vector glyphs
    └── Ui.java                drawing helpers
```

## Typography licence

Sora and IBM Plex Mono are distributed under the SIL Open Font License 1.1.
See [`NOTICE.md`](NOTICE.md). The application code is MIT licensed.
