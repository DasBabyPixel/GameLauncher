package gamelauncher.gles.gl.redirect;

import de.dasbabypixel.annotations.Api;
import gamelauncher.gles.gl.GLES32;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Api
public class RedirectGLES32 extends RedirectGLES31 implements GLES32 {
    private final GLES32 wrapper;

    @Api public RedirectGLES32(GLES32 wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    @Override public void glBlendBarrier() {
        wrapper.glBlendBarrier();
    }

    @Override public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        wrapper.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth);
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, int[] ids, int offset, boolean enabled) {
        wrapper.glDebugMessageControl(source, type, severity, count, ids, offset, enabled);
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, IntBuffer ids, boolean enabled) {
        wrapper.glDebugMessageControl(source, type, severity, count, ids, enabled);
    }

    @Override public void glDebugMessageInsert(int source, int type, int id, int severity, int length, String buf) {
        wrapper.glDebugMessageInsert(source, type, id, severity, length, buf);
    }

    @Override public void glDebugMessageCallback(DebugProc callback) {
        wrapper.glDebugMessageCallback(callback);
    }

    @Override public int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset, int[] lengths, int lengthsOffset, byte[] messageLog, int messageLogOffset) {
        return wrapper.glGetDebugMessageLog(count, bufSize, sources, sourcesOffset, types, typesOffset, ids, idsOffset, severities, severitiesOffset, lengths, lengthsOffset, messageLog, messageLogOffset);
    }

    @Override public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        return wrapper.glGetDebugMessageLog(count, sources, types, ids, severities, lengths, messageLog);
    }

    @Override public String[] glGetDebugMessageLog(int count, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset) {
        return wrapper.glGetDebugMessageLog(count, sources, sourcesOffset, types, typesOffset, ids, idsOffset, severities, severitiesOffset);
    }

    @Override public String[] glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities) {
        return wrapper.glGetDebugMessageLog(count, sources, types, ids, severities);
    }

    @Override public void glPushDebugGroup(int source, int id, int length, String message) {
        wrapper.glPushDebugGroup(source, id, length, message);
    }

    @Override public void glPopDebugGroup() {
        wrapper.glPopDebugGroup();
    }

    @Override public void glObjectLabel(int identifier, int name, int length, String label) {
        wrapper.glObjectLabel(identifier, name, length, label);
    }

    @Override public String glGetObjectLabel(int identifier, int name) {
        return wrapper.glGetObjectLabel(identifier, name);
    }

    @Override public void glObjectPtrLabel(long ptr, String label) {
        wrapper.glObjectPtrLabel(ptr, label);
    }

    @Override public String glGetObjectPtrLabel(long ptr) {
        return wrapper.glGetObjectPtrLabel(ptr);
    }

    @Override public long glGetPointerv(int pname) {
        return wrapper.glGetPointerv(pname);
    }

    @Override public void glEnablei(int target, int index) {
        wrapper.glEnablei(target, index);
    }

    @Override public void glDisablei(int target, int index) {
        wrapper.glDisablei(target, index);
    }

    @Override public void glBlendEquationi(int buf, int mode) {
        wrapper.glBlendEquationi(buf, mode);
    }

    @Override public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        wrapper.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
    }

    @Override public void glBlendFunci(int buf, int src, int dst) {
        wrapper.glBlendFunci(buf, src, dst);
    }

    @Override public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        wrapper.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        wrapper.glColorMaski(index, r, g, b, a);
    }

    @Override public boolean glIsEnabledi(int target, int index) {
        return wrapper.glIsEnabledi(target, index);
    }

    @Override public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        wrapper.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
    }

    @Override public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        wrapper.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
    }

    @Override public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        wrapper.glDrawElementsInstancedBaseVertex(mode, count, type, indices, instanceCount, basevertex);
    }

    @Override public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex) {
        wrapper.glDrawElementsInstancedBaseVertex(mode, count, type, indicesOffset, instanceCount, basevertex);
    }

    @Override public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        wrapper.glFramebufferTexture(target, attachment, texture, level);
    }

    @Override public void glPrimitiveBoundingBox(float minX, float minY, float minZ, float minW, float maxX, float maxY, float maxZ, float maxW) {
        wrapper.glPrimitiveBoundingBox(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
    }

    @Override public int glGetGraphicsResetStatus() {
        return wrapper.glGetGraphicsResetStatus();
    }

    @Override public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        wrapper.glReadnPixels(x, y, width, height, format, type, bufSize, data);
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, float[] params, int offset) {
        wrapper.glGetnUniformfv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, FloatBuffer params) {
        wrapper.glGetnUniformfv(program, location, bufSize, params);
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, int[] params, int offset) {
        wrapper.glGetnUniformiv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, IntBuffer params) {
        wrapper.glGetnUniformiv(program, location, bufSize, params);
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, int[] params, int offset) {
        wrapper.glGetnUniformuiv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, IntBuffer params) {
        wrapper.glGetnUniformuiv(program, location, bufSize, params);
    }

    @Override public void glMinSampleShading(float value) {
        wrapper.glMinSampleShading(value);
    }

    @Override public void glPatchParameteri(int pname, int value) {
        wrapper.glPatchParameteri(pname, value);
    }

    @Override public void glTexParameterIiv(int target, int pname, int[] params, int offset) {
        wrapper.glTexParameterIiv(target, pname, params, offset);
    }

    @Override public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        wrapper.glTexParameterIiv(target, pname, params);
    }

    @Override public void glTexParameterIuiv(int target, int pname, int[] params, int offset) {
        wrapper.glTexParameterIuiv(target, pname, params, offset);
    }

    @Override public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        wrapper.glTexParameterIuiv(target, pname, params);
    }

    @Override public void glGetTexParameterIiv(int target, int pname, int[] params, int offset) {
        wrapper.glGetTexParameterIiv(target, pname, params, offset);
    }

    @Override public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        wrapper.glGetTexParameterIiv(target, pname, params);
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, int[] params, int offset) {
        wrapper.glGetTexParameterIuiv(target, pname, params, offset);
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        wrapper.glGetTexParameterIuiv(target, pname, params);
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, int[] param, int offset) {
        wrapper.glSamplerParameterIiv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        wrapper.glSamplerParameterIiv(sampler, pname, param);
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, int[] param, int offset) {
        wrapper.glSamplerParameterIuiv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        wrapper.glSamplerParameterIuiv(sampler, pname, param);
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int offset) {
        wrapper.glGetSamplerParameterIiv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        wrapper.glGetSamplerParameterIiv(sampler, pname, params);
    }

    @Override public void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int offset) {
        wrapper.glGetSamplerParameterIuiv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        wrapper.glGetSamplerParameterIuiv(sampler, pname, params);
    }

    @Override public void glTexBuffer(int target, int internalformat, int buffer) {
        wrapper.glTexBuffer(target, internalformat, buffer);
    }

    @Override public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        wrapper.glTexBufferRange(target, internalformat, buffer, offset, size);
    }

    @Override public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
        wrapper.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
    }
}
