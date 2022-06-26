package gamelauncher.engine.settings;

import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.settings.controls.ControlsSettingSection;

/**
 * @author DasBabyPixel
 */
public class MainSettingSection extends AbstractSettingSection {

	@SuppressWarnings("javadoc")
	public static final SettingPath CONTROLS = new SettingPath("controls");

	/**
	 * @param eventManager
	 */
	public MainSettingSection(EventManager eventManager) {
		super(eventManager);
	}

	@Override
	protected void addSettings(EventManager eventManager) {
		this.addSetting(CONTROLS, new ControlsSettingSection(eventManager));
	}
}
