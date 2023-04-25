package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.property.SupplierReadOnlyStorage;
import gamelauncher.engine.util.text.Component;
import org.joml.Math;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public class TextGui extends ParentableAbstractGui {

    private final Property<Component> text;
    private final PropertyVector4f color = new PropertyVector4f(1, 1, 1, 1);
    private final NumberValue baselineYOffset;
    private final Camera camera;
    private final AtomicBoolean recreateModel = new AtomicBoolean(true);
    private final NumberValue cwidthprop;
    private GlyphStaticModel model;
    private GameItemModel itemModel;
    private DrawContext hud;
    private volatile int cwidth;

    public TextGui(GameLauncher launcher, Component text, int height) {
        super(launcher);
        this.heightProperty().number(height);
        this.text = Property.withValue(text);
        this.camera = new BasicCamera();
        this.cwidthprop = NumberValue.withStorage(new SupplierReadOnlyStorage<>(() -> this.cwidth));
        this.widthProperty().bind(this.cwidthprop);
        InvalidationListener invalidationListener = property -> {
            TextGui.this.recreateModel.set(true);
            this.redraw();
        };
        this.baselineYOffset = NumberValue.withStorage(new SupplierReadOnlyStorage<>(() -> this.model == null ? 0 : -this.model.descent()));
        this.heightProperty().addListener(invalidationListener);
        this.text.addListener(invalidationListener);
        this.color.x.addListener((NumberValue p) -> this.redraw());
        this.color.y.addListener((NumberValue p) -> this.redraw());
        this.color.z.addListener((NumberValue p) -> this.redraw());
        this.color.w.addListener((NumberValue p) -> this.redraw());
    }

    @Override protected void doCleanup(Framebuffer framebuffer) throws GameException {
        if (this.itemModel != null) this.itemModel.cleanup();
        this.launcher().contextProvider().freeContext(this.hud, ContextType.HUD);
    }

    @Override protected void doInit(Framebuffer framebuffer) throws GameException {
        this.hud = this.launcher().contextProvider().loadContext(framebuffer, ContextType.HUD);
    }

    @Override
    protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
        this.ensureModel();
        super.preRender(framebuffer, mouseX, mouseY, partialTick);
    }

    @Override
    protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
        this.hud.update(this.camera);
        this.hud.drawModel(this.itemModel, Math.round(this.x()), Math.round(this.y() + this.baselineYOffset.floatValue()), 0);
        this.hud.program().clearUniforms();
        return true;
    }

    @Override protected String additionalToStringData() {
        return "text=" + text.value();
    }

    /**
     * @return text
     */
    public Property<Component> text() {
        return this.text;
    }

    /**
     * @return the color property vector
     */
    public PropertyVector4f color() {
        return this.color;
    }

    private void ensureModel() throws GameException {
        if (this.recreateModel.compareAndSet(true, false)) {
            GameItemModel oldModel = this.itemModel;

            this.model = this.launcher().glyphProvider().loadStaticModel(this.text.value(), this.heightProperty().intValue());
            GameItem item = new GameItem(this.model);
            item.color().x.bind(this.color.x);
            item.color().y.bind(this.color.y);
            item.color().z.bind(this.color.z);
            item.color().w.bind(this.color.w);
            this.itemModel = item.createModel();
            this.cwidth = this.model.width();
            this.cwidthprop.invalidate();
            this.baselineYOffset.invalidate();

            if (oldModel != null) {
                oldModel.cleanup();
            }
        }
    }
}
