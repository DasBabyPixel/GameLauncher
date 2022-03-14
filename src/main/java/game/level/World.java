package game.level;

import java.lang.reflect.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import game.render.*;
import game.util.*;

public class World implements Serializable, Drawable {

	public static final int MAX_WIDTH = 16384, MAX_HEIGHT = 16384;
	private static final Type TILES_TYPE = new TypeToken<Map<Integer, Tile>>() {
	}.getType();

	private final double space = 2;

	public int width, height;

	private final Map<Integer, Tile> tiles = new HashMap<>();

	// Serializer Constructor
	@Deprecated
	public World() {
	}

	public World(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Tile getTile(int x, int y) {
		return tiles.get(calculateTileHash(x, y));
	}

	private int calculateTileHash(int x, int y) {
		return x + MAX_WIDTH * y;
	}

	@Override
	public void draw(DrawContext context) {
		double w = Tile.WIDTH * (width + space) + space;
		double h = Tile.HEIGHT * (height + space) + space;
		context.drawRect(-w / 2, -h / 2, w / 2, h / 2, Color.white);
		for (Tile tile : tiles.values()) {
			tile.draw(context.translate(tile.x * (Tile.WIDTH + space), tile.y * (Tile.HEIGHT + space), 0F));
		}
	}

	@Override
	public JsonElement serialize(Gson gson) {
		JsonObject o = new JsonObject();
		o.add("width", new JsonPrimitive(width));
		o.add("height", new JsonPrimitive(height));
		o.add("tiles", gson.toJsonTree(tiles, TILES_TYPE));
		return o;
	}

	@Override
	public void deserialize(Gson gson, JsonElement element) {
		JsonObject o = element.getAsJsonObject();
		width = o.get("width").getAsJsonPrimitive().getAsInt();
		height = o.get("height").getAsJsonPrimitive().getAsInt();
		tiles.clear();
		tiles.putAll(gson.fromJson(o.get("tiles"), TILES_TYPE));
	}
}
