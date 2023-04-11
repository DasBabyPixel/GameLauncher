package gamelauncher.gles;

import gamelauncher.engine.util.concurrent.AbstractExecutorThread;

/**
 * @author DasBabyPixel
 */
public class GLESThreadGroup extends ThreadGroup {
    public GLESThreadGroup() {
        super("GL-Threads");
    }

    public void terminated(AbstractExecutorThread glThread) {
        // Utility for future?
    }
}
