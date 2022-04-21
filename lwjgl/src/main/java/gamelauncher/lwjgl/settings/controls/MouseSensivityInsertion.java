package gamelauncher.lwjgl.settings.controls;

import gamelauncher.engine.settings.AbstractSettingSection.SettingSectionConstructor;
import gamelauncher.engine.settings.ClassBasedSettingSectionInsertion;
import gamelauncher.engine.settings.SettingPath;
import gamelauncher.engine.settings.SimpleSetting;
import gamelauncher.engine.settings.controls.ControlsSettingSection;

public class MouseSensivityInsertion extends ClassBasedSettingSectionInsertion {

	public static final SettingPath SENSIVITY = new SettingPath("sensivity");

	public MouseSensivityInsertion() {
		super(ControlsSettingSection.class);
	}

	@Override
	protected void construct(SettingSectionConstructor constructor) {
		constructor.addSetting(SENSIVITY, new SimpleSetting<>(Float.TYPE, 1.0F));
	}
}
