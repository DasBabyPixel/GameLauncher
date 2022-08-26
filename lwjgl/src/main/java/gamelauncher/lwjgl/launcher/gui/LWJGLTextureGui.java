package gamelauncher.lwjgl.launcher.gui;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.TextureGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

/**
 * @author DasBabyPixel
 */
public class LWJGLTextureGui extends ParentableAbstractGui implements TextureGui {

	private Texture texture;

	private volatile GameItemModel model;

	private final CompletableFuture<LWJGLTexture> future;

	private DrawContext hud;

	private Camera camera;

	/**
	 * @param launcher
	 */
	public LWJGLTextureGui(LWJGLGameLauncher launcher) {
		super(launcher);
		camera = new BasicCamera();
		future = launcher.getTextureManager().createTexture();
		future.thenAccept(tex -> {
			texture = tex;
			Texture2DModel t2d = new Texture2DModel(tex);
			GameItem item = new GameItem(t2d);

			item.position().x.bind(getXProperty().add(item.scale().x.divide(2)));
			item.position().y.bind(getYProperty().add(item.scale().y.divide(2)));
			item.scale().x.bind(getWidthProperty());
			item.scale().y.bind(getHeightProperty());

			model = item.createModel();
		});
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		hud = getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		hud.update(camera);
		hud.drawModel(model);
		hud.getProgram().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		getTexture().cleanup();
		model.cleanup();
		getLauncher().getContextProvider().freeContext(hud, ContextType.HUD);
		super.doCleanup(framebuffer);
	}

	@Override
	public Texture getTexture() {
		if (texture == null) {
			texture = Threads.waitFor(future);
		}
		return texture;
	}

}
