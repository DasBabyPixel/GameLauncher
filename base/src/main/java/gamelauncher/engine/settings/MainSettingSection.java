package gamelauncher.engine.settings;

import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.settings.controls.ControlsSettingSection;

public class MainSettingSection extends AbstractSettingSection {

	public static final SettingPath CONTROLS = new SettingPath("controls");

	public MainSettingSection(EventManager eventManager) {
		super(eventManager);
	}

	@Override
	protected void addSettings(EventManager eventManager) {
		this.addSetting(CONTROLS, new ControlsSettingSection(eventManager));
	}
}
