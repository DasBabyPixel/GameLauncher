/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.glfw;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.collections.Collections;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.framebuffer.ManualQueryFramebuffer;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;
import java8.util.concurrent.CompletableFuture;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWFrame extends AbstractGameResource implements Frame {

    private static final Logger logger = Logger.logger();
    private static final GameConsumer<Frame> simpleCCB = f -> ((GLFWFrame) f).close0();
    final LWJGLGameLauncher launcher;
    final GLFWFrameRenderThread renderThread;
    final LWJGLInput input;
    final LWJGLMouse mouse;
    final CompletableFuture<Frame> closeFuture;
    final GLFWFrameFramebuffer framebuffer;
    final Property<RenderMode> renderMode;
    final Property<FrameRenderer> frameRenderer;
    final FrameCounter frameCounter;
    final GLFWGLContext context;
    final Deque<GLFWGLContext> contexts;
    final Property<GameConsumer<Frame>> closeCallback = Property.withValue(GLFWFrame.simpleCCB);
    final NumberValue windowWidth = NumberValue.withValue(0D);
    final NumberValue windowHeight = NumberValue.withValue(0D);
    final BooleanValue fullscreen = BooleanValue.falseValue();
    final Property<Monitor> monitor = Property.empty();
    final ManualQueryFramebuffer manualFramebuffer;
    boolean cleaningUp = false;
    private boolean created;

    public GLFWFrame(LWJGLGameLauncher launcher) throws GameException {
        super();
        this.created = false;
        this.launcher = launcher;
        this.contexts = Collections.newConcurrentDeque();
        this.frameCounter = new FrameCounter();
        this.closeFuture = new CompletableFuture<>();
        this.renderMode = Property.empty();
        this.frameRenderer = Property.empty();
        this.mouse = new LWJGLMouse(this);
        this.framebuffer = new GLFWFrameFramebuffer(this);
        this.renderThread = new GLFWFrameRenderThread(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(this.framebuffer);
        this.input = new LWJGLInput(this);
        this.context = new GLFWGLContext(new CopyOnWriteArraySet<>());
        this.context.owner = renderThread;
        this.context.frame = this;
        this.launcher.getGLFWThread().submit(() -> {
            this.context.create(null);
            this.created = true;
            this.renderThread.start();
        });
    }

    GLFWFrame(LWJGLGameLauncher launcher, GLFWGLContext context) {
        super();
        this.launcher = launcher;
        this.contexts = Collections.newConcurrentDeque();
        this.frameCounter = new FrameCounter();
        this.closeFuture = new CompletableFuture<>();
        this.renderMode = Property.empty();
        this.frameRenderer = Property.empty();
        this.mouse = new LWJGLMouse(this);
        this.framebuffer = new GLFWFrameFramebuffer(this);
        this.renderThread = new GLFWFrameRenderThread(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(this.framebuffer);
        this.input = new LWJGLInput(this);
        this.context = context;
        this.context.owner = renderThread;
        this.created = true;
    }

    /**
     * Called from the default closecallback
     */
    private void close0() {
        new Thread(() -> {
            try {
                launcher.stop();
            } catch (GameException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        cleaningUp = true;
        this.renderThread.cleanupContextOnExit = true;
        CompletableFuture<Void> f3 = this.renderThread.exit();
        List<CompletableFuture<Void>> futs = new ArrayList<>();
        GLFWGLContext context;
        while ((context = contexts.poll()) != null) {
            context.parent = null;
            futs.add(context.cleanup());
        }
        CompletableFuture<Void> f1 = this.manualFramebuffer.cleanup();
        CompletableFuture<Void> f2 = this.framebuffer.cleanup();
        futs.add(f1);
        futs.add(f2);
        futs.add(f3);
        this.contexts.clear();
        CompletableFuture<Void> f = CompletableFuture.allOf(futs.toArray(new CompletableFuture[0]));
        f.thenRun(() -> cleaningUp = false);
        return f;
    }

    @Override public GLFWFrameRenderThread renderThread() {
        return this.renderThread;
    }

    public CompletableFuture<Void> showWindow() {
        return this.launcher.getGLFWThread().submit(() -> {
            this.framebuffer.swapBuffers().value(true);
            glfwShowWindow(this.context.glfwId);
            this.renderThread.scheduleDrawRefreshWait();
        });
    }

    public void hideWindow() {
        this.launcher.getGLFWThread().submit(() -> {
            this.framebuffer.swapBuffers().value(false);
            glfwHideWindow(this.context.glfwId);
        });
    }

    public GLFWGLContext context() {
        return context;
    }

    @Api public void freeContext(GLFWGLContext context) throws GameException {
        if (!this.contexts.contains(context)) {
            throw new IllegalStateException("Frame does not contain context");
        }
        this.freeContextManual(context, false);
        this.contexts.remove(context);
    }

    public void freeContextManual(GLFWGLContext context, boolean renderThreadCleanedUp) throws GameException {
        if (!renderThreadCleanedUp) {
            context.cleanup();
        }
    }

    public long getGLFWId() {
        return this.context.glfwId;
    }

    @Override public Input input() {
        return this.input;
    }

    @Override public CompletableFuture<Frame> frameCloseFuture() {
        return this.closeFuture;
    }

    @Override public ManualQueryFramebuffer framebuffer() {
        return this.manualFramebuffer;
    }

    @Override public RenderMode renderMode() {
        return this.renderMode.value();
    }

    @Override public void renderMode(RenderMode renderMode) {
        this.renderMode.value(renderMode);
    }

    @Override public void frameRenderer(FrameRenderer renderer) {
        if (this == launcher.frame() && renderer != launcher.gameRenderer()) {
            throw new UnsupportedOperationException("Please set the renderer via GameLauncher#gameRenderer to preserve a consistent state");
        }
        this.frameRenderer.value(renderer);
    }

    @Override public BooleanValue fullscreen() {
        return fullscreen;
    }

    @Override public FrameRenderer frameRenderer() {
        return this.frameRenderer.value();
    }

    @Override public GLFWFrame newFrame() throws GameException {
        return context.createSharedContext().frame();
    }

    @Override public FrameCounter frameCounter() {
        return this.frameCounter;
    }

    @Override public void scheduleDraw() {
        this.renderThread.scheduleDraw();
    }

    @Override public void waitForFrame() {
        this.renderThread.waitForFrame();
    }

    @Override public void scheduleDrawWaitForFrame() {
        this.renderThread.scheduleDrawWait();
    }

    @Override public LWJGLGameLauncher launcher() {
        return this.launcher;
    }

    public LWJGLMouse mouse() {
        return this.mouse;
    }

    public Property<GameConsumer<Frame>> closeCallback() {
        return this.closeCallback;
    }

    static class Creator implements Runnable {

        public final NumberValue width = NumberValue.withValue(0);
        public final NumberValue height = NumberValue.withValue(0);
        public final NumberValue fbwidth = NumberValue.withValue(0);
        public final NumberValue fbheight = NumberValue.withValue(0);
        public final NumberValue scaleX = NumberValue.withValue(0F);
        public final NumberValue scaleY = NumberValue.withValue(0F);
        public final NumberValue xpos = NumberValue.withValue(0);
        public final NumberValue ypos = NumberValue.withValue(0);
        public final Property<Monitor> monitor = Property.empty();
        public final GLFWFrame frame;
        private final NumberValue fullscreenOldX = NumberValue.withValue(0);
        private final NumberValue fullscreenOldY = NumberValue.withValue(0);
        private final NumberValue fullscreenOldW = NumberValue.withValue(0);
        private final NumberValue fullscreenOldH = NumberValue.withValue(0);
        private final GLFWGLContext shared;
        public long glfwId;

        public Creator(GLFWFrame frame, GLFWGLContext shared) {
            this.frame = frame;
            this.shared = shared;
        }

        @Override public void run() {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

            Monitor primaryMonitor = frame.launcher.getGLFWThread().getMonitorManager().getMonitor(glfwGetPrimaryMonitor());

            this.glfwId = glfwCreateWindow(primaryMonitor.width() / 2, primaryMonitor.height() / 2, GameLauncher.NAME, 0, shared == null ? 0 : shared.glfwId);
            if (glfwId == 0L) {
                PointerBuffer buf = MemoryUtil.memAllocPointer(1);
                glfwGetError(buf);
                String error = buf.getStringUTF8();
                MemoryUtil.memFree(buf);
                if (shared != null) System.out.println(shared.sharedContexts);
                throw new RuntimeException("Error creating window: " + error);
            }
            glfwSetWindowSizeLimits(this.glfwId, 1, 1, GLFW_DONT_CARE, GLFW_DONT_CARE);
            glfwSetWindowPos(glfwId, primaryMonitor.x() + primaryMonitor.width() / 4, primaryMonitor.y() + primaryMonitor.height() / 4);
            int[] a0 = new int[1];
            int[] a1 = new int[1];
            glfwGetWindowSize(this.glfwId, a0, a1);
            this.width.number(a0[0]);
            this.height.number(a1[0]);
            glfwGetWindowPos(glfwId, a0, a1);
            this.xpos.number(a0[0]);
            this.ypos.number(a1[0]);
            if (!this.frame.created) {
                this.frame.windowWidth.bind(this.width);
                this.frame.windowHeight.bind(this.height);
                this.monitor.value(primaryMonitor);
            }
            glfwGetFramebufferSize(this.glfwId, a0, a1);
            this.fbwidth.number(a0[0]);
            this.fbheight.number(a1[0]);
            if (!this.frame.created) {
                this.frame.framebuffer.width().bind(this.fbwidth);
                this.frame.framebuffer.height().bind(this.fbheight);
                this.frame.monitor.bind(monitor);
                this.frame.monitor.addListener(Property::value);

                InvalidationListener l = p -> {
                    GLFWMonitorManager manager = frame.launcher.getGLFWThread().getMonitorManager();
                    Monitor nearest = null;
                    Rect window = new Rect(xpos.intValue(), ypos.intValue(), width.intValue(), height.intValue());
                    for (Monitor monitor : manager.getMonitors()) {
                        if (nearest != null && new Rect(nearest.x(), nearest.y(), nearest.width(), nearest.height()).contains(window)) {
                            break;
                        }
                        if (nearest == null) {
                            nearest = monitor;
                            continue;
                        }

                        Rect rnearest = new Rect(nearest.x(), nearest.y(), nearest.width(), nearest.height());
                        Rect rmonitor = new Rect(monitor.x(), monitor.y(), monitor.width(), monitor.height());
                        if (rmonitor.contains(window)) {
                            nearest = monitor;
                            continue;
                        }
                        Rect overlapNearest = rnearest.overlap(window);
                        Rect overlapMonitor = rmonitor.overlap(window);
                        if (overlapMonitor.size() > overlapNearest.size()) {
                            nearest = monitor;
                        }
                    }
                    monitor.value(nearest);
                };
                this.xpos.addListener(l);
                this.ypos.addListener(l);
                this.width.addListener(l);
                this.height.addListener(l);

                this.frame.fullscreen.addListener(Property::value);
                this.frame.fullscreen.value();
                this.frame.fullscreen.addListener((p, o, n) -> frame.launcher.getGLFWThread().submit(() -> {
                    Monitor monitor = frame.monitor.value();
                    if (n) {
                        fullscreenOldX.number(xpos.intValue());
                        fullscreenOldY.number(ypos.intValue());
                        fullscreenOldW.number(width.intValue());
                        fullscreenOldH.number(height.intValue());
                        glfwSetWindowPos(glfwId, monitor.x(), monitor.y());
                        glfwSetWindowSize(glfwId, monitor.width(), monitor.height());
                        int[] w = new int[1];
                        int[] h = new int[1];
                        glfwGetWindowSize(glfwId, w, h);
                        if (w[0] < monitor.width() || h[0] < monitor.height()) {
                            glfwSetWindowSizeLimits(glfwId, 1, 1, monitor.width(), monitor.height());
                            glfwSetWindowSize(glfwId, monitor.width(), monitor.height());
                        }
                    } else {
                        glfwSetWindowPos(glfwId, fullscreenOldX.intValue(), fullscreenOldY.intValue());
                        glfwSetWindowSizeLimits(glfwId, 1, 1, GLFW_DONT_CARE, GLFW_DONT_CARE);
                        glfwSetWindowSize(glfwId, fullscreenOldW.intValue(), fullscreenOldH.intValue());
                    }
                }));
            }
            float[] f0 = new float[1];
            float[] f1 = new float[1];
            glfwGetWindowContentScale(this.glfwId, f0, f1);
            this.scaleX.number(f0[0]);
            this.scaleY.number(f1[0]);
            glfwSetWindowPosCallback(glfwId, (window, xpos, ypos) -> {
                this.xpos.number(xpos);
                this.ypos.number(ypos);
            });
            glfwSetScrollCallback(this.glfwId, (wid, xo, yo) -> {
                try {
                    this.frame.input.scroll((float) xo, (float) yo);
                } catch (GameException ex) {
                    ex.printStackTrace();
                }
            });
            glfwSetMonitorCallback((monitor, event) -> this.monitor.value(frame.launcher.getGLFWThread().getMonitorManager().getMonitor(monitor)));
            glfwSetWindowCloseCallback(this.glfwId, wid -> {
                GameConsumer<Frame> cs = this.frame.closeCallback.value();
                if (cs != null) {
                    try {
                        cs.accept(this.frame);
                    } catch (GameException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            glfwSetCursorEnterCallback(this.glfwId, (wid, entered) -> {
                this.frame.mouse.setInWindow(entered);
                if (!entered) this.frame.input.mouseMove((float) frame.mouse().getX(), (float) frame.mouse().getY(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
            });
            glfwSetCursorPosCallback(this.glfwId, (wid, xpos, ypos) -> {
                ypos = ypos + 0.5F;
                xpos = xpos + 0.5F;
                float omx = (float) this.frame.mouse().getX();
                float omy = (float) this.frame.mouse().getY();
                ypos = this.height.doubleValue() - ypos;
                this.frame.mouse().setPosition(xpos, ypos);
                this.frame.input.mouseMove(omx, omy, (float) xpos, (float) ypos);
            });
            glfwSetWindowSizeCallback(this.glfwId, (wid, w, h) -> {
                this.width.number(w);
                this.height.number(h);
            });
            glfwSetMouseButtonCallback(this.glfwId, (wid, button, action, mods) -> {
                switch (action) {
                    case GLFW_PRESS:
                        this.frame.input.mousePress(button, (float) this.frame.mouse().getX(), (float) this.frame.mouse().getY());
                        break;
                    case GLFW_RELEASE:
                        this.frame.input.mouseRelease(button, (float) this.frame.mouse().getX(), (float) this.frame.mouse().getY());
                        break;
                }
            });
            glfwSetKeyCallback(this.glfwId, (wid, key, scancode, action, mods) -> {
                switch (action) {
                    case GLFW_PRESS:
                        this.frame.input.keyPress(key, scancode, (char) 0);
                        break;
                    case GLFW_RELEASE:
                        this.frame.input.keyRelease(key, scancode, (char) 0);
                        break;
                    case GLFW_REPEAT:
                        this.frame.input.keyRepeat(key, scancode, (char) 0);
                        break;
                }
            });
            glfwSetWindowContentScaleCallback(this.glfwId, (window, xscale, yscale) -> {
                this.scaleX.number(xscale);
                this.scaleY.number(yscale);
            });
            glfwSetCharCallback(this.glfwId, (wid, codepoint) -> this.frame.input.character((char) codepoint));
            glfwSetWindowRefreshCallback(this.glfwId, wid -> this.frame.renderThread.refreshWait());
            glfwSetFramebufferSizeCallback(this.glfwId, (wid, width, height) -> {
                GLFWFrame.logger.debugf("Viewport changed: (%4d, %4d)", width, height);
                this.fbwidth.number(width);
                this.fbheight.number(height);
                if (this.frame.renderMode() != RenderMode.MANUAL) {
                    this.frame.renderThread.scheduleDrawRefreshWait();
                }
            });
        }

    }

    private static class Rect {
        private final double x, y, w, h;

        public Rect(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public double size() {
            return w * h;
        }

        public boolean contains(Rect other) {
            return x <= other.x && y <= other.y && x + w >= other.x + other.w && y + h >= other.y + other.h;
        }

        public Rect overlap(Rect other) {
            double nx = Math.max(x, other.x);
            double ny = Math.max(y, other.y);
            double right = Math.min(x + w, other.x + other.w);
            double top = Math.min(y + h, other.y + other.h);

            return new Rect(nx, ny, Math.max(0, right - nx), Math.max(top - ny, 0));
        }
    }
}
