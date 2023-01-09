package gamelauncher.lwjgl.render.font;

import gamelauncher.lwjgl.render.texture.LWJGLTexture;

import java.awt.*;

class AtlasEntry {

	final GlyphEntry entry;
	final Rectangle bounds;
	volatile LWJGLTexture texture;

	public AtlasEntry(LWJGLTexture texture, GlyphEntry entry, Rectangle bounds) {
		this.texture = texture;
		this.entry = entry;
		this.bounds = bounds;
	}
}
