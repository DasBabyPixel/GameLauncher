package gamelauncher.engine.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEntry;
import gamelauncher.engine.util.profiler.Profiler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public class GuiRenderer extends Renderer {

	private final GameLauncher launcher;

	private final Profiler profiler;

	private final Map<Framebuffer, Listener> listeners = new ConcurrentHashMap<>();

	public GuiRenderer(GameLauncher launcher) {
		this.launcher = launcher;
		this.profiler = this.launcher.getProfiler();
	}

	public GameLauncher getLauncher() {
		return launcher;
	}

	@Override
	public void render(Framebuffer framebuffer) throws GameException {
		profiler.begin("render", "render_window");
		Gui gui = launcher.getGuiManager().getCurrentGui(framebuffer);
		Listener l = listeners.get(framebuffer);
		float partial = launcher.getGameThread().getPartialTick();
		if (gui != null) {
			gui.render(framebuffer, l.mx, l.my, partial);
		}
		profiler.end();
	}

	@Override
	public void init(Framebuffer framebuffer) throws GameException {
		profiler.begin("render", "init_window");
		listeners.put(framebuffer, new Listener());
		launcher.getEventManager().registerListener(listeners.get(framebuffer));
		profiler.end();
	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {
		profiler.begin("render", "cleanup_window");
		Listener l = listeners.remove(framebuffer);
		launcher.getEventManager().unregisterListener(l);
		profiler.end();
	}

	private static class Listener {

		private float mx;

		private float my;

		@EventHandler
		public void handle(KeybindEntryEvent event) {
			KeybindEntry e = event.getEntry();
			if (e instanceof MouseMoveKeybindEntry m) {
				mx = m.mouseX();
				my = m.mouseY();
			}
		}

	}

}
