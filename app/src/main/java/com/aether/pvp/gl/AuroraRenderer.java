package com.aether.pvp.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A slow aurora veil: layered value noise, a whisper of dust and a vignette.
 * Everything is procedural, so the APK carries no background photography.
 */
public final class AuroraRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AetherGL";

    private static final String VERTEX =
            "attribute vec4 a_position;\n" +
            "void main() {\n" +
            "    gl_Position = a_position;\n" +
            "}\n";

    private static final String FRAGMENT =
            "precision highp float;\n" +
            "uniform vec2 u_res;\n" +
            "uniform float u_time;\n" +
            "uniform float u_dim;\n" +
            "float hash21(vec2 p) {\n" +
            "    p = fract(p * vec2(123.34, 456.21));\n" +
            "    p += dot(p, p + 45.32);\n" +
            "    return fract(p.x * p.y);\n" +
            "}\n" +
            "float noise(vec2 p) {\n" +
            "    vec2 i = floor(p);\n" +
            "    vec2 f = fract(p);\n" +
            "    f = f * f * (3.0 - 2.0 * f);\n" +
            "    float a = hash21(i);\n" +
            "    float b = hash21(i + vec2(1.0, 0.0));\n" +
            "    float c = hash21(i + vec2(0.0, 1.0));\n" +
            "    float d = hash21(i + vec2(1.0, 1.0));\n" +
            "    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);\n" +
            "}\n" +
            "float fbm(vec2 p) {\n" +
            "    float v = 0.0;\n" +
            "    float a = 0.5;\n" +
            "    for (int i = 0; i < 5; i++) {\n" +
            "        v += a * noise(p);\n" +
            "        p = p * 2.02 + vec2(7.13, 3.71);\n" +
            "        a *= 0.5;\n" +
            "    }\n" +
            "    return v;\n" +
            "}\n" +
            "void main() {\n" +
            "    vec2 uv = gl_FragCoord.xy / u_res;\n" +
            "    vec2 p = (gl_FragCoord.xy - 0.5 * u_res) / u_res.y;\n" +
            "    float t = u_time * 0.022;\n" +
            "    vec3 col = vec3(0.023, 0.025, 0.036);\n" +
            "    col += vec3(0.016, 0.013, 0.028) * (1.0 - uv.y);\n" +
            "    float n1 = fbm(vec2(p.x * 1.05 + t * 0.7, p.y * 1.6 - t * 0.3));\n" +
            "    float b1 = (p.y + 0.08 + 0.28 * n1 - 0.1 * sin(p.x * 1.4 + t)) * 3.2;\n" +
            "    float band = exp(-b1 * b1);\n" +
            "    col += vec3(0.17, 0.115, 0.31) * band * 0.58;\n" +
            "    float n2 = fbm(vec2(p.x * 1.45 - t * 0.45, p.y * 2.1 + t * 0.22) + 13.0);\n" +
            "    float b2 = (p.y + 0.34 + 0.2 * n2) * 4.0;\n" +
            "    float band2 = exp(-b2 * b2);\n" +
            "    col += vec3(0.055, 0.19, 0.18) * band2 * 0.46;\n" +
            "    float sheen = 1.0 - smoothstep(0.0, 0.42, abs(p.x * 0.62 + p.y * 0.95 - 0.55 + 0.05 * sin(t * 1.7)));\n" +
            "    col += vec3(0.16, 0.14, 0.24) * sheen * 0.055;\n" +
            "    vec2 gp = p * vec2(52.0, 32.0) + vec2(t * 4.0, t * 1.5);\n" +
            "    vec2 id = floor(gp);\n" +
            "    vec2 f = fract(gp) - 0.5;\n" +
            "    float h = hash21(id);\n" +
            "    float twinkle = 0.55 + 0.45 * sin(u_time * 0.55 + h * 24.0);\n" +
            "    float dust = step(0.962, h) * smoothstep(0.2, 0.0, length(f)) * twinkle;\n" +
            "    col += vec3(0.58, 0.53, 0.82) * dust * 0.32;\n" +
            "    float vig = 1.0 - smoothstep(0.28, 1.12, length(p * vec2(0.82, 1.18)));\n" +
            "    col *= mix(0.56, 1.0, vig);\n" +
            "    col += (hash21(gl_FragCoord.xy + fract(u_time) * 91.0) - 0.5) * 0.013;\n" +
            "    col *= u_dim;\n" +
            "    gl_FragColor = vec4(max(col, 0.0), 1.0);\n" +
            "}\n";

    private final FloatBuffer quad;
    private int program = -1;
    private int positionHandle;
    private int resolutionHandle;
    private int timeHandle;
    private int dimHandle;
    private long started;
    private volatile float dim = 1f;

    public AuroraRenderer() {
        float[] vertices = {-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f};
        quad = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        quad.put(vertices).position(0);
    }

    public void setDimmed(boolean dimmed) {
        dim = dimmed ? 0.68f : 1f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        started = System.nanoTime();
        GLES20.glClearColor(0.023f, 0.025f, 0.036f, 1f);
        program = buildProgram(VERTEX, FRAGMENT);
        if (program != 0) {
            positionHandle = GLES20.glGetAttribLocation(program, "a_position");
            resolutionHandle = GLES20.glGetUniformLocation(program, "u_res");
            timeHandle = GLES20.glGetUniformLocation(program, "u_time");
            dimHandle = GLES20.glGetUniformLocation(program, "u_dim");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (program == 0) {
            return;
        }
        float seconds = (System.nanoTime() - started) / 1_000_000_000f;
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(positionHandle);
        quad.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, quad);
        GLES20.glUniform2f(resolutionHandle, getWidth(), getHeight());
        GLES20.glUniform1f(timeHandle, seconds);
        GLES20.glUniform1f(dimHandle, dim);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private int width = 1;
    private int height = 1;

    private float getWidth() {
        return width;
    }

    private float getHeight() {
        return height;
    }

    public void setViewport(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    private int buildProgram(String vertexSource, String fragmentSource) {
        int vs = compile(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fs = compile(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (vs == 0 || fs == 0) {
            return 0;
        }
        int p = GLES20.glCreateProgram();
        GLES20.glAttachShader(p, vs);
        GLES20.glAttachShader(p, fs);
        GLES20.glLinkProgram(p);
        int[] status = new int[1];
        GLES20.glGetProgramiv(p, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Program link failed: " + GLES20.glGetProgramInfoLog(p));
            GLES20.glDeleteProgram(p);
            return 0;
        }
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);
        return p;
    }

    private int compile(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Shader compile failed: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
}
