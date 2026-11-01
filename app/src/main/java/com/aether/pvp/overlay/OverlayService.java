package com.aether.pvp.overlay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Insets;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import com.aether.pvp.MainActivity;
import com.aether.pvp.data.Prefs;
import com.aether.pvp.ui.MenuView;
import com.aether.pvp.ui.Theme;

/**
 * Foreground service that owns the two floating windows:
 *
 * <ul>
 *   <li>a touch-through HUD layer, and</li>
 *   <li>a focusable ClickGUI layer, opened from the draggable AETHER orb.</li>
 * </ul>
 *
 * <p>Only AETHER's own mock UI is drawn. The service has no access to another
 * app's content, input, or memory; Android's overlay permission is the only
 * cross-app capability used here.
 */
public final class OverlayService extends Service {
    public static final String ACTION_START = "com.aether.pvp.overlay.START";
    public static final String ACTION_TOGGLE_GUI = "com.aether.pvp.overlay.TOGGLE_GUI";
    public static final String ACTION_STOP = "com.aether.pvp.overlay.STOP";

    private static final String CHANNEL = "aether_overlay";
    private static final int NOTIFICATION_ID = 0xAE71;
    private static final int REQUEST_TOGGLE = 11;
    private static final int REQUEST_STOP = 12;

    private static volatile boolean running;

    private WindowManager wm;
    private Prefs prefs;
    private float density;

    private OverlayHudView hudView;
    private LauncherView launcherView;
    private MenuView menuView;
    private WindowManager.LayoutParams hudParams;
    private WindowManager.LayoutParams launcherParams;
    private WindowManager.LayoutParams menuParams;
    private int launcherSize;
    private boolean foreground;

    public static boolean isRunning() {
        return running;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, OverlayService.class).setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, OverlayService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        prefs = new Prefs(this);
        prefs.load();
        Theme.load(getResources());
        density = getResources().getDisplayMetrics().density;
        launcherSize = Math.round(58f * density);
        createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_START : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        beginForeground();
        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        try {
            if (hudView == null) {
                addHud();
            }
            if (launcherView == null) {
                addLauncher();
            }
            running = true;
            if (ACTION_TOGGLE_GUI.equals(action)) {
                if (menuView == null) {
                    openGui();
                } else {
                    closeGui();
                }
            }
        } catch (Throwable error) {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        remove(menuView);
        remove(hudView);
        remove(launcherView);
        menuView = null;
        hudView = null;
        launcherView = null;
        super.onDestroy();
    }

    private void beginForeground() {
        if (foreground) {
            return;
        }
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        foreground = true;
    }

    private void addHud() {
        hudView = new OverlayHudView(this);
        hudParams = baseParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        applyInsets(hudView);
        wm.addView(hudView, hudParams);
    }

    private void addLauncher() {
        launcherView = new LauncherView(this);
        launcherParams = baseParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        int[] size = screenSize();
        int x = prefs.launcherX();
        int y = prefs.launcherY();
        if (x < 0 || y < 0) {
            x = Math.max(0, size[0] - launcherSize - Math.round(14f * density));
            y = Math.max(0, Math.round(size[1] * 0.32f));
        }
        launcherParams.x = clamp(x, 0, Math.max(0, size[0] - launcherSize));
        launcherParams.y = clamp(y, 0, Math.max(0, size[1] - launcherSize));
        launcherView.setWindowPosition(launcherParams.x, launcherParams.y);
        launcherView.setListener(new LauncherView.Listener() {
            @Override
            public void onTap() {
                openGui();
            }

            @Override
            public void onMove(float rawX, float rawY) {
                moveLauncher(rawX, rawY);
            }

            @Override
            public void onRelease() {
                if (launcherParams != null) {
                    prefs.setLauncherPosition(launcherParams.x, launcherParams.y);
                }
            }
        });
        wm.addView(launcherView, launcherParams);
    }

    private void openGui() {
        if (menuView != null) {
            menuView.openMenu();
            return;
        }
        menuView = new MenuView(this, prefs, true);
        menuView.setOnCloseListener(this::closeGui);
        menuView.setOnOverlayAction(this::stopSelf);
        menuView.setOnModuleEventListener((title, detail, accent) -> {
            if (hudView != null) {
                hudView.push(title, detail, accent);
            }
        });
        menuView.setOverlayActive(true);
        applyInsets(menuView);
        menuParams = baseParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        menuParams.gravity = Gravity.TOP | Gravity.START;
        try {
            wm.addView(menuView, menuParams);
        } catch (Throwable error) {
            menuView = null;
            return;
        }
        menuView.requestFocus();
        if (launcherView != null) {
            launcherView.setVisibility(LauncherView.GONE);
        }
    }

    private void closeGui() {
        if (menuView == null) {
            return;
        }
        MenuView closing = menuView;
        menuView = null;
        remove(closing);
        if (launcherView != null) {
            launcherView.setVisibility(LauncherView.VISIBLE);
        }
    }

    private void moveLauncher(float rawX, float rawY) {
        if (launcherView == null || launcherParams == null) {
            return;
        }
        int[] size = screenSize();
        int x = clamp(Math.round(rawX), 0, Math.max(0, size[0] - launcherSize));
        int y = clamp(Math.round(rawY), 0, Math.max(0, size[1] - launcherSize));
        launcherParams.x = x;
        launcherParams.y = y;
        try {
            wm.updateViewLayout(launcherView, launcherParams);
            launcherView.setWindowPosition(x, y);
        } catch (Throwable ignored) {
        }
    }

    private WindowManager.LayoutParams baseParams(int width, int height, int flags) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width, height,
                Build.VERSION.SDK_INT >= 26
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                flags,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.alpha = 1f;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        return params;
    }

    private void applyInsets(android.view.View view) {
        if (view == null) {
            return;
        }
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                WindowMetrics metrics = wm.getCurrentWindowMetrics();
                Insets cutout = metrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.displayCutout());
                left = cutout.left;
                top = cutout.top;
                right = cutout.right;
                bottom = cutout.bottom;
            }
        } catch (Throwable ignored) {
        }
        if (view instanceof OverlayHudView) {
            ((OverlayHudView) view).setSafeInsets(left, top, right, bottom);
        } else if (view instanceof MenuView) {
            ((MenuView) view).setSafeInsets(left, top, right, bottom);
        }
    }

    private int[] screenSize() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                Rect bounds = wm.getCurrentWindowMetrics().getBounds();
                return new int[]{bounds.width(), bounds.height()};
            }
        } catch (Throwable ignored) {
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    private void remove(android.view.View view) {
        if (view == null) {
            return;
        }
        try {
            wm.removeView(view);
        } catch (Throwable ignored) {
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void createChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(CHANNEL, "AETHER overlay",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Floating ClickGUI and HUD controls");
        channel.setShowBadge(false);
        nm.createNotificationChannel(channel);
    }

    private Notification buildNotification() {
        Intent open = new Intent(this, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent content = PendingIntent.getActivity(this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent toggle = PendingIntent.getService(this, REQUEST_TOGGLE,
                new Intent(this, OverlayService.class).setAction(ACTION_TOGGLE_GUI),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent stop = PendingIntent.getService(this, REQUEST_STOP,
                new Intent(this, OverlayService.class).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = new Notification.Builder(this, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .setContentTitle("AETHER overlay")
                .setContentText("ClickGUI and HUD are floating above other apps")
                .setContentIntent(content)
                .setOngoing(true)
                .addAction(0, "GUI", toggle)
                .addAction(0, "STOP", stop);
        if (Build.VERSION.SDK_INT >= 31) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        return builder.build();
    }
}
