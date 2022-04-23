package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Vector3f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.Math;
import gamelauncher.lwjgl.file.EmbedFileSystem;
import gamelauncher.lwjgl.file.LWJGLFileSystem;
import gamelauncher.lwjgl.render.LWJGLCamera;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.LWJGLInput.InputType;
import gamelauncher.lwjgl.render.LWJGLInput.Listener;
import gamelauncher.lwjgl.render.LWJGLWindow;
import gamelauncher.lwjgl.render.LWJGLWindow.CloseCallback;
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
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);
		window.createWindow();
		window.startRendering();
		AtomicBoolean boost = new AtomicBoolean(false);
		window.getInput().addListener(new Listener() {

			private final double moveSpeed = 1.0 / MAX_TPS;

			@Override
			public void handleKeyboard(InputType inputType, int key) {
				float moveSpeed = (float) (boost.get() ? 4.0 * this.moveSpeed : this.moveSpeed);
				if (inputType == InputType.HELD) {
					if (key == GLFW_KEY_W) {
						window.getCamera().movePosition(0, 0, -moveSpeed);
					} else if (key == GLFW_KEY_S) {
						window.getCamera().movePosition(0, 0, moveSpeed);
					} else if (key == GLFW_KEY_A) {
						window.getCamera().movePosition(-moveSpeed, 0, 0);
					} else if (key == GLFW_KEY_D) {
						window.getCamera().movePosition(moveSpeed, 0, 0);
					} else if (key == GLFW_KEY_SPACE) {
						window.getCamera().movePosition(0, moveSpeed, 0);
					} else if (key == GLFW_KEY_LEFT_SHIFT) {
						window.getCamera().movePosition(0, -moveSpeed, 0);
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
						window.getCamera().getRotation().z += mouseX * 3;
					}
				} else if (inputType == InputType.PRESSED) {
					if (mouseButton == 1) {
						boolean pin = !window.isFloating();
						window.setFloating(pin);
						window.setTitle(pin ? window.title.get() + " - pinned"
								: window.title.get().substring(0, window.title.get().length() - " - pinned".length()));
					}
				}
//				getLogger().infof("MouseEvent[%s %s %s %s]", inputType, mouseButton, mouseX, mouseY);
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
		window.getFrameCounter().ifPresent(fc -> {
			fc.limit(60);
			fc.addUpdateListener(fps -> {
				getLogger().infof("FPS: %s", fps);
			});
		});
		window.waitForFrame();
		window.show();
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
//		double speed = 30D / MAX_TPS;
//		LWJGLGameRenderer.rx += 1 * speed;
//		LWJGLGameRenderer.ry += 1.3 * speed;
//		LWJGLGameRenderer.rz += 0.6 * speed;
		window.getInput().handleInput();
		mouse: if (mouseMovement) {
			LWJGLCamera cam = window.getCamera();
			float dy = (float) (window.mouse.getDeltaX() * 0.4) * mouseSensivity;
			float dx = (float) (window.mouse.getDeltaY() * 0.4) * mouseSensivity;
			if ((dx != 0 || dy != 0) && ignoreNextMovement) {
				ignoreNextMovement = false;
				break mouse;
			}
			Vector3f rot = cam.getRotation();
			rot.x = Math.clamp(rot.x + dx, -90F, 90F);
			rot.y = rot.y + dy;
		}
	}
}
