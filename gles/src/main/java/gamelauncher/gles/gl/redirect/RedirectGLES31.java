package gamelauncher.gles.gl.redirect;

import gamelauncher.gles.gl.GLES31;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RedirectGLES31 extends RedirectGLES30 {

    private final GLES31 wrapper;

    public RedirectGLES31(GLES31 wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        wrapper.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
    }

    public void glDispatchComputeIndirect(long indirect) {
        wrapper.glDispatchComputeIndirect(indirect);
    }

    public void glDrawArraysIndirect(int mode, long indirect) {
        wrapper.glDrawArraysIndirect(mode, indirect);
    }

    public void glDrawElementsIndirect(int mode, int type, long indirect) {
        wrapper.glDrawElementsIndirect(mode, type, indirect);
    }

    public void glFramebufferParameteri(int target, int pname, int param) {
        wrapper.glFramebufferParameteri(target, pname, param);
    }

    public void glGetFramebufferParameteriv(int target, int pname, int[] params, int offset) {
        wrapper.glGetFramebufferParameteriv(target, pname, params, offset);
    }

    public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        wrapper.glGetFramebufferParameteriv(target, pname, params);
    }

    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, int[] params, int offset) {
        wrapper.glGetProgramInterfaceiv(program, programInterface, pname, params, offset);
    }

    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        wrapper.glGetProgramInterfaceiv(program, programInterface, pname, params);
    }

    public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        return wrapper.glGetProgramResourceIndex(program, programInterface, name);
    }

    public String glGetProgramResourceName(int program, int programInterface, int index) {
        return wrapper.glGetProgramResourceName(program, programInterface, index);
    }

    public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, int[] props, int propsOffset, int bufSize, int[] length, int lengthOffset, int[] params, int paramsOffset) {
        wrapper.glGetProgramResourceiv(program, programInterface, index, propCount, props, propsOffset, bufSize, length, lengthOffset, params, paramsOffset);
    }

    public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, IntBuffer props, int bufSize, IntBuffer length, IntBuffer params) {
        wrapper.glGetProgramResourceiv(program, programInterface, index, propCount, props, bufSize, length, params);
    }

    public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        return wrapper.glGetProgramResourceLocation(program, programInterface, name);
    }

    public void glUseProgramStages(int pipeline, int stages, int program) {
        wrapper.glUseProgramStages(pipeline, stages, program);
    }

    public void glActiveShaderProgram(int pipeline, int program) {
        wrapper.glActiveShaderProgram(pipeline, program);
    }

    public int glCreateShaderProgramv(int type, String[] strings) {
        return wrapper.glCreateShaderProgramv(type, strings);
    }

    public void glBindProgramPipeline(int pipeline) {
        wrapper.glBindProgramPipeline(pipeline);
    }

    public void glDeleteProgramPipelines(int n, int[] pipelines, int offset) {
        wrapper.glDeleteProgramPipelines(n, pipelines, offset);
    }

    public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {
        wrapper.glDeleteProgramPipelines(n, pipelines);
    }

    public void glGenProgramPipelines(int n, int[] pipelines, int offset) {
        wrapper.glGenProgramPipelines(n, pipelines, offset);
    }

    public void glGenProgramPipelines(int n, IntBuffer pipelines) {
        wrapper.glGenProgramPipelines(n, pipelines);
    }

    public boolean glIsProgramPipeline(int pipeline) {
        return wrapper.glIsProgramPipeline(pipeline);
    }

    public void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int offset) {
        wrapper.glGetProgramPipelineiv(pipeline, pname, params, offset);
    }

    public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        wrapper.glGetProgramPipelineiv(pipeline, pname, params);
    }

    public void glProgramUniform1i(int program, int location, int v0) {
        wrapper.glProgramUniform1i(program, location, v0);
    }

    public void glProgramUniform2i(int program, int location, int v0, int v1) {
        wrapper.glProgramUniform2i(program, location, v0, v1);
    }

    public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        wrapper.glProgramUniform3i(program, location, v0, v1, v2);
    }

    public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        wrapper.glProgramUniform4i(program, location, v0, v1, v2, v3);
    }

    public void glProgramUniform1ui(int program, int location, int v0) {
        wrapper.glProgramUniform1ui(program, location, v0);
    }

    public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        wrapper.glProgramUniform2ui(program, location, v0, v1);
    }

    public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        wrapper.glProgramUniform3ui(program, location, v0, v1, v2);
    }

    public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        wrapper.glProgramUniform4ui(program, location, v0, v1, v2, v3);
    }

    public void glProgramUniform1f(int program, int location, float v0) {
        wrapper.glProgramUniform1f(program, location, v0);
    }

    public void glProgramUniform2f(int program, int location, float v0, float v1) {
        wrapper.glProgramUniform2f(program, location, v0, v1);
    }

    public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        wrapper.glProgramUniform3f(program, location, v0, v1, v2);
    }

    public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        wrapper.glProgramUniform4f(program, location, v0, v1, v2, v3);
    }

    public void glProgramUniform1iv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform1iv(program, location, count, value, offset);
    }

    public void glProgramUniform1iv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform1iv(program, location, count, value);
    }

    public void glProgramUniform2iv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform2iv(program, location, count, value, offset);
    }

    public void glProgramUniform2iv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform2iv(program, location, count, value);
    }

    public void glProgramUniform3iv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform3iv(program, location, count, value, offset);
    }

    public void glProgramUniform3iv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform3iv(program, location, count, value);
    }

    public void glProgramUniform4iv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform4iv(program, location, count, value, offset);
    }

    public void glProgramUniform4iv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform4iv(program, location, count, value);
    }

    public void glProgramUniform1uiv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform1uiv(program, location, count, value, offset);
    }

    public void glProgramUniform1uiv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform1uiv(program, location, count, value);
    }

    public void glProgramUniform2uiv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform2uiv(program, location, count, value, offset);
    }

    public void glProgramUniform2uiv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform2uiv(program, location, count, value);
    }

    public void glProgramUniform3uiv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform3uiv(program, location, count, value, offset);
    }

    public void glProgramUniform3uiv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform3uiv(program, location, count, value);
    }

    public void glProgramUniform4uiv(int program, int location, int count, int[] value, int offset) {
        wrapper.glProgramUniform4uiv(program, location, count, value, offset);
    }

    public void glProgramUniform4uiv(int program, int location, int count, IntBuffer value) {
        wrapper.glProgramUniform4uiv(program, location, count, value);
    }

    public void glProgramUniform1fv(int program, int location, int count, float[] value, int offset) {
        wrapper.glProgramUniform1fv(program, location, count, value, offset);
    }

    public void glProgramUniform1fv(int program, int location, int count, FloatBuffer value) {
        wrapper.glProgramUniform1fv(program, location, count, value);
    }

    public void glProgramUniform2fv(int program, int location, int count, float[] value, int offset) {
        wrapper.glProgramUniform2fv(program, location, count, value, offset);
    }

    public void glProgramUniform2fv(int program, int location, int count, FloatBuffer value) {
        wrapper.glProgramUniform2fv(program, location, count, value);
    }

    public void glProgramUniform3fv(int program, int location, int count, float[] value, int offset) {
        wrapper.glProgramUniform3fv(program, location, count, value, offset);
    }

    public void glProgramUniform3fv(int program, int location, int count, FloatBuffer value) {
        wrapper.glProgramUniform3fv(program, location, count, value);
    }

    public void glProgramUniform4fv(int program, int location, int count, float[] value, int offset) {
        wrapper.glProgramUniform4fv(program, location, count, value, offset);
    }

    public void glProgramUniform4fv(int program, int location, int count, FloatBuffer value) {
        wrapper.glProgramUniform4fv(program, location, count, value);
    }

    public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix2fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix2fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix3fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix3fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix4fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix4fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix2x3fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix2x3fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix3x2fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix3x2fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix2x4fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix2x4fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix4x2fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix4x2fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix3x4fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix3x4fv(program, location, count, transpose, value);
    }

    public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glProgramUniformMatrix4x3fv(program, location, count, transpose, value, offset);
    }

    public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glProgramUniformMatrix4x3fv(program, location, count, transpose, value);
    }

    public void glValidateProgramPipeline(int pipeline) {
        wrapper.glValidateProgramPipeline(pipeline);
    }

    public String glGetProgramPipelineInfoLog(int program) {
        return wrapper.glGetProgramPipelineInfoLog(program);
    }

    public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        wrapper.glBindImageTexture(unit, texture, level, layered, layer, access, format);
    }

    public void glGetBooleani_v(int target, int index, boolean[] data, int offset) {
        wrapper.glGetBooleani_v(target, index, data, offset);
    }

    public void glGetBooleani_v(int target, int index, IntBuffer data) {
        wrapper.glGetBooleani_v(target, index, data);
    }

    public void glMemoryBarrier(int barriers) {
        wrapper.glMemoryBarrier(barriers);
    }

    public void glMemoryBarrierByRegion(int barriers) {
        wrapper.glMemoryBarrierByRegion(barriers);
    }

    public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        wrapper.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
    }

    public void glGetMultisamplefv(int pname, int index, float[] val, int offset) {
        wrapper.glGetMultisamplefv(pname, index, val, offset);
    }

    public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        wrapper.glGetMultisamplefv(pname, index, val);
    }

    public void glSampleMaski(int maskNumber, int mask) {
        wrapper.glSampleMaski(maskNumber, mask);
    }

    public void glGetTexLevelParameteriv(int target, int level, int pname, int[] params, int offset) {
        wrapper.glGetTexLevelParameteriv(target, level, pname, params, offset);
    }

    public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        wrapper.glGetTexLevelParameteriv(target, level, pname, params);
    }

    public void glGetTexLevelParameterfv(int target, int level, int pname, float[] params, int offset) {
        wrapper.glGetTexLevelParameterfv(target, level, pname, params, offset);
    }

    public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        wrapper.glGetTexLevelParameterfv(target, level, pname, params);
    }

    public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        wrapper.glBindVertexBuffer(bindingindex, buffer, offset, stride);
    }

    public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        wrapper.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
    }

    public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        wrapper.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
    }

    public void glVertexAttribBinding(int attribindex, int bindingindex) {
        wrapper.glVertexAttribBinding(attribindex, bindingindex);
    }

    public void glVertexBindingDivisor(int bindingindex, int divisor) {
        wrapper.glVertexBindingDivisor(bindingindex, divisor);
    }
}
