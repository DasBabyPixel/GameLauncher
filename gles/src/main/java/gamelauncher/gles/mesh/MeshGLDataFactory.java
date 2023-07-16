/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.mesh;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.states.StateRegistry;
import java8.util.concurrent.CompletableFuture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface MeshGLDataFactory {

    MeshGLData createMeshGLData(float[] vertices, float[] texCoords, float[] normals, int[] indices, int renderType);

    MeshGLData createMeshGLData(float[] vertices, float[] texCoords, int[] indices, int renderType);

    class FactoryGL30 implements MeshGLDataFactory {
        private final GLES gles;

        public FactoryGL30(GLES gles) {
            this.gles = gles;
        }

        @Override public MeshGLData createMeshGLData(float[] vertices, float[] texCoords, float[] normals, int[] indices, int renderType) {
            return new MeshGLDataGL30(gles, vertices, texCoords, normals, indices, renderType);
        }

        @Override public MeshGLData createMeshGLData(float[] vertices, float[] texCoords, int[] indices, int renderType) {
            return new MeshGLDataGL30(gles, vertices, texCoords, indices, renderType);
        }
    }

    @SuppressWarnings("SameParameterValue")
    class MeshGLDataGL30 extends AbstractGameResource implements MeshGLData {
        private final int vao;
        private final int vertexCount;
        private final int vaoSize;
        private final int renderType;
        private final int[] vbos;
        private final GLES gles;

        public MeshGLDataGL30(GLES gles, float[] vertices, float[] texCoords, int[] indices, int renderType) {
            gles.launcher().profiler().begin("render", "init_mesh");
            this.gles = gles;
            this.vertexCount = indices.length;
            this.renderType = renderType;
            this.vaoSize = 2;
            this.vbos = new int[3];
            GLES30 gl = StateRegistry.currentGl();
            vao = gl.glGenVertexArrays();
            gl.glBindVertexArray(vao);
            intBuffer(gles, gl, GLES20.GL_ELEMENT_ARRAY_BUFFER, indices, 0);
            floatBuffer(gles, gl, GLES20.GL_ARRAY_BUFFER, vertices, 1);
            gl.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
            floatBuffer(gles, gl, GLES20.GL_ARRAY_BUFFER, texCoords, 2);
            gl.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);
            gl.glBindVertexArray(0);
            gles.launcher().profiler().end();
        }

        public MeshGLDataGL30(GLES gles, float[] vertices, float[] texCoords, float[] normals, int[] indices, int renderType) {
            this.gles = gles;
            this.vertexCount = indices.length;
            this.renderType = renderType;
            this.vaoSize = 2;
            this.vbos = new int[4];
            GLES30 gl = StateRegistry.currentGl();
            vao = gl.glGenVertexArrays();
            gl.glBindVertexArray(vao);
            intBuffer(gles, gl, GLES20.GL_ELEMENT_ARRAY_BUFFER, indices, 0);
            floatBuffer(gles, gl, GLES20.GL_ARRAY_BUFFER, vertices, 1);
            gl.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
            floatBuffer(gles, gl, GLES20.GL_ARRAY_BUFFER, texCoords, 2);
            gl.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);
            floatBuffer(gles, gl, GLES20.GL_ARRAY_BUFFER, normals, 3);
            gl.glVertexAttribPointer(2, 3, GLES20.GL_FLOAT, false, 0, 0);
            gl.glBindVertexArray(0);
        }

        @Override public void draw() {
            GLES30 gl = StateRegistry.currentGl();
//            gl.glDrawBuffers(1, new int[]{GLES30.GL_BACK}, 0);
            gles.launcher().profiler().check();
            gl.glBindVertexArray(vao);
            gles.launcher().profiler().check();
            for (int i = 0; i < vaoSize; i++) {
                gl.glEnableVertexAttribArray(i);
            }
            gl.glDrawElements(renderType, vertexCount, GLES20.GL_UNSIGNED_INT, 0);
            gles.launcher().profiler().check();
            for (int i = 0; i < vaoSize; i++) {
                gl.glDisableVertexAttribArray(i);
            }
            gl.glBindVertexArray(0);
            gles.launcher().profiler().check();
        }

        @Override protected CompletableFuture<Void> cleanup0() throws GameException {
            GLES30 gl = StateRegistry.currentGl();
            gl.glDeleteBuffers(vbos.length, vbos, 0);
            gl.glDeleteVertexArrays(1, new int[]{this.vao}, 0);
            return null;
        }

        private void intBuffer(GLES gles, GLES20 gl, int bindTarget, int[] data, int vbosIndex) {
            int vbo = vbos[vbosIndex] = gl.glGenBuffers();
            gl.glBindBuffer(bindTarget, vbo);
            IntBuffer buf = gles.memoryManagement().allocDirectInt(data.length);
            buf.put(data).flip();
            gl.glBufferData(bindTarget, buf, GLES20.GL_STATIC_DRAW);
            gles.memoryManagement().free(buf);
        }

        private void floatBuffer(GLES gles, GLES20 gl, int bindTarget, float[] data, int vbosIndex) {
            int vbo = vbos[vbosIndex] = gl.glGenBuffers();
            gl.glBindBuffer(bindTarget, vbo);
            FloatBuffer buf = gles.memoryManagement().allocDirectFloat(data.length);
            buf.put(data).flip();
            gl.glBufferData(bindTarget, buf, GLES20.GL_STATIC_DRAW);
            gles.memoryManagement().free(buf);
        }
    }
}
