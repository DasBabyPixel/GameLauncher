package game.level;

import java.util.*;

import game.render.*;
import game.util.*;

public class Tile implements Drawable {

	public static final double WIDTH = 10, HEIGHT = 10;

	public final int x, y;
	public final List<TileAttribute> attributes = new ArrayList<>();

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(DrawContext context) {
		context.drawRect(-WIDTH / 2, -HEIGHT / 2, WIDTH / 2, HEIGHT / 2, Color.white);
		for (TileAttribute attribute : attributes) {
			attribute.draw(context);
		}
	}
}
