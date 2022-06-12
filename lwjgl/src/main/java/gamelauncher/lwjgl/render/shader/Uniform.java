package gamelauncher.lwjgl.render.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface Uniform {

	Uniform upload();

	Uniform set(int i);

	Uniform set(float f1);

	Uniform set(float f1, float f2);

	Uniform set(float f1, float f2, float f3);

	Uniform set(float f1, float f2, float f3, float f4);

	Uniform set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33);

	Uniform set(Matrix4f m);

	Uniform set(Vector2f vec);

	Uniform set(Vector3f vec);

	Uniform set(Vector4f vec);

	Uniform set(ProgramObject object);

	Uniform clear();

}
