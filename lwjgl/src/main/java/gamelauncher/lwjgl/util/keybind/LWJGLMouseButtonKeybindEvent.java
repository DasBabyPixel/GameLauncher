package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;

/**
 * @author DasBabyPixel
 */
public class LWJGLMouseButtonKeybindEvent extends AbstractKeybindEvent implements MouseButtonKeybindEvent {

    private final int buttonId;
    private final float mouseX;
    private final float mouseY;
    private final Type type;

    public LWJGLMouseButtonKeybindEvent(Keybind keybind, int buttonId, float mouseX, float mouseY, Type type) {
        super(keybind);
        this.buttonId = buttonId;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.type = type;
    }

    @Override public float mouseX() {
        return mouseX;
    }

    @Override public float mouseY() {
        return mouseY;
    }

    @Override public Type type() {
        return type;
    }

    @Override public int buttonId() {
        return buttonId;
    }

    @Override public MouseButtonKeybindEvent withType(Type type) {
        return new LWJGLMouseButtonKeybindEvent(keybind(), buttonId, mouseX, mouseY, type);
    }

    @Override public String toString() {
        return "MouseButtonKeybindEntry [mouseX=" + mouseX + ", mouseY=" + mouseY + ", type=" + type + "]";
    }

}
