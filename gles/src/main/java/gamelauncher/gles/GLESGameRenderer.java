package gamelauncher.gles;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.context.GLESDrawContext;
import gamelauncher.gles.context.GLESStates;
import gamelauncher.gles.framebuffer.BasicFramebuffer;
import gamelauncher.gles.gl.GLES20;
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
    private final GLESStates GLESStates = new GLESStates();

    public GLESGameRenderer(GLES gles) {
        this.gles = gles;
        this.launcher = gles.launcher();
        this.renderer.set(new GuiRenderer(launcher));
    }

    @Override
    public void renderFrame(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "frame");
        this.map.get(frame).renderFrame(this.renderer.get());
        this.launcher.profiler().end();
    }

    @Override
    public void windowSizeChanged(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "windowSizeChanged");
        this.map.get(frame).windowSizeChanged();
        this.launcher.profiler().end();
    }

    @Override
    public void init(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "init");
        this.launcher.logger().info("Initializing RenderEngine");
        this.map.put(frame, new Entry(frame));
        this.map.get(frame).init();
        this.launcher.logger().info("RenderEngine initialized");
        this.launcher.profiler().end();
    }

    @Override
    public void cleanup(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "cleanup");
        this.launcher.logger().info("Cleaning up RenderEngine");
        this.map.remove(frame).cleanup();
        this.launcher.logger().info("RenderEngine cleaned up");
        this.launcher.profiler().end();
    }

    @Override
    public void refreshDisplay(Frame frame) throws GameException {
        this.launcher.profiler().begin("render", "refresh");
        this.map.get(frame).refreshDisplay(this.renderer.get());
        this.launcher.profiler().end();
    }

    public class Entry extends AbstractGameResource {

        public final Frame frame;
        public BasicFramebuffer mainFramebuffer;
        public GameItem mainScreenItem;
        public GameItem.GameItemModel mainScreenItemModel;
        public Renderer crenderer;
        public Camera camera;
        public DrawContext contexthud;

        public Entry(Frame frame) {
            this.frame = frame;
            this.camera = new BasicCamera();
        }

        public void init() throws GameException {
            GLESStates.depth.enabled.value.set(true);
            GLESStates.depth.depthFunc.set(GLES20.GL_LEQUAL);
            GLESStates.blend.enabled.value.set(true);
            GLESStates.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
            GLESStates.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLESStates.replace(null);

            this.mainFramebuffer = new BasicFramebuffer(gles,
                    this.frame.framebuffer().width().intValue(),
                    this.frame.framebuffer().height().intValue());
            this.mainScreenItem =
                    new GameItem(new Texture2DModel(this.mainFramebuffer.getColorTexture()));
            this.mainScreenItem.scale().x.bind(this.mainFramebuffer.width());
            this.mainScreenItem.scale().y.bind(this.mainFramebuffer.height());
            this.mainScreenItem.position().x.bind(this.mainFramebuffer.width().divide(2));
            this.mainScreenItem.position().y.bind(this.mainFramebuffer.height().divide(2));

            this.mainScreenItemModel = this.mainScreenItem.createModel();

            this.contexthud = launcher.contextProvider()
                    .loadContext(this.mainFramebuffer, ContextType.HUD);
            ((GLESDrawContext) this.contexthud).swapTopBottom = true;

            launcher.guiManager().openGuiByClass(this.mainFramebuffer, MainScreenGui.class);

            //			updateScreenItems();

        }

        @Override
        public void cleanup0() throws GameException {
            this.mainScreenItemModel.cleanup();
            launcher.contextProvider().freeContext(this.contexthud, ContextType.HUD);
            this.mainFramebuffer.cleanup();
        }

        public void windowSizeChanged() throws GameException {
            this.mainFramebuffer.resize(this.frame.framebuffer().width().intValue(),
                    this.frame.framebuffer().height().intValue());
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
            cur.glViewport(0, 0, this.frame.framebuffer().width().intValue(),
                    this.frame.framebuffer().height().intValue());
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
            cur.glViewport(0, 0, this.frame.framebuffer().width().intValue(),
                    this.frame.framebuffer().height().intValue());
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

    @Override
    public void setRenderer(Renderer renderer) {
        this.renderer.set(renderer);
    }

    @Override
    public Renderer renderer() {
        return this.renderer.get();
    }

}
