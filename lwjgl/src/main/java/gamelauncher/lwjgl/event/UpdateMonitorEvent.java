/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.event;

import gamelauncher.engine.event.Event;
import gamelauncher.lwjgl.render.glfw.Monitor;

public class UpdateMonitorEvent extends Event {
    private final Monitor monitor;

    public UpdateMonitorEvent(Monitor monitor) {
        this.monitor = monitor;
    }

    public Monitor monitor() {
        return monitor;
    }
}
