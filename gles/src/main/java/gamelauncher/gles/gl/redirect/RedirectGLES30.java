package gamelauncher.gles.gl.redirect;

import gamelauncher.gles.gl.GLES30;

import java.nio.*;

public class RedirectGLES30 extends RedirectGLES20 {
    private final GLES30 wrapper;

    public RedirectGLES30(GLES30 wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    public void glReadBuffer(int mode) {
        wrapper.glReadBuffer(mode);
    }

    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        wrapper.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        wrapper.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        wrapper.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        wrapper.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        wrapper.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        wrapper.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        wrapper.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, Buffer data) {
        wrapper.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, data);
    }

    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset) {
        wrapper.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, offset);
    }

    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {
        wrapper.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, data);
    }

    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset) {
        wrapper.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, offset);
    }

    public void glGenQueries(int n, int[] ids, int offset) {
        wrapper.glGenQueries(n, ids, offset);
    }

    public void glGenQueries(int n, IntBuffer ids) {
        wrapper.glGenQueries(n, ids);
    }

    public void glDeleteQueries(int n, int[] ids, int offset) {
        wrapper.glDeleteQueries(n, ids, offset);
    }

    public void glDeleteQueries(int n, IntBuffer ids) {
        wrapper.glDeleteQueries(n, ids);
    }

    public boolean glIsQuery(int id) {
        return wrapper.glIsQuery(id);
    }

    public void glBeginQuery(int target, int id) {
        wrapper.glBeginQuery(target, id);
    }

    public void glEndQuery(int target) {
        wrapper.glEndQuery(target);
    }

    public void glGetQueryiv(int target, int pname, int[] params, int offset) {
        wrapper.glGetQueryiv(target, pname, params, offset);
    }

    public void glGetQueryiv(int target, int pname, IntBuffer params) {
        wrapper.glGetQueryiv(target, pname, params);
    }

    public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
        wrapper.glGetQueryObjectuiv(id, pname, params, offset);
    }

    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        wrapper.glGetQueryObjectuiv(id, pname, params);
    }

    public boolean glUnmapBuffer(int target) {
        return wrapper.glUnmapBuffer(target);
    }

    public Buffer glGetBufferPointerv(int target, int pname) {
        return wrapper.glGetBufferPointerv(target, pname);
    }

    public void glDrawBuffers(int n, int[] bufs, int offset) {
        wrapper.glDrawBuffers(n, bufs, offset);
    }

    public void glDrawBuffers(int n, IntBuffer bufs) {
        wrapper.glDrawBuffers(n, bufs);
    }

    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix2x3fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix2x3fv(location, count, transpose, value);
    }

    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix3x2fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix3x2fv(location, count, transpose, value);
    }

    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix2x4fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix2x4fv(location, count, transpose, value);
    }

    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix4x2fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix4x2fv(location, count, transpose, value);
    }

    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix3x4fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix3x4fv(location, count, transpose, value);
    }

    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix4x3fv(location, count, transpose, value, offset);
    }

    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix4x3fv(location, count, transpose, value);
    }

    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        wrapper.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        wrapper.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        wrapper.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return wrapper.glMapBufferRange(target, offset, length, access);
    }

    public void glFlushMappedBufferRange(int target, int offset, int length) {
        wrapper.glFlushMappedBufferRange(target, offset, length);
    }

    public void glBindVertexArray(int array) {
        wrapper.glBindVertexArray(array);
    }

    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        wrapper.glDeleteVertexArrays(n, arrays, offset);
    }

    public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        wrapper.glDeleteVertexArrays(n, arrays);
    }

    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        wrapper.glGenVertexArrays(n, arrays, offset);
    }

    public void glGenVertexArrays(int n, IntBuffer arrays) {
        wrapper.glGenVertexArrays(n, arrays);
    }

    public boolean glIsVertexArray(int array) {
        return wrapper.glIsVertexArray(array);
    }

    public void glGetIntegeri_v(int target, int index, int[] data, int offset) {
        wrapper.glGetIntegeri_v(target, index, data, offset);
    }

    public void glGetIntegeri_v(int target, int index, IntBuffer data) {
        wrapper.glGetIntegeri_v(target, index, data);
    }

    public void glBeginTransformFeedback(int primitiveMode) {
        wrapper.glBeginTransformFeedback(primitiveMode);
    }

    public void glEndTransformFeedback() {
        wrapper.glEndTransformFeedback();
    }

    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        wrapper.glBindBufferRange(target, index, buffer, offset, size);
    }

    public void glBindBufferBase(int target, int index, int buffer) {
        wrapper.glBindBufferBase(target, index, buffer);
    }

    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        wrapper.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Deprecated
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, byte name) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
    }

    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
        wrapper.glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
    }

    public String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        return wrapper.glGetTransformFeedbackVarying(program, index, size, sizeOffset, type, typeOffset);
    }

    public String glGetTransformFeedbackVarying(int program, int index, IntBuffer size, IntBuffer type) {
        return wrapper.glGetTransformFeedbackVarying(program, index, size, type);
    }

    public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {
        wrapper.glVertexAttribIPointer(index, size, type, stride, pointer);
    }

    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        wrapper.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    public void glGetVertexAttribIiv(int index, int pname, int[] params, int offset) {
        wrapper.glGetVertexAttribIiv(index, pname, params, offset);
    }

    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        wrapper.glGetVertexAttribIiv(index, pname, params);
    }

    public void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset) {
        wrapper.glGetVertexAttribIuiv(index, pname, params, offset);
    }

    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        wrapper.glGetVertexAttribIuiv(index, pname, params);
    }

    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        wrapper.glVertexAttribI4i(index, x, y, z, w);
    }

    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        wrapper.glVertexAttribI4ui(index, x, y, z, w);
    }

    public void glVertexAttribI4iv(int index, int[] v, int offset) {
        wrapper.glVertexAttribI4iv(index, v, offset);
    }

    public void glVertexAttribI4iv(int index, IntBuffer v) {
        wrapper.glVertexAttribI4iv(index, v);
    }

    public void glVertexAttribI4uiv(int index, int[] v, int offset) {
        wrapper.glVertexAttribI4uiv(index, v, offset);
    }

    public void glVertexAttribI4uiv(int index, IntBuffer v) {
        wrapper.glVertexAttribI4uiv(index, v);
    }

    public void glGetUniformuiv(int program, int location, int[] params, int offset) {
        wrapper.glGetUniformuiv(program, location, params, offset);
    }

    public void glGetUniformuiv(int program, int location, IntBuffer params) {
        wrapper.glGetUniformuiv(program, location, params);
    }

    public int glGetFragDataLocation(int program, String name) {
        return wrapper.glGetFragDataLocation(program, name);
    }

    public void glUniform1ui(int location, int v0) {
        wrapper.glUniform1ui(location, v0);
    }

    public void glUniform2ui(int location, int v0, int v1) {
        wrapper.glUniform2ui(location, v0, v1);
    }

    public void glUniform3ui(int location, int v0, int v1, int v2) {
        wrapper.glUniform3ui(location, v0, v1, v2);
    }

    public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {
        wrapper.glUniform4ui(location, v0, v1, v2, v3);
    }

    public void glUniform1uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform1uiv(location, count, value, offset);
    }

    public void glUniform1uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform1uiv(location, count, value);
    }

    public void glUniform2uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform2uiv(location, count, value, offset);
    }

    public void glUniform2uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform2uiv(location, count, value);
    }

    public void glUniform3uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform3uiv(location, count, value, offset);
    }

    public void glUniform3uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform3uiv(location, count, value);
    }

    public void glUniform4uiv(int location, int count, int[] value, int offset) {
        wrapper.glUniform4uiv(location, count, value, offset);
    }

    public void glUniform4uiv(int location, int count, IntBuffer value) {
        wrapper.glUniform4uiv(location, count, value);
    }

    public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
        wrapper.glClearBufferiv(buffer, drawbuffer, value, offset);
    }

    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        wrapper.glClearBufferiv(buffer, drawbuffer, value);
    }

    public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
        wrapper.glClearBufferuiv(buffer, drawbuffer, value, offset);
    }

    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        wrapper.glClearBufferuiv(buffer, drawbuffer, value);
    }

    public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
        wrapper.glClearBufferfv(buffer, drawbuffer, value, offset);
    }

    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        wrapper.glClearBufferfv(buffer, drawbuffer, value);
    }

    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        wrapper.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    public String glGetStringi(int name, int index) {
        return wrapper.glGetStringi(name, index);
    }

    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        wrapper.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    public void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
        wrapper.glGetUniformIndices(program, uniformNames, uniformIndices, uniformIndicesOffset);
    }

    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        wrapper.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

    public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset) {
        wrapper.glGetActiveUniformsiv(program, uniformCount, uniformIndices, uniformIndicesOffset, pname, params, paramsOffset);
    }

    public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        wrapper.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return wrapper.glGetUniformBlockIndex(program, uniformBlockName);
    }

    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
        wrapper.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params, offset);
    }

    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        wrapper.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset) {
        wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, lengthOffset, uniformBlockName, uniformBlockNameOffset);
    }

    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
    }

    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return wrapper.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        wrapper.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        wrapper.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices, int instanceCount) {
        wrapper.glDrawElementsInstanced(mode, count, type, indices, instanceCount);
    }

    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        wrapper.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    public long glFenceSync(int condition, int flags) {
        return wrapper.glFenceSync(condition, flags);
    }

    public boolean glIsSync(long sync) {
        return wrapper.glIsSync(sync);
    }

    public void glDeleteSync(long sync) {
        wrapper.glDeleteSync(sync);
    }

    public int glClientWaitSync(long sync, int flags, long timeout) {
        return wrapper.glClientWaitSync(sync, flags, timeout);
    }

    public void glWaitSync(long sync, int flags, long timeout) {
        wrapper.glWaitSync(sync, flags, timeout);
    }

    public void glGetInteger64v(int pname, long[] params, int offset) {
        wrapper.glGetInteger64v(pname, params, offset);
    }

    public void glGetInteger64v(int pname, LongBuffer params) {
        wrapper.glGetInteger64v(pname, params);
    }

    public void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
        wrapper.glGetSynciv(sync, pname, bufSize, length, lengthOffset, values, valuesOffset);
    }

    public void glGetSynciv(long sync, int pname, int bufSize, IntBuffer length, IntBuffer values) {
        wrapper.glGetSynciv(sync, pname, bufSize, length, values);
    }

    public void glGetInteger64i_v(int target, int index, long[] data, int offset) {
        wrapper.glGetInteger64i_v(target, index, data, offset);
    }

    public void glGetInteger64i_v(int target, int index, LongBuffer data) {
        wrapper.glGetInteger64i_v(target, index, data);
    }

    public void glGetBufferParameteri64v(int target, int pname, long[] params, int offset) {
        wrapper.glGetBufferParameteri64v(target, pname, params, offset);
    }

    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        wrapper.glGetBufferParameteri64v(target, pname, params);
    }

    public void glGenSamplers(int count, int[] samplers, int offset) {
        wrapper.glGenSamplers(count, samplers, offset);
    }

    public void glGenSamplers(int count, IntBuffer samplers) {
        wrapper.glGenSamplers(count, samplers);
    }

    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        wrapper.glDeleteSamplers(count, samplers, offset);
    }

    public void glDeleteSamplers(int count, IntBuffer samplers) {
        wrapper.glDeleteSamplers(count, samplers);
    }

    public boolean glIsSampler(int sampler) {
        return wrapper.glIsSampler(sampler);
    }

    public void glBindSampler(int unit, int sampler) {
        wrapper.glBindSampler(unit, sampler);
    }

    public void glSamplerParameteri(int sampler, int pname, int param) {
        wrapper.glSamplerParameteri(sampler, pname, param);
    }

    public void glSamplerParameteriv(int sampler, int pname, int[] param, int offset) {
        wrapper.glSamplerParameteriv(sampler, pname, param, offset);
    }

    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        wrapper.glSamplerParameteriv(sampler, pname, param);
    }

    public void glSamplerParameterf(int sampler, int pname, float param) {
        wrapper.glSamplerParameterf(sampler, pname, param);
    }

    public void glSamplerParameterfv(int sampler, int pname, float[] param, int offset) {
        wrapper.glSamplerParameterfv(sampler, pname, param, offset);
    }

    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        wrapper.glSamplerParameterfv(sampler, pname, param);
    }

    public void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
        wrapper.glGetSamplerParameteriv(sampler, pname, params, offset);
    }

    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        wrapper.glGetSamplerParameteriv(sampler, pname, params);
    }

    public void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
        wrapper.glGetSamplerParameterfv(sampler, pname, params, offset);
    }

    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        wrapper.glGetSamplerParameterfv(sampler, pname, params);
    }

    public void glVertexAttribDivisor(int index, int divisor) {
        wrapper.glVertexAttribDivisor(index, divisor);
    }

    public void glBindTransformFeedback(int target, int id) {
        wrapper.glBindTransformFeedback(target, id);
    }

    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        wrapper.glDeleteTransformFeedbacks(n, ids, offset);
    }

    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        wrapper.glDeleteTransformFeedbacks(n, ids);
    }

    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        wrapper.glGenTransformFeedbacks(n, ids, offset);
    }

    public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        wrapper.glGenTransformFeedbacks(n, ids);
    }

    public boolean glIsTransformFeedback(int id) {
        return wrapper.glIsTransformFeedback(id);
    }

    public void glPauseTransformFeedback() {
        wrapper.glPauseTransformFeedback();
    }

    public void glResumeTransformFeedback() {
        wrapper.glResumeTransformFeedback();
    }

    public void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, Buffer binary) {
        wrapper.glGetProgramBinary(program, bufSize, length, lengthOffset, binaryFormat, binaryFormatOffset, binary);
    }

    public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
        wrapper.glGetProgramBinary(program, bufSize, length, binaryFormat, binary);
    }

    public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
        wrapper.glProgramBinary(program, binaryFormat, binary, length);
    }

    public void glProgramParameteri(int program, int pname, int value) {
        wrapper.glProgramParameteri(program, pname, value);
    }

    public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
        wrapper.glInvalidateFramebuffer(target, numAttachments, attachments, offset);
    }

    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        wrapper.glInvalidateFramebuffer(target, numAttachments, attachments);
    }

    public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height) {
        wrapper.glInvalidateSubFramebuffer(target, numAttachments, attachments, offset, x, y, width, height);
    }

    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        wrapper.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
    }

    public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
        wrapper.glTexStorage2D(target, levels, internalformat, width, height);
    }

    public void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
        wrapper.glTexStorage3D(target, levels, internalformat, width, height, depth);
    }

    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
        wrapper.glGetInternalformativ(target, internalformat, pname, bufSize, params, offset);
    }

    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, IntBuffer params) {
        wrapper.glGetInternalformativ(target, internalformat, pname, bufSize, params);
    }

    public void glReadPixels(int x, int y, int width, int height, int format, int type, int offset) {
        wrapper.glReadPixels(x, y, width, height, format, type, offset);
    }
}
