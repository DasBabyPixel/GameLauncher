package gamelauncher.engine.render;

import org.joml.Vector3f;

/**
 * @author DasBabyPixel
 */
public class BasicCamera implements Camera {

	private final Vector3f position = new Vector3f();
	private final Vector3f rotation = new Vector3f();

	/**
	 * 
	 */
	public BasicCamera() {
	}

	@Override
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	@Override
	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		if (offsetZ != 0) {
			position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
			position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
		}
		if (offsetX != 0) {
			position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
			position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
		}
		position.y += offsetY;
	}

	@Override
	public float x() {
		return position.x;
	}

	@Override
	public float y() {
		return position.y;
	}

	@Override
	public float z() {
		return position.z;
	}

	@Override
	public float rotX() {
		return rotation.x;
	}

	@Override
	public float rotY() {
		return rotation.y;
	}

	@Override
	public float rotZ() {
		return rotation.z;
	}

	@Override
	public void rotation(float rx, float ry, float rz) {
		boolean change = rotation.x != rx || rotation.y != ry || rotation.z != rz;
		if (!change)
			return;
		rotation.x = rx;
		rotation.y = ry;
		rotation.z = rz;
	}

	@Override
	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		rotation(rotation.x + offsetX, rotation.y + offsetY, rotation.z + offsetZ);
	}

	@Override
	public void rotX(float rx) {
		rotation.x = rx;
	}

	@Override
	public void rotY(float ry) {
		rotation.y = ry;
	}

	@Override
	public void rotZ(float rz) {
		rotation.z = rz;
	}

}
