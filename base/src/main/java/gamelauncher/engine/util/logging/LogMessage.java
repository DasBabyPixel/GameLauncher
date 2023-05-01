/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.logging;

import gamelauncher.engine.util.Key;

public class LogMessage {
    private final Key key;
    private final Object[] args;

    public LogMessage(Key key, Object[] args) {
        this.key = key;
        this.args = args;
    }

    public Key key() {
        return key;
    }

    public Object[] args() {
        return args;
    }
}
