package gamelauncher.lwjgl.gui.launcher;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.guis.GuiContainer;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.text.Component;
import gamelauncher.lwjgl.LWJGLGameLauncher;

import java.util.stream.Collectors;

/**
 * @author DasBabyPixel
 */
public class LWJGLMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

	public LWJGLMainScreenGui(LWJGLGameLauncher launcher) throws GameException {
		super(launcher);

		font = launcher().fontFactory().createFont(launcher().resourceLoader()
				.resource(launcher().embedFileSystem().getPath("fonts", "garamond_bold.ttf"))
				.newResourceStream());

		GuiContainer container = new GuiContainer(launcher);

		NumberValue spacing = NumberValue.withValue(5);
		NumberValue currentY = null;
		GameGui ogui = null;

		for (Game game : launcher.gameRegistry().games().stream().sorted()
				.collect(Collectors.toList())) {
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
		scrollGui.xProperty().bind(xProperty());
		scrollGui.yProperty().bind(yProperty());
		scrollGui.widthProperty().bind(widthProperty());
		scrollGui.heightProperty().bind(heightProperty());

		this.GUIs.add(scrollGui);
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof KeyboardKeybindEntry) {
			KeyboardKeybindEntry e = (KeyboardKeybindEntry) entry;
			e.keybind().uniqueId();
		}
		return super.doHandle(entry);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
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
			buttonGui.text().setValue(Component.text(game.key().key()));

			buttonGui.xProperty().bind(this.xProperty());
			buttonGui.yProperty().bind(this.yProperty());
			buttonGui.widthProperty().bind(this.widthProperty());
			buttonGui.heightProperty().bind(this.heightProperty());
			this.GUIs.add(buttonGui);
		}

	}

}
