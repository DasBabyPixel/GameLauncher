package gamelauncher.lwjgl.render.light;

import org.joml.Vector3f;

public class DirectionalLight {

	public Vector3f color;
	public Vector3f direction;
	public float intensity;

	public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
		this.color = color;
		this.direction = direction;
		this.intensity = intensity;
	}

	public DirectionalLight(DirectionalLight other) {
		this(new Vector3f(other.color), new Vector3f(other.direction), other.intensity);
	}

}
