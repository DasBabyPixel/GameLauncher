/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl.supported;

import android.opengl.GLES32;
import android.os.Build;
import androidx.annotation.RequiresApi;
import de.dasbabypixel.annotations.Api;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Api
@RequiresApi(api = Build.VERSION_CODES.N)
public class SupportedAndroidGLES32 extends SupportedAndroidGLES31 implements gamelauncher.gles.gl.GLES32 {

    @Override public void glBlendBarrier() {
        GLES32.glBlendBarrier();
    }

    @Override public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        GLES32.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth);
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, int[] ids, int offset, boolean enabled) {
        GLES32.glDebugMessageControl(source, type, severity, count, ids, offset, enabled);
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, IntBuffer ids, boolean enabled) {
        GLES32.glDebugMessageControl(source, type, severity, count, ids, enabled);
    }

    @Override public void glDebugMessageInsert(int source, int type, int id, int severity, int length, String buf) {
        GLES32.glDebugMessageInsert(source, type, id, severity, length, buf);
    }

    @Override public void glDebugMessageCallback(DebugProc callback) {
        GLES32.glDebugMessageCallback(callback::onMessage);
    }

    @Override public int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset, int[] lengths, int lengthsOffset, byte[] messageLog, int messageLogOffset) {
        return GLES32.glGetDebugMessageLog(count, bufSize, sources, sourcesOffset, types, typesOffset, ids, idsOffset, severities, severitiesOffset, lengths, lengthsOffset, messageLog, messageLogOffset);
    }

    @Override public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        return GLES32.glGetDebugMessageLog(count, sources, types, ids, severities, lengths, messageLog);
    }

    @Override public String[] glGetDebugMessageLog(int count, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset) {
        return GLES32.glGetDebugMessageLog(count, sources, sourcesOffset, types, typesOffset, ids, idsOffset, severities, severitiesOffset);
    }

    @Override public String[] glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities) {
        return GLES32.glGetDebugMessageLog(count, sources, types, ids, severities);
    }

    @Override public void glPushDebugGroup(int source, int id, int length, String message) {
        GLES32.glPushDebugGroup(source, id, length, message);
    }

    @Override public void glPopDebugGroup() {
        GLES32.glPopDebugGroup();
    }

    @Override public void glObjectLabel(int identifier, int name, int length, String label) {
        GLES32.glObjectLabel(identifier, name, length, label);
    }

    @Override public String glGetObjectLabel(int identifier, int name) {
        return GLES32.glGetObjectLabel(identifier, name);
    }

    @Override public void glObjectPtrLabel(long ptr, String label) {
        GLES32.glObjectPtrLabel(ptr, label);
    }

    @Override public String glGetObjectPtrLabel(long ptr) {
        return GLES32.glGetObjectPtrLabel(ptr);
    }

    @Override public long glGetPointerv(int pname) {
        return GLES32.glGetPointerv(pname);
    }

    @Override public void glEnablei(int target, int index) {
        GLES32.glEnablei(target, index);
    }

    @Override public void glDisablei(int target, int index) {
        GLES32.glDisablei(target, index);
    }

    @Override public void glBlendEquationi(int buf, int mode) {
        GLES32.glBlendEquationi(buf, mode);
    }

    @Override public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        GLES32.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
    }

    @Override public void glBlendFunci(int buf, int src, int dst) {
        GLES32.glBlendFunci(buf, src, dst);
    }

    @Override public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES32.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        GLES32.glColorMaski(index, r, g, b, a);
    }

    @Override public boolean glIsEnabledi(int target, int index) {
        return GLES32.glIsEnabledi(target, index);
    }

    @Override public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        GLES32.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
    }

    @Override public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        GLES32.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
    }

    @Override public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        GLES32.glDrawElementsInstancedBaseVertex(mode, count, type, indices, instanceCount, basevertex);
    }

    @Override public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex) {
        GLES32.glDrawElementsInstancedBaseVertex(mode, count, type, indicesOffset, instanceCount, basevertex);
    }

    @Override public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        GLES32.glFramebufferTexture(target, attachment, texture, level);
    }

    @Override public void glPrimitiveBoundingBox(float minX, float minY, float minZ, float minW, float maxX, float maxY, float maxZ, float maxW) {
        GLES32.glPrimitiveBoundingBox(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
    }

    @Override public int glGetGraphicsResetStatus() {
        return GLES32.glGetGraphicsResetStatus();
    }

    @Override public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        GLES32.glReadnPixels(x, y, width, height, format, type, bufSize, data);
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, float[] params, int offset) {
        GLES32.glGetnUniformfv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, FloatBuffer params) {
        GLES32.glGetnUniformfv(program, location, bufSize, params);
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, int[] params, int offset) {
        GLES32.glGetnUniformiv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, IntBuffer params) {
        GLES32.glGetnUniformiv(program, location, bufSize, params);
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, int[] params, int offset) {
        GLES32.glGetnUniformuiv(program, location, bufSize, params, offset);
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, IntBuffer params) {
        GLES32.glGetnUniformuiv(program, location, bufSize, params);
    }

    @Override public void glMinSampleShading(float value) {
        GLES32.glMinSampleShading(value);
    }

    @Override public void glPatchParameteri(int pname, int value) {
        GLES32.glPatchParameteri(pname, value);
    }

    @Override public void glTexParameterIiv(int target, int pname, int[] params, int offset) {
        GLES32.glTexParameterIiv(target, pname, params, offset);
    }

    @Override public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        GLES32.glTexParameterIiv(target, pname, params);
    }

    @Override public void glTexParameterIuiv(int target, int pname, int[] params, int offset) {
        GLES32.glTexParameterIuiv(target, pname, params, offset);
    }

    @Override public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        GLES32.glTexParameterIuiv(target, pname, params);
    }

    @Override public void glGetTexParameterIiv(int target, int pname, int[] params, int offset) {
        GLES32.glGetTexParameterIiv(target, pname, params, offset);
    }

    @Override public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        GLES32.glGetTexParameterIiv(target, pname, params);
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, int[] params, int offset) {
        GLES32.glGetTexParameterIuiv(target, pname, params, offset);
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        GLES32.glGetTexParameterIuiv(target, pname, params);
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, int[] param, int offset) {
        GLES32.glSamplerParameterIiv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        GLES32.glSamplerParameterIiv(sampler, pname, param);
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, int[] param, int offset) {
        GLES32.glSamplerParameterIuiv(sampler, pname, param, offset);
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        GLES32.glSamplerParameterIuiv(sampler, pname, param);
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int offset) {
        GLES32.glGetSamplerParameterIiv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        GLES32.glGetSamplerParameterIiv(sampler, pname, params);
    }

    @Override public void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int offset) {
        GLES32.glGetSamplerParameterIuiv(sampler, pname, params, offset);
    }

    @Override public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        GLES32.glGetSamplerParameterIuiv(sampler, pname, params);
    }

    @Override public void glTexBuffer(int target, int internalformat, int buffer) {
        GLES32.glTexBuffer(target, internalformat, buffer);
    }

    @Override public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        GLES32.glTexBufferRange(target, internalformat, buffer, offset, size);
    }

    @Override public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
        GLES32.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
    }
}
