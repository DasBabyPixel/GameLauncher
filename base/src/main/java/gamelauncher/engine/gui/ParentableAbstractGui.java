package gamelauncher.engine.gui;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.GameException.Stack;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.function.GamePredicate;
import gamelauncher.engine.util.keybind.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Gui} for having and handling sub-{@link Gui}s
 *
 * @author DasBabyPixel
 * @see ParentableAbstractGui#GUIs
 */
public abstract class ParentableAbstractGui extends AbstractGui {

	/**
	 * The {@link Gui}s of this {@link ParentableAbstractGui} object
	 */
	public final Collection<Gui> GUIs = new ConcurrentLinkedDeque<>();
	private final AtomicReference<Gui> focusedGui = new AtomicReference<>(null);
	private final AtomicBoolean initialized = new AtomicBoolean();
	private final NumberValue lastMouseX = NumberValue.zero();
	private final NumberValue lastMouseY = NumberValue.zero();
	private final BooleanValue hovering = BooleanValue.falseValue()
			.mapToBoolean(unused -> isHovering(lastMouseX.floatValue(), lastMouseY.floatValue()));
	private final String className = this.getClass().getName();
	protected Framebuffer framebuffer;
	private Collection<Integer> mouseButtons = ConcurrentHashMap.newKeySet();
	private Map<Integer, Collection<Gui>> mouseDownGuis = new ConcurrentHashMap<>();

	public ParentableAbstractGui(GameLauncher launcher) {
		super(launcher);
		hovering.addDependencies(lastMouseX, lastMouseY)
				.addDependencies(getVisibleXProperty(), getVisibleYProperty(),
						getVisibleWidthProperty(), getVisibleHeightProperty());
		hovering.addListener(Property::getValue);
	}

	private void mouseClicked(MouseButtonKeybindEntry entry) throws GameException {
		int id = entry.getKeybind().getUniqueId();
		if (this.mouseButtons.contains(id)) {
			this.handle(entry.withType(MouseButtonKeybindEntry.Type.RELEASE));
		}
		this.mouseButtons.add(id);
		Collection<Gui> guis = new ArrayList<>();
		AtomicBoolean hasFoundFocusedGui = new AtomicBoolean(false);
		float mouseX = entry.mouseX();
		float mouseY = entry.mouseY();
		this.doForGUIs(gui -> {
			if (!hasFoundFocusedGui.get()) {
				if (gui.isHovering(mouseX, mouseY)) {
					if (gui == this.focusedGui.get()) {
						if (!gui.isFocused()) {
							gui.focus();
						}
						if (!gui.isFocused()) {
							return;
						}
						hasFoundFocusedGui.set(true);
						gui.handle(entry);
						guis.add(gui);
					} else if (!gui.isFocused()) {
						if (this.focusedGui.get() != null) {
							this.focusedGui.get().unfocus();
							if (this.focusedGui.get().isFocused()) {
								return;
							}
						}
						gui.focus();
						if (gui.isFocused()) {
							hasFoundFocusedGui.set(true);
							this.focusedGui.set(gui);
							gui.handle(entry);
							guis.add(gui);
						}
					}
				}
			}
		});
		if (!hasFoundFocusedGui.get() && this.focusedGui.get() != null) {
			this.focusedGui.get().unfocus();
			if (!this.focusedGui.get().isFocused()) {
				this.focusedGui.set(null);
			}
		}
		this.mouseDownGuis.put(id, guis);
	}

	private void mouseReleased(MouseButtonKeybindEntry entry) throws GameException {
		int id = entry.getKeybind().getUniqueId();
		if (this.mouseButtons.contains(id)) {
			this.mouseButtons.remove(id);
			Collection<Gui> guis = this.mouseDownGuis.remove(id);
			if (guis != null) {
				for (Gui gui : guis) {
					gui.handle(entry);
				}
			}
		}
	}

	private void mouseMove(MouseMoveKeybindEntry entry) throws GameException {
		this.doForGUIs(gui -> gui.handle(entry));
	}

	private void scroll(ScrollKeybindEntry entry) throws GameException {
		AtomicBoolean done = new AtomicBoolean();
		this.doForGUIs(gui -> {
			if (done.compareAndSet(false, true)) {
				gui.handle(entry);
			}
		}, gui -> gui.isHovering(this.lastMouseX.floatValue(), this.lastMouseY.floatValue()));
	}

	private void forFocused(KeybindEntry e) throws GameException {
		this.doForGUIs(gui -> gui.handle(e), Gui::isFocused);
	}

