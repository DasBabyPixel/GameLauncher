package gamelauncher.lwjgl.render.font.deprecated;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.lwjgl.render.font.GlyphData;

@SuppressWarnings("javadoc")
@Deprecated
public class TextureAtlas implements GameResource {

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
	public CompletableFuture<Boolean> addGlyph(int id, GlyphEntry glyph) throws GameException {
		glyphs.put(id, glyph.glyphData);
		return CompletableFuture.completedFuture(true);
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
