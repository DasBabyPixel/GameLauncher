package gamelauncher.engine.render;

import java.awt.Color;

public interface DrawContext {

	void drawRect(double x, double y, double w, double h, Color color);

	void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color);

	DrawContext translate(double x, double y, double z);

	DrawContext scale(double x, double y, double z);

}
