package gamelauncher.lwjgl.render.shader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BasicUniform implements Uniform {

	private final int id;
	private final int size;
	private final String name;
	private final Type type;
	private final ByteBuffer buffer;
	private final IntBuffer intBuffer;
	private final FloatBuffer floatBuffer;
	private final AtomicBoolean hasValue = new AtomicBoolean(false);

	public BasicUniform(String name, int id, Type type) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.size = type.size;
		this.buffer = memAlloc(this.size);
		this.intBuffer = this.buffer.asIntBuffer();
		this.floatBuffer = this.buffer.asFloatBuffer();
	}

	@Override
	public Uniform clear() {
		hasValue.set(false);
		return this;
	}

	@Override
	public Uniform upload() {
		if (!hasValue.get()) {
			return this;
		}
		hasValue.set(false);
		switch (type) {
		case FLOAT1:
			glUniform1fv(id, floatBuffer);
			break;
		case FLOAT2:
			glUniform2fv(id, floatBuffer);
			break;
		case FLOAT3:
			glUniform3fv(id, floatBuffer);
			break;
		case FLOAT4:
			glUniform4fv(id, floatBuffer);
			break;
		case INT1:
			glUniform1iv(id, intBuffer);
			break;
		case MAT4:
			glUniformMatrix4fv(id, false, floatBuffer);
			break;
		case SAMPLER2D:
			glUniform1iv(id, intBuffer);
			break;
		}
		return this;
	}

	@Override
	public Uniform set(int i) {
		this.intBuffer.put(0, i);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(ProgramObject object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uniform set(float f1) {
		this.floatBuffer.put(0, f1);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(float f1, float f2) {
		this.floatBuffer.put(0, f1).put(1, f2);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(float f1, float f2, float f3) {
		this.floatBuffer.put(0, f1).put(1, f2).put(2, f3);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(float f1, float f2, float f3, float f4) {
		this.floatBuffer.put(0, f1).put(1, f2).put(2, f3).put(3, f4);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13,
			float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		this.floatBuffer.put(0, m00)
				.put(1, m01)
				.put(2, m02)
				.put(3, m03)
				.put(4, m10)
				.put(5, m11)
				.put(6, m12)
				.put(7, m13)
				.put(8, m20)
				.put(9, m21)
				.put(10, m22)
				.put(11, m23)
				.put(12, m30)
				.put(13, m31)
				.put(14, m32)
				.put(15, m33);
		this.hasValue.set(true);
		return this;
	}

	@Override
	public Uniform set(Matrix4f m) {
		return this.set(m.m00(), m.m01(), m.m02(), m.m03(), m.m10(), m.m11(), m.m12(), m.m13(), m.m20(), m.m21(),
				m.m22(), m.m23(), m.m30(), m.m31(), m.m32(), m.m33());
	}

	@Override
	public Uniform set(Vector2f vec) {
		return this.set(vec.x, vec.y);
	}

	@Override
	public Uniform set(Vector3f vec) {
		return this.set(vec.x, vec.y, vec.z);
	}

	@Override
	public Uniform set(Vector4f vec) {
		return this.set(vec.x, vec.y, vec.z, vec.w);
	}

	public static enum Type {
		INT1("int", Integer.BYTES),
		FLOAT1("float", Float.BYTES),
		FLOAT2("vec2", 2 * Float.BYTES),
		FLOAT3("vec3", 3 * Float.BYTES),
		FLOAT4("vec4", 4 * Float.BYTES),
		MAT4("mat4", 4 * 4 * Float.BYTES),
		SAMPLER2D("sampler2D", Integer.BYTES)

		;

		private final String glName;
		private final int size;

		private Type(String name, int size) {
			this.glName = name;
			this.size = size;
		}

		public String getGlName() {
			return glName;
		}

		public int getSize() {
			return size;
		}
	}
}
