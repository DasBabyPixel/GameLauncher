package gamelauncher.engine.render;

import java.awt.Color;

import gamelauncher.engine.GameException;

public interface DrawContext {

	void drawRect(double x, double y, double w, double h, Color color) throws GameException;

	void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color)
			throws GameException;

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz) throws GameException;

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) throws GameException;

	void drawModel(Model model, double x, double y, double z) throws GameException;

	void setProjectionMatrix(Transformations.Projection projection) throws GameException;
	
	void update(Camera camera) throws GameException;

//	void setViewMatrix(Transformations.Transformation view) throws GameException;

	DrawContext translate(double x, double y, double z) throws GameException;

	DrawContext scale(double x, double y, double z) throws GameException;

}
