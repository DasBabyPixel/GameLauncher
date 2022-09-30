package gamelauncher.engine.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DasBabyPixel
 */
public class Arrays {

	/**
	 * @param <T>
	 * @param array
	 * @return a new {@link ArrayList} created of the array
	 */
	public static <T> List<T> asList(T[] array) {
		return java.util.Arrays.asList(array);
	}
	
	/**
	 * @param <T>
	 * @param array
	 * @return a cloned array
	 */
	public static <T> T[] copy(T[] array) {
		return java.util.Arrays.copyOf(array, array.length);
	}

	/**
	 * From {@link java.util.Arrays}
	 * @param original
	 * @param from
	 * @param to
	 * @return
	 */
	public static StackTraceElement[] copyOfRange(StackTraceElement[] original, int from, int to) {
		return java.util.Arrays.copyOfRange(original, from, to);
	}
}
