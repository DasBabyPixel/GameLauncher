package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ShaderProgram {

	public static final Logger logger = Logger.getLogger(ShaderProgram.class.getName());

	private final int id;
	public final Shader vertexShader, fragmentShader;

	public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		id = glCreateProgram();
		glAttachShader(id, vertexShader.id);
		glAttachShader(id, fragmentShader.id);
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) != 1) {
			error("ShaderProgram Link Problem: " + glGetProgramInfoLog(id));
		}
	}

	public void use() {
		glUseProgram(id);
	}
	
	public void delete() {
		glDeleteProgram(id);
	}

	private static void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	public static class Shader {

		private final int id;

		public Shader(Shader.Type type, String source) {
			id = glCreateShader(type.getId());
			glShaderSource(id, source);
			glCompileShader(id);
			if (glGetShaderi(id, GL_COMPILE_STATUS) != 1) {
				error("Shader Compile Problem: " + glGetShaderInfoLog(id));
			}
		}
		
		public void delete() {
			glDeleteShader(id);
		}

		public int getId() {
			return id;
		}

		public static enum Type {
			VERTEX(GL_VERTEX_SHADER), FRAGMENT(GL_FRAGMENT_SHADER);

			private final int id;

			private Type(int id) {
				this.id = id;
			}

			public int getId() {
				return id;
			}
		}
	}
}
