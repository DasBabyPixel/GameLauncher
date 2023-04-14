package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;

/**
 * @author DasBabyPixel
 */
public class LWJGLMouseButtonKeybindEvent extends AbstractKeybindEvent implements MouseButtonKeybindEvent {

    private final float mouseX;

    private final float mouseY;

    private final Type type;

    /**
     * @param keybind
     * @param mouseX
     * @param mouseY
     * @param type
     */
    public LWJGLMouseButtonKeybindEvent(Keybind keybind, float mouseX, float mouseY, Type type) {
        super(keybind);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.type = type;
    }

    @Override
    public float mouseX() {
        return mouseX;
    }

    @Override
    public float mouseY() {
        return mouseY;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public MouseButtonKeybindEvent withType(Type type) {
        return new LWJGLMouseButtonKeybindEvent(keybind(), mouseX, mouseY, type);
    }

    @Override
    public String toString() {
        return "MouseButtonKeybindEntry [mouseX=" + mouseX + ", mouseY=" + mouseY + ", type=" + type + "]";
    }

}
