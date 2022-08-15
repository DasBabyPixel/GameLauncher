package gamelauncher.engine.util;

import gamelauncher.engine.util.math.Math;

/**
 * @author DasBabyPixel
 */
public class Color {

	@SuppressWarnings("javadoc")
	public static final Color white = new Color(1F, 1F, 1F, 1F);

	@SuppressWarnings("javadoc")
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
		return new Color(r, g, b, alpha);
	}

	/**
	 * @return the red byte
	 */
	public byte bred() {
		return toByte(r);
	}

	/**
	 * @return the green byte
	 */
	public byte bgreen() {
		return toByte(g);
	}

	/**
	 * @return the blue byte
	 */
	public byte bblue() {
		return toByte(b);
	}

	/**
	 * @return the alpha byte
	 */
	public byte balpha() {
		return toByte(a);
	}

	private byte toByte(float f) {
		return (byte) Math.floor(f >= 1.0 ? 255 : f * 256.0);
	}

	/**
	 * @return the rgba int
	 */
	public int toRGBA() {
		return (bred() & 0xFF) << 24 | (bgreen() & 0xFF) << 16 | (bblue() & 0xFF) << 8 | (balpha() & 0xFF);
	}

}
