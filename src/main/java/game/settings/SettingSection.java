package game.settings;

public interface SettingSection extends Setting<SettingSection> {

	SettingSection getSubSection(SettingPath path);
	
	<T> Setting<T> getSetting(SettingPath path);
	
	@Override
	default SettingSection getValue() {
		return this;
	}
	
	@Override
	default void setValue(SettingSection value) {
		throw new UnsupportedOperationException();
	}
}
