package com.aether.pvp.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.aether.pvp.CrashLog;
import com.aether.pvp.data.Content;
import com.aether.pvp.data.Prefs;
import com.aether.pvp.model.ConfigSetting;
import com.aether.pvp.model.Module;
import com.aether.pvp.model.Tab;
import com.aether.pvp.render.AetherSurface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The whole interface is drawn with Android's Skia-backed Canvas, over a live
 * OpenGL backdrop. No AndroidX, no image assets, no game integration.
 */
public final class MenuView extends View {
    private static final Tab[] TABS = Tab.values();
    private static final long TOAST_MS = 1700L;
    private static final long SCROLLBAR_MS = 1100L;

    public interface ModuleEventListener {
        void onModuleEvent(String title, String detail, int accent);
    }

    private final Prefs prefs;
    private final HudOverlay hud = new HudOverlay();
    private final boolean overlay;
    private final OverScroller scroller;
    private final int touchSlop;

    private final RectF panel = new RectF();
    private final RectF cardsViewport = new RectF();
    private final RectF closeRect = new RectF();
    private final RectF saveRect = new RectF();
    private final RectF capsuleRect = new RectF();
    private final RectF launchRect = new RectF();
    private final RectF configPanel = new RectF();
    private final RectF configClose = new RectF();
    private final RectF configToggle = new RectF();
    private final RectF[] configKeys = {new RectF(), new RectF(), new RectF(), new RectF(), new RectF()};
    private final RectF[] configModes = {new RectF(), new RectF(), new RectF()};
    private final RectF[] configSliderTracks = {new RectF(), new RectF()};
    private final RectF[] configAccents = {new RectF(), new RectF(), new RectF(), new RectF()};
    private final RectF configReset = new RectF();
    private final RectF configDone = new RectF();
    private final RectF configViewport = new RectF();
    private final List<ConfigRow> configRows = new ArrayList<>();
    private final RectF[] tabRects = new RectF[TABS.length];
    private final RectF[] chipRects = {new RectF(), new RectF(), new RectF()};
    private final List<Card> cards = new ArrayList<>();
    private final Paint shape = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = Ui.stroke(0xFFFFFFFF, 1f);
    private final Paint iconPaint = Ui.stroke(0xFFFFFFFF, 1f);
    private final Paint textPaint = Ui.text(10f, Theme.TEXT, Theme.regular, 0f);

    private float w;
    private float h;
    private float u = 1f;
    private float railRight;
    private float contentLeft;
    private float contentRight;
    private float dividerY;
    private float cardsTop;
    private float footerLineY;
    private float cardHeight;
    private float cardGap;
    private float scrollY;
    private float maxScroll;
    private float downX;
    private float downY;
    private float downScroll;
    private long lastScrollAt;
    private long lastFrameAt;
    private long toastStart;
    private long toastUntil;
    private String toastText = "";
    private float safeLeft;
    private float safeTop;
    private float safeRight;
    private float safeBottom;

    private int tabIndex;
    private Tab tab;
    private int filter;
    private int activeProfile;
    private boolean open;
    private float panelAnim;
    private float pageAnim = 1f;
    private boolean dragging;
    private boolean pressedClose;
    private boolean pressedSave;
    private boolean pressedLaunch;
    private boolean pressedCapsule;
    private boolean overlayActive;
    private boolean closePending;
    private Runnable closeListener;
    private Runnable overlayAction;
    private ModuleEventListener moduleEventListener;
    private int pressedTab = -1;
    private int pressedChip = -1;
    private Module pressedModule;
    private Card pressedCard;
    private Module activeSlider;
    private RectF activeSliderCard;
    private float activeSliderTrackLeft;
    private float activeSliderTrackWidth;
    private float hoverX;
    private float hoverY;
    private boolean hoverActive;
    private boolean drawFailed;
    private Module configModule;
    private float configAnim;
    private int configSlider = -1;
    private int pressedConfigKey = -1;
    private int pressedConfigMode = -1;
    private int pressedConfigAccent = -1;
    private boolean pressedConfigClose;
    private boolean pressedConfigToggle;
    private boolean pressedConfigReset;
    private boolean pressedConfigDone;
    private float configScroll;
    private float configMaxScroll;
    private float configContentHeight;
    private float configDownY;
    private float configDownScroll;
    private float configScrollStart;
    private boolean configDragging;
    private boolean configGesture;
    private int configNumberRow = -1;
    private int pressedConfigRow = -1;
    private int pressedConfigOption = -1;
    private VelocityTracker velocityTracker;

    private Shader bgShader;
    private Shader vignetteShader;
    private Shader panelFillShader;
    private Shader panelBorderShader;
    private Shader railShader;
    private Shader panelGlowLavender;
    private Shader panelGlowMint;
    private Shader accentWash;
    private Shader terrainShader0;
    private Shader terrainShader1;
    private Shader terrainShader2;
    private Path terrain0;
    private Path terrain1;
    private Path terrain2;
    private Path terrainEdge0;
    private Path terrainEdge1;

    private Paint pBrand;
    private Paint pBrandSub;
    private Paint pNavLabel;
    private Paint pNav;
    private Paint pNavIndex;
    private Paint pKicker;
    private Paint pTitle;
    private Paint pSubtitle;
    private Paint pStatus;
    private Paint pChip;
    private Paint pCount;
    private Paint pCardName;
    private Paint pCardDesc;
    private Paint pKey;
    private Paint pValue;
    private Paint pFooter;
    private Paint pButton;
    private Paint pToast;
    private Paint pLaunchTitle;
    private Paint pLaunchSub;
    private Paint pAction;

    public MenuView(Context context, Prefs prefs) {
        this(context, prefs, false);
    }

