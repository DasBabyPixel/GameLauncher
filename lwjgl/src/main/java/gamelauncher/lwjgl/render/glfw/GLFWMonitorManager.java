/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.event.AddMonitorEvent;
import gamelauncher.lwjgl.event.RemoveMonitorEvent;
import gamelauncher.lwjgl.event.UpdateMonitorEvent;
import gamelauncher.lwjgl.render.glfw.Monitor.VideoMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class GLFWMonitorManager {

    private final Logger logger = Logger.logger();
    private final CopyOnWriteArrayList<Monitor> monitors = new CopyOnWriteArrayList<>();
    private final GameLauncher launcher;
    private final GLFWMonitorCallback callback = new GLFWMonitorCallback() {
        @Override public void invoke(long monitor, int event) {
            if (event == GLFW.GLFW_CONNECTED) {
                GLFWMonitorManager.this.newMonitor(monitor);
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                GLFWMonitorManager.this.removeMonitor(monitor);
            } else System.out.println(event);
        }
    };

    public GLFWMonitorManager(GameLauncher launcher) {
        this.launcher = launcher;
    }

    public Monitor monitor(long glfwId) {
        for (Monitor monitor : monitors) {
            if (monitor.glfwId() == glfwId) return monitor;
        }
        return null;
    }

    public Collection<Monitor> monitors() {
        return Collections.unmodifiableCollection(monitors);
    }

    public synchronized void pollAll() {
        for (Monitor o : monitors) {
            long monitor = o.glfwId();

            Monitor m = createMonitor(monitor);
            if (!m.equals(o)) {
                logger.infof("Monitor Updated! %s[x=%s, y=%s, width=%s, height=%s, " + "refreshRate=%sHz, scaleX=%.2f, scaleY=%.2f]", m.name(), m.x(), m.y(), m.width(), m.height(), m.videoMode().refreshRate(), m.scaleX(), m.scaleY());
                this.monitors.add(m);
                this.monitors.remove(o);
                launcher.eventManager().post(new UpdateMonitorEvent(m));
            }
        }
    }

    void init() {
        GLFW.glfwSetMonitorCallback(callback);
        PointerBuffer mbuf = GLFW.glfwGetMonitors();
        assert mbuf != null;
        while (mbuf.hasRemaining()) {
            this.newMonitor(mbuf.get());
        }
    }

    void cleanup() {
        GLFW.glfwSetMonitorCallback(null);
        callback.free();
    }

    private Monitor createMonitor(long monitor) {
        int[] x = new int[1];
        int[] y = new int[1];
        GLFW.glfwGetMonitorPos(monitor, x, y);
        String name = GLFW.glfwGetMonitorName(monitor);
        float[] sx = new float[1];
        float[] sy = new float[1];
        GLFW.glfwGetMonitorContentScale(monitor, sx, sy);
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
        if (vidMode == null) throw new RuntimeException();
        return new Monitor(name, x[0], y[0], vidMode.width(), vidMode.height(), sx[0], sy[0], monitor, new VideoMode(vidMode.width(), vidMode.height(), vidMode.refreshRate()));
    }

    private synchronized void newMonitor(long monitor) {
        Monitor m = createMonitor(monitor);
        logger.infof("New Monitor connected! %s[x=%s, y=%s, width=%s, height=%s, " + "refreshRate=%sHz, scaleX=%.2f, scaleY=%.2f]", m.name(), m.x(), m.y(), m.width(), m.height(), m.videoMode().refreshRate(), m.scaleX(), m.scaleY());
        this.monitors.add(m);
        launcher.eventManager().post(new AddMonitorEvent(m));
    }

    private synchronized void removeMonitor(long monitor) {
        ListIterator<Monitor> it = this.monitors.listIterator();
        int index = -1;
        while (it.hasNext()) {
            int i = it.nextIndex();
            Monitor m = it.next();
            if (m.glfwId() == monitor) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalStateException();
        }
        Monitor m = this.monitors.remove(index);
        launcher.eventManager().post(new RemoveMonitorEvent(m));
    }

}
