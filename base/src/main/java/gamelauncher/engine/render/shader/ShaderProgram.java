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
    public Uniform umaterial = EmptyUniform.instance;
    public Uniform umodelMatrix = EmptyUniform.instance;
    public Uniform ucolor = EmptyUniform.instance;
    public Uniform uid = EmptyUniform.instance;
    public Uniform umodelViewMatrix = EmptyUniform.instance;
    public Uniform uviewMatrix = EmptyUniform.instance;
    public Uniform uprojectionMatrix = EmptyUniform.instance;
    public Uniform ucamera_pos = EmptyUniform.instance;
    public Uniform uambientLight = EmptyUniform.instance;
    public Uniform utexture_sampler = EmptyUniform.instance;
    public Uniform uspecularPower = EmptyUniform.instance;
    public Uniform upointLight = EmptyUniform.instance;
    public Uniform udirectionalLight = EmptyUniform.instance;
    public Uniform utextureAddColor = EmptyUniform.instance;
    public Uniform uapplyLighting = EmptyUniform.instance;
    public Uniform uhasTexture = EmptyUniform.instance;

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
