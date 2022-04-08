package gamelauncher.engine.render;

public interface Camera {

	void setPosition(float x, float y, float z);
	
	void movePosition(float offsetX, float offsetY, float offsetZ);
	
	float getX();
	
	float getY();
	
	float getZ();
	
	float getRotX();
	
	float getRotY();
	
	float getRotZ();
	
	void setRotation(float rx, float ry, float rz);
	
	void moveRotation(float offsetX, float offsetY, float offsetZ);
	
}
