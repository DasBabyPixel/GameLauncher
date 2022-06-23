package gamelauncher.engine.render;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;

public interface DrawContext {

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz) throws GameException;

	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) throws GameException;

	void drawModel(Model model, double x, double y, double z) throws GameException;

	void drawModel(Model model) throws GameException;

	DrawContext withProjection(Transformations.Projection projection) throws GameException;
	
	DrawContext withProgram(ShaderProgram program) throws GameException;

	Transformations.Projection getProjection();
	
	ShaderProgram getProgram();
	
	void setProgram(ShaderProgram program) throws GameException;

	void setProjection(Transformations.Projection projection) throws GameException;

	DrawContext translate(double x, double y, double z) throws GameException;

	DrawContext scale(double x, double y, double z) throws GameException;

	void update(Camera camera) throws GameException;

	void cleanup() throws GameException;

	DrawContext duplicate() throws GameException;

	void reloadProjectionMatrix() throws GameException;

}
