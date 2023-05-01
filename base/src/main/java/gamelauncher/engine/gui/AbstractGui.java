package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGui implements Gui {

    private final NumberValue x = NumberValue.withValue(0D);
    private final NumberValue y = NumberValue.withValue(0D);
    private final NumberValue w = NumberValue.withValue(0D);
    private final NumberValue h = NumberValue.withValue(0D);
    private final NumberValue visibleX = NumberValue.withValue(0D);
    private final NumberValue visibleY = NumberValue.withValue(0D);
    private final NumberValue visibleW = NumberValue.withValue(0D);
    private final NumberValue visibleH = NumberValue.withValue(0D);
    private final GameLauncher launcher;
    private final BooleanValue focused = BooleanValue.falseValue();
    private final Map<Class<?>, List<Consumer<KeybindEvent>>> keybindHandlers = new ConcurrentHashMap<>();
    private final List<Class<?>> registeredHandlers = new CopyOnWriteArrayList<>();

    public AbstractGui(GameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public NumberValue widthProperty() {
        return w;
    }

    @Override
    public NumberValue heightProperty() {
        return h;
    }

    @Override
    public NumberValue xProperty() {
        return x;
    }

    @Override
    public NumberValue yProperty() {
        return y;
    }

    @Override
    public NumberValue visibleXProperty() {
        return visibleX;
    }

    @Override
    public NumberValue visibleYProperty() {
        return visibleY;
    }

    @Override
    public NumberValue visibleWidthProperty() {
        return visibleW;
    }

    @Override
    public NumberValue visibleHeightProperty() {
        return visibleH;
    }

    @Override
    public BooleanValue focusedProperty() {
        return focused;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KeybindEvent> void registerKeybindHandler(Class<T> clazz, Consumer<T> eventConsumer) {
        keybindHandlers.compute(clazz, (aClass, consumers) -> {
            if (consumers == null) consumers = new CopyOnWriteArrayList<>();
            consumers.add((Consumer<KeybindEvent>) eventConsumer);
            registeredHandlers.add(aClass);
            return consumers;
        });
    }

    @Override
    public <T extends KeybindEvent> void unregisterKeybindHandler(Class<T> clazz, Consumer<T> eventConsumer) {
        keybindHandlers.computeIfPresent(clazz, (aClass, consumers) -> {
            consumers.remove(eventConsumer);
            if (consumers.isEmpty()) {
                registeredHandlers.remove(aClass);
                return null;
            }
            return consumers;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KeybindEvent> Collection<Consumer<? super T>> keybindHandlers(Class<T> clazz) {
        Collection<Consumer<? super T>> col = new ArrayList<>();
        for (Map.Entry<Class<?>, List<Consumer<KeybindEvent>>> entry : keybindHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                col.add((Consumer<? super T>) entry.getValue());
            }
        }
        return col;
    }

    protected void callKeybindHandlers(KeybindEvent event) {
        for (int i = 0; i < registeredHandlers.size(); i++) {
            Class<?> clazz = registeredHandlers.get(i);
            if (clazz.isInstance(event)) {
                List<Consumer<KeybindEvent>> l = keybindHandlers.get(clazz);
                if (l != null) {
                    for (int i1 = 0; i1 < l.size(); i1++) {
                        l.get(i1).accept(event);
                    }
                }
            }
        }
    }

    @Override
    public void init(Framebuffer framebuffer) throws GameException {
    }

    @Override
    public void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
    }

    @Override
    public void cleanup(Framebuffer framebuffer) throws GameException {
    }

    @Api
    @Override
    public void onClose() throws GameException {
    }

    @Api
    @Override
    public void onOpen() throws GameException {
    }

    @Api
    @Override
    public void focus() throws GameException {
        focused.value(true);
    }

    @Override
    public void unfocus() throws GameException {
        focused.value(false);
    }

    @Override
    public void update() throws GameException {

    }

    @Override
    public void handle(KeybindEvent entry) throws GameException {

    }

    @Override
    public GameLauncher launcher() {
        return launcher;
    }

    @Override
    public String toString() {
        String cname = getClass().getSimpleName();
        return String.format("%s[x=%s,y=%s,w=%s,h=%s]", cname, x.intValue(), y.intValue(), w.intValue(), h.intValue());
    }
}
