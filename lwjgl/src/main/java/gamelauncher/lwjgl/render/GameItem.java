package gamelauncher.lwjgl.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gamelauncher.engine.render.Model;

public class GameItem {

	protected Model model;
	private final Vector3f position;
	private final Vector3f scale;
	private final Vector3f rotation;

	public GameItem() {
		position = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);
		rotation = new Vector3f(0, 0, 0);
	}

	public GameItem(Model model) {
		this();
		this.model = model;
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

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public Model getModel() {
		return model;
	}

	public void cleanup() {
		model.cleanup();
	}

	public void applyToTransformationMatrix(Matrix4f transformationMatrix) {
		transformationMatrix.translate(position.x, position.y, position.z);
		transformationMatrix.rotateX((float) Math.toRadians(-rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(-rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(-rotation.z));
		transformationMatrix.scale(scale.x, scale.y, scale.z);
	}

	public static class GameItemModel implements Model {
		public final GameItem gameItem;

		public GameItemModel(GameItem gameItem) {
			this.gameItem = gameItem;
		}

		@Override
		public void cleanup() {
			gameItem.cleanup();
		}
	}
}
