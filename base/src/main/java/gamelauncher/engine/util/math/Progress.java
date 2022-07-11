package gamelauncher.engine.util.math;

import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.engine.util.Arrays;

/**
 * Utility class for calculating the progress for longer tasks like downloads or
 * loading
 * 
 * @author DasBabyPixel
 */
public class Progress {

	private final Progress[] children;

	private final int steps;
	private final AtomicInteger step = new AtomicInteger(0);

	/**
	 * Creates a {@link Progress} object with a specified amount of steps
	 * 
	 * @param steps
	 */
	public Progress(int steps) {
		this.steps = steps;
		this.children = new Progress[0];
	}

	/**
	 * Creates a {@link Progress} object with children This object will not support
	 * steps
	 * 
	 * @param children
	 */
	public Progress(Progress... children) {
		this.children = Arrays.copy(children);
		this.steps = -1;
	}

	/**
	 * Steps forward
	 */
	public void nextStep() {
		int max;
		if (steps == -1) {
			max = children.length;
		} else {
			max = steps;
		}
		if (step.get() < max) {
			step.incrementAndGet();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * @return the current sub-progress. This returns the {@link Progress} at the
	 *         index of {@link Progress#getStep()}
	 */
	public Progress getCurrentSubProgress() {
		return children[step.get()];
	}

	/**
	 * @return the current step
	 */
	public int getStep() {
		return step.get();
	}

	/**
	 * @return the progress, as a floating point from 0 to 1
	 */
	public float getProgress() {
		if (steps == -1) {
			return (float) step.get() / (float) children.length;
		}
		return (float) step.get() / (float) steps;
	}
}
