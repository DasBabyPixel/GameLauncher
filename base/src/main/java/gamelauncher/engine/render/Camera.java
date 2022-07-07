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
	float getX();

	/**
	 * @return y position
	 */
	float getY();

	/**
	 * @return z position
	 */
	float getZ();

	/**
	 * @return x rotation
	 */
	float getRotX();

	/**
	 * @return y position
	 */
	float getRotY();

	/**
	 * @return z position
	 */
	float getRotZ();

	/**
	 * Sets the rotation of the camera
	 * 
	 * @param rx
	 * @param ry
	 * @param rz
	 */
	void setRotation(float rx, float ry, float rz);

	/**
	 * Sets the x rotation
	 * 
	 * @param rx
	 */
	void setRotX(float rx);

	/**
	 * Sets the y position
	 * 
	 * @param ry
	 */
	void setRotY(float ry);

	/**
	 * Sets the z position
	 * 
	 * @param rz
	 */
	void setRotZ(float rz);

	/**
	 * Increases the rotation by the given amounts
	 * 
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 */
	void moveRotation(float offsetX, float offsetY, float offsetZ);

}
