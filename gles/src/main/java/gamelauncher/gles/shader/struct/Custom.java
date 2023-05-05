package gamelauncher.gles.shader.struct;

import gamelauncher.engine.render.shader.ObjectUniform;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.gles.shader.ShaderConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DasBabyPixel
 */
public class Custom implements Struct {

    private final String name;
    private final Map<String, Struct> variables;

    public Custom(String name) {
        this.name = name;
        this.variables = new HashMap<>();
    }

    /**
     * @return the variables
     */
    public Map<String, Struct> getVariables() {
        return variables;
    }

    @Override public String name() {
        return name;
    }

    @Override public Uniform createUniform(ShaderProgram program, ShaderConfiguration.Uniform uniformConfiguration) {
        return new ObjectUniform(program, uniformConfiguration.name());
    }
}
