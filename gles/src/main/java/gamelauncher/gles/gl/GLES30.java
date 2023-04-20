package gamelauncher.gles.gl;

import de.dasbabypixel.annotations.Api;

@Api public interface GLES30 extends GLES20 {
    @Api int GL_READ_BUFFER = 0x0C02;
    @Api int GL_UNPACK_ROW_LENGTH = 0x0CF2;
    @Api int GL_UNPACK_SKIP_ROWS = 0x0CF3;
    @Api int GL_UNPACK_SKIP_PIXELS = 0x0CF4;
    @Api int GL_PACK_ROW_LENGTH = 0x0D02;
    @Api int GL_PACK_SKIP_ROWS = 0x0D03;
    @Api int GL_PACK_SKIP_PIXELS = 0x0D04;
    @Api int GL_COLOR = 0x1800;
    @Api int GL_DEPTH = 0x1801;
    @Api int GL_STENCIL = 0x1802;
    @Api int GL_RED = 0x1903;
    @Api int GL_RGB8 = 0x8051;
    @Api int GL_RGBA8 = 0x8058;
    @Api int GL_RGB10_A2 = 0x8059;
    @Api int GL_TEXTURE_BINDING_3D = 0x806A;
    @Api int GL_UNPACK_SKIP_IMAGES = 0x806D;
    @Api int GL_UNPACK_IMAGE_HEIGHT = 0x806E;
    @Api int GL_TEXTURE_3D = 0x806F;
    @Api int GL_TEXTURE_WRAP_R = 0x8072;
    @Api int GL_MAX_3D_TEXTURE_SIZE = 0x8073;
    @Api int GL_UNSIGNED_INT_2_10_10_10_REV = 0x8368;
    @Api int GL_MAX_ELEMENTS_VERTICES = 0x80E8;
    @Api int GL_MAX_ELEMENTS_INDICES = 0x80E9;
    @Api int GL_TEXTURE_MIN_LOD = 0x813A;
    @Api int GL_TEXTURE_MAX_LOD = 0x813B;
    @Api int GL_TEXTURE_BASE_LEVEL = 0x813C;
    @Api int GL_TEXTURE_MAX_LEVEL = 0x813D;
    @Api int GL_MIN = 0x8007;
    @Api int GL_MAX = 0x8008;
    @Api int GL_DEPTH_COMPONENT24 = 0x81A6;
    @Api int GL_MAX_TEXTURE_LOD_BIAS = 0x84FD;
    @Api int GL_TEXTURE_COMPARE_MODE = 0x884C;
    @Api int GL_TEXTURE_COMPARE_FUNC = 0x884D;
    @Api int GL_CURRENT_QUERY = 0x8865;
    @Api int GL_QUERY_RESULT = 0x8866;
    @Api int GL_QUERY_RESULT_AVAILABLE = 0x8867;
    @Api int GL_BUFFER_MAPPED = 0x88BC;
    @Api int GL_BUFFER_MAP_POINTER = 0x88BD;
    @Api int GL_STREAM_READ = 0x88E1;
    @Api int GL_STREAM_COPY = 0x88E2;
    @Api int GL_STATIC_READ = 0x88E5;
    @Api int GL_STATIC_COPY = 0x88E6;
    @Api int GL_DYNAMIC_READ = 0x88E9;
    @Api int GL_DYNAMIC_COPY = 0x88EA;
    @Api int GL_MAX_DRAW_BUFFERS = 0x8824;
    @Api int GL_DRAW_BUFFER0 = 0x8825;
    @Api int GL_DRAW_BUFFER1 = 0x8826;
    @Api int GL_DRAW_BUFFER2 = 0x8827;
    @Api int GL_DRAW_BUFFER3 = 0x8828;
    @Api int GL_DRAW_BUFFER4 = 0x8829;
    @Api int GL_DRAW_BUFFER5 = 0x882A;
    @Api int GL_DRAW_BUFFER6 = 0x882B;
    @Api int GL_DRAW_BUFFER7 = 0x882C;
    @Api int GL_DRAW_BUFFER8 = 0x882D;
    @Api int GL_DRAW_BUFFER9 = 0x882E;
    @Api int GL_DRAW_BUFFER10 = 0x882F;
    @Api int GL_DRAW_BUFFER11 = 0x8830;
    @Api int GL_DRAW_BUFFER12 = 0x8831;
    @Api int GL_DRAW_BUFFER13 = 0x8832;
    @Api int GL_DRAW_BUFFER14 = 0x8833;
    @Api int GL_DRAW_BUFFER15 = 0x8834;
    @Api int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 0x8B49;
    @Api int GL_MAX_VERTEX_UNIFORM_COMPONENTS = 0x8B4A;
    @Api int GL_SAMPLER_3D = 0x8B5F;
    @Api int GL_SAMPLER_2D_SHADOW = 0x8B62;
    @Api int GL_FRAGMENT_SHADER_DERIVATIVE_HINT = 0x8B8B;
    @Api int GL_PIXEL_PACK_BUFFER = 0x88EB;
    @Api int GL_PIXEL_UNPACK_BUFFER = 0x88EC;
    @Api int GL_PIXEL_PACK_BUFFER_BINDING = 0x88ED;
    @Api int GL_PIXEL_UNPACK_BUFFER_BINDING = 0x88EF;
    @Api int GL_FLOAT_MAT2x3 = 0x8B65;
    @Api int GL_FLOAT_MAT2x4 = 0x8B66;
    @Api int GL_FLOAT_MAT3x2 = 0x8B67;
    @Api int GL_FLOAT_MAT3x4 = 0x8B68;
    @Api int GL_FLOAT_MAT4x2 = 0x8B69;
    @Api int GL_FLOAT_MAT4x3 = 0x8B6A;
    @Api int GL_SRGB = 0x8C40;
    @Api int GL_SRGB8 = 0x8C41;
    @Api int GL_SRGB8_ALPHA8 = 0x8C43;
    @Api int GL_COMPARE_REF_TO_TEXTURE = 0x884E;
    @Api int GL_MAJOR_VERSION = 0x821B;
    @Api int GL_MINOR_VERSION = 0x821C;
    @Api int GL_NUM_EXTENSIONS = 0x821D;
    @Api int GL_RGBA32F = 0x8814;
    @Api int GL_RGB32F = 0x8815;
    @Api int GL_RGBA16F = 0x881A;
    @Api int GL_RGB16F = 0x881B;
    @Api int GL_VERTEX_ATTRIB_ARRAY_INTEGER = 0x88FD;
    @Api int GL_MAX_ARRAY_TEXTURE_LAYERS = 0x88FF;
    @Api int GL_MIN_PROGRAM_TEXEL_OFFSET = 0x8904;
    @Api int GL_MAX_PROGRAM_TEXEL_OFFSET = 0x8905;
    @Api int GL_MAX_VARYING_COMPONENTS = 0x8B4B;
    @Api int GL_TEXTURE_2D_ARRAY = 0x8C1A;
    @Api int GL_TEXTURE_BINDING_2D_ARRAY = 0x8C1D;
    @Api int GL_R11F_G11F_B10F = 0x8C3A;
    @Api int GL_UNSIGNED_INT_10F_11F_11F_REV = 0x8C3B;
    @Api int GL_RGB9_E5 = 0x8C3D;
    @Api int GL_UNSIGNED_INT_5_9_9_9_REV = 0x8C3E;
    @Api int GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH = 0x8C76;
    @Api int GL_TRANSFORM_FEEDBACK_BUFFER_MODE = 0x8C7F;
    @Api int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS = 0x8C80;
    @Api int GL_TRANSFORM_FEEDBACK_VARYINGS = 0x8C83;
    @Api int GL_TRANSFORM_FEEDBACK_BUFFER_START = 0x8C84;
    @Api int GL_TRANSFORM_FEEDBACK_BUFFER_SIZE = 0x8C85;
    @Api int GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN = 0x8C88;
    @Api int GL_RASTERIZER_DISCARD = 0x8C89;
    @Api int GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS = 0x8C8A;
    @Api int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS = 0x8C8B;
    @Api int GL_INTERLEAVED_ATTRIBS = 0x8C8C;
    @Api int GL_SEPARATE_ATTRIBS = 0x8C8D;
    @Api int GL_TRANSFORM_FEEDBACK_BUFFER = 0x8C8E;
    @Api int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING = 0x8C8F;
    @Api int GL_RGBA32UI = 0x8D70;
    @Api int GL_RGB32UI = 0x8D71;
    @Api int GL_RGBA16UI = 0x8D76;
    @Api int GL_RGB16UI = 0x8D77;
    @Api int GL_RGBA8UI = 0x8D7C;
    @Api int GL_RGB8UI = 0x8D7D;
    @Api int GL_RGBA32I = 0x8D82;
    @Api int GL_RGB32I = 0x8D83;
    @Api int GL_RGBA16I = 0x8D88;
    @Api int GL_RGB16I = 0x8D89;
    @Api int GL_RGBA8I = 0x8D8E;
    @Api int GL_RGB8I = 0x8D8F;
    @Api int GL_RED_INTEGER = 0x8D94;
    @Api int GL_RGB_INTEGER = 0x8D98;
    @Api int GL_RGBA_INTEGER = 0x8D99;
    @Api int GL_SAMPLER_2D_ARRAY = 0x8DC1;
    @Api int GL_SAMPLER_2D_ARRAY_SHADOW = 0x8DC4;
    @Api int GL_SAMPLER_CUBE_SHADOW = 0x8DC5;
    @Api int GL_UNSIGNED_INT_VEC2 = 0x8DC6;
    @Api int GL_UNSIGNED_INT_VEC3 = 0x8DC7;
    @Api int GL_UNSIGNED_INT_VEC4 = 0x8DC8;
    @Api int GL_INT_SAMPLER_2D = 0x8DCA;
    @Api int GL_INT_SAMPLER_3D = 0x8DCB;
    @Api int GL_INT_SAMPLER_CUBE = 0x8DCC;
    @Api int GL_INT_SAMPLER_2D_ARRAY = 0x8DCF;
    @Api int GL_UNSIGNED_INT_SAMPLER_2D = 0x8DD2;
    @Api int GL_UNSIGNED_INT_SAMPLER_3D = 0x8DD3;
    @Api int GL_UNSIGNED_INT_SAMPLER_CUBE = 0x8DD4;
    @Api int GL_UNSIGNED_INT_SAMPLER_2D_ARRAY = 0x8DD7;
    @Api int GL_BUFFER_ACCESS_FLAGS = 0x911F;
    @Api int GL_BUFFER_MAP_LENGTH = 0x9120;
    @Api int GL_BUFFER_MAP_OFFSET = 0x9121;
    @Api int GL_DEPTH_COMPONENT32F = 0x8CAC;
    @Api int GL_DEPTH32F_STENCIL8 = 0x8CAD;
    @Api int GL_FLOAT_32_UNSIGNED_INT_24_8_REV = 0x8DAD;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 0x8210;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 0x8211;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE = 0x8212;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 0x8213;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 0x8214;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 0x8215;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 0x8216;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 0x8217;
    @Api int GL_FRAMEBUFFER_DEFAULT = 0x8218;
    @Api int GL_FRAMEBUFFER_UNDEFINED = 0x8219;
    @Api int GL_DEPTH_STENCIL_ATTACHMENT = 0x821A;
    @Api int GL_DEPTH_STENCIL = 0x84F9;
    @Api int GL_UNSIGNED_INT_24_8 = 0x84FA;
    @Api int GL_DEPTH24_STENCIL8 = 0x88F0;
    @Api int GL_UNSIGNED_NORMALIZED = 0x8C17;
    @Api int GL_DRAW_FRAMEBUFFER_BINDING = GL_FRAMEBUFFER_BINDING;
    @Api int GL_READ_FRAMEBUFFER = 0x8CA8;
    @Api int GL_DRAW_FRAMEBUFFER = 0x8CA9;
    @Api int GL_READ_FRAMEBUFFER_BINDING = 0x8CAA;
    @Api int GL_RENDERBUFFER_SAMPLES = 0x8CAB;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 0x8CD4;
    @Api int GL_MAX_COLOR_ATTACHMENTS = 0x8CDF;
    @Api int GL_COLOR_ATTACHMENT1 = 0x8CE1;
    @Api int GL_COLOR_ATTACHMENT2 = 0x8CE2;
    @Api int GL_COLOR_ATTACHMENT3 = 0x8CE3;
    @Api int GL_COLOR_ATTACHMENT4 = 0x8CE4;
    @Api int GL_COLOR_ATTACHMENT5 = 0x8CE5;
    @Api int GL_COLOR_ATTACHMENT6 = 0x8CE6;
    @Api int GL_COLOR_ATTACHMENT7 = 0x8CE7;
    @Api int GL_COLOR_ATTACHMENT8 = 0x8CE8;
    @Api int GL_COLOR_ATTACHMENT9 = 0x8CE9;
    @Api int GL_COLOR_ATTACHMENT10 = 0x8CEA;
    @Api int GL_COLOR_ATTACHMENT11 = 0x8CEB;
    @Api int GL_COLOR_ATTACHMENT12 = 0x8CEC;
    @Api int GL_COLOR_ATTACHMENT13 = 0x8CED;
    @Api int GL_COLOR_ATTACHMENT14 = 0x8CEE;
    @Api int GL_COLOR_ATTACHMENT15 = 0x8CEF;
    @Api int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 0x8D56;
    @Api int GL_MAX_SAMPLES = 0x8D57;
    @Api int GL_HALF_FLOAT = 0x140B;
    @Api int GL_MAP_READ_BIT = 0x0001;
    @Api int GL_MAP_WRITE_BIT = 0x0002;
    @Api int GL_MAP_INVALIDATE_RANGE_BIT = 0x0004;
    @Api int GL_MAP_INVALIDATE_BUFFER_BIT = 0x0008;
    @Api int GL_MAP_FLUSH_EXPLICIT_BIT = 0x0010;
    @Api int GL_MAP_UNSYNCHRONIZED_BIT = 0x0020;
    @Api int GL_RG = 0x8227;
    @Api int GL_RG_INTEGER = 0x8228;
    @Api int GL_R8 = 0x8229;
    @Api int GL_RG8 = 0x822B;
    @Api int GL_R16F = 0x822D;
    @Api int GL_R32F = 0x822E;
    @Api int GL_RG16F = 0x822F;
    @Api int GL_RG32F = 0x8230;
    @Api int GL_R8I = 0x8231;
    @Api int GL_R8UI = 0x8232;
    @Api int GL_R16I = 0x8233;
    @Api int GL_R16UI = 0x8234;
    @Api int GL_R32I = 0x8235;
    @Api int GL_R32UI = 0x8236;
    @Api int GL_RG8I = 0x8237;
    @Api int GL_RG8UI = 0x8238;
    @Api int GL_RG16I = 0x8239;
    @Api int GL_RG16UI = 0x823A;
    @Api int GL_RG32I = 0x823B;
    @Api int GL_RG32UI = 0x823C;
    @Api int GL_VERTEX_ARRAY_BINDING = 0x85B5;
    @Api int GL_R8_SNORM = 0x8F94;
    @Api int GL_RG8_SNORM = 0x8F95;
    @Api int GL_RGB8_SNORM = 0x8F96;
    @Api int GL_RGBA8_SNORM = 0x8F97;
    @Api int GL_SIGNED_NORMALIZED = 0x8F9C;
    @Api int GL_PRIMITIVE_RESTART_FIXED_INDEX = 0x8D69;
    @Api int GL_COPY_READ_BUFFER = 0x8F36;
    @Api int GL_COPY_WRITE_BUFFER = 0x8F37;
    @Api int GL_COPY_READ_BUFFER_BINDING = GL_COPY_READ_BUFFER;
    @Api int GL_COPY_WRITE_BUFFER_BINDING = GL_COPY_WRITE_BUFFER;
    @Api int GL_UNIFORM_BUFFER = 0x8A11;
    @Api int GL_UNIFORM_BUFFER_BINDING = 0x8A28;
    @Api int GL_UNIFORM_BUFFER_START = 0x8A29;
    @Api int GL_UNIFORM_BUFFER_SIZE = 0x8A2A;
    @Api int GL_MAX_VERTEX_UNIFORM_BLOCKS = 0x8A2B;
    @Api int GL_MAX_FRAGMENT_UNIFORM_BLOCKS = 0x8A2D;
    @Api int GL_MAX_COMBINED_UNIFORM_BLOCKS = 0x8A2E;
    @Api int GL_MAX_UNIFORM_BUFFER_BINDINGS = 0x8A2F;
    @Api int GL_MAX_UNIFORM_BLOCK_SIZE = 0x8A30;
    @Api int GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 0x8A31;
    @Api int GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 0x8A33;
    @Api int GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT = 0x8A34;
    @Api int GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH = 0x8A35;
    @Api int GL_ACTIVE_UNIFORM_BLOCKS = 0x8A36;
    @Api int GL_UNIFORM_TYPE = 0x8A37;
    @Api int GL_UNIFORM_SIZE = 0x8A38;
    @Api int GL_UNIFORM_NAME_LENGTH = 0x8A39;
    @Api int GL_UNIFORM_BLOCK_INDEX = 0x8A3A;
    @Api int GL_UNIFORM_OFFSET = 0x8A3B;
    @Api int GL_UNIFORM_ARRAY_STRIDE = 0x8A3C;
    @Api int GL_UNIFORM_MATRIX_STRIDE = 0x8A3D;
    @Api int GL_UNIFORM_IS_ROW_MAJOR = 0x8A3E;
    @Api int GL_UNIFORM_BLOCK_BINDING = 0x8A3F;
    @Api int GL_UNIFORM_BLOCK_DATA_SIZE = 0x8A40;
    @Api int GL_UNIFORM_BLOCK_NAME_LENGTH = 0x8A41;
    @Api int GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS = 0x8A42;
    @Api int GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 0x8A43;
    @Api int GL_UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 0x8A44;
    @Api int GL_UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 0x8A46;
    // GL_INVALID_INDEX is defined as 0xFFFFFFFFu in C.
    @Api int GL_INVALID_INDEX = -1;
    @Api int GL_MAX_VERTEX_OUTPUT_COMPONENTS = 0x9122;
    @Api int GL_MAX_FRAGMENT_INPUT_COMPONENTS = 0x9125;
    @Api int GL_MAX_SERVER_WAIT_TIMEOUT = 0x9111;
    @Api int GL_OBJECT_TYPE = 0x9112;
    @Api int GL_SYNC_CONDITION = 0x9113;
    @Api int GL_SYNC_STATUS = 0x9114;
    @Api int GL_SYNC_FLAGS = 0x9115;
    @Api int GL_SYNC_FENCE = 0x9116;
    @Api int GL_SYNC_GPU_COMMANDS_COMPLETE = 0x9117;
    @Api int GL_UNSIGNALED = 0x9118;
    @Api int GL_SIGNALED = 0x9119;
    @Api int GL_ALREADY_SIGNALED = 0x911A;
    @Api int GL_TIMEOUT_EXPIRED = 0x911B;
    @Api int GL_CONDITION_SATISFIED = 0x911C;
    @Api int GL_WAIT_FAILED = 0x911D;
    @Api int GL_SYNC_FLUSH_COMMANDS_BIT = 0x00000001;
    // GL_TIMEOUT_IGNORED is defined as 0xFFFFFFFFFFFFFFFFull in C.
    @Api long GL_TIMEOUT_IGNORED = -1;
    @Api int GL_VERTEX_ATTRIB_ARRAY_DIVISOR = 0x88FE;
    @Api int GL_ANY_SAMPLES_PASSED = 0x8C2F;
    @Api int GL_ANY_SAMPLES_PASSED_CONSERVATIVE = 0x8D6A;
    @Api int GL_SAMPLER_BINDING = 0x8919;
    @Api int GL_RGB10_A2UI = 0x906F;
    @Api int GL_TEXTURE_SWIZZLE_R = 0x8E42;
    @Api int GL_TEXTURE_SWIZZLE_G = 0x8E43;
    @Api int GL_TEXTURE_SWIZZLE_B = 0x8E44;
    @Api int GL_TEXTURE_SWIZZLE_A = 0x8E45;
    @Api int GL_GREEN = 0x1904;
    @Api int GL_BLUE = 0x1905;
    @Api int GL_INT_2_10_10_10_REV = 0x8D9F;
    @Api int GL_TRANSFORM_FEEDBACK = 0x8E22;
    @Api int GL_TRANSFORM_FEEDBACK_PAUSED = 0x8E23;
    @Api int GL_TRANSFORM_FEEDBACK_ACTIVE = 0x8E24;
    @Api int GL_TRANSFORM_FEEDBACK_BINDING = 0x8E25;
    @Api int GL_PROGRAM_BINARY_RETRIEVABLE_HINT = 0x8257;
    @Api int GL_PROGRAM_BINARY_LENGTH = 0x8741;
    @Api int GL_NUM_PROGRAM_BINARY_FORMATS = 0x87FE;
    @Api int GL_PROGRAM_BINARY_FORMATS = 0x87FF;
    @Api int GL_COMPRESSED_R11_EAC = 0x9270;
    @Api int GL_COMPRESSED_SIGNED_R11_EAC = 0x9271;
    @Api int GL_COMPRESSED_RG11_EAC = 0x9272;
    @Api int GL_COMPRESSED_SIGNED_RG11_EAC = 0x9273;
    @Api int GL_COMPRESSED_RGB8_ETC2 = 0x9274;
    @Api int GL_COMPRESSED_SRGB8_ETC2 = 0x9275;
    @Api int GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9276;
    @Api int GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9277;
    @Api int GL_COMPRESSED_RGBA8_ETC2_EAC = 0x9278;
    @Api int GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 0x9279;
    @Api int GL_TEXTURE_IMMUTABLE_FORMAT = 0x912F;
    @Api int GL_MAX_ELEMENT_INDEX = 0x8D6B;
    @Api int GL_NUM_SAMPLE_COUNTS = 0x9380;
    @Api int GL_TEXTURE_IMMUTABLE_LEVELS = 0x82DF;

