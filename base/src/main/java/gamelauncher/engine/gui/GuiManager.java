package gamelauncher.engine.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.function.GameSupplier;

/**
 * @author DasBabyPixel
 */
public interface GuiManager extends GameResource {

	/**
	 * Opens a {@link Gui} for a window. Use null to exit the current gui.
	 * 
	 * @param window
	 * @param gui
	 * @throws GameException
	 */
	void openGui(Window window, Gui gui) throws GameException;

	/**
	 * @param window
	 * @return the current gui for a window
	 * @throws GameException
	 */
	Gui getCurrentGui(Window window) throws GameException;

	/**
	 * Opens a {@link Gui} for a window.
	 * 
	 * @param window
	 * @param clazz
	 * @throws GameException
	 */
	default void openGuiByClass(Window window, Class<? extends LauncherBasedGui> clazz) throws GameException {
		openGui(window, createGui(clazz));
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	GameLauncher getLauncher();

	/**
	 * Creates a Gui for the given {@link Class class}.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return the created {@link Gui}
	 * @throws GameException
	 */
	<T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException;

	/**
	 * Registers a converter for {@link LauncherBasedGui}s. When a
	 * {@link LauncherBasedGui} is created via {@link GuiManager#createGui(Class)},
	 * this function will be called.
	 * 
	 * @param clazz 
	 * @param converter
	 */
	<T extends LauncherBasedGui>void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter);

	/**
	 * Registers a {@link LauncherBasedGui} creator. Used to create
	 * {@link LauncherBasedGui}s via {@link GuiManager#createGui(Class)}
	 * 
	 * @param clazz
	 * @param supplier
	 */
	<T extends LauncherBasedGui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> supplier);

}
