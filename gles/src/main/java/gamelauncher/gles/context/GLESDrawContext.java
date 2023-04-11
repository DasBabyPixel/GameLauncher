package gamelauncher.gles.context;

import gamelauncher.engine.render.*;
import gamelauncher.engine.render.light.DirectionalLight;
import gamelauncher.engine.render.light.PointLight;
import gamelauncher.engine.render.model.*;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.gles.GLES;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GLESDrawContext extends AbstractGameResource implements DrawContext {
    private static final Key GLES_COMBINED_MODELS_MODELMATRIX = new Key("gles_combined_models_modelmatrix");
    private static final Key GLES_COMBINED_MODELS_COLORMULTIPLIER = new Key("gles_combined_models_modelmatrix");
    private static final Key GLES_COMBINED_MODELS_COLORADD = new Key("gles_combined_models_modelmatrix");
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
    protected final AtomicReference<Transformations.Projection> projection;
    protected final Collection<WeakReference<GLESDrawContext>> children =
            ConcurrentHashMap.newKeySet();
    protected final AtomicBoolean projectionMatrixValid = new AtomicBoolean(false);
    protected final GLESDrawContextFramebufferChangeListener listener;
    // Used temporarily
    private final Matrix4f tempMatrix4f = new Matrix4f();
    private final GLES gles;
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
    public GLESDrawContext(GLES gles, Framebuffer framebuffer) {
        this(gles, null, framebuffer, 0, 0, 0, 1, 1, 1);
    }

    private GLESDrawContext(GLES gles, GLESDrawContext parent, Framebuffer framebuffer, double tx, double ty,
                            double tz, double sx, double sy, double sz) {
        this(gles, parent, framebuffer, tx, ty, tz, sx, sy, sz, new AtomicReference<>(), new Matrix4f(),
                new Matrix4f(), new AtomicReference<>());
    }

    private GLESDrawContext(GLES gles, GLESDrawContext parent, Framebuffer framebuffer, double tx, double ty,
                            double tz, double sx, double sy, double sz,
                            AtomicReference<ShaderProgram> shaderProgram, Matrix4f projectionMatrix,
                            Matrix4f viewMatrix, AtomicReference<Transformations.Projection> projection) {
        if (parent != null) {
            parent.children.add(new WeakReference<>(this));
        }
        this.gles = gles;
        this.framebuffer = framebuffer;
        this.listener = new GLESDrawContextFramebufferChangeListener(this, this.framebuffer.width(),
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
    public GLESDrawContext withProjection(Transformations.Projection projection) {
        GLESDrawContext ctx =
                new GLESDrawContext(gles, this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
                        this.sy, this.sz, this.shaderProgram, new Matrix4f(), this.viewMatrix,
                        new AtomicReference<>(projection));
        ctx.reloadProjectionMatrix();
        return ctx;
    }

    @Override
    public GLESDrawContext withProgram(ShaderProgram program) {
        return new GLESDrawContext(gles, this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
                this.sy, this.sz, new AtomicReference<>(program), this.projectionMatrix,
                this.viewMatrix, this.projection);
    }

    @Override
    public Transformations.Projection projection() {
        return this.projection.get();
    }

    @Override
    public void projection(Transformations.Projection projection) {
        if (this.projection.getAndSet(projection) != projection) {
            this.reloadProjectionMatrix();
        }
    }

    @Override
    public GLESDrawContext translate(double x, double y, double z) {
        return new GLESDrawContext(gles, this, this.framebuffer, this.tx + x, this.ty + y, this.tz + z,
                this.sx, this.sy, this.sz, this.shaderProgram, this.projectionMatrix,
                this.viewMatrix, this.projection);
    }

    @Override
    public GLESDrawContext scale(double x, double y, double z) {
        return new GLESDrawContext(gles, this, this.framebuffer, this.tx, this.ty, this.tz, this.sx * x,
                this.sy * y, this.sz * z, this.shaderProgram, this.projectionMatrix,
                this.viewMatrix, this.projection);
    }

    @Override
    public void update(Camera camera) {
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
        shaderProgram.udirectionalLight.set(this.directionalLight);
    }

    @Override
    public GLESDrawContext duplicate() {
        return new GLESDrawContext(gles, this, this.framebuffer, this.tx, this.ty, this.tz, this.sx,
                this.sy, this.sz, new AtomicReference<>(this.shaderProgram.get()), new Matrix4f(),
                new Matrix4f(), new AtomicReference<>(this.projection.get()));
    }

    @Override
    public void reloadProjectionMatrix() {
        Transformations.Projection projection = this.projection.get();
        if (projection == null) {
            return;
        }
        if (projection instanceof Transformations.Projection.Projection3D) {
            Transformations.Projection.Projection3D p3d = (Transformations.Projection.Projection3D) projection;
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

    private void runForChildren(GameConsumer<GLESDrawContext> run) throws GameException {
        for (WeakReference<GLESDrawContext> wref : this.children) {
            GLESDrawContext ctx = wref.get();
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
        this.runForChildren(GLESDrawContext::invalidateProjectionMatrix);
    }

    @Override
    public void cleanup0() throws GameException {
        this.listener.cleanup();
        this.runForChildren(GLESDrawContext::cleanup);
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
        if (model instanceof GameItem.GameItemModel) {
            GameItem item = ((GameItem.GameItemModel) model).gameItem;
            item.applyToTransformationMatrix(this.modelMatrix);
            this.pDrawModel(item.model(), x, y, z, rx, ry, rz, sx, sy, sz, colorMultiplier,
                    colorAdd);
        } else if (model instanceof CombinedModelsModel) {
            CombinedModelsModel comb = (CombinedModelsModel) model;
            this.setupModelMatrix(x, y, z, rx, ry, rz, sx, sy, sz);
            Matrix4f combModelMatrix = comb.storedValue(GLES_COMBINED_MODELS_MODELMATRIX, Matrix4f::new).set(modelMatrix);
            for (Model m : comb.getModels()) {
                Vector4f combColorMultiplier = comb.storedValue(GLES_COMBINED_MODELS_COLORMULTIPLIER, Vector4f::new).set(colorMultiplier);
                Vector4f combColorAdd = comb.storedValue(GLES_COMBINED_MODELS_COLORADD, Vector4f::new).set(colorAdd);
                this.modelMatrix.set(combModelMatrix);
                this.pDrawModel(m, 0, 0, 0, 0, 0, 0, 1, 1, 1, combColorMultiplier, combColorAdd);
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
        this.viewMatrix.rotate((float) Math.toRadians(camera.rotX()), GLESDrawContext.X_AXIS)
                .rotate((float) Math.toRadians(camera.rotY()), GLESDrawContext.Y_AXIS)
                .rotate((float) Math.toRadians(camera.rotZ()), GLESDrawContext.Z_AXIS);
        this.viewMatrix.translate(-camera.x(), -camera.y(), -camera.z());
    }
}
