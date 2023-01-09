package gamelauncher.lwjgl.gui.launcher;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.TextureGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

/**
 * @author DasBabyPixel
 */
public class LWJGLTextureGui extends ParentableAbstractGui implements TextureGui {

	private final LWJGLTexture texture;
	private final Camera camera;
	private GameItemModel model;
	private DrawContext hud;

	public LWJGLTextureGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);
		this.camera = EmptyCamera.instance();
		this.texture = launcher.getTextureManager().createTexture();
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		this.getTexture().cleanup();
		this.model.cleanup();
		this.getLauncher().getContextProvider().freeContext(this.hud, ContextType.HUD);
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		this.hud =
				this.getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
		Texture2DModel t2d = new Texture2DModel(texture);
		GameItem item = new GameItem(t2d);

		item.position().x.bind(this.getXProperty().add(item.scale().x.divide(2)));
		item.position().y.bind(this.getYProperty().add(item.scale().y.divide(2)));
		item.scale().x.bind(this.getWidthProperty());
		item.scale().y.bind(this.getHeightProperty());

		this.model = item.createModel();
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
		this.hud.update(this.camera);
		this.hud.drawModel(this.model);
		this.hud.getProgram().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	public Texture getTexture() {
		return this.texture;
	}

}
