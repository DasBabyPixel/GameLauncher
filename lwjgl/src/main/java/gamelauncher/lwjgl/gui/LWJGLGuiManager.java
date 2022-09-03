package gamelauncher.lwjgl.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.GuiStack.StackEntry;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.launcher.gui.ColorGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.launcher.gui.ScrollGui;
import gamelauncher.engine.launcher.gui.TextureGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.launcher.gui.LWJGLColorGui;
import gamelauncher.lwjgl.launcher.gui.LWJGLMainScreenGui;
import gamelauncher.lwjgl.launcher.gui.LWJGLScrollGui;
import gamelauncher.lwjgl.launcher.gui.LWJGLTextureGui;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLGuiManager extends AbstractGameResource implements GuiManager {

	private final LWJGLGameLauncher launcher;

	private final Map<Framebuffer, GuiStack> guis = new ConcurrentHashMap<>();

	private final Map<Class<? extends LauncherBasedGui>, GameSupplier<? extends LauncherBasedGui>> registeredGuis = new HashMap<>();

	private final Map<Class<? extends LauncherBasedGui>, Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>>> converters = new HashMap<>();

	/**
	 * @param launcher
	 */
	public LWJGLGuiManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.launcher.getEventManager().registerListener(this);
		registerGuiCreator(MainScreenGui.class, () -> new LWJGLMainScreenGui(launcher));
		registerGuiCreator(TextureGui.class, () -> new LWJGLTextureGui(launcher));
		registerGuiCreator(ColorGui.class, () -> new LWJGLColorGui(launcher));
		registerGuiCreator(ScrollGui.class, () -> new LWJGLScrollGui(launcher));
	}

	@Override
	public void updateGuis() throws GameException {
		for (GuiStack stack : guis.values()) {
			GuiStack.StackEntry e = stack.peekGui();
			if (e != null) {
				e.gui.update();
			}
		}
	}

	@Override
	public void cleanup0() throws GameException {
		this.launcher.getEventManager().unregisterListener(this);
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] futures = new CompletableFuture[guis.size()];
		int i = 0;
		for (Map.Entry<Framebuffer, GuiStack> entry : guis.entrySet()) {
			futures[i++] = cleanupLater(entry.getKey());
		}
		Threads.waitFor(futures);
	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {
		Threads.waitFor(cleanupLater(framebuffer));
	}

	/**
	 * Cleanes up a framebuffer and its guis
	 * 
	 * @param framebuffer
	 * @return a future for the task
	 */
	@Deprecated
	public CompletableFuture<Void> cleanupLater(Framebuffer framebuffer) {
		GuiStack stack = guis.remove(framebuffer);
		if (stack != null) {
			return framebuffer.getRenderThread().submit(() -> {
				GuiStack.StackEntry se;
				while ((se = stack.popGui()) != null) {
					se.gui.cleanup(framebuffer);
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
	public Gui getCurrentGui(Framebuffer framebuffer) throws GameException {
		if (!guis.containsKey(framebuffer)) {
			return null;
		}
		StackEntry e = guis.get(framebuffer).peekGui();
		return e == null ? null : e.gui;
	}

	@Override
	public void openGui(Framebuffer framebuffer, Gui gui) throws GameException {
		GuiStack stack = guis.get(framebuffer);
		if (stack == null) {
			stack = new GuiStack();
			guis.put(framebuffer, stack);
		}
		final boolean exit = gui == null;
		StackEntry scurrentGui = exit ? stack.popGui() : stack.peekGui();
		Gui currentGui = scurrentGui == null ? null : scurrentGui.gui;
		if (currentGui != null) {
			currentGui.unfocus();
			currentGui.onClose();
			if (exit) {
				framebuffer.getRenderThread().submit(() -> {
					currentGui.cleanup(framebuffer);
				});
			}
		} else {
			if (exit) {
				gui = createGui(MainScreenGui.class);
			}
		}
		if (gui != null) {
			gui.getWidthProperty().bind(framebuffer.width());
			gui.getHeightProperty().bind(framebuffer.height());
			stack.pushGui(gui);
			gui.onOpen();
			gui.focus();
			framebuffer.scheduleRedraw();
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
			throw new GameException("Gui " + clazz.getName() + " not registered! Outdated launcher?");
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

	@EventHandler
	private void handle(KeybindEntryEvent event) {
		KeybindEntry entry = event.getEntry();
		// TODO: Gui Selection - not relevant with only one gui being able to be opened
		// in this guimanager
		guis.values().forEach(stack -> {
			StackEntry e = stack.peekGui();
			if (e != null) {
				try {
					e.gui.handle(entry);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
