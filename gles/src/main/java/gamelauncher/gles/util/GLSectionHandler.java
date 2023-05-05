package gamelauncher.gles.util;

import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.profiler.SectionHandler;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;

public class GLSectionHandler implements SectionHandler {

    private static final Logger logger = Logger.logger("GL");

    @Override public void handleBegin(String type, String section) {
        if (StateRegistry.currentContext() != null) {
            int error = StateRegistry.currentGl().glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                logger.errorf("OpenGL error detected: 0x%s%nType: %s, Section: %s%nError out of profiler render section", Integer.toHexString(error), type, section);
                logger.error(new Exception("Stack trace"));
            }
        }
    }

    @Override public void handleEnd(String type, String section, long tookNanos) {
        if (StateRegistry.currentContext() != null) {
            int error = StateRegistry.currentGl().glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                logger.errorf("OpenGL error detected: 0x%s%nType: %s, Section: %s%nError out of profiler render section", Integer.toHexString(error), type, section);
                logger.error(new Exception("Stack trace"));
            }
        }
    }

    @Override public void check(String type, String section) {
        if (StateRegistry.currentContext() != null) {
            int error = StateRegistry.currentGl().glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                logger.errorf("OpenGL error detected: 0x%s%nType: %s, Section: %s", Integer.toHexString(error), type, section);
                logger.error(new Exception("Stack trace"));
            }
        }
    }
}
