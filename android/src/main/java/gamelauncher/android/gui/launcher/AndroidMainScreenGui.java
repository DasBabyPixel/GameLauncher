package gamelauncher.android.gui.launcher;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.guis.GuiContainer;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.gui.launcher.TextureGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.text.Component;

import java.util.stream.Collectors;

/**
 * @author DasBabyPixel
 */
public class AndroidMainScreenGui extends ParentableAbstractGui implements MainScreenGui {

    public AndroidMainScreenGui(GameLauncher launcher) throws GameException {
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

        ScrollGui scrollGui = launcher.guiManager().createGui(ScrollGui.class);
        scrollGui.gui().setValue(container);
        scrollGui.xProperty().bind(xProperty());
        scrollGui.yProperty().bind(yProperty());
        scrollGui.widthProperty().bind(widthProperty());
        scrollGui.heightProperty().bind(heightProperty());

        this.GUIs.add(scrollGui);

        TextureGui textureGui = launcher.guiManager().createGui(TextureGui.class);
        textureGui.texture().uploadAsync(launcher.resourceLoader().resource(launcher.embedFileSystem().getPath("pixel64x64.png")).newResourceStream()).thenRun(() -> {
            System.out.println("upload complete");
        });
        textureGui.heightProperty().bind(heightProperty());
        textureGui.widthProperty().bind(heightProperty());
        textureGui.xProperty().bind(xProperty().add(widthProperty().subtract(textureGui.widthProperty()).divide(2)));
//        textureGui.xProperty().bind(xProperty());
        textureGui.yProperty().bind(yProperty());

        GUIs.addFirst(textureGui);
    }

    @Override
    protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
        System.out.println("mainscreenredner");
        return super.doRender(framebuffer, mouseX, mouseY, partialTick);
    }

    @Override
    protected boolean doHandle(KeybindEvent entry) throws GameException {
        if (entry instanceof KeyboardKeybindEvent) {
            KeyboardKeybindEvent e = (KeyboardKeybindEvent) entry;
            e.keybind().uniqueId();
        }
        return super.doHandle(entry);
    }

    private class GameGui extends ParentableAbstractGui {

        public GameGui(Game game) throws GameException {
            super(AndroidMainScreenGui.this.launcher());
            ButtonGui buttonGui = new ButtonGui(launcher()) {

                @Override
                protected void buttonPressed(MouseButtonKeybindEvent e) {
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