	protected boolean doHandle(KeybindEntry entry) throws GameException {
		return true;
	}

	protected void postDoHandle(KeybindEntry entry) throws GameException {
	}

	protected void doUpdate() throws GameException {
	}

	protected void doCleanup(Framebuffer framebuffer) throws GameException {
	}

	protected void doInit(Framebuffer framebuffer) throws GameException {
	}

	protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
	}

	protected void postRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
	}

	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
		return true;
	}

	protected final void doForGUIs(GameConsumer<Gui> cons) throws GameException {
		this.doForGUIs(cons, t -> true);
	}

	protected final void doForGUIs(GameConsumer<Gui> cons, GamePredicate<Gui> pred)
			throws GameException {
		this.doForGUIs(cons, pred, Gui.class);
	}

	protected final <V> void doForGUIs(GameConsumer<V> cons, Class<V> clazz) throws GameException {
		this.doForGUIs(cons, v -> true, clazz);
	}

	protected final <V> void doForGUIs(GameConsumer<V> cons, GamePredicate<V> pred, Class<V> clazz)
			throws GameException {
		GameException.Stack stack = new Stack();
		for (Gui gui : this.GUIs) {
			try {
				if (!clazz.isAssignableFrom(gui.getClass())) {
					continue;
				}
				V v = clazz.cast(gui);
				if (pred.test(v)) {
					cons.accept(v);
				}
			} catch (GameException ex) {
				stack.add(ex);
			}
		}
		stack.work();
	}

	protected void redraw() {
		if (this.framebuffer != null)
			this.framebuffer.scheduleRedraw();
	}

	@Override
	public BooleanValue hovering() {
		return this.hovering;
	}

	@Override
	public final void init(Framebuffer framebuffer) throws GameException {
		if (this.initialized.compareAndSet(false, true)) {
			this.framebuffer = framebuffer;
			this.doInit(framebuffer);
			this.doForGUIs(gui -> gui.init(framebuffer));
		}
	}

	@Override
	public final void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		ScissorStack scissor = framebuffer.scissorStack();
		scissor.pushScissor(this);
		try {
			this.getLauncher().getProfiler().begin("render", "Gui-" + this.className);
			this.init(framebuffer);
			this.preRender(framebuffer, mouseX, mouseY, partialTick);
			if (this.doRender(framebuffer, mouseX, mouseY, partialTick)) {
				this.doForGUIs(gui -> gui.render(framebuffer, mouseX, mouseY, partialTick));
			}
			this.postRender(framebuffer, mouseX, mouseY, partialTick);
		} finally {
			this.getLauncher().getProfiler().end();
			scissor.popScissor();
		}
	}

	@Override
	public final void cleanup(Framebuffer framebuffer) throws GameException {
		if (this.initialized.compareAndSet(true, false)) {
			this.doForGUIs(gui -> gui.cleanup(framebuffer));
			this.doCleanup(framebuffer);
			super.cleanup(framebuffer);
			this.framebuffer = null;
		}
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
	public final void update() throws GameException {
		this.doForGUIs(Gui::update);
		this.doUpdate();
	}

	@Override
	public final void handle(KeybindEntry entry) throws GameException {
		if (this.doHandle(entry)) {
			if (entry instanceof KeyboardKeybindEntry c) {
				switch (c.type()) {
					case HOLD, PRESS, RELEASE, REPEAT, CHARACTER -> this.forFocused(c);
				}
			} else if (entry instanceof MouseButtonKeybindEntry c) {
				this.lastMouseX.setNumber(c.mouseX());
				this.lastMouseY.setNumber(c.mouseY());
				switch (c.type()) {
					case HOLD -> this.forFocused(c);
					case PRESS -> this.mouseClicked(c);
					case RELEASE -> this.mouseReleased(c);
				}
			} else if (entry instanceof MouseMoveKeybindEntry c) {
				this.lastMouseX.setNumber(c.mouseX());
				this.lastMouseY.setNumber(c.mouseY());
				this.mouseMove(c);
			} else if (entry instanceof ScrollKeybindEntry c) {
				this.scroll(c);
			}
			this.postDoHandle(entry);
		}
	}

	@Override
	public String toString() {
		String additional = additionalToStringData();
		return String.format("%s[x=%.0f, y=%.0f, w=%.0f, h=%.0f%s]",
				this.getClass().getSimpleName(), this.getX(), this.getY(), this.getWidth(),
				this.getHeight(), additional == null ? "" : " " + additional);
	}

	protected String additionalToStringData() {
		return null;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized.get();
	}

}
