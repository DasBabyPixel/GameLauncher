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
	 * @throws GameException 
	 */
	public LWJGLTextureGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);
		this.camera = new BasicCamera();
		this.future = launcher.getTextureManager().createTexture();
		this.future.thenAccept(tex -> {
			this.texture = tex;
			Texture2DModel t2d = new Texture2DModel(tex);
			GameItem item = new GameItem(t2d);

			item.position().x.bind(this.getXProperty().add(item.scale().x.divide(2)));
			item.position().y.bind(this.getYProperty().add(item.scale().y.divide(2)));
			item.scale().x.bind(this.getWidthProperty());
			item.scale().y.bind(this.getHeightProperty());

			this.model = item.createModel();
		});
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		this.hud = this.getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		this.hud.update(this.camera);
		this.hud.drawModel(this.model);
		this.hud.getProgram().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		this.getTexture().cleanup();
		this.model.cleanup();
		this.getLauncher().getContextProvider().freeContext(this.hud, ContextType.HUD);
		super.doCleanup(framebuffer);
	}

	@Override
	public Texture getTexture() throws GameException {
		if (this.texture == null) {
			this.texture = Threads.waitFor(this.future);
		}
		return this.texture;
	}

}
