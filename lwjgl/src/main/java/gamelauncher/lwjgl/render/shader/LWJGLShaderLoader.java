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
	final Map<Resource, LWJGLShaderProgram> programs = new ConcurrentHashMap<>();

	@Override
	public ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException {
		ResourceLoader loader = launcher.getResourceLoader();
		Resource resource = loader.getResource(path);
		if(programs.containsKey(resource)) {
			LWJGLShaderProgram program = programs.get(resource);
			program.refCount.incrementAndGet();
			return program;
		}
		ResourceStream rootStream = resource.newResourceStream();
		String rootutf8 = rootStream.readUTF8FullyClose();
		JsonObject root;
		try {
			Path parent = path.getParent();
			root = gson.fromJson(rootutf8, JsonObject.class);
			String vspathstr = root.get("vertexShader").getAsString();
			String fspathstr = root.get("fragmentShader").getAsString();
			String vscode = loader.getResource(parent.resolve(vspathstr)).newResourceStream().readUTF8FullyClose();
			String fscode = loader.getResource(parent.resolve(fspathstr)).newResourceStream().readUTF8FullyClose();
			LWJGLShaderProgram program = new LWJGLShaderProgram(launcher);
			programs.put(resource, program);
			program.createVertexShader(vscode);
			program.createFragmentShader(fscode);
			program.link();
			program.deleteVertexShader();
			program.deleteFragmentShader();
			Collection<Struct> structs = null;
			if (root.has("structs")) {
				structs = loadStructs(root.get("structs").getAsJsonArray());
			} else {
				structs = Arrays.asList(Struct.primitives);
			}
			loadUniforms(program, root.get("uniforms").getAsJsonObject(), structs);
			return program;
		} catch (JsonSyntaxException ex) {
			throw new GameException("Invalid Json File", ex);
		}
	}

	private static void loadUniforms(LWJGLShaderProgram p, JsonObject uniforms, Collection<Struct> structs)
			throws GameException {
		loadUniform(p, structs, uniforms, "material", u -> p.umaterial = u);
		loadUniform(p, structs, uniforms, "modelMatrix", u -> p.umodelMatrix = u);
		loadUniform(p, structs, uniforms, "color", u -> p.ucolor = u);
		loadUniform(p, structs, uniforms, "modelViewMatrix", u -> p.umodelViewMatrix = u);
		loadUniform(p, structs, uniforms, "viewMatrix", u -> p.uviewMatrix = u);
		loadUniform(p, structs, uniforms, "projectionMatrix", u -> p.uprojectionMatrix = u);
		loadUniform(p, structs, uniforms, "camera_pos", u -> p.ucamera_pos = u);
		loadUniform(p, structs, uniforms, "ambientLight", u -> p.uambientLight = u);
		loadUniform(p, structs, uniforms, "texture_sampler", u -> p.utexture_sampler = u);
		loadUniform(p, structs, uniforms, "specularPower", u -> p.uspecularPower = u);
		loadUniform(p, structs, uniforms, "pointLight", u -> p.upointLight = u);
		loadUniform(p, structs, uniforms, "directionalLight", u -> p.udirectionalLight = u);
		loadUniform(p, structs, uniforms, "textureAddColor", u -> p.utextureAddColor = u);
		loadUniform(p, structs, uniforms, "applyLighting", u -> p.uapplyLighting = u);
	}

	private static Uniform loadUniform(LWJGLShaderProgram program, String uniform, Struct struct) {
		Uniform u = struct.createUniform(program, uniform);
		program.uniformMap.put(uniform, u);
		if (struct instanceof Custom) {
			Custom c = (Custom) struct;
			for (Map.Entry<String, Struct> variable : c.getVariables().entrySet()) {
				loadUniform(program, uniform + "." + variable.getKey(), variable.getValue());
			}
		}
		return u;
	}

	private static void loadUniform(LWJGLShaderProgram program, Collection<Struct> structs, JsonObject ouniforms,
			String uniform, GameConsumer<Uniform> successConsumer) throws GameException {
		if (!ouniforms.has(uniform)) {
			return;
		}
		Uniform u = loadUniform(program, uniform, getStruct(structs, ouniforms.get(uniform).getAsString()));
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
				custom.getVariables().put(vname, getStruct(structs, vtype));
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
