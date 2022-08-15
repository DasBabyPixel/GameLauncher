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
	public float getX() {
		return position.x;
	}

	@Override
	public float getY() {
		return position.y;
	}

	@Override
	public float getZ() {
		return position.z;
	}

	@Override
	public float getRotX() {
		return rotation.x;
	}

	@Override
	public float getRotY() {
		return rotation.y;
	}

	@Override
	public float getRotZ() {
		return rotation.z;
	}

	@Override
	public void setRotation(float rx, float ry, float rz) {
		boolean change = rotation.x != rx || rotation.y != ry || rotation.z != rz;
		if (!change)
			return;
		rotation.x = rx;
		rotation.y = ry;
		rotation.z = rz;
	}

	@Override
	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		setRotation(rotation.x + offsetX, rotation.y + offsetY, rotation.z + offsetZ);
	}

	@Override
	public void setRotX(float rx) {
		rotation.x = rx;
	}

	@Override
	public void setRotY(float ry) {
		rotation.y = ry;
	}

	@Override
	public void setRotZ(float rz) {
		rotation.z = rz;
	}

}
