package gamelauncher.engine.gui.guis;

import java.util.concurrent.atomic.AtomicBoolean;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.property.SupplierReadOnlyStorage;
import gamelauncher.engine.util.property.WeakReferenceInvalidationListener;

/**
 * @author DasBabyPixel
 */
public class TextGui extends ParentableAbstractGui {

	private GlyphStaticModel model;

	private GameItem item;

	private GameItemModel itemModel;

	private DrawContext hud;

	private volatile boolean createdFont;

	private volatile boolean deleteFont = false;

	private volatile boolean recreating = false;

	private Font fontToDelete = null;

	private final Property<Font> font;

	private final Property<String> text;

	private final PropertyVector4f color = new PropertyVector4f(1, 1, 1, 1);

	private final NumberValue baselineYOffset;

	private final Camera camera;

	private final AtomicBoolean recreateModel = new AtomicBoolean(true);

	private final InvalidationListener invalidationListener;

	private final WeakReferenceInvalidationListener weakListener;

	private volatile int cwidth;

	private final NumberValue cwidthprop;

	/**
	 * @param launcher
	 * @param text
	 * @param height
	 * @throws GameException
	 */
	public TextGui(GameLauncher launcher, String text, int height) throws GameException {
		this(launcher, TextGui.defaultFont(launcher), text, height);
		this.createdFont = true;
	}

	/**
	 * @param launcher
	 * @param font
	 * @param text
	 * @param height
	 * @throws GameException
	 */
	public TextGui(GameLauncher launcher, Font font, String text, int height) throws GameException {
		super(launcher);
		this.createdFont = true;
		this.font = ObjectProperty.withValue(font);
		this.getHeightProperty().setNumber(height);
		this.text = ObjectProperty.withValue(text);
		this.camera = new BasicCamera();
		this.cwidthprop = NumberValue.withStorage(new SupplierReadOnlyStorage<>(() -> this.cwidth));
		this.getWidthProperty().bind(this.cwidthprop);
		this.invalidationListener = property -> TextGui.this.recreateModel.set(true);
		this.baselineYOffset = NumberValue
				.withStorage(new SupplierReadOnlyStorage<>(() -> this.model == null ? 0 : -this.model.getDescent()));
		this.weakListener = new WeakReferenceInvalidationListener(this.invalidationListener);
		this.getHeightProperty().addListener(this.weakListener);
		this.text.addListener(this.weakListener);
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
		this.font.addListener(this.weakListener);
	}

	@Override
	protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		this.ensureModel();
		super.preRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		this.hud.update(this.camera);
		this.hud.drawModel(this.itemModel, this.getX(), this.getY() + this.baselineYOffset.floatValue(), 0);
		this.hud.getProgram().clearUniforms();
		return true;
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		this.hud = this.getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
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
		this.getLauncher().getContextProvider().freeContext(this.hud, ContextType.HUD);
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

			this.model = this.getLauncher()
					.getGlyphProvider()
					.loadStaticModel(this.font.getValue(), this.text.getValue(), this.getHeightProperty().intValue());
			this.item = new GameItem(this.model);
			this.item.color().x.bind(this.color.x);
			this.item.color().y.bind(this.color.y);
			this.item.color().z.bind(this.color.z);
			this.item.color().w.bind(this.color.w);
			this.itemModel = this.item.createModel();
			this.cwidth = this.model.getWidth();
			this.cwidthprop.invalidate();
			this.baselineYOffset.invalidate();

			if (oldModel != null) {
				oldModel.cleanup();
			}
			this.recreating = false;
		}
	}

	private static Font defaultFont(GameLauncher launcher) throws GameException {
		ResourceStream stream = launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("fonts", "cinzel_regular.ttf"))
				.newResourceStream();
		Font font = launcher.getFontFactory().createFont(stream);
//		stream.cleanup();
		return font;
	}

}
