package gamelauncher.engine.gui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEntry;

/**
 * @author DasBabyPixel
 */
public class GuiRenderer extends Renderer {
	private final GameLauncher launcher;
	private final GuiManager guiManager;
	private final Map<Window, Listener> listeners = new ConcurrentHashMap<>();

	/**
	 * @param launcher
	 */
	public GuiRenderer(GameLauncher launcher) {
		this.launcher = launcher;
		this.guiManager = launcher.getGuiManager();
	}

	@Override
	public void init(Window window) throws GameException {
		listeners.put(window, new Listener());
		launcher.getEventManager().registerListener(listeners.get(window));
	}

	@Override
	public void render(Window window) throws GameException {
		Gui gui = guiManager.getCurrentGui(window);
		Listener l = listeners.get(window);
		if (gui != null) {
			gui.render(window, l.mx, l.my, l.partialTick);
		}
	}

	@Override
	public void close(Window window) throws GameException {
		Listener l = listeners.remove(window);
		launcher.getEventManager().unregisterListener(l);
	}

	private class Listener {

		private float mx;
		private float my;
		private float partialTick = 0;

		@EventHandler
		public void handle(KeybindEntryEvent event) {
			KeybindEntry e = event.getEntry();
			if (e instanceof MouseMoveKeybindEntry) {
				MouseMoveKeybindEntry m = (MouseMoveKeybindEntry) e;
				mx = m.mouseX();
				my = m.mouseY();
			}
		}
	}
}
