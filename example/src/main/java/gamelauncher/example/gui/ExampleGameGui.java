package gamelauncher.example.gui;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.text.Component;

public class ExampleGameGui extends ParentableAbstractGui {

    public ExampleGameGui(GameLauncher launcher) throws GameException {
        super(launcher);
        ButtonGui button = launcher().guiManager().createGui(ButtonGui.class);
        button.onButtonPressed(event -> System.out.println("gaayyyy"));

        button.widthProperty().bind(this.widthProperty());
        button.heightProperty().bind(this.heightProperty());
        button.xProperty().bind(this.xProperty());
        button.yProperty().bind(this.yProperty());
        ((ButtonGui.Simple.TextForeground) button.foreground()).textGui().text().value(Component.text("Deine MuddalhagsDOiguzSAPIgh U?SiaNPviUHNvfiposAUHvPAuvUA+oVAMR)A0Rb+" + "aßr" + "bs+*RBmüaOPIR+ücoms+"));
        addGUI(button);
    }
}
