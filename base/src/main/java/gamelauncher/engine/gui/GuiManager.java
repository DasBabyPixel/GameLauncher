package gamelauncher.engine.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface GuiManager {

	/**
	 * Opens a {@link Gui} for a window. Use null to exit the current gui. 
	 * @param window
	 * @param gui
	 * @throws GameException
	 */
	void openGui(Window window, Gui gui) throws GameException;

	/**
	 * Opens a {@link Gui} for a window.
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
	 * @param <T>
	 * @param clazz
	 * @return the created {@link Gui}
	 * @throws GameException
	 */
	<T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException;

}
