package gamelauncher.labyrinth;

import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.model.Model;
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
				this.getLauncher().getGuiManager()
						.openGui(framebuffer, new ParentableAbstractGui(this.getLauncher()) {

							{
								ButtonGui g = new ButtonGui(this.getLauncher()) {

									@Override
									protected void buttonPressed(
											gamelauncher.engine.util.keybind.MouseButtonKeybindEntry e)
											throws GameException {
										this.getLauncher().getGuiManager().openGui(this.framebuffer,
												new ParentableAbstractGui(this.getLauncher()) {

													{
														ColorGui g =
																this.getLauncher().getGuiManager()
																		.createGui(ColorGui.class);
														g.getColor().set(1, 0, 1, 0.5F);
														g.getWidthProperty()
																.bind(this.getWidthProperty());
														g.getHeightProperty()
																.bind(this.getHeightProperty());
														g.getXProperty().bind(this.getXProperty());
														g.getYProperty().bind(this.getYProperty());
														this.GUIs.add(g);
													}

												});
									}

								};
								g.getWidthProperty().bind(this.getWidthProperty().divide(2));
								g.getHeightProperty().bind(this.getHeightProperty().divide(2));
								g.getXProperty().bind(this.getXProperty()
										.add(g.getWidthProperty().divide(2)));
								g.getYProperty().bind(this.getYProperty()
										.add(g.getHeightProperty().divide(2)));
								g.text().setValue("labyrinth game lol test");
								this.GUIs.add(g);
							}

						});
			}

			@Override
			protected void close0() throws GameException {
			}

		});
		getLauncher().getGameRegistry().register(new Game(this, "g1") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
				getLauncher().getGuiManager().openGui(framebuffer, new ParentableAbstractGui(getLauncher()) {

					private Model model;
					private DrawContext context;

					@Override
					protected void doInit(Framebuffer framebuffer) throws GameException {
						context = getLauncher().getContextProvider().loadContext(framebuffer, ContextProvider.ContextType.HUD);
						model = getLauncher().getModelLoader().loadModel(getLauncher().getResourceLoader().getResource(
								getLauncher().getEmbedFileSystem().getPath("cube.obj")));
						GameItem item = new GameItem(model);
						item.setPosition(300,300,0);
						item.setScale(400);
						item.setRotation(40,40,40);
						model = item.createModel();
					}

					@Override
					protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
						context.update(EmptyCamera.instance());
						context.drawModel(model);
						return true;
					}

					@Override
					protected void doCleanup(Framebuffer framebuffer) throws GameException {
						getLauncher().getContextProvider().freeContext(context, ContextProvider.ContextType.HUD);
						model.cleanup();
					}
				});
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		getLauncher().getGameRegistry().register(new Game(this, "g2") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		getLauncher().getGameRegistry().register(new Game(this, "g3") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		getLauncher().getGameRegistry().register(new Game(this, "g4") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
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
