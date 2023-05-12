package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.text.Component;
import org.joml.Math;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public interface TextGui extends Gui {

    Property<Component> text();

    PropertyVector4f color();

    @Api
    class Simple extends ParentableAbstractGui implements TextGui {

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

        public Simple(GameLauncher launcher) {
            super(launcher);
            this.heightProperty().number(1);
            this.text = Property.withValue(Component.empty());
            this.camera = new BasicCamera();
            this.cwidthprop = NumberValue.computing(() -> this.cwidth);
            this.widthProperty().bind(this.cwidthprop);
            InvalidationListener invalidationListener = property -> {
                Simple.this.recreateModel.set(true);
                this.redraw();
            };
            this.baselineYOffset = NumberValue.computing(() -> this.model == null ? 0 : -this.model.descent());
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

        @Override protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
            this.ensureModel();
            super.preRender(framebuffer, mouseX, mouseY, partialTick);
        }

        @Override protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
            if (itemModel != null) {
                this.hud.update(this.camera);
                this.hud.drawModel(this.itemModel, Math.round(this.x()), Math.round(this.y() + this.baselineYOffset.floatValue()), 0);
                this.hud.program().clearUniforms();
            }
            return true;
        }

        @Override protected String additionalToStringData() {
            return "text=" + text.value();
        }

        /**
         * @return text
         */
        @Override public Property<Component> text() {
            return this.text;
        }

        /**
         * @return the color property vector
         */
        @Override public PropertyVector4f color() {
            return this.color;
        }

        private void ensureModel() throws GameException {
            if (this.recreateModel.compareAndSet(true, false)) {
                GameItemModel oldModel = this.itemModel;

                Component t = text.value();
                this.model = t == null ? null : this.launcher().glyphProvider().loadStaticModel(t, this.heightProperty().intValue());
                if (model != null) {
                    GameItem item = new GameItem(this.model);
                    item.color().x.bind(this.color.x);
                    item.color().y.bind(this.color.y);
                    item.color().z.bind(this.color.z);
                    item.color().w.bind(this.color.w);
                    this.itemModel = item.createModel();
                    this.cwidth = this.model.width();
                    cwidthprop.intValue();
                } else {
                    this.itemModel = null;
                }

                if (oldModel != null) {
                    oldModel.cleanup();
                }
            }
        }
    }
}
