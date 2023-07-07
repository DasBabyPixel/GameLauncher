/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengles.GLDebugMessageCallback;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES32;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.nio.*;
import java.util.Arrays;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class LWJGLGLES implements gamelauncher.gles.gl.GLES32 {

    public static final LWJGLGLES instance = new LWJGLGLES();

    private LWJGLGLES() {
    }

    public static void initialize() {
        Configuration.OPENGL_EXPLICIT_INIT.set(true);
        Configuration.OPENGLES_EXPLICIT_INIT.set(true);
        GL.create();
        org.lwjgl.opengles.GLES.create(GL.getFunctionProvider());
        GL.destroy();
    }

    public static GLESCapabilities createCapabilities() {
        return GLES.createCapabilities();
    }

    public static void setCapabilities(GLESCapabilities capabilities) {
        GLES.setCapabilities(capabilities);
    }

    @Override public void glActiveTexture(int texture) {
        GLES32.glActiveTexture(texture);
    }

    @Override public void glAttachShader(int program, int shader) {
        GLES32.glAttachShader(program, shader);
    }

    @Override public void glBindAttribLocation(int program, int index, String name) {
        GLES32.glBindAttribLocation(program, index, name);
    }

    @Override public void glBindBuffer(int target, int buffer) {
        GLES32.glBindBuffer(target, buffer);
    }

    @Override public void glBindFramebuffer(int target, int framebuffer) {
        GLES32.glBindFramebuffer(target, framebuffer);
    }

    @Override public void glBindRenderbuffer(int target, int renderbuffer) {
        GLES32.glBindRenderbuffer(target, renderbuffer);
    }

    @Override public void glBindTexture(int target, int texture) {
        GLES32.glBindTexture(target, texture);
    }

    @Override public void glBlendColor(float red, float green, float blue, float alpha) {
        GLES32.glBlendColor(red, green, blue, alpha);
    }

    @Override public void glBlendEquation(int mode) {
        GLES32.glBlendEquation(mode);
    }

    @Override public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        GLES32.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override public void glBlendFunc(int sfactor, int dfactor) {
        GLES32.glBlendFunc(sfactor, dfactor);
    }

    @Override public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES32.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override public void glBufferData(int target, long size, Buffer data, int usage) {
        GLES32.nglBufferData(target, size, memAddress(data), usage);
    }

    @Override public void glBufferData(int target, IntBuffer buffer, int usage) {
        GLES32.glBufferData(target, buffer, usage);
    }

    @Override public void glBufferData(int target, FloatBuffer buffer, int usage) {
        GLES32.glBufferData(target, buffer, usage);
    }

    @Override public void glBufferSubData(int target, int offset, long size, Buffer data) {
        GLES32.nglBufferSubData(target, offset, size, memAddress(data));
    }

    @Override public int glCheckFramebufferStatus(int target) {
        return GLES32.glCheckFramebufferStatus(target);
    }

    @Override public void glClear(int mask) {
        GLES32.glClear(mask);
    }

    @Override public void glClearColor(float red, float green, float blue, float alpha) {
        GLES32.glClearColor(red, green, blue, alpha);
    }

    @Override public void glClearDepthf(float depth) {
        GLES32.glClearDepthf(depth);
    }

    @Override public void glClearStencil(int s) {
        GLES32.glClearStencil(s);
    }

    @Override public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GLES32.glColorMask(red, green, blue, alpha);
    }

    @Override public void glCompileShader(int shader) {
        GLES32.glCompileShader(shader);
    }

    @Override public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
        GLES32.nglCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, memAddress(data));
    }

    @Override public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
        GLES32.nglCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, memAddress(data));
    }

    @Override public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        GLES32.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        GLES32.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override public int glCreateProgram() {
        return GLES32.glCreateProgram();
    }

    @Override public int glCreateShader(int type) {
        return GLES32.glCreateShader(type);
    }

    @Override public void glCullFace(int mode) {
        GLES32.glCullFace(mode);
    }

    @Override public void glDeleteBuffers(int n, int[] buffers, int offset) {
        GLES32.glDeleteBuffers(Arrays.copyOfRange(buffers, offset, offset + n));
    }

    @Override public void glDeleteBuffers(int n, IntBuffer buffers) {
        throw new LazyException();
    }

    @Override public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        GLES32.glDeleteFramebuffers(Arrays.copyOfRange(framebuffers, offset, offset + n));
    }

    @Override public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        throw new LazyException();
    }

    @Override public void glDeleteProgram(int program) {
        GLES32.glDeleteProgram(program);
    }

    @Override public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        GLES32.glDeleteRenderbuffers(Arrays.copyOfRange(renderbuffers, offset, offset + n));
    }

    @Override public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES32.nglDeleteRenderbuffers(n, memAddress(renderbuffers));
    }

    @Override public void glDeleteShader(int shader) {
        GLES32.glDeleteShader(shader);
    }

    @Override public void glDeleteTextures(int n, int[] textures, int offset) {
        GLES32.glDeleteTextures(Arrays.copyOfRange(textures, offset, offset + n));
    }

    @Override public void glDeleteTextures(int n, IntBuffer textures) {
        GLES32.nglDeleteTextures(n, memAddress(textures));
    }

    @Override public void glDepthFunc(int func) {
        GLES32.glDepthFunc(func);
    }

    @Override public void glDepthMask(boolean flag) {
        GLES32.glDepthMask(flag);
    }

    @Override public void glDepthRangef(float zNear, float zFar) {
        GLES32.glDepthRangef(zNear, zFar);
    }

    @Override public void glDetachShader(int program, int shader) {
        GLES32.glDetachShader(program, shader);
    }

    @Override public void glDisable(int cap) {
        GLES32.glDisable(cap);
    }

    @Override public void glDisableVertexAttribArray(int index) {
        GLES32.glDisableVertexAttribArray(index);
    }

    @Override public void glDrawArrays(int mode, int first, int count) {
        GLES32.glDrawArrays(mode, first, count);
    }

    @Override public void glDrawElements(int mode, int count, int type, int offset) {
        GLES32.glDrawElements(mode, count, type, offset);
    }

    @Override public void glDrawElements(int mode, int count, int type, Buffer indices) {
        GLES32.nglDrawElements(mode, count, type, memAddress(indices));
    }

    @Override public void glEnable(int cap) {
        GLES32.glEnable(cap);
    }

    @Override public void glEnableVertexAttribArray(int index) {
        GLES32.glEnableVertexAttribArray(index);
    }

    @Override public void glFinish() {
        GLES32.glFinish();
    }

    @Override public void glFlush() {
        GLES32.glFlush();
    }

    @Override public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        GLES32.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GLES32.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override public void glFrontFace(int mode) {
        GLES32.glFrontFace(mode);
    }

    @Override public void glGenBuffers(int n, int[] buffers, int offset) {
        int[] na = new int[n];
        GLES32.glGenBuffers(na);
        for (int i = 0, j = offset; i < n; i++, j++) buffers[j] = na[i];
    }

    @Override public int glGenBuffers() {
        return GLES32.glGenBuffers();
    }

    @Override public void glGenBuffers(int n, IntBuffer buffers) {
        GLES32.nglGenBuffers(n, memAddress(buffers));
    }

    @Override public void glGenerateMipmap(int target) {
        GLES32.glGenerateMipmap(target);
    }

    @Override public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        int[] na = new int[n];
        GLES32.glGenFramebuffers(na);
        for (int i = 0, j = offset; i < n; i++, j++) framebuffers[j] = na[i];
    }

    @Override public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        GLES32.nglGenFramebuffers(n, memAddress(framebuffers));
    }

    @Override public int glGenFramebuffers() {
        return GLES32.glGenFramebuffers();
    }

    @Override public void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
        int[] na = new int[n];
        GLES32.glGenRenderbuffers(na);
        for (int i = 0, j = offset; i < n; i++, j++) renderbuffers[j] = na[i];
    }

    @Override public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES32.nglGenRenderbuffers(n, memAddress(renderbuffers));
    }

    @Override public int glGenRenderbuffers() {
        return GLES32.glGenRenderbuffers();
    }

    @Override public void glGenTextures(int n, int[] textures, int offset) {
        int[] na = new int[n];
        GLES32.glGenTextures(na);
        for (int i = 0, j = offset; i < n; i++, j++) textures[j] = na[i];
    }

    @Override public void glGenTextures(int n, IntBuffer textures) {
        GLES32.nglGenTextures(n, memAddress(textures));
    }

    @Override public int glGenTextures() {
        return GLES32.glGenTextures();
    }

    @Override public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            IntBuffer len = stack.mallocInt(length.length - lengthOffset).put(length, lengthOffset, length.length - lengthOffset);
            IntBuffer siz = stack.mallocInt(size.length - sizeOffset).put(size, sizeOffset, size.length - sizeOffset);
            IntBuffer typ = stack.mallocInt(type.length - typeOffset).put(type, typeOffset, type.length - typeOffset);
            ByteBuffer nam = stack.malloc(bufsize).put(name, nameOffset, bufsize);
            GLES32.glGetActiveAttrib(program, index, len, siz, typ, nam);
            nam.get(name, nameOffset, bufsize);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    @Override public String glGetActiveAttrib(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        throw new LazyException();
    }

    @Override public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        return GLES32.glGetActiveAttrib(program, index, size, type);
    }

    @Override public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        throw new LazyException();
    }

    @Override public String glGetActiveUniform(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        throw new LazyException();
    }

    @Override public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        return GLES32.glGetActiveUniform(program, index, size, type);
    }

    @Override public void glGetAttachedShaders(int program, int maxcount, int[] count, int countOffset, int[] shaders, int shadersOffset) {
        throw new LazyException();
    }

    @Override public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
        throw new LazyException();
    }

    @Override public int glGetAttribLocation(int program, String name) {
        return GLES32.glGetAttribLocation(program, name);
    }

    @Override public void glGetBooleanv(int pname, boolean[] params, int offset) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            ByteBuffer buf = stack.malloc(params.length - offset);
            for (int i = offset, j = 0; i < params.length; i++, j++)
                buf.put(j, params[i] ? (byte) 1 : 0);
            GLES32.nglGetBooleanv(pname, memAddress(buf));
            for (int i = offset, j = 0; i < params.length; i++, j++)
                params[i] = buf.get(j) != 0;
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    @Override public void glGetBooleanv(int pname, IntBuffer params) {
        GLES32.nglGetBooleanv(pname, memAddress(params));
    }

    @Override public void glGetBufferParameteriv(int target, int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetBufferParameteriv(target, pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        GLES32.glGetBufferParameteriv(target, pname, params);
    }

    @Override public int glGetError() {
        return GLES32.glGetError();
    }

    @Override public void glGetFloatv(int pname, float[] params, int offset) {
        float[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetFloatv(pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetFloatv(int pname, FloatBuffer params) {
        GLES32.glGetFloatv(pname, params);
    }

    @Override public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetFramebufferAttachmentParameteriv(target, attachment, pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        GLES32.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    @Override public void glGetIntegerv(int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetIntegerv(pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetIntegerv(int pname, IntBuffer params) {
        GLES32.glGetIntegerv(pname, params);
    }

    @Override public void glGetProgramiv(int program, int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetProgramiv(program, pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetProgramiv(int program, int pname, IntBuffer params) {
        GLES32.glGetProgramiv(program, pname, params);
    }

    @Override public String glGetProgramInfoLog(int program) {
        return GLES32.glGetProgramInfoLog(program);
    }

    @Override public void glGetRenderbufferParameteriv(int target, int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetRenderbufferParameteriv(target, pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GLES32.glGetRenderbufferParameteriv(target, pname, params);
    }

    @Override public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        int[] an = Arrays.copyOfRange(params, offset, params.length);
        GLES32.glGetShaderiv(shader, pname, an);
        for (int i = 0, j = offset; i < an.length; i++, j++)
            params[j] = an[i];
    }

    @Override public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        GLES32.glGetShaderiv(shader, pname, params);
    }

    @Override public String glGetShaderInfoLog(int shader) {
        return GLES32.glGetShaderInfoLog(shader);
    }

    @Override public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range, int rangeOffset, int[] precision, int precisionOffset) {
        int[] an1 = Arrays.copyOfRange(range, rangeOffset, range.length);
        int[] an2 = Arrays.copyOfRange(precision, precisionOffset, precision.length);
        GLES32.glGetShaderPrecisionFormat(shadertype, precisiontype, an1, an2);
        for (int i = 0, j = rangeOffset; i < an1.length; i++, j++)
            range[j] = an1[i];
        for (int i = 0, j = precisionOffset; i < an2.length; i++, j++)
            precision[j] = an2[i];
    }

    @Override public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        GLES32.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
    }

    @Override public void glGetShaderSource(int shader, int bufsize, int[] length, int lengthOffset, byte[] source, int sourceOffset) {
        throw new LazyException();
    }

    @Override public String glGetShaderSource(int shader) {
        return GLES32.glGetShaderSource(shader);
    }

    @Override public String glGetString(int name) {
        return GLES32.glGetString(name);
    }

    @Override public void glGetTexParameterfv(int target, int pname, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES32.glGetTexParameterfv(target, pname, params);
    }

    @Override public void glGetTexParameteriv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        GLES32.glGetTexParameteriv(target, pname, params);
    }

    @Override public void glGetUniformfv(int program, int location, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetUniformfv(int program, int location, FloatBuffer params) {
        GLES32.glGetUniformfv(program, location, params);
    }

    @Override public void glGetUniformiv(int program, int location, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetUniformiv(int program, int location, IntBuffer params) {
        GLES32.glGetUniformiv(program, location, params);
    }

    @Override public int glGetUniformLocation(int program, String name) {
        return GLES32.glGetUniformLocation(program, name);
    }

    @Override public void glGetVertexAttribfv(int index, int pname, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        GLES32.glGetVertexAttribfv(index, pname, params);
    }

    @Override public void glGetVertexAttribiv(int index, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        GLES32.glGetVertexAttribiv(index, pname, params);
    }

    @Override public void glHint(int target, int mode) {
        GLES32.glHint(target, mode);
    }

    @Override public boolean glIsBuffer(int buffer) {
        return GLES32.glIsBuffer(buffer);
    }

    @Override public boolean glIsEnabled(int cap) {
        return GLES32.glIsEnabled(cap);
    }

    @Override public boolean glIsFramebuffer(int framebuffer) {
        return GLES32.glIsFramebuffer(framebuffer);
    }

    @Override public boolean glIsProgram(int program) {
        return GLES32.glIsProgram(program);
    }

    @Override public boolean glIsRenderbuffer(int renderbuffer) {
        return GLES32.glIsRenderbuffer(renderbuffer);
    }

    @Override public boolean glIsShader(int shader) {
        return GLES32.glIsShader(shader);
    }

    @Override public boolean glIsTexture(int texture) {
        return GLES32.glIsTexture(texture);
    }

    @Override public void glLineWidth(float width) {
        GLES32.glLineWidth(width);
    }

    @Override public void glLinkProgram(int program) {
        GLES32.glLinkProgram(program);
    }

    @Override public void glPixelStorei(int pname, int param) {
        GLES32.glPixelStorei(pname, param);
    }

    @Override public void glPolygonOffset(float factor, float units) {
        GLES32.glPolygonOffset(factor, units);
    }

    @Override public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        GLES32.glReadPixels(x, y, width, height, format, type, memAddress(pixels));
    }

    @Override public void glReleaseShaderCompiler() {
        GLES32.glReleaseShaderCompiler();
    }

    @Override public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        GLES32.glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override public void glSampleCoverage(float value, boolean invert) {
        GLES32.glSampleCoverage(value, invert);
    }

    @Override public void glScissor(int x, int y, int width, int height) {
        GLES32.glScissor(x, y, width, height);
    }

    @Override public void glShaderBinary(int n, int[] shaders, int offset, int binaryformat, Buffer binary, int length) {
        throw new LazyException();
    }

    @Override public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        throw new LazyException();
    }

    @Override public void glShaderSource(int shader, String string) {
        GLES32.glShaderSource(shader, string);
    }

    @Override public void glStencilFunc(int func, int ref, int mask) {
        GLES32.glStencilFunc(func, ref, mask);
    }

    @Override public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        GLES32.glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override public void glStencilMask(int mask) {
        GLES32.glStencilMask(mask);
    }

    @Override public void glStencilMaskSeparate(int face, int mask) {
        GLES32.glStencilMaskSeparate(face, mask);
    }

    @Override public void glStencilOp(int fail, int zfail, int zpass) {
        GLES32.glStencilOp(fail, zfail, zpass);
    }

    @Override public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        GLES32.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        GLES32.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels == null ? 0 : memAddress(pixels));
    }

    @Override public void glTexParameterf(int target, int pname, float param) {
        GLES32.glTexParameterf(target, pname, param);
    }

    @Override public void glTexParameterfv(int target, int pname, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES32.glTexParameterfv(target, pname, params);
    }

    @Override public void glTexParameteri(int target, int pname, int param) {
        GLES32.glTexParameteri(target, pname, param);
    }

    @Override public void glTexParameteriv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glTexParameteriv(int target, int pname, IntBuffer params) {
        GLES32.glTexParameteriv(target, pname, params);
    }

    @Override public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        GLES32.nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, memAddress(pixels));
    }

    @Override public void glUniform1f(int location, float x) {
        GLES32.glUniform1f(location, x);
    }

    @Override public void glUniform1fv(int location, int count, float[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform1fv(int location, int count, FloatBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniform1i(int location, int x) {
        GLES32.glUniform1i(location, x);
    }

    @Override public void glUniform1iv(int location, int count, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform1iv(int location, int count, IntBuffer v) {
        GLES32.nglUniform1iv(location, count, memAddress(v));
    }

    @Override public void glUniform2f(int location, float x, float y) {
        GLES32.glUniform2f(location, x, y);
    }

    @Override public void glUniform2fv(int location, int count, float[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform2fv(int location, int count, FloatBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniform2i(int location, int x, int y) {
        GLES32.glUniform2i(location, x, y);
    }

    @Override public void glUniform2iv(int location, int count, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform2iv(int location, int count, IntBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniform3f(int location, float x, float y, float z) {
        GLES32.glUniform3f(location, x, y, z);
    }

    @Override public void glUniform3fv(int location, int count, float[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform3fv(int location, int count, FloatBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniform3i(int location, int x, int y, int z) {
        GLES32.glUniform3i(location, x, y, z);
    }

    @Override public void glUniform3iv(int location, int count, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform3iv(int location, int count, IntBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniform4f(int location, float x, float y, float z, float w) {
        GLES32.glUniform4f(location, x, y, z, w);
    }

    @Override public void glUniform4fv(int location, int count, float[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform4fv(int location, int count, FloatBuffer v) {
        GLES32.nglUniform4fv(location, count, memAddress(v));
    }

    @Override public void glUniform4i(int location, int x, int y, int z, int w) {
        GLES32.glUniform4i(location, x, y, z, w);
    }

    @Override public void glUniform4iv(int location, int count, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform4iv(int location, int count, IntBuffer v) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES32.nglUniformMatrix4fv(location, count, transpose, memAddress(value));
    }

    @Override public void glUseProgram(int program) {
        GLES32.glUseProgram(program);
    }

    @Override public void glValidateProgram(int program) {
        GLES32.glValidateProgram(program);
    }

    @Override public void glVertexAttrib1f(int indx, float x) {
        GLES32.glVertexAttrib1f(indx, x);
    }

    @Override public void glVertexAttrib1fv(int indx, float[] values, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        GLES32.glVertexAttrib1fv(indx, values);
    }

    @Override public void glVertexAttrib2f(int indx, float x, float y) {
        GLES32.glVertexAttrib2f(indx, x, y);
    }

    @Override public void glVertexAttrib2fv(int indx, float[] values, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        GLES32.glVertexAttrib2fv(indx, values);
    }

    @Override public void glVertexAttrib3f(int indx, float x, float y, float z) {
        GLES32.glVertexAttrib3f(indx, x, y, z);
    }

    @Override public void glVertexAttrib3fv(int indx, float[] values, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        GLES32.glVertexAttrib3fv(indx, values);
    }

    @Override public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        GLES32.glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override public void glVertexAttrib4fv(int indx, float[] values, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        GLES32.glVertexAttrib4fv(indx, values);
    }

    @Override public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
        GLES32.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }

    @Override public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        throw new LazyException();
    }

    @Override public void glViewport(int x, int y, int width, int height) {
        GLES32.glViewport(x, y, width, height);
    }

    @Override public void glReadBuffer(int mode) {
        GLES32.glReadBuffer(mode);
    }

    @Override public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        throw new LazyException();
    }

    @Override public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        GLES32.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        throw new LazyException();
    }

    @Override public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        GLES32.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        throw new LazyException();
    }

    @Override public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        GLES32.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        GLES32.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, Buffer data) {
        throw new LazyException();
    }

    @Override public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset) {
        GLES32.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, offset);
    }

    @Override public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {
        throw new LazyException();
    }

    @Override public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset) {
        GLES32.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, offset);
    }

    @Override public void glGenQueries(int n, int[] ids, int offset) {
        throw new LazyException();
    }

    @Override public void glGenQueries(int n, IntBuffer ids) {
        throw new LazyException();
    }

    @Override public void glDeleteQueries(int n, int[] ids, int offset) {
        throw new LazyException();
    }

    @Override public void glDeleteQueries(int n, IntBuffer ids) {
        throw new LazyException();
    }

    @Override public boolean glIsQuery(int id) {
        return GLES32.glIsQuery(id);
    }

    @Override public void glBeginQuery(int target, int id) {
        GLES32.glBeginQuery(target, id);
    }

    @Override public void glEndQuery(int target) {
        GLES32.glEndQuery(target);
    }

    @Override public void glGetQueryiv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetQueryiv(int target, int pname, IntBuffer params) {
        GLES32.glGetQueryiv(target, pname, params);
    }

    @Override public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        GLES32.glGetQueryObjectuiv(id, pname, params);
    }

    @Override public boolean glUnmapBuffer(int target) {
        return GLES32.glUnmapBuffer(target);
    }

    @Override public Buffer glGetBufferPointerv(int target, int pname) {
        throw new LazyException();
    }

    @Override public void glDrawBuffers(int n, int[] bufs, int offset) {
        GLES32.glDrawBuffers(Arrays.copyOfRange(bufs, offset, offset + n));
    }

    @Override public void glDrawBuffers(int n, IntBuffer bufs) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        GLES32.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        GLES32.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        GLES32.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return GLES32.glMapBufferRange(target, offset, length, access);
    }

    @Override public void glFlushMappedBufferRange(int target, int offset, int length) {
        GLES32.glFlushMappedBufferRange(target, offset, length);
    }

    @Override public void glBindVertexArray(int array) {
        GLES32.glBindVertexArray(array);
    }

    @Override public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        GLES32.glDeleteVertexArrays(Arrays.copyOfRange(arrays, offset, offset + n));
    }

    @Override public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        throw new LazyException();
    }

    @Override public void glGenVertexArrays(int n, int[] arrays, int offset) {
        throw new LazyException();
    }

    @Override public void glGenVertexArrays(int n, IntBuffer arrays) {
        throw new LazyException();
    }

    @Override public int glGenVertexArrays() {
        return GLES32.glGenVertexArrays();
    }

    @Override public boolean glIsVertexArray(int array) {
        return GLES32.glIsVertexArray(array);
    }

    @Override public void glGetIntegeri_v(int target, int index, int[] data, int offset) {
        throw new LazyException();
    }

    @Override public void glGetIntegeri_v(int target, int index, IntBuffer data) {
        GLES32.glGetIntegeri_v(target, index, data);
    }

    @Override public void glBeginTransformFeedback(int primitiveMode) {
        GLES32.glBeginTransformFeedback(primitiveMode);
    }

    @Override public void glEndTransformFeedback() {
        GLES32.glEndTransformFeedback();
    }

    @Override public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        GLES32.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override public void glBindBufferBase(int target, int index, int buffer) {
        GLES32.glBindBufferBase(target, index, buffer);
    }

    @Override public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        GLES32.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override public void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        throw new LazyException();
    }

    @Override @Deprecated public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, byte name) {
        throw new LazyException();
    }

    @Override public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
        throw new LazyException();
    }

    @Override public String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        throw new LazyException();
    }

    @Override public String glGetTransformFeedbackVarying(int program, int index, IntBuffer size, IntBuffer type) {
        return GLES32.glGetTransformFeedbackVarying(program, index, size, type);
    }

    @Override public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {
        throw new LazyException();
    }

    @Override public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        GLES32.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override public void glGetVertexAttribIiv(int index, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        GLES32.glGetVertexAttribIiv(index, pname, params);
    }

    @Override public void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        GLES32.glGetVertexAttribIuiv(index, pname, params);
    }

    @Override public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        GLES32.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        GLES32.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override public void glVertexAttribI4iv(int index, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttribI4iv(int index, IntBuffer v) {
        GLES32.glVertexAttribI4iv(index, v);
    }

    @Override public void glVertexAttribI4uiv(int index, int[] v, int offset) {
        throw new LazyException();
    }

    @Override public void glVertexAttribI4uiv(int index, IntBuffer v) {
        GLES32.glVertexAttribI4uiv(index, v);
    }

    @Override public void glGetUniformuiv(int program, int location, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetUniformuiv(int program, int location, IntBuffer params) {
        GLES32.glGetUniformuiv(program, location, params);
    }

    @Override public int glGetFragDataLocation(int program, String name) {
        return GLES32.glGetFragDataLocation(program, name);
    }

    @Override public void glUniform1ui(int location, int v0) {
        GLES32.glUniform1ui(location, v0);
    }

    @Override public void glUniform2ui(int location, int v0, int v1) {
        GLES32.glUniform2ui(location, v0, v1);
    }

    @Override public void glUniform3ui(int location, int v0, int v1, int v2) {
        GLES32.glUniform3ui(location, v0, v1, v2);
    }

    @Override public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {
        GLES32.glUniform4ui(location, v0, v1, v2, v3);
    }

    @Override public void glUniform1uiv(int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform1uiv(int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniform2uiv(int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform2uiv(int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniform3uiv(int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform3uiv(int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glUniform4uiv(int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glUniform4uiv(int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        GLES32.glClearBufferiv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        GLES32.glClearBufferuiv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        GLES32.glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        GLES32.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override public String glGetStringi(int name, int index) {
        return GLES32.glGetStringi(name, index);
    }

    @Override public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        GLES32.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override public void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
        throw new LazyException();
    }

    @Override public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        throw new LazyException();
    }

    @Override public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset) {
        throw new LazyException();
    }

    @Override public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        throw new LazyException();
    }

    @Override public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return GLES32.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        GLES32.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

    @Override public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset) {
        throw new LazyException();
    }

    @Override public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        throw new LazyException();
    }

    @Override public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return GLES32.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    @Override public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        GLES32.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        GLES32.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices, int instanceCount) {
        throw new LazyException();
    }

    @Override public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        GLES32.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    @Override public long glFenceSync(int condition, int flags) {
        return GLES32.glFenceSync(condition, flags);
    }

    @Override public boolean glIsSync(long sync) {
        return GLES32.glIsSync(sync);
    }

    @Override public void glDeleteSync(long sync) {
        GLES32.glDeleteSync(sync);
    }

    @Override public int glClientWaitSync(long sync, int flags, long timeout) {
        return GLES32.glClientWaitSync(sync, flags, timeout);
    }

    @Override public void glWaitSync(long sync, int flags, long timeout) {
        GLES32.glWaitSync(sync, flags, timeout);
    }

    @Override public void glGetInteger64v(int pname, long[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetInteger64v(int pname, LongBuffer params) {
        GLES32.glGetInteger64v(pname, params);
    }

    @Override public void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
        throw new LazyException();
    }

    @Override public void glGetSynciv(long sync, int pname, int bufSize, IntBuffer length, IntBuffer values) {
        throw new LazyException();
    }

    @Override public void glGetInteger64i_v(int target, int index, long[] data, int offset) {
        throw new LazyException();
    }

    @Override public void glGetInteger64i_v(int target, int index, LongBuffer data) {
        GLES32.glGetInteger64i_v(target, index, data);
    }

    @Override public void glGetBufferParameteri64v(int target, int pname, long[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        GLES32.glGetBufferParameteri64v(target, pname, params);
    }

    @Override public void glGenSamplers(int count, int[] samplers, int offset) {
        throw new LazyException();
    }

    @Override public void glGenSamplers(int count, IntBuffer samplers) {
        throw new LazyException();
    }

    @Override public void glDeleteSamplers(int count, int[] samplers, int offset) {
        throw new LazyException();
    }

    @Override public void glDeleteSamplers(int count, IntBuffer samplers) {
        throw new LazyException();
    }

    @Override public boolean glIsSampler(int sampler) {
        return GLES32.glIsSampler(sampler);
    }

    @Override public void glBindSampler(int unit, int sampler) {
        GLES32.glBindSampler(unit, sampler);
    }

    @Override public void glSamplerParameteri(int sampler, int pname, int param) {
        GLES32.glSamplerParameteri(sampler, pname, param);
    }

    @Override public void glSamplerParameteriv(int sampler, int pname, int[] param, int offset) {
        throw new LazyException();
    }

    @Override public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        GLES32.glSamplerParameteriv(sampler, pname, param);
    }

    @Override public void glSamplerParameterf(int sampler, int pname, float param) {
        GLES32.glSamplerParameterf(sampler, pname, param);
    }

    @Override public void glSamplerParameterfv(int sampler, int pname, float[] param, int offset) {
        throw new LazyException();
    }

    @Override public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        GLES32.glSamplerParameterfv(sampler, pname, param);
    }

    @Override public void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        GLES32.glGetSamplerParameteriv(sampler, pname, params);
    }

    @Override public void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        GLES32.glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override public void glVertexAttribDivisor(int index, int divisor) {
        GLES32.glVertexAttribDivisor(index, divisor);
    }

    @Override public void glBindTransformFeedback(int target, int id) {
        GLES32.glBindTransformFeedback(target, id);
    }

    @Override public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        throw new LazyException();
    }

    @Override public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        throw new LazyException();
    }

    @Override public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        throw new LazyException();
    }

    @Override public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        throw new LazyException();
    }

    @Override public boolean glIsTransformFeedback(int id) {
        return GLES32.glIsTransformFeedback(id);
    }

    @Override public void glPauseTransformFeedback() {
        GLES32.glPauseTransformFeedback();
    }

    @Override public void glResumeTransformFeedback() {
        GLES32.glResumeTransformFeedback();
    }

    @Override public void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, Buffer binary) {
        throw new LazyException();
    }

    @Override public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
        throw new LazyException();
    }

    @Override public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
        throw new LazyException();
    }

    @Override public void glProgramParameteri(int program, int pname, int value) {
        GLES32.glProgramParameteri(program, pname, value);
    }

    @Override public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
        throw new LazyException();
    }

    @Override public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        throw new LazyException();
    }

    @Override public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height) {
        throw new LazyException();
    }

    @Override public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        throw new LazyException();
    }

    @Override public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
        GLES32.glTexStorage2D(target, levels, internalformat, width, height);
    }

    @Override public void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
        GLES32.glTexStorage3D(target, levels, internalformat, width, height, depth);
    }

    @Override public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, IntBuffer params) {
        throw new LazyException();
    }

    @Override public void glReadPixels(int x, int y, int width, int height, int format, int type, int offset) {
        GLES32.glReadPixels(x, y, width, height, format, type, offset);
    }

    @Override public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        GLES32.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
    }

    @Override public void glDispatchComputeIndirect(long indirect) {
        GLES32.glDispatchComputeIndirect(indirect);
    }

    @Override public void glDrawArraysIndirect(int mode, long indirect) {
        GLES32.glDrawArraysIndirect(mode, indirect);
    }

    @Override public void glDrawElementsIndirect(int mode, int type, long indirect) {
        GLES32.glDrawElementsIndirect(mode, type, indirect);
    }

    @Override public void glFramebufferParameteri(int target, int pname, int param) {
        GLES32.glFramebufferParameteri(target, pname, param);
    }

    @Override public void glGetFramebufferParameteriv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        GLES32.glGetFramebufferParameteriv(target, pname, params);
    }

    @Override public void glGetProgramInterfaceiv(int program, int programInterface, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        GLES32.glGetProgramInterfaceiv(program, programInterface, pname, params);
    }

    @Override public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        return GLES32.glGetProgramResourceIndex(program, programInterface, name);
    }

    @Override public String glGetProgramResourceName(int program, int programInterface, int index) {
        return GLES32.glGetProgramResourceName(program, programInterface, index);
    }

    @Override public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, int[] props, int propsOffset, int bufSize, int[] length, int lengthOffset, int[] params, int paramsOffset) {
        throw new LazyException();
    }

    @Override public void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, IntBuffer props, int bufSize, IntBuffer length, IntBuffer params) {
        throw new LazyException();
    }

    @Override public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        return GLES32.glGetProgramResourceLocation(program, programInterface, name);
    }

    @Override public void glUseProgramStages(int pipeline, int stages, int program) {
        GLES32.glUseProgramStages(pipeline, stages, program);
    }

    @Override public void glActiveShaderProgram(int pipeline, int program) {
        GLES32.glActiveShaderProgram(pipeline, program);
    }

    @Override public int glCreateShaderProgramv(int type, String[] strings) {
        return GLES32.glCreateShaderProgramv(type, strings);
    }

    @Override public void glBindProgramPipeline(int pipeline) {
        GLES32.glBindProgramPipeline(pipeline);
    }

    @Override public void glDeleteProgramPipelines(int n, int[] pipelines, int offset) {
        throw new LazyException();
    }

    @Override public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {
        throw new LazyException();
    }

    @Override public void glGenProgramPipelines(int n, int[] pipelines, int offset) {
        throw new LazyException();
    }

    @Override public void glGenProgramPipelines(int n, IntBuffer pipelines) {
        throw new LazyException();
    }

    @Override public boolean glIsProgramPipeline(int pipeline) {
        return GLES32.glIsProgramPipeline(pipeline);
    }

    @Override public void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        GLES32.glGetProgramPipelineiv(pipeline, pname, params);
    }

    @Override public void glProgramUniform1i(int program, int location, int v0) {
        GLES32.glProgramUniform1i(program, location, v0);
    }

    @Override public void glProgramUniform2i(int program, int location, int v0, int v1) {
        GLES32.glProgramUniform2i(program, location, v0, v1);
    }

    @Override public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        GLES32.glProgramUniform3i(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        GLES32.glProgramUniform4i(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1ui(int program, int location, int v0) {
        GLES32.glProgramUniform1ui(program, location, v0);
    }

    @Override public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        GLES32.glProgramUniform2ui(program, location, v0, v1);
    }

    @Override public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        GLES32.glProgramUniform3ui(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        GLES32.glProgramUniform4ui(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1f(int program, int location, float v0) {
        GLES32.glProgramUniform1f(program, location, v0);
    }

    @Override public void glProgramUniform2f(int program, int location, float v0, float v1) {
        GLES32.glProgramUniform2f(program, location, v0, v1);
    }

    @Override public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        GLES32.glProgramUniform3f(program, location, v0, v1, v2);
    }

    @Override public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        GLES32.glProgramUniform4f(program, location, v0, v1, v2, v3);
    }

    @Override public void glProgramUniform1iv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform1iv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2iv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2iv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3iv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3iv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4iv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4iv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform1uiv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform1uiv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2uiv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2uiv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3uiv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3uiv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4uiv(int program, int location, int count, int[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4uiv(int program, int location, int count, IntBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform1fv(int program, int location, int count, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform1fv(int program, int location, int count, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2fv(int program, int location, int count, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform2fv(int program, int location, int count, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3fv(int program, int location, int count, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform3fv(int program, int location, int count, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4fv(int program, int location, int count, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniform4fv(int program, int location, int count, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, float[] value, int offset) {
        throw new LazyException();
    }

    @Override public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, FloatBuffer value) {
        throw new LazyException();
    }

    @Override public void glValidateProgramPipeline(int pipeline) {
        GLES32.glValidateProgramPipeline(pipeline);
    }

    @Override public String glGetProgramPipelineInfoLog(int program) {
        return GLES32.glGetProgramPipelineInfoLog(program);
    }

    @Override public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        GLES32.glBindImageTexture(unit, texture, level, layered, layer, access, format);
    }

    @Override public void glGetBooleani_v(int target, int index, boolean[] data, int offset) {
        throw new LazyException();
    }

    @Override public void glGetBooleani_v(int target, int index, IntBuffer data) {
        throw new LazyException();
    }

    @Override public void glMemoryBarrier(int barriers) {
        GLES32.glMemoryBarrier(barriers);
    }

    @Override public void glMemoryBarrierByRegion(int barriers) {
        GLES32.glMemoryBarrierByRegion(barriers);
    }

    @Override public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        GLES32.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
    }

    @Override public void glGetMultisamplefv(int pname, int index, float[] val, int offset) {
        throw new LazyException();
    }

    @Override public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        GLES32.glGetMultisamplefv(pname, index, val);
    }

    @Override public void glSampleMaski(int maskNumber, int mask) {
        GLES32.glSampleMaski(maskNumber, mask);
    }

    @Override public void glGetTexLevelParameteriv(int target, int level, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        GLES32.glGetTexLevelParameteriv(target, level, pname, params);
    }

    @Override public void glGetTexLevelParameterfv(int target, int level, int pname, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        GLES32.glGetTexLevelParameterfv(target, level, pname, params);
    }

    @Override public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        GLES32.glBindVertexBuffer(bindingindex, buffer, offset, stride);
    }

    @Override public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        GLES32.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
    }

    @Override public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        GLES32.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
    }

    @Override public void glVertexAttribBinding(int attribindex, int bindingindex) {
        GLES32.glVertexAttribBinding(attribindex, bindingindex);
    }

    @Override public void glVertexBindingDivisor(int bindingindex, int divisor) {
        GLES32.glVertexBindingDivisor(bindingindex, divisor);
    }

    @Override public void glBlendBarrier() {
        GLES32.glBlendBarrier();
    }

    @Override public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        GLES32.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth);
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, int[] ids, int offset, boolean enabled) {
        throw new LazyException();
    }

    @Override public void glDebugMessageControl(int source, int type, int severity, int count, IntBuffer ids, boolean enabled) {
        throw new LazyException();
    }

    @Override public void glDebugMessageInsert(int source, int type, int id, int severity, int length, String buf) {
        throw new LazyException();
    }

    @Override public void glDebugMessageCallback(DebugProc callback) {
        GLES32.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> callback.onMessage(source, type, id, severity, GLDebugMessageCallback.getMessage(length, message)), NULL);
    }

    @Override public int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset, int[] lengths, int lengthsOffset, byte[] messageLog, int messageLogOffset) {
        throw new LazyException();
    }

    @Override public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        return GLES32.glGetDebugMessageLog(count, sources, types, ids, severities, lengths, messageLog);
    }

    @Override public String[] glGetDebugMessageLog(int count, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset) {
        throw new LazyException();
    }

    @Override public String[] glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities) {
        throw new LazyException();
    }

    @Override public void glPushDebugGroup(int source, int id, int length, String message) {
        throw new LazyException();
    }

    @Override public void glPopDebugGroup() {
        GLES32.glPopDebugGroup();
    }

    @Override public void glObjectLabel(int identifier, int name, int length, String label) {
        throw new LazyException();
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
        throw new LazyException();
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
        throw new LazyException();
    }

    @Override public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        throw new LazyException();
    }

    @Override public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        throw new LazyException();
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
        throw new LazyException();
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, float[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetnUniformfv(int program, int location, int bufSize, FloatBuffer params) {
        throw new LazyException();
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetnUniformiv(int program, int location, int bufSize, IntBuffer params) {
        throw new LazyException();
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetnUniformuiv(int program, int location, int bufSize, IntBuffer params) {
        throw new LazyException();
    }

    @Override public void glMinSampleShading(float value) {
        GLES32.glMinSampleShading(value);
    }

    @Override public void glPatchParameteri(int pname, int value) {
        GLES32.glPatchParameteri(pname, value);
    }

    @Override public void glTexParameterIiv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        GLES32.glTexParameterIiv(target, pname, params);
    }

    @Override public void glTexParameterIuiv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        GLES32.glTexParameterIuiv(target, pname, params);
    }

    @Override public void glGetTexParameterIiv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        GLES32.glGetTexParameterIiv(target, pname, params);
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        GLES32.glGetTexParameterIuiv(target, pname, params);
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, int[] param, int offset) {
        throw new LazyException();
    }

    @Override public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        GLES32.glSamplerParameterIiv(sampler, pname, param);
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, int[] param, int offset) {
        throw new LazyException();
    }

    @Override public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        GLES32.glSamplerParameterIuiv(sampler, pname, param);
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int offset) {
        throw new LazyException();
    }

    @Override public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        GLES32.glGetSamplerParameterIiv(sampler, pname, params);
    }

    @Override public void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int offset) {
        throw new LazyException();
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

    private static class LazyException extends RuntimeException {
    }
}
