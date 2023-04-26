package gamelauncher.engine.util.property;

import de.dasbabypixel.api.property.NumberValue;
import org.joml.Vector3f;

public class PropertyVector3f {

    public final NumberValue x;

    public final NumberValue y;

    public final NumberValue z;

    /**
     *
     */
    public PropertyVector3f() {
        this.x = NumberValue.withValue(0F);
        this.y = NumberValue.withValue(0F);
        this.z = NumberValue.withValue(0F);
    }

    public PropertyVector3f(float x, float y, float z) {
        this();
        this.x.number(x);
        this.y.number(y);
        this.z.number(z);
    }

    public PropertyVector3f set(float x, float y, float z) {
        this.x.number(x);
        this.y.number(y);
        this.z.number(z);
        return this;
    }

    /**
     * @return x
     */
    public NumberValue getX() {
        return this.x;
    }

    /**
     * @return y
     */
    public NumberValue getY() {
        return this.y;
    }

    /**
     * @return z
     */
    public NumberValue getZ() {
        return this.z;
    }

    /**
     * @return a new vector3f
     */
    public Vector3f toVector3f() {
        return this.toVector3f(new Vector3f());
    }

    /**
     * @param dest
     * @return the vector3f
     */
    public Vector3f toVector3f(Vector3f dest) {
        dest.set(this.x.floatValue(), this.y.floatValue(), this.z.floatValue());
        return dest;
    }

    @Override public String toString() {
        return "PropertyVector3f{" + "x=" + x.floatValue() + ", y=" + y.floatValue() + ", z=" + z.floatValue() + '}';
    }
}
