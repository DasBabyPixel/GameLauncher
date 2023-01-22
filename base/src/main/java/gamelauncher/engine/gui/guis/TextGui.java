package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.property.SupplierReadOnlyStorage;
import gamelauncher.engine.util.property.WeakReferenceInvalidationListener;
import gamelauncher.engine.util.text.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public class TextGui extends ParentableAbstractGui {

	private final Property<Font> font;
	private final Property<String> text;
	private final PropertyVector4f color = new PropertyVector4f(1, 1, 1, 1);
	private final NumberValue baselineYOffset;
	private final Camera camera;
	private final AtomicBoolean recreateModel = new AtomicBoolean(true);
	private final NumberValue cwidthprop;
	private final InvalidationListener invalidationListener;
	private GlyphStaticModel model;
	private GameItemModel itemModel;
	private DrawContext hud;
	private volatile boolean createdFont;
	private volatile boolean deleteFont = false;
	private volatile boolean recreating = false;
	private Font fontToDelete = null;
	private volatile int cwidth;

	public TextGui(GameLauncher launcher, String text, int height) throws GameException {
		this(launcher, TextGui.defaultFont(launcher), text, height);
		this.createdFont = true;
	}

	public TextGui(GameLauncher launcher, Font font, String text, int height) {
		super(launcher);
		this.font = ObjectProperty.withValue(font);
		this.heightProperty().setNumber(height);
		this.text = ObjectProperty.withValue(text);
		this.camera = new BasicCamera();
		this.cwidthprop = NumberValue.withStorage(new SupplierReadOnlyStorage<>(() -> this.cwidth));
		this.widthProperty().bind(this.cwidthprop);
		invalidationListener = property -> {
			TextGui.this.recreateModel.set(true);
			this.redraw();
		};
		this.baselineYOffset = NumberValue.withStorage(new SupplierReadOnlyStorage<>(
				() -> this.model == null ? 0 : -this.model.descent()));
		WeakReferenceInvalidationListener weakListener =
				new WeakReferenceInvalidationListener(invalidationListener);
		this.heightProperty().addListener(weakListener);
		this.text.addListener(weakListener);
		this.font.addListener((property, oldValue, newValue) -> {
			if (TextGui.this.createdFont) {
				TextGui.this.fontToDelete = oldValue;
				TextGui.this.deleteFont = true;
			}
			TextGui.this.createdFont = false;
			if (!TextGui.this.recreating) {
				TextGui.this.recreateModel.set(true);
			}
		});
		this.font.addListener(weakListener);
		this.color.x.addListener((NumberValue p) -> this.redraw());
		this.color.y.addListener((NumberValue p) -> this.redraw());
		this.color.z.addListener((NumberValue p) -> this.redraw());
		this.color.w.addListener((NumberValue p) -> this.redraw());
	}

	private static Font defaultFont(GameLauncher launcher) throws GameException {
		ResourceStream stream = launcher.resourceLoader()
				.resource(launcher.embedFileSystem().getPath("fonts", "cinzel_regular.ttf"))
				.newResourceStream();
		return launcher.fontFactory().createFont(stream);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		if (this.itemModel != null)
			this.itemModel.cleanup();
		if (this.createdFont) {
			this.font.getValue().cleanup();
		}
		if (this.deleteFont) {
			this.fontToDelete.cleanup();
			this.deleteFont = false;
		}
		this.launcher().contextProvider().freeContext(this.hud, ContextType.HUD);
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		this.hud = this.launcher().contextProvider().loadContext(framebuffer, ContextType.HUD);
	}

	@Override
	protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		this.ensureModel();
		super.preRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
			float partialTick) throws GameException {
		this.hud.update(this.camera);
		this.hud.drawModel(this.itemModel, Math.round(this.x()),
				Math.round(this.y() + this.baselineYOffset.floatValue()), 0);
		this.hud.program().clearUniforms();
		return true;
	}

	@Override
	protected String additionalToStringData() {
		return "text=" + text.getValue();
	}

	/**
	 * @return text
	 */
	public Property<String> text() {
		return this.text;
	}

	/**
	 * @return font
	 */
	public Property<Font> font() {
		return this.font;
	}

	/**
	 * @return the color property vector
	 */
	public PropertyVector4f color() {
		return this.color;
	}

	private void ensureModel() throws GameException {
		if (this.deleteFont) {
			this.fontToDelete.cleanup();
			this.deleteFont = false;
		}
		if (this.recreateModel.compareAndSet(true, false)) {
			this.recreating = true;
			GameItemModel oldModel = this.itemModel;

			this.model = this.launcher().glyphProvider()
					.loadStaticModel(this.font.getValue(), Component.text(this.text.getValue()),
							this.heightProperty().intValue());
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
			this.recreating = false;
		}
	}
}
