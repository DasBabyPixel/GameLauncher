package gamelauncher.engine.gui.guis;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry.Type;

/**
 * @author DasBabyPixel
 */
public class ButtonGui extends ParentableAbstractGui {

	/**
	 * @param launcher
	 * @throws GameException
	 */
	public ButtonGui(GameLauncher launcher) throws GameException {
		super(launcher);
		setWidth(100);
		setHeight(50);
		TextGui textGui = new TextGui(launcher, "TbuttonteQst", 50);
		textGui.getXProperty()
				.bind(getXProperty().add(getWidthProperty().divide(2)).subtract(textGui.getWidthProperty().divide(2)));
		textGui.getYProperty()
				.bind(getYProperty().add(getHeightProperty().divide(2))
						.subtract(textGui.getHeightProperty().divide(2)));
//		textGui.getWidthProperty().bind(getWidthProperty());
		textGui.getHeightProperty().bind(getHeightProperty());
		this.GUIs.add(textGui);
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof MouseButtonKeybindEntry) {
			MouseButtonKeybindEntry mb = (MouseButtonKeybindEntry) entry;
			System.out.println(mb);
			if (mb.type() == Type.RELEASE) {
				buttonPressed(mb);
			} else if(mb.type()==Type.PRESS) {
			}
		}
		return super.doHandle(entry);
	}

	private void buttonPressed(MouseButtonKeybindEntry e) {
		System.out.println("ButtonPressed");
	}

}
