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

/**
 * @author DasBabyPixel
 */
public class GameItem implements GameResource {

	protected Model model;
	private final Vector3f position;
	private final Vector3f scale;
	private final Vector3f rotation;
	private final Vector4f color;
	private final Vector4f addColor;

	/**
	 * 
	 */
	private GameItem() {
		position = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);
		rotation = new Vector3f(0, 0, 0);
		color = new Vector4f(1, 1, 1, 1);
		addColor = new Vector4f(0, 0, 0, 0);
	}

	/**
	 * @param model
	 */
	public GameItem(Model model) {
		this();
		this.model = model;
	}

	/**
	 * @return the position vector
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @return the addColor
	 */
	public Vector4f getAddColor() {
		return addColor;
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
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	/**
	 * @return the scale vector
	 */
	public Vector3f getScale() {
		return scale;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setScale(float x, float y, float z) {
		this.scale.x = x;
		this.scale.y = y;
		this.scale.z = z;
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
		return rotation;
	}

	/**
	 * @return the color vector
	 */
	public Vector4f getColor() {
		return color;
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
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
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
		transformationMatrix.translate(position.x, position.y, position.z);
		transformationMatrix.rotateX((float) Math.toRadians(-rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(-rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(-rotation.z));
		transformationMatrix.scale(scale.x, scale.y, scale.z);
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
