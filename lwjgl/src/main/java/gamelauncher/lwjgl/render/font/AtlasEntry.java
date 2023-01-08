package gamelauncher.lwjgl.render.font;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

import java.awt.*;

class AtlasEntry {

	LWJGLTexture texture;

	GlyphEntry entry;

	Rectangle bounds;

	public AtlasEntry(LWJGLTexture texture, GlyphEntry entry, Rectangle bounds) {
		this.texture = texture;
		this.entry = entry;
		this.bounds = bounds;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public GlyphEntry getEntry() {
		return entry;
	}

	public LWJGLTexture getTexture() {
		return texture;
	}

}
