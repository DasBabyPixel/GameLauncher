package game;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import game.render.RenderMode;
import game.render.Renderer;
import game.render.Window;
import game.resource.EmbedResourceLoader;
import game.settings.MainSettingSection;
import game.settings.SettingSection;
import game.util.logging.Logger;

public class Game {

	public Window window;
	public final SettingSection settings = new MainSettingSection();
	public final Path gameDirectory = Paths.get("labyrinth");
	public final Path settingsFile = gameDirectory.resolve("settings");
	public final Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();
	public final GameRenderer gameRenderer = new GameRenderer();

	public void start() throws IOException {
		if (window != null) {
			return;
		}

		new EmbedResourceLoader().set();

		Logger logger = Logger.getLogger(Game.class);
		System.setOut(logger.createPrintStream());
		Files.createDirectories(gameDirectory);
		if (!Files.exists(settingsFile)) {
			Files.createFile(settingsFile);
			settings.setDefaultValue();
			saveSettings();
		} else {
			byte[] bytes = Files.readAllBytes(settingsFile);
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonElement element = settingsGson.fromJson(json, JsonElement.class);
			settings.deserialize(element);
		}
		settings.getSetting(MainSettingSection.TEST).setValue("test2");
		saveSettings();

		window = new Window(400, 400, "Game");
		gameRenderer.setRenderer(new Renderer() {
			private double r1 = 0.0, g1 = 0.3, b1 = 0.6;
			private double r2 = 0.5, g2 = 0.8, b2 = 0.1;
			private double r3 = 0.8, g3 = 0.1, b3 = 0.9;

			@Override
			public void render(Window window) {
				glBegin(GL_TRIANGLES);

				double min = -(Math.sin(Math.toRadians(System.currentTimeMillis() / 20)) + 1) / 3 - 0.3;

				glVertex2d(min, -0.5);
				glColor3d(r1, g1, b1);
				glVertex2d(0, 0.5);
				glColor3d(r2, g2, b2);
				glVertex2d(-min, -0.5);
				glColor3d(r3, g3, b3);
				glEnd();
			}
		});

		window.frameRenderer.set(gameRenderer);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);

		window.createWindow();
		window.startRendering();
		window.show();
		window.setFloating(true);

		window.getFrameCounter().ifPresent(f -> {
			f.limit(60);
		});
		while (!window.isClosed()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void saveSettings() throws IOException {
		byte[] bytes = settingsGson.toJson(settings.serialize()).getBytes(StandardCharsets.UTF_8);
		Files.write(settingsFile, bytes);
	}
}
