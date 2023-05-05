/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.render;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.guis.MainScreenGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESCompat;
import gamelauncher.gles.context.GLESDrawContext;
import gamelauncher.gles.context.GLESStates;
import gamelauncher.gles.framebuffer.BasicFramebuffer;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.model.Texture2DModel;
import gamelauncher.gles.states.StateRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class GLESGameRenderer implements GameRenderer {

    //	public static final boolean WIREFRAMES = false;

    private final AtomicReference<Renderer> renderer = new AtomicReference<>();
    private final Map<Frame, Entry> map = new ConcurrentHashMap<>();
    private final GLES gles;
    private final GameLauncher launcher;
    private final GLESStates states = new GLESStates();

    public GLESGameRenderer(GLES gles) {
        this.gles = gles;
        this.launcher = gles.launcher();
        this.renderer.set(new GuiRenderer(launcher));
    }

    @Override public void renderFrame(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "frame");
        this.map.get(frame).renderFrame(this.renderer.get());
        this.launcher.profiler().end();
    }

    @Override public void windowSizeChanged(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "windowSizeChanged");
        this.map.get(frame).windowSizeChanged();
        this.launcher.profiler().end();
    }

    @Override public void init(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "init");
        this.launcher.logger().info("Initializing RenderEngine");
        this.map.put(frame, new Entry(frame));
        this.map.get(frame).init();
        this.launcher.logger().info("RenderEngine initialized");
        this.launcher.profiler().end();
    }

    @Override public void cleanup(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "cleanup");
        this.launcher.logger().info("Cleaning up RenderEngine");
        this.map.remove(frame).cleanup();
        this.launcher.logger().info("RenderEngine cleaned up");
        this.launcher.profiler().end();
    }

    @Override public void refreshDisplay(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "refresh");
        this.map.get(frame).refreshDisplay(this.renderer.get());
        this.launcher.profiler().end();
    }

    @Override public void setRenderer(Renderer renderer) {
        this.renderer.set(renderer);
    }

    @Override public Renderer renderer() {
        return this.renderer.get();
    }

    public class Entry extends AbstractGameResource {

        public final Frame frame;
        public final Camera camera;
        public BasicFramebuffer mainFramebuffer;
        public GameItem mainScreenItem;
        public GameItem.GameItemModel mainScreenItemModel;
        public Renderer crenderer;
        public DrawContext contexthud;

        public Entry(Frame frame) {
            this.frame = frame;
            this.camera = new BasicCamera();
        }

        public void init() throws GameException {
            states.depth.enabled.value.set(true);
            states.depth.depthFunc.set(GLES20.GL_LEQUAL);
            states.blend.enabled.value.set(true);
            states.blend.separate.set(true);
            states.replace(null);
            GLES31 gl = StateRegistry.currentGl();
            GLESCompat.VERSION = gl.glGetString(GLES20.GL_VERSION);
            GLESCompat.SHADING_VERSION = gl.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION);
            GLESCompat.VERSION_MAJOR = gl.glGetInteger(GLES30.GL_MAJOR_VERSION);
            GLESCompat.VERSION_MINOR = gl.glGetInteger(GLES30.GL_MINOR_VERSION);
            GLESCompat.MAX_TEXTURE_SIZE = gl.glGetInteger(GLES20.GL_MAX_TEXTURE_SIZE);
            GLESCompat.MAX_TEXTURE_IMAGE_UNITS = gl.glGetInteger(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS);
            GLESCompat.MAX_DRAW_BUFFERS = gl.glGetInteger(GLES30.GL_MAX_DRAW_BUFFERS);

            GLESCompat.printDebugInfos(gles.launcher().logger());

            this.mainFramebuffer = new BasicFramebuffer(gles, this.frame.framebuffer().width().intValue(), this.frame.framebuffer().height().intValue());
            this.mainScreenItem = new GameItem(new Texture2DModel(this.mainFramebuffer.getColorTexture()));
            this.mainScreenItem.scale().x.bind(this.mainFramebuffer.width());
            this.mainScreenItem.scale().y.bind(this.mainFramebuffer.height());
            this.mainScreenItem.position().x.bind(this.mainFramebuffer.width().divide(2D));
            this.mainScreenItem.position().y.bind(this.mainFramebuffer.height().divide(2D));

            this.mainScreenItemModel = this.mainScreenItem.createModel();

            this.contexthud = launcher.contextProvider().loadContext(this.mainFramebuffer, ContextType.HUD);
            ((GLESDrawContext) this.contexthud).swapTopBottom = true;

            launcher.guiManager().openGuiByClass(this.mainFramebuffer, MainScreenGui.class);

            //			updateScreenItems();

        }

        @Override public void cleanup0() throws GameException {
            this.mainScreenItemModel.cleanup();
            launcher.contextProvider().freeContext(this.contexthud, ContextType.HUD);
            this.mainFramebuffer.cleanup();
        }

        public void windowSizeChanged() throws GameException {
            this.mainFramebuffer.resize(this.frame.framebuffer().width().intValue(), this.frame.framebuffer().height().intValue());
            //			updateScreenItems();
        }

        //		private void updateScreenItems() {
        //			float fw = window.getFramebuffer().width().floatValue();
        //			float fh = window.getFramebuffer().height().floatValue();
        //			mainScreenItem.setScale(fw, fh, 1);
        //			mainScreenItem.setPosition(fw / 2F, fh / 2F, 0);
        //		}

        public void refreshDisplay(Renderer renderer) throws GameException {
            GLES31 cur = StateRegistry.currentGl();
            this.frame.framebuffer().beginFrame();
            cur.glViewport(0, 0, this.frame.framebuffer().width().intValue(), this.frame.framebuffer().height().intValue());
            cur.glClearColor(0, 0, 0, 0);
            cur.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            this.contexthud.update(this.camera);
            this.contexthud.drawModel(this.mainScreenItemModel, 0, 0, 0);
            this.contexthud.program().clearUniforms();
            this.frame.framebuffer().endFrame();
        }

        public void renderFrame(Renderer renderer) throws GameException {
            GLES31 cur = StateRegistry.currentGl();
            this.frame.framebuffer().beginFrame();
            cur.glViewport(0, 0, this.frame.framebuffer().width().intValue(), this.frame.framebuffer().height().intValue());
            cur.glClearColor(0, 0, 0, 0);
            cur.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            this.mainFramebuffer.bind();
            cur.glClearColor(0.2F, 0.2F, 0.2F, 0.8F);
            cur.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            if (renderer != this.crenderer) {
                this.cleanup(this.crenderer);
                this.init(renderer);
                this.crenderer = renderer;
            }
            if (renderer != null) {
                renderer.render(this.mainFramebuffer);
            }

            this.mainFramebuffer.unbind();

            this.contexthud.update(this.camera);
            this.contexthud.drawModel(this.mainScreenItemModel, 0, 0, 0);
            this.contexthud.program().clearUniforms();

            this.frame.framebuffer().endFrame();
        }

        private void cleanup(Renderer renderer) throws GameException {
            if (renderer == null) {
                return;
            }
            renderer.cleanup(this.mainFramebuffer);
        }

        private void init(Renderer renderer) throws GameException {
            if (renderer == null) {
                return;
            }
            renderer.init(this.mainFramebuffer);
        }

    }

}
