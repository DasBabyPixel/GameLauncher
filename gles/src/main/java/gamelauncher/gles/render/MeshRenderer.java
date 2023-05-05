/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.render;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.states.StateRegistry;

public class MeshRenderer {

    private final GLES gles;

    public MeshRenderer(GLES gles) {
        this.gles = gles;
    }

    public void render(ShaderProgram program, Mesh... meshes) {
        for (Mesh mesh : meshes) {
            renderMesh(mesh, program);
        }
    }

    private void renderMesh(Mesh mesh, ShaderProgram program) { // Rather inefficient render, completely setup everything for every single mesh
        GLES20 gl = StateRegistry.currentGl();
        Mesh.Material mat = mesh.material();
        if (mat.texture != null) {
            gl.glActiveTexture(GLES20.GL_TEXTURE0);
            gl.glBindTexture(GLES20.GL_TEXTURE_2D, mat.texture.getTextureId());
            program.uTexture.set(0);
            program.uHasTexture.set(1);
        } else {
            program.uTexture.set(0);
            program.uHasTexture.set(0);
        }
        program.uApplyLighting.set(mesh.applyLighting());
        program.uMaterial.set(mesh.material());
        program.uploadUniforms();
        program.bind();
        mesh.glData().draw();
        program.unbind();
    }
}
