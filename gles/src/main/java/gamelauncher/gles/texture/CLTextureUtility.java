package gamelauncher.gles.texture;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.framebuffer.GLESFramebuffer;
import gamelauncher.gles.states.ContextLocal;

public class CLTextureUtility extends AbstractGameResource {

    public final GLESFramebuffer framebuffer1;

    public final GLESFramebuffer framebuffer2;

    public CLTextureUtility(Frame frame) {
        this.framebuffer1 = new GLESFramebuffer(frame);
        this.framebuffer2 = new GLESFramebuffer(frame);
    }

    /**
     * @return a new {@link ContextLocal}
     */
    public static ContextLocal<CLTextureUtility> local(GLES gles) {
        return new TUContextLocal(gles);
    }

    @Override
    public void cleanup0() throws GameException {
        this.framebuffer1.cleanup();
        this.framebuffer2.cleanup();
    }

    private static class TUContextLocal extends ContextLocal<CLTextureUtility> {
        private final GLES gles;

        public TUContextLocal(GLES gles) {
            this.gles = gles;
        }

        @Override
        protected void valueRemoved(CLTextureUtility value) {
            try {
                value.cleanup();
            } catch (GameException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected CLTextureUtility initialValue() {
            return new CLTextureUtility(gles.launcher().frame());
        }
    }
}
