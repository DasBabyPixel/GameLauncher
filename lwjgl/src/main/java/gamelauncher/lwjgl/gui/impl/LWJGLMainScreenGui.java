package gamelauncher.lwjgl.gui.impl;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.lwjgl.LWJGLGameLauncher;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	@SuppressWarnings("unused")
	private final LWJGLGameLauncher launcher;

	/**
	 * @param launcher
	 * @throws GameException
	 */
	public LWJGLMainScreenGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);
		this.launcher = launcher;
		ButtonGui buttonGui = new ButtonGui(launcher);
		buttonGui.getXProperty()
				.bind(getXProperty().add(getWidthProperty().divide(2))
						.subtract(buttonGui.getWidthProperty().divide(2)));
		buttonGui.getYProperty()
				.bind(getYProperty().add(getHeightProperty().divide(2))
						.subtract(buttonGui.getHeightProperty().divide(2)));
		buttonGui.getWidthProperty().bind(getWidthProperty().divide(2));
		buttonGui.getHeightProperty().bind(getHeightProperty().divide(2));
		GUIs.add(buttonGui);
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		return super.doHandle(entry);
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		System.out.println("Initialized");

	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		System.out.println("Cleaned up");
//		model.cleanup();
	}

}
