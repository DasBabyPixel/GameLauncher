/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gl.redirect;

import gamelauncher.gles.gl.GLES20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RedirectGLES20 implements GLES20 {
    private final GLES20 wrapper;

    public RedirectGLES20(GLES20 wrapper) {
        this.wrapper = wrapper;
    }

    @Override public void glActiveTexture(int texture) {
        wrapper.glActiveTexture(texture);
    }

    @Override public void glAttachShader(int program, int shader) {
        wrapper.glAttachShader(program, shader);
    }

    @Override public void glBindAttribLocation(int program, int index, String name) {
        wrapper.glBindAttribLocation(program, index, name);
    }

    @Override public void glBindBuffer(int target, int buffer) {
        wrapper.glBindBuffer(target, buffer);
    }

    @Override public void glBindFramebuffer(int target, int framebuffer) {
        wrapper.glBindFramebuffer(target, framebuffer);
    }

    @Override public void glBindRenderbuffer(int target, int renderbuffer) {
        wrapper.glBindRenderbuffer(target, renderbuffer);
    }

    @Override public void glBindTexture(int target, int texture) {
        wrapper.glBindTexture(target, texture);
    }

    @Override public void glBlendColor(float red, float green, float blue, float alpha) {
        wrapper.glBlendColor(red, green, blue, alpha);
    }

    @Override public void glBlendEquation(int mode) {
        wrapper.glBlendEquation(mode);
    }

    @Override public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        wrapper.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override public void glBlendFunc(int sfactor, int dfactor) {
        wrapper.glBlendFunc(sfactor, dfactor);
    }

    @Override public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        wrapper.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override public void glBufferData(int target, long size, Buffer data, int usage) {
        wrapper.glBufferData(target, size, data, usage);
    }

    @Override public void glBufferSubData(int target, int offset, long size, Buffer data) {
        wrapper.glBufferSubData(target, offset, size, data);
    }

    @Override public int glCheckFramebufferStatus(int target) {
        return wrapper.glCheckFramebufferStatus(target);
    }

    @Override public void glClear(int mask) {
        wrapper.glClear(mask);
    }

    @Override public void glClearColor(float red, float green, float blue, float alpha) {
        wrapper.glClearColor(red, green, blue, alpha);
    }

    @Override public void glClearDepthf(float depth) {
        wrapper.glClearDepthf(depth);
    }

    @Override public void glClearStencil(int s) {
        wrapper.glClearStencil(s);
    }

    @Override public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        wrapper.glColorMask(red, green, blue, alpha);
    }

    @Override public void glCompileShader(int shader) {
        wrapper.glCompileShader(shader);
    }

    @Override public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
        wrapper.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
    }

    @Override public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
        wrapper.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
    }

    @Override public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        wrapper.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        wrapper.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override public int glCreateProgram() {
        return wrapper.glCreateProgram();
    }

    @Override public int glCreateShader(int type) {
        return wrapper.glCreateShader(type);
    }

    @Override public void glCullFace(int mode) {
        wrapper.glCullFace(mode);
    }

    @Override public void glDeleteBuffers(int n, int[] buffers, int offset) {
        wrapper.glDeleteBuffers(n, buffers, offset);
    }

    @Override public void glDeleteBuffers(int n, IntBuffer buffers) {
        wrapper.glDeleteBuffers(n, buffers);
    }

    @Override public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        wrapper.glDeleteFramebuffers(n, framebuffers, offset);
    }

    @Override public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        wrapper.glDeleteFramebuffers(n, framebuffers);
    }

    @Override public void glDeleteProgram(int program) {
        wrapper.glDeleteProgram(program);
    }

    @Override public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        wrapper.glDeleteRenderbuffers(n, renderbuffers, offset);
    }

    @Override public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        wrapper.glDeleteRenderbuffers(n, renderbuffers);
    }

    @Override public void glDeleteShader(int shader) {
        wrapper.glDeleteShader(shader);
    }

    @Override public void glDeleteTextures(int n, int[] textures, int offset) {
        wrapper.glDeleteTextures(n, textures, offset);
    }

    @Override public void glDeleteTextures(int n, IntBuffer textures) {
        wrapper.glDeleteTextures(n, textures);
    }

    @Override public void glDepthFunc(int func) {
        wrapper.glDepthFunc(func);
    }

    @Override public void glDepthMask(boolean flag) {
        wrapper.glDepthMask(flag);
    }

    @Override public void glDepthRangef(float zNear, float zFar) {
        wrapper.glDepthRangef(zNear, zFar);
    }

    @Override public void glDetachShader(int program, int shader) {
        wrapper.glDetachShader(program, shader);
    }

    @Override public void glDisable(int cap) {
        wrapper.glDisable(cap);
    }

    @Override public void glDisableVertexAttribArray(int index) {
        wrapper.glDisableVertexAttribArray(index);
    }

    @Override public void glDrawArrays(int mode, int first, int count) {
        wrapper.glDrawArrays(mode, first, count);
    }

    @Override public void glDrawElements(int mode, int count, int type, int offset) {
        wrapper.glDrawElements(mode, count, type, offset);
    }

    @Override public void glDrawElements(int mode, int count, int type, Buffer indices) {
        wrapper.glDrawElements(mode, count, type, indices);
    }

    @Override public void glEnable(int cap) {
        wrapper.glEnable(cap);
    }

    @Override public void glEnableVertexAttribArray(int index) {
        wrapper.glEnableVertexAttribArray(index);
    }

    @Override public void glFinish() {
        wrapper.glFinish();
    }

    @Override public void glFlush() {
        wrapper.glFlush();
    }

    @Override public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        wrapper.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        wrapper.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override public void glFrontFace(int mode) {
        wrapper.glFrontFace(mode);
    }

    @Override public void glGenBuffers(int n, int[] buffers, int offset) {
        wrapper.glGenBuffers(n, buffers, offset);
    }

    @Override public void glGenBuffers(int n, IntBuffer buffers) {
        wrapper.glGenBuffers(n, buffers);
    }

    @Override public void glGenerateMipmap(int target) {
        wrapper.glGenerateMipmap(target);
    }

    @Override public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        wrapper.glGenFramebuffers(n, framebuffers, offset);
    }

    @Override public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        wrapper.glGenFramebuffers(n, framebuffers);
    }

    @Override public void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
        wrapper.glGenRenderbuffers(n, renderbuffers, offset);
    }

    @Override public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        wrapper.glGenRenderbuffers(n, renderbuffers);
    }

    @Override public void glGenTextures(int n, int[] textures, int offset) {
        wrapper.glGenTextures(n, textures, offset);
    }

    @Override public void glGenTextures(int n, IntBuffer textures) {
        wrapper.glGenTextures(n, textures);
    }

    @Override public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        wrapper.glGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override public String glGetActiveAttrib(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        return wrapper.glGetActiveAttrib(program, index, size, sizeOffset, type, typeOffset);
    }

    @Override public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        return wrapper.glGetActiveAttrib(program, index, size, type);
    }

    @Override public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        wrapper.glGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override public String glGetActiveUniform(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        return wrapper.glGetActiveUniform(program, index, size, sizeOffset, type, typeOffset);
    }

    @Override public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        return wrapper.glGetActiveUniform(program, index, size, type);
    }

    @Override public void glGetAttachedShaders(int program, int maxcount, int[] count, int countOffset, int[] shaders, int shadersOffset) {
        wrapper.glGetAttachedShaders(program, maxcount, count, countOffset, shaders, shadersOffset);
    }

    @Override public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
        wrapper.glGetAttachedShaders(program, maxcount, count, shaders);
    }

    @Override public int glGetAttribLocation(int program, String name) {
        return wrapper.glGetAttribLocation(program, name);
    }

    @Override public void glGetBooleanv(int pname, boolean[] params, int offset) {
        wrapper.glGetBooleanv(pname, params, offset);
    }

    @Override public void glGetBooleanv(int pname, IntBuffer params) {
        wrapper.glGetBooleanv(pname, params);
    }

    @Override public void glGetBufferParameteriv(int target, int pname, int[] params, int offset) {
        wrapper.glGetBufferParameteriv(target, pname, params, offset);
    }

    @Override public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        wrapper.glGetBufferParameteriv(target, pname, params);
    }

    @Override public int glGetError() {
        return wrapper.glGetError();
    }

    @Override public void glGetFloatv(int pname, float[] params, int offset) {
        wrapper.glGetFloatv(pname, params, offset);
    }

    @Override public void glGetFloatv(int pname, FloatBuffer params) {
        wrapper.glGetFloatv(pname, params);
    }

    @Override public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, int[] params, int offset) {
        wrapper.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params, offset);
    }

    @Override public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        wrapper.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    @Override public void glGetIntegerv(int pname, int[] params, int offset) {
        wrapper.glGetIntegerv(pname, params, offset);
    }

    @Override public void glGetIntegerv(int pname, IntBuffer params) {
        wrapper.glGetIntegerv(pname, params);
    }

    @Override public void glGetProgramiv(int program, int pname, int[] params, int offset) {
        wrapper.glGetProgramiv(program, pname, params, offset);
    }

    @Override public void glGetProgramiv(int program, int pname, IntBuffer params) {
        wrapper.glGetProgramiv(program, pname, params);
    }

    @Override public String glGetProgramInfoLog(int program) {
        return wrapper.glGetProgramInfoLog(program);
    }

    @Override public void glGetRenderbufferParameteriv(int target, int pname, int[] params, int offset) {
        wrapper.glGetRenderbufferParameteriv(target, pname, params, offset);
    }

    @Override public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        wrapper.glGetRenderbufferParameteriv(target, pname, params);
    }

    @Override public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        wrapper.glGetShaderiv(shader, pname, params, offset);
    }

    @Override public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        wrapper.glGetShaderiv(shader, pname, params);
    }

    @Override public String glGetShaderInfoLog(int shader) {
        return wrapper.glGetShaderInfoLog(shader);
    }

    @Override public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range, int rangeOffset, int[] precision, int precisionOffset) {
        wrapper.glGetShaderPrecisionFormat(shadertype, precisiontype, range, rangeOffset, precision, precisionOffset);
    }

    @Override public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        wrapper.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
    }

    @Override public void glGetShaderSource(int shader, int bufsize, int[] length, int lengthOffset, byte[] source, int sourceOffset) {
        wrapper.glGetShaderSource(shader, bufsize, length, lengthOffset, source, sourceOffset);
    }

    @Override public String glGetShaderSource(int shader) {
        return wrapper.glGetShaderSource(shader);
    }

    @Override public String glGetString(int name) {
        return wrapper.glGetString(name);
    }

    @Override public void glGetTexParameterfv(int target, int pname, float[] params, int offset) {
        wrapper.glGetTexParameterfv(target, pname, params, offset);
    }

    @Override public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        wrapper.glGetTexParameterfv(target, pname, params);
    }

    @Override public void glGetTexParameteriv(int target, int pname, int[] params, int offset) {
        wrapper.glGetTexParameteriv(target, pname, params, offset);
    }

    @Override public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        wrapper.glGetTexParameteriv(target, pname, params);
    }

    @Override public void glGetUniformfv(int program, int location, float[] params, int offset) {
        wrapper.glGetUniformfv(program, location, params, offset);
    }

    @Override public void glGetUniformfv(int program, int location, FloatBuffer params) {
        wrapper.glGetUniformfv(program, location, params);
    }

    @Override public void glGetUniformiv(int program, int location, int[] params, int offset) {
        wrapper.glGetUniformiv(program, location, params, offset);
    }

    @Override public void glGetUniformiv(int program, int location, IntBuffer params) {
        wrapper.glGetUniformiv(program, location, params);
    }

    @Override public int glGetUniformLocation(int program, String name) {
        return wrapper.glGetUniformLocation(program, name);
    }

    @Override public void glGetVertexAttribfv(int index, int pname, float[] params, int offset) {
        wrapper.glGetVertexAttribfv(index, pname, params, offset);
    }

    @Override public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        wrapper.glGetVertexAttribfv(index, pname, params);
    }

    @Override public void glGetVertexAttribiv(int index, int pname, int[] params, int offset) {
        wrapper.glGetVertexAttribiv(index, pname, params, offset);
    }

    @Override public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        wrapper.glGetVertexAttribiv(index, pname, params);
    }

    @Override public void glHint(int target, int mode) {
        wrapper.glHint(target, mode);
    }

    @Override public boolean glIsBuffer(int buffer) {
        return wrapper.glIsBuffer(buffer);
    }

    @Override public boolean glIsEnabled(int cap) {
        return wrapper.glIsEnabled(cap);
    }

    @Override public boolean glIsFramebuffer(int framebuffer) {
        return wrapper.glIsFramebuffer(framebuffer);
    }

    @Override public boolean glIsProgram(int program) {
        return wrapper.glIsProgram(program);
    }

    @Override public boolean glIsRenderbuffer(int renderbuffer) {
        return wrapper.glIsRenderbuffer(renderbuffer);
    }

    @Override public boolean glIsShader(int shader) {
        return wrapper.glIsShader(shader);
    }

    @Override public boolean glIsTexture(int texture) {
        return wrapper.glIsTexture(texture);
    }

    @Override public void glLineWidth(float width) {
        wrapper.glLineWidth(width);
    }

    @Override public void glLinkProgram(int program) {
        wrapper.glLinkProgram(program);
    }

    @Override public void glPixelStorei(int pname, int param) {
        wrapper.glPixelStorei(pname, param);
    }

    @Override public void glPolygonOffset(float factor, float units) {
        wrapper.glPolygonOffset(factor, units);
    }

    @Override public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        wrapper.glReadPixels(x, y, width, height, format, type, pixels);
    }

    @Override public void glReleaseShaderCompiler() {
        wrapper.glReleaseShaderCompiler();
    }

    @Override public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        wrapper.glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override public void glSampleCoverage(float value, boolean invert) {
        wrapper.glSampleCoverage(value, invert);
    }

    @Override public void glScissor(int x, int y, int width, int height) {
        wrapper.glScissor(x, y, width, height);
    }

    @Override public void glShaderBinary(int n, int[] shaders, int offset, int binaryformat, Buffer binary, int length) {
        wrapper.glShaderBinary(n, shaders, offset, binaryformat, binary, length);
    }

    @Override public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        wrapper.glShaderBinary(n, shaders, binaryformat, binary, length);
    }

    @Override public void glShaderSource(int shader, String string) {
        wrapper.glShaderSource(shader, string);
    }

    @Override public void glStencilFunc(int func, int ref, int mask) {
        wrapper.glStencilFunc(func, ref, mask);
    }

    @Override public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        wrapper.glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override public void glStencilMask(int mask) {
        wrapper.glStencilMask(mask);
    }

    @Override public void glStencilMaskSeparate(int face, int mask) {
        wrapper.glStencilMaskSeparate(face, mask);
    }

    @Override public void glStencilOp(int fail, int zfail, int zpass) {
        wrapper.glStencilOp(fail, zfail, zpass);
    }

    @Override public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        wrapper.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        wrapper.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
    }

    @Override public void glTexParameterf(int target, int pname, float param) {
        wrapper.glTexParameterf(target, pname, param);
    }

    @Override public void glTexParameterfv(int target, int pname, float[] params, int offset) {
        wrapper.glTexParameterfv(target, pname, params, offset);
    }

    @Override public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        wrapper.glTexParameterfv(target, pname, params);
    }

    @Override public void glTexParameteri(int target, int pname, int param) {
        wrapper.glTexParameteri(target, pname, param);
    }

    @Override public void glTexParameteriv(int target, int pname, int[] params, int offset) {
        wrapper.glTexParameteriv(target, pname, params, offset);
    }

    @Override public void glTexParameteriv(int target, int pname, IntBuffer params) {
        wrapper.glTexParameteriv(target, pname, params);
    }

    @Override public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        wrapper.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
    }

    @Override public void glUniform1f(int location, float x) {
        wrapper.glUniform1f(location, x);
    }

    @Override public void glUniform1fv(int location, int count, float[] v, int offset) {
        wrapper.glUniform1fv(location, count, v, offset);
    }

    @Override public void glUniform1fv(int location, int count, FloatBuffer v) {
        wrapper.glUniform1fv(location, count, v);
    }

    @Override public void glUniform1i(int location, int x) {
        wrapper.glUniform1i(location, x);
    }

    @Override public void glUniform1iv(int location, int count, int[] v, int offset) {
        wrapper.glUniform1iv(location, count, v, offset);
    }

    @Override public void glUniform1iv(int location, int count, IntBuffer v) {
        wrapper.glUniform1iv(location, count, v);
    }

    @Override public void glUniform2f(int location, float x, float y) {
        wrapper.glUniform2f(location, x, y);
    }

    @Override public void glUniform2fv(int location, int count, float[] v, int offset) {
        wrapper.glUniform2fv(location, count, v, offset);
    }

    @Override public void glUniform2fv(int location, int count, FloatBuffer v) {
        wrapper.glUniform2fv(location, count, v);
    }

    @Override public void glUniform2i(int location, int x, int y) {
        wrapper.glUniform2i(location, x, y);
    }

    @Override public void glUniform2iv(int location, int count, int[] v, int offset) {
        wrapper.glUniform2iv(location, count, v, offset);
    }

    @Override public void glUniform2iv(int location, int count, IntBuffer v) {
        wrapper.glUniform2iv(location, count, v);
    }

    @Override public void glUniform3f(int location, float x, float y, float z) {
        wrapper.glUniform3f(location, x, y, z);
    }

    @Override public void glUniform3fv(int location, int count, float[] v, int offset) {
        wrapper.glUniform3fv(location, count, v, offset);
    }

    @Override public void glUniform3fv(int location, int count, FloatBuffer v) {
        wrapper.glUniform3fv(location, count, v);
    }

    @Override public void glUniform3i(int location, int x, int y, int z) {
        wrapper.glUniform3i(location, x, y, z);
    }

    @Override public void glUniform3iv(int location, int count, int[] v, int offset) {
        wrapper.glUniform3iv(location, count, v, offset);
    }

    @Override public void glUniform3iv(int location, int count, IntBuffer v) {
        wrapper.glUniform3iv(location, count, v);
    }

    @Override public void glUniform4f(int location, float x, float y, float z, float w) {
        wrapper.glUniform4f(location, x, y, z, w);
    }

    @Override public void glUniform4fv(int location, int count, float[] v, int offset) {
        wrapper.glUniform4fv(location, count, v, offset);
    }

    @Override public void glUniform4fv(int location, int count, FloatBuffer v) {
        wrapper.glUniform4fv(location, count, v);
    }

    @Override public void glUniform4i(int location, int x, int y, int z, int w) {
        wrapper.glUniform4i(location, x, y, z, w);
    }

    @Override public void glUniform4iv(int location, int count, int[] v, int offset) {
        wrapper.glUniform4iv(location, count, v, offset);
    }

    @Override public void glUniform4iv(int location, int count, IntBuffer v) {
        wrapper.glUniform4iv(location, count, v);
    }

    @Override public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix2fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix2fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix3fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix3fv(location, count, transpose, value);
    }

    @Override public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        wrapper.glUniformMatrix4fv(location, count, transpose, value, offset);
    }

    @Override public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        wrapper.glUniformMatrix4fv(location, count, transpose, value);
    }

    @Override public void glUseProgram(int program) {
        wrapper.glUseProgram(program);
    }

    @Override public void glValidateProgram(int program) {
        wrapper.glValidateProgram(program);
    }

    @Override public void glVertexAttrib1f(int indx, float x) {
        wrapper.glVertexAttrib1f(indx, x);
    }

    @Override public void glVertexAttrib1fv(int indx, float[] values, int offset) {
        wrapper.glVertexAttrib1fv(indx, values, offset);
    }

    @Override public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        wrapper.glVertexAttrib1fv(indx, values);
    }

    @Override public void glVertexAttrib2f(int indx, float x, float y) {
        wrapper.glVertexAttrib2f(indx, x, y);
    }

    @Override public void glVertexAttrib2fv(int indx, float[] values, int offset) {
        wrapper.glVertexAttrib2fv(indx, values, offset);
    }

    @Override public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        wrapper.glVertexAttrib2fv(indx, values);
    }

    @Override public void glVertexAttrib3f(int indx, float x, float y, float z) {
        wrapper.glVertexAttrib3f(indx, x, y, z);
    }

    @Override public void glVertexAttrib3fv(int indx, float[] values, int offset) {
        wrapper.glVertexAttrib3fv(indx, values, offset);
    }

    @Override public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        wrapper.glVertexAttrib3fv(indx, values);
    }

    @Override public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        wrapper.glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override public void glVertexAttrib4fv(int indx, float[] values, int offset) {
        wrapper.glVertexAttrib4fv(indx, values, offset);
    }

    @Override public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        wrapper.glVertexAttrib4fv(indx, values);
    }

    @Override public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
        wrapper.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }

    @Override public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        wrapper.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    @Override public void glViewport(int x, int y, int width, int height) {
        wrapper.glViewport(x, y, width, height);
    }
}
