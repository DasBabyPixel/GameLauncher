package gamelauncher.lwjgl.render;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.lwjgl.render.GameItem.GameItemModel;
import gamelauncher.lwjgl.render.Mesh.MeshModel;
import gamelauncher.lwjgl.render.light.DirectionalLight;
import gamelauncher.lwjgl.render.light.PointLight;

public class LWJGLDrawContext implements DrawContext {

	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	private final LWJGLWindow window;
	private final double tx, ty, tz;
	private final double sx, sy, sz;
	private final Matrix4f projectionMatrix;
	private final Matrix4f modelMatrix = new Matrix4f();
	private final Matrix4f viewMatrix;
	private final Matrix4f tempMatrix = new Matrix4f();
	private final AtomicReference<ShaderProgram> shaderProgram;
	private final AtomicReference<Projection> projection;

	public LWJGLDrawContext(LWJGLWindow window) {
		this(window, 0, 0, 0, 1, 1, 1);
	}

	private LWJGLDrawContext(LWJGLWindow window, double tx, double ty, double tz, double sx, double sy, double sz) {
		this(window, tx, ty, tz, sx, sy, sz, new AtomicReference<>(), new Matrix4f(), new Matrix4f(),
				new AtomicReference<>());
	}

	private LWJGLDrawContext(LWJGLWindow window, double tx, double ty, double tz, double sx, double sy, double sz,
			AtomicReference<ShaderProgram> shaderProgram, Matrix4f projectionMatrix, Matrix4f viewMatrix,
			AtomicReference<Projection> projection) {
		this.window = window;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.shaderProgram = shaderProgram;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = viewMatrix;
		this.projection = projection;
	}

	@Override
	public void setProjection(Transformations.Projection projection) throws GameException {
		if (this.projection.getAndSet(projection) != projection) {
			reloadProjectionMatrix();
		}
	}

	@Override
	public Projection getProjection() {
		return projection.get();
	}

	@Override
	public void reloadProjectionMatrix() throws GameException {
		Projection projection = this.projection.get();
		if (projection instanceof Transformations.Projection.Projection3D) {
			Transformations.Projection.Projection3D p3d = (Transformations.Projection.Projection3D) projection;
			float aspectRatio = (float) window.framebufferWidth.get() / (float) window.framebufferHeight.get();
			projectionMatrix.setPerspective(p3d.fov, aspectRatio, p3d.zNear, p3d.zFar);
		}
	}

	@Override
	public void drawRect(double x, double y, double w, double h, Color color) {

	}

	@Override
	public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {

	}

	@Override
	public void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz)
			throws GameException {
		drawModel(model, x, y, z, rx, ry, rz, 1, 1, 1);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx,
			double sy, double sz) throws GameException {
		modelMatrix.identity();
		pDrawModel(model, x, y, z, rx, ry, rz, sx, sy, sz);
	}

	private void pDrawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx,
			double sy, double sz) throws GameException {
		Mesh mesh = null;
		if (model instanceof MeshModel) {
			mesh = ((MeshModel) model).mesh;
		} else if (model instanceof GameItemModel) {
			GameItem item = ((GameItemModel) model).gameItem;
			item.applyToTransformationMatrix(modelMatrix);
			pDrawModel(item.getModel(), x, y, z, rx, ry, rz, sx, sy, sz);
			return;
		}
		modelMatrix.translate((float) (x + this.tx), (float) (y + this.ty), (float) (z + this.tz));
		modelMatrix.rotateXYZ((float) Math.toRadians(-rx), (float) Math.toRadians(-ry), (float) Math.toRadians(-rz));
		modelMatrix.scale((float) sx, (float) sy, (float) sz);
		drawMesh(mesh);
	}

	@Override
	public DrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(window, tx + x, ty + y, tz + z, sx, sy, sz, shaderProgram, projectionMatrix,
				viewMatrix, projection);
	}

	@Override
	public DrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(window, tx, ty, tz, sx * x, sy * y, sz * z, shaderProgram, projectionMatrix,
				viewMatrix, projection);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z) throws GameException {
		drawModel(model, x, y, z, 0, 0, 0);
	}

	float reflectance = 5F;
	float lightIntensity = 40F;
	Vector3f ambientLight = new Vector3f(.1F);
	Vector3f lightPosition = new Vector3f(2, 2, 2);
	Vector3f lightColor = new Vector3f(1, 1, 1);
	float specularPower = 200;
	PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity,
			new PointLight.Attenuation(0, 0, 1));
	DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1);
	float lightAngle = 0;

	@Override
	public void update(Camera camera) throws GameException {
		loadViewMatrix(camera);
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.setUniform("viewMatrix", viewMatrix);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		shaderProgram.setUniform("texture_sampler", 0);

		float pow = (float) (Math.sin(System.currentTimeMillis() / 1000D) + 1) * 200;
		shaderProgram.setUniform("specularPower", pow);

		PointLight cPointLight = new PointLight(pointLight);
		Vector3f lightPos = cPointLight.position;
		Vector4f aux = new Vector4f(lightPos, 1);
		aux.mul(viewMatrix);
		lightPos.x = aux.x;
		lightPos.y = aux.y;
		lightPos.z = aux.z;
		shaderProgram.setUniform("pointLight", cPointLight);

		lightAngle += 1.1f;
		if (lightAngle > 90) {
			directionalLight.intensity = 0;
			if (lightAngle >= 360) {
				lightAngle = -90;
			}
		} else if (lightAngle <= -80 || lightAngle >= 80) {
			float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
			directionalLight.intensity = factor;
			directionalLight.color.y = Math.max(factor, 0.9f);
			directionalLight.color.z = Math.max(factor, 0.5f);
		} else {
			directionalLight.intensity = 1;
			directionalLight.color.x = 1;
			directionalLight.color.y = 1;
			directionalLight.color.z = 1;
		}
		double angRad = Math.toRadians(lightAngle);
		directionalLight.direction.x = (float) Math.sin(angRad);
		directionalLight.direction.y = (float) Math.cos(angRad);

		DirectionalLight currDirLight = new DirectionalLight(directionalLight);
		Vector4f dir = new Vector4f(currDirLight.direction, 0);
		dir.mul(viewMatrix);
		currDirLight.direction = new Vector3f(dir.x, dir.y, dir.z);
		shaderProgram.setUniform("directionalLight", currDirLight);

	}

	private void drawMesh(Mesh mesh) throws GameException {
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.setUniform("modelMatrix", modelMatrix);
		if (shaderProgram.hasUniform("modelViewMatrix")) {
			viewMatrix.mul(modelMatrix, tempMatrix);
			shaderProgram.setUniform("modelViewMatrix", tempMatrix);
		}
		shaderProgram.setUniform("ambientLight", ambientLight);
		Mesh.Material mat = mesh.getMaterial();
		shaderProgram.setUniform("material", mat);

		mesh.render();
		shaderProgram.unbind();
	}

	public void loadViewMatrix(Camera camera) {
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getRotX()), X_AXIS)
				.rotate((float) Math.toRadians(camera.getRotY()), Y_AXIS)
				.rotate((float) Math.toRadians(camera.getRotZ()), Z_AXIS);
		viewMatrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());
	}

	public void setProgram(ShaderProgram program) {
		this.shaderProgram.set(program);
	}

	public ShaderProgram getProgram() {
		return shaderProgram.get();
	}
}
