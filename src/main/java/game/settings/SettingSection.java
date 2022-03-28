package game.settings;

public interface SettingSection extends Setting<SettingSection> {

	SettingSection getSubSection(SettingPath<SettingSection> path);
	
	<T> Setting<T> getSetting(SettingPath<T> path);
	
	@Override
	default SettingSection getValue() {
		return this;
	}
	
	@Override
	default void setValue(SettingSection value) {
		throw new UnsupportedOperationException();
	}
}
