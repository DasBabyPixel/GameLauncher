package gamelauncher.lwjgl.render.font.deprecated;

import java.nio.ByteBuffer;

import gamelauncher.engine.render.font.Font;
import gamelauncher.lwjgl.render.font.GlyphData;
import gamelauncher.lwjgl.render.font.deprecated.BasicGlyphProvider.GlyphKey;

@SuppressWarnings("javadoc")
@Deprecated
public class GlyphEntry {

	public final GlyphKey key;
	public final Font font;
	public final int glyphIndex;
	public final int pixelHeight;
	public final GlyphData glyphData;
	public final int codepoint;
	public final ByteBuffer abuffer;

	public GlyphEntry(GlyphKey key, Font font, int glyphIndex, int pixelHeight, GlyphData glyphData, int codepoint,
			ByteBuffer abuffer) {
		super();
		this.key = key;
		this.font = font;
		this.glyphIndex = glyphIndex;
		this.pixelHeight = pixelHeight;
		this.glyphData = glyphData;
		this.codepoint = codepoint;
		this.abuffer = abuffer;
	}
}
