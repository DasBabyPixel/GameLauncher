package gamelauncher.lwjgl.render;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.model.ColorAddModel;
import gamelauncher.engine.render.model.ColorMultiplierModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.lwjgl.render.light.DirectionalLight;
import gamelauncher.lwjgl.render.light.PointLight;
import gamelauncher.lwjgl.render.model.LWJGLCombinedModelsModel;
import gamelauncher.lwjgl.render.shader.LWJGLShaderProgram;

@SuppressWarnings("javadoc")
public class LWJGLDrawContext implements DrawContext {

	protected static final Vector3f X_AXIS = new Vector3f(1, 0, 0);

	protected static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);

	protected static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	protected final Framebuffer framebuffer;

	protected final double tx, ty, tz;

	protected final double sx, sy, sz;

	protected final Matrix4f projectionMatrix;

	protected final Matrix4f modelMatrix = new Matrix4f();

	protected final Matrix4f viewMatrix;

	protected final AtomicReference<ShaderProgram> shaderProgram;

	protected final AtomicReference<Projection> projection;

	protected final Collection<WeakReference<LWJGLDrawContext>> children = ConcurrentHashMap.newKeySet();

	protected final AtomicBoolean projectionMatrixValid = new AtomicBoolean(false);

	public boolean swapTopBottom = false;

	protected final DrawContextFramebufferChangeListener listener;

	// Used temporarily
	private final Matrix4f tempMatrix4f = new Matrix4f();

