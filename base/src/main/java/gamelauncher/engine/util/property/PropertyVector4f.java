package gamelauncher.engine.util.property;

import de.dasbabypixel.api.property.NumberValue;
import org.joml.Vector4f;

import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public class PropertyVector4f {

    public final NumberValue x;
    public final NumberValue y;
    public final NumberValue z;
    public final NumberValue w;

    /**
     *
     */
    public PropertyVector4f() {
        this.x = NumberValue.withValue(0F);
        this.y = NumberValue.withValue(0F);
        this.z = NumberValue.withValue(0F);
        this.w = NumberValue.withValue(0F);
    }

    public PropertyVector4f(float x, float y, float z, float w) {
        this();
        this.x.number(x);
        this.y.number(y);
        this.z.number(z);
        this.w.number(w);
    }

    public PropertyVector4f set(float x, float y, float z, float w) {
        this.x.number(x);
        this.y.number(y);
        this.z.number(z);
        this.w.number(w);
        return this;
    }

    public PropertyVector4f set(PropertyVector4f other) {
        this.x.number(other.x.doubleValue());
        this.y.number(other.y.doubleValue());
        this.z.number(other.z.doubleValue());
        this.w.number(other.w.doubleValue());
        return this;
    }

    public void bind(PropertyVector4f other) {
        this.x.bind(other.x);
        this.y.bind(other.y);
        this.z.bind(other.z);
        this.w.bind(other.w);
    }

    public PropertyVector4f unbind() {
        this.x.unbind();
        this.y.unbind();
        this.z.unbind();
        this.w.unbind();
        return this;
    }

    /**
     * @return w
     */
    public NumberValue getW() {
        return this.w;
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
     * @return a new vector4f
     */
    public Vector4f toVector4f() {
        return this.toVector4f(new Vector4f());
    }

    /**
     * @param dest
     * @return the vector4f
     */
    public Vector4f toVector4f(Vector4f dest) {
        dest.set(this.x.floatValue(), this.y.floatValue(), this.z.floatValue(), this.w.floatValue());
        return dest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.w, this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        PropertyVector4f other = (PropertyVector4f) obj;
        return Double.compare(this.w.doubleValue(), other.w.doubleValue()) == 0 && Double.compare(this.x.doubleValue(), other.x.doubleValue()) == 0 && Double.compare(this.y.doubleValue(), other.y.doubleValue()) == 0 && Double.compare(this.z.doubleValue(), other.z.doubleValue()) == 0;
    }

}
