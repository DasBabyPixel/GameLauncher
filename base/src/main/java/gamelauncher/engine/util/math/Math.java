package gamelauncher.engine.util.math;

/**
 * @author DasBabyPixel
 */
public class Math {

	/**
	 * @param v1
	 * @param v2
	 * @return the smaller value of the two arguments
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
	 * @param val
	 * @return the absolute value of the argument
	 */
	public static float abs(float val) {
		return val >= 0 ? val : -val;
	}
}
