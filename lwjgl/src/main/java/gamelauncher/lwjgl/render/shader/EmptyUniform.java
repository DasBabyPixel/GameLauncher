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
	public Uniform upload() {
		return this;
	}

	@Override
	public Uniform set(float f1) {
		return this;
	}

	@Override
	public Uniform set(float f1, float f2) {
		return this;
	}

	@Override
	public Uniform set(float f1, float f2, float f3) {
		return this;
	}

	@Override
	public Uniform set(float f1, float f2, float f3, float f4) {
		return this;
	}

	@Override
	public Uniform set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13,
			float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		return this;
	}

	@Override
	public Uniform set(Matrix4f m) {
		return this;
	}

	@Override
	public Uniform set(Vector2f vec) {
		return this;
	}

	@Override
	public Uniform set(Vector3f vec) {
		return this;
	}

	@Override
	public Uniform set(Vector4f vec) {
		return this;
	}

	@Override
	public Uniform set(ProgramObject object) {
		return this;
	}

	@Override
	public Uniform clear() {
		return this;
	}

	@Override
	public Uniform set(int i) {
		return this;
	}
}
