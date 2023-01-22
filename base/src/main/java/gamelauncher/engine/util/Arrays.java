package gamelauncher.engine.util;

import java.util.List;

/**
 * @author DasBabyPixel
 */
public class Arrays {

	public static <T> List<T> asList(T[] array) {
		return java.util.Arrays.asList(array);
	}

	public static <T> T[] copy(T[] array) {
		return java.util.Arrays.copyOf(array, array.length);
	}

	public static <T> T[] copyOfRange(T[] original, int from, int to) {
		return java.util.Arrays.copyOfRange(original, from, to);
	}
}
