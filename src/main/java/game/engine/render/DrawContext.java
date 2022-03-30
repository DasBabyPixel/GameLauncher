package game.engine.render;

import game.util.*;

public interface DrawContext {

	void drawRect(double x, double y, double w, double h, Color color);

	DrawContext translate(double x, double y, double z);
	
}
