package gamelauncher.engine.settings;

/**
 * @author DasBabyPixel
 */
public interface SettingSection extends Setting<SettingSection> {

    /**
     * @param path
     * @return the {@link SettingSection} for this {@link SettingPath}
     */
    SettingSection getSubSection(SettingPath path);

    /**
     * @param <T>
     * @param path
     * @return the {@link Setting} for this {@link SettingPath}
     */
    <T> Setting<T> getSetting(SettingPath path);

    @Override default SettingSection getValue() {
        return this;
    }

    @Override default void setValue(SettingSection value) {
        throw new UnsupportedOperationException();
    }
}
