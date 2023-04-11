package gamelauncher.gles.gl.unsupported;

import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.gl.GLES32;
import gamelauncher.gles.gl.redirect.RedirectGLES31;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UnsupportedGLES32 extends RedirectGLES31 implements GLES32 {

    public UnsupportedGLES32(GLES31 wrapper) {
        super(wrapper);
    }

    @Override
    public void glBlendBarrier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDebugMessageControl(int source, int type, int severity, int count, int[] ids, int offset, boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDebugMessageControl(int source, int type, int severity, int count, IntBuffer ids, boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDebugMessageInsert(int source, int type, int id, int severity, int length, String buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDebugMessageCallback(DebugProc callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset, int[] lengths, int lengthsOffset, byte[] messageLog, int messageLogOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] glGetDebugMessageLog(int count, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glPushDebugGroup(int source, int id, int length, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glPopDebugGroup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glObjectLabel(int identifier, int name, int length, String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetObjectLabel(int identifier, int name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glObjectPtrLabel(long ptr, String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String glGetObjectPtrLabel(long ptr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long glGetPointerv(int pname) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glEnablei(int target, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDisablei(int target, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBlendEquationi(int buf, int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBlendFunci(int buf, int src, int dst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean glIsEnabledi(int target, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glPrimitiveBoundingBox(float minX, float minY, float minZ, float minW, float maxX, float maxY, float maxZ, float maxW) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int glGetGraphicsResetStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformfv(int program, int location, int bufSize, float[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformfv(int program, int location, int bufSize, FloatBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformiv(int program, int location, int bufSize, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformiv(int program, int location, int bufSize, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformuiv(int program, int location, int bufSize, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetnUniformuiv(int program, int location, int bufSize, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glMinSampleShading(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glPatchParameteri(int pname, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexParameterIiv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexParameterIuiv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexParameterIiv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexParameterIuiv(int target, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterIiv(int sampler, int pname, int[] param, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterIuiv(int sampler, int pname, int[] param, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexBuffer(int target, int internalformat, int buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
        throw new UnsupportedOperationException();
    }
}
