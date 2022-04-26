package gamelauncher.lwjgl.render.light;

import org.joml.Vector3f;

public class PointLight {

	public Vector3f position;
	public Vector3f color;
	public float intensity;
	public Attenuation att;

	public PointLight() {
	}
	
	public PointLight(PointLight light) {
		this(new Vector3f(light.position), new Vector3f(light.color), light.intensity,
				new PointLight.Attenuation(light.att));
	}

	public PointLight(Vector3f position, Vector3f color, float intensity, Attenuation att) {
		this.position = position;
		this.color = color;
		this.intensity = intensity;
		this.att = att;
	}

	public static class Attenuation {
		public float constant;
		public float linear;
		public float exponent;

		public Attenuation() {
		}

		public Attenuation(Attenuation att) {
			this(att.constant, att.linear, att.exponent);
		}

		public Attenuation(float constant, float linear, float exponent) {
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}
	}
}
