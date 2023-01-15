package gamelauncher.engine.render;

/**
 * @author DasBabyPixel
 */
public interface Camera {

	/**
	 * Sets the position of the camera
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	void setPosition(float x, float y, float z);

	/**
	 * Increases the position by the given amounts
	 * 
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 */
	void movePosition(float offsetX, float offsetY, float offsetZ);

	/**
	 * @return x position
	 */
	float x();

	/**
	 * @return y position
	 */
	float y();

	/**
	 * @return z position
	 */
	float z();

	/**
	 * @return x rotation
	 */
	float rotX();

	/**
	 * @return y position
	 */
	float rotY();

	/**
	 * @return z position
	 */
	float rotZ();

	/**
	 * Sets the rotation of the camera
	 * 
	 * @param rx
	 * @param ry
	 * @param rz
	 */
	void rotation(float rx, float ry, float rz);

	/**
	 * Sets the x rotation
	 * 
	 * @param rx
	 */
	void rotX(float rx);

	/**
	 * Sets the y position
	 * 
	 * @param ry
	 */
	void rotY(float ry);

	/**
	 * Sets the z position
	 * 
	 * @param rz
	 */
	void rotZ(float rz);

	/**
	 * Increases the rotation by the given amounts
	 * 
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 */
	void moveRotation(float offsetX, float offsetY, float offsetZ);

}
