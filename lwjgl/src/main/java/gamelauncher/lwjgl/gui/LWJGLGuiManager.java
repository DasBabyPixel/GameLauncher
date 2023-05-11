package gamelauncher.lwjgl.gui;

import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.gui.SimpleGuiManager;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEvent;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.util.keybind.LWJGLMouseMoveKeybindEvent;

/**
 * @author DasBabyPixel
 */
public class LWJGLGuiManager extends SimpleGuiManager {

    private final LWJGLGameLauncher launcher;

    public LWJGLGuiManager(LWJGLGameLauncher launcher) {
        super(launcher);
        this.launcher = launcher;
    }

    @Override protected boolean shouldHandle(KeybindEntryEvent event) {
        if (!launcher.frame().mouse().isInWindow()) {
            if (event.entry() instanceof MouseMoveKeybindEvent) {
                if (((MouseMoveKeybindEvent) event.entry()).mouseX() == Float.NEGATIVE_INFINITY) return true;
                super.handle(new KeybindEntryEvent(((LWJGLMouseMoveKeybindEvent) event.entry()).withPosition(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)));
                return false;
            }
        }
        return super.shouldHandle(event);
    }
}
