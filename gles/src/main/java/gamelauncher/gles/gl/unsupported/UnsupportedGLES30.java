package gamelauncher.gles.gl.unsupported;

import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.redirect.RedirectGLES20;

import java.nio.*;

public class UnsupportedGLES30 extends RedirectGLES20 implements GLES30 {

    public UnsupportedGLES30(GLES20 wrapper) {
        super(wrapper);
    }

    @Override
    public void glReadBuffer(int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, Buffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenQueries(int n, IntBuffer ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteQueries(int n, IntBuffer ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsQuery(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBeginQuery(int target, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glEndQuery(int target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetQueryiv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetQueryiv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glUnmapBuffer(int target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Buffer glGetBufferPointerv(int target, int pname) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawBuffers(int n, int[] bufs, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawBuffers(int n, IntBuffer bufs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindVertexArray(int array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenVertexArrays(int n, IntBuffer arrays) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsVertexArray(int array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetIntegeri_v(int target, int index, int[] data, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetIntegeri_v(int target, int index, IntBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glEndTransformFeedback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, byte name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetTransformFeedbackVarying(int program, int index, IntBuffer size, IntBuffer type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4iv(int index, int[] v, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4iv(int index, IntBuffer v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4uiv(int index, int[] v, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribI4uiv(int index, IntBuffer v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetUniformuiv(int program, int location, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetUniformuiv(int program, int location, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform1ui(int location, int v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform2ui(int location, int v0, int v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform3ui(int location, int v0, int v1, int v2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform1uiv(int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform1uiv(int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform2uiv(int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform2uiv(int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform3uiv(int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform3uiv(int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform4uiv(int location, int count, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniform4uiv(int location, int count, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetStringi(int name, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices, int instanceCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long glFenceSync(int condition, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsSync(long sync) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteSync(long sync) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glClientWaitSync(long sync, int flags, long timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glWaitSync(long sync, int flags, long timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInteger64v(int pname, long[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInteger64v(int pname, LongBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSynciv(long sync, int pname, int bufSize, IntBuffer length, IntBuffer values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInteger64i_v(int target, int index, long[] data, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInteger64i_v(int target, int index, LongBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, long[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenSamplers(int count, int[] samplers, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenSamplers(int count, IntBuffer samplers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteSamplers(int count, IntBuffer samplers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsSampler(int sampler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindSampler(int unit, int sampler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, int[] param, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, float[] param, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBindTransformFeedback(int target, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glPauseTransformFeedback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glResumeTransformFeedback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, Buffer binary) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, int offset) {
        throw new UnsupportedOperationException();
    }
}
