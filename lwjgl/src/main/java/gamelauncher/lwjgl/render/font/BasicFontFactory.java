package gamelauncher.lwjgl.render.font;

import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class BasicFontFactory implements FontFactory {

	@Override
	public Font createFont(ResourceStream stream) throws GameException {
		return new BasicFont(stream);
	}
}
