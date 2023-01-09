package gamelauncher.lwjgl.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.GuiStack.StackEntry;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.gui.launcher.TextureGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.gui.launcher.LWJGLColorGui;
import gamelauncher.lwjgl.gui.launcher.LWJGLMainScreenGui;
import gamelauncher.lwjgl.gui.launcher.LWJGLScrollGui;
import gamelauncher.lwjgl.gui.launcher.LWJGLTextureGui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public class LWJGLGuiManager extends AbstractGameResource implements GuiManager {
	private static final Logger logger = Logger.getLogger();

	private final LWJGLGameLauncher launcher;

	private final Map<Framebuffer, GuiStack> guis = new ConcurrentHashMap<>();

	private final Map<Class<? extends LauncherBasedGui>, GameSupplier<? extends LauncherBasedGui>>
			registeredGuis = new HashMap<>();

	private final Map<Class<? extends LauncherBasedGui>, Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>>>
			converters = new HashMap<>();

	public LWJGLGuiManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.launcher.getEventManager().registerListener(this);
		this.registerGuiCreator(MainScreenGui.class, () -> new LWJGLMainScreenGui(launcher));
		this.registerGuiCreator(TextureGui.class, () -> new LWJGLTextureGui(launcher));
		this.registerGuiCreator(ColorGui.class, () -> new LWJGLColorGui(launcher));
		this.registerGuiCreator(ScrollGui.class, () -> new LWJGLScrollGui(launcher));
	}

	@Override
	public void cleanup0() throws GameException {
		this.launcher.getEventManager().unregisterListener(this);
		CompletableFuture<?>[] futures = new CompletableFuture[this.guis.size()];
		int i = 0;
		for (Map.Entry<Framebuffer, GuiStack> entry : this.guis.entrySet()) {
			futures[i++] = this.cleanupLater(entry.getKey());
		}
		Threads.waitFor(futures);
	}

	/**
	 * Cleanes up a framebuffer and its guis
	 *
	 * @param framebuffer the framebuffer
	 *
	 * @return a future for the task
	 */
	@Deprecated
	private CompletableFuture<Void> cleanupLater(Framebuffer framebuffer) {
		GuiStack stack = this.guis.remove(framebuffer);
		if (stack != null) {
			return framebuffer.renderThread().submit(() -> {
				GuiStack.StackEntry se;
				while ((se = stack.popGui()) != null) {
					se.gui.cleanup(framebuffer);
				}
			});
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void openGui(Framebuffer framebuffer, Gui gui) throws GameException {
		GuiStack stack = this.guis.get(framebuffer);
		if (stack == null) {
			stack = new GuiStack();
			this.guis.put(framebuffer, stack);
		}
		final boolean exit = gui == null;
		StackEntry scurrentGui = exit ? stack.popGui() : stack.peekGui();
		Gui currentGui = scurrentGui == null ? null : scurrentGui.gui;
		if (currentGui != null) {
			currentGui.unfocus();
			currentGui.onClose();
			if (exit) {
				framebuffer.renderThread().submit(() -> {
					currentGui.cleanup(framebuffer);
				});
			}
		} else {
			if (exit) {
				gui = this.createGui(MainScreenGui.class);
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
	public Gui getCurrentGui(Framebuffer framebuffer) throws GameException {
		if (!this.guis.containsKey(framebuffer)) {
			return null;
		}
		StackEntry e = this.guis.get(framebuffer).peekGui();
		return e == null ? null : e.gui;
	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {
		Threads.waitFor(this.cleanupLater(framebuffer));
	}

	@Override
	public GameLauncher getLauncher() {
		return this.launcher;
	}

	@Override
	public void updateGuis() throws GameException {
		for (GuiStack stack : this.guis.values()) {
			GuiStack.StackEntry e = stack.peekGui();
			if (e != null) {
				e.gui.update();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException {
		GameSupplier<? extends LauncherBasedGui> sup = this.registeredGuis.get(clazz);
		if (sup == null) {
			throw new GameException(
					"Gui " + clazz.getName() + " not registered! Outdated launcher?");
		}
		T t = clazz.cast(sup.get());
		if (this.converters.containsKey(clazz)) {
			Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c =
					this.converters.get(clazz);
			for (@SuppressWarnings("rawtypes") GameFunction func : c) {
				t = clazz.cast(func.apply(t));
			}
		}
		return t;
	}

	@Override
	public <T extends LauncherBasedGui> void registerGuiConverter(Class<T> clazz,
			GameFunction<T, T> converter) {
		Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c =
				this.converters.get(clazz);
		if (c == null) {
			c = new HashSet<>();
			this.converters.put(clazz, c);
		}
		c.add(converter);
	}

	@Override
	public <T extends LauncherBasedGui> void registerGuiCreator(Class<T> clazz,
			GameSupplier<T> sup) {
		this.registeredGuis.put(clazz, sup);
	}

	public void redraw() {
		for (Map.Entry<Framebuffer, GuiStack> e : guis.entrySet()) {
			e.getKey().scheduleRedraw();
		}
	}

	@EventHandler
	private void handle(KeybindEntryEvent event) {
		KeybindEntry entry = event.getEntry();
		// TODO: Gui Selection - not relevant with only one gui being able to be opened
		// in this guimanager
		this.guis.values().forEach(stack -> {
			StackEntry e = stack.peekGui();
			if (e != null) {
				try {
					e.gui.handle(entry);
				} catch (GameException ex) {
					logger.error(ex);
				}
			}
		});
	}

	//	private boolean isExitKeybind(KeybindEntry entry) {
	//
	//	}

}
