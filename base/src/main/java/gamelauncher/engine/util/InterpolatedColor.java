package gamelauncher.engine.util;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.util.math.Math;
import gamelauncher.engine.util.property.PropertyVector4f;

public class InterpolatedColor {
	private final PropertyVector4f curcolor = new PropertyVector4f();
	private final PropertyVector4f desiredColor = new PropertyVector4f();
	private final PropertyVector4f lastColor = new PropertyVector4f();
	private final NumberValue nanotimeDone = NumberValue.zero();
	private final NumberValue nanotimeStarted = NumberValue.zero();

	public void set(PropertyVector4f other) {
		this.desiredColor.set(other);
		this.lastColor.set(other);
		this.curcolor.set(other);
		this.nanotimeDone.setNumber(System.nanoTime());
		this.nanotimeStarted.setNumber(System.nanoTime());
	}

	public PropertyVector4f currentColor() {
		return curcolor;
	}

	/**
	 * @return if the color changed
	 */
	public boolean calculateCurrent() {
		long time = System.nanoTime();
		if (this.nanotimeDone.longValue() - time < 0) {
			if (this.curcolor.equals(this.desiredColor)) {
				return false;
			}
			this.curcolor.set(this.desiredColor);
			return true;
		}
		long diff = this.nanotimeDone.longValue() - this.nanotimeStarted.longValue();
		if (diff == 0) {
			if (this.curcolor.equals(this.desiredColor)) {
				return false;
			}
			this.curcolor.set(this.desiredColor);
			return true;
		}
		float progress = (float) (time - this.nanotimeStarted.longValue()) / (float) diff;
		this.curcolor.x.setNumber(
				Math.lerp(this.lastColor.x.doubleValue(), this.desiredColor.x.doubleValue(),
						progress));
		this.curcolor.y.setNumber(
				Math.lerp(this.lastColor.y.doubleValue(), this.desiredColor.y.doubleValue(),
						progress));
		this.curcolor.z.setNumber(
				Math.lerp(this.lastColor.z.doubleValue(), this.desiredColor.z.doubleValue(),
						progress));
		this.curcolor.w.setNumber(
				Math.lerp(this.lastColor.w.doubleValue(), this.desiredColor.w.doubleValue(),
						progress));
		return true;
	}

	public void setDesired(PropertyVector4f other, long time) {
		long started = System.nanoTime();
		long done = System.nanoTime() + time;
		this.nanotimeStarted.setNumber(started);
		this.nanotimeDone.setNumber(done);
		this.lastColor.set(this.curcolor);
		this.desiredColor.set(other);
	}
}
