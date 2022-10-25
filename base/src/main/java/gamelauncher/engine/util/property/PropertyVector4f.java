package gamelauncher.engine.util.property;

import java.util.Objects;

import org.joml.Vector4f;

import de.dasbabypixel.api.property.NumberValue;

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
		this.x = NumberValue.zero();
		this.y = NumberValue.zero();
		this.z = NumberValue.zero();
		this.w = NumberValue.zero();
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		PropertyVector4f other = (PropertyVector4f) obj;
		return Objects.equals(this.w.getNumber(), other.w.getNumber()) && Objects.equals(this.x.getNumber(), other.x.getNumber())
				&& Objects.equals(this.y.getNumber(), other.y.getNumber())
				&& Objects.equals(this.z.getNumber(), other.z.getNumber());
	}

}
