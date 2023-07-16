/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.settings;

import gamelauncher.engine.settings.*;
import org.jetbrains.annotations.Nullable;

public class DisplayInsertion extends ClassBasedSettingSectionInsertion {
    public static final SettingPath FULLSCREEN = new SettingPath("fullscreen");
    public static final SettingPath MONITOR = new SettingPath("monitor");

    public DisplayInsertion() {
        super(MainSettingSection.class);
    }

    public static @Nullable MonitorInfo monitor(SettingSection root) {
        return root.<MonitorInfo>getSetting(MONITOR).getValue();
    }

    public static void monitor(SettingSection root, @Nullable MonitorInfo monitor) {
        root.<MonitorInfo>getSetting(MONITOR).setValue(monitor);
    }

    public static boolean fullscreen(SettingSection root) {
        return root.<Boolean>getSetting(FULLSCREEN).getValue();
    }

    public static void fullscreen(SettingSection root, boolean fullscreen) {
        root.getSetting(FULLSCREEN).setValue(fullscreen);
    }

    @Override protected void construct(AbstractSettingSection.SettingSectionConstructor constructor) {
        constructor.addSetting(FULLSCREEN, new SimpleSetting<>(Boolean.class, true));
        constructor.addSetting(MONITOR, new SimpleSetting<>(MonitorInfo.class, (Object) null));
    }

    public static class MonitorInfo {
        public final String name;
        public final int width;
        public final int height;
        public final int refreshRate;

        public MonitorInfo(String name, int width, int height, int refreshRate) {
            this.name = name;
            this.width = width;
            this.height = height;
            this.refreshRate = refreshRate;
        }
    }
}
