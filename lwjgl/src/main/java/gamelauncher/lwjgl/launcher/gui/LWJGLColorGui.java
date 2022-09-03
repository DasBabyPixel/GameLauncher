package gamelauncher.lwjgl.launcher.gui;

import org.joml.Vector4f;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.ColorGui;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.EmptyCamera;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.lwjgl.render.mesh.Mesh;
import gamelauncher.lwjgl.render.mesh.PlaneMesh;
import gamelauncher.lwjgl.render.model.MeshModel;

/**
 * @author DasBabyPixel
 */
public class LWJGLColorGui extends ParentableAbstractGui implements ColorGui {

	private final PropertyVector4f color;

	private GameItemModel model;

	private DrawContext context;

	/**
	 * @param launcher
	 */
	public LWJGLColorGui(GameLauncher launcher) {
		super(launcher);
		color = new PropertyVector4f(0, 0, 0, 0);
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		context = getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
		
		Mesh mesh = new PlaneMesh();
		Mesh.Material mat = mesh.getMaterial();
		mat.ambientColour = mat.diffuseColour = mat.specularColour = new Vector4f(1, 0, 0, 1);
		MeshModel model = new MeshModel(mesh);
		GameItem item = new GameItem(model);
		item.position().x.bind(getXProperty().add(getWidthProperty().divide(2)));
		item.position().y.bind(getYProperty().add(getHeightProperty().divide(2)));
		item.scale().x.bind(getWidthProperty());
		item.scale().y.bind(getHeightProperty());
		this.model = item.createModel();

		item.color().x.bind(color.x);
		item.color().y.bind(color.y);
		item.color().z.bind(color.z);
		item.color().w.bind(color.w);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		context.update(EmptyCamera.instance());
		context.drawModel(model);
		context.getProgram().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		getLauncher().getContextProvider().freeContext(context, ContextType.HUD);
		this.model.cleanup();
	}

	@Override
	public PropertyVector4f getColor() {
		return color;
	}

}
