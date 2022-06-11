package gamelauncher.lwjgl.render.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface Uniform {

	public void upload();

	public void set(float f1);

	public void set(float f1, float f2);

	public void set(float f1, float f2, float f3);

	public void set(float f1, float f2, float f3, float f4);

	public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33);

	public void set(Matrix4f m);

	public void set(Vector2f vec);

	public void set(Vector3f vec);

	public void set(Vector4f vec);
	
	public void set(ProgramObject object);
	
	public void clear();

}
