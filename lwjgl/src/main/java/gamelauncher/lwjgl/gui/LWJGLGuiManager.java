package gamelauncher.lwjgl.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
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
	private final Map<Class<? extends LauncherBasedGui>, Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>>> converters = new HashMap<>();

	/**
	 * @param launcher
	 */
	public LWJGLGuiManager(GameLauncher launcher) {
		this.launcher = launcher;
		registerGuiCreator(MainScreenGui.class, () -> new LWJGLMainScreenGui(launcher));
	}

	@Override
	public <T extends LauncherBasedGui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> sup) {
		registeredGuis.put(clazz, sup);
	}

	@Override
	public <T extends LauncherBasedGui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter) {
		Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c = converters.get(clazz);
		if (c == null) {
			c = new HashSet<>();
			converters.put(clazz, c);
		}
		c.add(converter);
	}

	@Override
	public Gui getCurrentGui(Window window) throws GameException {
		if (!guis.containsKey(window)) {
			return null;
		}
		return guis.get(window).peekGui();
	}

	@Override
	public void openGui(Window window, Gui gui) throws GameException {
		GuiStack stack = guis.get(window);
		if (stack == null) {
			stack = new GuiStack();
			guis.put(window, stack);
		}
		final boolean exit = gui == null;
		Gui currentGui = exit ? stack.popGui() : stack.peekGui();
		if (currentGui != null) {
			currentGui.unfocus();
			currentGui.onClose();
			if (exit) {
				window.getRenderThread().runLater(() -> {
					currentGui.cleanup(window);
				});
			}
		} else {
			if (exit) {
				gui = createGui(MainScreenGui.class);
			}
		}
		if (gui != null) {
			stack.pushGui(gui);
			gui.getWidthProperty().bind(window.getFramebuffer().width());
			gui.getHeightProperty().bind(window.getFramebuffer().height());
			Gui fgui = gui;
			window.getRenderThread().runLater(() -> {
				fgui.init(window);
				window.scheduleDraw();
			});
			gui.onOpen();
			gui.focus();
		}
	}

	@Override
	public GameLauncher getLauncher() {
		return launcher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException {
		GameSupplier<? extends LauncherBasedGui> sup = registeredGuis.get(clazz);
		if (sup == null) {
			return null;
		}
		T t = clazz.cast(sup.get());
		if (converters.containsKey(clazz)) {
			Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c = converters.get(clazz);
			for (@SuppressWarnings("rawtypes")
			GameFunction func : c) {
				t = clazz.cast(func.apply(t));
			}
		}
		return t;
	}
}
