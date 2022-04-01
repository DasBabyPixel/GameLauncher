package gamelauncher.lwjgl.render;

import java.awt.Color;

import org.joml.Matrix4f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Transformations;
import gamelauncher.lwjgl.render.GameItem.GameItemModel;
import gamelauncher.lwjgl.render.Mesh.MeshModel;

public class LWJGLDrawContext implements DrawContext {

	private final LWJGLWindow window;
	private final double tx, ty, tz;
	private final double sx, sy, sz;
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final Matrix4f transformationMatrix = new Matrix4f();
	private ShaderProgram shaderProgram;

	public LWJGLDrawContext(LWJGLWindow window) {
		this(window, 0, 0, 0, 1, 1, 1);
	}

	public LWJGLDrawContext(LWJGLWindow window, double tx, double ty, double tz, double sx, double sy, double sz) {
		this.window = window;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
	}

	@Override
	public void setProjectionMatrix(Transformations.Projection projection) throws GameException {
		if (projection instanceof Transformations.Projection.Projection3D) {
			Transformations.Projection.Projection3D p3d = (Transformations.Projection.Projection3D) projection;
			float aspectRatio = (float) window.framebufferWidth.get() / (float) window.framebufferHeight.get();
			projectionMatrix.setPerspective(p3d.fov, aspectRatio, p3d.zNear, p3d.zFar);
		}
	}

//	@Override
//	public void setViewMatrix(Transformations.Transformation view) throws GameException {
//		throw new UnsupportedOperationException();
//		if (view instanceof Transformations.Transformation.BasicTransformation) {
//			Transformations.Transformation.BasicTransformation bv = (Transformations.Transformation.BasicTransformation) view;
//			transformationMatrix.identity().translate(bv.offsetX, bv.offsetY, bv.offsetZ);
//			transformationMatrix.rotateX((float) Math.toRadians(bv.rotationX));
//			transformationMatrix.rotateY((float) Math.toRadians(bv.rotationY));
//			transformationMatrix.rotateZ((float) Math.toRadians(bv.rotationZ));
//			transformationMatrix.scale(bv.scaleX, bv.scaleY, bv.scaleZ);
//		}
//	}

	@Override
	public void drawRect(double x, double y, double w, double h, Color color) {

	}

	@Override
	public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {

	}

	@Override
	public void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz)
			throws GameException {
		transformationMatrix.identity();
		shaderProgram.bind();
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		if (model instanceof MeshModel) {
			Mesh mesh = ((MeshModel) model).mesh;
			shaderProgram.setUniform("transformationMatrix", transformationMatrix);
			mesh.render();
		} else if (model instanceof GameItemModel) {
			GameItem item = ((GameItemModel) model).gameItem;
			item.applyToTransformationMatrix(transformationMatrix);
			shaderProgram.setUniform("transformationMatrix", transformationMatrix);
			item.getMesh().render();
		}
		shaderProgram.unbind();
	}

	@Override
	public DrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(window, tx + x, ty + y, tz + z, sx, sy, sz);
	}

	@Override
	public DrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(window, tx, ty, tz, sx * x, sy * y, sz * z);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z) throws GameException {
		drawModel(model, x, y, z, 0, 0, 0);
	}

	public void setProgram(ShaderProgram program) {
		this.shaderProgram = program;
	}

	public ShaderProgram getProgram() {
		return shaderProgram;
	}
}
