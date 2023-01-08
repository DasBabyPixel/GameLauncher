package gamelauncher.engine.render;

import gamelauncher.engine.render.model.ColorAddModel;
import gamelauncher.engine.render.model.ColorMultiplierModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector3f;
import gamelauncher.engine.util.property.PropertyVector4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author DasBabyPixel
 */
public class GameItem extends AbstractGameResource {

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
	protected Model model;

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

	//	/**
	//	 * @return the position vector
	//	 */
	//	public Vector3f getPosition() {
	//		return position.toVector3f(cposition);
	//	}
	//
	//	/**
	//	 * @return the addColor
	//	 */
	//	public Vector4f getAddColor() {
	//		return addColor.toVector4f(caddColor);
	//	}

	public void setAddColor(float r, float g, float b, float a) {
		this.addColor.set(r, g, b, a);
	}

	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	//	/**
	//	 * @return the scale vector
	//	 */
	//	public Vector3f getScale() {
	//		return cscale;
	//	}

	public void setScale(float scale) {
		this.setScale(scale, scale, scale);
	}

	public void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	//	public Vector3f getRotation() {
	//		return rotation.toVector3f(crotation);
	//	}

	//	public Vector4f getColor() {
	//		return color.toVector4f(ccolor);
	//	}

	public void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}

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
	public void cleanup0() throws GameException {
		model.cleanup();
	}

	/**
	 * @return the {@link GameItemModel} for this {@link GameItem}
	 */
	public GameItemModel createModel() {
		return new GameItemModel(this);
	}

	public void applyToTransformationMatrix(Matrix4f transformationMatrix) {
		transformationMatrix.translate(position.x.floatValue(), position.y.floatValue(),
				position.z.floatValue());
		transformationMatrix.rotateX((float) Math.toRadians(-rotation.x.floatValue()));
		transformationMatrix.rotateY((float) Math.toRadians(-rotation.y.floatValue()));
		transformationMatrix.rotateZ((float) Math.toRadians(-rotation.z.floatValue()));
		transformationMatrix.scale(scale.x.floatValue(), scale.y.floatValue(),
				scale.z.floatValue());
	}

	/**
	 * @author DasBabyPixel
	 */
	public static final class GameItemModel extends AbstractGameResource
			implements ColorMultiplierModel, ColorAddModel {

		/**
		 *
		 */
		public final GameItem gameItem;

		private GameItemModel(GameItem gameItem) {
			this.gameItem = gameItem;
		}

		@Override
		public void cleanup0() throws GameException {
			gameItem.cleanup();
		}

		@Override
		public void render(ShaderProgram program) throws GameException {
			gameItem.model.render(program);
		}

		@Override
		public Vector4f getAddColor() {
			return gameItem.addColor().toVector4f();
		}

		@Override
		public Vector4f getColor() {
			return gameItem.color().toVector4f();
		}

		public GameItem getGameItem() {
			return gameItem;
		}

	}

}
