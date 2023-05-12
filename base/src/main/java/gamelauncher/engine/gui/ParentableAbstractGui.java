package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.collections.Collections;
import gamelauncher.engine.util.keybind.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Gui} for having and handling sub-{@link Gui}s
 *
 * @author DasBabyPixel
 * @see ParentableAbstractGui#GUIs
 */
@Api
public abstract class ParentableAbstractGui extends AbstractGui {

    private final Deque<Gui> addGUIQueue = Collections.newConcurrentDeque();
    private final Deque<Gui> removeGUIQueue = Collections.newConcurrentDeque();
    /**
     * The {@link Gui}s of this {@link ParentableAbstractGui} object
     */
    private final Deque<Gui> GUIs = Collections.newConcurrentDeque();
    private final AtomicReference<Gui> focusedGui = new AtomicReference<>(null);
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final NumberValue lastMouseX = NumberValue.withValue(Float.NEGATIVE_INFINITY);
    private final NumberValue lastMouseY = NumberValue.withValue(Float.NEGATIVE_INFINITY);
    private final BooleanValue hovering = BooleanValue.falseValue().mapToBoolean(unused -> hovering(lastMouseX.floatValue(), lastMouseY.floatValue()));
    private final String className = this.getClass().getName();
    private final Collection<Integer> mouseButtons = ConcurrentHashMap.newKeySet();
    private final Map<Integer, Collection<Gui>> mouseDownGuis = new ConcurrentHashMap<>();

    public ParentableAbstractGui(GameLauncher launcher) {
        super(launcher);
        hovering.addDependencies(lastMouseX, lastMouseY).addDependencies(visibleXProperty(), visibleYProperty(), visibleWidthProperty(), visibleHeightProperty());
        hovering.addListener(Property::value);
    }

    public void addGUI(Gui gui) {
        addGUIQueue.offer(gui);
    }

    public void removeGUI(Gui gui) {
        removeGUIQueue.offer(gui);
    }

    public Deque<Gui> GUIs() {
        return GUIs;
    }

    @Override public final void update() throws GameException {
        Gui g;
        while ((g = addGUIQueue.poll()) != null) {
            GUIs.add(g);
            g.onOpen();
            Framebuffer fb = this.launcher().frame().framebuffer();
            fb.renderThread().submit(g::init);
        }
        while ((g = removeGUIQueue.poll()) != null) {
            GUIs.remove(g);
            if (g.focused()) g.unfocus();
            g.onClose();
            Framebuffer fb = this.launcher().frame().framebuffer();
            fb.renderThread().submit(g::cleanup);
        }

        this.doUpdate();
        for (Gui gui : GUIs) {
            gui.update();
        }
    }

    private void mouseClicked(MouseButtonKeybindEvent entry) throws GameException {
        int id = entry.keybind().uniqueId();
        if (this.mouseButtons.contains(id)) {
            this.handle(entry.withType(MouseButtonKeybindEvent.Type.RELEASE));
        }
        this.mouseButtons.add(id);
        Collection<Gui> guis = new ArrayList<>();
        boolean hasFoundFocusedGui = false;
        float mouseX = entry.mouseX();
        float mouseY = entry.mouseY();
        Iterator<Gui> it = GUIs.descendingIterator();
        while (it.hasNext()) {
            Gui gui = it.next();
            if (!hasFoundFocusedGui) {
                if (gui.hovering(mouseX, mouseY)) {
                    if (gui == this.focusedGui.get()) {
                        if (!gui.focused()) {
                            gui.focus();
                        }
                        if (!gui.focused()) {
                            return;
                        }
                        hasFoundFocusedGui = true;
                        gui.handle(entry);
                        guis.add(gui);
                    } else if (!gui.focused()) {
                        if (this.focusedGui.get() != null) {
                            this.focusedGui.get().unfocus();
                            if (this.focusedGui.get().focused()) {
                                return;
                            }
                        }
                        gui.focus();
                        if (gui.focused()) {
                            hasFoundFocusedGui = true;
                            this.focusedGui.set(gui);
                            gui.handle(entry);
                            guis.add(gui);
                        }
                    }
                }
            }
        }
        if (!hasFoundFocusedGui && this.focusedGui.get() != null) {
            this.focusedGui.get().unfocus();
            if (!this.focusedGui.get().focused()) {
                this.focusedGui.set(null);
            }
        }
        this.mouseDownGuis.put(id, guis);
    }

    private void forFocused(KeybindEvent e) throws GameException {
        Iterator<Gui> it = GUIs.descendingIterator();
        while (it.hasNext()) {
            Gui gui = it.next();
            if (!gui.focused()) continue;
            gui.handle(e);
            if (e.consumed()) break;
        }
    }

    @Api protected boolean doHandle(KeybindEvent entry) throws GameException {
        return true;
    }

