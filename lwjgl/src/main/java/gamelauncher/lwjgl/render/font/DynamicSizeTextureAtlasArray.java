package gamelauncher.lwjgl.render.font;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import gamelauncher.engine.GameException;

public class DynamicSizeTextureAtlasArray extends TextureAtlas {

	public final Collection<DynamicSizeTextureAtlas> atlases = new CopyOnWriteArrayList<>();
	public final Map<Integer, DynamicSizeTextureAtlas> map = new ConcurrentHashMap<>();
	private final Random random = new Random(163478519);

	@Override
	public boolean addGlyph(int id, GlyphEntry glyph) throws GameException {
		if (map.containsKey(id)) {
			return true;
		}
		for (DynamicSizeTextureAtlas a : atlases) {
			if (a.addGlyph(id, glyph)) {
				map.put(id, a);
				super.addGlyph(id, glyph);
				return true;
			}
		}

		DynamicSizeTextureAtlas a = new DynamicSizeTextureAtlas(8, atlas -> Integer.toString(random.nextInt()));
		if (!a.addGlyph(id, glyph))
			return false;
		atlases.add(a);
		map.put(id, a);
		super.addGlyph(id, glyph);
		return true;
	}

	@Override
	public void removeGlyph(int id) throws GameException {
		DynamicSizeTextureAtlas a = map.remove(id);
		a.removeGlyph(id);
		if (a.isEmpty()) {
			atlases.remove(a);
			a.cleanup();
		}
		super.removeGlyph(id);
	}

	@Override
	public void cleanup() throws GameException {
		for (DynamicSizeTextureAtlas atlas : atlases) {
			atlas.cleanup();
		}
	}
}
