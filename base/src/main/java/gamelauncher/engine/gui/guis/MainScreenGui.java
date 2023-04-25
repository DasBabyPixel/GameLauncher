/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.text.Component;

import java.util.stream.Collectors;

/**
 * The {@link MainScreenGui} when the {@link GameLauncher} is started
 *
 * @author DasBabyPixel
 */
public interface MainScreenGui extends Gui {

    class Simple extends ParentableAbstractGui implements MainScreenGui {

        public Simple(GameLauncher launcher) throws GameException {
            super(launcher);

            GuiContainer container = new GuiContainer(launcher);
            NumberValue spacing = NumberValue.withValue(5);
            NumberValue currentY = null;
            GameGui ogui = null;

            for (Game game : launcher.gameRegistry().games().stream().sorted().collect(Collectors.toList())) {
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

            ColorGui colorGui = launcher.guiManager().createGui(ColorGui.class);
            colorGui.xProperty().bind(xProperty());
            colorGui.yProperty().bind(yProperty());
            colorGui.widthProperty().bind(widthProperty());
            colorGui.heightProperty().bind(heightProperty());
            colorGui.color().set(1, 0.5F, 0F, 1);
            GUIs.add(colorGui);

            TextureGui textureGui = launcher.guiManager().createGui(TextureGui.class);
            textureGui.texture().uploadAsync(launcher.resourceLoader().resource(launcher.embedFileSystem().getPath("pixel64x64.png")).newResourceStream());
            textureGui.heightProperty().bind(heightProperty());
            textureGui.widthProperty().bind(heightProperty());
            textureGui.xProperty().bind(xProperty().add(widthProperty().subtract(textureGui.widthProperty()).divide(2)));
            textureGui.yProperty().bind(yProperty());
            GUIs.add(textureGui);

//            ScrollGui scrollGui = launcher.guiManager().createGui(ScrollGui.class);
//            scrollGui.gui().setValue(container);
//            scrollGui.xProperty().bind(xProperty());
//            scrollGui.yProperty().bind(yProperty());
//            scrollGui.widthProperty().bind(widthProperty());
//            scrollGui.heightProperty().bind(heightProperty());
//
//            GUIs.add(scrollGui);

        }

        private class GameGui extends ParentableAbstractGui {

            public GameGui(Game game) throws GameException {
                super(Simple.this.launcher());
                ButtonGui buttonGui = new ButtonGui(launcher()) {

                    @Override protected void buttonPressed(MouseButtonKeybindEvent e) {
                        try {
                            game.launch(this.framebuffer);
                        } catch (GameException ex) {
                            ex.printStackTrace();
                        }
                    }

                };
                buttonGui.text().value(Component.text(game.key().key()));

                buttonGui.xProperty().bind(this.xProperty());
                buttonGui.yProperty().bind(this.yProperty());
                buttonGui.widthProperty().bind(this.widthProperty());
                buttonGui.heightProperty().bind(this.heightProperty());
                this.GUIs.add(buttonGui);
            }

        }
    }
}
