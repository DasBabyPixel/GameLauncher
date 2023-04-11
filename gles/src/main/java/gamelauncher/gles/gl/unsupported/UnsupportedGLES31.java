package gamelauncher.gles.gl.unsupported;

import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.gl.redirect.RedirectGLES30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UnsupportedGLES31 extends RedirectGLES30 implements GLES31 {

    public UnsupportedGLES31(GLES30 wrapper) {
        super(wrapper);
    }

    @Override
    public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDispatchComputeIndirect(long indirect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawArraysIndirect(int mode, long indirect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsIndirect(int mode, int type, long indirect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glFramebufferParameteri(int target, int pname, int param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetFramebufferParameteriv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetProgramResourceName(int program, int programInterface, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, int[] props, int propsOffset, int bufSize, int[] length, int lengthOffset, int[] params, int paramsOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, IntBuffer props, int bufSize, IntBuffer length, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUseProgramStages(int pipeline, int stages, int program) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glActiveShaderProgram(int pipeline, int program) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glCreateShaderProgramv(int type, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindProgramPipeline(int pipeline) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteProgramPipelines(int n, int[] pipelines, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenProgramPipelines(int n, int[] pipelines, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenProgramPipelines(int n, IntBuffer pipelines) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsProgramPipeline(int pipeline) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1i(int program, int location, int v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2i(int program, int location, int v0, int v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1ui(int program, int location, int v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1f(int program, int location, float v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2f(int program, int location, float v0, float v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1iv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1iv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2iv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2iv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3iv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3iv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4iv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4iv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1uiv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1uiv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2uiv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2uiv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3uiv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3uiv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4uiv(int program, int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4uiv(int program, int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1fv(int program, int location, int count, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform1fv(int program, int location, int count, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2fv(int program, int location, int count, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform2fv(int program, int location, int count, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3fv(int program, int location, int count, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform3fv(int program, int location, int count, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4fv(int program, int location, int count, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniform4fv(int program, int location, int count, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glValidateProgramPipeline(int pipeline) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetProgramPipelineInfoLog(int program) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetBooleani_v(int target, int index, boolean[] data, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetBooleani_v(int target, int index, IntBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glMemoryBarrier(int barriers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glMemoryBarrierByRegion(int barriers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetMultisamplefv(int pname, int index, float[] val, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSampleMaski(int maskNumber, int mask) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexLevelParameteriv(int target, int level, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexLevelParameterfv(int target, int level, int pname, float[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribBinding(int attribindex, int bindingindex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexBindingDivisor(int bindingindex, int divisor) {
        throw new UnsupportedOperationException();
    }
}
