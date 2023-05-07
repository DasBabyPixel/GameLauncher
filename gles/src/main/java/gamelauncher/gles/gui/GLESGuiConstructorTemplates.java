/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.gui.GuiConstructorTemplate;
import gamelauncher.engine.gui.GuiConstructorTemplates;
import gamelauncher.gles.GLES;
import org.jetbrains.annotations.ApiStatus;

@Api
public class GLESGuiConstructorTemplates {

    private static GLES gles;

    @Api public static final GuiConstructorTemplate GLES = new GuiConstructorTemplate(gamelauncher.gles.GLES.class) {
        @Override public Object[] arguments() {
            return new Object[]{gles};
        }
    };

    @ApiStatus.Internal public static void init(GLES gles) {
        GLESGuiConstructorTemplates.gles = gles;
        GuiConstructorTemplates.addDefault(GLES);
    }
}
