package gamelauncher.engine.gui;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEvent;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGui implements Gui {

    private final NumberValue x = NumberValue.zero();
    private final NumberValue y = NumberValue.zero();
    private final NumberValue w = NumberValue.zero();
    private final NumberValue h = NumberValue.zero();
    private final NumberValue visibleX = NumberValue.zero();
    private final NumberValue visibleY = NumberValue.zero();
    private final NumberValue visibleW = NumberValue.zero();
    private final NumberValue visibleH = NumberValue.zero();
    private final GameLauncher launcher;
    private final BooleanValue focused = BooleanValue.falseValue();

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

    @Override
    public void init(Framebuffer framebuffer) throws GameException {

    }

    @Override
    public void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
            throws GameException {

    }

    @Override
    public void cleanup(Framebuffer framebuffer) throws GameException {

    }

    @Override
    public void onClose() throws GameException {

    }

    @Override
    public void onOpen() throws GameException {

    }

    @Override
    public void focus() throws GameException {
        focused.setValue(true);
    }

    @Override
    public void unfocus() throws GameException {
        focused.setValue(false);
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
        return String.format("%s[x=%s,y=%s,w=%s,h=%s]", cname, x.intValue(), y.intValue(),
                w.intValue(), h.intValue());
    }

}