    /**
     * @param overlay true when hosted in a WindowManager overlay rather than the
     *                Activity; the game-like backdrop and the in-panel HUD are
     *                then suppressed so the live app behind stays visible.
     */
    public MenuView(Context context, Prefs prefs, boolean overlay) {
        super(context);
        this.prefs = prefs;
        this.overlay = overlay;
        this.scroller = new OverScroller(context);
        prefs.load();
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        setWillNotDraw(false);
        setContentDescription("AETHER, a Minecraft PvP interface concept");
        tabIndex = prefs.savedTab(TABS);
        tab = TABS[tabIndex];
        activeProfile = 0;
        open = !overlay && prefs.menuOpen();
        panelAnim = 0f;
        pageAnim = open ? 0f : 1f;
        lastFrameAt = SystemClock.uptimeMillis();
        buildCards();
        setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                return handleBack();
            }
            return false;
        });
    }

    public void setOnCloseListener(Runnable listener) {
        closeListener = listener;
    }

    public void setOnOverlayAction(Runnable action) {
        overlayAction = action;
    }

    public void setOverlayActive(boolean active) {
        overlayActive = active;
        invalidate();
    }

    public void setOnModuleEventListener(ModuleEventListener listener) {
        moduleEventListener = listener;
    }

    public void openMenu() {
        closePending = false;
        if (!open) {
            setOpen(true);
        } else {
            pageAnim = Math.max(pageAnim, 0.72f);
            postInvalidateOnAnimation();
        }
        requestFocus();
    }

    public void setSafeInsets(int left, int top, int right, int bottom) {
        if (left == safeLeft && top == safeTop && right == safeRight && bottom == safeBottom) {
            return;
        }
        safeLeft = left;
        safeTop = top;
        safeRight = right;
        safeBottom = bottom;
        if (w > 0 && h > 0) {
            try {
                computeLayout();
            } catch (Throwable error) {
                CrashLog.write(getContext(), error);
                drawFailed = true;
            }
            invalidate();
        }
    }

    public boolean handleBack() {
        if (configModule != null) {
            closeConfig();
            return true;
        }
        if (open) {
            requestClose();
            return true;
        }
        return overlay;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        try {
            computeLayout();
        } catch (Throwable error) {
            CrashLog.write(getContext(), error);
            drawFailed = true;
        }
    }

    private void computeLayout() {
        w = getWidth();
        h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        u = Math.min(w / 800f, h / 360f);
        if (u <= 0f) {
            u = 1f;
        }

        float marginX = Math.max(w * 0.030f, safeLeft + 7f * u);
        float marginY = Math.max(h * 0.042f, safeTop + 7f * u);
        float rightMargin = Math.max(w * 0.030f, safeRight + 7f * u);
        float bottomMargin = Math.max(h * 0.042f, safeBottom + 7f * u);
        panel.set(marginX, marginY, w - rightMargin, h - bottomMargin);

        float railW = Math.min(149f * u, panel.width() * 0.235f);
        railRight = panel.left + railW;
        contentLeft = railRight + 29f * u;
        contentRight = panel.right - 24f * u;
        dividerY = panel.top + 78f * u;
        cardsTop = panel.top + 119f * u;
        footerLineY = panel.bottom - 31f * u;
        cardsViewport.set(contentLeft - 2f * u, cardsTop - 4f * u,
                contentRight + 2f * u, footerLineY - 15f * u);
        cardHeight = 76f * u;
        cardGap = 9f * u;

        int count = TABS.length;
        float navLeft = panel.left + 10f * u;
        float navRight = railRight - 11f * u;
        float navTop = panel.top + 76f * u;
        float navBottom = panel.bottom - 29f * u;
        float pitch = Math.min(33f * u, Math.max(20f * u,
                (navBottom - navTop) / Math.max(1, count)));
        float itemH = Math.max(17f * u, pitch - 2.5f * u);
        for (int i = 0; i < count; i++) {
            if (tabRects[i] == null) {
                tabRects[i] = new RectF();
            }
            tabRects[i].set(navLeft, navTop + i * pitch, navRight,
                    navTop + i * pitch + itemH);
        }

        closeRect.set(contentRight - 34f * u, panel.top + 12f * u,
                contentRight, panel.top + 46f * u);
        float capsuleW = 93f * u;
        capsuleRect.set(closeRect.left - 8f * u - capsuleW, panel.top + 16f * u,
                closeRect.left - 8f * u, panel.top + 38f * u);
        saveRect.set(contentRight - 94f * u, footerLineY + 3f * u,
                contentRight, footerLineY + 29f * u);

        float chipTop = panel.top + 88f * u;
        float chipH = 24f * u;
        float chipX = contentLeft;
        for (int i = 0; i < 3; i++) {
            float cw = chipWidth(i);
            chipRects[i].set(chipX, chipTop, chipX + cw, chipTop + chipH);
            chipX += cw + 6f * u;
        }

        float cw = 188f * u;
        float ch = 43f * u;
        launchRect.set(w * 0.5f - cw * 0.5f, h - bottomMargin - ch - 4f * u,
                w * 0.5f + cw * 0.5f, h - bottomMargin - 4f * u);

        hud.layout(w, h, u, safeLeft, safeTop, safeRight, safeBottom);
        buildPaints();
        buildBackdrop();
        buildCards();
    }

    private float chipWidth(int index) {
        String label;
        if (index == 0) {
            label = "ALL  " + two(tab.modules().size());
        } else if (index == 1) {
            label = "ON  " + two(Content.activeCount(tab));
        } else {
            int off = tab.modules().size() - Content.activeCount(tab);
            label = "OFF  " + two(off);
        }
        Paint p = Ui.text(6.6f * u, Theme.TEXT_DIM, Theme.monoMedium, 0.12f);
        return Math.max(54f * u, p.measureText(label) + 22f * u);
    }

    private void buildPaints() {
        pBrand = Ui.text(15.5f * u, Theme.TEXT, Theme.semibold, 0.10f);
        pBrandSub = Ui.text(5.2f * u, Theme.TEXT_FAINT, Theme.mono, 0.17f);
        pNavLabel = Ui.text(5.5f * u, Theme.TEXT_FAINT, Theme.mono, 0.20f);
        pNav = Ui.text(9.5f * u, Theme.TEXT_SOFT, Theme.medium, 0.015f);
        pNavIndex = Ui.text(5.5f * u, Theme.TEXT_FAINT, Theme.mono, 0.08f);
        pKicker = Ui.text(6.6f * u, Theme.LAVENDER, Theme.monoMedium, 0.18f);
        pTitle = Ui.text(27f * u, Theme.TEXT, Theme.light, -0.018f);
        pSubtitle = Ui.text(7.9f * u, Theme.TEXT_DIM, Theme.regular, 0.006f);
        pStatus = Ui.text(5.35f * u, Theme.TEXT_DIM, Theme.mono, 0.12f);
        pChip = Ui.text(6.6f * u, Theme.TEXT_DIM, Theme.monoMedium, 0.12f);
        pCount = Ui.text(5.7f * u, Theme.TEXT_FAINT, Theme.mono, 0.12f);
        pCardName = Ui.text(11.2f * u, Theme.TEXT, Theme.medium, -0.008f);
        pCardDesc = Ui.text(7.35f * u, Theme.TEXT_DIM, Theme.regular, 0.004f);
        pKey = Ui.text(5.9f * u, Theme.TEXT_DIM, Theme.monoMedium, 0.08f);
        pValue = Ui.text(6.7f * u, Theme.TEXT_SOFT, Theme.monoMedium, 0.02f);
        pFooter = Ui.text(5.9f * u, Theme.TEXT_FAINT, Theme.mono, 0.12f);
        pButton = Ui.text(6.0f * u, Theme.TEXT_SOFT, Theme.monoMedium, 0.10f);
        pToast = Ui.text(7.3f * u, Theme.TEXT, Theme.medium, 0.006f);
        pLaunchTitle = Ui.text(12.5f * u, Theme.TEXT, Theme.medium, 0.08f);
        pLaunchSub = Ui.text(5.3f * u, Theme.TEXT_FAINT, Theme.mono, 0.14f);
        pAction = Ui.text(6.3f * u, Theme.LAVENDER, Theme.monoMedium, 0.08f);
    }

    private void buildBackdrop() {
        bgShader = new LinearGradient(0f, 0f, 0f, h,
                0x8C07080C, 0xB80A0A12, Shader.TileMode.CLAMP);
        vignetteShader = new RadialGradient(w * 0.5f, h * 0.42f,
                Math.max(w, h) * 0.78f,
                new int[]{0x00000000, 0x00000000, 0x6E000000},
                new float[]{0f, 0.48f, 1f}, Shader.TileMode.CLAMP);

        panelFillShader = new LinearGradient(0f, panel.top, 0f, panel.bottom,
                0xF30B0D13, 0xF5090B10, Shader.TileMode.CLAMP);
        panelBorderShader = new LinearGradient(panel.left, panel.top,
                panel.right, panel.bottom,
                new int[]{Ui.alpha(Theme.LAVENDER, 0.24f), Ui.alpha(Theme.TEXT, 0.06f),
                        Ui.alpha(Theme.MINT, 0.17f)},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP);
        railShader = new LinearGradient(railRight, 0f, panel.left, 0f,
                Ui.alpha(Theme.TEXT, 0.035f), 0x00FFFFFF, Shader.TileMode.CLAMP);
        panelGlowLavender = new RadialGradient(panel.right - 90f * u,
                panel.top + 30f * u, 300f * u,
                new int[]{Ui.alpha(Theme.LAVENDER, 0.105f), 0x00C9B8FF},
                null, Shader.TileMode.CLAMP);
        panelGlowMint = new RadialGradient(panel.left + 80f * u,
                panel.bottom - 30f * u, 260f * u,
                new int[]{Ui.alpha(Theme.MINT, 0.06f), 0x009BE8DD},
                null, Shader.TileMode.CLAMP);
        accentWash = new LinearGradient(0f, 0f, 52f * u, 0f,
                Ui.alpha(Theme.LAVENDER, 0.09f), 0x00C9B8FF, Shader.TileMode.CLAMP);

        terrainShader0 = new LinearGradient(0f, h * 0.82f, 0f, h,
                0x00171324, 0xD0151222, Shader.TileMode.CLAMP);
        terrainShader1 = new LinearGradient(0f, h * 0.87f, 0f, h,
                0x000D111A, 0xE60D111A, Shader.TileMode.CLAMP);
        terrainShader2 = new LinearGradient(0f, h * 0.91f, 0f, h,
                0x00080A0F, 0xFF080A0F, Shader.TileMode.CLAMP);

        terrain0 = ridge(h * 0.925f, 22f * u, 11f * u, 1.3f);
        terrain1 = ridge(h * 0.958f, 17f * u, 9f * u, 4.1f);
        terrain2 = ridge(h * 0.988f, 13f * u, 7f * u, 2.2f);
        terrainEdge0 = ridgeEdge(h * 0.925f, 22f * u, 11f * u, 1.3f);
        terrainEdge1 = ridgeEdge(h * 0.958f, 17f * u, 9f * u, 4.1f);
    }

    private Path ridge(float base, float amp, float step, float phase) {
        Path p = new Path();
        p.moveTo(0f, h);
        float x = 0f;
        while (x <= w + step) {
            float n = (float) (Math.sin(x / w * 6.2f + phase) * 0.55f
                    + Math.sin(x / w * 15.3f + phase * 2.1f) * 0.26f
                    + Math.sin(x / w * 27.0f + phase * 0.7f) * 0.12f);
            float y = base + n * amp;
            y = Math.round(y / step) * step;
            p.lineTo(x, y);
            x += step;
        }
        p.lineTo(w + step, h);
        p.close();
        return p;
    }

    private Path ridgeEdge(float base, float amp, float step, float phase) {
        Path p = new Path();
        float x = 0f;
        boolean first = true;
        while (x <= w + step) {
            float n = (float) (Math.sin(x / w * 6.2f + phase) * 0.55f
                    + Math.sin(x / w * 15.3f + phase * 2.1f) * 0.26f
                    + Math.sin(x / w * 27.0f + phase * 0.7f) * 0.12f);
            float y = base + n * amp;
            y = Math.round(y / step) * step;
            if (first) {
                p.moveTo(x, y);
                first = false;
            } else {
                p.lineTo(x, y);
            }
            x += step;
        }
        return p;
    }

    private void buildCards() {
        if (w <= 0 || h <= 0) {
            return;
        }
        cards.clear();
        List<Module> modules = tab.modules();
        List<Module> shown = new ArrayList<>();
        for (Module m : modules) {
            if (tab == Tab.CONFIG || filter == 0
                    || (filter == 1 && m.enabled)
                    || (filter == 2 && !m.enabled)) {
                shown.add(m);
            }
        }
        int columns = (contentRight - contentLeft) >= 480f * u ? 2 : 1;
        float gap = cardGap;
        float available = contentRight - contentLeft;
        float cardW = (available - gap * (columns - 1)) / columns;
        for (int i = 0; i < shown.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float left = contentLeft + col * (cardW + gap);
            float top = cardsTop + row * (cardHeight + gap);
            cards.add(new Card(shown.get(i), new RectF(left, top, left + cardW, top + cardHeight)));
        }
        int rows = (shown.size() + columns - 1) / columns;
        float total = rows == 0 ? 0f : rows * cardHeight + (rows - 1) * gap;
        maxScroll = Math.max(0f, total - cardsViewport.height());
        scrollY = Math.max(0f, Math.min(scrollY, maxScroll));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawFailed) {
            drawFailure(canvas);
            return;
        }
        try {
            drawFrame(canvas);
        } catch (Throwable error) {
            CrashLog.write(getContext(), error);
            drawFailed = true;
            drawFailure(canvas);
        }
    }

    private void drawFrame(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        advance(now);
        if (overlay) {
            drawOverlayScrim(canvas);
        } else {
            drawBackdrop(canvas);
            float hudAlpha = open ? 0f : (1f - Ui.easeOutCubic(panelAnim));
            hud.draw(canvas, hudAlpha, now);
        }

        if (panelAnim > 0.002f) {
            drawPanel(canvas);
        }
        if (configModule != null && configAnim > 0.01f) {
            drawConfig(canvas);
        }
        if (!overlay && panelAnim < 0.998f) {
            drawLaunch(canvas, 1f - Ui.easeOutCubic(panelAnim));
        }
        if (open && now < toastUntil + 260L && !toastText.isEmpty()) {
            drawToast(canvas, now);
        }
    }

    private void drawFailure(Canvas canvas) {
        try {
            canvas.drawColor(0xFF07080C);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(0xFFF4F2F8);
            p.setTextSize(Math.max(12f, 13f * u));
            p.setTypeface(Theme.regular == null ? Typeface.DEFAULT : Theme.regular);
            canvas.drawText("AETHER could not draw this screen.", 28f, 48f, p);
            p.setTextSize(Math.max(9f, 10f * u));
            p.setColor(0xFF9693A3);
            canvas.drawText("The error was saved to Download/AETHER.", 28f, 74f, p);
        } catch (Throwable ignored) {
        }
    }

    private void advance(long now) {
        float dt = Math.min(0.05f, (now - lastFrameAt) / 1000f);
        lastFrameAt = now;
        boolean needsFrame = false;

        float target = open ? 1f : 0f;
        if (Math.abs(panelAnim - target) > 0.0015f) {
            panelAnim += (target - panelAnim) * Math.min(1f, dt * 13f);
            if (Math.abs(panelAnim - target) <= 0.0015f) {
                panelAnim = target;
            }
            needsFrame = true;
        }
        if (open && pageAnim < 1f) {
            pageAnim = Math.min(1f, pageAnim + dt * 5.2f);
            needsFrame = true;
        }
        for (Module m : Content.all()) {
            float mt = m.enabled ? 1f : 0f;
            if (Math.abs(m.toggleAnim - mt) > 0.002f) {
                m.toggleAnim += (mt - m.toggleAnim) * Math.min(1f, dt * 17f);
                needsFrame = true;
            } else {
                m.toggleAnim = mt;
            }
        }
        if (now < toastUntil + 260L) {
            needsFrame = true;
        }
        hud.tick(dt, now);
        if (panelAnim < 0.998f) {
            needsFrame = true;
        }
        if (configModule != null) {
            float configTarget = 1f;
            if (configAnim < configTarget) {
                configAnim += (configTarget - configAnim) * Math.min(1f, dt * 16f);
                if (configTarget - configAnim < 0.002f) {
                    configAnim = configTarget;
                }
            }
            needsFrame = true;
        } else if (configAnim > 0f) {
            configAnim = Math.max(0f, configAnim - dt * 12f);
            needsFrame = true;
        }
        if (needsFrame) {
            postInvalidateOnAnimation();
        }
    }

    /** Dims the app underneath without hiding it; there is no fake game scene in overlay mode. */
    private void drawOverlayScrim(Canvas canvas) {
        float e = Ui.easeOutCubic(panelAnim);
        if (e <= 0.002f) {
            return;
        }
        shape.setStyle(Paint.Style.FILL);
        shape.setShader(null);
        shape.setColor(Ui.alpha(0xFF04050A, 0.50f * e));
        canvas.drawRect(0f, 0f, w, h, shape);
        shape.setShader(new RadialGradient(w * 0.5f, h * 0.44f, Math.max(w, h) * 0.62f,
                new int[]{Ui.alpha(Theme.LAVENDER, 0.055f * e), 0x00C9B8FF},
                null, Shader.TileMode.CLAMP));
        canvas.drawRect(0f, 0f, w, h, shape);
        shape.setShader(null);
    }

    private void drawBackdrop(Canvas canvas) {
        shape.setShader(bgShader);
        canvas.drawRect(0f, 0f, w, h, shape);
        shape.setShader(terrainShader0);
        canvas.drawPath(terrain0, shape);
        shape.setShader(terrainShader1);
        canvas.drawPath(terrain1, shape);
        shape.setShader(terrainShader2);
        canvas.drawPath(terrain2, shape);
        shape.setShader(null);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(1f);
        line.setColor(Ui.alpha(Theme.LAVENDER, 0.09f));
        canvas.drawPath(terrainEdge0, line);
        line.setColor(Ui.alpha(Theme.MINT, 0.055f));
        canvas.drawPath(terrainEdge1, line);
        shape.setShader(vignetteShader);
        canvas.drawRect(0f, 0f, w, h, shape);
        shape.setShader(null);
    }

    private void drawPanel(Canvas canvas) {
        float eased = Ui.easeOutCubic(panelAnim);
        int alpha = Math.round(255f * eased);
        canvas.save();
        float scale = 0.986f + 0.014f * eased;
        canvas.scale(scale, scale, panel.centerX(), panel.centerY());
        canvas.translate(0f, (1f - eased) * 9f * u);
        if (alpha < 255) {
            canvas.saveLayerAlpha(null, alpha);
        }

        // Android adaptation of OpenNilore's layered SDF panel: shadow, smoked
        // glass, clipped grain, specular edge and gradient rim.
        AetherSurface.panel(canvas, panel, 18f * u, u, Theme.LAVENDER);

        canvas.save();
        Path clip = new Path();
        clip.addRoundRect(panel, 18f * u, 18f * u, Path.Direction.CW);
        canvas.clipPath(clip);

        shape.setShader(panelGlowLavender);
        canvas.drawRect(panel, shape);
        shape.setShader(panelGlowMint);
        canvas.drawRect(panel, shape);
        shape.setShader(railShader);
        canvas.drawRect(panel.left, panel.top, railRight, panel.bottom, shape);
        shape.setShader(null);
        drawInnerGrid(canvas);
        drawRail(canvas);
        drawPage(canvas);
        drawHeaderChrome(canvas);
        drawFooter(canvas);
        canvas.restore();

        drawPanelBorder(canvas);
        drawRailDivider(canvas);
        if (alpha < 255) {
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawPanelShadow(Canvas canvas) {
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(5f * u);
        for (int i = 1; i <= 4; i++) {
            float expand = i * 3.5f * u;
            line.setColor(Ui.alpha(0xFF000000, 0.055f / i));
            RectF r = new RectF(panel);
            r.inset(-expand, -expand);
            canvas.drawRoundRect(r, 18f * u + expand, 18f * u + expand, line);
        }
        line.setStrokeWidth(1f);
    }

    private void drawInnerGrid(Canvas canvas) {
        Paint p = line;
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1f);
        p.setColor(0x04FFFFFF);
        float step = 46f * u;
        for (float x = contentLeft; x < contentRight; x += step) {
            canvas.drawLine(x, dividerY, x, footerLineY, p);
        }
        for (float y = dividerY; y < footerLineY; y += step) {
            canvas.drawLine(contentLeft, y, contentRight, y, p);
        }
    }

    private void drawPanelBorder(Canvas canvas) {
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(1f);
        line.setShader(panelBorderShader);
        canvas.drawRoundRect(panel, 18f * u, 18f * u, line);
        line.setShader(null);
        Paint inner = Ui.stroke(0x08FFFFFF, 1f);
        RectF r = new RectF(panel);
        r.inset(1.5f * u, 1.5f * u);
        canvas.drawRoundRect(r, 17f * u, 17f * u, inner);
    }

    private void drawRailDivider(Canvas canvas) {
        Paint p = line;
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1f);
        p.setShader(new LinearGradient(0f, panel.top + 16f * u, 0f, panel.bottom - 16f * u,
                0x00FFFFFF, 0x18FFFFFF, Shader.TileMode.CLAMP));
        p.setColor(0xFFFFFFFF);
        canvas.drawLine(railRight, panel.top + 16f * u, railRight, panel.bottom - 16f * u, p);
        p.setShader(null);
    }

    private void drawRail(Canvas canvas) {
        float logoX = panel.left + 25f * u;
        float logoY = panel.top + 29f * u;
        drawLogo(canvas, logoX, logoY, 24f * u);

        textPaint.set(pBrand);
        textPaint.setColor(Theme.TEXT);
        canvas.drawText("AETHER", panel.left + 45f * u, panel.top + 33f * u, textPaint);
        textPaint.set(pBrandSub);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText("PVP INTERFACE", panel.left + 45f * u, panel.top + 45f * u, textPaint);

        textPaint.set(pNavLabel);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText("NAVIGATION", panel.left + 22f * u, panel.top + 68f * u, textPaint);

        for (int i = 0; i < TABS.length; i++) {
            drawTab(canvas, i);
        }

        float bottom = panel.bottom - 17f * u;
        Ui.dot(canvas, panel.left + 21f * u, bottom - 2f * u, 2.1f * u, Theme.MINT);
        textPaint.set(pFooter);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText("LOCAL SESSION", panel.left + 29f * u, bottom, textPaint);
        textPaint.set(pNavIndex);
        textPaint.setColor(Theme.TEXT_FAINT);
        Ui.textRight(canvas, "v1.0", railRight - 13f * u, bottom, textPaint);
    }

    private void drawLogo(Canvas canvas, float cx, float cy, float size) {
        for (int i = 3; i >= 1; i--) {
            canvas.drawCircle(cx, cy, size * (0.5f + i * 0.18f),
                    Ui.fill(Ui.alpha(Theme.LAVENDER, 0.025f / i)));
        }
        Paint ring = Ui.stroke(Ui.alpha(Theme.TEXT, 0.12f), 1f);
        canvas.drawCircle(cx, cy, size * 0.62f, ring);
        Paint star = new Paint(Paint.ANTI_ALIAS_FLAG);
        star.setStyle(Paint.Style.FILL);
        star.setShader(new LinearGradient(cx - size * 0.5f, cy - size * 0.5f,
                cx + size * 0.5f, cy + size * 0.5f,
                Theme.LAVENDER, Theme.MINT, Shader.TileMode.CLAMP));
        canvas.drawPath(Ui.sparkle(cx, cy, size * 0.43f), star);
        Ui.dot(canvas, cx + size * 0.53f, cy - size * 0.39f, 1.8f * u, Theme.MINT);
    }

    private void drawTab(Canvas canvas, int i) {
        RectF r = tabRects[i];
        if (r == null) {
            return;
        }
        boolean selected = i == tabIndex;
        boolean hovered = hoverActive && r.contains(hoverX, hoverY);
        if (selected) {
            shape.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                    Ui.alpha(Theme.LAVENDER, 0.145f), Ui.alpha(Theme.MINT, 0.025f),
                    Shader.TileMode.CLAMP));
            canvas.drawRoundRect(r, 9f * u, 9f * u, shape);
            shape.setShader(null);
            Paint stroke = Ui.stroke(Ui.alpha(Theme.TEXT, 0.10f), 1f);
            canvas.drawRoundRect(r, 9f * u, 9f * u, stroke);
            Paint accent = Ui.fill(0xFFFFFFFF);
            accent.setShader(new LinearGradient(0f, r.centerY() - 7f * u, 0f,
                    r.centerY() + 7f * u, Theme.LAVENDER, Theme.MINT, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(r.left + 1.5f * u, r.centerY() - 7f * u,
                    r.left + 3.5f * u, r.centerY() + 7f * u, 1f * u, 1f * u, accent);
        } else if (hovered || pressedTab == i) {
            canvas.drawRoundRect(r, 9f * u, 9f * u, Ui.fill(0x07FFFFFF));
        }

        iconPaint.setStyle(Paint.Style.STROKE);
        iconPaint.setStrokeWidth(Math.max(1.1f, 13f * u * 0.07f));
        iconPaint.setColor(selected ? Theme.LAVENDER : Theme.TEXT_FAINT);
        Icons.draw(canvas, TABS[i].icon, r.left + 17f * u, r.centerY() + 0.5f * u,
                13f * u, iconPaint);

        textPaint.set(pNav);
        textPaint.setColor(selected ? Theme.TEXT : Theme.TEXT_DIM);
        canvas.drawText(TABS[i].label, r.left + 34f * u, r.centerY() + 3.2f * u, textPaint);

        textPaint.set(pNavIndex);
        textPaint.setColor(selected ? Ui.alpha(Theme.LAVENDER, 0.9f) : Theme.TEXT_FAINT);
        Ui.textRight(canvas, "0" + (i + 1), r.right - 9f * u,
                r.centerY() + 3f * u, textPaint);
    }

    private void drawHeaderChrome(Canvas canvas) {
        if (pStatus == null) {
            return;
        }
        boolean capsuleHover = hoverActive && capsuleRect.contains(hoverX, hoverY);
        shape.setShader(null);
        shape.setColor(capsuleHover ? 0x0BFFFFFF : 0x06FFFFFF);
        canvas.drawRoundRect(capsuleRect, 11f * u, 11f * u, shape);
        Paint cStroke = Ui.stroke(0x14FFFFFF, 1f);
        canvas.drawRoundRect(capsuleRect, 11f * u, 11f * u, cStroke);
        Ui.dot(canvas, capsuleRect.left + 11f * u, capsuleRect.centerY(), 2.1f * u,
                overlayActive ? Theme.LAVENDER : Theme.MINT);
        textPaint.set(pStatus);
        textPaint.setColor(overlayActive ? Theme.LAVENDER : Theme.TEXT_DIM);
        canvas.drawText(overlayActive ? "OVERLAY · ON"
                        : overlayAction != null ? "FLOAT · OFF" : "OPENGL · SKIA",
                capsuleRect.left + 18f * u, capsuleRect.centerY() + 1.9f * u, textPaint);

        boolean closeHover = hoverActive && closeRect.contains(hoverX, hoverY);
        if (pressedClose || closeHover) {
            canvas.drawCircle(closeRect.centerX(), closeRect.centerY(), closeRect.width() * 0.5f,
                    Ui.fill(pressedClose ? 0x12FFFFFF : 0x09FFFFFF));
        }
        Paint closeRing = Ui.stroke(0x16FFFFFF, 1f);
        canvas.drawCircle(closeRect.centerX(), closeRect.centerY(), closeRect.width() * 0.5f, closeRing);
        iconPaint.setStyle(Paint.Style.STROKE);
        iconPaint.setColor(closeHover || pressedClose ? Theme.TEXT : Theme.TEXT_DIM);
        Icons.draw(canvas, Icons.CLOSE, closeRect.centerX(), closeRect.centerY(),
                9.5f * u, iconPaint);
    }

    private void drawPage(Canvas canvas) {
        float e = Ui.easeOutCubic(pageAnim);
        int alpha = Math.round(255f * e);
        if (alpha <= 0) {
            return;
        }
        canvas.save();
        canvas.clipRect(contentLeft - 2f * u, panel.top + 8f * u, contentRight + 8f * u, footerLineY);
        if (alpha < 255) {
            canvas.saveLayerAlpha(null, alpha);
        }
        canvas.translate(0f, (1f - e) * 9f * u);

        drawPageHeader(canvas);
        drawFilters(canvas);
        drawCards(canvas);

        if (alpha < 255) {
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawPageHeader(Canvas canvas) {
        float kickerY = panel.top + 20f * u;
        Ui.dot(canvas, contentLeft + 2.5f * u, kickerY - 2f * u, 2.2f * u, Theme.LAVENDER);
        textPaint.set(pKicker);
        textPaint.setColor(Theme.LAVENDER);
        canvas.drawText(tab.kicker, contentLeft + 10f * u, kickerY, textPaint);

        textPaint.set(pTitle);
        textPaint.setColor(Theme.TEXT);
        canvas.drawText(tab.label, contentLeft, panel.top + 50f * u, textPaint);

        textPaint.set(pSubtitle);
        textPaint.setColor(Theme.TEXT_DIM);
        canvas.drawText(tab.subtitle, contentLeft, panel.top + 66f * u, textPaint);

        Paint rule = line;
        rule.setStyle(Paint.Style.STROKE);
        rule.setStrokeWidth(1f);
        rule.setShader(null);
        rule.setColor(Ui.alpha(Theme.LAVENDER, 0.36f));
        canvas.drawLine(contentLeft, dividerY, contentLeft + 27f * u, dividerY, rule);
        rule.setColor(0x0FFFFFFF);
        canvas.drawLine(contentLeft + 33f * u, dividerY, contentRight, dividerY, rule);
    }

    private void drawFilters(Canvas canvas) {
        int active = Content.activeCount(tab);
        int total = tab.modules().size();
        textPaint.set(pCount);
        textPaint.setColor(Theme.TEXT_FAINT);
        String count = tab == Tab.CONFIG
                ? two(activeProfile + 1) + " / 03 PROFILE"
                : two(total) + " MODULES";
        Ui.textRight(canvas, count, contentRight, chipRects[0].centerY() + 2.2f * u, textPaint);

        for (int i = 0; i < 3; i++) {
            if (tab == Tab.CONFIG) {
                break;
            }
            RectF r = chipRects[i];
            boolean sel = filter == i;
            boolean hover = hoverActive && r.contains(hoverX, hoverY);
            shape.setShader(null);
            shape.setColor(sel ? 0x0EFFFFFF : (pressedChip == i ? 0x0AFFFFFF : (hover ? 0x06FFFFFF : 0x00FFFFFF)));
            canvas.drawRoundRect(r, 11f * u, 11f * u, shape);
            Paint b = Ui.stroke(sel ? Ui.alpha(Theme.LAVENDER, 0.35f) : 0x12FFFFFF, 1f);
            canvas.drawRoundRect(r, 11f * u, 11f * u, b);
            textPaint.set(pChip);
            textPaint.setColor(sel ? Theme.TEXT : Theme.TEXT_DIM);
            String label = i == 0 ? "ALL  " + two(total)
                    : i == 1 ? "ON  " + two(active)
                    : "OFF  " + two(total - active);
            Ui.textCenter(canvas, label, r.centerX(), r.centerY() + 2.3f * u, textPaint);
        }

        if (tab == Tab.CONFIG) {
            textPaint.set(pCount);
            textPaint.setColor(Theme.TEXT_FAINT);
            canvas.drawText("PROFILES ARE STORED ON THIS DEVICE", contentLeft + 2f * u,
                    chipRects[0].centerY() + 2.2f * u, textPaint);
        }
    }

    private void drawCards(Canvas canvas) {
        canvas.save();
        canvas.clipRect(cardsViewport);
        if (cards.isEmpty()) {
            drawEmpty(canvas);
        } else {
            for (Card card : cards) {
                float top = card.rect.top - scrollY;
                if (top > cardsViewport.bottom || top + cardHeight < cardsViewport.top) {
                    continue;
                }
                drawCard(canvas, card, top);
            }
        }
        canvas.restore();

        if (maxScroll > 0f) {
            drawScrollFades(canvas);
            drawScrollbar(canvas);
        }
    }

    private void drawEmpty(Canvas canvas) {
        float cx = (contentLeft + contentRight) * 0.5f;
        float cy = cardsViewport.centerY() - 6f * u;
        drawLogo(canvas, cx, cy - 26f * u, 24f * u);
        textPaint.set(pTitle);
        textPaint.setTextSize(15f * u);
        textPaint.setTypeface(Theme.light);
        textPaint.setColor(Theme.TEXT_DIM);
        Ui.textCenter(canvas, "Quiet here", cx, cy + 6f * u, textPaint);
        textPaint.set(pCardDesc);
        textPaint.setColor(Theme.TEXT_FAINT);
        Ui.textCenter(canvas, "No modules match this view.", cx, cy + 21f * u, textPaint);
    }

    private void drawCard(Canvas canvas, Card card, float top) {
        Module m = card.module;
        RectF r = new RectF(card.rect.left, top, card.rect.right, top + cardHeight);
        boolean on = m.enabled;
        boolean hover = hoverActive && r.contains(hoverX, hoverY);
        boolean pressed = pressedModule == m || activeSlider == m;

        canvas.save();
        Path clip = new Path();
        clip.addRoundRect(r, 10f * u, 10f * u, Path.Direction.CW);
        canvas.clipPath(clip);

        shape.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                on ? 0x13C9B8FF : 0x0AFFFFFF,
                on ? 0x079BE8DD : 0x04FFFFFF, Shader.TileMode.CLAMP));
        canvas.drawRect(r, shape);
        shape.setShader(null);
        shape.setColor(on ? 0x06FFFFFF : 0x00FFFFFF);
        canvas.drawRect(r, shape);

        if (on) {
            Paint wash = Ui.fill(0xFFFFFFFF);
            wash.setShader(accentWash);
            canvas.save();
            canvas.translate(r.left, 0f);
            canvas.drawRect(0f, r.top, 52f * u, r.bottom, wash);
            canvas.restore();
            Paint accent = Ui.fill(0xFFFFFFFF);
            accent.setShader(new LinearGradient(0f, r.top + 15f * u, 0f, r.bottom - 15f * u,
                    Theme.LAVENDER, Theme.MINT, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(r.left + 1.2f * u, r.top + 15f * u,
                    r.left + 3.2f * u, r.bottom - 15f * u, 1f * u, 1f * u, accent);
        }

        if (pressed) {
            canvas.drawRect(r, Ui.fill(0x07FFFFFF));
        } else if (hover) {
            canvas.drawRect(r, Ui.fill(0x04FFFFFF));
        }
        canvas.restore();

        Paint borderPaint = Ui.stroke(on ? 0xFFFFFFFF : 0x12FFFFFF, 1f);
        if (on) {
            borderPaint.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                    Ui.alpha(Theme.LAVENDER, 0.31f), Ui.alpha(Theme.MINT, 0.16f),
                    Shader.TileMode.CLAMP));
        }
        canvas.drawRoundRect(r, 10f * u, 10f * u, borderPaint);

        textPaint.set(pCardName);
        textPaint.setColor(on ? Theme.TEXT : Theme.TEXT_SOFT);
        float nameRight = r.right - (m.isAction() ? 15f * u : 55f * u);
        String name = Ui.ellipsize(m.name, textPaint, Math.max(30f * u, nameRight - (r.left + 14f * u)));
        canvas.drawText(name, r.left + 14f * u, r.top + 24f * u, textPaint);

        textPaint.set(pCardDesc);
        textPaint.setColor(on ? Theme.TEXT_DIM : Theme.TEXT_FAINT);
        String desc = Ui.ellipsize(m.description, textPaint, r.width() - 28f * u);
        canvas.drawText(desc, r.left + 14f * u, r.top + 40f * u, textPaint);

        if (m.isAction()) {
            drawAction(canvas, r, m);
        } else if (m.hasSlider) {
            drawSlider(canvas, r, m, on);
            drawToggle(canvas, r.right - 14f * u - 30f * u, r.top + 14f * u, m);
            drawConfigHint(canvas, r, true);
        } else {
            drawBadge(canvas, r.left + 14f * u, r.top + 50f * u, m.badge);
            drawToggle(canvas, r.right - 14f * u - 30f * u, r.top + 14f * u, m);
            drawConfigHint(canvas, r, false);
        }
    }

    private void drawConfigHint(Canvas canvas, RectF r, boolean compact) {
        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        if (compact) {
            canvas.drawText("CONFIG", r.left + 14f * u, r.top + 67f * u, textPaint);
        } else {
            Ui.textRight(canvas, "CONFIG", r.right - 15f * u, r.top + 65f * u, textPaint);
        }
    }

    private void drawAction(Canvas canvas, RectF r, Module m) {
        int index = profileIndex(m.id);
        boolean active = index == activeProfile && index >= 0;
        if (active) {
            Paint check = Ui.stroke(Theme.MINT, 1.2f * u);
            Icons.draw(canvas, Icons.CHECK, r.right - 12f * u, r.top + 58f * u,
                    8f * u, check);
            textPaint.set(pAction);
            textPaint.setColor(Theme.MINT);
            Ui.textRight(canvas, "ACTIVE", r.right - 24f * u, r.top + 61f * u, textPaint);
        } else {
            textPaint.set(pAction);
            textPaint.setColor(Theme.LAVENDER);
            Ui.textRight(canvas, m.actionLabel, r.right - 24f * u, r.top + 61f * u, textPaint);
            Paint arrow = Ui.stroke(Theme.LAVENDER, 1.1f * u);
            Icons.draw(canvas, Icons.ARROW_RIGHT, r.right - 12f * u, r.top + 58f * u,
                    8.5f * u, arrow);
        }
        if (!m.badge.isEmpty()) {
            drawBadge(canvas, r.left + 14f * u, r.top + 50f * u, m.badge);
        }
    }

    private void drawBadge(Canvas canvas, float x, float y, String label) {
        if (label == null || label.isEmpty()) {
            return;
        }
        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        float bw = textPaint.measureText(label) + 13f * u;
        RectF br = new RectF(x, y, x + bw, y + 17f * u);
        canvas.drawRoundRect(br, 5f * u, 5f * u, Ui.fill(0x08FFFFFF));
        canvas.drawRoundRect(br, 5f * u, 5f * u, Ui.stroke(0x10FFFFFF, 1f));
        Ui.textCenter(canvas, label, br.centerX(), br.centerY() + 2f * u, textPaint);
    }

    private void drawSlider(Canvas canvas, RectF r, Module m, boolean on) {
        float left = r.left + 14f * u;
        float right = r.right - 60f * u;
        float y = r.top + 59f * u;
        float ratio = m.ratio();
        Paint track = Ui.fill(0x12FFFFFF);
        canvas.drawRoundRect(left, y - 1f * u, right, y + 1f * u, 1f * u, 1f * u, track);
        if (ratio > 0f) {
            Paint fill = Ui.fill(0xFFFFFFFF);
            fill.setShader(new LinearGradient(left, 0f, right, 0f,
                    Ui.alpha(Theme.LAVENDER, on ? 0.9f : 0.55f),
                    Ui.alpha(Theme.MINT, on ? 0.8f : 0.45f), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(left, y - 1f * u, left + (right - left) * ratio, y + 1f * u,
                    1f * u, 1f * u, fill);
        }
        float tx = left + (right - left) * ratio;
        canvas.drawCircle(tx, y, 7f * u, Ui.fill(Ui.alpha(Theme.LAVENDER, on ? 0.13f : 0.07f)));
        canvas.drawCircle(tx, y, 4.2f * u, Ui.fill(on ? Theme.TEXT : Theme.TEXT_SOFT));
        canvas.drawCircle(tx, y, 1.6f * u, Ui.fill(on ? Theme.LAVENDER : Theme.TEXT_DIM));

        textPaint.set(pValue);
        textPaint.setColor(on ? Theme.TEXT : Theme.TEXT_DIM);
        Ui.textRight(canvas, m.valueLabel(), r.right - 14f * u, y + 2.4f * u, textPaint);
    }

    private void drawToggle(Canvas canvas, float x, float y, Module m) {
        float tw = 30f * u;
        float th = 15f * u;
        RectF track = new RectF(x, y, x + tw, y + th);
        float t = Ui.clamp(m.toggleAnim, 0f, 1f);
        if (t > 0.02f) {
            canvas.drawRoundRect(track.left - 2f * u, track.top - 2f * u,
                    track.right + 2f * u, track.bottom + 2f * u, 10f * u, 10f * u,
                    Ui.fill(Ui.alpha(Theme.LAVENDER, 0.065f * t)));
        }
        Paint bg = Ui.fill(0xFFFFFFFF);
        if (t > 0.01f) {
            bg.setShader(new LinearGradient(track.left, track.top, track.right, track.bottom,
                    Ui.mix(0x18FFFFFF, Ui.alpha(Theme.LAVENDER, 0.95f), t),
                    Ui.mix(0x18FFFFFF, Ui.alpha(Theme.MINT, 0.82f), t), Shader.TileMode.CLAMP));
        } else {
            bg.setColor(0x14FFFFFF);
        }
        canvas.drawRoundRect(track, th * 0.5f, th * 0.5f, bg);
        Paint ring = Ui.stroke(t > 0.5f ? 0x00FFFFFF : 0x18FFFFFF, 1f);
        canvas.drawRoundRect(track, th * 0.5f, th * 0.5f, ring);
        float knobX = Ui.lerp(track.left + 7.5f * u, track.right - 7.5f * u, t);
        float knobR = 5.2f * u;
        canvas.drawCircle(knobX, track.centerY(), knobR,
                Ui.fill(t > 0.5f ? 0xFF0B0D13 : 0xFF9692A4));
    }

    private void drawScrollFades(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        boolean active = dragging || now - lastScrollAt < 650L;
        if (scrollY > 0.5f) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setShader(new LinearGradient(0f, cardsViewport.top, 0f, cardsViewport.top + 16f * u,
                    0xF20B0D13, 0x000B0D13, Shader.TileMode.CLAMP));
            canvas.drawRect(cardsViewport.left, cardsViewport.top, cardsViewport.right,
                    cardsViewport.top + 16f * u, p);
        }
        if (active && scrollY < maxScroll - 0.5f) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setShader(new LinearGradient(0f, cardsViewport.bottom - 16f * u, 0f,
                    cardsViewport.bottom, 0x000B0D13, 0xF20B0D13, Shader.TileMode.CLAMP));
            canvas.drawRect(cardsViewport.left, cardsViewport.bottom - 16f * u,
                    cardsViewport.right, cardsViewport.bottom, p);
        }
    }

    private void drawScrollbar(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        float age = (now - lastScrollAt) / (float) SCROLLBAR_MS;
        float vis = Ui.clamp(1f - age, 0f, 1f);
        if (dragging) {
            vis = 1f;
        }
        if (vis <= 0.03f) {
            return;
        }
        float trackTop = cardsViewport.top + 3f * u;
        float trackBottom = cardsViewport.bottom - 3f * u;
        float trackH = trackBottom - trackTop;
        canvas.drawRoundRect(contentRight + 5f * u, trackTop, contentRight + 6.6f * u,
                trackBottom, 1f * u, 1f * u, Ui.fill(Ui.alpha(Theme.TEXT, 0.035f * vis)));
        float thumbH = Math.max(18f * u, trackH * trackH / (trackH + maxScroll));
        float thumbTop = trackTop + (maxScroll <= 0f ? 0f : (scrollY / maxScroll) * (trackH - thumbH));
        Paint thumb = Ui.fill(0xFFFFFFFF);
        thumb.setShader(new LinearGradient(0f, thumbTop, 0f, thumbTop + thumbH,
                Ui.alpha(Theme.LAVENDER, 0.38f * vis), Ui.alpha(Theme.MINT, 0.24f * vis),
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(contentRight + 4.4f * u, thumbTop, contentRight + 7.2f * u,
                thumbTop + thumbH, 1.4f * u, 1.4f * u, thumb);
    }

    private void drawFooter(Canvas canvas) {
        Paint rule = line;
        rule.setStyle(Paint.Style.STROKE);
        rule.setStrokeWidth(1f);
        rule.setShader(new LinearGradient(contentLeft, 0f, contentRight, 0f,
                0x14FFFFFF, 0x04FFFFFF, Shader.TileMode.CLAMP));
        rule.setColor(0xFFFFFFFF);
        canvas.drawLine(contentLeft, footerLineY, contentRight, footerLineY, rule);
        rule.setShader(null);

        float baseline = footerLineY + 19f * u;
        int active = Content.activeCount(tab);
        int total = tab.modules().size();
        Ui.dot(canvas, contentLeft + 2.5f * u, baseline - 2f * u, 2.1f * u,
                active > 0 ? Theme.MINT : Theme.TEXT_FAINT);
        textPaint.set(pFooter);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText(String.format(Locale.US, "%02d / %02d ACTIVE", active, total),
                contentLeft + 10f * u, baseline, textPaint);

        boolean hover = hoverActive && saveRect.contains(hoverX, hoverY);
        if (pressedSave) {
            canvas.drawRoundRect(saveRect, 11f * u, 11f * u, Ui.fill(0x10FFFFFF));
        } else if (hover) {
            canvas.drawRoundRect(saveRect, 11f * u, 11f * u, Ui.fill(0x08FFFFFF));
        }
        Paint saveBorder = Ui.stroke(hover || pressedSave
                ? Ui.alpha(Theme.LAVENDER, 0.34f) : 0x16FFFFFF, 1f);
        canvas.drawRoundRect(saveRect, 11f * u, 11f * u, saveBorder);
        textPaint.set(pButton);
        textPaint.setColor(hover || pressedSave ? Theme.TEXT : Theme.TEXT_DIM);
        Ui.textCenter(canvas, tab == Tab.HUD ? "PREVIEW HUD" : "SAVE PROFILE",
                saveRect.centerX(), saveRect.centerY() + 2.1f * u, textPaint);
    }

    private void openConfig(Module module) {
        if (module == null || module.isAction()) {
            return;
        }
        configModule = module;
        configAnim = 0f;
        configSlider = -1;
        configScroll = 0f;
        configNumberRow = -1;
        configGesture = false;
        configDragging = false;
        clearConfigPresses();
        layoutConfig();
        invalidate();
    }

    private void closeConfig() {
        configModule = null;
        configAnim = 0f;
        configSlider = -1;
        configScroll = 0f;
        configNumberRow = -1;
        configGesture = false;
        configDragging = false;
        clearConfigPresses();
        invalidate();
    }

    private void layoutConfig() {
        if (configModule == null) {
            return;
        }
        boolean settings = !configModule.settings.isEmpty();
        float mw = Math.min(settings ? 536f * u : 520f * u,
                panel.width() - 34f * u);
        float mh = settings
                ? Math.min(338f * u, panel.height() - 20f * u)
                : Math.min(286f * u, panel.height() - 22f * u);
        float left = panel.centerX() - mw * 0.5f;
        float top = panel.centerY() - mh * 0.5f;
        configPanel.set(left, top, left + mw, top + mh);

        configClose.set(configPanel.right - 39f * u, configPanel.top + 14f * u,
                configPanel.right - 13f * u, configPanel.top + 40f * u);
        configToggle.set(configPanel.right - 74f * u, configPanel.top + 27f * u,
                configPanel.right - 44f * u, configPanel.top + 42f * u);

        float bodyTop = configPanel.top + 96f * u;
        float footerTop = configPanel.bottom - (settings ? 38f : 34f) * u;
        configViewport.set(configPanel.left + 16f * u, bodyTop,
                configPanel.right - 16f * u, footerTop - (settings ? 7f : 0f) * u);

        configReset.set(configPanel.left + 24f * u,
                configPanel.bottom - (settings ? 31f : 34f) * u + 8f * u,
                configPanel.left + 128f * u,
                configPanel.bottom - (settings ? 31f : 34f) * u + 28f * u);
        configDone.set(configPanel.right - 84f * u,
                configPanel.bottom - (settings ? 31f : 34f) * u + 7f * u,
                configPanel.right - 24f * u,
                configPanel.bottom - (settings ? 31f : 34f) * u + 27f * u);

        if (settings) {
            layoutSettingRows();
            return;
        }

        configRows.clear();
        configScroll = 0f;
        configMaxScroll = 0f;
        float rowH = (footerTop - bodyTop) / 5f;
        float controlX = configPanel.left + 100f * u;
        float valueRight = configPanel.right - 24f * u;

        float keyY = bodyTop + rowH * 0.5f - 10f * u;
        float kw = 34f * u;
        for (int i = 0; i < 5; i++) {
            configKeys[i].set(controlX + i * (kw + 4f * u), keyY,
                    controlX + i * (kw + 4f * u) + kw, keyY + 20f * u);
        }

        float modeY = bodyTop + rowH * 1.5f - 10f * u;
        float modeChipW = 62f * u;
        for (int i = 0; i < 3; i++) {
            configModes[i].set(controlX + i * (modeChipW + 4f * u), modeY,
                    controlX + i * (modeChipW + 4f * u) + modeChipW, modeY + 20f * u);
        }

        for (int i = 0; i < 2; i++) {
            float cy = bodyTop + rowH * (2.5f + i);
            configSliderTracks[i].set(controlX, cy - 10f * u,
                    valueRight - 44f * u, cy + 10f * u);
        }

        float accentY = bodyTop + rowH * 4.5f;
        for (int i = 0; i < 4; i++) {
            float cx = controlX + 8f * u + i * 25f * u;
            configAccents[i].set(cx - 9f * u, accentY - 9f * u,
                    cx + 9f * u, accentY + 9f * u);
        }
    }

    private void layoutSettingRows() {
        configRows.clear();
        float width = Math.max(1f, configViewport.width());
        Paint measure = Ui.text(7.1f * u, Theme.TEXT_DIM, Theme.regular, 0f);
        float y = 0f;
        for (ConfigSetting setting : configModule.settings) {
            ConfigRow row = new ConfigRow(setting);
            if (setting.type == ConfigSetting.BOOLEAN) {
                row.bounds.set(0f, y, width, y + 30f * u);
                row.toggle.set(width - 38f * u, y + 7f * u,
                        width - 8f * u, y + 23f * u);
                y = row.bounds.bottom + 4f * u;
            } else if (setting.type == ConfigSetting.NUMBER) {
                row.bounds.set(0f, y, width, y + 46f * u);
                row.track.set(2f * u, y + 29f * u, width - 2f * u, y + 35f * u);
                y = row.bounds.bottom + 4f * u;
            } else {
                float chipX = 0f;
                float chipY = y + 21f * u;
                for (int i = 0; i < setting.options.length; i++) {
                    float chipW = Math.min(width,
                            measure.measureText(setting.options[i]) + 15f * u);
                    if (chipX > 0f && chipX + chipW > width) {
                        chipX = 0f;
                        chipY += 21f * u;
                    }
                    ConfigChip chip = new ConfigChip(i);
                    chip.rect.set(chipX, chipY, chipX + chipW, chipY + 18f * u);
                    row.chips.add(chip);
                    chipX += chipW + 4f * u;
                }
                row.bounds.set(0f, y, width, chipY + 22f * u);
                y = row.bounds.bottom + 4f * u;
            }
            configRows.add(row);
        }
        configContentHeight = Math.max(0f, y);
        configMaxScroll = Math.max(0f, configContentHeight - configViewport.height());
        configScroll = Ui.clamp(configScroll, 0f, configMaxScroll);
    }

    private void drawConfig(Canvas canvas) {
        if (configModule == null) {
            return;
        }
        layoutConfig();
        Module m = configModule;
        float e = Ui.easeOutCubic(configAnim);
        int layerAlpha = Math.round(255f * e);
        canvas.drawRect(0f, 0f, w, h, Ui.fill(Ui.alpha(0xFF000000, 0.54f * e)));
        canvas.saveLayerAlpha(null, layerAlpha);

        for (int i = 5; i >= 1; i--) {
            RectF shadow = new RectF(configPanel);
            shadow.inset(-i * 4f * u, -i * 4f * u);
            canvas.drawRoundRect(shadow, 20f * u + i * 4f * u,
                    20f * u + i * 4f * u, Ui.fill(Ui.alpha(0xFF000000, 0.035f / i)));
        }
        Paint fill = Ui.fill(0xFFFFFFFF);
        fill.setShader(new LinearGradient(configPanel.left, configPanel.top,
                configPanel.right, configPanel.bottom, 0xFA0D0F16, 0xFA080A0F,
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(configPanel, 18f * u, 18f * u, fill);
        Paint border = Ui.stroke(0xFFFFFFFF, 1f);
        border.setShader(new LinearGradient(configPanel.left, configPanel.top,
                configPanel.right, configPanel.bottom,
                new int[]{Ui.alpha(Theme.LAVENDER, 0.46f), 0x18FFFFFF,
                        Ui.alpha(Theme.MINT, 0.30f)}, null, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(configPanel, 18f * u, 18f * u, border);

        float left = configPanel.left + 24f * u;
        textPaint.set(pKicker);
        textPaint.setColor(Theme.LAVENDER);
        canvas.drawText(tab.kicker + " / MODULE", left, configPanel.top + 22f * u, textPaint);

        textPaint.set(pTitle);
        textPaint.setTextSize(19f * u);
        textPaint.setTypeface(Theme.light);
        textPaint.setColor(Theme.TEXT);
        String title = Ui.ellipsize(m.name, textPaint,
                configToggle.left - left - 14f * u);
        canvas.drawText(title, left, configPanel.top + 50f * u, textPaint);

        textPaint.set(pCardDesc);
        textPaint.setTextSize(7.2f * u);
        textPaint.setTypeface(Theme.regular);
        textPaint.setColor(Theme.TEXT_DIM);
        String desc = Ui.ellipsize(m.description, textPaint,
                configPanel.width() - 48f * u);
        canvas.drawText(desc, left, configPanel.top + 67f * u, textPaint);

        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        Ui.textRight(canvas, "ENABLED", configToggle.left - 7f * u,
                configToggle.centerY() + 2f * u, textPaint);
        drawToggle(canvas, configToggle.left, configToggle.top, m);

        boolean closeHover = hoverActive && configClose.contains(hoverX, hoverY);
        if (pressedConfigClose || closeHover) {
            canvas.drawCircle(configClose.centerX(), configClose.centerY(),
                    configClose.width() * 0.5f, Ui.fill(0x0BFFFFFF));
        }
        Paint closeRing = Ui.stroke(0x16FFFFFF, 1f);
        canvas.drawCircle(configClose.centerX(), configClose.centerY(),
                configClose.width() * 0.5f, closeRing);
        iconPaint.setStyle(Paint.Style.STROKE);
        iconPaint.setColor(closeHover || pressedConfigClose ? Theme.TEXT : Theme.TEXT_DIM);
        Icons.draw(canvas, Icons.CLOSE, configClose.centerX(), configClose.centerY(),
                9f * u, iconPaint);

        Paint rule = Ui.stroke(0x0FFFFFFF, 1f);
        canvas.drawLine(left, configPanel.top + 80f * u,
                configPanel.right - 24f * u, configPanel.top + 80f * u, rule);

        if (m.settings.isEmpty()) {
            if (m.id.startsWith("nilore.")) {
                drawNoSettings(canvas, m, left);
            } else {
                drawLegacyConfigBody(canvas, m, left);
            }
        } else {
            drawSettingsBody(canvas);
        }
        drawConfigFooter(canvas, left);
        canvas.restore();
    }

    private void drawNoSettings(Canvas canvas, Module m, float left) {
        float top = configPanel.top + 118f * u;
        float right = configPanel.right - 24f * u;
        canvas.drawRoundRect(left, top, right, top + 92f * u, 10f * u, 10f * u,
                Ui.fill(0x03FFFFFF));
        canvas.drawRoundRect(left, top, right, top + 92f * u, 10f * u, 10f * u,
                Ui.stroke(0x0FFFFFFF, 1f));
        textPaint.set(pKicker);
        textPaint.setColor(Theme.LAVENDER);
        Ui.textCenter(canvas, "NO MODULE SETTINGS", (left + right) * 0.5f,
                top + 32f * u, textPaint);
        textPaint.set(pCardDesc);
        textPaint.setColor(Theme.TEXT_DIM);
        Ui.textCenter(canvas, "OpenNilore exposes only an enable state for " + m.name + ".",
                (left + right) * 0.5f, top + 51f * u, textPaint);
        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        Ui.textCenter(canvas, "TOGGLE IT FROM THE CARD OR THE SWITCH ABOVE",
                (left + right) * 0.5f, top + 70f * u, textPaint);
    }

    private void drawSettingsBody(Canvas canvas) {
        if (configModule == null) {
            return;
        }
        canvas.save();
        canvas.clipRect(configViewport);
        float x = configViewport.left;
        for (int i = 0; i < configRows.size(); i++) {
            ConfigRow row = configRows.get(i);
            float top = configViewport.top + row.bounds.top - configScroll;
            if (top + row.bounds.height() < configViewport.top
                    || top > configViewport.bottom) {
                continue;
            }
            boolean pressed = pressedConfigRow == i;
            boolean hover = hoverActive && hoverX >= configViewport.left
                    && hoverX <= configViewport.right
                    && hoverY >= top && hoverY <= top + row.bounds.height();
            if (pressed || hover) {
                canvas.drawRoundRect(x - 2f * u, top + 1f * u,
                        x + row.bounds.width() + 2f * u,
                        top + row.bounds.height() - 1f * u,
                        6f * u, 6f * u, Ui.fill(pressed ? 0x07FFFFFF : 0x04FFFFFF));
            }
            drawSettingRow(canvas, row, x, top, i);
        }
        canvas.restore();

        if (configMaxScroll > 0f) {
            float trackTop = configViewport.top + 2f * u;
            float trackH = configViewport.height() - 4f * u;
            float thumbH = Math.max(20f * u,
                    trackH * configViewport.height() / Math.max(1f, configContentHeight));
            float progress = configScroll / configMaxScroll;
            float thumbTop = trackTop + (trackH - thumbH) * progress;
            canvas.drawRoundRect(configViewport.right - 3f * u, trackTop,
                    configViewport.right - 1f * u, trackTop + trackH,
                    1f * u, 1f * u, Ui.fill(0x08FFFFFF));
            canvas.drawRoundRect(configViewport.right - 3f * u, thumbTop,
                    configViewport.right - 1f * u, thumbTop + thumbH,
                    1f * u, 1f * u, Ui.fill(0x22C9B8FF));
        }
    }

    private void drawSettingRow(Canvas canvas, ConfigRow row, float x, float top, int index) {
        ConfigSetting setting = row.setting;
        textPaint.setTypeface(Theme.regular);
        textPaint.setLetterSpacing(0f);
        if (setting.type == ConfigSetting.BOOLEAN) {
            textPaint.set(pKey);
            textPaint.setTextSize(8.2f * u);
            textPaint.setColor(Theme.TEXT_DIM);
            canvas.drawText(setting.name, x + 2f * u, top + 19f * u, textPaint);
            drawMiniToggle(canvas, x + row.toggle.left, top + row.toggle.top, setting.bool,
                    pressedConfigRow == index);
            return;
        }
        if (setting.type == ConfigSetting.NUMBER) {
            textPaint.set(pKey);
            textPaint.setTextSize(8.2f * u);
            textPaint.setColor(Theme.TEXT_DIM);
            canvas.drawText(setting.name, x + 2f * u, top + 14f * u, textPaint);
            textPaint.set(pValue);
            textPaint.setColor(Theme.TEXT);
            Ui.textRight(canvas, setting.numberLabel(), x + row.bounds.width() - 2f * u,
                    top + 14f * u, textPaint);
            float trackLeft = x + row.track.left;
            float trackRight = x + row.track.right;
            float trackY = top + row.track.centerY();
            canvas.drawRoundRect(trackLeft, trackY - 1f * u, trackRight, trackY + 1f * u,
                    1f * u, 1f * u, Ui.fill(0x14FFFFFF));
            float ratio = setting.ratio();
            if (ratio > 0f) {
                Paint fill = Ui.fill(0xFFFFFFFF);
                fill.setShader(new LinearGradient(trackLeft, 0f, trackRight, 0f,
                        Ui.alpha(Theme.LAVENDER, 0.9f), Ui.alpha(Theme.MINT, 0.8f),
                        Shader.TileMode.CLAMP));
                canvas.drawRoundRect(trackLeft, trackY - 1f * u,
                        trackLeft + (trackRight - trackLeft) * ratio, trackY + 1f * u,
                        1f * u, 1f * u, fill);
            }
            float knob = trackLeft + (trackRight - trackLeft) * ratio;
            canvas.drawCircle(knob, trackY, 6.4f * u,
                    Ui.fill(Ui.alpha(Theme.LAVENDER, 0.14f)));
            canvas.drawCircle(knob, trackY, 3.4f * u, Ui.fill(Theme.TEXT));
            return;
        }

        textPaint.set(pKey);
        textPaint.setTextSize(8.2f * u);
        textPaint.setColor(Theme.TEXT_DIM);
        canvas.drawText(setting.name, x + 2f * u, top + 12f * u, textPaint);
        for (ConfigChip chip : row.chips) {
            boolean selected = setting.type == ConfigSetting.MODE
                    ? setting.options[chip.index].equals(setting.mode)
                    : chip.index < setting.selected.length && setting.selected[chip.index];
            boolean chipHover = hoverActive && hoverX >= x + chip.rect.left
                    && hoverX <= x + chip.rect.right
                    && hoverY >= top + chip.rect.top && hoverY <= top + chip.rect.bottom;
            boolean chipPressed = pressedConfigRow == index && pressedConfigOption == chip.index;
            int accent = setting.type == ConfigSetting.MODE ? Theme.LAVENDER : Theme.MINT;
            float l = x + chip.rect.left;
            float t = top + chip.rect.top;
            float r = x + chip.rect.right;
            float b = top + chip.rect.bottom;
            canvas.drawRoundRect(l, t, r, b, 5f * u, 5f * u,
                    Ui.fill(selected ? Ui.alpha(accent, 0.14f)
                            : (chipPressed ? 0x10FFFFFF : (chipHover ? 0x08FFFFFF : 0x03FFFFFF))));
            canvas.drawRoundRect(l, t, r, b, 5f * u, 5f * u,
                    Ui.stroke(selected ? Ui.alpha(accent, 0.50f) : 0x10FFFFFF, 1f));
            textPaint.set(pKey);
            textPaint.setTextSize(6.8f * u);
            textPaint.setColor(selected ? Theme.TEXT : Theme.TEXT_FAINT);
            Ui.textCenter(canvas, setting.options[chip.index],
                    (l + r) * 0.5f, (t + b) * 0.5f + 2.2f * u, textPaint);
        }
    }

    private void drawMiniToggle(Canvas canvas, float left, float top, boolean on,
                                boolean pressed) {
        float width = 30f * u;
        float height = 16f * u;
        RectF r = new RectF(left, top, left + width, top + height);
        canvas.drawRoundRect(r, height * 0.5f, height * 0.5f,
                Ui.fill(on ? Ui.alpha(Theme.LAVENDER, pressed ? 0.32f : 0.24f)
                        : (pressed ? 0x16FFFFFF : 0x0AFFFFFF)));
        canvas.drawRoundRect(r, height * 0.5f, height * 0.5f,
                Ui.stroke(on ? Ui.alpha(Theme.LAVENDER, 0.65f) : 0x18FFFFFF, 1f));
        float cx = on ? r.right - height * 0.5f : r.left + height * 0.5f;
        canvas.drawCircle(cx, r.centerY(), height * 0.31f,
                Ui.fill(on ? Theme.LAVENDER : Theme.TEXT_FAINT));
    }

    private void drawConfigFooter(Canvas canvas, float left) {
        float footerTop = configPanel.bottom - (configModule.settings.isEmpty() ? 34f : 31f) * u;
        Paint footerRule = Ui.stroke(0x0FFFFFFF, 1f);
        canvas.drawLine(left, footerTop, configPanel.right - 24f * u, footerTop, footerRule);
        boolean resetHover = hoverActive && configReset.contains(hoverX, hoverY);
        textPaint.set(pButton);
        textPaint.setColor(resetHover || pressedConfigReset ? Theme.ROSE : Theme.TEXT_DIM);
        canvas.drawText("RESET MODULE", configReset.left,
                configReset.centerY() + 2f * u, textPaint);
        drawConfigButton(canvas, configDone, "DONE", pressedConfigDone);
    }

    private void drawLegacyConfigBody(Canvas canvas, Module m, float left) {
        float bodyTop = configPanel.top + 96f * u;
        float footerTop = configPanel.bottom - 34f * u;
        float rowH = (footerTop - bodyTop) / 5f;
        float controlX = configPanel.left + 100f * u;

        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText("KEYBIND", left, bodyTop + rowH * 0.5f + 2f * u, textPaint);
        String[] keys = {"NONE", "R", "V", "G", "M4"};
        for (int i = 0; i < 5; i++) {
            drawConfigChip(canvas, configKeys[i], keys[i],
                    m.keybind.equals(keys[i]), i == pressedConfigKey);
        }

        canvas.drawText("MODE", left, bodyTop + rowH * 1.5f + 2f * u, textPaint);
        String[] modes = modesFor(m);
        for (int i = 0; i < 3; i++) {
            drawConfigChip(canvas, configModes[i], modes[i],
                    m.mode == i, i == pressedConfigMode);
        }

        drawConfigSlider(canvas, m, 0, bodyTop + rowH * 2.5f, controlX);
        drawConfigSlider(canvas, m, 1, bodyTop + rowH * 3.5f, controlX);

        float accentY = bodyTop + rowH * 4.5f;
        canvas.drawText("ACCENT", left, accentY + 2f * u, textPaint);
        for (int i = 0; i < 4; i++) {
            int color = accentAt(i);
            float cx = configAccents[i].centerX();
            float cy = configAccents[i].centerY();
            if (m.accent == i || i == pressedConfigAccent) {
                canvas.drawCircle(cx, cy, 9f * u, Ui.fill(Ui.alpha(color, 0.13f)));
                canvas.drawCircle(cx, cy, 8f * u, Ui.stroke(Ui.alpha(color, 0.72f), 1f));
            }
            canvas.drawCircle(cx, cy, 4.8f * u, Ui.fill(Ui.alpha(color, 0.9f)));
        }
    }

    private void drawConfigChip(Canvas canvas, RectF r, String label,
                                boolean selected, boolean pressed) {
        boolean hover = hoverActive && r.contains(hoverX, hoverY);
        canvas.drawRoundRect(r, 5f * u, 5f * u,
                Ui.fill(selected ? 0x1AC9B8FF : (pressed ? 0x10FFFFFF : (hover ? 0x08FFFFFF : 0x04FFFFFF))));
        canvas.drawRoundRect(r, 5f * u, 5f * u,
                Ui.stroke(selected ? Ui.alpha(Theme.LAVENDER, 0.44f) : 0x12FFFFFF, 1f));
        textPaint.set(pKey);
        textPaint.setColor(selected ? Theme.TEXT : Theme.TEXT_DIM);
        Ui.textCenter(canvas, label, r.centerX(), r.centerY() + 2f * u, textPaint);
    }

    private void drawConfigButton(Canvas canvas, RectF r, String label, boolean pressed) {
        boolean hover = hoverActive && r.contains(hoverX, hoverY);
        canvas.drawRoundRect(r, 7f * u, 7f * u,
                Ui.fill(pressed ? 0x12FFFFFF : (hover ? 0x0AFFFFFF : 0x06FFFFFF)));
        canvas.drawRoundRect(r, 7f * u, 7f * u,
                Ui.stroke(hover || pressed ? Ui.alpha(Theme.LAVENDER, 0.40f) : 0x1AFFFFFF, 1f));
        textPaint.set(pButton);
        textPaint.setColor(hover || pressed ? Theme.TEXT : Theme.TEXT_DIM);
        Ui.textCenter(canvas, label, r.centerX(), r.centerY() + 2f * u, textPaint);
    }

    private void drawConfigSlider(Canvas canvas, Module m, int index, float centerY, float left) {
        RectF track = configSliderTracks[index];
        float right = track.right;
        float ratio = configSliderRatio(m, index);
        String label = index == 0 ? sliderLabel(m) : "OPACITY";
        textPaint.set(pKey);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText(label, configPanel.left + 24f * u, centerY + 2f * u, textPaint);

        canvas.drawRoundRect(left, centerY - 1f * u, right, centerY + 1f * u,
                1f * u, 1f * u, Ui.fill(0x14FFFFFF));
        if (ratio > 0f) {
            Paint fill = Ui.fill(0xFFFFFFFF);
            fill.setShader(new LinearGradient(left, 0f, right, 0f,
                    Ui.alpha(Theme.LAVENDER, 0.9f), Ui.alpha(Theme.MINT, 0.8f),
                    Shader.TileMode.CLAMP));
            canvas.drawRoundRect(left, centerY - 1f * u, left + (right - left) * ratio,
                    centerY + 1f * u, 1f * u, 1f * u, fill);
        }
        float tx = left + (right - left) * ratio;
        canvas.drawCircle(tx, centerY, 7f * u, Ui.fill(Ui.alpha(Theme.LAVENDER, 0.13f)));
        canvas.drawCircle(tx, centerY, 4.2f * u, Ui.fill(Theme.TEXT));
        canvas.drawCircle(tx, centerY, 1.6f * u, Ui.fill(Theme.LAVENDER));

        textPaint.set(pValue);
        textPaint.setColor(Theme.TEXT);
        String value = configSliderLabel(m, index);
        Ui.textRight(canvas, value, configPanel.right - 24f * u, centerY + 2.3f * u, textPaint);
    }

    private String[] modesFor(Module m) {
        if (m.id.equals("hud.watermark")) {
            return new String[]{"MINIMAL", "GLASS", "BOLD"};
        }
        if (m.id.equals("hud.arraylist")) {
            return new String[]{"LENGTH", "A-Z", "CATEGORY"};
        }
        if (m.id.equals("hud.notifications")) {
            return new String[]{"STACK", "MINIMAL", "COMPACT"};
        }
        if (m.id.equals("hud.targethud")) {
            return new String[]{"LINE", "GLASS", "BOLD"};
        }
        if (m.id.equals("hud.keystrokes")) {
            return new String[]{"WASD", "BAR", "MINIMAL"};
        }
        if (m.id.equals("hud.coordinates")) {
            return new String[]{"XYZ", "MINIMAL", "DETAILED"};
        }
        switch (tab) {
            case COMBAT:
            case MOVEMENT:
                return new String[]{"SILENT", "SMOOTH", "LEGIT"};
            case RENDER:
                return new String[]{"SOFT", "BALANCED", "VIVID"};
            case PLAYER:
                return new String[]{"LOCAL", "SYNC", "MANUAL"};
            case WORLD:
            case MISC:
            case GHOST:
                return new String[]{"NORMAL", "SMOOTH", "INSTANT"};
            case CONFIG:
                return new String[]{"DEFAULT", "RANKED", "VANILLA"};
            default:
                return new String[]{"NORMAL", "SMOOTH", "INSTANT"};
        }
    }

    private String sliderLabel(Module m) {
        if (m.hasSlider) {
            return "VALUE";
        }
        if (m.id.equals("hud.watermark")) {
            return "SCALE";
        }
        if (m.id.equals("hud.arraylist")) {
            return "WIDTH";
        }
        if (m.id.equals("hud.notifications")) {
            return "DURATION";
        }
        if (m.id.equals("hud.targethud") || m.id.equals("hud.keystrokes")) {
            return "SCALE";
        }
        if (m.id.equals("hud.coordinates")) {
            return "OFFSET";
        }
        return "RANGE";
    }

    private float configSliderRatio(Module m, int index) {
        if (index == 0 && m.hasSlider) {
            return m.ratio();
        }
        return index == 0 ? m.configA : m.configB;
    }

    private String configSliderLabel(Module m, int index) {
        if (index == 0 && m.hasSlider) {
            return m.valueLabel();
        }
        return Math.round(configSliderRatio(m, index) * 100f) + "%";
    }

    private int accentAt(int index) {
        switch (index) {
            case 1:
                return Theme.MINT;
            case 2:
                return Theme.ROSE;
            case 3:
                return Theme.TEXT;
            default:
                return Theme.LAVENDER;
        }
    }

    private void configDown(float x, float y) {
        clearConfigPresses();
        configSlider = -1;
        configNumberRow = -1;
        configDragging = false;
        configGesture = false;
        if (!configPanel.contains(x, y)) {
            closeConfig();
            return;
        }
        if (configClose.contains(x, y)) {
            pressedConfigClose = true;
            return;
        }
        if (configToggle.contains(x, y)) {
            pressedConfigToggle = true;
            return;
        }
        if (configReset.contains(x, y)) {
            pressedConfigReset = true;
            return;
        }
        if (configDone.contains(x, y)) {
            pressedConfigDone = true;
            return;
        }
        if (configModule == null || configModule.settings.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                if (configKeys[i].contains(x, y)) {
                    pressedConfigKey = i;
                    return;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (configModes[i].contains(x, y)) {
                    pressedConfigMode = i;
                    return;
                }
            }
            for (int i = 0; i < 4; i++) {
                if (configAccents[i].contains(x, y)) {
                    pressedConfigAccent = i;
                    return;
                }
            }
            for (int i = 0; i < 2; i++) {
                if (configSliderTracks[i].contains(x, y)) {
                    configSlider = i;
                    updateConfigSlider(x);
                    return;
                }
            }
            return;
        }

        if (!configViewport.contains(x, y)) {
            return;
        }
        configDownY = y;
        configScrollStart = configScroll;
        configGesture = true;
        float contentY = y - configViewport.top + configScroll;
        int index = settingRowAt(contentY);
        if (index < 0) {
            return;
        }
        ConfigRow row = configRows.get(index);
        if (row.setting.type == ConfigSetting.NUMBER) {
            float trackTop = configViewport.top + row.track.top - configScroll;
            if (y >= trackTop - 9f * u && y <= trackTop + row.track.height() + 9f * u) {
                configNumberRow = index;
                updateConfigNumber(x);
                return;
            }
        }
        float contentX = x - configViewport.left;
        for (ConfigChip chip : row.chips) {
            if (chip.rect.contains(contentX, contentY)) {
                pressedConfigRow = index;
                pressedConfigOption = chip.index;
                return;
            }
        }
        pressedConfigRow = index;
    }

    private void configMove(float x, float y) {
        if (configModule == null) {
            return;
        }
        if (!configModule.settings.isEmpty()) {
            if (configNumberRow >= 0) {
                updateConfigNumber(x);
                return;
            }
            if (!configGesture) {
                return;
            }
            float dy = y - configDownY;
            if (!configDragging && Math.abs(dy) > touchSlop) {
                configDragging = true;
            }
            if (configDragging) {
                configScroll = Ui.clamp(configScrollStart - dy, 0f, configMaxScroll);
                invalidate();
            }
            return;
        }
        if (configSlider >= 0) {
            updateConfigSlider(x);
        }
    }

    private void configUp(float x, float y) {
        Module m = configModule;
        if (m == null) {
            return;
        }
        if (!m.settings.isEmpty()) {
            if (configNumberRow >= 0) {
                prefs.saveSettings(m);
                configNumberRow = -1;
                clearConfigPresses();
                invalidate();
                return;
            }
            if (configGesture) {
                if (!configDragging && pressedConfigRow >= 0
                        && pressedConfigRow < configRows.size()) {
                    ConfigRow row = configRows.get(pressedConfigRow);
                    float contentY = y - configViewport.top + configScroll;
                    float contentX = x - configViewport.left;
                    boolean onRow = contentY >= row.bounds.top && contentY <= row.bounds.bottom;
                    if (pressedConfigOption >= 0) {
                        for (ConfigChip chip : row.chips) {
                            if (chip.index == pressedConfigOption
                                    && chip.rect.contains(contentX, contentY)) {
                                if (row.setting.type == ConfigSetting.MODE) {
                                    row.setting.mode = row.setting.options[chip.index];
                                } else if (chip.index < row.setting.selected.length) {
                                    row.setting.selected[chip.index] = !row.setting.selected[chip.index];
                                }
                                prefs.saveSettings(m);
                                break;
                            }
                        }
                    } else if (onRow) {
                        if (row.setting.type == ConfigSetting.BOOLEAN) {
                            row.setting.toggleBool();
                            prefs.saveSettings(m);
                        } else if (row.setting.type == ConfigSetting.MODE) {
                            row.setting.cycleMode();
                            prefs.saveSettings(m);
                        }
                    }
                }
                configGesture = false;
                configDragging = false;
                clearConfigPresses();
                invalidate();
                return;
            }
        }

        if (configSlider >= 0) {
            prefs.saveConfig(m);
            if (m.hasSlider) {
                prefs.saveValue(m);
            }
            configSlider = -1;
        }
        if (pressedConfigClose && configClose.contains(x, y)) {
            closeConfig();
            return;
        }
        if (pressedConfigToggle && configToggle.contains(x, y)) {
            activate(m);
        } else if (pressedConfigReset && configReset.contains(x, y)) {
            resetConfig(m);
        } else if (pressedConfigKey >= 0 && configKeys[pressedConfigKey].contains(x, y)) {
            String[] keys = {"NONE", "R", "V", "G", "M4"};
            m.keybind = keys[pressedConfigKey];
            prefs.saveConfig(m);
            showToast("Keybind set to " + m.keybind);
        } else if (pressedConfigMode >= 0 && configModes[pressedConfigMode].contains(x, y)) {
            m.mode = pressedConfigMode;
            prefs.saveConfig(m);
        } else if (pressedConfigAccent >= 0 && configAccents[pressedConfigAccent].contains(x, y)) {
            m.accent = pressedConfigAccent;
            prefs.saveConfig(m);
        } else if (pressedConfigDone && configDone.contains(x, y)) {
            closeConfig();
        }
        clearConfigPresses();
        invalidate();
    }

    private int settingRowAt(float contentY) {
        for (int i = 0; i < configRows.size(); i++) {
            RectF bounds = configRows.get(i).bounds;
            if (contentY >= bounds.top && contentY <= bounds.bottom) {
                return i;
            }
        }
        return -1;
    }

    private void updateConfigNumber(float x) {
        if (configModule == null || configNumberRow < 0
                || configNumberRow >= configRows.size()) {
            return;
        }
        ConfigRow row = configRows.get(configNumberRow);
        float width = row.track.width();
        if (width <= 1f) {
            return;
        }
        float ratio = Ui.clamp((x - (configViewport.left + row.track.left)) / width, 0f, 1f);
        row.setting.setFromRatio(ratio);
        invalidate();
    }

    private void updateConfigSlider(float x) {
        if (configModule == null || configSlider < 0 || configSlider > 1) {
            return;
        }
        RectF track = configSliderTracks[configSlider];
        float width = track.width();
        if (width <= 1f) {
            return;
        }
        float ratio = Ui.clamp((x - track.left) / width, 0f, 1f);
        Module m = configModule;
        if (configSlider == 0 && m.hasSlider) {
            m.setFromRatio(ratio);
        } else if (configSlider == 0) {
            m.configA = ratio;
        } else {
            m.configB = ratio;
        }
        invalidate();
    }

    private void resetConfig(Module m) {
        for (ConfigSetting setting : m.settings) {
            setting.reset();
        }
        prefs.saveSettings(m);
        m.keybind = "NONE";
        m.mode = 0;
        m.accent = 0;
        m.configA = 0.5f;
        m.configB = 0.65f;
        if (m.hasSlider) {
            m.setFromRatio(0.5f);
            prefs.saveValue(m);
        }
        prefs.saveConfig(m);
        showToast(m.name + " reset");
        hud.push("Module reset", m.name + " returned to defaults", Theme.TEXT_DIM);
    }

    private void clearConfigPresses() {
        pressedConfigClose = false;
        pressedConfigToggle = false;
        pressedConfigKey = -1;
        pressedConfigMode = -1;
        pressedConfigAccent = -1;
        pressedConfigReset = false;
        pressedConfigDone = false;
        pressedConfigRow = -1;
        pressedConfigOption = -1;
    }

    private boolean toggleHit(Card card, float x, float y) {
        if (card == null) {
            return false;
        }
        float top = card.rect.top - scrollY;
        float right = card.rect.right - 14f * u;
        return x >= right - 46f * u && x <= right - 8f * u
                && y >= top + 9f * u && y <= top + 35f * u;
    }

    private void drawLaunch(Canvas canvas, float alpha) {
        if (alpha <= 0.002f) {
            return;
        }
        int a = Math.round(255f * alpha);
        canvas.saveLayerAlpha(null, a);
        for (int i = 4; i >= 1; i--) {
            canvas.drawRoundRect(launchRect.left - i * 4f * u, launchRect.top - i * 4f * u,
                    launchRect.right + i * 4f * u, launchRect.bottom + i * 4f * u,
                    21f * u + i * 4f * u, 21f * u + i * 4f * u,
                    Ui.fill(Ui.alpha(Theme.LAVENDER, 0.022f / i)));
        }
        shape.setShader(new LinearGradient(launchRect.left, launchRect.top,
                launchRect.right, launchRect.bottom, 0xF80D0F16, 0xF8080A0F,
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(launchRect, 21f * u, 21f * u, shape);
        shape.setShader(null);
        Paint border = Ui.stroke(0xFFFFFFFF, 1f);
        border.setShader(new LinearGradient(launchRect.left, launchRect.top,
                launchRect.right, launchRect.bottom,
                new int[]{Ui.alpha(Theme.LAVENDER, 0.45f), 0x14FFFFFF, Ui.alpha(Theme.MINT, 0.32f)},
                null, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(launchRect, 21f * u, 21f * u, border);

        drawLogo(canvas, launchRect.left + 25f * u, launchRect.centerY(), 21f * u);
        textPaint.set(pLaunchTitle);
        textPaint.setColor(Theme.TEXT);
        canvas.drawText("AETHER", launchRect.left + 45f * u, launchRect.centerY() - 1f * u, textPaint);
        textPaint.set(pLaunchSub);
        textPaint.setColor(Theme.TEXT_FAINT);
        canvas.drawText("TAP TO OPEN", launchRect.left + 45f * u, launchRect.centerY() + 12f * u, textPaint);
        Ui.glowDot(canvas, launchRect.right - 18f * u, launchRect.centerY(),
                2.1f * u, Theme.MINT);
        canvas.restore();
    }

    private void drawToast(Canvas canvas, long now) {
        float in = Ui.clamp((now - toastStart) / 160f, 0f, 1f);
        float out = Ui.clamp((toastUntil + 240L - now) / 240f, 0f, 1f);
        float alpha = Math.min(Ui.easeOutCubic(in), Ui.easeOutCubic(out));
        if (alpha <= 0.01f) {
            return;
        }
        textPaint.set(pToast);
        textPaint.setColor(Theme.TEXT);
        float tw = textPaint.measureText(toastText) + 30f * u;
        float cx = (contentLeft + contentRight) * 0.5f;
        float cy = footerLineY - 17f * u;
        RectF r = new RectF(cx - tw * 0.5f, cy - 13f * u, cx + tw * 0.5f, cy + 13f * u);
        int a = Math.round(alpha * 255f);
        canvas.saveLayerAlpha(null, a);
        canvas.drawRoundRect(r, 13f * u, 13f * u, Ui.fill(0xF20D0F15));
        Paint border = Ui.stroke(0xFFFFFFFF, 1f);
        border.setShader(new LinearGradient(r.left, r.top, r.right, r.bottom,
                Ui.alpha(Theme.LAVENDER, 0.32f), Ui.alpha(Theme.MINT, 0.22f),
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(r, 13f * u, 13f * u, border);
        Ui.textCenter(canvas, toastText, r.centerX(), r.centerY() + 2.3f * u, textPaint);
        canvas.restore();
    }

    private void showToast(String message) {
        toastText = message;
        toastStart = SystemClock.uptimeMillis();
        toastUntil = toastStart + TOAST_MS;
        postInvalidateOnAnimation();
    }

    private void setOpen(boolean value) {
        if (open == value) {
            return;
        }
        open = value;
        prefs.setMenuOpen(value);
        if (open) {
            pageAnim = 0f;
            closePending = false;
            lastFrameAt = SystemClock.uptimeMillis();
        } else {
            publish("ClickGUI hidden", "HUD overlay active · local session", Theme.LAVENDER);
        }
        postInvalidateOnAnimation();
    }

    private void requestClose() {
        if (!open) {
            return;
        }
        setOpen(false);
        if (overlay && closeListener != null && !closePending) {
            closePending = true;
            postDelayed(() -> {
                closePending = false;
                if (closeListener != null) {
                    closeListener.run();
                }
            }, 190L);
        }
    }

    private void publish(String title, String detail, int accent) {
        hud.push(title, detail, accent);
        if (moduleEventListener != null) {
            moduleEventListener.onModuleEvent(title, detail, accent);
        }
    }

    private int profileIndex(String id) {
        if ("config.default".equals(id)) {
            return 0;
        }
        if ("config.ranked".equals(id)) {
            return 1;
        }
        if ("config.vanilla".equals(id)) {
            return 2;
        }
        return -1;
    }

    private void activate(Module m) {
        if (m.isAction()) {
            int index = profileIndex(m.id);
            if (index >= 0) {
                activeProfile = index;
                showToast(m.name + " profile loaded");
            } else if ("config.export".equals(m.id)) {
                exportProfile();
            } else if ("config.reset".equals(m.id)) {
                prefs.reset();
                activeProfile = 0;
                showToast("Interface restored");
            }
            invalidate();
            return;
        }
        m.enabled = !m.enabled;
        prefs.save(m);
        publish(m.name + (m.enabled ? " enabled" : " disabled"),
                "Local preview · " + (m.enabled ? "active" : "inactive"),
                m.enabled ? Theme.MINT : Theme.TEXT_FAINT);
        showToast(m.name + (m.enabled ? " enabled" : " disabled"));
        invalidate();
    }

    private void exportProfile() {
        ClipboardManager clipboard = (ClipboardManager) getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            showToast("Clipboard unavailable");
            return;
        }
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"client\": \"AETHER\",\n  \"profile\": \"")
                .append(activeProfile == 0 ? "Default" : activeProfile == 1 ? "Ranked" : "Vanilla")
                .append("\",\n  \"modules\": [\n");
        List<Module> all = Content.all();
        for (int i = 0; i < all.size(); i++) {
            Module m = all.get(i);
            json.append("    {\"id\":\"").append(m.id)
                    .append("\",\"enabled\":").append(m.enabled)
                    .append(",\"value\":").append(String.format(Locale.US, "%.2f", m.value))
                    .append("}");
            if (i < all.size() - 1) {
                json.append(',');
            }
            json.append('\n');
        }
        json.append("  ]\n}\n");
        clipboard.setPrimaryClip(ClipData.newPlainText("AETHER profile", json.toString()));
        showToast("Profile copied to clipboard");
    }

    private void saveProfile() {
        for (Module m : Content.all()) {
            prefs.save(m);
            prefs.saveConfig(m);
            prefs.saveSettings(m);
        }
        hud.push("Profile saved", "All module settings stored locally", Theme.MINT);
        showToast("Configuration saved locally");
    }

    private void clearPresses() {
        pressedClose = false;
        pressedSave = false;
        pressedLaunch = false;
        pressedCapsule = false;
        pressedTab = -1;
        pressedChip = -1;
        pressedModule = null;
        pressedCard = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                downX = x;
                downY = y;
                dragging = false;
                clearPresses();
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                }
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);
                if (configModule != null) {
                    configDown(x, y);
                    invalidate();
                    return true;
                }
                if (!open) {
                    pressedLaunch = launchRect.contains(x, y);
                    return true;
                }
                downScroll = scrollY;
                scroller.forceFinished(true);
                if (closeRect.contains(x, y)) {
                    pressedClose = true;
                } else if (capsuleRect.contains(x, y)) {
                    pressedCapsule = true;
                } else if (saveRect.contains(x, y)) {
                    pressedSave = true;
                } else {
                    for (int i = 0; i < TABS.length; i++) {
                        if (tabRects[i] != null && tabRects[i].contains(x, y)) {
                            pressedTab = i;
                            break;
                        }
                    }
                    if (pressedTab < 0) {
                        for (int i = 0; i < 3; i++) {
                            if (tab != Tab.CONFIG && chipRects[i].contains(x, y)) {
                                pressedChip = i;
                                break;
                            }
                        }
                    }
                    if (pressedTab < 0 && pressedChip < 0 && cardsViewport.contains(x, y)) {
                        Card card = cardAt(x, y);
                        if (card != null) {
                            if (card.module.hasSlider && sliderHit(card, x, y)) {
                                activeSlider = card.module;
                                activeSliderCard = card.rect;
                                activeSliderTrackLeft = card.rect.left + 14f * u;
                                activeSliderTrackWidth = card.rect.width() - 74f * u;
                                updateSlider(x);
                            } else {
                                pressedModule = card.module;
                                pressedCard = card;
                            }
                        }
                    }
                }
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                if (configModule != null) {
                    configMove(x, y);
                    return true;
                }
                if (!open) {
                    return true;
                }
                if (activeSlider != null) {
                    updateSlider(x);
                    return true;
                }
                float dx = x - downX;
                float dy = y - downY;
                if (!dragging && Math.abs(dy) > touchSlop
                        && cardsViewport.contains(downX, downY)) {
                    dragging = true;
                    clearPresses();
                }
                if (dragging) {
                    scrollY = Ui.clamp(downScroll - dy, 0f, maxScroll);
                    lastScrollAt = SystemClock.uptimeMillis();
                    invalidate();
                } else if (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop) {
                    clearPresses();
                    invalidate();
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                if (configModule != null) {
                    configUp(x, y);
                    recycleVelocity();
                    return true;
                }
                if (!open) {
                    if (pressedLaunch && launchRect.contains(x, y)) {
                        setOpen(true);
                    }
                    clearPresses();
                    recycleVelocity();
                    return true;
                }
                if (activeSlider != null) {
                    prefs.saveValue(activeSlider);
                    activeSlider = null;
                    activeSliderCard = null;
                    invalidate();
                } else if (dragging) {
                    flingFromVelocity();
                } else {
                    performClick();
                    if (pressedClose && closeRect.contains(x, y)) {
                        requestClose();
                    } else if (pressedCapsule && capsuleRect.contains(x, y)) {
                        if (overlayAction != null) {
                            overlayAction.run();
                        }
                    } else if (pressedSave && saveRect.contains(x, y)) {
                        if (tab == Tab.HUD) {
                            requestClose();
                        } else {
                            saveProfile();
                        }
                    } else if (pressedTab >= 0 && tabRects[pressedTab].contains(x, y)) {
                        selectTab(pressedTab);
                    } else if (pressedChip >= 0 && chipRects[pressedChip].contains(x, y)) {
                        filter = pressedChip;
                        scrollY = 0f;
                        buildCards();
                        invalidate();
                    } else if (pressedModule != null) {
                        if (pressedModule.isAction()
                                || (pressedCard != null && toggleHit(pressedCard, x, y))) {
                            activate(pressedModule);
                        } else {
                            openConfig(pressedModule);
                        }
                    }
                }
                clearPresses();
                recycleVelocity();
                return true;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (configModule != null) {
                    configSlider = -1;
                    configNumberRow = -1;
                    configGesture = false;
                    configDragging = false;
                    clearConfigPresses();
                }
                if (activeSlider != null) {
                    prefs.saveValue(activeSlider);
                    activeSlider = null;
                }
                dragging = false;
                clearPresses();
                recycleVelocity();
                invalidate();
                return true;
            }
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void updateSlider(float x) {
        if (activeSlider == null || activeSliderTrackWidth <= 1f) {
            return;
        }
        float ratio = (x - activeSliderTrackLeft) / activeSliderTrackWidth;
        activeSlider.setFromRatio(ratio);
        lastScrollAt = 0L;
        invalidate();
    }

    private boolean sliderHit(Card card, float x, float y) {
        float top = card.rect.top - scrollY;
        float localY = y;
        float trackY = top + 59f * u;
        return x >= card.rect.left + 8f * u && x <= card.rect.right - 8f * u
                && localY >= trackY - 11f * u && localY <= trackY + 11f * u;
    }

    private Card cardAt(float x, float y) {
        float adjusted = y + scrollY;
        for (Card card : cards) {
            if (x >= card.rect.left && x <= card.rect.right
                    && adjusted >= card.rect.top && adjusted <= card.rect.bottom) {
                return card;
            }
        }
        return null;
    }

    private void flingFromVelocity() {
        if (velocityTracker == null) {
            return;
        }
        velocityTracker.computeCurrentVelocity(1000);
        int vy = (int) velocityTracker.getYVelocity();
        if (Math.abs(vy) < 60) {
            return;
        }
        scroller.fling(0, (int) scrollY, 0, -vy, 0, 0, 0, (int) maxScroll);
        lastScrollAt = SystemClock.uptimeMillis();
        postInvalidateOnAnimation();
    }

    private void recycleVelocity() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollY = scroller.getCurrY();
            lastScrollAt = SystemClock.uptimeMillis();
            postInvalidateOnAnimation();
        }
    }

    private void selectTab(int index) {
        if (index < 0 || index >= TABS.length || index == tabIndex) {
            return;
        }
        closeConfig();
        tabIndex = index;
        tab = TABS[index];
        filter = 0;
        scrollY = 0f;
        pageAnim = 0f;
        scroller.forceFinished(true);
        buildCards();
        prefs.setActiveTab(index);
        invalidate();
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_HOVER_EXIT) {
            hoverActive = false;
        } else {
            hoverActive = true;
            hoverX = event.getX();
            hoverY = event.getY();
        }
        invalidate();
        return true;
    }

    private static String two(int value) {
        return String.format(Locale.US, "%02d", value);
    }

    private static final class Card {
        final Module module;
        final RectF rect;

        Card(Module module, RectF rect) {
            this.module = module;
            this.rect = rect;
        }
    }

    private static final class ConfigRow {
        final ConfigSetting setting;
        final RectF bounds = new RectF();
        final RectF track = new RectF();
        final RectF toggle = new RectF();
        final List<ConfigChip> chips = new ArrayList<>();

        ConfigRow(ConfigSetting setting) {
            this.setting = setting;
        }
    }

    private static final class ConfigChip {
        final int index;
        final RectF rect = new RectF();

        ConfigChip(int index) {
            this.index = index;
        }
    }
}
