package gamelauncher.lwjgl.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.GuiStack.StackEntry;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.gui.impl.LWJGLMainScreenGui;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLGuiManager implements GuiManager {

	private final LWJGLGameLauncher launcher;
	private final Map<Window, GuiStack> guis = new ConcurrentHashMap<>();
	private final Map<Class<? extends LauncherBasedGui>, GameSupplier<? extends LauncherBasedGui>> registeredGuis = new HashMap<>();
	private final Map<Class<? extends LauncherBasedGui>, Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>>> converters = new HashMap<>();

	/**
	 * @param launcher
	 */
	public LWJGLGuiManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		registerGuiCreator(MainScreenGui.class, () -> new LWJGLMainScreenGui(launcher));
	}

	@Override
	public void cleanup() throws GameException {
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[guis.size()];
		int i = 0;
		for (Map.Entry<Window, GuiStack> entry : guis.entrySet()) {
			futures[i++] = cleanup(entry.getKey());
		}
		Threads.waitFor(futures);
	}

	/**
	 * Cleanes up a window and its guis
	 * 
	 * @param window
	 * @return a future for the task
	 */
	public CompletableFuture<Void> cleanup(Window window) {
		GuiStack stack = guis.remove(window);
		if (stack != null) {
			return window.getRenderThread().submit(() -> {
				GuiStack.StackEntry se;
				while ((se = stack.popGui()) != null) {
					se.gui.cleanup(window);
				}
			});
		}
		return CompletableFuture.completedFuture(null);
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
		StackEntry e = guis.get(window).peekGui();
		return e == null ? null : e.gui;
	}

	@Override
	public void openGui(Window window, Gui gui) throws GameException {
		GuiStack stack = guis.get(window);
		if (stack == null) {
			stack = new GuiStack();
			guis.put(window, stack);
		}
		final boolean exit = gui == null;
		StackEntry scurrentGui = exit ? stack.popGui() : stack.peekGui();
		Gui currentGui = scurrentGui == null ? null : scurrentGui.gui;
		if (currentGui != null) {
			currentGui.unfocus();
			currentGui.onClose();
			if (exit) {
				window.getRenderThread().submit(() -> {
					currentGui.cleanup(window);
				});
			}
		} else {
			if (exit) {
				gui = createGui(MainScreenGui.class);
			}
		}
		if (gui != null) {
			gui.getWidthProperty().bind(window.getFramebuffer().width());
			gui.getHeightProperty().bind(window.getFramebuffer().height());
			stack.pushGui(gui);
			gui.onOpen();
			gui.focus();
			window.scheduleDraw();
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
