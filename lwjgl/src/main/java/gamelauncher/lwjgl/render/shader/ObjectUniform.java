package gamelauncher.lwjgl.render.shader;

import java.util.concurrent.atomic.AtomicReference;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ObjectUniform implements Uniform {

	private final AtomicReference<ProgramObject> value = new AtomicReference<>();
	private final ShaderProgram program;

	public ObjectUniform(ShaderProgram program) {
		this.program = program;
	}

	@Override
	public void upload() {
		ProgramObject object = this.value.get();
		if (object == null) {
			return;
		}
		object.upload(program);
	}

	@Override
	public void set(ProgramObject object) {
		this.value.set(object);
	}

	@Override
	public void set(float f1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(float f1, float f2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(float f1, float f2, float f3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(float f1, float f2, float f3, float f4) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Matrix4f m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Vector2f vec) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Vector3f vec) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Vector4f vec) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.value.set(null);
	}
}
