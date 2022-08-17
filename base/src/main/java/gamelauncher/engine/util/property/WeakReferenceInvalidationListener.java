package gamelauncher.engine.util.property;

import java.lang.ref.WeakReference;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.Property;

/**
 * @author DasBabyPixel
 */
public class WeakReferenceInvalidationListener implements InvalidationListener {

	private final WeakReference<InvalidationListener> ref;

	/**
	 * @param listener
	 */
	public WeakReferenceInvalidationListener(InvalidationListener listener) {
		this.ref = new WeakReference<InvalidationListener>(listener);
	}

	@Override
	public void invalidated(Property<?> property) {
		InvalidationListener l = ref.get();
		if (l == null) {
			property.removeListener(this);
		} else {
			l.invalidated(property);
		}
	}

}
