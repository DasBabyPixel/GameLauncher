package gamelauncher.lwjgl.gui.launcher;

import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.LineGui;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.EmptyCamera;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.mesh.Mesh;
import gamelauncher.lwjgl.render.model.MeshModel;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;

public class LWJGLLineGui extends ParentableAbstractGui implements LineGui {
	private final NumberValue fromX = NumberValue.zero();
	private final NumberValue fromY = NumberValue.zero();
	private final NumberValue toX = NumberValue.zero();
	private final NumberValue toY = NumberValue.zero();
	private boolean arrow = true;
	private DrawContext context;
	private GameItem arrowItem;
	private Model arrowModel;
	private GameItem lineItem;
	private Model lineModel;
	private float lineWidth = 200;

	public LWJGLLineGui(GameLauncher launcher) throws GameException {
		super(launcher);
		NumberValue x = NumberValue.zero();
		NumberValue y = NumberValue.zero();
		NumberValue w = NumberValue.zero();
		NumberValue h = NumberValue.zero();
		this.xProperty().bind(x);
		this.yProperty().bind(y);
		this.widthProperty().bind(w);
		this.heightProperty().bind(h);

		NumberInvalidationListener invalidationListener = numberValue -> {
			Vector2f to = new Vector2f(toX.floatValue(), toY.floatValue());
			Vector2f from = new Vector2f(fromX.floatValue(), fromY.floatValue());
			Vector2f direction = to.sub(from, new Vector2f());
			if (arrowItem != null) {
				arrowItem.scale(20, 20, 0);
				arrowItem.position(to.x, to.y, 0);
				arrowItem.rotation(0, 0,
						(float) Math.toDegrees(direction.angle(new Vector2f(0, 1))));
			}
			if (lineItem != null) {
				lineItem.position(from.x, from.y, 0);
				lineItem.scale(lineWidth / 100, direction.length(), 0);
				lineItem.rotation(0, 0,
						(float) Math.toDegrees(direction.angle(new Vector2f(0, 1))));
			}
			float minX = Math.min(from.x, to.x) - lineWidth;
			float minY = Math.min(from.y, to.y) - lineWidth;
			x.setNumber(minX);
			y.setNumber(minY);
			w.setNumber(Math.abs(from.x - to.x) + lineWidth * 2);
			h.setNumber(Math.abs(from.y - to.y) + lineWidth * 2);
			redraw();
		};
		fromX.addListener(invalidationListener);
		fromY.addListener(invalidationListener);
		toX.addListener(invalidationListener);
		toY.addListener(invalidationListener);
	}

	@Override
	public NumberValue fromX() {
		return fromX;
	}

	@Override
	public NumberValue fromY() {
		return fromY;
	}

	@Override
	public NumberValue toX() {
		return toX;
	}

	@Override
	public NumberValue toY() {
		return toY;
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		super.doCleanup(framebuffer);
		launcher().contextProvider().freeContext(context, ContextType.HUD);
		lineModel.cleanup();
		if (arrowModel != null) {
			arrowModel.cleanup();
			arrowModel = null;
		}
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		super.doInit(framebuffer);
		context = launcher().contextProvider().loadContext(framebuffer, ContextType.HUD);
		if (arrow) {

			Mesh mesh = new Mesh(new float[] {-0.5F, -0.5F, 0, 0, 0.5F, 0, 0.5F, -0.5F, 0},
					new float[] {0, 0, 0, 0, 0, 0}, new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[] {0, 2, 1}, GL_TRIANGLES);
			Mesh.Material mat = mesh.material();
			mat.ambientColour = mat.diffuseColour = mat.specularColour = new Vector4f(1, 0, 0, 1);
			MeshModel model = new MeshModel(mesh);
			arrowItem = new GameItem(model);
			arrowItem.color().set(1, 1, 0, 1);
			arrowModel = arrowItem.createModel();
		}
		Mesh mesh = new Mesh(new float[] {-0.5F, 0, 0, -0.5F, 1, 0, 0.5F, 1, 0, 0.5F, 0, 0},
				new float[] {0, 0, 0, 0, 0, 0, 0, 0,},
				new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,}, new int[] {0, 3, 2, 0, 2, 1,},
				GL_TRIANGLES);
		Mesh.Material mat = mesh.material();
		mat.ambientColour = mat.diffuseColour = mat.specularColour = new Vector4f(1, 0, 0, 1);
		MeshModel model = new MeshModel(mesh);
		lineItem = new GameItem(model);
		lineItem.color().set(1, 1, 1, 1);
		lineModel = lineItem.createModel();
		fromX.invalidate();
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
		context.update(EmptyCamera.instance());
		context.drawModel(lineModel);
		if (arrowModel != null) {
			context.drawModel(arrowModel);
		}
		context.program().clearUniforms();
		return super.doRender(framebuffer, mouseX, mouseY, partialTick);
	}
}
