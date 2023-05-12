package gamelauncher.gles.gl.redirect;

import gamelauncher.gles.gl.GLES30;

import java.nio.*;

public class RedirectGLES30 extends RedirectGLES20 implements GLES30 {
    private final GLES30 wrapper;

    public RedirectGLES30(GLES30 wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    @Override public void glReadBuffer(int mode) {
        wrapper.glReadBuffer(mode);
    }

    @Override public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        wrapper.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    @Override public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        wrapper.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        wrapper.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    @Override public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        wrapper.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        wrapper.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    @Override public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        wrapper.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        wrapper.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, Buffer data) {
        wrapper.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, data);
    }

    @Override public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset) {
        wrapper.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, offset);
    }

    @Override public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {
        wrapper.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, data);
    }

    @Override public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset) {
        wrapper.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, offset);
    }

    @Override public void glGenQueries(int n, int[] ids, int offset) {
        wrapper.glGenQueries(n, ids, offset);
    }

    @Override public void glGenQueries(int n, IntBuffer ids) {
        wrapper.glGenQueries(n, ids);
    }

    @Override public void glDeleteQueries(int n, int[] ids, int offset) {
        wrapper.glDeleteQueries(n, ids, offset);
    }

    @Override public void glDeleteQueries(int n, IntBuffer ids) {
        wrapper.glDeleteQueries(n, ids);
    }

    @Override public boolean glIsQuery(int id) {
        return wrapper.glIsQuery(id);
    }

    @Override public void glBeginQuery(int target, int id) {
        wrapper.glBeginQuery(target, id);
    }

    @Override public void glEndQuery(int target) {
        wrapper.glEndQuery(target);
    }

    @Override public void glGetQueryiv(int target, int pname, int[] params, int offset) {
        wrapper.glGetQueryiv(target, pname, params, offset);
    }

    @Override public void glGetQueryiv(int target, int pname, IntBuffer params) {
        wrapper.glGetQueryiv(target, pname, params);
    }

    @Override public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
        wrapper.glGetQueryObjectuiv(id, pname, params, offset);
    }

    @Override public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        wrapper.glGetQueryObjectuiv(id, pname, params);
    }

    @Override public boolean glUnmapBuffer(int target) {
        return wrapper.glUnmapBuffer(target);
    }

    @Override public Buffer glGetBufferPointerv(int target, int pname) {
        return wrapper.glGetBufferPointerv(target, pname);
    }

    @Override public void glDrawBuffers(int n, int[] bufs, int offset) {
        wrapper.glDrawBuffers(n, bufs, offset);
    }

    @Override public void glDrawBuffers(int n, IntBuffer bufs) {
        wrapper.glDrawBuffers(n, bufs);
    }

    @Override public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix2x3fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix2x3fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix3x2fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix3x2fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix2x4fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix2x4fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix4x2fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix4x2fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix3x4fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix3x4fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix4x3fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix4x3fv(location, count, transpose, value);
    }

    @Override public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        wrapper.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        wrapper.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        wrapper.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return wrapper.glMapBufferRange(target, offset, length, access);
    }

    @Override public void glFlushMappedBufferRange(int target, int offset, int length) {
        wrapper.glFlushMappedBufferRange(target, offset, length);
    }

    @Override public void glBindVertexArray(int array) {
        wrapper.glBindVertexArray(array);
    }

    @Override public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        wrapper.glDeleteVertexArrays(n, arrays, offset);
    }

    @Override public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        wrapper.glDeleteVertexArrays(n, arrays);
    }

    @Override public void glGenVertexArrays(int n, int[] arrays, int offset) {
        wrapper.glGenVertexArrays(n, arrays, offset);
    }

    @Override public void glGenVertexArrays(int n, IntBuffer arrays) {
        wrapper.glGenVertexArrays(n, arrays);
    }

    @Override public boolean glIsVertexArray(int array) {
        return wrapper.glIsVertexArray(array);
    }

    @Override public void glGetIntegeri_v(int target, int index, int[] data, int offset) {
        wrapper.glGetIntegeri_v(target, index, data, offset);
    }

    @Override public void glGetIntegeri_v(int target, int index, IntBuffer data) {
        wrapper.glGetIntegeri_v(target, index, data);
    }

    @Override public void glBeginTransformFeedback(int primitiveMode) {
        wrapper.glBeginTransformFeedback(primitiveMode);
    }

    @Override public void glEndTransformFeedback() {
        wrapper.glEndTransformFeedback();
    }

    @Override public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        wrapper.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override public void glBindBufferBase(int target, int index, int buffer) {
        wrapper.glBindBufferBase(target, index, buffer);
    }

    @Override public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        wrapper.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override public void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override @Deprecated public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, byte name) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
    }

    @Override public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
    }

    @Override public String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        return wrapper.glGetTransformFeedbackVarying(program, index, size, sizeOffset, type, typeOffset);
    }

    @Override public String glGetTransformFeedbackVarying(int program, int index, IntBuffer size, IntBuffer type) {
        return wrapper.glGetTransformFeedbackVarying(program, index, size, type);
    }

    @Override public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {
        wrapper.glVertexAttribIPointer(index, size, type, stride, pointer);
    }

    @Override public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        wrapper.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override public void glGetVertexAttribIiv(int index, int pname, int[] params, int offset) {
        wrapper.glGetVertexAttribIiv(index, pname, params, offset);
    }

    @Override public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        wrapper.glGetVertexAttribIiv(index, pname, params);
    }

    @Override public void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset) {
        wrapper.glGetVertexAttribIuiv(index, pname, params, offset);
    }

    @Override public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        wrapper.glGetVertexAttribIuiv(index, pname, params);
    }

    @Override public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        wrapper.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        wrapper.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override public void glVertexAttribI4iv(int index, int[] v, int offset) {
        wrapper.glVertexAttribI4iv(index, v, offset);
    }

    @Override public void glVertexAttribI4iv(int index, IntBuffer v) {
        wrapper.glVertexAttribI4iv(index, v);
    }

    @Override public void glVertexAttribI4uiv(int index, int[] v, int offset) {
        wrapper.glVertexAttribI4uiv(index, v, offset);
    }

    @Override public void glVertexAttribI4uiv(int index, IntBuffer v) {
        wrapper.glVertexAttribI4uiv(index, v);
    }

    @Override public void glGetUniformuiv(int program, int location, int[] params, int offset) {
        wrapper.glGetUniformuiv(program, location, params, offset);
    }

    @Override public void glGetUniformuiv(int program, int location, IntBuffer params) {
        wrapper.glGetUniformuiv(program, location, params);
    }

    @Override public int glGetFragDataLocation(int program, String name) {
        return wrapper.glGetFragDataLocation(program, name);
    }

    @Override public void glUniform1ui(int location, int v0) {
        wrapper.glUniform1ui(location, v0);
    }

    @Override public void glUniform2ui(int location, int v0, int v1) {
        wrapper.glUniform2ui(location, v0, v1);
    }

    @Override public void glUniform3ui(int location, int v0, int v1, int v2) {
        wrapper.glUniform3ui(location, v0, v1, v2);
    }

    @Override public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {
        wrapper.glUniform4ui(location, v0, v1, v2, v3);
    }

    @Override public void glUniform1uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform1uiv(location, count, value, offset);
    }

    @Override public void glUniform1uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform1uiv(location, count, value);
    }

    @Override public void glUniform2uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform2uiv(location, count, value, offset);
    }

    @Override public void glUniform2uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform2uiv(location, count, value);
    }

    @Override public void glUniform3uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform3uiv(location, count, value, offset);
    }

    @Override public void glUniform3uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform3uiv(location, count, value);
    }

    @Override public void glUniform4uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform4uiv(location, count, value, offset);
    }

    @Override public void glUniform4uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform4uiv(location, count, value);
    }

    @Override public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
        wrapper.glClearBufferiv(buffer, drawbuffer, value, offset);
    }

    @Override public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        wrapper.glClearBufferiv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
        wrapper.glClearBufferuiv(buffer, drawbuffer, value, offset);
    }

    @Override public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        wrapper.glClearBufferuiv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
        wrapper.glClearBufferfv(buffer, drawbuffer, value, offset);
    }

    @Override public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        wrapper.glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        wrapper.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override public String glGetStringi(int name, int index) {
        return wrapper.glGetStringi(name, index);
    }

    @Override public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        wrapper.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override public void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
        wrapper.glGetUniformIndices(program, uniformNames, uniformIndices, uniformIndicesOffset);
    }

    @Override public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        wrapper.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

    @Override public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset) {
        wrapper.glGetActiveUniformsiv(program, uniformCount, uniformIndices, uniformIndicesOffset, pname, params, paramsOffset);
    }

    @Override public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        wrapper.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    @Override public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return wrapper.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
        wrapper.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params, offset);
    }

    @Override public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        wrapper.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

    @Override public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset) {
        wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, lengthOffset, uniformBlockName, uniformBlockNameOffset);
    }

    @Override public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
    }

    @Override public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    @Override public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        wrapper.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        wrapper.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices, int instanceCount) {
        wrapper.glDrawElementsInstanced(mode, count, type, indices, instanceCount);
    }

    @Override public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        wrapper.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    @Override public long glFenceSync(int condition, int flags) {
        return wrapper.glFenceSync(condition, flags);
    }

    @Override public boolean glIsSync(long sync) {
        return wrapper.glIsSync(sync);
    }

    @Override public void glDeleteSync(long sync) {
        wrapper.glDeleteSync(sync);
    }

    @Override public int glClientWaitSync(long sync, int flags, long timeout) {
        return wrapper.glClientWaitSync(sync, flags, timeout);
    }

    @Override public void glWaitSync(long sync, int flags, long timeout) {
        wrapper.glWaitSync(sync, flags, timeout);
    }

    @Override public void glGetInteger64v(int pname, long[] params, int offset) {
        wrapper.glGetInteger64v(pname, params, offset);
    }

    @Override public void glGetInteger64v(int pname, LongBuffer params) {
        wrapper.glGetInteger64v(pname, params);
    }

    @Override public void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
        wrapper.glGetSynciv(sync, pname, bufSize, length, lengthOffset, values, valuesOffset);
    }

    @Override public void glGetSynciv(long sync, int pname, int bufSize, IntBuffer length, IntBuffer values) {
        wrapper.glGetSynciv(sync, pname, bufSize, length, values);
    }

    @Override public void glGetInteger64i_v(int target, int index, long[] data, int offset) {
        wrapper.glGetInteger64i_v(target, index, data, offset);
    }

    @Override public void glGetInteger64i_v(int target, int index, LongBuffer data) {
        wrapper.glGetInteger64i_v(target, index, data);
    }

    @Override public void glGetBufferParameteri64v(int target, int pname, long[] params, int offset) {
        wrapper.glGetBufferParameteri64v(target, pname, params, offset);
    }

    @Override public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        wrapper.glGetBufferParameteri64v(target, pname, params);
    }

    @Override public void glGenSamplers(int count, int[] samplers, int offset) {
        wrapper.glGenSamplers(count, samplers, offset);
    }

    @Override public void glGenSamplers(int count, IntBuffer samplers) {
        wrapper.glGenSamplers(count, samplers);
    }

    @Override public void glDeleteSamplers(int count, int[] samplers, int offset) {
        wrapper.glDeleteSamplers(count, samplers, offset);
    }

    @Override public void glDeleteSamplers(int count, IntBuffer samplers) {
        wrapper.glDeleteSamplers(count, samplers);
    }

    @Override public boolean glIsSampler(int sampler) {
        return wrapper.glIsSampler(sampler);
    }

    @Override public void glBindSampler(int unit, int sampler) {
        wrapper.glBindSampler(unit, sampler);
    }

    @Override public void glSamplerParameteri(int sampler, int pname, int param) {
        wrapper.glSamplerParameteri(sampler, pname, param);
    }

    @Override public void glSamplerParameteriv(int sampler, int pname, int[] param, int offset) {
        wrapper.glSamplerParameteriv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        wrapper.glSamplerParameteriv(sampler, pname, param);
    }

    @Override public void glSamplerParameterf(int sampler, int pname, float param) {
        wrapper.glSamplerParameterf(sampler, pname, param);
    }

    @Override public void glSamplerParameterfv(int sampler, int pname, float[] param, int offset) {
        wrapper.glSamplerParameterfv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        wrapper.glSamplerParameterfv(sampler, pname, param);
    }

    @Override public void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
        wrapper.glGetSamplerParameteriv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        wrapper.glGetSamplerParameteriv(sampler, pname, params);
    }

    @Override public void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
        wrapper.glGetSamplerParameterfv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        wrapper.glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override public void glVertexAttribDivisor(int index, int divisor) {
        wrapper.glVertexAttribDivisor(index, divisor);
    }

    @Override public void glBindTransformFeedback(int target, int id) {
        wrapper.glBindTransformFeedback(target, id);
    }

    @Override public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        wrapper.glDeleteTransformFeedbacks(n, ids, offset);
    }

    @Override public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        wrapper.glDeleteTransformFeedbacks(n, ids);
    }

    @Override public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        wrapper.glGenTransformFeedbacks(n, ids, offset);
    }

    @Override public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        wrapper.glGenTransformFeedbacks(n, ids);
    }

    @Override public boolean glIsTransformFeedback(int id) {
        return wrapper.glIsTransformFeedback(id);
    }

    @Override public void glPauseTransformFeedback() {
        wrapper.glPauseTransformFeedback();
    }

    @Override public void glResumeTransformFeedback() {
        wrapper.glResumeTransformFeedback();
    }

    @Override public void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, Buffer binary) {
        wrapper.glGetProgramBinary(program, bufSize, length, lengthOffset, binaryFormat, binaryFormatOffset, binary);
    }

    @Override public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
        wrapper.glGetProgramBinary(program, bufSize, length, binaryFormat, binary);
    }

    @Override public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
        wrapper.glProgramBinary(program, binaryFormat, binary, length);
    }

    @Override public void glProgramParameteri(int program, int pname, int value) {
        wrapper.glProgramParameteri(program, pname, value);
    }

    @Override public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
        wrapper.glInvalidateFramebuffer(target, numAttachments, attachments, offset);
    }

    @Override public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        wrapper.glInvalidateFramebuffer(target, numAttachments, attachments);
    }

    @Override public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height) {
        wrapper.glInvalidateSubFramebuffer(target, numAttachments, attachments, offset, x, y, width, height);
    }

    @Override public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        wrapper.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
    }

    @Override public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
        wrapper.glTexStorage2D(target, levels, internalformat, width, height);
    }

    @Override public void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
        wrapper.glTexStorage3D(target, levels, internalformat, width, height, depth);
    }

    @Override public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
        wrapper.glGetInternalformativ(target, internalformat, pname, bufSize, params, offset);
    }

    @Override public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, IntBuffer params) {
        wrapper.glGetInternalformativ(target, internalformat, pname, bufSize, params);
    }

    @Override public void glReadPixels(int x, int y, int width, int height, int format, int type, int offset) {
        wrapper.glReadPixels(x, y, width, height, format, type, offset);
    }
}
