package gamelauncher.engine.settings;

import gamelauncher.engine.util.keybind.Keybind;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class KeybindSetting extends SimpleSetting<Keybind> {
	public KeybindSetting(Type type, Supplier<Keybind> defaultSupplier) {
		super(type, defaultSupplier);
	}
}
