package gamelauncher.lwjgl.render;

import gamelauncher.engine.render.*;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.Transformations.Projection.Projection3D;
import gamelauncher.engine.render.model.ColorAddModel;
import gamelauncher.engine.render.model.ColorMultiplierModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.WrapperModel;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.lwjgl.render.light.DirectionalLight;
import gamelauncher.lwjgl.render.light.PointLight;
import gamelauncher.lwjgl.render.model.LWJGLCombinedModelsModel;
import gamelauncher.lwjgl.render.shader.LWJGLShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("javadoc")
public class LWJGLDrawContext extends AbstractGameResource implements DrawContext {

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

	protected final Collection<WeakReference<LWJGLDrawContext>> children =
			ConcurrentHashMap.newKeySet();

	protected final AtomicBoolean projectionMatrixValid = new AtomicBoolean(false);
	protected final DrawContextFramebufferChangeListener listener;
	// Used temporarily
	private final Matrix4f tempMatrix4f = new Matrix4f();
	//	private final Vector3f tempVector3f = new Vector3f();
	private final Vector4f colorMultiplier = new Vector4f();
	private final Vector4f colorAdd = new Vector4f();
	public boolean swapTopBottom = false;
	float reflectance = 5F;
	float lightIntensity = 10F;
	Vector3f ambientLight = new Vector3f(.1F);
	Vector3f lightPosition = new Vector3f(2, 2, 2);
	Vector3f lightColor = new Vector3f(1, 1, 1);
	float specularPower = 200;
	PointLight pointLight =
			new PointLight(this.lightColor, new Vector3f(this.lightPosition), this.lightIntensity,
					new PointLight.Attenuation(0, 0, 1));
	Vector3f directionalLightDirection = new Vector3f(0, -1, 0);
	DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1),
			new Vector3f(this.directionalLightDirection), 1);
	float lightAngle = 0;

	// Used for combined models
	public LWJGLDrawContext(Framebuffer framebuffer) {
		this(null, framebuffer, 0, 0, 0, 1, 1, 1);
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, Framebuffer framebuffer, double tx, double ty,
			double tz, double sx, double sy, double sz) {
		this(parent, framebuffer, tx, ty, tz, sx, sy, sz, new AtomicReference<>(), new Matrix4f(),
				new Matrix4f(), new AtomicReference<>());
	}

	private LWJGLDrawContext(LWJGLDrawContext parent, Framebuffer framebuffer, double tx, double ty,
			double tz, double sx, double sy, double sz,
			AtomicReference<ShaderProgram> shaderProgram, Matrix4f projectionMatrix,
			Matrix4f viewMatrix, AtomicReference<Projection> projection) {
		if (parent != null) {
			parent.children.add(new WeakReference<>(this));
		}
		this.framebuffer = framebuffer;
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
	public void drawModel(Model model, double x, double y, double z, double rx, double ry,
			double rz) throws GameException {
		this.drawModel(model, x, y, z, rx, ry, rz, 1, 1, 1);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z, double rx, double ry,
			double rz, double sx, double sy, double sz) throws GameException {
		this.modelMatrix.identity();
		this.setupColors(this.colorMultiplier, this.colorAdd);
		this.pDrawModel(model, x, y, z, rx, ry, rz, sx, sy, sz, this.colorMultiplier,
				this.colorAdd);
	}

	@Override
	public void drawModel(Model model, double x, double y, double z) throws GameException {
		this.drawModel(model, x, y, z, 0, 0, 0);
	}

	@Override
	public void drawModel(Model model) throws GameException {
		this.drawModel(model, 0, 0, 0);
	}

	@Override
	public LWJGLDrawContext withProjection(Projection projection) throws GameException {
		LWJGLDrawContext ctx =
				new LWJGLDrawContext(this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
						this.sy, this.sz, this.shaderProgram, new Matrix4f(), this.viewMatrix,
						new AtomicReference<>(projection));
		ctx.reloadProjectionMatrix();
		return ctx;
	}

	@Override
	public DrawContext withProgram(ShaderProgram program) throws GameException {
		return new LWJGLDrawContext(this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
				this.sy, this.sz, new AtomicReference<>(program), this.projectionMatrix,
				this.viewMatrix, this.projection);
	}

	@Override
	public Projection projection() {
		return this.projection.get();
	}

	@Override
	public void projection(Transformations.Projection projection) throws GameException {
		if (this.projection.getAndSet(projection) != projection) {
			this.reloadProjectionMatrix();
		}
	}

	@Override
	public LWJGLDrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(this, this.framebuffer, this.tx + x, this.ty + y, this.tz + z,
				this.sx, this.sy, this.sz, this.shaderProgram, this.projectionMatrix,
				this.viewMatrix, this.projection);
	}

	@Override
	public LWJGLDrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(this, this.framebuffer, this.tx, this.ty, this.tz, this.sx * x,
				this.sy * y, this.sz * z, this.shaderProgram, this.projectionMatrix,
				this.viewMatrix, this.projection);
	}

	@Override
	public void update(Camera camera) throws GameException {
		if (this.projectionMatrixValid.compareAndSet(false, true)) {
			this.reloadProjectionMatrix();
		}

		this.loadViewMatrix(camera);
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.uviewMatrix.set(this.viewMatrix);
		shaderProgram.uprojectionMatrix.set(this.projectionMatrix);
		shaderProgram.ucamera_pos.set(camera.x(), camera.y(), camera.z());
		shaderProgram.uambientLight.set(this.ambientLight);

		shaderProgram.utexture_sampler.set(0);

		float pow = (float) (Math.sin(System.currentTimeMillis() / 1000D) + 1) * 200;
		shaderProgram.uspecularPower.set(pow);

		Vector3f lightPos = this.pointLight.position;
		lightPos.set(this.lightPosition);
		lightPos.mulPosition(this.viewMatrix);
		shaderProgram.upointLight.set(this.pointLight);

		this.directionalLight.direction.set(this.directionalLightDirection);
		this.directionalLight.direction.mulDirection(this.viewMatrix);
		//		Vector4f dir = new Vector4f(currDirLight.direction, 0);
		//		dir.mul(viewMatrix);
		//		currDirLight.direction = new Vector3f(dir.x, dir.y, dir.z);
		shaderProgram.udirectionalLight.set(this.directionalLight);
	}

	@Override
	public LWJGLDrawContext duplicate() throws GameException {
		return new LWJGLDrawContext(this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
				this.sy, this.sz, new AtomicReference<>(this.shaderProgram.get()), new Matrix4f(),
				new Matrix4f(), new AtomicReference<>(this.projection.get()));
	}

	@Override
	public void reloadProjectionMatrix() throws GameException {
		Projection projection = this.projection.get();
		if (projection == null) {
			return;
		}
		if (projection instanceof Transformations.Projection.Projection3D) {
			Projection3D p3d = (Projection3D) projection;
			float aspectRatio =
					this.framebuffer.width().floatValue() / this.framebuffer.height().floatValue();
			this.projectionMatrix.setPerspective(p3d.fov, aspectRatio, p3d.zNear, p3d.zFar);
		} else if (projection instanceof Transformations.Projection.Projection2D) {
			this.projectionMatrix.identity();
			if (this.swapTopBottom) {
				this.projectionMatrix.ortho(0, this.framebuffer.width().floatValue(), 0,
						this.framebuffer.height().floatValue(), -10000, 10000);
			} else {
				this.projectionMatrix.ortho(0, this.framebuffer.width().floatValue(),
						this.framebuffer.height().floatValue(), 0, -10000, 10000);
			}
		}
	}

	@Override
	public ShaderProgram program() {
		return this.shaderProgram.get();
	}

	@Override
	public void program(ShaderProgram program) {
		this.shaderProgram.set(program);
	}

	private void runForChildren(GameConsumer<LWJGLDrawContext> run) throws GameException {
		for (WeakReference<LWJGLDrawContext> wref : this.children) {
			LWJGLDrawContext ctx = wref.get();
			if (ctx != null) {
				run.accept(ctx);
				ctx.runForChildren(run);
			} else {
				this.children.remove(wref);
			}
		}
	}

	public void invalidateProjectionMatrix() throws GameException {
		this.projectionMatrixValid.set(false);
		this.runForChildren(LWJGLDrawContext::invalidateProjectionMatrix);
	}

	public LWJGLDrawContext withProgram(LWJGLShaderProgram program) {
		return new LWJGLDrawContext(this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
				this.sy, this.sz, new AtomicReference<ShaderProgram>(program),
				this.projectionMatrix, this.viewMatrix, this.projection);
	}

	@Override
	public void cleanup0() throws GameException {
		this.listener.cleanup();
		this.runForChildren(LWJGLDrawContext::cleanup);
	}

	private void pDrawModel(Model model, double x, double y, double z, double rx, double ry,
			double rz, double sx, double sy, double sz, Vector4f colorMultiplier, Vector4f colorAdd)
			throws GameException {
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
			item.applyToTransformationMatrix(this.modelMatrix);
			this.pDrawModel(item.model(), x, y, z, rx, ry, rz, sx, sy, sz, colorMultiplier,
					colorAdd);
		} else if (model instanceof LWJGLCombinedModelsModel) {
			LWJGLCombinedModelsModel comb = (LWJGLCombinedModelsModel) model;
			this.setupModelMatrix(x, y, z, rx, ry, rz, sx, sy, sz);
			comb.modelMatix.set(this.modelMatrix);
			for (Model m : comb.getModels()) {
				comb.colorMultiplier.set(colorMultiplier);
				comb.colorAdd.set(colorAdd);
				this.modelMatrix.set(comb.modelMatix);
				this.pDrawModel(m, 0, 0, 0, 0, 0, 0, 1, 1, 1, comb.colorMultiplier, comb.colorAdd);
			}
		} else if (model instanceof WrapperModel) {
			this.pDrawModel(((WrapperModel) model).getHandle(), x, y, z, rx, ry, rz, sx, sy, sz,
					colorMultiplier, colorAdd);
		} else {
			this.setupModelMatrix(x, y, z, rx, ry, rz, sx, sy, sz);
			this.drawMesh(model, colorMultiplier, colorAdd);
		}
	}

	private void drawMesh(Model model, Vector4f colorMultiplier, Vector4f colorAdd)
			throws GameException {
		ShaderProgram shaderProgram = this.shaderProgram.get();
		shaderProgram.bind();
		shaderProgram.umodelMatrix.set(this.modelMatrix);
		if (shaderProgram.hasUniform("modelViewMatrix")) {
			this.viewMatrix.mul(this.modelMatrix, this.tempMatrix4f);
			shaderProgram.umodelViewMatrix.set(this.tempMatrix4f);
		}
		shaderProgram.ucolor.set(colorMultiplier);
		shaderProgram.utextureAddColor.set(colorAdd);
		model.render(shaderProgram);
	}

	private void setupColors(Vector4f colorMultiplier, Vector4f colorAdd) {
		colorMultiplier.set(1, 1, 1, 1);
		colorAdd.set(0, 0, 0, 0);
	}

	private void setupModelMatrix(double x, double y, double z, double rx, double ry, double rz,
			double sx, double sy, double sz) {
		this.modelMatrix.translate((float) (x + this.tx), (float) (y + this.ty),
				(float) (z + this.tz));
		this.modelMatrix.rotateXYZ((float) Math.toRadians(-rx), (float) Math.toRadians(-ry),
				(float) Math.toRadians(-rz));
		this.modelMatrix.scale((float) (sx * this.sx), (float) (sy * this.sy),
				(float) (sz * this.sz));
	}

	private void loadViewMatrix(Camera camera) {
		this.viewMatrix.identity();
		this.viewMatrix.rotate((float) Math.toRadians(camera.rotX()), LWJGLDrawContext.X_AXIS)
				.rotate((float) Math.toRadians(camera.rotY()), LWJGLDrawContext.Y_AXIS)
				.rotate((float) Math.toRadians(camera.rotZ()), LWJGLDrawContext.Z_AXIS);
		this.viewMatrix.translate(-camera.x(), -camera.y(), -camera.z());
	}

}
