/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;

@Api
public abstract class GuiConstructorTemplate {

    private final Class<?>[] argumentTypes;

    @Api public GuiConstructorTemplate(Class<?>... argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    @Api public Class<?>[] argumentTypes() {
        return argumentTypes;
    }

    @Api public abstract Object[] arguments();
}
