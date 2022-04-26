package gamelauncher.lwjgl.render.light;

import org.joml.Vector4f;

import gamelauncher.lwjgl.render.LWJGLTexture;

public class Material {

	public static final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);

	public Vector4f ambient;
	public Vector4f diffuse;
	public Vector4f specular;
	public float reflectance;
	public LWJGLTexture texture;

}
