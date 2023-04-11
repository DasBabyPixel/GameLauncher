package gamelauncher.lwjgl.gui.launcher;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.TextureGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.model.Texture2DModel;
import gamelauncher.gles.texture.GLESTexture;
import gamelauncher.lwjgl.LWJGLGameLauncher;

/**
 * @author DasBabyPixel
 */
public class LWJGLTextureGui extends ParentableAbstractGui implements TextureGui {

	private final GLESTexture texture;
	private final Camera camera;
	private GameItemModel model;
	private DrawContext hud;

	public LWJGLTextureGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);
		this.camera = EmptyCamera.instance();
		this.texture = launcher.textureManager().createTexture();
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		this.texture().cleanup();
		this.model.cleanup();
		this.launcher().contextProvider().freeContext(this.hud, ContextType.HUD);
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		this.hud =
				this.launcher().contextProvider().loadContext(framebuffer, ContextType.HUD);
		Texture2DModel t2d = new Texture2DModel(texture);
		GameItem item = new GameItem(t2d);

		item.position().x.bind(this.xProperty().add(item.scale().x.divide(2)));
		item.position().y.bind(this.yProperty().add(item.scale().y.divide(2)));
		item.scale().x.bind(this.widthProperty());
		item.scale().y.bind(this.heightProperty());

		this.model = item.createModel();
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
		this.hud.update(this.camera);
		this.hud.drawModel(this.model);
		this.hud.program().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	public Texture texture() {
		return this.texture;
	}

}
