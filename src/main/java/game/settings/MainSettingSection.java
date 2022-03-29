package game.settings;

public class MainSettingSection extends AbstractSettingSection {

	public static final SettingPath TEST = new SettingPath("test");

	@Override
	protected void addSettings() {
		this.addSetting(TEST, new SimpleSetting<>(String.class, () -> "Kleiner Test"));
	}
}
