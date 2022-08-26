package gamelauncher.engine.render;

/**
 * @author DasBabyPixel
 */
public class EmptyCamera implements Camera {

	private static final EmptyCamera instance = new EmptyCamera();

	/**
	 * @return the instance
	 */
	public static final EmptyCamera instance() {
		return instance;
	}

	private EmptyCamera() {
	}

	@Override
	public void setPosition(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public float getZ() {
		return 0;
	}

	@Override
	public float getRotX() {
		return 0;
	}

	@Override
	public float getRotY() {
		return 0;
	}

	@Override
	public float getRotZ() {
		return 0;
	}

	@Override
	public void setRotation(float rx, float ry, float rz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotX(float rx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotY(float ry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotZ(float rz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		throw new UnsupportedOperationException();
	}

}
