package gamelauncher.lwjgl.gui.impl;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	private DrawContext hud;

	private Model model;

	private Model text;

	private Camera camera;

	private Font font;

	private final LWJGLGameLauncher launcher;

	/**
	 * @param launcher
	 */
	public LWJGLMainScreenGui(LWJGLGameLauncher launcher) {
		super(launcher);
		this.launcher = launcher;
	}

	GameItem textItem;

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		System.out.println("Initialized");

		camera = new BasicCamera();
		hud = getLauncher().createContext(framebuffer);
		hud.setProgram(getLauncher().getShaderLoader()
				.loadShader(getLauncher(), getLauncher().getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
		hud.setProjection(new Transformations.Projection.Projection2D());
//		model = launcher.getModelLoader()
//				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
//		GameItem gi = new GameItem(model);
//		gi.setPosition(300, 300, 0);
//		gi.setScale(500);
//		model = gi.createModel();
		LWJGLTexture texture = Threads.waitFor(launcher.getTextureManager().createTexture());
		Threads.waitFor(texture.uploadAsync(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("pixel64x64.png"))
				.newResourceStream()));
		model = new Texture2DModel(texture);
		texture.write();
		GameItem gi = new GameItem(model);
		gi.setScale(800);
		model = gi.createModel();

		font = launcher.getFontFactory()
				.createFont(launcher.getResourceLoader()
						.getResource(launcher.getEmbedFileSystem().getPath("fonts", "cinzel_regular.ttf"))
						.newResourceStream());
		text = launcher.getGlyphProvider().loadStaticModel(font, "abcdefghijkl", 300);
		gi = new GameItem(text);
		gi.setPosition(100, 50, 0);
		text = gi.createModel();
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
		hud.update(camera);
//		textItem.setRotation(0, textItem.getRotation().y + 0.3F, 0);
		hud.drawModel(model);
		hud.drawModel(text);
		hud.getProgram().clearUniforms();
		return true;
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		System.out.println("Cleaned up");
//		model.cleanup();
		text.cleanup();
		font.cleanup();
		hud.getProgram().cleanup();
		hud.cleanup();
	}

//	Threads.waitFor(launcher.getAsyncUploader().submit(() -> {
//	ByteBuffer buf = memAlloc(3 * 3 * Integer.BYTES);
//	buf.putInt(0x112233cc);
//	buf.putInt(0x0);
//	buf.putInt(0x112233cc);
//	buf.putInt(0x0);
//	buf.putInt(0x112233cc);
//	buf.putInt(0x0);
//	buf.putInt(0x112233cc);
//	buf.putInt(0x0);
//	buf.putInt(0x11223399);
//	buf.flip();
//
//	int tex0 = glGenTextures();
//	GlStates.current().bindTexture(GL_TEXTURE_2D, tex0);
//	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 3, 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
//	GlStates.current().bindTexture(GL_TEXTURE_2D, 0);
//
//	int tex1 = glGenTextures();
//	GlStates.current().bindTexture(GL_TEXTURE_2D, tex1);
//	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 6, 6, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
//	GlStates.current().bindTexture(GL_TEXTURE_2D, 0);
//
//	glCopyImageSubData(tex0, GL_TEXTURE_2D, 0, 0, 0, 0, tex1, GL_TEXTURE_2D, 0, 0, 0, 0, 3, 3, 1);
//
//	try {
//		ImageIO.write(getBufferedImage(tex0, 3, 3), "png", new File("tex0.png"));
//		ImageIO.write(getBufferedImage(tex1, 6, 6), "png", new File("tex1.png"));
//	} catch (Exception ex) {
//		ex.printStackTrace();
//	}
//	glDeleteTextures(tex0);
//	glDeleteTextures(tex1);
//
//}));
}
