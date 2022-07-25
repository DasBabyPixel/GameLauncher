package gamelauncher.lwjgl.gui.impl;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES32.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;

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

	@Override
	protected void doInit(Window window) throws GameException {
		System.out.println("Initialized");

		camera = new BasicCamera(() -> window.scheduleDraw());
		hud = getLauncher().createContext(window.getFramebuffer());
		hud.setProgram(getLauncher().getShaderLoader()
				.loadShader(getLauncher(), getLauncher().getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
		hud.setProjection(new Transformations.Projection.Projection2D());
		model = launcher.getModelLoader()
				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
		GameItem gi = new GameItem(model);
		gi.setScale(500);
		model = gi.createModel();
		font = launcher.getFontFactory()
				.createFont(launcher.getResourceLoader()
						.getResource(launcher.getEmbedFileSystem().getPath("fonts", "cinzel_regular.ttf"))
						.newResourceStream());
//		text = launcher.getGlyphProvider().loadStaticModel(font, "test", 100);
		Threads.waitFor(launcher.getAsyncUploader().submit(() -> {
			ByteBuffer buf = memAlloc(3 * 3 * Integer.BYTES);
			buf.putInt(0x112233cc);
			buf.putInt(0x0);
			buf.putInt(0x112233cc);
			buf.putInt(0x0);
			buf.putInt(0x112233cc);
			buf.putInt(0x0);
			buf.putInt(0x112233cc);
			buf.putInt(0x0);
			buf.putInt(0x11223399);
			buf.flip();

			int tex0 = glGenTextures();
			GlStates.current().bindTexture(GL_TEXTURE_2D, tex0);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 3, 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			GlStates.current().bindTexture(GL_TEXTURE_2D, 0);

			int tex1 = glGenTextures();
			GlStates.current().bindTexture(GL_TEXTURE_2D, tex1);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 6, 6, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
			GlStates.current().bindTexture(GL_TEXTURE_2D, 0);

			glCopyImageSubData(tex0, GL_TEXTURE_2D, 0, 0, 0, 0, tex1, GL_TEXTURE_2D, 0, 0, 0, 0, 3, 3, 1);

			try {
				ImageIO.write(getBufferedImage(tex0, 3, 3), "png", new File("tex0.png"));
				ImageIO.write(getBufferedImage(tex1, 6, 6), "png", new File("tex1.png"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			glDeleteTextures(tex0);
			glDeleteTextures(tex1);

		}));
	}

	public static BufferedImage getBufferedImage(int texture, int width, int height) {
		ByteBuffer pixels = getBufferedImageBuffer(texture, width, height);
		IntBuffer ipixels = pixels.asIntBuffer();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				img.setRGB(x, y, ipixels.get(y * width + x));
			}
		}
		memFree(pixels);
		return img;
	}

	private static ByteBuffer getBufferedImageBuffer(int texture, int width, int height) {
		ByteBuffer pixels = memCalloc(4 * width * height);
		glBindTexture(GL_TEXTURE_2D, texture);
		int fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(fbo);
		glBindTexture(GL_TEXTURE_2D, 0);
		return pixels;
	}

	@Override
	public void onOpen() throws GameException {
		System.out.println("Opened");
	}

	@Override
	protected boolean doRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
		hud.update(camera);
		hud.drawModel(model);
		hud.getProgram().clearUniforms();
		return true;
	}

	@Override
	protected void doCleanup(Window window) throws GameException {
		System.out.println("Cleaned up");
		model.cleanup();
//		text.cleanup();
		font.cleanup();
		hud.getProgram().cleanup();
		hud.cleanup();
	}
}
