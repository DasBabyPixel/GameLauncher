package gamelauncher.lwjgl.render.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EmptyUniform implements Uniform {

	public static final EmptyUniform instance = new EmptyUniform();
	
	private EmptyUniform() {
	}
	
	@Override
	public void upload() {
	}

	@Override
	public void set(float f1) {
	}

	@Override
	public void set(float f1, float f2) {
	}

	@Override
	public void set(float f1, float f2, float f3) {
	}

	@Override
	public void set(float f1, float f2, float f3, float f4) {
	}

	@Override
	public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
	}

	@Override
	public void set(Matrix4f m) {
	}

	@Override
	public void set(Vector2f vec) {
	}

	@Override
	public void set(Vector3f vec) {
	}

	@Override
	public void set(Vector4f vec) {
	}

	@Override
	public void set(ProgramObject object) {
	}

	@Override
	public void clear() {
	}
}
