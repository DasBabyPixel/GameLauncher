package gamelauncher.engine.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.function.GamePredicate;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEntry;
import gamelauncher.engine.util.keybind.ScrollKeybindEntry;

/**
 * A {@link Gui} for having and handling sub-{@link Gui}s
 * 
 * @see ParentableAbstractGui#GUIs
 * 
 * @author DasBabyPixel
 */
public abstract class ParentableAbstractGui extends AbstractGui {

	private final AtomicReference<Gui> focusedGui = new AtomicReference<>(null);
	/**
	 * The {@link Gui}s of this {@link ParentableAbstractGui} object
	 */
	public final Collection<Gui> GUIs = ConcurrentHashMap.newKeySet();
	private Collection<Integer> mouseButtons = ConcurrentHashMap.newKeySet();
	private Map<Integer, Collection<Gui>> mouseDownGuis = new ConcurrentHashMap<>();
	private final NumberValue lastMouseX = NumberValue.zero();
	private final NumberValue lastMouseY = NumberValue.zero();

	/**
	 * @param launcher
	 */
	public ParentableAbstractGui(GameLauncher launcher) {
		super(launcher);
	}

	@Override
	public final void init(Window window) throws GameException {
		doInit(window);
		doForGUIs(gui -> {
			gui.init(window);
		});
	}

	@Override
	public final void cleanup(Window window) throws GameException {
		doForGUIs(gui -> {
			gui.cleanup(window);
		});
		doCleanup(window);
		super.cleanup(window);
	}

	@Override
	public final void update() throws GameException {
		doForGUIs(gui -> {
			gui.update();
		});
		doUpdate();
	}

	@Override
	public void unfocus() throws GameException {
		super.unfocus();
		Gui focusedGui = this.focusedGui.get();
		if (focusedGui != null) {
			focusedGui.unfocus();
		}
	}

	@Override
	public final void render(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
		preRender(window, mouseX, mouseY, partialTick);
		if (doRender(window, mouseX, mouseY, partialTick)) {
			doForGUIs(gui -> {
				gui.render(window, mouseX, mouseY, partialTick);
			});
		}
		postRender(window, mouseX, mouseY, partialTick);
	}

	@Override
	public final void handle(KeybindEntry entry) throws GameException {
		if (doHandle(entry)) {
			if (entry instanceof KeyboardKeybindEntry) {
				KeyboardKeybindEntry c = (KeyboardKeybindEntry) entry;
				switch (c.type()) {
				case HOLD:
					forFocused(c);
					break;
				case PRESS:
					forFocused(c);
					break;
				case RELEASE:
					forFocused(c);
					break;
				case REPEAT:
					forFocused(c);
					break;
				case CHARACTER:
					forFocused(c);
					break;
				}
			} else if (entry instanceof MouseButtonKeybindEntry) {
				MouseButtonKeybindEntry c = (MouseButtonKeybindEntry) entry;
				lastMouseX.setNumber(c.mouseX());
				lastMouseY.setNumber(c.mouseY());
				switch (c.type()) {
				case HOLD:
					forFocused(c);
					break;
				case PRESS:
					mouseClicked(c);
					break;
				case RELEASE:
					mouseReleased(c);
					break;
				}
			} else if (entry instanceof MouseMoveKeybindEntry) {
				MouseMoveKeybindEntry c = (MouseMoveKeybindEntry) entry;
				lastMouseX.setNumber(c.mouseX());
				lastMouseY.setNumber(c.mouseY());
				mouseMove(c);
			} else if (entry instanceof ScrollKeybindEntry) {
				ScrollKeybindEntry c = (ScrollKeybindEntry) entry;
				scroll(c);
			}
			postDoHandle(entry);
		}
	}

