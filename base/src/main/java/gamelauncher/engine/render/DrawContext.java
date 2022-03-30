package gamelauncher.engine.render;

import java.awt.Color;

public interface DrawContext {

	void drawRect(double x, double y, double w, double h, Color color);

	DrawContext translate(double x, double y, double z);
	
}
