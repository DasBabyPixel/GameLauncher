package gamelauncher.lwjgl.render.font;

import gamelauncher.lwjgl.render.texture.LWJGLTexture;
import org.joml.Vector4i;

class AtlasEntry {

	final GlyphEntry entry;
	final Vector4i bounds;
	volatile LWJGLTexture texture;

	public AtlasEntry(LWJGLTexture texture, GlyphEntry entry, Vector4i bounds) {
		this.texture = texture;
		this.entry = entry;
		this.bounds = bounds;
	}
}
