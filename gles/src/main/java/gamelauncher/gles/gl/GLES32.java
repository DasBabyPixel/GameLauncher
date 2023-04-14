package gamelauncher.gles.gl;

import de.dasbabypixel.annotations.Api;

@Api
@Deprecated
public interface GLES32 extends GLES31 {
    @Api
    int GL_CONTEXT_FLAG_DEBUG_BIT = 0x00000002;

    @Api
    int GL_CONTEXT_FLAG_ROBUST_ACCESS_BIT = 0x00000004;

    @Api
    int GL_GEOMETRY_SHADER_BIT = 0x00000004;
    @Api
    int GL_TESS_CONTROL_SHADER_BIT = 0x00000008;
    @Api
    int GL_TESS_EVALUATION_SHADER_BIT = 0x00000010;

    @Api
    int GL_QUADS = 0x0007;
    @Api
    int GL_LINES_ADJACENCY = 0x000A;
    @Api
    int GL_LINE_STRIP_ADJACENCY = 0x000B;
    @Api
    int GL_TRIANGLES_ADJACENCY = 0x000C;
    @Api
    int GL_TRIANGLE_STRIP_ADJACENCY = 0x000D;
    @Api
    int GL_PATCHES = 0x000E;
    @Api
    int GL_STACK_OVERFLOW = 0x0503;
    @Api
    int GL_STACK_UNDERFLOW = 0x0504;
    @Api
    int GL_CONTEXT_LOST = 0x0507;
    @Api
    int GL_TEXTURE_BORDER_COLOR = 0x1004;
    @Api
    int GL_VERTEX_ARRAY = 0x8074;
    @Api
    int GL_CLAMP_TO_BORDER = 0x812D;
    @Api
    int GL_CONTEXT_FLAGS = 0x821E;
    @Api
    int GL_PRIMITIVE_RESTART_FOR_PATCHES_SUPPORTED = 0x8221;
    @Api
    int GL_DEBUG_OUTPUT_SYNCHRONOUS = 0x8242;
    @Api
    int GL_DEBUG_NEXT_LOGGED_MESSAGE_LENGTH = 0x8243;
    @Api
    int GL_DEBUG_CALLBACK_FUNCTION = 0x8244;
    @Api
    int GL_DEBUG_CALLBACK_USER_PARAM = 0x8245;
    @Api
    int GL_DEBUG_SOURCE_API = 0x8246;
    @Api
    int GL_DEBUG_SOURCE_WINDOW_SYSTEM = 0x8247;
    @Api
    int GL_DEBUG_SOURCE_SHADER_COMPILER = 0x8248;
    @Api
    int GL_DEBUG_SOURCE_THIRD_PARTY = 0x8249;
    @Api
    int GL_DEBUG_SOURCE_APPLICATION = 0x824A;
    @Api
    int GL_DEBUG_SOURCE_OTHER = 0x824B;
    @Api
    int GL_DEBUG_TYPE_ERROR = 0x824C;
    @Api
    int GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR = 0x824D;
    @Api
    int GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR = 0x824E;
    @Api
    int GL_DEBUG_TYPE_PORTABILITY = 0x824F;
    @Api
    int GL_DEBUG_TYPE_PERFORMANCE = 0x8250;
    @Api
    int GL_DEBUG_TYPE_OTHER = 0x8251;
    @Api
    int GL_LOSE_CONTEXT_ON_RESET = 0x8252;
    @Api
    int GL_GUILTY_CONTEXT_RESET = 0x8253;
    @Api
    int GL_INNOCENT_CONTEXT_RESET = 0x8254;
    @Api
    int GL_UNKNOWN_CONTEXT_RESET = 0x8255;
    @Api
    int GL_RESET_NOTIFICATION_STRATEGY = 0x8256;
    @Api
    int GL_LAYER_PROVOKING_VERTEX = 0x825E;
    @Api
    int GL_UNDEFINED_VERTEX = 0x8260;
    @Api
    int GL_NO_RESET_NOTIFICATION = 0x8261;
    @Api
    int GL_DEBUG_TYPE_MARKER = 0x8268;
    @Api
    int GL_DEBUG_TYPE_PUSH_GROUP = 0x8269;
    @Api
    int GL_DEBUG_TYPE_POP_GROUP = 0x826A;
    @Api
    int GL_DEBUG_SEVERITY_NOTIFICATION = 0x826B;
    @Api
    int GL_MAX_DEBUG_GROUP_STACK_DEPTH = 0x826C;
    @Api
    int GL_DEBUG_GROUP_STACK_DEPTH = 0x826D;
    @Api
    int GL_BUFFER = 0x82E0;
    @Api
    int GL_SHADER = 0x82E1;
    @Api
    int GL_PROGRAM = 0x82E2;
    @Api
    int GL_QUERY = 0x82E3;
    @Api
    int GL_PROGRAM_PIPELINE = 0x82E4;
    @Api
    int GL_SAMPLER = 0x82E6;
    @Api
    int GL_MAX_LABEL_LENGTH = 0x82E8;
    @Api
    int GL_MAX_TESS_CONTROL_INPUT_COMPONENTS = 0x886C;
    @Api
    int GL_MAX_TESS_EVALUATION_INPUT_COMPONENTS = 0x886D;
    @Api
    int GL_GEOMETRY_SHADER_INVOCATIONS = 0x887F;
    @Api
    int GL_GEOMETRY_VERTICES_OUT = 0x8916;
    @Api
    int GL_GEOMETRY_INPUT_TYPE = 0x8917;
    @Api
    int GL_GEOMETRY_OUTPUT_TYPE = 0x8918;
    @Api
    int GL_MAX_GEOMETRY_UNIFORM_BLOCKS = 0x8A2C;
    @Api
    int GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS = 0x8A32;
    @Api
    int GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS = 0x8C29;
    @Api
    int GL_TEXTURE_BUFFER = 0x8C2A;
    @Api
    int GL_TEXTURE_BUFFER_BINDING = 0x8C2A;
    @Api
    int GL_MAX_TEXTURE_BUFFER_SIZE = 0x8C2B;
    @Api
    int GL_TEXTURE_BINDING_BUFFER = 0x8C2C;
    @Api
    int GL_TEXTURE_BUFFER_DATA_STORE_BINDING = 0x8C2D;
    @Api
    int GL_SAMPLE_SHADING = 0x8C36;
    @Api
    int GL_MIN_SAMPLE_SHADING_VALUE = 0x8C37;
    @Api
    int GL_PRIMITIVES_GENERATED = 0x8C87;
    @Api
    int GL_FRAMEBUFFER_ATTACHMENT_LAYERED = 0x8DA7;
    @Api
    int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS = 0x8DA8;
    @Api
    int GL_SAMPLER_BUFFER = 0x8DC2;
    @Api
    int GL_INT_SAMPLER_BUFFER = 0x8DD0;
    @Api
    int GL_UNSIGNED_INT_SAMPLER_BUFFER = 0x8DD8;
    @Api
    int GL_GEOMETRY_SHADER = 0x8DD9;
    @Api
    int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS = 0x8DDF;
    @Api
    int GL_MAX_GEOMETRY_OUTPUT_VERTICES = 0x8DE0;
    @Api
    int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS = 0x8DE1;
    @Api
    int GL_MAX_COMBINED_TESS_CONTROL_UNIFORM_COMPONENTS = 0x8E1E;
    @Api
    int GL_MAX_COMBINED_TESS_EVALUATION_UNIFORM_COMPONENTS = 0x8E1F;
    @Api
    int GL_FIRST_VERTEX_CONVENTION = 0x8E4D;
    @Api
    int GL_LAST_VERTEX_CONVENTION = 0x8E4E;
    @Api
    int GL_MAX_GEOMETRY_SHADER_INVOCATIONS = 0x8E5A;
    @Api
    int GL_MIN_FRAGMENT_INTERPOLATION_OFFSET = 0x8E5B;
    @Api
    int GL_MAX_FRAGMENT_INTERPOLATION_OFFSET = 0x8E5C;
    @Api
    int GL_FRAGMENT_INTERPOLATION_OFFSET_BITS = 0x8E5D;
    @Api
    int GL_PATCH_VERTICES = 0x8E72;
    @Api
    int GL_TESS_CONTROL_OUTPUT_VERTICES = 0x8E75;
    @Api
    int GL_TESS_GEN_MODE = 0x8E76;
    @Api
    int GL_TESS_GEN_SPACING = 0x8E77;
    @Api
    int GL_TESS_GEN_VERTEX_ORDER = 0x8E78;
    @Api
    int GL_TESS_GEN_POINT_MODE = 0x8E79;
    @Api
    int GL_ISOLINES = 0x8E7A;
    @Api
    int GL_FRACTIONAL_ODD = 0x8E7B;
    @Api
    int GL_FRACTIONAL_EVEN = 0x8E7C;
    @Api
    int GL_MAX_PATCH_VERTICES = 0x8E7D;
    @Api
    int GL_MAX_TESS_GEN_LEVEL = 0x8E7E;
    @Api
    int GL_MAX_TESS_CONTROL_UNIFORM_COMPONENTS = 0x8E7F;
    @Api
    int GL_MAX_TESS_EVALUATION_UNIFORM_COMPONENTS = 0x8E80;
    @Api
    int GL_MAX_TESS_CONTROL_TEXTURE_IMAGE_UNITS = 0x8E81;
    @Api
    int GL_MAX_TESS_EVALUATION_TEXTURE_IMAGE_UNITS = 0x8E82;
    @Api
    int GL_MAX_TESS_CONTROL_OUTPUT_COMPONENTS = 0x8E83;
    @Api
    int GL_MAX_TESS_PATCH_COMPONENTS = 0x8E84;
    @Api
    int GL_MAX_TESS_CONTROL_TOTAL_OUTPUT_COMPONENTS = 0x8E85;
    @Api
    int GL_MAX_TESS_EVALUATION_OUTPUT_COMPONENTS = 0x8E86;
    @Api
    int GL_TESS_EVALUATION_SHADER = 0x8E87;
    @Api
    int GL_TESS_CONTROL_SHADER = 0x8E88;
    @Api
    int GL_MAX_TESS_CONTROL_UNIFORM_BLOCKS = 0x8E89;
    @Api
    int GL_MAX_TESS_EVALUATION_UNIFORM_BLOCKS = 0x8E8A;
    @Api
    int GL_TEXTURE_CUBE_MAP_ARRAY = 0x9009;
    @Api
    int GL_TEXTURE_BINDING_CUBE_MAP_ARRAY = 0x900A;
    @Api
    int GL_SAMPLER_CUBE_MAP_ARRAY = 0x900C;
    @Api
    int GL_SAMPLER_CUBE_MAP_ARRAY_SHADOW = 0x900D;
    @Api
    int GL_INT_SAMPLER_CUBE_MAP_ARRAY = 0x900E;
    @Api
    int GL_UNSIGNED_INT_SAMPLER_CUBE_MAP_ARRAY = 0x900F;
    @Api
    int GL_IMAGE_BUFFER = 0x9051;
    @Api
    int GL_IMAGE_CUBE_MAP_ARRAY = 0x9054;
    @Api
    int GL_INT_IMAGE_BUFFER = 0x905C;
    @Api
    int GL_INT_IMAGE_CUBE_MAP_ARRAY = 0x905F;
    @Api
    int GL_UNSIGNED_INT_IMAGE_BUFFER = 0x9067;
    @Api
    int GL_UNSIGNED_INT_IMAGE_CUBE_MAP_ARRAY = 0x906A;
    @Api
    int GL_MAX_TESS_CONTROL_IMAGE_UNIFORMS = 0x90CB;
    @Api
    int GL_MAX_TESS_EVALUATION_IMAGE_UNIFORMS = 0x90CC;
    @Api
    int GL_MAX_GEOMETRY_IMAGE_UNIFORMS = 0x90CD;
    @Api
    int GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS = 0x90D7;
    @Api
    int GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS = 0x90D8;
    @Api
    int GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS = 0x90D9;
    @Api
    int GL_TEXTURE_2D_MULTISAMPLE_ARRAY = 0x9102;
    @Api
    int GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY = 0x9105;
    @Api
    int GL_SAMPLER_2D_MULTISAMPLE_ARRAY = 0x910B;
    @Api
    int GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 0x910C;
    @Api
    int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 0x910D;
    @Api
    int GL_MAX_GEOMETRY_INPUT_COMPONENTS = 0x9123;
    @Api
    int GL_MAX_GEOMETRY_OUTPUT_COMPONENTS = 0x9124;
    @Api
    int GL_MAX_DEBUG_MESSAGE_LENGTH = 0x9143;
    @Api
    int GL_MAX_DEBUG_LOGGED_MESSAGES = 0x9144;
    @Api
    int GL_DEBUG_LOGGED_MESSAGES = 0x9145;
    @Api
    int GL_DEBUG_SEVERITY_HIGH = 0x9146;
    @Api
    int GL_DEBUG_SEVERITY_MEDIUM = 0x9147;
    @Api
    int GL_DEBUG_SEVERITY_LOW = 0x9148;
    @Api
    int GL_TEXTURE_BUFFER_OFFSET = 0x919D;
    @Api
    int GL_TEXTURE_BUFFER_SIZE = 0x919E;
    @Api
    int GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT = 0x919F;
    @Api
    int GL_MULTIPLY = 0x9294;
    @Api
    int GL_SCREEN = 0x9295;
    @Api
    int GL_OVERLAY = 0x9296;
    @Api
    int GL_DARKEN = 0x9297;
    @Api
    int GL_LIGHTEN = 0x9298;
    @Api
    int GL_COLORDODGE = 0x9299;
    @Api
    int GL_COLORBURN = 0x929A;
    @Api
    int GL_HARDLIGHT = 0x929B;
    @Api
    int GL_SOFTLIGHT = 0x929C;
    @Api
    int GL_DIFFERENCE = 0x929E;
    @Api
    int GL_EXCLUSION = 0x92A0;
    @Api
    int GL_HSL_HUE = 0x92AD;
    @Api
    int GL_HSL_SATURATION = 0x92AE;
    @Api
    int GL_HSL_COLOR = 0x92AF;
    @Api
    int GL_HSL_LUMINOSITY = 0x92B0;
    @Api
    int GL_PRIMITIVE_BOUNDING_BOX = 0x92BE;
    @Api
    int GL_MAX_TESS_CONTROL_ATOMIC_COUNTER_BUFFERS = 0x92CD;
    @Api
    int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTER_BUFFERS = 0x92CE;
    @Api
    int GL_MAX_GEOMETRY_ATOMIC_COUNTER_BUFFERS = 0x92CF;
    @Api
    int GL_MAX_TESS_CONTROL_ATOMIC_COUNTERS = 0x92D3;
    @Api
    int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTERS = 0x92D4;
    @Api
    int GL_MAX_GEOMETRY_ATOMIC_COUNTERS = 0x92D5;
    @Api
    int GL_DEBUG_OUTPUT = 0x92E0;
    @Api
    int GL_IS_PER_PATCH = 0x92E7;
    @Api
    int GL_REFERENCED_BY_TESS_CONTROL_SHADER = 0x9307;
    @Api
    int GL_REFERENCED_BY_TESS_EVALUATION_SHADER = 0x9308;
    @Api
    int GL_REFERENCED_BY_GEOMETRY_SHADER = 0x9309;
    @Api
    int GL_FRAMEBUFFER_DEFAULT_LAYERS = 0x9312;
    @Api
    int GL_MAX_FRAMEBUFFER_LAYERS = 0x9317;
    @Api
    int GL_MULTISAMPLE_LINE_WIDTH_RANGE = 0x9381;
    @Api
    int GL_MULTISAMPLE_LINE_WIDTH_GRANULARITY = 0x9382;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_4x4 = 0x93B0;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_5x4 = 0x93B1;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_5x5 = 0x93B2;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_6x5 = 0x93B3;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_6x6 = 0x93B4;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_8x5 = 0x93B5;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_8x6 = 0x93B6;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_8x8 = 0x93B7;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_10x5 = 0x93B8;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_10x6 = 0x93B9;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_10x8 = 0x93BA;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_10x10 = 0x93BB;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_12x10 = 0x93BC;
    @Api
    int GL_COMPRESSED_RGBA_ASTC_12x12 = 0x93BD;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4 = 0x93D0;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4 = 0x93D1;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5 = 0x93D2;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5 = 0x93D3;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6 = 0x93D4;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5 = 0x93D5;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6 = 0x93D6;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8 = 0x93D7;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5 = 0x93D8;

    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6 = 0x93D9;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8 = 0x93DA;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10 = 0x93DB;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10 = 0x93DC;
    @Api
    int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12 = 0x93DD;