    // C function void glReadBuffer ( GLenum mode )

    @Api void glReadBuffer(int mode);

    // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum type, const GLvoid *indices )

    @Api void glDrawRangeElements(int mode, int start, int end, int count, int type, java.nio.Buffer indices);

    // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum type, GLsizei offset )

    @Api void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset);

    // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLenum format, GLenum type, const GLvoid *pixels )

    @Api void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, java.nio.Buffer pixels);

    // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLenum format, GLenum type, GLsizei offset )

    @Api void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset);

    // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width, GLsizei height, GLsizei depth, GLenum format, GLenum type, const GLvoid *pixels )

    @Api void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, java.nio.Buffer pixels);

    // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width, GLsizei height, GLsizei depth, GLenum format, GLenum type, GLsizei offset )

    @Api void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset);

    // C function void glCopyTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLint x, GLint y, GLsizei width, GLsizei height )

    @Api void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height);

    // C function void glCompressedTexImage3D ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLsizei imageSize, const GLvoid *data )

    @Api void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, java.nio.Buffer data);

    // C function void glCompressedTexImage3D (GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLsizei imageSize, GLsizei offset )

    @Api void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset);

    // C function void glCompressedTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, const GLvoid *data )

    @Api void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, java.nio.Buffer data);

    // C function void glCompressedTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, GLsizei offset )

    @Api void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset);

    // C function void glGenQueries ( GLsizei n, GLuint *ids )

    @Api void glGenQueries(int n, int[] ids, int offset);

    // C function void glGenQueries ( GLsizei n, GLuint *ids )

    @Api void glGenQueries(int n, java.nio.IntBuffer ids);

    // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

    @Api void glDeleteQueries(int n, int[] ids, int offset);

    // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

    @Api void glDeleteQueries(int n, java.nio.IntBuffer ids);

    // C function GLboolean glIsQuery ( GLuint id )

    @Api boolean glIsQuery(int id);

    // C function void glBeginQuery ( GLenum target, GLuint id )

    @Api void glBeginQuery(int target, int id);

    // C function void glEndQuery ( GLenum target )

    @Api void glEndQuery(int target);

    // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )

    @Api void glGetQueryiv(int target, int pname, int[] params, int offset);

    // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )

    @Api void glGetQueryiv(int target, int pname, java.nio.IntBuffer params);

    // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )

    @Api void glGetQueryObjectuiv(int id, int pname, int[] params, int offset);

    // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )

    @Api void glGetQueryObjectuiv(int id, int pname, java.nio.IntBuffer params);

    // C function GLboolean glUnmapBuffer ( GLenum target )

    @Api boolean glUnmapBuffer(int target);

    // C function void glGetBufferPointerv ( GLenum target, GLenum pname, GLvoid** params )

    /**
     * The {@link java.nio.Buffer} instance returned by this method is guaranteed
     * to be an instance of {@link java.nio.ByteBuffer}.
     */
    @Api java.nio.Buffer glGetBufferPointerv(int target, int pname);

    // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )

    @Api void glDrawBuffers(int n, int[] bufs, int offset);

    // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )

    @Api void glDrawBuffers(int n, java.nio.IntBuffer bufs);

    // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix2x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix3x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix2x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix4x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix3x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset);

    // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glUniformMatrix4x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glBlitFramebuffer ( GLint srcX0, GLint srcY0, GLint srcX1, GLint srcY1, GLint dstX0, GLint dstY0, GLint dstX1, GLint dstY1, GLbitfield mask, GLenum filter )

    @Api void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

    // C function void glRenderbufferStorageMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width, GLsizei height )

    @Api void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height);

    // C function void glFramebufferTextureLayer ( GLenum target, GLenum attachment, GLuint texture, GLint level, GLint layer )

    @Api void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer);

    // C function GLvoid * glMapBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length, GLbitfield access )

    /**
     * The {@link java.nio.Buffer} instance returned by this method is guaranteed
     * to be an instance of {@link java.nio.ByteBuffer}.
     */
    @Api java.nio.Buffer glMapBufferRange(int target, int offset, int length, int access);

    // C function void glFlushMappedBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length )

    @Api void glFlushMappedBufferRange(int target, int offset, int length);

    // C function void glBindVertexArray ( GLuint array )

    @Api void glBindVertexArray(int array);

    // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

    @Api void glDeleteVertexArrays(int n, int[] arrays, int offset);

    // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

    @Api void glDeleteVertexArrays(int n, java.nio.IntBuffer arrays);

    // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

    @Api void glGenVertexArrays(int n, int[] arrays, int offset);

    // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

    @Api void glGenVertexArrays(int n, java.nio.IntBuffer arrays);

    default int glGenVertexArrays() {
        int[] a = new int[1];
        glGenVertexArrays(1, a, 0);
        return a[0];
    }

    // C function GLboolean glIsVertexArray ( GLuint array )

    @Api boolean glIsVertexArray(int array);

    // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )

    @Api void glGetIntegeri_v(int target, int index, int[] data, int offset);

    // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )

    @Api void glGetIntegeri_v(int target, int index, java.nio.IntBuffer data);

    // C function void glBeginTransformFeedback ( GLenum primitiveMode )

    @Api void glBeginTransformFeedback(int primitiveMode);

    // C function void glEndTransformFeedback ( void )

    @Api void glEndTransformFeedback();

    // C function void glBindBufferRange ( GLenum target, GLuint index, GLuint buffer, GLintptr offset, GLsizeiptr size )

    @Api void glBindBufferRange(int target, int index, int buffer, int offset, int size);

    // C function void glBindBufferBase ( GLenum target, GLuint index, GLuint buffer )

    @Api void glBindBufferBase(int target, int index, int buffer);

    // C function void glTransformFeedbackVaryings ( GLuint program, GLsizei count, const GLchar *varyings, GLenum bufferMode )

    @Api void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode);

    // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, GLchar *name )

    @Api void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset);

    // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, GLchar *name )

    /**
     * @deprecated Use the version that takes a ByteBuffer as the last argument, or the versions that return a String.
     */
    @Api @Deprecated void glGetTransformFeedbackVarying(int program, int index, int bufsize, java.nio.IntBuffer length, java.nio.IntBuffer size, java.nio.IntBuffer type, byte name);

    // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, GLchar *name )

    @Api void glGetTransformFeedbackVarying(int program, int index, int bufsize, java.nio.IntBuffer length, java.nio.IntBuffer size, java.nio.IntBuffer type, java.nio.ByteBuffer name);

    // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, GLchar *name )

    @Api String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset);

    // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size, GLenum *type, GLchar *name )

    @Api String glGetTransformFeedbackVarying(int program, int index, java.nio.IntBuffer size, java.nio.IntBuffer type);

    @Api void glVertexAttribIPointer(int index, int size, int type, int stride, java.nio.Buffer pointer);
