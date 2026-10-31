package com.aether.pvp.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/** GPU backdrop. A single full-screen triangle pair, no textures, no dependencies. */
public final class AuroraView extends GLSurfaceView {
    private final AuroraRenderer renderer = new AuroraRenderer();

    public AuroraView(Context context) {
        this(context, null);
    }

    public AuroraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setZOrderOnTop(false);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void setDimmed(boolean dimmed) {
        renderer.setDimmed(dimmed);
    }
}
