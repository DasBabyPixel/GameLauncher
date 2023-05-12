package gamelauncher.engine.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEvent;
import gamelauncher.engine.util.profiler.Profiler;

/**
 * @author DasBabyPixel
 */
public class GuiRenderer extends Renderer {

    private final GameLauncher launcher;

    private final Profiler profiler;

    private volatile Listener listener = null;

    public GuiRenderer(GameLauncher launcher) {
        this.launcher = launcher;
        this.profiler = this.launcher.profiler();
    }

    public GameLauncher launcher() {
        return launcher;
    }

    @Override public void render() throws GameException {
        profiler.begin("render", "render_window");
        Gui gui = launcher.guiManager().currentGui();
        Listener l = listener;
        float partial = launcher.gameThread().partialTick();
        if (gui != null) {
            gui.render(l.mx, l.my, partial);
        }
        profiler.end();
    }

    @Override public void init() throws GameException {
        profiler.begin("render", "init_window");
        this.listener = new Listener();
        launcher.eventManager().registerListener(listener);
        profiler.end();
    }

    @Override public void cleanup() throws GameException {
        profiler.begin("render", "cleanup_window");
        Listener l = listener;
        listener = null;
        launcher.eventManager().unregisterListener(l);
        profiler.end();
    }

    private static class Listener {

        private float mx;

        private float my;

        @EventHandler public void handle(KeybindEntryEvent event) {
            KeybindEvent e = event.entry();
            if (e instanceof MouseMoveKeybindEvent) {
                MouseMoveKeybindEvent m = (MouseMoveKeybindEvent) e;
                mx = m.mouseX();
                my = m.mouseY();
            }
        }

    }

}
