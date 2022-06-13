package gamelauncher.lwjgl.render.font;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.ResourceStream;

public class BasicFont implements Font {

	private final ByteBuffer data;

	public BasicFont(ResourceStream stream) throws GameException {
		byte[] b = stream.readAllBytes();
		stream.cleanup();
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