    @Api protected void postDoHandle(KeybindEvent entry) throws GameException {
    }

    @Api protected void doUpdate() throws GameException {
    }

    @Api protected void doCleanup() throws GameException {
    }

    @Api protected void doInit() throws GameException {
    }

    @Api protected void preRender(float mouseX, float mouseY, float partialTick) throws GameException {
    }

    @Api protected void postRender(float mouseX, float mouseY, float partialTick) throws GameException {
    }

    @Api protected boolean doRender(float mouseX, float mouseY, float partialTick) throws GameException {
        return true;
    }

    @Api protected void redraw() {
        this.launcher().frame().framebuffer().scheduleRedraw();
    }

    @Override public BooleanValue hovering() {
        return this.hovering;
    }

    @Override public boolean initialized() {
        return this.initialized.get();
    }

    @Override public final void init() throws GameException {
        if (this.initialized.compareAndSet(false, true)) {
            this.doInit();
            Iterator<Gui> it = GUIs.descendingIterator();
            while (it.hasNext()) it.next().init();
        }
    }

    @Override public final void render(float mouseX, float mouseY, float partialTick) throws GameException {
        ScissorStack scissor = launcher().frame().framebuffer().scissorStack();
        scissor.pushScissor(this);
        try {
            this.launcher().profiler().begin("render", "Gui-" + this.className);
            this.init();
            this.preRender(mouseX, mouseY, partialTick);
            if (this.doRender(mouseX, mouseY, partialTick)) {
                for (Gui gui : GUIs) {
                    gui.render(mouseX, mouseY, partialTick);
                }
            }
            this.postRender(mouseX, mouseY, partialTick);
        } finally {
            this.launcher().profiler().end();
            scissor.popScissor();
        }
    }

    @Override public final void cleanup() throws GameException {
        if (this.initialized.compareAndSet(true, false)) {
            for (Gui gui : GUIs) {
                gui.cleanup();
            }
            this.doCleanup();
            super.cleanup();
        }
    }

    @Override public void unfocus() throws GameException {
        super.unfocus();
        Gui focusedGui = this.focusedGui.get();
        if (focusedGui != null) {
            focusedGui.unfocus();
        }
    }

    @Override public final void handle(KeybindEvent event) throws GameException {
        callKeybindHandlers(event);
        if (this.doHandle(event)) {
            if (event instanceof KeyboardKeybindEvent) {
                KeyboardKeybindEvent c = (KeyboardKeybindEvent) event;
                switch (c.type()) {
                    case HOLD:
                    case PRESS:
                    case RELEASE:
                    case REPEAT:
                    case CHARACTER:
                        this.forFocused(c);
                }
            } else if (event instanceof MouseButtonKeybindEvent) {
                MouseButtonKeybindEvent c = (MouseButtonKeybindEvent) event;
                this.lastMouseX.number(c.mouseX());
                this.lastMouseY.number(c.mouseY());
                switch (c.type()) {
                    case HOLD:
                        this.forFocused(c);
                        break;
                    case PRESS:
                        this.mouseClicked(c);
                        break;
                    case RELEASE:
                        int id = c.keybind().uniqueId();
                        if (this.mouseButtons.contains(id)) {
                            this.mouseButtons.remove(id);
                            Collection<Gui> guis = this.mouseDownGuis.remove(id);
                            if (guis != null) {
                                for (Gui gui : guis) {
                                    gui.handle(c);
                                }
                            }
                        }
                        break;
                }
            } else if (event instanceof MouseMoveKeybindEvent) {
                MouseMoveKeybindEvent c = (MouseMoveKeybindEvent) event;
                this.lastMouseX.number(c.mouseX());
                this.lastMouseY.number(c.mouseY());
                Iterator<Gui> it = GUIs.descendingIterator();
                while (it.hasNext()) {
                    Gui gui = it.next();
                    gui.handle(c);
                    if (c.consumed()) break;
                }
            } else if (event instanceof ScrollKeybindEvent) {
                ScrollKeybindEvent c = (ScrollKeybindEvent) event;
                Iterator<Gui> it = GUIs.descendingIterator();
                while (it.hasNext()) {
                    Gui gui = it.next();
                    if (!gui.hovering(lastMouseX.floatValue(), lastMouseY.floatValue())) continue;
                    gui.handle(c);
                    if (c.consumed()) break;
                }
            }
            this.postDoHandle(event);
        }
    }

    @Override public String toString() {
        String additional = additionalToStringData();
        return String.format(Locale.getDefault(), "%s[x=%.0f, y=%.0f, w=%.0f, h=%.0f%s]", this.getClass().getSimpleName(), this.x(), this.y(), this.width(), this.height(), additional == null ? "" : " " + additional);
    }

    protected String additionalToStringData() {
        return null;
    }

}
