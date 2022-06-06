package gamelauncher.lwjgl.render.font;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameResource;

public class TextureAtlas implements GameResource {

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
	@SuppressWarnings("unused")
	public boolean addGlyph(int id, GlyphEntry glyph) throws GameException {
		glyphs.put(id, glyph.glyphData);
		return true;
	}
	
	@SuppressWarnings("unused")
	public void removeGlyph(int id) throws GameException {
		glyphs.remove(id);
	}
	
	public boolean isEmpty() {
		return glyphs.isEmpty();
	}

	@Override
	public void cleanup() throws GameException {
		glyphs.clear();
	}
}
