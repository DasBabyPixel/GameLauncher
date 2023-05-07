/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.Key;

import java.util.Objects;

@Api
public class GuiDistribution {
    @Api public static final GuiDistribution DEFAULT = new GuiDistribution("default_gui_distribution");
    private final Key key;

    @Api public GuiDistribution(Key key) {
        this.key = key;
    }

    @Api public GuiDistribution(String key) {
        this(new Key(key));
    }

    @Api public GuiDistribution(String namespace, String name) {
        this(new Key(namespace, name));
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiDistribution that = (GuiDistribution) o;
        return Objects.equals(key, that.key);
    }

    @Override public int hashCode() {
        return Objects.hash(key);
    }
}
