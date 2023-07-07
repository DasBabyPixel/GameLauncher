/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util;

public enum DefaultOperatingSystems implements OperatingSystem {
    WINDOWS, ANDROID, UNKNOWN;

    @Override public String osName() {
        return name().toLowerCase();
    }
}
