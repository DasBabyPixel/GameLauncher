package gamelauncher.lwjgl.gui.impl;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	/**
	 * @param launcher
	 */
	public LWJGLMainScreenGui(GameLauncher launcher) {
		super(launcher);
	}

	@Override
	protected boolean doRender(Window window, float mouseX, float mouseY, float partialTick) throws GameException {
		System.out.println("Rendered MainScreenGui");
		return true;
	}
}
