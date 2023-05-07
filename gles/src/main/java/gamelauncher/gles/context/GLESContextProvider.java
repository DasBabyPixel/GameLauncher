package gamelauncher.gles.context;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.gles.GLES;

public class GLESContextProvider extends ContextProvider {
    private final GLES gles;

    public GLESContextProvider(GLES gles, GameLauncher launcher) {
        super(launcher);
        this.gles = gles;
    }

    @Override public DrawContext createContext(Framebuffer framebuffer) {
        return new GLESDrawContext(gles, framebuffer);
    }
}
