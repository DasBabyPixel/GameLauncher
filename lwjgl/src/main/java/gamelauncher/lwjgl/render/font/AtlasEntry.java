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

		NumberValue z1 = NumberValue.constant(10);
		NumberValue z2 = NumberValue.constant(10);
		NumberValue z3 = z1.add(z2);

		z1.setValue(10);
		z1.setValue(11);
		z1.setValue(12);
		z1.setValue(13);

		z1.bind(z2);

		z3.getNumber();
		z3.getNumber();

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
