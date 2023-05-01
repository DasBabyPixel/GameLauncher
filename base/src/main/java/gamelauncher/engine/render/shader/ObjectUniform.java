package gamelauncher.engine.render.shader;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DasBabyPixel
 */
public class ObjectUniform extends AbstractGameResource implements Uniform {

    private final AtomicReference<ProgramObject> value = new AtomicReference<>();
    private final ShaderProgram program;
    private final String name;

    public ObjectUniform(ShaderProgram program, String name) {
        this.program = program;
        this.name = name;
    }

    @Override
    public boolean cleanedUp() {
        return true;
    }

    @Override
    protected void cleanup0() throws GameException {
    }

    @Override
    public Uniform upload() {
        ProgramObject object = this.value.get();
        if (object == null) {
            return this;
        }
        object.upload(program, name);
        return this;
    }

    @Override
    public Uniform set(ProgramObject object) {
        this.value.set(object);
        return this;
    }

    @Override
    public Uniform set(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(float f1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(float f1, float f2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(float f1, float f2, float f3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(float f1, float f2, float f3, float f4) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(Matrix4f m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(Vector2f vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(Vector3f vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uniform set(Vector4f vec) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean empty() {
        return false;
    }

    @Override
    public Uniform clear() {
        this.value.set(null);
        return this;
    }
}
