/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.shader;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.engine.util.Debug;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;
import java8.util.concurrent.CompletableFuture;

import java.nio.file.Path;

public class GLESShaderProgram extends ShaderProgram {

    final Path path;
    private final int programId;
    private final GLES gles;
    private int vertexShaderId;
    private int fragmentShaderId;

    public GLESShaderProgram(GLES gles, Path path) throws GameException {
        super(gles.launcher());
        this.gles = gles;
        this.path = path;
        this.programId = StateRegistry.currentGl().glCreateProgram();
        if (this.programId == 0) {
            throw new GameException("Could not create Shader");
        }
    }

    public GLES gles() {
        return gles;
    }

    public void createVertexShader(String shaderCode) throws GameException {
        this.vertexShaderId = this.createShader(shaderCode, GLES20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws GameException {
        this.fragmentShaderId = this.createShader(shaderCode, GLES20.GL_FRAGMENT_SHADER);
    }

    public int getProgramId() {
        return this.programId;
    }

    public void deleteVertexShader() {
        StateRegistry.currentGl().glDeleteShader(this.vertexShaderId);
    }

    public void deleteFragmentShader() {
        StateRegistry.currentGl().glDeleteShader(this.fragmentShaderId);
    }

    protected int createShader(String shaderCode, int shaderType) throws GameException {
        GLES20 c = StateRegistry.currentGl();
        int shaderId = c.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new GameException("Error creating shader. Type: " + shaderType);
        }

        c.glShaderSource(shaderId, shaderCode);
        c.glCompileShader(shaderId);

        int[] a = new int[1];
        c.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, a, 0);
        if (a[0] == 0) {
            throw new GameException("Error compiling Shader code: " + c.glGetShaderInfoLog(shaderId));
        }

        c.glAttachShader(this.programId, shaderId);

        return shaderId;
    }

    public void link() throws GameException {
        GLES20 c = StateRegistry.currentGl();
        c.glLinkProgram(this.programId);
        int[] a = new int[1];
        c.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, a, 0);
        if (a[0] == 0) {
            throw new GameException("Error linking Shader code: " + c.glGetProgramInfoLog(this.programId));
        }

        if (this.vertexShaderId != 0) {
            c.glDetachShader(this.programId, this.vertexShaderId);
        }
        if (this.fragmentShaderId != 0) {
            c.glDetachShader(this.programId, this.fragmentShaderId);
        }

        if (Debug.debug) {
            c.glValidateProgram(this.programId);
            c.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, a, 0);
            if (a[0] == 0) {
                this.launcher.logger().warnf("Warning validating Shader code: %s", c.glGetProgramInfoLog(this.programId));
            }
        }
    }

    @Override public void bind() {
        StateRegistry.currentGl().glUseProgram(this.programId);
    }

    @Override public void unbind() {
        StateRegistry.currentGl().glUseProgram(0);
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        this.unbind();
        if (this.programId != 0) {
            StateRegistry.currentGl().glDeleteProgram(this.programId);
        }
        for (Uniform uniform : uniformMap.values()) {
            uniform.cleanup();
        }
        return null;
    }
}
