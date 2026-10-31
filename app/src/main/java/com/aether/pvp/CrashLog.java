package com.aether.pvp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Writes startup failures somewhere the user (and I) can read without adb. */
public final class CrashLog {
    private static final String TAG = "AetherCrash";
    private static final String NAME = "AETHER-crash.txt";

    private CrashLog() {
    }

    public static void write(Context context, Throwable error) {
        String text = report(error);
        Log.e(TAG, text);
        if (context == null) {
            return;
        }
        writePrivate(context, text);
        writeDownloads(context, text);
    }

    private static String report(Throwable error) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        out.println("AETHER crash report");
        out.println("time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(new Date()));
        out.println("sdk: " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")");
        out.println("device: " + Build.MANUFACTURER + " " + Build.MODEL);
        out.println("thread: " + Thread.currentThread().getName());
        out.println();
        error.printStackTrace(out);
        out.flush();
        return buffer.toString();
    }

    private static void writePrivate(Context context, String text) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            dir = context.getFilesDir();
        }
        if (dir == null) {
            return;
        }
        File file = new File(dir, NAME);
        try (FileOutputStream out = new FileOutputStream(file, true)) {
            out.write(text.getBytes("UTF-8"));
            out.write('\n');
        } catch (Throwable ignored) {
        }
    }

    private static void writeDownloads(Context context, String text) {
        if (Build.VERSION.SDK_INT < 29) {
            return;
        }
        Uri uri = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, NAME);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/AETHER");
            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                return;
            }
            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (out != null) {
                    out.write(text.getBytes("UTF-8"));
                    out.write('\n');
                }
            }
        } catch (Throwable ignored) {
            if (uri != null) {
                try {
                    context.getContentResolver().delete(uri, null, null);
                } catch (Throwable ignored2) {
                }
            }
        }
    }
}
