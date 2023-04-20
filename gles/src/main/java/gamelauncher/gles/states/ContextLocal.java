package gamelauncher.gles.states;

/**
 * Pretty much same as ThreadLocal just for contexts. If a thread loses the
 * context, the local is deleted, if it gets a new context new objects are
 * created.
 *
 * @param <T>
 * @author DasBabyPixel
 */
public abstract class ContextLocal<T> {

    /**
     * @return the current value for this context
     */
    @SuppressWarnings("unchecked") public final T get() {
        ContextDependant cd = StateRegistry.currentContext(); // TODO: Atomic replacement in contextLocals
        if (cd.contextLocals.containsKey(this)) {
            return (T) cd.contextLocals.get(this);
        }
        T initial = initialValue();
        if (initial != null) cd.contextLocals.put(this, initial);
        return initial;
    }

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
     *
     */
    public final void remove() {
        remove(StateRegistry.currentContext());
    }

    @SuppressWarnings("unchecked") final void remove(ContextDependant cd) {
        cd.contextLocals.computeIfPresent(this, (t, u) -> {
            valueRemoved((T) u);
            return null;
        });
    }

    /**
     * @return an empty contextlocal
     */
    public static <T> ContextLocal<T> empty() {
        return new EmptyContextLocal<>();
    }

    private static class EmptyContextLocal<T> extends ContextLocal<T> {
        @Override protected void valueRemoved(T value) {
        }

        @Override protected T initialValue() {
            return null;
        }
    }

    protected abstract void valueRemoved(T value);

    protected abstract T initialValue();

}