//	private final Vector3f tempVector3f = new Vector3f();
	private final Vector4f colorMultiplier = new Vector4f();

	private final Vector4f colorAdd = new Vector4f();

	// Used for combined models
	private final Matrix4f combinedModelMMatrix4f = new Matrix4f();

	public LWJGLDrawContext(Framebuffer framebuffer) {
		this(null, framebuffer, 0, 0, 0, 1, 1, 1);
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, Framebuffer framebuffer, double tx, double ty, double tz, double sx,
			double sy, double sz) {
		this(parent, framebuffer, tx, ty, tz, sx, sy, sz, new AtomicReference<>(), new Matrix4f(), new Matrix4f(),
				new AtomicReference<>());
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, Framebuffer framebuffer, double tx, double ty, double tz,
			double sx, double sy, double sz, AtomicReference<ShaderProgram> shaderProgram, Matrix4f projectionMatrix,
			Matrix4f viewMatrix, AtomicReference<Projection> projection) {
		if (parent != null) {
			parent.children.add(new WeakReference<LWJGLDrawContext>(this));
		}
		this.framebuffer = framebuffer;
		System.out.println("Context: " + this.framebuffer);
		this.listener = new DrawContextFramebufferChangeListener(this, this.framebuffer.width(),
				this.framebuffer.height());
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
	public void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz)
			throws GameException {
		drawModel(model, x, y, z, rx, ry, rz, 1, 1, 1);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx,
			double sy, double sz) throws GameException {
		modelMatrix.identity();
		setupColors(colorMultiplier, colorAdd);
		pDrawModel(model, x, y, z, rx, ry, rz, sx, sy, sz, colorMultiplier, colorAdd);
	}

	@Override
	public void drawModel(Model model) throws GameException {
		drawModel(model, 0, 0, 0);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z) throws GameException {
		drawModel(model, x, y, z, 0, 0, 0);
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

	public void invalidateProjectionMatrix() throws GameException {
		projectionMatrixValid.set(false);
		System.out.println("Invalid");
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
			float aspectRatio = framebuffer.width().floatValue() / framebuffer.height().floatValue();
			projectionMatrix.setPerspective(p3d.fov, aspectRatio, p3d.zNear, p3d.zFar);
		} else if (projection instanceof Transformations.Projection.Projection2D) {
			projectionMatrix.identity();
			if (swapTopBottom) {
				projectionMatrix.ortho(0, framebuffer.width().floatValue(), 0, framebuffer.height().floatValue(),
						-10000, 10000);
			} else {
				projectionMatrix.ortho(0, framebuffer.width().floatValue(), framebuffer.height().floatValue(), 0,
						-10000, 10000);
			}
		}
	}

	float reflectance = 5F;

	float lightIntensity = 10F;

	Vector3f ambientLight = new Vector3f(.1F);

	Vector3f lightPosition = new Vector3f(2, 2, 2);

	Vector3f lightColor = new Vector3f(1, 1, 1);

	float specularPower = 200;

	PointLight pointLight = new PointLight(lightColor, new Vector3f(lightPosition), lightIntensity,
			new PointLight.Attenuation(0, 0, 1));

	Vector3f directionalLightDirection = new Vector3f(0, -1, 0);

	DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1),
			new Vector3f(directionalLightDirection), 1);

	float lightAngle = 0;

	@Override
	public void update(Camera camera) throws GameException {
		if (projectionMatrixValid.compareAndSet(false, true)) {
			reloadProjectionMatrix();
		}

		loadViewMatrix(camera);
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.uviewMatrix.set(viewMatrix);
		shaderProgram.uprojectionMatrix.set(projectionMatrix);
		shaderProgram.ucamera_pos.set(camera.getX(), camera.getY(), camera.getZ());
		shaderProgram.uambientLight.set(ambientLight);

		shaderProgram.utexture_sampler.set(0);

		float pow = (float) (Math.sin(System.currentTimeMillis() / 1000D) + 1) * 200;
		shaderProgram.uspecularPower.set(pow);

		Vector3f lightPos = pointLight.position;
		lightPos.set(lightPosition);
		lightPos.mulPosition(viewMatrix);
		shaderProgram.upointLight.set(pointLight);

		directionalLight.direction.set(directionalLightDirection);
		directionalLight.direction.mulDirection(viewMatrix);
//		Vector4f dir = new Vector4f(currDirLight.direction, 0);
//		dir.mul(viewMatrix);
//		currDirLight.direction = new Vector3f(dir.x, dir.y, dir.z);
		shaderProgram.udirectionalLight.set(directionalLight);

	}

	@Override
	public LWJGLDrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(this, framebuffer, tx + x, ty + y, tz + z, sx, sy, sz, shaderProgram,
				projectionMatrix, viewMatrix, projection);
	}

	@Override
	public LWJGLDrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(this, framebuffer, tx, ty, tz, sx * x, sy * y, sz * z, shaderProgram,
				projectionMatrix, viewMatrix, projection);
	}

	public LWJGLDrawContext withProgram(LWJGLShaderProgram program) {
		return new LWJGLDrawContext(this, framebuffer, tx, ty, tz, sx, sy, sz,
				new AtomicReference<ShaderProgram>(program), projectionMatrix, viewMatrix, projection);
	}

	@Override
	public DrawContext withProgram(ShaderProgram program) throws GameException {
		return new LWJGLDrawContext(this, framebuffer, tx, ty, tz, sx, sy, sz, new AtomicReference<>(program),
				projectionMatrix, viewMatrix, projection);
	}

	@Override
	public LWJGLDrawContext withProjection(Projection projection) throws GameException {
		LWJGLDrawContext ctx = new LWJGLDrawContext(this, framebuffer, tx, ty, tz, sx, sy, sz, shaderProgram,
				new Matrix4f(), viewMatrix, new AtomicReference<>(projection));
		ctx.reloadProjectionMatrix();
		return ctx;
	}

	@Override
	public LWJGLDrawContext duplicate() throws GameException {
		return new LWJGLDrawContext(this, framebuffer, tx, ty, tz, sx, sy, sz,
				new AtomicReference<>(shaderProgram.get()), new Matrix4f(), new Matrix4f(),
				new AtomicReference<>(projection.get()));
	}

	@Override
	public void cleanup() throws GameException {
		listener.cleanup();
		runForChildren(ctx -> ctx.cleanup());
	}

	private void pDrawModel(Model model, double x, double y, double z, double rx, double ry, double rz, double sx,
			double sy, double sz, Vector4f colorMultiplier, Vector4f colorAdd) throws GameException {
		if (model instanceof ColorMultiplierModel) {
			Vector4f mult = ((ColorMultiplierModel) model).getColor();
			colorMultiplier.mul(mult);
			colorAdd.mul(mult);
		}
		if (model instanceof ColorAddModel) {
			colorAdd.add(((ColorAddModel) model).getAddColor());
		}
		if (model instanceof GameItemModel) {
			GameItem item = ((GameItemModel) model).gameItem;
			item.applyToTransformationMatrix(modelMatrix);
			pDrawModel(item.getModel(), x, y, z, rx, ry, rz, sx, sy, sz, colorMultiplier, colorAdd);
		} else if (model instanceof LWJGLCombinedModelsModel) {
			LWJGLCombinedModelsModel comb = (LWJGLCombinedModelsModel) model;
			combinedModelMMatrix4f.set(modelMatrix);
			for (Model m : comb.getModels()) {
				comb.colorMultiplier.set(colorMultiplier);
				comb.colorAdd.set(colorAdd);
				modelMatrix.set(combinedModelMMatrix4f);
				pDrawModel(m, x, y, z, rx, ry, rz, sx, sy, sz, comb.colorMultiplier, comb.colorAdd);
			}
		} else {
			setupModelMatrix(x, y, z, rx, ry, rz, sx, sy, sz);
			drawMesh(model, colorMultiplier, colorAdd);
		}
	}

	private void drawMesh(Model model, Vector4f colorMultiplier, Vector4f colorAdd) throws GameException {
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.umodelMatrix.set(modelMatrix);
		if (shaderProgram.hasUniform("modelViewMatrix")) {
			viewMatrix.mul(modelMatrix, tempMatrix4f);
			shaderProgram.umodelViewMatrix.set(tempMatrix4f);
		}
		shaderProgram.ucolor.set(colorMultiplier);
		shaderProgram.utextureAddColor.set(colorAdd);
		model.render(shaderProgram);
	}

	private void setupColors(Vector4f colorMultiplier, Vector4f colorAdd) {
		colorMultiplier.set(1, 1, 1, 1);
		colorAdd.set(0, 0, 0, 0);
	}

	private void setupModelMatrix(double x, double y, double z, double rx, double ry, double rz, double sx, double sy,
			double sz) {
		modelMatrix.translate((float) (x + this.tx), (float) (y + this.ty), (float) (z + this.tz));
		modelMatrix.rotateXYZ((float) Math.toRadians(-rx), (float) Math.toRadians(-ry), (float) Math.toRadians(-rz));
		modelMatrix.scale((float) (sx * this.sx), (float) (sy * this.sy), (float) (sz * this.sz));
	}

	private void loadViewMatrix(Camera camera) {
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getRotX()), X_AXIS)
				.rotate((float) Math.toRadians(camera.getRotY()), Y_AXIS)
				.rotate((float) Math.toRadians(camera.getRotZ()), Z_AXIS);
		viewMatrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());
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
	public void setProgram(ShaderProgram program) {
		this.shaderProgram.set(program);
	}

	@Override
	public ShaderProgram getProgram() {
		return shaderProgram.get();
	}

}
