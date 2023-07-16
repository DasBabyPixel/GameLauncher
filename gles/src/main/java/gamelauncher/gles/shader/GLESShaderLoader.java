package gamelauncher.gles.shader;

import com.google.gson.*;
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
import gamelauncher.gles.GLES;
import gamelauncher.gles.shader.struct.Custom;
import gamelauncher.gles.shader.struct.Struct;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public class GLESShaderLoader implements ShaderLoader {
    /**
     * The gson for shaderPrograms
     */
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.logger();
    private final Map<Resource, GLESShaderProgram> programs = new ConcurrentHashMap<>();
    private final GLES gles;

    public GLESShaderLoader(GLES gles) {
        this.gles = gles;
    }

    private static void loadUniforms(GLESShaderProgram p, ShaderConfiguration.UniformMap uniforms, Collection<Struct> structs) throws GameException {
        loadUniform(p, structs, uniforms, "Material", u -> p.uMaterial = u);
        loadUniform(p, structs, uniforms, "ModelMat", u -> p.uModelMat = u);
        loadUniform(p, structs, uniforms, "ColorMultiplier", u -> p.uColorMultiplier = u);
        loadUniform(p, structs, uniforms, "ModelViewMat", u -> p.uModelViewMat = u);
        loadUniform(p, structs, uniforms, "ViewMat", u -> p.uViewMat = u);
        loadUniform(p, structs, uniforms, "ProjectionMat", u -> p.uProjectionMat = u);
        loadUniform(p, structs, uniforms, "CameraPosition", u -> p.uCameraPosition = u);
//        loadUniform(p, structs, uniforms, "ambientLight", u -> p.uambientLight = u);
        loadUniform(p, structs, uniforms, "Texture", u -> p.uTexture = u);
//        loadUniform(p, structs, uniforms, "specularPower", u -> p.uspecularPower = u);
//        loadUniform(p, structs, uniforms, "pointLight", u -> p.upointLight = u);
//        loadUniform(p, structs, uniforms, "directionalLight", u -> p.udirectionalLight = u);
        loadUniform(p, structs, uniforms, "TextureAddColor", u -> p.uTextureAddColor = u);
        loadUniform(p, structs, uniforms, "ApplyLighting", u -> p.uApplyLighting = u);
        loadUniform(p, structs, uniforms, "HasTexture", u -> p.uHasTexture = u);
        loadUniform(p, structs, uniforms, "Id", u -> p.uId = u);
    }

    private static Uniform loadUniform(GLESShaderProgram program, ShaderConfiguration.Uniform uniform, Struct struct, float[] defaultValues) {
        Uniform u = struct.createUniform(program, uniform);
        if (u == null) return null;
        program.uniformMap.put(uniform.name(), u);
        if (struct instanceof Custom) {
            throw new UnsupportedOperationException();
//            Custom c = (Custom) struct;
//            for (Map.Entry<String, Struct> variable : c.getVariables().entrySet()) {
//                loadUniform(program, uniform.name() + "." + variable.getKey(), variable.getValue(), defaultValues);
//            }
        }
        return u;
    }

    private static void loadUniform(GLESShaderProgram program, Collection<Struct> structs, ShaderConfiguration.UniformMap uniforms, String uniform, GameConsumer<Uniform> successConsumer) throws GameException {
        if (!uniforms.typeByUniform().containsKey(uniform)) return;
        Uniform u = loadUniform(program, uniforms.typeByUniform().get(uniform), GLESShaderLoader.getStruct(structs, uniforms.typeByUniform().get(uniform).type()), uniforms.typeByUniform().get(uniform).values());
        if (u == null) return;
        program.uploadUniforms.add(u);
        successConsumer.accept(u);
    }

    private static Collection<Struct> loadStructs(ShaderConfiguration.Structs astructs) throws GameException {
        Collection<Struct> structs = new HashSet<>(Arrays.asList(Struct.primitives));
        Map<ShaderConfiguration.StructConfiguration, Custom> map = new HashMap<>();
        for (ShaderConfiguration.StructConfiguration sconf : astructs.structs()) {
            String sname = sconf.name();
            Custom custom = new Custom(sname);
            structs.add(custom);
            map.put(sconf, custom);
        }
        for (ShaderConfiguration.StructConfiguration sconf : astructs.structs()) {
            for (ShaderConfiguration.StructVariable variable : sconf.variables().variables()) {
                map.get(sconf).getVariables().put(variable.name(), getStruct(structs, variable.type()));
            }
        }
        return structs;
    }

    private static Struct getStruct(Collection<Struct> structs, String type) throws GameException {
        for (Struct struct : structs) {
            if (struct.name().equals(type)) {
                return struct;
            }
        }
        throw new GameException("Struct not found: " + type);
    }

    @Override public ShaderProgram loadShader(Path path) throws GameException {
        ResourceLoader loader = gles.launcher().resourceLoader();
        Resource resource = loader.resource(path);
        if (this.programs.containsKey(resource)) {
            GLESShaderProgram program = this.programs.get(resource);
            program.require();
            return program;
        }
        ResourceStream rootStream = resource.newResourceStream();
        String rootutf8 = rootStream.readUTF8FullyClose();
        JsonObject root;
        try {
            Path parentFile = path.getParent();
            root = GLESShaderLoader.gson.fromJson(rootutf8, JsonObject.class);

            String[] versions = root.keySet().toArray(new String[0]);
            int newestIdx = newestVersion(versions);
            String newestVersion = versions[newestIdx];
            ShaderConfiguration[] configurations = loadConfigurations(parentFile.resolve(root.get(newestVersion).getAsString() + ".json"));
            ShaderConfiguration configuration = configurations[0];

            Path vspath = parentFile.resolve(configuration.vertexShader() + ".glsl");
            Path fspath = parentFile.resolve(configuration.fragmentShader() + ".glsl");

            String vscode = loader.resource(vspath).newResourceStream().readUTF8FullyClose();
            String fscode = loader.resource(fspath).newResourceStream().readUTF8FullyClose();

            GLESShaderProgram program = new GLESShaderProgram(gles);
            this.programs.put(resource, program);
            program.cleanupFuture().thenRun(() -> this.programs.remove(resource));
            program.createVertexShader(vscode);
            program.createFragmentShader(fscode);
            program.link();
            program.deleteVertexShader();
            program.deleteFragmentShader();
            Collection<Struct> structs;
            structs = loadStructs(configuration.structs());
            loadUniforms(program, configuration.uniforms(), structs);
            return program;
        } catch (JsonSyntaxException ex) {
            throw new GameException("Invalid Json File", ex);
        }
    }

    private ShaderConfiguration[] loadConfigurations(Path path) throws GameException {
        JsonArray array = gson.fromJson(gles.launcher().resourceLoader().resource(path).newResourceStream().readUTF8FullyClose(), JsonArray.class);
        int idx = 0;
        ShaderConfiguration[] configurations = new ShaderConfiguration[array.size()];
        for (JsonElement e1 : array) {
            JsonObject json = e1.getAsJsonObject();
            JsonArray acapabilities = json.getAsJsonArray("capabilities");
            String[] capabilities = new String[acapabilities.size()];
            int i = 0;
            for (JsonElement e2 : acapabilities) capabilities[i++] = e2.getAsString();
            JsonArray atargets = json.getAsJsonArray("targets");
            String[] targets = new String[atargets.size()];
            i = 0;
            for (JsonElement e2 : atargets) targets[i++] = e2.getAsString();
            String vertexShader = json.get("vertexShader").getAsString();
            String fragmentShader = json.get("fragmentShader").getAsString();
            ShaderConfiguration.UniformMap uniforms = new ShaderConfiguration.UniformMap(new HashMap<>());
            for (JsonElement e2 : json.get("uniforms").getAsJsonArray()) {
                JsonObject json2 = e2.getAsJsonObject();
                String name = json2.get("name").getAsString();
                String type = json2.get("type").getAsString();
                JsonArray avalues = json2.get("values").getAsJsonArray();
                float[] values = new float[avalues.size()];
                i = 0;
                for (JsonElement e3 : avalues) values[i++] = e3.getAsJsonPrimitive().getAsFloat();
                uniforms.typeByUniform().put(name, new ShaderConfiguration.Uniform(name, type, values));
            }
            ShaderConfiguration.Structs structs = new ShaderConfiguration.Structs(new ArrayList<>());
            ShaderConfiguration configuration = new ShaderConfiguration(vertexShader, fragmentShader, capabilities, targets, uniforms, structs);
            configurations[idx++] = configuration;
        }
        return configurations;
    }

    private int newestVersion(String... versions) {
        int newestIdx = -1;
        String newest = null;
        for (int i = 0; i < versions.length; i++) {
            if (newest == null) newest = versions[newestIdx = i];
            else if (versions[i].compareTo(newest) > 0) newest = versions[newestIdx = i];
        }
        return newestIdx;
    }
}
