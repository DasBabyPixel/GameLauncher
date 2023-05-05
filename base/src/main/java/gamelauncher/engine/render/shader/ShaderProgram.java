package gamelauncher.engine.render.shader;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DasBabyPixel
 */
public abstract class ShaderProgram extends AbstractGameResource {

    public final Map<String, Uniform> uniformMap;
    public final List<Uniform> uploadUniforms;
    protected final GameLauncher launcher;
    public Uniform uMaterial = EmptyUniform.instance;
    public Uniform uModelMat = EmptyUniform.instance;
    public Uniform uColorMultiplier = EmptyUniform.instance;
    public Uniform uId = EmptyUniform.instance;
    public Uniform uModelViewMat = EmptyUniform.instance;
    public Uniform uViewMat = EmptyUniform.instance;
    public Uniform uProjectionMat = EmptyUniform.instance;
    public Uniform uCameraPosition = EmptyUniform.instance;
    public Uniform uHasTexture = EmptyUniform.instance;
    //    public Uniform uambientLight = EmptyUniform.instance;
    public Uniform uTexture = EmptyUniform.instance;
    //    public Uniform uspecularPower = EmptyUniform.instance;
//    public Uniform upointLight = EmptyUniform.instance;
//    public Uniform udirectionalLight = EmptyUniform.instance;
    public Uniform uTextureAddColor = EmptyUniform.instance;
    public Uniform uApplyLighting = EmptyUniform.instance;

    public ShaderProgram(GameLauncher launcher) {
        this.launcher = launcher;
        this.uniformMap = new HashMap<>();
        this.uploadUniforms = new ArrayList<>();
    }

    /**
     * @return the {@link GameLauncher}
     */
    public GameLauncher getLauncher() {
        return this.launcher;
    }

    /**
     * Clears the value of all {@link Uniform}s in this {@link ShaderProgram}
     */
    public void clearUniforms() {
        for (int i = 0; i < uploadUniforms.size(); i++) {
            uploadUniforms.get(i).clear();
        }
    }

    /**
     * @param name the name of the uniform
     * @return if this {@link ShaderProgram} has a {@link Uniform} with the given name
     */
    public boolean hasUniform(String name) {
        return this.uniformMap.containsKey(name);
    }

    /**
     * Uploads all {@link Uniform}s in this {@link ShaderProgram}
     */
    public void uploadUniforms() {
        for (int i = 0; i < uploadUniforms.size(); i++) {
            uploadUniforms.get(i).upload();
        }
    }

    /**
     *
     */
    public abstract void bind();

    /**
     *
     */
    public abstract void unbind();

}
