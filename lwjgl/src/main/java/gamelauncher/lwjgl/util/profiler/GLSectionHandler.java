package gamelauncher.lwjgl.util.profiler;

import static org.lwjgl.opengles.GLES20.*;

import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.profiler.SectionHandler;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLSectionHandler implements SectionHandler {

	private static final Logger logger = Logger.logger("GL");

	@Override
	public void handleBegin(String type, String section) {
		if (StateRegistry.currentContext() != null) {
			int error = GlStates.current().getError();
			if (error != GL_NO_ERROR) {
				logger.errorf("OpenGL error detected: 0x%s%nType: %s, Section: %s%nError out of profiler render section",
						Integer.toHexString(error), type, section);
				logger.error(new Exception("Stack trace"));
			}
		}
	}

	@Override
	public void handleEnd(String type, String section, long tookNanos) {
	}

	@Override
	public void check(String type, String section) {
		if (StateRegistry.currentContext() != null) {
			int error = GlStates.current().getError();
			if (error != GL_NO_ERROR) {
				logger.errorf("OpenGL error detected: 0x%s%nType: %s, Section: %s", Integer.toHexString(error), type,
						section);
				logger.error(new Exception("Stack trace"));
			}
		}
	}

}
