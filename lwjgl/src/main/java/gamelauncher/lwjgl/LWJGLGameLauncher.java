package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Vector3f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.Math;
import gamelauncher.lwjgl.file.EmbedFileSystem;
import gamelauncher.lwjgl.file.LWJGLFileSystem;
import gamelauncher.lwjgl.input.LWJGLInput.InputType;
import gamelauncher.lwjgl.input.LWJGLInput.Listener;
import gamelauncher.lwjgl.render.BasicCamera;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.LWJGLWindow;
import gamelauncher.lwjgl.render.LWJGLWindow.CloseCallback;
import gamelauncher.lwjgl.render.font.BasicGlyphProvider;
import gamelauncher.lwjgl.render.modelloader.LWJGLModelLoader;
import gamelauncher.lwjgl.settings.controls.MouseSensivityInsertion;

public class LWJGLGameLauncher extends GameLauncher {

	private LWJGLWindow window;
	private boolean mouseMovement = false;
	private float mouseSensivity = 1.0F;
	private boolean ignoreNextMovement = false;

	public LWJGLGameLauncher() throws GameException {
		setResourceLoader(new SimpleResourceLoader());
		setFileSystem(new LWJGLFileSystem(), new EmbedFileSystem());
		setGameRenderer(new LWJGLGameRenderer(this));
		setModelLoader(new LWJGLModelLoader(this));
	}

	@Override
	protected void start0() throws GameException {
		window = new LWJGLWindow(400, 400, NAME);
		setWindow(window);
		setCamera(new BasicCamera(() -> window.scheduleDraw()));
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
			setGlyphProvider(new BasicGlyphProvider());
		});
		window.setRenderMode(RenderMode.ON_UPDATE);
		window.createWindow();
		window.startRendering();
		AtomicBoolean boost = new AtomicBoolean(false);
		window.getInput().addListener(new Listener() {

			private final double moveSpeed = 1.0 / MAX_TPS;

			@Override
			public void handleKeyboard(InputType inputType, int key) {
				float moveSpeed = (float) (boost.get() ? 200.0 * this.moveSpeed : this.moveSpeed);
				if (inputType == InputType.HELD) {
					if (key == GLFW_KEY_W) {
						getCamera().movePosition(0, 0, -moveSpeed);
					} else if (key == GLFW_KEY_S) {
						getCamera().movePosition(0, 0, moveSpeed);
					} else if (key == GLFW_KEY_A) {
						getCamera().movePosition(-moveSpeed, 0, 0);
					} else if (key == GLFW_KEY_D) {
						getCamera().movePosition(moveSpeed, 0, 0);
					} else if (key == GLFW_KEY_SPACE) {
						getCamera().movePosition(0, moveSpeed, 0);
					} else if (key == GLFW_KEY_LEFT_SHIFT) {
						getCamera().movePosition(0, -moveSpeed, 0);
					}
				} else if (inputType == InputType.PRESSED) {
					if (key == GLFW_KEY_LEFT_CONTROL) {
						boost.set(true);
					} else if (key == GLFW_KEY_ESCAPE) {
						mouseMovement(!mouseMovement);
					}
				} else if (inputType == InputType.RELEASED) {
					if (key == GLFW_KEY_LEFT_CONTROL) {
						boost.set(false);
					}
				}
			}

			@Override
			public void handleMouse(InputType inputType, int mouseButton, double mouseX, double mouseY) {
				if (inputType == InputType.SCROLL) {
					if (mouseY != 0) {
						float v = Math.pow(0.9F, Math.abs((float) mouseY));
						if (mouseY < 0) {
							mouseSensivity *= v;
						} else {
							mouseSensivity /= v;
						}
					}
					if (mouseX != 0) {
						getCamera().moveRotation((float) mouseX * 3, 0, 0);
					}
				} else if (inputType == InputType.PRESSED) {
					if (mouseButton == 1) {
						boolean pin = !window.isFloating();
						window.setFloating(pin);
						window.setTitle(pin ? window.title.get() + " - pinned"
								: window.title.get().substring(0, window.title.get().length() - " - pinned".length()));
					}
				}
			}
		});
		CloseCallback oldCloseCallback = window.getCloseCallback();
		window.setCloseCallback(new CloseCallback() {
			@Override
			public void close() throws GameException {
				oldCloseCallback.close();
				LWJGLGameLauncher.this.stop();
			}
		});
		window.swapBuffers.set(false);
		window.getFrameCounter().ifPresent(fc -> {
			fc.addUpdateListener(fps -> {
				getLogger().infof("FPS: %s", fps);
			});
		});
		window.scheduleDrawAndWaitForFrame();
		window.swapBuffers.set(true);
		window.showAndEndFrame();
		window.setFloating(true);
		window.setFloating(false);
		mouseMovement(false);
	}

	@Override
	protected void registerSettingInsertions() {
		new MouseSensivityInsertion().register(this);
	}

	private void mouseMovement(boolean movement) {
		window.mouse.grabbed(movement).thenRun(() -> {
			if (!movement) {
				glfwSetCursorPos(window.id.get(), window.width.get() / 2, window.height.get() / 2);
			} else {
				glfwSetCursorPos(window.id.get(), 0, 0);
				ignoreNextMovement = true;
			}
		});
		this.mouseMovement = movement;
	}

	public LWJGLWindow getWindow() {
		return window;
	}

	@Override
	protected void tick() {
		window.getInput().handleInput();
		mouse: if (mouseMovement) {
			Camera cam = getCamera();
			float dy = (float) (window.mouse.getDeltaX() * 0.4) * mouseSensivity;
			float dx = (float) (window.mouse.getDeltaY() * 0.4) * mouseSensivity;
			if ((dx != 0 || dy != 0) && ignoreNextMovement) {
				ignoreNextMovement = false;
				break mouse;
			}
			Vector3f rot = new Vector3f(cam.getRotX(), cam.getRotY(), cam.getRotZ());
			cam.setRotation(Math.clamp(rot.x + dx, -90F, 90F), rot.y + dy, rot.z);
		}
	}
}
