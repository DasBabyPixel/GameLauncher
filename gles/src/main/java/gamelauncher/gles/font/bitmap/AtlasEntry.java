package gamelauncher.gles.font.bitmap;

import gamelauncher.gles.texture.GLESTexture;
import org.joml.Vector4i;

public class AtlasEntry {

	public final GlyphEntry entry;
	public final Vector4i bounds;
	public volatile GLESTexture texture;

	public AtlasEntry(GLESTexture texture, GlyphEntry entry, Vector4i bounds) {
		this.texture = texture;
		this.entry = entry;
		this.bounds = bounds;
	}
}
