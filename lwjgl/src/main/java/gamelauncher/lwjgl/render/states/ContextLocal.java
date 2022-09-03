package gamelauncher.lwjgl.render.states;

import java.util.function.BiFunction;

import gamelauncher.lwjgl.LWJGLGameLauncher;

/**
 * Pretty much same as ThreadLocal just for contexts. If a thread loses the
 * context, the local is deleted, if it gets a new context new objects are
 * created.
 * 
 * @author DasBabyPixel
 * @param <T>
 */
public abstract class ContextLocal<T> {

	private final LWJGLGameLauncher launcher;

	/**
	 * @param launcher
	 */
	public ContextLocal(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * @return the {@link LWJGLGameLauncher}
	 */
	public LWJGLGameLauncher getLauncher() {
		return launcher;
	}

	/**
	 * @return the current value for this context
	 */
	@SuppressWarnings("unchecked")
	public final T get() {
		ContextDependant cd = StateRegistry.currentContext();
		if (cd.contextLocals.containsKey(this)) {
			return (T) cd.contextLocals.get(this);
		}
		T initial = initialValue();
		if (initial != null)
			cd.contextLocals.put(this, initial);
		return initial;
	}

	/**
	 * @param value
	 */
	public final void set(T value) {
		ContextDependant cd = StateRegistry.currentContext();
		cd.contextLocals.put(this, value);
	}

	/**
	 * @return if this contextlocal has a value
	 */
	public final boolean has() {
		ContextDependant cd = StateRegistry.currentContext();
		return cd.contextLocals.containsKey(this);
	}

	/**
	 */
	public final void remove() {
		remove(StateRegistry.currentContext());
	}

	final void remove(ContextDependant cd) {
		cd.contextLocals.computeIfPresent(this, new BiFunction<ContextLocal<?>, Object, Object>() {

			@SuppressWarnings("unchecked")
			@Override
			public Object apply(ContextLocal<?> t, Object u) {
				valueRemoved((T) u);
				return null;
			}

		});
	}

	/**
	 * @param <T>
	 * @param launcher
	 * @return an empty contextlocal
	 */
	public static <T> ContextLocal<T> empty(LWJGLGameLauncher launcher) {
		return new ContextLocal<T>(launcher) {

			@Override
			protected void valueRemoved(T value) {
			}

			@Override
			protected T initialValue() {
				return null;
			}

		};
	}

	protected abstract void valueRemoved(T value);

	protected abstract T initialValue();

}