    // C function void glBlendBarrier ( void )

    void glBlendBarrier();

    // C function void glCopyImageSubData ( GLuint srcName, GLenum srcTarget, GLint srcLevel, GLint srcX, GLint srcY, GLint srcZ, GLuint dstName, GLenum dstTarget, GLint dstLevel, GLint dstX, GLint dstY, GLint dstZ, GLsizei srcWidth, GLsizei srcHeight, GLsizei srcDepth )

    void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth);

    // C function void glDebugMessageControl ( GLenum source, GLenum type, GLenum severity, GLsizei count, const GLuint *ids, GLboolean enabled )

    void glDebugMessageControl(int source, int type, int severity, int count, int[] ids, int offset, boolean enabled);

    // C function void glDebugMessageControl ( GLenum source, GLenum type, GLenum severity, GLsizei count, const GLuint *ids, GLboolean enabled )

    void glDebugMessageControl(int source, int type, int severity, int count, java.nio.IntBuffer ids, boolean enabled);

    // C function void glDebugMessageInsert ( GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar *buf )

    void glDebugMessageInsert(int source, int type, int id, int severity, int length, String buf);

    // C function void glDebugMessageCallback ( GLDEBUGPROC callback, const void *userParam )

    void glDebugMessageCallback(DebugProc callback);

    int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset, int[] lengths, int lengthsOffset, byte[] messageLog, int messageLogOffset);

    // C function GLuint glGetDebugMessageLog ( GLuint count, GLsizei bufSize, GLenum *sources, GLenum *types, GLuint *ids, GLenum *severities, GLsizei *lengths, GLchar *messageLog )

    int glGetDebugMessageLog(int count, java.nio.IntBuffer sources, java.nio.IntBuffer types, java.nio.IntBuffer ids, java.nio.IntBuffer severities, java.nio.IntBuffer lengths, java.nio.ByteBuffer messageLog);

    // C function GLuint glGetDebugMessageLog ( GLuint count, GLsizei bufSize, GLenum *sources, GLenum *types, GLuint *ids, GLenum *severities, GLsizei *lengths, GLchar *messageLog )

    String[] glGetDebugMessageLog(int count, int[] sources, int sourcesOffset, int[] types, int typesOffset, int[] ids, int idsOffset, int[] severities, int severitiesOffset);

    // C function GLuint glGetDebugMessageLog ( GLuint count, GLsizei bufSize, GLenum *sources, GLenum *types, GLuint *ids, GLenum *severities, GLsizei *lengths, GLchar *messageLog )

    String[] glGetDebugMessageLog(int count, java.nio.IntBuffer sources, java.nio.IntBuffer types, java.nio.IntBuffer ids, java.nio.IntBuffer severities);

    // C function GLuint glGetDebugMessageLog ( GLuint count, GLsizei bufSize, GLenum *sources, GLenum *types, GLuint *ids, GLenum *severities, GLsizei *lengths, GLchar *messageLog )

    void glPushDebugGroup(int source, int id, int length, String message);

    // C function void glPushDebugGroup ( GLenum source, GLuint id, GLsizei length, const GLchar *message )

    void glPopDebugGroup();

    // C function void glPopDebugGroup ( void )

    void glObjectLabel(int identifier, int name, int length, String label);

    // C function void glObjectLabel ( GLenum identifier, GLuint name, GLsizei length, const GLchar *label )

    String glGetObjectLabel(int identifier, int name);

    // C function void glGetObjectLabel ( GLenum identifier, GLuint name, GLsizei bufSize, GLsizei *length, GLchar *label )

    void glObjectPtrLabel(long ptr, String label);

    // C function void glObjectPtrLabel ( const void *ptr, GLsizei length, const GLchar *label )

    String glGetObjectPtrLabel(long ptr);

    // C function void glGetObjectPtrLabel ( const void *ptr, GLsizei bufSize, GLsizei *length, GLchar *label )

    long glGetPointerv(int pname);

    // C function void glGetPointerv ( GLenum pname, void **params )

    void glEnablei(int target, int index);

    // C function void glEnablei ( GLenum target, GLuint index )

    void glDisablei(int target, int index);

    // C function void glDisablei ( GLenum target, GLuint index )

    void glBlendEquationi(int buf, int mode);

    // C function void glBlendEquationi ( GLuint buf, GLenum mode )

    void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha);

    // C function void glBlendEquationSeparatei ( GLuint buf, GLenum modeRGB, GLenum modeAlpha )

    void glBlendFunci(int buf, int src, int dst);

    // C function void glBlendFunci ( GLuint buf, GLenum src, GLenum dst )

    void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    // C function void glBlendFuncSeparatei ( GLuint buf, GLenum srcRGB, GLenum dstRGB, GLenum srcAlpha, GLenum dstAlpha )

    void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a);

    // C function void glColorMaski ( GLuint index, GLboolean r, GLboolean g, GLboolean b, GLboolean a )

    boolean glIsEnabledi(int target, int index);

    // C function GLboolean glIsEnabledi ( GLenum target, GLuint index )

    void glDrawElementsBaseVertex(int mode, int count, int type, java.nio.Buffer indices, int basevertex);

    // C function void glDrawElementsBaseVertex ( GLenum mode, GLsizei count, GLenum type, const void *indices, GLint basevertex )

    void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, java.nio.Buffer indices, int basevertex);

    // C function void glDrawRangeElementsBaseVertex ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum type, const void *indices, GLint basevertex )

    void glDrawElementsInstancedBaseVertex(int mode, int count, int type, java.nio.Buffer indices, int instanceCount, int basevertex);

    // C function void glDrawElementsInstancedBaseVertex ( GLenum mode, GLsizei count, GLenum type, const void *indices, GLsizei instanceCount, GLint basevertex )

    void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex);

    // C function void glDrawElementsInstancedBaseVertex ( GLenum mode, GLsizei count, GLenum type, const void *indices, GLsizei instanceCount, GLint basevertex )

    void glFramebufferTexture(int target, int attachment, int texture, int level);

    // C function void glFramebufferTexture ( GLenum target, GLenum attachment, GLuint texture, GLint level )

    void glPrimitiveBoundingBox(float minX, float minY, float minZ, float minW, float maxX, float maxY, float maxZ, float maxW);

    // C function void glPrimitiveBoundingBox ( GLfloat minX, GLfloat minY, GLfloat minZ, GLfloat minW, GLfloat maxX, GLfloat maxY, GLfloat maxZ, GLfloat maxW )

    int glGetGraphicsResetStatus();

    // C function GLenum glGetGraphicsResetStatus ( void )

    void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, java.nio.Buffer data);

    // C function void glReadnPixels ( GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, GLsizei bufSize, void *data )

    void glGetnUniformfv(int program, int location, int bufSize, float[] params, int offset);

    // C function void glGetnUniformfv ( GLuint program, GLint location, GLsizei bufSize, GLfloat *params )

    void glGetnUniformfv(int program, int location, int bufSize, java.nio.FloatBuffer params);

    // C function void glGetnUniformfv ( GLuint program, GLint location, GLsizei bufSize, GLfloat *params )

    void glGetnUniformiv(int program, int location, int bufSize, int[] params, int offset);

    // C function void glGetnUniformiv ( GLuint program, GLint location, GLsizei bufSize, GLint *params )

    void glGetnUniformiv(int program, int location, int bufSize, java.nio.IntBuffer params);

    // C function void glGetnUniformiv ( GLuint program, GLint location, GLsizei bufSize, GLint *params )

    void glGetnUniformuiv(int program, int location, int bufSize, int[] params, int offset);

    // C function void glGetnUniformuiv ( GLuint program, GLint location, GLsizei bufSize, GLuint *params )

    void glGetnUniformuiv(int program, int location, int bufSize, java.nio.IntBuffer params);

    // C function void glGetnUniformuiv ( GLuint program, GLint location, GLsizei bufSize, GLuint *params )

    void glMinSampleShading(float value);

    // C function void glMinSampleShading ( GLfloat value )

    void glPatchParameteri(int pname, int value);

    // C function void glPatchParameteri ( GLenum pname, GLint value )

    void glTexParameterIiv(int target, int pname, int[] params, int offset);

    // C function void glTexParameterIiv ( GLenum target, GLenum pname, const GLint *params )

    void glTexParameterIiv(int target, int pname, java.nio.IntBuffer params);

    // C function void glTexParameterIiv ( GLenum target, GLenum pname, const GLint *params )

    void glTexParameterIuiv(int target, int pname, int[] params, int offset);

    // C function void glTexParameterIuiv ( GLenum target, GLenum pname, const GLuint *params )

    void glTexParameterIuiv(int target, int pname, java.nio.IntBuffer params);

    // C function void glTexParameterIuiv ( GLenum target, GLenum pname, const GLuint *params )

    void glGetTexParameterIiv(int target, int pname, int[] params, int offset);

    // C function void glGetTexParameterIiv ( GLenum target, GLenum pname, GLint *params )

    void glGetTexParameterIiv(int target, int pname, java.nio.IntBuffer params);

    // C function void glGetTexParameterIiv ( GLenum target, GLenum pname, GLint *params )

    void glGetTexParameterIuiv(int target, int pname, int[] params, int offset);

    // C function void glGetTexParameterIuiv ( GLenum target, GLenum pname, GLuint *params )

    void glGetTexParameterIuiv(int target, int pname, java.nio.IntBuffer params);

    // C function void glGetTexParameterIuiv ( GLenum target, GLenum pname, GLuint *params )

    void glSamplerParameterIiv(int sampler, int pname, int[] param, int offset);

    // C function void glSamplerParameterIiv ( GLuint sampler, GLenum pname, const GLint *param )

    void glSamplerParameterIiv(int sampler, int pname, java.nio.IntBuffer param);

    // C function void glSamplerParameterIiv ( GLuint sampler, GLenum pname, const GLint *param )

    void glSamplerParameterIuiv(int sampler, int pname, int[] param, int offset);

    // C function void glSamplerParameterIuiv ( GLuint sampler, GLenum pname, const GLuint *param )

    void glSamplerParameterIuiv(int sampler, int pname, java.nio.IntBuffer param);

    // C function void glSamplerParameterIuiv ( GLuint sampler, GLenum pname, const GLuint *param )

    void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int offset);

    // C function void glGetSamplerParameterIiv ( GLuint sampler, GLenum pname, GLint *params )

    void glGetSamplerParameterIiv(int sampler, int pname, java.nio.IntBuffer params);

    // C function void glGetSamplerParameterIiv ( GLuint sampler, GLenum pname, GLint *params )

    void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int offset);

    // C function void glGetSamplerParameterIuiv ( GLuint sampler, GLenum pname, GLuint *params )

    void glGetSamplerParameterIuiv(int sampler, int pname, java.nio.IntBuffer params);

    // C function void glGetSamplerParameterIuiv ( GLuint sampler, GLenum pname, GLuint *params )

    void glTexBuffer(int target, int internalformat, int buffer);

    // C function void glTexBuffer ( GLenum target, GLenum internalformat, GLuint buffer )

    void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size);

    // C function void glTexBufferRange ( GLenum target, GLenum internalformat, GLuint buffer, GLintptr offset, GLsizeiptr size )

    void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations);

    // C function void glTexStorage3DMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth, GLboolean fixedsamplelocations )

    interface DebugProc {
        void onMessage(int source, int type, int id, int severity, String message);
    }
}
