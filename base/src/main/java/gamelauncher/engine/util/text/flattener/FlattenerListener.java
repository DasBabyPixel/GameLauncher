package gamelauncher.engine.util.text.flattener;

import gamelauncher.engine.util.text.format.Style;
import org.jetbrains.annotations.NotNull;

/**
 * A listener accepting styled information from flattened components.
 */
@FunctionalInterface
public interface FlattenerListener {
    /**
     * Begin a region of style in the component.
     *
     * @param style the style to push
     */
    default void pushStyle(final @NotNull Style style) {
    }

    /**
     * Accept the plain-text content of a single component.
     *
     * @param text the component text
     */
    void component(final @NotNull String text);

    /**
     * Pop a pushed style.
     *
     * <p>The popped style will always be the most recent un-popped style that has been
     * {@link #pushStyle(Style) pushed}.</p>
     *
     * @param style the style popped, as passed to {@link #pushStyle(Style)}
     */
    default void popStyle(final @NotNull Style style) {
    }
}
