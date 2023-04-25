package gamelauncher.engine.util;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.util.property.PropertyVector4f;
import org.joml.Math;

public class InterpolatedColor {
    private final PropertyVector4f curcolor = new PropertyVector4f();
    private final PropertyVector4f desiredColor = new PropertyVector4f();
    private final PropertyVector4f lastColor = new PropertyVector4f();
    private final NumberValue nanotimeDone = NumberValue.withValue(0L);
    private final NumberValue nanotimeStarted = NumberValue.withValue(0L);

    public void set(PropertyVector4f other) {
        this.desiredColor.set(other);
        this.lastColor.set(other);
        this.curcolor.set(other);
        this.nanotimeDone.number(System.nanoTime());
        this.nanotimeStarted.number(System.nanoTime());
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
        this.curcolor.x.number(Math.lerp(this.lastColor.x.doubleValue(), this.desiredColor.x.doubleValue(), progress));
        this.curcolor.y.number(Math.lerp(this.lastColor.y.doubleValue(), this.desiredColor.y.doubleValue(), progress));
        this.curcolor.z.number(Math.lerp(this.lastColor.z.doubleValue(), this.desiredColor.z.doubleValue(), progress));
        this.curcolor.w.number(Math.lerp(this.lastColor.w.doubleValue(), this.desiredColor.w.doubleValue(), progress));
        return true;
    }

    public void setDesired(PropertyVector4f other, long time) {
        long started = System.nanoTime();
        long done = System.nanoTime() + time;
        this.nanotimeStarted.number(started);
        this.nanotimeDone.number(done);
        this.lastColor.set(this.curcolor);
        this.desiredColor.set(other);
    }
}
