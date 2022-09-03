package gamelauncher.lwjgl.launcher.gui;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.launcher.gui.ScrollGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
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
		ButtonGui buttonGui = new ButtonGui(launcher) {

			@Override
			protected void buttonPressed(MouseButtonKeybindEntry e) {
				System.out.println("Button pressed");
			}

		};
//		buttonGui.getXProperty()
//				.bind(getXProperty().add(getWidthProperty().divide(1))
//						.subtract(buttonGui.getWidthProperty().divide(2)));
//		buttonGui.getYProperty()
//				.bind(getYProperty().add(getHeightProperty().divide(1))
//						.subtract(buttonGui.getHeightProperty().divide(2)));
		buttonGui.getWidthProperty().setNumber(800);
		buttonGui.getHeightProperty().setNumber(300);
		buttonGui.text()
				.bind(getWidthProperty().multiply(getHeightProperty()).map(n -> Integer.toString(n.intValue())));
		ScrollGui scrollGui = launcher.getGuiManager().createGui(ScrollGui.class);
		scrollGui.gui().setValue(buttonGui);
		scrollGui.getXProperty()
				.bind(getXProperty().add(getWidthProperty().divide(2))
						.subtract(scrollGui.getWidthProperty().divide(2)));
		scrollGui.getYProperty()
				.bind(getYProperty().add(getHeightProperty().divide(2))
						.subtract(scrollGui.getHeightProperty().divide(2)));
		scrollGui.getWidthProperty().bind(getWidthProperty().divide(2));
		scrollGui.getHeightProperty().bind(getHeightProperty().divide(2));

		GUIs.add(scrollGui);
	}

}
