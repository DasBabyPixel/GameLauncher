package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEvent;

/**
 * @author DasBabyPixel
 */
public class LWJGLMouseMoveKeybindEvent extends AbstractKeybindEvent implements MouseMoveKeybindEvent {

    private final float oldMouseX;
    private final float oldMouseY;
    private final float mouseX;
    private final float mouseY;

    public LWJGLMouseMoveKeybindEvent(Keybind keybind, float oldMouseX, float oldMouseY, float mouseX, float mouseY) {
        super(keybind);
        this.oldMouseX = oldMouseX;
        this.oldMouseY = oldMouseY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override public float oldMouseX() {
        return oldMouseX;
    }

    @Override public float oldMouseY() {
        return oldMouseY;
    }

    @Override public float mouseX() {
        return mouseX;
    }

    @Override public float mouseY() {
        return mouseY;
    }

    public LWJGLMouseMoveKeybindEvent withPosition(float x, float y) {
        return new LWJGLMouseMoveKeybindEvent(keybind(), oldMouseX, oldMouseY, x, y);
    }
}
