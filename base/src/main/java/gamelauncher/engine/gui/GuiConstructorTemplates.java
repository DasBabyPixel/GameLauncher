/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Api
public final class GuiConstructorTemplates {

    private static final CopyOnWriteArraySet<GuiConstructorTemplate> defaults = new CopyOnWriteArraySet<>();
    private static GameLauncher launcher;

    @Api
    public static final GuiConstructorTemplate LAUNCHER = new GuiConstructorTemplate(GameLauncher.class) {
        @Override
        public Object[] arguments() {
            return new Object[]{launcher};
        }
    };

    static {
        addDefault(LAUNCHER);
    }

    @ApiStatus.Internal
    public static void init(GameLauncher launcher) {
        GuiConstructorTemplates.launcher = launcher;
    }

    @Api
    public static Set<GuiConstructorTemplate> defaults() {
        return defaults;
    }

    public static void addDefault(GuiConstructorTemplate template) {
        defaults.add(template);
    }
}
