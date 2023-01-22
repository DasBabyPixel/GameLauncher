package gamelauncher.engine.util.text.flattener;


import gamelauncher.engine.util.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ComponentFlattener {
	/**
	 * Create a new builder for a flattener.
	 *
	 * @return a new builder
	 */
	static @NotNull Builder builder() {
		return new ComponentFlattenerImpl.BuilderImpl();
	}

	/**
	 * A component flattener that will only handle text components.
	 *
	 * <p>All other component types will not be included in the output.</p>
	 *
	 * @return a text-only flattener
	 */
	static @NotNull ComponentFlattener textOnly() {
		return ComponentFlattenerImpl.TEXT_ONLY;
	}

	/**
	 * Perform a flattening on the component, providing output to the {@code listener}.
	 *
	 * @param input the component to be flattened
	 * @param listener the listener that will receive flattened component state
	 */
	void flatten(final @NotNull Component input, final @NotNull FlattenerListener listener);

	Builder toBuilder();

	/**
	 * A builder for a component flattener.
	 *
	 * <p>A new builder will start out empty, providing empty strings for all component types.</p>
	 */
	interface Builder {
		/**
		 * Register a type of component to be handled.
		 *
		 * @param type the component type
		 * @param converter the converter to map that component to a string
		 * @param <T> component type
		 * @return this builder
		 * @see #complexMapper(Class, BiConsumer) for component types that are too complex to be
		 * directly rendered to a string
		 */
		<T extends Component> @NotNull Builder mapper(final @NotNull Class<T> type,
				final @NotNull Function<T, String> converter);

		/**
		 * Register a type of component that needs to be flattened to an intermediate stage.
		 *
		 * @param type the component type
		 * @param converter a provider of contained Components
		 * @param <T> component type
		 * @return this builder
		 */
		<T extends Component> @NotNull Builder complexMapper(final @NotNull Class<T> type,
				final @NotNull BiConsumer<T, Consumer<Component>> converter);

		/**
		 * Register a handler for unknown component types.
		 *
		 * <p>This will be called if no other converter can be found.</p>
		 *
		 * @param converter the converter, may be null to ignore unknown components
		 * @return this builder
		 */
		@NotNull Builder unknownMapper(final @Nullable Function<Component, String> converter);

		ComponentFlattener build();
	}
}
