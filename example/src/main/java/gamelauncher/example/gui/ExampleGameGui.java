package gamelauncher.example.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;

public class ExampleGameGui extends ParentableAbstractGui {

	public ExampleGameGui(GameLauncher launcher) throws GameException {
		super(launcher);
		ButtonGui button = new ButtonGui(launcher) {

			@Override
			protected void buttonPressed(MouseButtonKeybindEntry e) {
				System.out.println("gayyy");
			}

		};
		button.getWidthProperty().bind(this.getWidthProperty());
		button.getHeightProperty().bind(this.getHeightProperty());
		button.getXProperty().bind(this.getXProperty());
		button.getYProperty().bind(this.getYProperty());
		button.text().setValue("Deine Mudda");
		this.GUIs.add(button);
	}
}
