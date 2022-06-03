package gamelauncher.lwjgl.render.font;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TextureAtlas {

	public final Lock lock = new ReentrantLock(true);
	protected final Map<Integer, GlyphData> glyphs = new ConcurrentHashMap<>();

	public boolean hasGlyph(int id) {
		return glyphs.containsKey(id);
	}

	/**
	 * 
	 * @param id
	 * @param glyph
	 * @return true if success, false if cant add glyph e.g. font atlas is full
	 */
	public boolean addGlyph(int id, GlyphEntry glyph) {
		glyphs.put(id, glyph.glyphData);
		return true;
	}
	
	public void removeGlyph(int id) {
		glyphs.remove(id);
	}
	
	public boolean isEmpty() {
		return glyphs.isEmpty();
	}
}
