package gamelauncher.engine.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;

/**
 * @author DasBabyPixel
 */
public interface GuiManager extends GameResource {

	/**
	 * Opens a {@link Gui} for a window. Use null to exit the current gui.
	 * 
	 * @param framebuffer
	 * @param gui
	 * @throws GameException
	 */
	void openGui(Framebuffer framebuffer, Gui gui) throws GameException;

	/**
	 * @param framebuffer
	 * @return the current gui for a window
	 * @throws GameException
	 */
	Gui currentGui(Framebuffer framebuffer) throws GameException;

	/**
	 * @param framebuffer
	 * @throws GameException
	 */
	void cleanup(Framebuffer framebuffer) throws GameException;

	/**
	 * Opens a {@link Gui} for a window.
	 * 
	 * @param framebuffer
	 * @param clazz
	 * @throws GameException
	 */
	default void openGuiByClass(Framebuffer framebuffer, Class<? extends LauncherBasedGui> clazz) throws GameException {
		openGui(framebuffer, createGui(clazz));
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	GameLauncher launcher();
	
	/**
	 * @throws GameException 
	 * 
	 */
	void updateGuis() throws GameException;

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
	<T extends LauncherBasedGui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter);

	/**
	 * Registers a {@link LauncherBasedGui} creator. Used to create
	 * {@link LauncherBasedGui}s via {@link GuiManager#createGui(Class)}
	 * 
	 * @param clazz
	 * @param supplier
	 */
	<T extends LauncherBasedGui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> supplier);

}
