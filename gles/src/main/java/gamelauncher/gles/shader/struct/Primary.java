package gamelauncher.gles.shader.struct;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.gles.shader.BasicUniform;
import gamelauncher.gles.shader.BasicUniform.Type;
import gamelauncher.gles.shader.GLESShaderProgram;
import gamelauncher.gles.shader.ShaderConfiguration;
import gamelauncher.gles.states.StateRegistry;

/**
 * @author DasBabyPixel
 */
public class Primary implements Struct {

    private final Type type;

    public Primary(Type type) {
        this.type = type;
    }

    /**
     * @return the type of this struct
     */
    public Type getType() {
        return type;
    }

    @Override public String name() {
        return type.getGlName();
    }

    @Override public Uniform createUniform(ShaderProgram program, ShaderConfiguration.Uniform uniformConfiguration) {
        GLESShaderProgram sp = (GLESShaderProgram) program;
        int location = StateRegistry.currentGl().glGetUniformLocation(sp.getProgramId(), uniformConfiguration.name());
        if (location == -1) return null;
        return new BasicUniform(sp.gles().launcher().profiler(), sp.gles().memoryManagement(), uniformConfiguration.name(), location, type, uniformConfiguration.values());
    }

}
