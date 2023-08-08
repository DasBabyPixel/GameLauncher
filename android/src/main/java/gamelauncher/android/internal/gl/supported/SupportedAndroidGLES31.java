/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.gl.supported;

import android.opengl.GLES31;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SupportedAndroidGLES31 extends SupportedAndroidGLES30 implements gamelauncher.gles.gl.GLES31 {

    @Override public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        GLES31.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
    }

    @Override public void glDispatchComputeIndirect(long indirect) {
        GLES31.glDispatchComputeIndirect(indirect);
    }

    @Override public void glDrawArraysIndirect(int mode, long indirect) {
        GLES31.glDrawArraysIndirect(mode, indirect);
    }

    @Override public void glDrawElementsIndirect(int mode, int type, long indirect) {
        GLES31.glDrawElementsIndirect(mode, type, indirect);
    }

    @Override public void glFramebufferParameteri(int target, int pname, int param) {
        GLES31.glFramebufferParameteri(target, pname, param);
    }

    @Override public void glGetFramebufferParameteriv(int target, int pname, int[] params, int offset) {
        GLES31.glGetFramebufferParameteriv(target, pname, params, offset);
    }

    @Override public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        GLES31.glGetFramebufferParameteriv(target, pname, params);
    }

    @Override public void glGetProgramInterfaceiv(int program, int programInterface, int pname, int[] params, int offset) {
        GLES31.glGetProgramInterfaceiv(program, programInterface, pname, params, offset);
    }

    @Override public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        GLES31.glGetProgramInterfaceiv(program, programInterface, pname, params);
    }

    @Override public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        return GLES31.glGetProgramResourceIndex(program, programInterface, name);
    }

    @Override public String glGetProgramResourceName(int program, int programInterface, int index) {
        return GLES31.glGetProgramResourceName(program, programInterface, index);
    }

    @Override public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, int[] props, int propsOffset, int bufSize, int[] length, int lengthOffset, int[] params, int paramsOffset) {
        GLES31.glGetProgramResourceiv(program, programInterface, index, propCount, props, propsOffset, bufSize, length, lengthOffset, params, paramsOffset);
    }

    @Override public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, IntBuffer props, int bufSize, IntBuffer length, IntBuffer params) {
        GLES31.glGetProgramResourceiv(program, programInterface, index, propCount, props, bufSize, length, params);
    }

    @Override public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        return GLES31.glGetProgramResourceLocation(program, programInterface, name);
    }

    @Override public void glUseProgramStages(int pipeline, int stages, int program) {
        GLES31.glUseProgramStages(pipeline, stages, program);
    }

    @Override public void glActiveShaderProgram(int pipeline, int program) {
        GLES31.glActiveShaderProgram(pipeline, program);
    }

    @Override public int glCreateShaderProgramv(int type, String[] strings) {
        return GLES31.glCreateShaderProgramv(type, strings);
    }

    @Override public void glBindProgramPipeline(int pipeline) {
        GLES31.glBindProgramPipeline(pipeline);
    }

    @Override public void glDeleteProgramPipelines(int n, int[] pipelines, int offset) {
        GLES31.glDeleteProgramPipelines(n, pipelines, offset);
    }

    @Override public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {
        GLES31.glDeleteProgramPipelines(n, pipelines);
    }

    @Override public void glGenProgramPipelines(int n, int[] pipelines, int offset) {
        GLES31.glGenProgramPipelines(n, pipelines, offset);
    }

    @Override public void glGenProgramPipelines(int n, IntBuffer pipelines) {
        GLES31.glGenProgramPipelines(n, pipelines);
    }

    @Override public boolean glIsProgramPipeline(int pipeline) {
        return GLES31.glIsProgramPipeline(pipeline);
    }

    @Override public void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int offset) {
        GLES31.glGetProgramPipelineiv(pipeline, pname, params, offset);
    }

    @Override public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        GLES31.glGetProgramPipelineiv(pipeline, pname, params);
    }

    @Override public void glProgramUniform1i(int program, int location, int v0) {
        GLES31.glProgramUniform1i(program, location, v0);
    }

    @Override public void glProgramUniform2i(int program, int location, int v0, int v1) {
        GLES31.glProgramUniform2i(program, location, v0, v1);
    }

    @Override public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        GLES31.glProgramUniform3i(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        GLES31.glProgramUniform4i(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1ui(int program, int location, int v0) {
        GLES31.glProgramUniform1ui(program, location, v0);
    }

    @Override public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        GLES31.glProgramUniform2ui(program, location, v0, v1);
    }

    @Override public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        GLES31.glProgramUniform3ui(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        GLES31.glProgramUniform4ui(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1f(int program, int location, float v0) {
        GLES31.glProgramUniform1f(program, location, v0);
    }

    @Override public void glProgramUniform2f(int program, int location, float v0, float v1) {
        GLES31.glProgramUniform2f(program, location, v0, v1);
    }

    @Override public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        GLES31.glProgramUniform3f(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        GLES31.glProgramUniform4f(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1iv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform1iv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform1iv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform1iv(program, location, count, value);
    }

    @Override public void glProgramUniform2iv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform2iv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform2iv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform2iv(program, location, count, value);
    }

    @Override public void glProgramUniform3iv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform3iv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform3iv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform3iv(program, location, count, value);
    }

    @Override public void glProgramUniform4iv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform4iv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform4iv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform4iv(program, location, count, value);
    }

    @Override public void glProgramUniform1uiv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform1uiv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform1uiv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform1uiv(program, location, count, value);
    }

    @Override public void glProgramUniform2uiv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform2uiv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform2uiv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform2uiv(program, location, count, value);
    }

    @Override public void glProgramUniform3uiv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform3uiv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform3uiv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform3uiv(program, location, count, value);
    }

    @Override public void glProgramUniform4uiv(int program, int location, int count, int[] value, int offset) {
        GLES31.glProgramUniform4uiv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform4uiv(int program, int location, int count, IntBuffer value) {
        GLES31.glProgramUniform4uiv(program, location, count, value);
    }

    @Override public void glProgramUniform1fv(int program, int location, int count, float[] value, int offset) {
        GLES31.glProgramUniform1fv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform1fv(int program, int location, int count, FloatBuffer value) {
        GLES31.glProgramUniform1fv(program, location, count, value);
    }

    @Override public void glProgramUniform2fv(int program, int location, int count, float[] value, int offset) {
        GLES31.glProgramUniform2fv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform2fv(int program, int location, int count, FloatBuffer value) {
        GLES31.glProgramUniform2fv(program, location, count, value);
    }

    @Override public void glProgramUniform3fv(int program, int location, int count, float[] value, int offset) {
        GLES31.glProgramUniform3fv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform3fv(int program, int location, int count, FloatBuffer value) {
        GLES31.glProgramUniform3fv(program, location, count, value);
    }

    @Override public void glProgramUniform4fv(int program, int location, int count, float[] value, int offset) {
        GLES31.glProgramUniform4fv(program, location, count, value, offset);
    }

    @Override public void glProgramUniform4fv(int program, int location, int count, FloatBuffer value) {
        GLES31.glProgramUniform4fv(program, location, count, value);
    }

    @Override public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix2fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix2fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix3fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix3fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix4fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix4fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix2x3fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix2x3fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix3x2fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix3x2fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix2x4fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix2x4fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix4x2fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix4x2fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix3x4fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix3x4fv(program, location, count, transpose, value);
    }

    @Override public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        GLES31.glProgramUniformMatrix4x3fv(program, location, count, transpose, value, offset);
    }

    @Override public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        GLES31.glProgramUniformMatrix4x3fv(program, location, count, transpose, value);
    }

    @Override public void glValidateProgramPipeline(int pipeline) {
        GLES31.glValidateProgramPipeline(pipeline);
    }

    @Override public String glGetProgramPipelineInfoLog(int program) {
        return GLES31.glGetProgramPipelineInfoLog(program);
    }

    @Override public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        GLES31.glBindImageTexture(unit, texture, level, layered, layer, access, format);
    }

    @Override public void glGetBooleani_v(int target, int index, boolean[] data, int offset) {
        GLES31.glGetBooleani_v(target, index, data, offset);
    }

    @Override public void glGetBooleani_v(int target, int index, IntBuffer data) {
        GLES31.glGetBooleani_v(target, index, data);
    }

    @Override public void glMemoryBarrier(int barriers) {
        GLES31.glMemoryBarrier(barriers);
    }

    @Override public void glMemoryBarrierByRegion(int barriers) {
        GLES31.glMemoryBarrierByRegion(barriers);
    }

    @Override public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        GLES31.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
    }

    @Override public void glGetMultisamplefv(int pname, int index, float[] val, int offset) {
        GLES31.glGetMultisamplefv(pname, index, val, offset);
    }

    @Override public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        GLES31.glGetMultisamplefv(pname, index, val);
    }

    @Override public void glSampleMaski(int maskNumber, int mask) {
        GLES31.glSampleMaski(maskNumber, mask);
    }

    @Override public void glGetTexLevelParameteriv(int target, int level, int pname, int[] params, int offset) {
        GLES31.glGetTexLevelParameteriv(target, level, pname, params, offset);
    }

    @Override public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        GLES31.glGetTexLevelParameteriv(target, level, pname, params);
    }

    @Override public void glGetTexLevelParameterfv(int target, int level, int pname, float[] params, int offset) {
        GLES31.glGetTexLevelParameterfv(target, level, pname, params, offset);
    }

    @Override public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        GLES31.glGetTexLevelParameterfv(target, level, pname, params);
    }

    @Override public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        GLES31.glBindVertexBuffer(bindingindex, buffer, offset, stride);
    }

    @Override public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        GLES31.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
    }

    @Override public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        GLES31.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
    }

    @Override public void glVertexAttribBinding(int attribindex, int bindingindex) {
        GLES31.glVertexAttribBinding(attribindex, bindingindex);
    }

    @Override public void glVertexBindingDivisor(int bindingindex, int divisor) {
        GLES31.glVertexBindingDivisor(bindingindex, divisor);
    }
}
