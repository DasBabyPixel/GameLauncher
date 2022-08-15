package gamelauncher.lwjgl.render.states;

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
		cd.contextLocals.put(this, initial);
		return initial;
	}

	/**
	 */
	public final void remove() {
		remove(StateRegistry.currentContext());
	}
	
	final void remove(ContextDependant cd) {
		cd.contextLocals.remove(this);
	}

	protected abstract void valueRemoved(T value);

	protected abstract T initialValue();

}
