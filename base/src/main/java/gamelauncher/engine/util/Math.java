package gamelauncher.engine.util;

public class Math {

	public static float min(float v1, float v2) {
		return v1 < v2 ? v1 : v2;
	}

	public static float max(float v1, float v2) {
		return v1 > v2 ? v1 : v2;
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

	public static float abs(float val) {
		return val >= 0 ? val : -val;
	}
}
