package gamelauncher.engine.render.shader;

import gamelauncher.engine.resource.GameResource;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author DasBabyPixel
 */
public interface Uniform extends GameResource {

    /**
     * Uploads this uniform
     *
     * @return this
     */
    Uniform upload();

    /**
     * @param i
     * @return this
     */
    Uniform set(int i);

    /**
     * @param f1
     * @return this
     */
    Uniform set(float f1);

    /**
     * @param f1
     * @param f2
     * @return this
     */
    Uniform set(float f1, float f2);

    /**
     * @param f1
     * @param f2
     * @param f3
     * @return this
     */
    Uniform set(float f1, float f2, float f3);

    /**
     * @param f1
     * @param f2
     * @param f3
     * @param f4
     * @return this
     */
    Uniform set(float f1, float f2, float f3, float f4);

    /**
     * @param m00
     * @param m01
     * @param m02
     * @param m03
     * @param m10
     * @param m11
     * @param m12
     * @param m13
     * @param m20
     * @param m21
     * @param m22
     * @param m23
     * @param m30
     * @param m31
     * @param m32
     * @param m33
     * @return this
     */
    Uniform set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
                float m21, float m22, float m23, float m30, float m31, float m32, float m33);

    /**
     * @param m
     * @return this
     */
    Uniform set(Matrix4f m);

    /**
     * @param vec
     * @return this
     */
    Uniform set(Vector2f vec);

    /**
     * @param vec
     * @return this
     */
    Uniform set(Vector3f vec);

    /**
     * @param vec
     * @return this
     */
    Uniform set(Vector4f vec);

    /**
     * @param object
     * @return this
     */
    Uniform set(ProgramObject object);

    /**
     * Clears the value of this uniform
     *
     * @return this
     */
    Uniform clear();

}
