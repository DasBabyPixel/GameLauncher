package gamelauncher.engine.gui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.model.Model;
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

	private DrawContext hud;
	private Model model;

	/**
	 * @param launcher
	 */
	public GuiRenderer(GameLauncher launcher) {
		this.launcher = launcher;
		this.guiManager = launcher.getGuiManager();
	}

	@SuppressWarnings("javadoc")
	public GameLauncher getLauncher() {
		return launcher;
	}

	@Override
	public void init(Window window) throws GameException {
		listeners.put(window, new Listener());
		launcher.getEventManager().registerListener(listeners.get(window));

		hud = launcher.createContext(window.getFramebuffer());
		hud.setProgram(getLauncher().getShaderLoader()
				.loadShader(getLauncher(), getLauncher().getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
		hud.setProjection(new Transformations.Projection.Projection2D());
		GameLauncher launcher = getLauncher();
		model = launcher.getModelLoader()
				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
		GameItem gi = new GameItem(model);
		gi.setScale(500);
		model = new GameItem.GameItemModel(gi);
		System.out.println("Init");

	}

	@Override
	public void render(Window window) throws GameException {
		Gui gui = guiManager.getCurrentGui(window);
		Listener l = listeners.get(window);
		if (gui != null) {
			gui.render(window, l.mx, l.my, l.partialTick);
		}
		hud.update(getLauncher().getCamera());
		hud.drawModel(model);
		hud.getProgram().clearUniforms();
		System.out.println("Render");
	}

	@Override
	public void cleanup(Window window) throws GameException {
		Listener l = listeners.remove(window);
		launcher.getEventManager().unregisterListener(l);
		System.out.println("Clear");
		hud.getProgram().cleanup();
		hud.cleanup();
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
