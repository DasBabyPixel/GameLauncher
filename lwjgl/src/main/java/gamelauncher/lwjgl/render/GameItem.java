package gamelauncher.lwjgl.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.util.GameResource;
import gamelauncher.lwjgl.render.model.ColorMultiplierModel;
import gamelauncher.lwjgl.render.model.MeshLikeModel;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class GameItem implements GameResource {

	protected MeshLikeModel model;
	private final Vector3f position;
	private final Vector3f scale;
	private final Vector3f rotation;
	private final Vector4f color;

	public GameItem() {
		position = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);
		rotation = new Vector3f(0, 0, 0);
		color = new Vector4f(1, 1, 1, 1);
	}

	public GameItem(Model model) throws GameException {
		this(cast(model));
	}

	public GameItem(MeshLikeModel model) {
		this();
		this.model = model;
	}

	private static MeshLikeModel cast(Model model) throws GameException {
		if (model instanceof MeshLikeModel) {
			return (MeshLikeModel) model;
		}
		throw new GameException("Can't cast model to MeshLikeModel");
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(float x, float y, float z) {
		this.scale.x = x;
		this.scale.y = y;
		this.scale.z = z;
	}

	public void setScale(float scale) {
		this.setScale(scale, scale, scale);
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector4f getColor() {
		return color;
	}

	public void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public MeshLikeModel getModel() {
		return model;
	}

	@Override
	public void cleanup() throws GameException {
		model.cleanup();
	}

	public void applyToTransformationMatrix(Matrix4f transformationMatrix) {
		transformationMatrix.translate(position.x, position.y, position.z);
		transformationMatrix.rotateX((float) Math.toRadians(-rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(-rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(-rotation.z));
		transformationMatrix.scale(scale.x, scale.y, scale.z);
	}

	public static class GameItemModel implements MeshLikeModel, ColorMultiplierModel {
		public final GameItem gameItem;

		public GameItemModel(GameItem gameItem) {
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
		public Vector4f getColor() {
			return gameItem.getColor();
		}
	}
}
