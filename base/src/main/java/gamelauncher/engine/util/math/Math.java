package gamelauncher.engine.util.math;

/**
 * @author DasBabyPixel
 */
public class Math {

	public static float min(float v1, float v2) {
		return java.lang.Math.min(v1, v2);
	}

	public static float max(float v1, float v2) {
		return java.lang.Math.max(v1, v2);
	}

	public static int min(int v1, int v2) {
		return java.lang.Math.min(v1, v2);
	}

	public static int max(int v1, int v2) {
		return java.lang.Math.max(v1, v2);
	}

	public static float clamp(float val, float min, float max) {
		return max(min, min(max, val));
	}

	public static float sqrt(float val) {
		return (float) java.lang.Math.sqrt(val);
	}

	public static float pow(float val, float exp) {
		return (float) java.lang.Math.pow(val, exp);
	}

	public static double lerp(double n1, double n2, double progress) {
		return n1 + (n2 - n1) * progress;
	}

	public static float lerp(float n1, float n2, float progress) {
		return n1 + (n2 - n1) * progress;
	}

	public static float abs(float val) {
		return val >= 0 ? val : -val;
	}

	public static int abs(int val) {
		return val >= 0 ? val : -val;
	}

	public static double floor(double d) {
		return java.lang.Math.floor(d);
	}

	public static int floor(float f) {
		return (int) java.lang.Math.floor(f);
	}

	public static int ceil(double height) {
		return (int) java.lang.Math.ceil(height);
	}

	public static int round(float f) {
		return java.lang.Math.round(f);
	}

}
