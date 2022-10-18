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

		NumberValue currentY = container.getYProperty().subtract(this.spacing);

		for (Game game : launcher.getGameRegistry().getGames()) {
			GameGui gui = new GameGui(game);
			gui.getYProperty().bind(currentY.add(this.spacing));
			currentY = gui.getYProperty().add(gui.getHeightProperty());
			gui.getXProperty().bind(container.getXProperty());
			gui.setHeight(600);
			gui.setWidth(3000);
			container.addGui(gui);
		}

		ScrollGui scrollGui = launcher.getGuiManager().createGui(ScrollGui.class);
		scrollGui.gui().setValue(container);
		scrollGui.getXProperty()
				.bind(this.getXProperty()
						.add(this.getWidthProperty().divide(2))
						.subtract(scrollGui.getWidthProperty().divide(2)));
		scrollGui.getYProperty()
				.bind(this.getYProperty()
						.add(this.getHeightProperty().divide(2))
						.subtract(scrollGui.getHeightProperty().divide(2)));
		scrollGui.getWidthProperty().bind(this.getWidthProperty().divide(2));
		scrollGui.getHeightProperty().bind(this.getHeightProperty().divide(2));

		this.GUIs.add(scrollGui);
	}

	private class GameGui extends ParentableAbstractGui {

		public GameGui(Game game) throws GameException {
			super(LWJGLMainScreenGui.this.getLauncher());
			ButtonGui buttonGui = new ButtonGui(this.getLauncher()) {

				@Override
				protected void buttonPressed(MouseButtonKeybindEntry e) {
					try {
						game.launch(this.framebuffer);
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}

			};
			buttonGui.text().setValue(game.getKey().toString());
			buttonGui.getXProperty().bind(this.getXProperty());
			buttonGui.getYProperty().bind(this.getYProperty());
			buttonGui.getWidthProperty().bind(this.getWidthProperty());
			buttonGui.getHeightProperty().bind(this.getHeightProperty());
			this.GUIs.add(buttonGui);
		}

	}

}
