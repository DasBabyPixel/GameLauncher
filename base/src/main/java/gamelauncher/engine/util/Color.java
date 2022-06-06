package gamelauncher.engine.util;

public class Color {

	public static final Color white = new Color(1F, 1F, 1F, 1F);

	public final float r, g, b, a;

	public Color(int r, int g, int b, int a) {
		this(r / 255F, g / 255F, b / 255F, a / 255F);
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color withAlpha(float alpha) {
		return new Color(r, g, b, alpha);
	}
}
