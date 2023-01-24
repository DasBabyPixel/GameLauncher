package gamelauncher.lwjgl.render.font;

import java.nio.ByteBuffer;

public class GlyphEntry {

	public final GlyphData data;
	public final int glyphIndex;
	public final int pixelHeight;
	public final GlyphKey key;
	public final ByteBuffer buffer;

	public GlyphEntry(GlyphData data, int glyphIndex, int pixelHeight, GlyphKey key, ByteBuffer buffer) {
		this.data = data;
		this.glyphIndex = glyphIndex;
		this.pixelHeight = pixelHeight;
		this.key = key;
		this.buffer = buffer;
	}
}
