package gamelauncher.lwjgl.render.font;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourceStream;

public class Font {

	private final ByteBuffer data;

	public Font(ResourceStream stream) throws GameException {
		byte[] b = stream.readAllBytes();
		stream.cleanup();
		data = memAlloc(b.length);
		data.put(b).flip();
	}

	public ByteBuffer data() {
		return data;
	}

	public void cleanup() {
		memFree(data);
	}
}
