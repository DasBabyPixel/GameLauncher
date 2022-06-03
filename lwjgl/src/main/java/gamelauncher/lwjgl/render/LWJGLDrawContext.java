package gamelauncher.lwjgl.render;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
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
import gamelauncher.engine.util.GameConsumer;
import gamelauncher.lwjgl.render.GameItem.GameItemModel;
import gamelauncher.lwjgl.render.light.DirectionalLight;
import gamelauncher.lwjgl.render.light.PointLight;
import gamelauncher.lwjgl.render.model.MeshLikeModel;

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
	private final Matrix4f tempMatrix4f = new Matrix4f();
	private final Vector3f tempVector3f = new Vector3f();
	private final AtomicReference<ShaderProgram> shaderProgram;
	private final AtomicReference<Projection> projection;
	private final Collection<WeakReference<LWJGLDrawContext>> children = ConcurrentHashMap.newKeySet();
	private final AtomicBoolean projectionMatrixValid = new AtomicBoolean(false);

	public LWJGLDrawContext(LWJGLWindow window) {
		this(null, window, 0, 0, 0, 1, 1, 1);
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, LWJGLWindow window, double tx, double ty, double tz, double sx,
			double sy, double sz) {
		this(parent, window, tx, ty, tz, sx, sy, sz, new AtomicReference<>(), new Matrix4f(), new Matrix4f(),
				new AtomicReference<>());
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, LWJGLWindow window, double tx, double ty, double tz, double sx,
			double sy, double sz, AtomicReference<ShaderProgram> shaderProgram, Matrix4f projectionMatrix,
			Matrix4f viewMatrix, AtomicReference<Projection> projection) {
		if (parent != null) {
			parent.children.add(new WeakReference<LWJGLDrawContext>(this));
		}
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
	public void cleanup() throws GameException {
		runForChildren(ctx -> ctx.cleanup());
	}

	private void runForChildren(GameConsumer<LWJGLDrawContext> run) throws GameException {
		for (WeakReference<LWJGLDrawContext> wref : children) {
			LWJGLDrawContext ctx = wref.get();
			if (ctx != null) {
				run.accept(ctx);
				ctx.runForChildren(run);
			} else {
				children.remove(wref);
			}
		}
	}

	@Override
	public LWJGLDrawContext duplicate() throws GameException {
		return new LWJGLDrawContext(this, window, tx, ty, tz, sx, sy, sz, new AtomicReference<>(shaderProgram.get()),
				new Matrix4f(), new Matrix4f(), new AtomicReference<>(projection.get()));
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

	public void invalidateProjectionMatrix() throws GameException {
		projectionMatrixValid.set(false);
		runForChildren(ctx -> {
			ctx.invalidateProjectionMatrix();
		});
	}

	@Override
	public void reloadProjectionMatrix() throws GameException {
		Projection projection = this.projection.get();
		if (projection == null) {
			return;
		}
		if (projection instanceof Transformations.Projection.Projection3D) {
			Transformations.Projection.Projection3D p3d = (Transformations.Projection.Projection3D) projection;
			float aspectRatio = (float) window.framebufferWidth.get() / (float) window.framebufferHeight.get();
			projectionMatrix.setPerspective(p3d.fov, aspectRatio, p3d.zNear, p3d.zFar);
		} else if (projection instanceof Transformations.Projection.Projection2D) {
			projectionMatrix.setOrtho2D(0, window.framebufferWidth.get(), 0, window.framebufferHeight.get());
		}
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
		if (model instanceof MeshLikeModel) {
			setupModelMatrix(x, y, z, rx, ry, rz, sx, sy, sz);
			drawMesh((MeshLikeModel) model);
		} else if (model instanceof GameItemModel) {
			GameItem item = ((GameItemModel) model).gameItem;
			item.applyToTransformationMatrix(modelMatrix);
			pDrawModel(item.getModel(), x, y, z, rx, ry, rz, sx, sy, sz);
		}
	}

	private void setupModelMatrix(double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) {
		modelMatrix.translate((float) (x + this.tx), (float) (y + this.ty), (float) (z + this.tz));
		modelMatrix.rotateXYZ((float) Math.toRadians(-rx), (float) Math.toRadians(-ry), (float) Math.toRadians(-rz));
		modelMatrix.scale((float) (sx * this.sx), (float) (sy * this.sy), (float) (sz * this.sz));
	}

	@Override
	public LWJGLDrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(this, window, tx + x, ty + y, tz + z, sx, sy, sz, shaderProgram, projectionMatrix,
				viewMatrix, projection);
	}

	@Override
	public LWJGLDrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(this, window, tx, ty, tz, sx * x, sy * y, sz * z, shaderProgram, projectionMatrix,
				viewMatrix, projection);
	}

	public LWJGLDrawContext withProgram(ShaderProgram program) {
		return new LWJGLDrawContext(this, window, tx, ty, tz, sx, sy, sz, new AtomicReference<ShaderProgram>(program),
				projectionMatrix, viewMatrix, projection);
	}

	@Override
	public LWJGLDrawContext withProjection(Projection projection) throws GameException {
		LWJGLDrawContext ctx = new LWJGLDrawContext(this, window, tx, ty, tz, sx, sy, sz, shaderProgram, new Matrix4f(),
				viewMatrix, new AtomicReference<>(projection));
		ctx.reloadProjectionMatrix();
		return ctx;
	}

	@Override
	public void drawModel(Model model, double x, double y, double z) throws GameException {
		drawModel(model, x, y, z, 0, 0, 0);
	}

	private void drawMesh(MeshLikeModel model) throws GameException {
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.setUniform("modelMatrix", modelMatrix);
		if (shaderProgram.hasUniform("modelViewMatrix")) {
			viewMatrix.mul(modelMatrix, tempMatrix4f);
			shaderProgram.setUniform("modelViewMatrix", tempMatrix4f);
		}
		model.render(shaderProgram);
	}

	float reflectance = 5F;
	float lightIntensity = 10F;
	Vector3f ambientLight = new Vector3f(.1F);
	Vector3f lightPosition = new Vector3f(2, 2, 2);
	Vector3f lightColor = new Vector3f(1, 1, 1);
	float specularPower = 200;
	PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity,
			new PointLight.Attenuation(0, 0, 1));
	DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1);
	float lightAngle = 0;
	ShaderProgram skybox;

	@Override
	public void update(Camera camera) throws GameException {
		if (projectionMatrixValid.compareAndSet(false, true)) {
			reloadProjectionMatrix();
		}

		loadViewMatrix(camera);
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.setUniform("viewMatrix", viewMatrix);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		shaderProgram.setUniform("camera_pos", tempVector3f.set(camera.getX(), camera.getY(), camera.getZ()));
		shaderProgram.setUniform("ambientLight", ambientLight);

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

		DirectionalLight currDirLight = new DirectionalLight(directionalLight);
		Vector4f dir = new Vector4f(currDirLight.direction, 0);
		dir.mul(viewMatrix);
		currDirLight.direction = new Vector3f(dir.x, dir.y, dir.z);
		shaderProgram.setUniform("directionalLight", currDirLight);

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
