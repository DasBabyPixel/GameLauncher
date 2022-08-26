package gamelauncher.engine.gui.guis;

import java.util.concurrent.atomic.AtomicBoolean;

import de.dasbabypixel.api.property.ChangeListener;
import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.NumberProperty;
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
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
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
		this(launcher, defaultFont(launcher), text, height);
		createdFont = true;
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
		createdFont = false;
		this.font = ObjectProperty.<Font>withValue(font);
		getHeightProperty().setNumber(height);
		this.text = ObjectProperty.<String>withValue(text);
		this.camera = new BasicCamera();
		this.cwidthprop = new NumberProperty() {

			{
				computor.set(true);
			}

			@Override
			protected Number computeValue() {
				return cwidth;
			}

		};
		this.getWidthProperty().bind(cwidthprop);
		this.invalidationListener = new InvalidationListener() {

			@Override
			public void invalidated(Property<?> property) {
				recreateModel.set(true);
			}

		};
		this.baselineYOffset = new NumberProperty() {

			{
				computor.set(true);
			}

			@Override
			protected Number computeValue() {
				return -model.getDescent();
			}

		};
		this.weakListener = new WeakReferenceInvalidationListener(invalidationListener);
		getHeightProperty().addListener(weakListener);
		this.text.addListener(weakListener);
		this.font.addListener(new ChangeListener<Font>() {

			@Override
			public void handleChange(Property<? extends Font> property, Font oldValue, Font newValue) {
				if (createdFont) {
					fontToDelete = oldValue;
					deleteFont = true;
				}
				createdFont = false;
				if (!recreating) {
					recreateModel.set(true);
				}
			}

		});
		this.font.addListener(weakListener);

//		TextureGui textureGui = launcher.getGuiManager().createGui(TextureGui.class);
//		textureGui.getTexture()
//				.uploadAsync(launcher.getResourceLoader()
//						.getResource(launcher.getEmbedFileSystem().getPath("pixel64x64.png"))
//						.newResourceStream());
//		textureGui.getXProperty().bind(getXProperty());
//		textureGui.getYProperty().bind(getYProperty());
//		textureGui.getWidthProperty().bind(getWidthProperty());
//		textureGui.getHeightProperty().bind(getHeightProperty());
//		GUIs.add(textureGui);
	}

	@Override
	protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		ensureModel();
		super.preRender(framebuffer, mouseX, mouseY, partialTick);
	}

	@Override
	protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		hud.update(camera);
		hud.drawModel(itemModel, getX(), getY() + baselineYOffset.floatValue(), 0);
		hud.getProgram().clearUniforms();
		return true;
	}

	@Override
	protected void doInit(Framebuffer framebuffer) throws GameException {
		hud = getLauncher().getContextProvider().loadContext(framebuffer, ContextType.HUD);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		if (itemModel != null)
			itemModel.cleanup();
		if (createdFont) {
			font.getValue().cleanup();
		}
		if (deleteFont) {
			fontToDelete.cleanup();
			deleteFont = false;
		}
		getLauncher().getContextProvider().freeContext(hud, ContextType.HUD);
	}

	/**
	 * @return text
	 */
	public Property<String> text() {
		return text;
	}

	/**
	 * @return font
	 */
	public Property<Font> font() {
		return font;
	}

	/**
	 * @return the color property vector
	 */
	public PropertyVector4f color() {
		return color;
	}

	private void ensureModel() throws GameException {
		if (deleteFont) {
			fontToDelete.cleanup();
			deleteFont = false;
		}
		if (recreateModel.compareAndSet(true, false)) {
			recreating = true;
			GameItemModel oldModel = itemModel;

			model = getLauncher().getGlyphProvider()
					.loadStaticModel(font.getValue(), text.getValue(), getHeightProperty().intValue());
			item = new GameItem(model);
			item.color().x.bind(color.x);
			item.color().y.bind(color.y);
			item.color().z.bind(color.z);
			item.color().w.bind(color.w);
			itemModel = item.createModel();
			cwidth = model.getWidth();
			cwidthprop.invalidate();
			baselineYOffset.invalidate();

			if (oldModel != null) {
				oldModel.cleanup();
			}
			recreating = false;
		}
	}

	private static Font defaultFont(GameLauncher launcher) throws GameException {
		return launcher.getFontFactory()
				.createFont(launcher.getResourceLoader()
						.getResource(launcher.getEmbedFileSystem().getPath("fonts", "cinzel_regular.ttf"))
						.newResourceStream());
	}

}
