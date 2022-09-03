package gamelauncher.engine.util.property;

import java.util.Objects;

import org.joml.Vector4f;

import de.dasbabypixel.api.property.NumberValue;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class PropertyVector4f {

	public final NumberValue x;

	public final NumberValue y;

	public final NumberValue z;

	public final NumberValue w;

	/**
	 * 
	 */
	public PropertyVector4f() {
		x = NumberValue.zero();
		y = NumberValue.zero();
		z = NumberValue.zero();
		w = NumberValue.zero();
	}

	public PropertyVector4f(float x, float y, float z, float w) {
		this();
		this.x.setNumber(x);
		this.y.setNumber(y);
		this.z.setNumber(z);
		this.w.setNumber(w);
	}

	public PropertyVector4f set(float x, float y, float z, float w) {
		this.x.setNumber(x);
		this.y.setNumber(y);
		this.z.setNumber(z);
		this.w.setNumber(w);
		return this;
	}

	public PropertyVector4f set(PropertyVector4f other) {
		this.x.setNumber(other.x.getNumber());
		this.y.setNumber(other.y.getNumber());
		this.z.setNumber(other.z.getNumber());
		this.w.setNumber(other.w.getNumber());
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
		return w;
	}

	/**
	 * @return x
	 */
	public NumberValue getX() {
		return x;
	}

	/**
	 * @return y
	 */
	public NumberValue getY() {
		return y;
	}

	/**
	 * @return z
	 */
	public NumberValue getZ() {
		return z;
	}

	/**
	 * @return a new vector4f
	 */
	public Vector4f toVector4f() {
		return toVector4f(new Vector4f());
	}

	/**
	 * @param dest
	 * @return the vector4f
	 */
	public Vector4f toVector4f(Vector4f dest) {
		dest.set(x.floatValue(), y.floatValue(), z.floatValue(), w.floatValue());
		return dest;
	}

	@Override
	public int hashCode() {
		return Objects.hash(w, x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyVector4f other = (PropertyVector4f) obj;
		return Objects.equals(w.getNumber(), other.w.getNumber()) && Objects.equals(x.getNumber(), other.x.getNumber())
				&& Objects.equals(y.getNumber(), other.y.getNumber())
				&& Objects.equals(z.getNumber(), other.z.getNumber());
	}

}
