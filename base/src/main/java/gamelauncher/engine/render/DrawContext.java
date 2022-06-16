package gamelauncher.engine.render;

import java.nio.file.Path;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;

public interface DrawContext {

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz) throws GameException;

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) throws GameException;

	void drawModel(Model model, double x, double y, double z) throws GameException;

	void setProjection(Transformations.Projection projection) throws GameException;
	
	DrawContext withProjection(Transformations.Projection projection) throws GameException;
	
	DrawContext duplicate() throws GameException;
	
	Transformations.Projection getProjection();
	
	DrawContext withProgram(GameLauncher launcher, Path path) throws GameException;
	
	void reloadProjectionMatrix() throws GameException;
	
	void update(Camera camera) throws GameException;

	DrawContext translate(double x, double y, double z) throws GameException;

	DrawContext scale(double x, double y, double z) throws GameException;
	
	void cleanup() throws GameException;

}
