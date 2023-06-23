package gamelauncher.engine.settings;

import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.settings.controls.ControlsSettingSection;

/**
 * @author DasBabyPixel
 */
public class MainSettingSection extends AbstractSettingSection {

    @SuppressWarnings("javadoc") public static final SettingPath CONTROLS = new SettingPath("controls");
    public static final SettingPath PROXY_HOST = new SettingPath("proxy_host");
    public static final SettingPath PROXY_PORT = new SettingPath("proxy_port");
    public static final SettingPath PROXY_USERNAME = new SettingPath("proxy_username");
    public static final SettingPath PROXY_PASSWORD = new SettingPath("proxy_password");

    /**
     * @param eventManager
     */
    public MainSettingSection(EventManager eventManager) {
        super(eventManager);
    }

    @Override protected void addSettings(EventManager eventManager) {
        this.addSetting(CONTROLS, new ControlsSettingSection(eventManager));
        addSetting(PROXY_HOST, new SimpleSetting<>(String.class, (String) null));
        addSetting(PROXY_PORT, new SimpleSetting<>(Integer.class, 8080));
        addSetting(PROXY_USERNAME, new SimpleSetting<>(String.class, (String) null));
        addSetting(PROXY_PASSWORD, new SimpleSetting<>(String.class, (String) null));
    }
}
