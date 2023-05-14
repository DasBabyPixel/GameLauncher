package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.text.Component;

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
        private GameItemModel itemModel;
        private GlyphStaticModel newModel = null;
        private int lastHeight = 0;
        private DrawContext hud;

        public Simple(GameLauncher launcher) {
            super(launcher);
            this.heightProperty().number(1);
            this.text = Property.withValue(Component.empty());
            this.camera = new BasicCamera();
            this.cwidthprop = NumberValue.withValue(0F);
            this.widthProperty().bind(this.cwidthprop);
            InvalidationListener invalidationListener = property -> {
                Simple.this.recreateModel.set(true);
                this.redraw();
            };
            this.baselineYOffset = NumberValue.withValue(0F);
//            this.baselineYOffset = NumberValue.computing(() -> this.model == null ? 0 : -this.model.descent());
            this.heightProperty().addListener(invalidationListener);
            this.text.addListener(invalidationListener);
            this.color.x.addListener((NumberValue p) -> this.redraw());
            this.color.y.addListener((NumberValue p) -> this.redraw());
            this.color.z.addListener((NumberValue p) -> this.redraw());
            this.color.w.addListener((NumberValue p) -> this.redraw());
        }

        @Override protected void doCleanup() throws GameException {
            if (this.itemModel != null) {
                this.itemModel.cleanup();
                itemModel = null;
            }
            if (newModel != null) {
                newModel.cleanup();
                newModel = null;
            }
            this.launcher().contextProvider().freeContext(this.hud, ContextType.HUD);
            hud = null;
        }

        @Override protected void doInit() throws GameException {
            this.hud = this.launcher().contextProvider().loadContext(launcher().frame().framebuffer(), ContextType.HUD);
            ensureModel();
        }

        @Override protected void preRender(float mouseX, float mouseY, float partialTick) throws GameException {
            this.ensureModel();
            super.preRender(mouseX, mouseY, partialTick);
        }

        @Override protected boolean doRender(float mouseX, float mouseY, float partialTick) throws GameException {
            if (itemModel != null) {
                this.hud.update(this.camera);
                this.hud.drawModel(this.itemModel);
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
            ensureModel1();
            if (this.recreateModel.compareAndSet(true, false)) {
                if (newModel != null) {
                    if (newModel.handle() == null) {
                        recreateModel.set(true);
                        return;
                    }
                    newModel.cleanup();
                }
                Component t = text.value();
                newModel = t == null ? null : this.launcher().glyphProvider().loadStaticModel(t, lastHeight = heightProperty().intValue());
            }
            ensureModel1();
        }

        private void ensureModel1() throws GameException {
            if (newModel != null) {
                if (newModel.handle() == null) {
                    return;
                }
                GameItemModel oldModel = this.itemModel;
                GameItem item = new GameItem(newModel);
                item.color().x.bind(this.color.x);
                item.color().y.bind(this.color.y);
                item.color().z.bind(this.color.z);
                item.color().w.bind(this.color.w);
                NumberValue mult = heightProperty().divide(lastHeight);
                item.scale().x.bind(mult);
                item.scale().y.bind(mult);
                item.position().x.bind(xProperty());
                item.position().y.bind(yProperty().add(baselineYOffset));
                this.itemModel = item.createModel();
                this.baselineYOffset.unbind();
                this.cwidthprop.unbind();
                this.baselineYOffset.bind(newModel.descent().negate().multiply(mult));
                this.cwidthprop.bind(newModel.width().multiply(mult));
                newModel = null;
                if (oldModel != null) oldModel.cleanup();
            }
        }
    }
}
