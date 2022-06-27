package gamelauncher.lwjgl.gui.impl;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	private DrawContext hud;
	private Model model;

	/**
	 * @param launcher
	 */
	public LWJGLMainScreenGui(GameLauncher launcher) {
		super(launcher);
	}

	@Override
	protected void doInit(Window window) throws GameException {
		System.out.println("Initialized");
		hud = window.getContext().duplicate();
		hud.setProgram(getLauncher().getShaderLoader()
				.loadShader(getLauncher(), getLauncher().getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
		hud.setProjection(new Transformations.Projection.Projection2D());
		GameLauncher launcher = getLauncher();
		model = launcher.getModelLoader()
				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
		GameItem gi = new GameItem(model);
		gi.setScale(500);
		model = new GameItem.GameItemModel(gi);
	}
	
	@Override
	public void onOpen() throws GameException {
		System.out.println("Opened");
	}

	@Override
	protected boolean doRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
		System.out.println("Rendered MainScreenGui");
		hud.update(getLauncher().getCamera());
		hud.drawModel(model);
		hud.getProgram().clearUniforms();
		return true;
	}

	@Override
	protected void doCleanup(Window window) throws GameException {
		System.out.println("Cleaned up");
		hud.getProgram().cleanup();
		hud.cleanup();
	}
}
