package gamelauncher.labyrinth;

import de.dasbabypixel.api.property.NumberChangeListener;
import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
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
import gamelauncher.engine.util.text.Component;

@GamePlugin
public class Labyrinth extends Plugin {

	public final Key gkey;

	public Labyrinth() {
		super("labyrinth");
		this.gkey = new Key(this, "labyrinthgame");
	}

	@Override
	public void onEnable() throws GameException {
		this.launcher().gameRegistry().register(new Game(this.gkey) {

			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
				this.launcher().guiManager()
						.openGui(framebuffer, new ParentableAbstractGui(this.launcher()) {

							{
								ButtonGui g = new ButtonGui(this.launcher()) {

									@Override
									protected void buttonPressed(
											gamelauncher.engine.util.keybind.MouseButtonKeybindEntry e)
											throws GameException {
										this.launcher().guiManager().openGui(this.framebuffer,
												new ParentableAbstractGui(this.launcher()) {

													{
														ColorGui g = this.launcher().guiManager()
																.createGui(ColorGui.class);
														g.color().set(1, 0, 1, 0.5F);
														g.widthProperty()
																.bind(this.widthProperty());
														g.heightProperty()
																.bind(this.heightProperty());
														g.xProperty().bind(this.xProperty());
														g.yProperty().bind(this.yProperty());
														this.GUIs.add(g);
													}

												});
									}

								};
								g.widthProperty().bind(this.widthProperty().divide(2));
								g.heightProperty().bind(this.heightProperty().divide(2));
								g.xProperty()
										.bind(this.xProperty().add(g.widthProperty().divide(2)));
								g.yProperty()
										.bind(this.yProperty().add(g.heightProperty().divide(2)));
								g.text().setValue(Component.text("labyrinth game lol test"));
								this.GUIs.add(g);
							}

						});
			}

			@Override
			protected void close0() throws GameException {
			}

		});
		launcher().gameRegistry().register(new Game(this, "g1") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
				launcher().guiManager().openGui(framebuffer, new ParentableAbstractGui(launcher()) {

					private Model model;
					private DrawContext context;

					@Override
					protected void doInit(Framebuffer framebuffer) throws GameException {
						context = launcher().contextProvider()
								.loadContext(framebuffer, ContextProvider.ContextType.HUD);
						model = launcher().modelLoader().loadModel(launcher().resourceLoader()
								.resource(launcher().embedFileSystem().getPath("cube.obj")));
						GameItem item = new GameItem(model);
						item.rotation(40, 40, 40);
						item.position().x.bind(framebuffer.height().divide(2));
						item.position().y.bind(item.position().x);
						item.scale().x.bind(framebuffer.height().divide(2));
						item.scale().y.bind(item.scale().x);
						item.scale().z.bind(item.scale().x);
						model = item.createModel();
					}

					@Override
					protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
							float partialTick) throws GameException {
						context.update(EmptyCamera.instance());
						context.drawModel(model);
						return true;
					}

					@Override
					protected void doCleanup(Framebuffer framebuffer) throws GameException {
						launcher().contextProvider()
								.freeContext(context, ContextProvider.ContextType.HUD);
						model.cleanup();
					}
				});
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		launcher().gameRegistry().register(new Game(this, "g2") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		launcher().gameRegistry().register(new Game(this, "g3") {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
			}

			@Override
			protected void close0() throws GameException {
			}
		});
		launcher().gameRegistry().register(new Game(this, "g4") {
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
		this.launcher().gameRegistry().unregister(this.gkey);
	}

}
