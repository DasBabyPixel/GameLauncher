package gamelauncher.engine.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gamelauncher.engine.render.model.ColorAddModel;
import gamelauncher.engine.render.model.ColorMultiplierModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.property.PropertyVector3f;
import gamelauncher.engine.util.property.PropertyVector4f;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class GameItem implements GameResource {

	protected Model model;

	private final Vector3f cposition;

	private final Vector3f cscale;

	private final Vector3f crotation;

	private final Vector4f ccolor;

	private final Vector4f caddColor;

	private final PropertyVector3f position;

	private final PropertyVector3f scale;

	private final PropertyVector3f rotation;

	private final PropertyVector4f color;

	private final PropertyVector4f addColor;

	/**
	 * 
	 */
	private GameItem() {
		cposition = new Vector3f();
		position = new PropertyVector3f(0, 0, 0);
		cscale = new Vector3f();
		scale = new PropertyVector3f(1, 1, 1);
		crotation = new Vector3f();
		rotation = new PropertyVector3f(0, 0, 0);
		ccolor = new Vector4f();
		color = new PropertyVector4f(1, 1, 1, 1);
		caddColor = new Vector4f();
		addColor = new PropertyVector4f(0, 0, 0, 0);
	}

	/**
	 * @param model
	 */
	public GameItem(Model model) {
		this();
		this.model = model;
	}
	
	public PropertyVector3f position() {
		return position;
	}
	
	public PropertyVector4f addColor() {
		return addColor;
	}
	
	public PropertyVector4f color() {
		return color;
	}
	
	public PropertyVector3f rotation() {
		return rotation;
	}
	
	public PropertyVector3f scale() {
		return scale;
	}
	
	/**
	 * @return the position vector
	 */
	public Vector3f getPosition() {
		return position.toVector3f(cposition);
	}

	/**
	 * @return the addColor
	 */
	public Vector4f getAddColor() {
		return addColor.toVector4f(caddColor);
	}

	/**
	 * Sets the addColor
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setAddColor(float r, float g, float b, float a) {
		this.addColor.set(r, g, b, a);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	/**
	 * @return the scale vector
	 */
	public Vector3f getScale() {
		return cscale;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	/**
	 * @param scale
	 */
	public void setScale(float scale) {
		this.setScale(scale, scale, scale);
	}

	/**
	 * @return the rotation vector
	 */
	public Vector3f getRotation() {
		return rotation.toVector3f(crotation);
	}

	/**
	 * @return the color vector
	 */
	public Vector4f getColor() {
		return color.toVector4f(ccolor);
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	@Override
	public void cleanup() throws GameException {
		model.cleanup();
	}

	/**
	 * @return the {@link GameItemModel} for this {@link GameItem}
	 */
	public GameItemModel createModel() {
		return new GameItemModel(this);
	}

	/**
	 * @param transformationMatrix
	 */
	public void applyToTransformationMatrix(Matrix4f transformationMatrix) {
		transformationMatrix.translate(position.x.floatValue(), position.y.floatValue(), position.z.floatValue());
		transformationMatrix.rotateX((float) Math.toRadians(-rotation.x.floatValue()));
		transformationMatrix.rotateY((float) Math.toRadians(-rotation.y.floatValue()));
		transformationMatrix.rotateZ((float) Math.toRadians(-rotation.z.floatValue()));
		transformationMatrix.scale(scale.x.floatValue(), scale.y.floatValue(), scale.z.floatValue());
	}

	/**
	 * @author DasBabyPixel
	 */
	public static final class GameItemModel implements ColorMultiplierModel, ColorAddModel {

		/**
		 */
		public final GameItem gameItem;

		/**
		 * @param gameItem
		 */
		private GameItemModel(GameItem gameItem) {
			this.gameItem = gameItem;
		}

		@Override
		public void cleanup() throws GameException {
			gameItem.cleanup();
		}

		@Override
		public void render(ShaderProgram program) throws GameException {
			gameItem.model.render(program);
		}

		@Override
		public Vector4f getAddColor() {
			return gameItem.getAddColor();
		}

		@Override
		public Vector4f getColor() {
			return gameItem.getColor();
		}

	}

}
