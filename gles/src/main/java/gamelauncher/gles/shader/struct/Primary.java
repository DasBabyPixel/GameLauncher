package gamelauncher.gles.shader.struct;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.gles.shader.BasicUniform;
import gamelauncher.gles.shader.BasicUniform.Type;
import gamelauncher.gles.shader.GLESShaderProgram;
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

    @Override
    public String name() {
        return type.getGlName();
    }

    @Override
    public Uniform createUniform(ShaderProgram program, String name) {
        return new BasicUniform(((GLESShaderProgram) program).gles().memoryManagement(), StateRegistry.currentGl()
                .glGetUniformLocation(((GLESShaderProgram) program).getProgramId(), name), type);
    }

}
