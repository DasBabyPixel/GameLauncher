package gamelauncher.lwjgl.render.font;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class GlyphKey {

	public final float scale;

	public final int codepoint;

	public final AtomicInteger required;

	public GlyphKey(float scale, int codepoint) {
		this.scale = scale;
		this.codepoint = codepoint;
		this.required = new AtomicInteger();
	}

	@Override
	public int hashCode() {
		return Objects.hash(codepoint, scale);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlyphKey other = (GlyphKey) obj;
		return codepoint == other.codepoint && Float.floatToIntBits(scale) == Float.floatToIntBits(other.scale);
	}

}
