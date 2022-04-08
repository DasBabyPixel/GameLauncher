package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourcePath;
import gamelauncher.engine.resource.ResourceStream;

public class LWJGLTexture {

	private final int textureId;

	public LWJGLTexture(ResourcePath pngResource) throws GameException {
		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		ResourceStream stream = pngResource.newResourceStream();
		PNGDecoder decoder = stream.newPNGDecoder();
		ByteBuffer buf = memAlloc(4 * decoder.getWidth() * decoder.getHeight());
		try {
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
		buf.flip();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buf);
		memFree(buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		stream.cleanup();
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getTextureId() {
		return textureId;
	}
}
