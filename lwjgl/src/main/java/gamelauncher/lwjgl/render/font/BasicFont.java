package gamelauncher.lwjgl.render.font;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class BasicFont implements Font {

	private final ByteBuffer data;

	public BasicFont(ResourceStream stream) throws GameException {
		this(stream.readAllBytes());
		stream.cleanup();
	}
	
	public BasicFont(byte[] b) {
		data = memAlloc(b.length);
		data.put(b).flip();
	}

	@Override
	public ByteBuffer data() {
		return data;
	}

	@Override
	public void cleanup() {
		memFree(data);
	}
}