	private void mouseClicked(MouseButtonKeybindEntry entry) throws GameException {
		int id = entry.getKeybind().getUniqueId();
		if (mouseButtons.contains(id)) {
			handle(entry.withType(MouseButtonKeybindEntry.Type.RELEASE));
		}
		mouseButtons.add(id);
		Collection<Gui> guis = new ArrayList<>();
		AtomicBoolean hasFoundFocusedGui = new AtomicBoolean(false);
		float mouseX = entry.mouseX();
		float mouseY = entry.mouseY();
		doForGUIs(gui -> {
			if (!hasFoundFocusedGui.get()) {
				if (gui.isHovering(mouseX, mouseY)) {
					if (gui == focusedGui.get()) {
						if (!gui.isFocused()) {
							gui.focus();
						}
						hasFoundFocusedGui.set(true);
						gui.handle(entry);
						guis.add(gui);
					} else if (!gui.isFocused()) {
						if (focusedGui.get() != null) {
							focusedGui.get().unfocus();
							if (focusedGui.get().isFocused()) {
								return;
							}
						}
						gui.focus();
						if (gui.isFocused()) {
							hasFoundFocusedGui.set(true);
							focusedGui.set(gui);
							gui.handle(entry);
							guis.add(gui);
						}
					}
				}
			}
		});
		if (!hasFoundFocusedGui.get() && focusedGui.get() != null) {
			focusedGui.get().unfocus();
			if (!focusedGui.get().isFocused()) {
				focusedGui.set(null);
			}
		}
		mouseDownGuis.put(id, guis);
	}

	private void mouseReleased(MouseButtonKeybindEntry entry) throws GameException {
		int id = entry.getKeybind().getUniqueId();
		if (mouseButtons.contains(id)) {
			mouseButtons.remove(id);
			Collection<Gui> guis = mouseDownGuis.remove(id);
			if (guis != null) {
				for (Gui gui : guis) {
					gui.handle(entry);
				}
			}
		}
	}

	private void mouseMove(MouseMoveKeybindEntry entry) throws GameException {
		float mouseX = entry.mouseX();
		float mouseY = entry.mouseY();
		doForGUIs(gui -> {
			gui.handle(entry);
		}, gui -> gui.isHovering(mouseX, mouseY));
	}

	private void scroll(ScrollKeybindEntry entry) throws GameException {
		AtomicBoolean done = new AtomicBoolean();
		doForGUIs(gui -> {
			if (done.compareAndSet(false, true)) {
				gui.handle(entry);
			}
		}, gui -> gui.isHovering(lastMouseX.floatValue(), lastMouseY.floatValue()));
	}

	private void forFocused(KeybindEntry e) throws GameException {
		doForGUIs(gui -> {
			if (gui.isFocused()) {
				gui.handle(e);
			}
		});
	}

	@SuppressWarnings("unused")
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		return true;
	}

	@SuppressWarnings("unused")
	protected void postDoHandle(KeybindEntry entry) throws GameException {
	}

	@SuppressWarnings("unused")
	protected void doUpdate() throws GameException {
	}

	@SuppressWarnings("unused")
	protected void doCleanup(Window window) throws GameException {
	}

	@SuppressWarnings("unused")
	protected void doInit(Window window) throws GameException {
	}

	@SuppressWarnings("unused")
	protected void preRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
	}

	@SuppressWarnings("unused")
	protected void postRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
	}

	@SuppressWarnings("unused")
	protected boolean doRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
		return true;
	}

	protected final void doForGUIs(GameConsumer<Gui> cons) throws GameException {
		doForGUIs(cons, t -> true);
	}

	protected final void doForGUIs(GameConsumer<Gui> cons, GamePredicate<Gui> pred) throws GameException {
		doForGUIs(cons, pred, Gui.class);
	}

	protected final <V> void doForGUIs(GameConsumer<V> cons, Class<V> clazz) throws GameException {
		doForGUIs(cons, v -> true, clazz);
	}

	protected final <V> void doForGUIs(GameConsumer<V> cons, GamePredicate<V> pred, Class<V> clazz)
			throws GameException {
		for (Gui gui : GUIs) {
			try {
				if (!clazz.isAssignableFrom(gui.getClass())) {
					continue;
				}
				V v = clazz.cast(gui);
				if (pred.test(v)) {
					cons.accept(v);
				}
			} catch (GameException ex) {
				throw ex;
			}
		}
	}
}
