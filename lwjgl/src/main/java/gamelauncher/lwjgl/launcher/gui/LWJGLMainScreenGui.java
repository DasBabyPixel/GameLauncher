package gamelauncher.lwjgl.launcher.gui;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.guis.GuiContainer;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.launcher.gui.ScrollGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.lwjgl.LWJGLGameLauncher;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	private final NumberValue spacing = NumberValue.withValue(5);

	public LWJGLMainScreenGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);

		GuiContainer container = new GuiContainer(launcher);

		NumberValue currentY = container.getYProperty().subtract(spacing);

		for (Game game : launcher.getGameRegistry().getGames()) {
			GameGui gui = new GameGui(game);
			gui.getYProperty().bind(currentY.add(spacing));
			currentY = gui.getYProperty().add(gui.getHeightProperty());
			gui.getXProperty().bind(container.getXProperty());
			gui.setHeight(50);
			gui.setWidth(300);
			container.addGui(gui);
		}
		System.out.println(container);

		ScrollGui scrollGui = launcher.getGuiManager().createGui(ScrollGui.class);
		scrollGui.gui().setValue(container);
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

	private class GameGui extends ParentableAbstractGui {

		public GameGui(Game game) throws GameException {
			super(LWJGLMainScreenGui.this.getLauncher());
			ButtonGui buttonGui = new ButtonGui(getLauncher()) {

				@Override
				protected void buttonPressed(MouseButtonKeybindEntry e) {
					System.out.println("press");
					super.buttonPressed(e);
				}

			};
			buttonGui.text().setValue(game.getKey().toString());
			buttonGui.getXProperty().bind(getXProperty());
			buttonGui.getYProperty().bind(getYProperty());
			buttonGui.getWidthProperty().bind(getWidthProperty());
			buttonGui.getHeightProperty().bind(getHeightProperty());
			GUIs.add(buttonGui);
		}

	}

}
