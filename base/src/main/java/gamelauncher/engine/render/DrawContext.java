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
     * @param model the model
     * @param x     x position
     * @param y     y position
     * @param z     z position
     * @param rx    x rotation
     * @param ry    y rotation
     * @param rz    z rotation
     * @throws GameException an exception
     */
    void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz) throws GameException;

    /**
     * Draws a {@link Model} at a position with a rotation and scale
     *
     * @param model the model
     * @param x     x position
     * @param y     y position
     * @param z     z position
     * @param rx    x rotation
     * @param ry    y rotation
     * @param rz    z rotation
     * @param sx    x scale
     * @param sy    y scale
     * @param sz    z scale
     * @throws GameException an exception
     */
    void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx, double sy, double sz) throws GameException;

    /**
     * Draws a {@link Model} at a position
     *
     * @param model the model
     * @param x     x position
     * @param y     y position
     * @param z     z position
     * @throws GameException an exception
     */
    void drawModel(Model model, double x, double y, double z) throws GameException;

    /**
     * Draws a model
     *
     * @param model the model
     * @throws GameException an exception
     */
    void drawModel(Model model) throws GameException;

    /**
     * Creates a new {@link DrawContext} with the given {@link Projection}
     *
     * @param projection the projection
     * @return the new {@link DrawContext}
     * @throws GameException an exception
     */
    DrawContext withProjection(Transformations.Projection projection) throws GameException;

    /**
     * Creates a new {@link DrawContext} with the given {@link ShaderProgram}
     *
     * @param program the program
     * @return the new {@link DrawContext}
     * @throws GameException an exception
     */
    DrawContext withProgram(ShaderProgram program) throws GameException;

    /**
     * @return the {@link Projection} of this {@link DrawContext}
     */
    Transformations.Projection projection();

    /**
     * @return the {@link ShaderProgram} of this {@link DrawContext}
     */
    ShaderProgram program();

    /**
     * Sets the {@link ShaderProgram} of this {@link DrawContext}
     *
     * @param program the program
     * @throws GameException an exception
     */
    void program(ShaderProgram program) throws GameException;

    /**
     * Sets the {@link Projection} of this {@link DrawContext}
     *
     * @param projection the projection
     * @throws GameException an exception
     */
    void projection(Transformations.Projection projection) throws GameException;

    /**
     * Creates a new {@link DrawContext} with the given translation
     *
     * @param x x translation
     * @param y y translation
     * @param z z translation
     * @return the new {@link DrawContext}
     * @throws GameException an exception
     */
    DrawContext translate(double x, double y, double z) throws GameException;

    /**
     * Creates a new {@link DrawContext} with the given scale
     *
     * @param x x scale
     * @param y y scale
     * @param z z scale
     * @return the new {@link DrawContext}
     * @throws GameException an exception
     */
    DrawContext scale(double x, double y, double z) throws GameException;

    /**
     * Updates the values of this {@link DrawContext}. Should be called before
     *
     * @param camera a camera
     * @throws GameException an exception
     */
    void update(Camera camera) throws GameException;

    /**
     * @return a duplicate of this {@link DrawContext}
     * @throws GameException an exception
     */
    DrawContext duplicate() throws GameException;

    /**
     * Reloads the projection matrix. Should not be neccessary to call.
     *
     * @throws GameException an exception
     */
    void reloadProjectionMatrix() throws GameException;

}
