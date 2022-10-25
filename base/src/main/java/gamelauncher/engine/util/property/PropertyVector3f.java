package gamelauncher.engine.util.property;

import org.joml.Vector3f;

import de.dasbabypixel.api.property.NumberValue;

public class PropertyVector3f {

	public final NumberValue x;

	public final NumberValue y;

	public final NumberValue z;

	/**
	 * 
	 */
	public PropertyVector3f() {
		this.x = NumberValue.zero();
		this.y = NumberValue.zero();
		this.z = NumberValue.zero();
	}

	public PropertyVector3f(float x, float y, float z) {
		this();
		this.x.setNumber(x);
		this.y.setNumber(y);
		this.z.setNumber(z);
	}

	public PropertyVector3f set(float x, float y, float z) {
		this.x.setNumber(x);
		this.y.setNumber(y);
		this.z.setNumber(z);
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
	 * @return a new vector4f
	 */
	public Vector3f toVector3f() {
		return this.toVector3f(new Vector3f());
	}

	/**
	 * @param dest
	 * @return the vector4f
	 */
	public Vector3f toVector3f(Vector3f dest) {
		dest.set(this.x.floatValue(), this.y.floatValue(), this.z.floatValue());
		return dest;
	}

}
