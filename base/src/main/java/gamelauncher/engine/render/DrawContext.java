package gamelauncher.engine.render;

import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface DrawContext extends GameResource {

	/**
	 * Draws a {@link Model} at a position with a rotation
	 * 
	 * @param model
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @throws GameException
	 */
	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz) throws GameException;

	/**
	 * Draws a {@link Model} at a position with a rotation and scale
	 * 
	 * @param model
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param sx
	 * @param sy
	 * @param sz
	 * @throws GameException
	 */
	void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) throws GameException;

	/**
	 * Draws a {@link Model} at a position
	 * 
	 * @param model
	 * @param x
	 * @param y
	 * @param z
	 * @throws GameException
	 */
	void drawModel(Model model, double x, double y, double z) throws GameException;

	/**
	 * Draws a model
	 * 
	 * @param model
	 * @throws GameException
	 */
	void drawModel(Model model) throws GameException;

	/**
	 * Creates a new {@link DrawContext} with the given {@link Projection}
	 * 
	 * @param projection
	 * @return the new {@link DrawContext}
	 * @throws GameException
	 */
	DrawContext withProjection(Transformations.Projection projection) throws GameException;

	/**
	 * Creates a new {@link DrawContext} with the given {@link ShaderProgram}
	 * 
	 * @param program
	 * @return the new {@link DrawContext}
	 * @throws GameException
	 */
	DrawContext withProgram(ShaderProgram program) throws GameException;

	/**
	 * @return the {@link Projection} of this {@link DrawContext}
	 */
	Transformations.Projection getProjection();

	/**
	 * @return the {@link ShaderProgram} of this {@link DrawContext}
	 */
	ShaderProgram getProgram();

	/**
	 * Sets the {@link ShaderProgram} of this {@link DrawContext}
	 * 
	 * @param program
	 * @throws GameException
	 */
	void setProgram(ShaderProgram program) throws GameException;

	/**
	 * Sets the {@link Projection} of this {@link DrawContext}
	 * 
	 * @param projection
	 * @throws GameException
	 */
	void setProjection(Transformations.Projection projection) throws GameException;

	/**
	 * Creates a new {@link DrawContext} with the given translation
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the new {@link DrawContext}
	 * @throws GameException
	 */
	DrawContext translate(double x, double y, double z) throws GameException;

	/**
	 * Creates a new {@link DrawContext} with the given scale
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the new {@link DrawContext}
	 * @throws GameException
	 */
	DrawContext scale(double x, double y, double z) throws GameException;

	/**
	 * Updates the values of this {@link DrawContext}. Should be called before
	 * 
	 * @param camera
	 * @throws GameException
	 */
	void update(Camera camera) throws GameException;

	/**
	 * @return a duplicate of this {@link DrawContext}
	 * @throws GameException
	 */
	DrawContext duplicate() throws GameException;

	/**
	 * Reloads the projection matrix. Should not be neccessary to call.
	 * 
	 * @throws GameException
	 */
	void reloadProjectionMatrix() throws GameException;

}
