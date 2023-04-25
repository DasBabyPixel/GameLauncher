package gamelauncher.engine.util;

import org.joml.Math;

/**
 * @author DasBabyPixel
 */
public class Color {

    public static final Color white = new Color(1F, 1F, 1F, 1F);

    public final float r, g, b, a;

    /**
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public Color(int r, int g, int b, int a) {
        this((r & 0xFF) / 255F, (g & 0xFF) / 255F, (b & 0xFF) / 255F, (a & 0xFF) / 255F);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * @param alpha
     * @return the new color
     */
    public Color withAlpha(float alpha) {
        return new Color(this.r, this.g, this.b, alpha);
    }

    /**
     * @return the red byte
     */
    public byte bred() {
        return this.toByte(this.r);
    }

    /**
     * @return the green byte
     */
    public byte bgreen() {
        return this.toByte(this.g);
    }

    /**
     * @return the blue byte
     */
    public byte bblue() {
        return this.toByte(this.b);
    }

    /**
     * @return the alpha byte
     */
    public byte balpha() {
        return this.toByte(this.a);
    }

    public int ired() {
        return this.toInt(this.r);
    }

    public int igreen() {
        return this.toInt(this.g);
    }

    public int iblue() {
        return this.toInt(this.b);
    }

    private int toInt(float f) {
        f = f * 256;
        return Math.round(f >= 255 ? 255 : f);
    }

    private byte toByte(float f) {
        return (byte) Math.floor(f >= 1.0 ? 255 : f * 256.0);
    }

    /**
     * @return the rgba int
     */
    public int toRGBA() {
        return (this.bred() & 0xFF) << 24 | (this.bgreen() & 0xFF) << 16 | (this.bblue() & 0xFF) << 8 | (this.balpha() & 0xFF);
    }

}
