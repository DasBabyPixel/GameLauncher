package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameResource;

public class LWJGLTexture implements GameResource {

	private final int textureId;
	public int width;
	public int height;

	public LWJGLTexture(ResourceStream stream) throws GameException {
		textureId = glGenTextures();
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		PNGDecoder decoder = stream.newPNGDecoder();
		ByteBuffer buf = memAlloc(4 * decoder.getWidth() * decoder.getHeight());
		try {
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
		buf.flip();
		this.width = decoder.getWidth();
		this.height = decoder.getHeight();

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buf);
		memFree(buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		stream.cleanup();
		unbind();
	}

	public LWJGLTexture(BufferedImage img) {
		textureId = glGenTextures();
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		ByteBuffer buf = memAlloc(4 * img.getWidth() * img.getHeight());
		int[] rgb = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
		buf.asIntBuffer().put(rgb);
		buf.flip();
		this.width = img.getWidth();
		this.height = img.getHeight();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		memFree(buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		unbind();
	}

	public LWJGLTexture() {
		this(glGenTextures());
	}

	public LWJGLTexture(int textureId) {
		this.textureId = textureId;
	}

	public BufferedImage getBufferedImage() {
		ByteBuffer pixels = getBufferedImageBuffer();
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

	public ByteBuffer getBufferedImageBuffer() {
		ByteBuffer pixels = memAlloc(4 * width * height);
		bind();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		return pixels;
	}

	public void allocate(int width, int height) {
		bind();
		this.width = width;
		this.height = height;
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	}

	public void bind() {
		GlStates.bindTexture(GL_TEXTURE_2D, textureId);
	}

	public void unbind() {
		GlStates.bindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void cleanup() throws GameException {
		GlStates.deleteTextures(textureId);
//		glDeleteTextures(textureId);
	}

	public int getTextureId() {
		return textureId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(textureId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LWJGLTexture other = (LWJGLTexture) obj;
		return textureId == other.textureId;
	}
}
