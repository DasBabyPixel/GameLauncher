package gamelauncher.lwjgl.render.shader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.ResourceStream;

public class ShaderLoader {

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException {
		ResourceLoader loader = launcher.getResourceLoader();
		ResourceStream rootStream = loader.getResource(path).newResourceStream();
		String rootutf8 = rootStream.readUTF8FullyClose();
		JsonObject root;
		try {
			Path parent = path.getParent();
			root = gson.fromJson(rootutf8, JsonObject.class);
			String vspathstr = root.get("vertex").getAsString();
			String fspathstr = root.get("fragment").getAsString();
			String vscode = loader.getResource(parent.resolve(vspathstr)).newResourceStream().readUTF8FullyClose();
			String fscode = loader.getResource(parent.resolve(fspathstr)).newResourceStream().readUTF8FullyClose();
			ShaderProgram program = new ShaderProgram(launcher);
		} catch (JsonSyntaxException ex) {
			throw new GameException("Invalid Json File", ex);
		}
		return null;
	}
}
