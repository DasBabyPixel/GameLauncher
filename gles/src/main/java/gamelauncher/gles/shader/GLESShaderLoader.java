package gamelauncher.gles.shader;

import com.google.gson.*;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.data.Files;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.shader.struct.Custom;
import gamelauncher.gles.shader.struct.Struct;
import gamelauncher.gles.states.StateRegistry;

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
    private final Map<Resource, GLESShaderProgram> programs = new ConcurrentHashMap<>();
    private final GLES gles;

    public GLESShaderLoader(GLES gles) {
        this.gles = gles;
    }

    private static void loadUniforms(GLESShaderProgram p, ShaderConfiguration.UniformMap uniforms, Collection<Struct> structs) throws GameException {
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
        loadUniform(p, structs, uniforms, "hasTexture", u -> p.uhasTexture = u);
        loadUniform(p, structs, uniforms, "id", u -> p.uid = u);
    }

    private static Uniform loadUniform(GLESShaderProgram program, String uniform, Struct struct) {
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

    private static void loadUniform(GLESShaderProgram program, Collection<Struct> structs, ShaderConfiguration.UniformMap uniforms, String uniform, GameConsumer<Uniform> successConsumer) throws GameException {
        if (!uniforms.typeByUniform().containsKey(uniform)) return;
        Uniform u = loadUniform(program, uniform, GLESShaderLoader.getStruct(structs, uniforms.typeByUniform().get(uniform)));
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

    private static Struct getStruct(Collection<Struct> structs, String name) throws GameException {
        for (Struct struct : structs) {
            if (struct.name().equals(name)) {
                return struct;
            }
        }
        throw new GameException("Struct not found: " + name);
    }

    private static ShaderConfiguration selectBestConfiguration(Collection<ShaderConfiguration> configurations) {
        GLES31 gl = StateRegistry.currentGl();
        int major = gl.glGetInteger(GLES30.GL_MAJOR_VERSION);
        int minor = gl.glGetInteger(GLES30.GL_MINOR_VERSION);

        int bestMajor = -1, bestMinor = -1;
        ShaderConfiguration bestConfiguration = null;
        for (ShaderConfiguration configuration : configurations) {
            String v = configuration.version();
            if (v.equals(ShaderConfiguration.DEFAULT_VERSION)) v = "2.0";
            String[] a = v.split("\\.");
            int curMajor = Integer.parseInt(a[0]);
            int curMinor = Integer.parseInt(a[1]);
            if (curMajor > major) continue;
            if (curMajor == major && curMinor > minor) continue;
            if (curMajor < bestMajor) continue;
            if (curMajor == bestMajor && curMinor < bestMinor) continue;
            bestMajor = curMajor;
            bestMinor = curMinor;
            bestConfiguration = configuration;
        }
        return bestConfiguration;
    }

    private static void loadConfigurations(Map<ShaderConfigurationTemplate, ShaderConfiguration> configurations, ShaderConfigurationTemplate template) {
        if (template == null) return;
        if (configurations.containsKey(template)) return;
        loadConfigurations(configurations, template.parent());
        ShaderConfiguration parent = template.parent() == null ? null : configurations.get(template.parent());
        String version = template.version();
        String vertexShader = template.json().has("vertexShader") ? template.json().get("vertexShader").getAsString() : parent != null ? parent.vertexShader() : null;
        String fragmentShader = template.json().has("fragmentShader") ? template.json().get("fragmentShader").getAsString() : parent != null ? parent.fragmentShader() : null;
        ShaderConfiguration.Structs structs = new ShaderConfiguration.Structs(new ArrayList<>());
        if (template.json().has("structs")) {
            for (JsonElement element : template.json().get("structs").getAsJsonArray()) {
                JsonObject so = element.getAsJsonObject();
                List<ShaderConfiguration.StructVariable> variables = new ArrayList<>();
                if (so.has("variables")) {
                    for (JsonElement element2 : so.get("variables").getAsJsonArray()) {
                        variables.add(new ShaderConfiguration.StructVariable(element2.getAsJsonObject().get("name").getAsString(), element2.getAsJsonObject().get("type").getAsString()));
                    }
                }
                structs.structs().add(new ShaderConfiguration.StructConfiguration(so.get("name").getAsString(), new ShaderConfiguration.StructVariables(variables)));
            }
        }
        if (parent != null) structs.structs().addAll(parent.structs().structs());
        ShaderConfiguration.UniformMap uniforms = new ShaderConfiguration.UniformMap(new HashMap<>());
        if (template.json().has("uniforms")) {
            JsonObject o = template.json().get("uniforms").getAsJsonObject();
            for (String key : o.keySet()) {
                uniforms.typeByUniform().put(key, o.get(key).getAsString());
            }
        }
        if (parent != null) uniforms.typeByUniform().putAll(parent.uniforms().typeByUniform());
        configurations.put(template, new ShaderConfiguration(version, vertexShader, fragmentShader, uniforms, structs));
    }

    @SuppressWarnings("NewApi")
    @Override
    public ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException {
        ResourceLoader loader = launcher.resourceLoader();
        Resource resource = loader.resource(path);
        if (this.programs.containsKey(resource)) {
            GLESShaderProgram program = this.programs.get(resource);
            program.refCount.incrementAndGet();
            return program;
        }
        ResourceStream rootStream = resource.newResourceStream();
        String rootutf8 = rootStream.readUTF8FullyClose();
        JsonObject root;
        try {
            Path parentFile = path.getParent();
            root = GLESShaderLoader.gson.fromJson(rootutf8, JsonObject.class);

            Map<String, ShaderConfigurationTemplate> templates = new HashMap<>();
            if (root.has("versions")) {
                for (JsonElement element : root.get("versions").getAsJsonArray()) {
                    JsonObject json = element.getAsJsonObject();
                    if (!json.has("version")) throw new GameException("Shader Info File " + path + " has invalid version qualifiers for shaders");
                    String version = json.get("version").getAsString();
                    String parent = json.has("parent") ? json.get("parent").getAsString() : null;
                    templates.put(version, new ShaderConfigurationTemplate(version, parent, json));
                }
            }
            if (!templates.containsKey(ShaderConfiguration.DEFAULT_VERSION)) {
                templates.put(ShaderConfiguration.DEFAULT_VERSION, new ShaderConfigurationTemplate(ShaderConfiguration.DEFAULT_VERSION, null, root));
            }
            for (ShaderConfigurationTemplate template : templates.values()) {
                if (template.parentVersion() == null) continue;
                template.parent = templates.get(template.parentVersion());
            }
            Map<ShaderConfigurationTemplate, ShaderConfiguration> configurations = new HashMap<>();
            for (ShaderConfigurationTemplate template : templates.values()) {
                loadConfigurations(configurations, template);
            }

            ShaderConfiguration best = selectBestConfiguration(configurations.values());
            if (best == null) throw new GameException("Unable to find shader configuration for " + path);

            String vspathstr = best.vertexShader();
            String fspathstr = best.fragmentShader();
            Path vspath = parentFile.resolve(vspathstr + best.version().replace(".", "") + ".glsl");
            if (!Files.exists(vspath)) vspath = parentFile.resolve(vspathstr + ".glsl");
            Path fspath = parentFile.resolve(fspathstr + best.version().replace(".", "") + ".glsl");
            if (!Files.exists(fspath)) fspath = parentFile.resolve(fspathstr + ".glsl");

            String vscode = loader.resource(vspath).newResourceStream().readUTF8FullyClose();
            String fscode = loader.resource(fspath).newResourceStream().readUTF8FullyClose();

            GLESShaderProgram program = new GLESShaderProgram(gles, launcher, path);
            this.programs.put(resource, program);
            program.cleanupFuture().thenRun(() -> this.programs.remove(resource));
            program.createVertexShader(vscode);
            program.createFragmentShader(fscode);
            program.link();
            program.deleteVertexShader();
            program.deleteFragmentShader();
            Collection<Struct> structs;
            structs = loadStructs(best.structs());
            loadUniforms(program, best.uniforms(), structs);
            return program;
        } catch (JsonSyntaxException ex) {
            throw new GameException("Invalid Json File", ex);
        }
    }

    private static class ShaderConfigurationTemplate {
        private final String version;
        private final String parentVersion;
        private final JsonObject json;
        private ShaderConfigurationTemplate parent;

        public ShaderConfigurationTemplate(String version, String parent, JsonObject json) {
            this.version = version;
            this.parentVersion = parent;
            this.json = json;
        }

        public ShaderConfigurationTemplate parent() {
            return parent;
        }

        public String version() {
            return version;
        }

        public String parentVersion() {
            return parentVersion;
        }

        public JsonObject json() {
            return json;
        }
    }
}
