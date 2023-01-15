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
		button.widthProperty().bind(this.widthProperty());
		button.heightProperty().bind(this.heightProperty());
		button.xProperty().bind(this.xProperty());
		button.yProperty().bind(this.yProperty());
		button.text().setValue("Deine Mudda");
		this.GUIs.add(button);
	}
}
