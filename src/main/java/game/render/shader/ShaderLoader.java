package game.render.shader;

import java.io.IOException;

import game.render.shader.ShaderProgram.Shader;
import game.resource.ResourceLoader;
import game.resource.ResourcePath;
import game.resource.ResourceStream;

public class ShaderLoader {

	public static Shader loadShader(Shader.Type type, ResourcePath path) throws IOException {
		ResourceStream stream = ResourceLoader.getInstance().getResource(path).newResourceStream();
		String source = stream.readUTF8Fully();
		stream.close();
		return new Shader(type, source);
	}

}
