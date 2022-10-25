package gamelauncher.labyrinth;

import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

@GamePlugin
public class Labyrinth extends Plugin {

	public final Key gkey;

	public Labyrinth() {
		super("labyrinth");
		this.gkey = new Key(this, "labyrinthgame");
	}

	@Override
	public void onEnable() throws GameException {
		this.getLauncher().getGameRegistry().register(new Game(this.gkey) {

			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
				this.getLauncher().getGuiManager().openGui(framebuffer, new ParentableAbstractGui(this.getLauncher()) {

					{
						ButtonGui g = new ButtonGui(this.getLauncher()) {

							@Override
							protected void buttonPressed(gamelauncher.engine.util.keybind.MouseButtonKeybindEntry e) {
								System.out.println("pressed");
							}

						};
						g.getWidthProperty().bind(this.getWidthProperty().divide(2));
						g.getHeightProperty().bind(this.getHeightProperty().divide(2));
						g.getXProperty().bind(this.getXProperty().add(g.getWidthProperty().divide(2)));
						g.getYProperty().bind(this.getYProperty().add(g.getHeightProperty().divide(2)));
						g.text().setValue("labyrinth game lol test");
						this.GUIs.add(g);
					}

				});
			}

			@Override
			protected void close0() throws GameException {
			}

		});
	}

	@Override
	public void onDisable() {
		this.getLauncher().getGameRegistry().unregister(this.gkey);
	}

}
