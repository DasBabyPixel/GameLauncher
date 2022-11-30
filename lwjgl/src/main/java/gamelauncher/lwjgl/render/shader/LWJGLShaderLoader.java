package gamelauncher.lwjgl.render.shader;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.shader.struct.Custom;
import gamelauncher.lwjgl.render.shader.struct.Struct;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLShaderLoader implements ShaderLoader {

	/**
	 * The gson for shaderPrograms
	 */
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final Logger logger = Logger.getLogger();

	private final Map<Resource, LWJGLShaderProgram> programs = new ConcurrentHashMap<>();

	@Override
	public ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException {
		ResourceLoader loader = launcher.getResourceLoader();
		Resource resource = loader.getResource(path);
		if (this.programs.containsKey(resource)) {
			LWJGLShaderProgram program = this.programs.get(resource);
			program.refCount.incrementAndGet();
			return program;
		}
		ResourceStream rootStream = resource.newResourceStream();
		String rootutf8 = rootStream.readUTF8FullyClose();
		JsonObject root;
		try {
			Path parent = path.getParent();
			root = LWJGLShaderLoader.gson.fromJson(rootutf8, JsonObject.class);
			String vspathstr = root.get("vertexShader").getAsString();
			String fspathstr = root.get("fragmentShader").getAsString();
			String vscode = loader.getResource(parent.resolve(vspathstr)).newResourceStream().readUTF8FullyClose();
			String fscode = loader.getResource(parent.resolve(fspathstr)).newResourceStream().readUTF8FullyClose();
			LWJGLShaderProgram program = new LWJGLShaderProgram(launcher, path);
			this.programs.put(resource, program);
			program.cleanupFuture().thenRun(() -> this.programs.remove(resource));
			program.createVertexShader(vscode);
			program.createFragmentShader(fscode);
			program.link();
			program.deleteVertexShader();
			program.deleteFragmentShader();
			Collection<Struct> structs = null;
			if (root.has("structs")) {
				structs = LWJGLShaderLoader.loadStructs(root.get("structs").getAsJsonArray());
			} else {
				structs = Arrays.asList(Struct.primitives);
			}
			LWJGLShaderLoader.loadUniforms(program, root.get("uniforms").getAsJsonObject(), structs);
			return program;
		} catch (JsonSyntaxException ex) {
			throw new GameException("Invalid Json File", ex);
		}
	}

	private static void loadUniforms(LWJGLShaderProgram p, JsonObject uniforms, Collection<Struct> structs)
			throws GameException {
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "material", u -> p.umaterial = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "modelMatrix", u -> p.umodelMatrix = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "color", u -> p.ucolor = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "modelViewMatrix", u -> p.umodelViewMatrix = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "viewMatrix", u -> p.uviewMatrix = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "projectionMatrix", u -> p.uprojectionMatrix = u, true);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "camera_pos", u -> p.ucamera_pos = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "ambientLight", u -> p.uambientLight = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "texture_sampler", u -> p.utexture_sampler = u, true);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "specularPower", u -> p.uspecularPower = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "pointLight", u -> p.upointLight = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "directionalLight", u -> p.udirectionalLight = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "textureAddColor", u -> p.utextureAddColor = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "applyLighting", u -> p.uapplyLighting = u, false);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "hasTexture", u -> p.uhasTexture = u, true);
		LWJGLShaderLoader.loadUniform(p, structs, uniforms, "hasPixelTextureCoords", u -> p.uhasPixelTextureCoords = u,
				true);
	}

	private static Uniform loadUniform(LWJGLShaderProgram program, String uniform, Struct struct) {
		Uniform u = struct.createUniform(program, uniform);
		program.uniformMap.put(uniform, u);
		if (struct instanceof Custom) {
			Custom c = (Custom) struct;
			for (Map.Entry<String, Struct> variable : c.getVariables().entrySet()) {
				LWJGLShaderLoader.loadUniform(program, uniform + "." + variable.getKey(), variable.getValue());
			}
		}
		return u;
	}

	private static void loadUniform(LWJGLShaderProgram program, Collection<Struct> structs, JsonObject ouniforms,
			String uniform, GameConsumer<Uniform> successConsumer, boolean required) throws GameException {
		if (!ouniforms.has(uniform)) {
			if (required) {
				LWJGLShaderLoader.logger
						.errorf("ShaderProgram %s does not contain a uniform named %s! This is NOT recommended. "
								+ "Fix this as soon as possible!%n%s", program.path, uniform, new GameException());
			}
			return;
		}
		Uniform u = LWJGLShaderLoader.loadUniform(program, uniform,
				LWJGLShaderLoader.getStruct(structs, ouniforms.get(uniform).getAsString()));
		program.uploadUniforms.add(u);
		successConsumer.accept(u);
	}

	private static Collection<Struct> loadStructs(JsonArray astructs) throws GameException {
		Collection<Struct> structs = new HashSet<>();
		structs.addAll(Arrays.asList(Struct.primitives));
		for (JsonElement estruct : astructs) {
			JsonObject ostruct = estruct.getAsJsonObject();
			String sname = ostruct.get("name").getAsString();
			JsonArray ovariables = ostruct.get("variables").getAsJsonArray();
			Custom custom = new Custom(sname);
			for (JsonElement evariable : ovariables) {
				JsonObject ovariable = evariable.getAsJsonObject();
				String vname = ovariable.get("name").getAsString();
				String vtype = ovariable.get("type").getAsString();
				custom.getVariables().put(vname, LWJGLShaderLoader.getStruct(structs, vtype));
			}
			structs.add(custom);
		}
		return structs;
	}

	private static Struct getStruct(Collection<Struct> structs, String name) throws GameException {
		for (Struct struct : structs) {
			if (struct.name().equals(name)) {
				return struct;
			}
		}
		throw new GameException("Struct not found: " + name);
	}

}
