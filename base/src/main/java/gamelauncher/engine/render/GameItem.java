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
		position = new PropertyVector3f(0, 0, 0);
		scale = new PropertyVector3f(1, 1, 1);
		rotation = new PropertyVector3f(0, 0, 0);
		color = new PropertyVector4f(1, 1, 1, 1);
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

	public void addColor(float r, float g, float b, float a) {
		this.addColor.set(r, g, b, a);
	}

	public void position(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	public void scale(float scale) {
		this.scale(scale, scale, scale);
	}

	public void scale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	public void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}

	public void rotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
	}

	/**
	 * @return the model
	 */
	public Model model() {
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
