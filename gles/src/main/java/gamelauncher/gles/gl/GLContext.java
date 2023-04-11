package gamelauncher.gles.gl;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

public interface GLContext extends GameResource {

    /**
     * Make this context active in the current thread.<br>
     * <b>Behaviour when another context is already active is undefined</b>
     */
    void makeCurrent();

    /**
     * If the context is active in the current thread, then make it not active
     */
    void destroyCurrent();

    /**
     * @return the frame for this context. This frame will be destroyed when the context is destroyed
     */
    Frame frame();

    /**
     * @return a new context that shares resources with this context
     */
    GLContext createSharedContext() throws GameException;

    GLES20 gl20();

    GLES30 gl30();

    GLES31 gl31();

    GLES32 gl32();

}
