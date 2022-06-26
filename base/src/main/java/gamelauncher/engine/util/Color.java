package gamelauncher.engine.util;

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
		this(r / 255F, g / 255F, b / 255F, a / 255F);
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
}