//    {
//        glVertexAttribIPointerBounds(
//                index,
//                size,
//                type,
//                stride,
//                pointer,
//                pointer.remaining()
//        );
//    }

    // C function void glVertexAttribIPointer ( GLuint index, GLint size, GLenum type, GLsizei stride, GLsizei offset)

    @Api void glVertexAttribIPointer(int index, int size, int type, int stride, int offset);

    // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )

    @Api void glGetVertexAttribIiv(int index, int pname, int[] params, int offset);

    // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )

    @Api void glGetVertexAttribIiv(int index, int pname, java.nio.IntBuffer params);

    // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )

    @Api void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset);

    // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )

    @Api void glGetVertexAttribIuiv(int index, int pname, java.nio.IntBuffer params);

    // C function void glVertexAttribI4i ( GLuint index, GLint x, GLint y, GLint z, GLint w )

    @Api void glVertexAttribI4i(int index, int x, int y, int z, int w);

    // C function void glVertexAttribI4ui ( GLuint index, GLuint x, GLuint y, GLuint z, GLuint w )

    @Api void glVertexAttribI4ui(int index, int x, int y, int z, int w);

    // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )

    @Api void glVertexAttribI4iv(int index, int[] v, int offset);

    // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )

    @Api void glVertexAttribI4iv(int index, java.nio.IntBuffer v);

    // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )

    @Api void glVertexAttribI4uiv(int index, int[] v, int offset);

    // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )

    @Api void glVertexAttribI4uiv(int index, java.nio.IntBuffer v);

    // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )

    @Api void glGetUniformuiv(int program, int location, int[] params, int offset);

    // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )

    @Api void glGetUniformuiv(int program, int location, java.nio.IntBuffer params);

    // C function GLint glGetFragDataLocation ( GLuint program, const GLchar *name )

    @Api int glGetFragDataLocation(int program, String name);

    // C function void glUniform1ui ( GLint location, GLuint v0 )

    @Api void glUniform1ui(int location, int v0);

    // C function void glUniform2ui ( GLint location, GLuint v0, GLuint v1 )

    @Api void glUniform2ui(int location, int v0, int v1);

    // C function void glUniform3ui ( GLint location, GLuint v0, GLuint v1, GLuint v2 )

    @Api void glUniform3ui(int location, int v0, int v1, int v2);

    // C function void glUniform4ui ( GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )

    @Api void glUniform4ui(int location, int v0, int v1, int v2, int v3);

    // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform1uiv(int location, int count, int[] value, int offset);

    // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform1uiv(int location, int count, java.nio.IntBuffer value);

    // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform2uiv(int location, int count, int[] value, int offset);

    // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform2uiv(int location, int count, java.nio.IntBuffer value);

    // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform3uiv(int location, int count, int[] value, int offset);

    // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform3uiv(int location, int count, java.nio.IntBuffer value);

    // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform4uiv(int location, int count, int[] value, int offset);

    // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )

    @Api void glUniform4uiv(int location, int count, java.nio.IntBuffer value);

    // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )

    @Api void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset);

    // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )

    @Api void glClearBufferiv(int buffer, int drawbuffer, java.nio.IntBuffer value);

    // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )

    @Api void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset);

    // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )

    @Api void glClearBufferuiv(int buffer, int drawbuffer, java.nio.IntBuffer value);

    // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )

    @Api void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset);

    // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )

    @Api void glClearBufferfv(int buffer, int drawbuffer, java.nio.FloatBuffer value);

    // C function void glClearBufferfi ( GLenum buffer, GLint drawbuffer, GLfloat depth, GLint stencil )

    @Api void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil);

    // C function const GLubyte * glGetStringi ( GLenum name, GLuint index )

    @Api String glGetStringi(int name, int index);

    // C function void glCopyBufferSubData ( GLenum readTarget, GLenum writeTarget, GLintptr readOffset, GLintptr writeOffset, GLsizeiptr size )

    @Api void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size);

    // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint *uniformIndices )

    @Api void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset);

    // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint *uniformIndices )

    @Api void glGetUniformIndices(int program, String[] uniformNames, java.nio.IntBuffer uniformIndices);

    // C function void glGetActiveUniformsiv ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname, GLint *params )

    @Api void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset);

    // C function void glGetActiveUniformsiv ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname, GLint *params )

    @Api void glGetActiveUniformsiv(int program, int uniformCount, java.nio.IntBuffer uniformIndices, int pname, java.nio.IntBuffer params);

    // C function GLuint glGetUniformBlockIndex ( GLuint program, const GLchar *uniformBlockName )

    @Api int glGetUniformBlockIndex(int program, String uniformBlockName);

    // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )

    @Api void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset);

    // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )

    @Api void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, java.nio.IntBuffer params);

    // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length, GLchar *uniformBlockName )

    @Api void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset);

    // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length, GLchar *uniformBlockName )

    @Api void glGetActiveUniformBlockName(int program, int uniformBlockIndex, java.nio.Buffer length, java.nio.Buffer uniformBlockName);

    // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length, GLchar *uniformBlockName )

    @Api String glGetActiveUniformBlockName(int program, int uniformBlockIndex);

    // C function void glUniformBlockBinding ( GLuint program, GLuint uniformBlockIndex, GLuint uniformBlockBinding )

    @Api void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding);

    // C function void glDrawArraysInstanced ( GLenum mode, GLint first, GLsizei count, GLsizei instanceCount )

    @Api void glDrawArraysInstanced(int mode, int first, int count, int instanceCount);

    // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices, GLsizei instanceCount )

    @Api void glDrawElementsInstanced(int mode, int count, int type, java.nio.Buffer indices, int instanceCount);

    // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices, GLsizei instanceCount )

    @Api void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount);

    // C function GLsync glFenceSync ( GLenum condition, GLbitfield flags )

    long glFenceSync(int condition, int flags);

    // C function GLboolean glIsSync ( GLsync sync )

    @Api boolean glIsSync(long sync);

    // C function void glDeleteSync ( GLsync sync )

    @Api void glDeleteSync(long sync);

    // C function GLenum glClientWaitSync ( GLsync sync, GLbitfield flags, GLuint64 timeout )

    @Api int glClientWaitSync(long sync, int flags, long timeout);

    // C function void glWaitSync ( GLsync sync, GLbitfield flags, GLuint64 timeout )

    @Api void glWaitSync(long sync, int flags, long timeout);

    // C function void glGetInteger64v ( GLenum pname, GLint64 *params )

    @Api void glGetInteger64v(int pname, long[] params, int offset);

    // C function void glGetInteger64v ( GLenum pname, GLint64 *params )

    @Api void glGetInteger64v(int pname, java.nio.LongBuffer params);

    // C function void glGetSynciv ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )

    @Api void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset);

    // C function void glGetSynciv ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )

    @Api void glGetSynciv(long sync, int pname, int bufSize, java.nio.IntBuffer length, java.nio.IntBuffer values);

    // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )

    @Api void glGetInteger64i_v(int target, int index, long[] data, int offset);

    // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )

    @Api void glGetInteger64i_v(int target, int index, java.nio.LongBuffer data);

    // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )

    @Api void glGetBufferParameteri64v(int target, int pname, long[] params, int offset);

    // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )

    @Api void glGetBufferParameteri64v(int target, int pname, java.nio.LongBuffer params);

    // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

    @Api void glGenSamplers(int count, int[] samplers, int offset);

    // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

    @Api void glGenSamplers(int count, java.nio.IntBuffer samplers);

    // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

    @Api void glDeleteSamplers(int count, int[] samplers, int offset);

    // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

    @Api void glDeleteSamplers(int count, java.nio.IntBuffer samplers);

    // C function GLboolean glIsSampler ( GLuint sampler )

    @Api boolean glIsSampler(int sampler);

    // C function void glBindSampler ( GLuint unit, GLuint sampler )

    @Api void glBindSampler(int unit, int sampler);

    // C function void glSamplerParameteri ( GLuint sampler, GLenum pname, GLint param )

    @Api void glSamplerParameteri(int sampler, int pname, int param);

    // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )

    @Api void glSamplerParameteriv(int sampler, int pname, int[] param, int offset);

    // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )

    @Api void glSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer param);

    // C function void glSamplerParameterf ( GLuint sampler, GLenum pname, GLfloat param )

    @Api void glSamplerParameterf(int sampler, int pname, float param);

    // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )

    @Api void glSamplerParameterfv(int sampler, int pname, float[] param, int offset);

    // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )

    @Api void glSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer param);

    // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )

    @Api void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset);

    // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )

    @Api void glGetSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer params);

    // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )

    @Api void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset);

    // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )

    @Api void glGetSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer params);

    // C function void glVertexAttribDivisor ( GLuint index, GLuint divisor )

    @Api void glVertexAttribDivisor(int index, int divisor);

    // C function void glBindTransformFeedback ( GLenum target, GLuint id )

    @Api void glBindTransformFeedback(int target, int id);

    // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

    @Api void glDeleteTransformFeedbacks(int n, int[] ids, int offset);

    // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

    @Api void glDeleteTransformFeedbacks(int n, java.nio.IntBuffer ids);

    // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

    @Api void glGenTransformFeedbacks(int n, int[] ids, int offset);

    // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

    @Api void glGenTransformFeedbacks(int n, java.nio.IntBuffer ids);

    // C function GLboolean glIsTransformFeedback ( GLuint id )

    @Api boolean glIsTransformFeedback(int id);

    // C function void glPauseTransformFeedback ( void )

    @Api void glPauseTransformFeedback();

    // C function void glResumeTransformFeedback ( void )

    @Api void glResumeTransformFeedback();

    // C function void glGetProgramBinary ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary )

    @Api void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, java.nio.Buffer binary);

    // C function void glGetProgramBinary ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary )

    @Api void glGetProgramBinary(int program, int bufSize, java.nio.IntBuffer length, java.nio.IntBuffer binaryFormat, java.nio.Buffer binary);

    // C function void glProgramBinary ( GLuint program, GLenum binaryFormat, const GLvoid *binary, GLsizei length )

    @Api void glProgramBinary(int program, int binaryFormat, java.nio.Buffer binary, int length);

    // C function void glProgramParameteri ( GLuint program, GLenum pname, GLint value )

    @Api void glProgramParameteri(int program, int pname, int value);

    // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )

    @Api void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset);

    // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )

    @Api void glInvalidateFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments);

    // C function void glInvalidateSubFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x, GLint y, GLsizei width, GLsizei height )

    @Api void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height);

    // C function void glInvalidateSubFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x, GLint y, GLsizei width, GLsizei height )

    @Api void glInvalidateSubFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments, int x, int y, int width, int height);

    // C function void glTexStorage2D ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height )

    @Api void glTexStorage2D(int target, int levels, int internalformat, int width, int height);

    // C function void glTexStorage3D ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth )

    @Api void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth);

    // C function void glGetInternalformativ ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )

    @Api void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset);

    // C function void glGetInternalformativ ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )

    @Api void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, java.nio.IntBuffer params);

    // C function void glReadPixels ( GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, GLint offset )

    @Api void glReadPixels(int x, int y, int width, int height, int format, int type, int offset);
}
