package gamelauncher.engine.util.math;

/**
 * @author DasBabyPixel
 */
public class Math {

	/**
	 * @param v1
	 * @param v2
	 * @return the lesser value of the two arguments
	 */
	public static float min(float v1, float v2) {
		return v1 < v2 ? v1 : v2;
	}

	/**
	 * @param v1
	 * @param v2
	 * @return the higher value of the two arguments
	 */
	public static float max(float v1, float v2) {
		return v1 > v2 ? v1 : v2;
	}

	/**
	 * @param v1
	 * @param v2
	 * @return the lesser value of the two arguments
	 */
	public static int min(int v1, int v2) {
		return v1 < v2 ? v1 : v2;
	}

	/**
	 * @param v1
	 * @param v2
	 * @return the higher value of the two arguments
	 */
	public static int max(int v1, int v2) {
		return v1 > v2 ? v1 : v2;
	}

	/**
	 * @param val
	 * @param min
	 * @param max
	 * @return the clamped value
	 */
	public static float clamp(float val, float min, float max) {
		return max(min, min(max, val));
	}

	/**
	 * @param val
	 * @return the square root of the value
	 */
	public static float sqrt(float val) {
		return (float) java.lang.Math.sqrt(val);
	}

	/**
	 * @param val
	 * @param exp
	 * @return val^exp
	 */
	public static float pow(float val, float exp) {
		return (float) java.lang.Math.pow(val, exp);
	}

	/**
	 * @param n1
	 * @param n2
	 * @param progress
	 * @return the interpolated number
	 */
	public static double lerp(double n1, double n2, double progress) {
		return n1 + (n2 - n1) * progress;
	}

	/**
	 * @param val
	 * @return the absolute value of the argument
	 */
	public static float abs(float val) {
		return val >= 0 ? val : -val;
	}

	/**
	 * @param d
	 * @return the largest (closest to positive infinity)floating-point value that
	 *         less than or equal to the argumentand is equal to a mathematical
	 *         integer.
	 */
	public static double floor(double d) {
		return java.lang.Math.floor(d);
	}

	/**
	 * @param f
	 * @return the largest (closest to positive infinity)floating-point value that
	 *         less than or equal to the argumentand is equal to a mathematical
	 *         integer.
	 */
	public static int floor(float f) {
		return (int) java.lang.Math.floor(f);
	}

	/**
	 * @param height
	 * @return {@link Math#ceil(double)}
	 */
	public static int ceil(double height) {
		return (int) java.lang.Math.ceil(height);
	}

	/**
	 * @param f
	 * @return the rounded value
	 */
	public static int round(float f) {
		return java.lang.Math.round(f);
	}

}
