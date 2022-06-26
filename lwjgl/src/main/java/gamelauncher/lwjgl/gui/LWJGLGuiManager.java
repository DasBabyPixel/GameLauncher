package gamelauncher.lwjgl.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.lwjgl.gui.impl.LWJGLMainScreenGui;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLGuiManager implements GuiManager {

	private final GameLauncher launcher;
	private final Map<Window, GuiStack> guis = new ConcurrentHashMap<>();
	private final Map<Class<? extends LauncherBasedGui>, GameSupplier<? extends LauncherBasedGui>> registeredGuis = new HashMap<>();

	/**
	 * @param launcher
	 */
	public LWJGLGuiManager(GameLauncher launcher) {
		this.launcher = launcher;
		registerGui(MainScreenGui.class, () -> new LWJGLMainScreenGui(launcher));
	}

	/**
	 * Registers a {@link Gui} creator
	 * 
	 * @param <T>
	 * @param clazz
	 * @param sup
	 */
	public <T extends LauncherBasedGui> void registerGui(Class<T> clazz, GameSupplier<T> sup) {
		registeredGuis.put(clazz, sup);
	}

	@Override
	public void openGui(Window window, Gui gui) throws GameException {
		GuiStack stack = guis.get(window);
		final boolean exit = gui == null;
		Gui currentGui = exit ? stack.popGui() : stack.peekGui();
		if (currentGui != null) {
			currentGui.unfocus();
			currentGui.onClose();
			if (exit) {
				currentGui.cleanup(window);
			}
		} else {
			if (exit) {
				gui = createGui(MainScreenGui.class);
			}
		}
		if (gui != null) {
			stack.pushGui(gui);
			gui.init(window);
			gui.onOpen();
			gui.focus();
		}
	}

	@Override
	public GameLauncher getLauncher() {
		return launcher;
	}

	@Override
	public <T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException {
		GameSupplier<? extends LauncherBasedGui> sup = registeredGuis.get(clazz);
		if (sup == null) {
			return null;
		}
		return clazz.cast(sup.get());
	}
}
