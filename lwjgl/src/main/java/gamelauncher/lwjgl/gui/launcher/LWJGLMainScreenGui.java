package gamelauncher.lwjgl.gui.launcher;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.guis.GuiContainer;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.lwjgl.LWJGLGameLauncher;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	private static int id = 0;
	private Font font;

	public LWJGLMainScreenGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);

		font = launcher().fontFactory().createFont(launcher().resourceLoader()
				.resource(
						launcher().embedFileSystem().getPath("fonts", "garamond_bold.ttf"))
				.newResourceStream());

		GuiContainer container = new GuiContainer(launcher);

		NumberValue spacing = NumberValue.withValue(5);
		NumberValue currentY = null;
		GameGui ogui = null;

		for (Game game : launcher.gameRegistry().games().stream().sorted().toList()) {
			GameGui gui = new GameGui(game);
			if (currentY == null) {
				currentY = container.yProperty();
			} else {
				currentY = ogui.yProperty().add(ogui.heightProperty()).add(spacing);
			}
			gui.yProperty().bind(currentY);
			gui.xProperty().bind(container.xProperty());
			gui.height(80);
			gui.width(600);
			container.addGui(gui);
			ogui = gui;
		}

		ScrollGui scrollGui = launcher.guiManager().createGui(ScrollGui.class);
		scrollGui.gui().setValue(container);
		//		scrollGui.getXProperty().bind(this.getXProperty().add(this.getWidthProperty().divide(2))
		//				.subtract(scrollGui.getWidthProperty().divide(2)));
		//		scrollGui.getYProperty().bind(this.getYProperty().add(this.getHeightProperty().divide(2))
		//				.subtract(scrollGui.getHeightProperty().divide(2)));
		//		scrollGui.getWidthProperty().bind(this.getWidthProperty().divide(2));
		//		scrollGui.getHeightProperty().bind(this.getHeightProperty().divide(2));
		scrollGui.xProperty().bind(xProperty());
		scrollGui.yProperty().bind(yProperty());
		scrollGui.widthProperty().bind(widthProperty());
		scrollGui.heightProperty().bind(heightProperty());

		this.GUIs.add(scrollGui);
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof KeyboardKeybindEntry e) {
			e.keybind().uniqueId();
		}
		return super.doHandle(entry);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		font.cleanup();
	}

	private class GameGui extends ParentableAbstractGui {

		public GameGui(Game game) throws GameException {
			super(LWJGLMainScreenGui.this.launcher());
			ButtonGui buttonGui = new ButtonGui(launcher()) {

				@Override
				protected void buttonPressed(MouseButtonKeybindEntry e) {
					try {
						game.launch(this.framebuffer);
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}

			};
			buttonGui.text().setValue(game.key().key());
			buttonGui.font().setValue(font);

			buttonGui.xProperty().bind(this.xProperty());
			buttonGui.yProperty().bind(this.yProperty());
			buttonGui.widthProperty().bind(this.widthProperty());
			buttonGui.heightProperty().bind(this.heightProperty());
			this.GUIs.add(buttonGui);
		}

	}

}
