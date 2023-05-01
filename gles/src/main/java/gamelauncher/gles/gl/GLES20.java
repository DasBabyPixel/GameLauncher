/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gl;

import de.dasbabypixel.annotations.Api;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GLES20 {

    /**
     * Accepted by the {@code name} parameter of GetString.
     */
    @Api int GL_SHADING_LANGUAGE_VERSION = 0x8B8C;

    /**
     * Accepted by the {@code pname} parameter of GetInteger.
     */
    @Api int GL_CURRENT_PROGRAM = 0x8B8D;

    /**
     * Accepted by the {@code pname} parameter of GetShaderiv.
     */
    @Api int GL_SHADER_TYPE = 0x8B4F, GL_DELETE_STATUS = 0x8B80, GL_COMPILE_STATUS = 0x8B81, GL_LINK_STATUS = 0x8B82, GL_VALIDATE_STATUS = 0x8B83, GL_INFO_LOG_LENGTH = 0x8B84, GL_ATTACHED_SHADERS = 0x8B85, GL_ACTIVE_UNIFORMS = 0x8B86, GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87, GL_ACTIVE_ATTRIBUTES = 0x8B89, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A, GL_SHADER_SOURCE_LENGTH = 0x8B88;

    /**
     * Returned by the {@code type} parameter of GetActiveUniform.
     */
    @Api int GL_FLOAT_VEC2 = 0x8B50, GL_FLOAT_VEC3 = 0x8B51, GL_FLOAT_VEC4 = 0x8B52, GL_INT_VEC2 = 0x8B53, GL_INT_VEC3 = 0x8B54, GL_INT_VEC4 = 0x8B55, GL_BOOL = 0x8B56, GL_BOOL_VEC2 = 0x8B57, GL_BOOL_VEC3 = 0x8B58, GL_BOOL_VEC4 = 0x8B59, GL_FLOAT_MAT2 = 0x8B5A, GL_FLOAT_MAT3 = 0x8B5B, GL_FLOAT_MAT4 = 0x8B5C, GL_SAMPLER_2D = 0x8B5E, GL_SAMPLER_CUBE = 0x8B60;

    /**
     * Accepted by the {@code type} argument of CreateShader and returned by the {@code params} parameter of GetShaderiv.
     */
    @Api int GL_VERTEX_SHADER = 0x8B31;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api int GL_MAX_VERTEX_ATTRIBS = 0x8869, GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872, GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C, GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;

    /**
     * Accepted by the {@code pname} parameter of GetVertexAttrib{dfi}v.
     */
    @Api int GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622, GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623, GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624, GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625, GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A, GL_CURRENT_VERTEX_ATTRIB = 0x8626;

    /**
     * Accepted by the {@code pname} parameter of GetVertexAttribPointerv.
     */
    @Api int GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645;

    /**
     * Accepted by the {@code type} argument of CreateShader and returned by the {@code params} parameter of GetShaderiv.
     */
    @Api int GL_FRAGMENT_SHADER = 0x8B30;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api int GL_BLEND_EQUATION = 0x8009, GL_BLEND_EQUATION_RGB = 0x8009, GL_BLEND_EQUATION_ALPHA = 0x883D;

    /**
     * Accepted by the {@code pname} parameter of GetIntegerv.
     */
    @Api int GL_STENCIL_BACK_FUNC = 0x8800, GL_STENCIL_BACK_FAIL = 0x8801, GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802, GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803, GL_STENCIL_BACK_REF = 0x8CA3, GL_STENCIL_BACK_VALUE_MASK = 0x8CA4, GL_STENCIL_BACK_WRITEMASK = 0x8CA5;

    /**
     * Accepted by the {@code target} parameters of BindBuffer, BufferData, BufferSubData, MapBuffer, UnmapBuffer, GetBufferSubData,
     * GetBufferParameteriv, and GetBufferPointerv.
     */
    @Api int GL_ARRAY_BUFFER = 0x8892, GL_ELEMENT_ARRAY_BUFFER = 0x8893;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api int GL_ARRAY_BUFFER_BINDING = 0x8894, GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;

    /**
     * Accepted by the {@code pname} parameter of GetVertexAttribiv.
     */
    @Api int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;


    /**
     * Accepted by the {@code usage} parameter of BufferData.
     */
    @Api int GL_STREAM_DRAW = 0x88E0, GL_STATIC_DRAW = 0x88E4, GL_DYNAMIC_DRAW = 0x88E8;

    /**
     * Accepted by the {@code pname} parameter of GetBufferParameteriv.
     */
    @Api int GL_BUFFER_SIZE = 0x8764, GL_BUFFER_USAGE = 0x8765;

    /**
     * Accepted by the {@code target} parameter of Hint, and by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api int GL_GENERATE_MIPMAP_HINT = 0x8192;




    /**
     * Accepted by the {@code sfactor} and {@code dfactor} parameters of BlendFunc.
     */
    @Api  int GL_CONSTANT_COLOR = 0x8001, GL_ONE_MINUS_CONSTANT_COLOR = 0x8002, GL_CONSTANT_ALPHA = 0x8003, GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;

    /**
     * Accepted by the {@code mode} parameter of BlendEquation.
     */
    @Api  int GL_FUNC_ADD = 0x8006;

    /**
     * Accepted by the {@code mode} parameter of BlendEquation.
     */
    @Api  int GL_FUNC_SUBTRACT = 0x800A, GL_FUNC_REVERSE_SUBTRACT = 0x800B;

    /**
     * Accepted by the {@code internalFormat} parameter of TexImage1D, TexImage2D, CopyTexImage1D and CopyTexImage2D.
     */
    @Api  int GL_DEPTH_COMPONENT16 = 0x81A5;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api  int GL_BLEND_DST_RGB = 0x80C8, GL_BLEND_SRC_RGB = 0x80C9, GL_BLEND_DST_ALPHA = 0x80CA, GL_BLEND_SRC_ALPHA = 0x80CB;

    /**
     * Accepted by the {@code sfail}, {@code dpfail}, and {@code dppass} parameter of StencilOp.
     */
    @Api int GL_INCR_WRAP = 0x8507, GL_DECR_WRAP = 0x8508;





    /**
     * Accepted by the {@code param} parameter of TexParameteri and TexParameterf, and by the {@code params} parameter of TexParameteriv and TexParameterfv,
     * when their {@code pname} parameter is TEXTURE_WRAP_S, TEXTURE_WRAP_T, or TEXTURE_WRAP_R.
     */
    @Api int GL_MIRRORED_REPEAT = 0x8370;

    /**
     * Accepted by the {@code value} parameter of GetIntegerv, GetBooleanv, GetFloatv, and GetDoublev.
     */
    @Api int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2, GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3;

    /**
     * When the {@code pname} parameter of TexGendv, TexGenfv, and TexGeniv is TEXTURE_GEN_MODE, then the array {@code params} may also contain NORMAL_MAP
     * or REFLECTION_MAP. Accepted by the {@code cap} parameter of Enable, Disable, IsEnabled, and by the {@code pname} parameter of GetBooleanv,
     * GetIntegerv, GetFloatv, and GetDoublev, and by the {@code target} parameter of BindTexture, GetTexParameterfv, GetTexParameteriv, TexParameterf,
     * TexParameteri, TexParameterfv, and TexParameteriv.
     */
    @Api int GL_TEXTURE_CUBE_MAP = 0x8513;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.
     */
    @Api int GL_TEXTURE_BINDING_CUBE_MAP = 0x8514;

    /**
     * Accepted by the {@code target} parameter of GetTexImage, GetTexLevelParameteriv, GetTexLevelParameterfv, TexImage2D, CopyTexImage2D, TexSubImage2D, and
     * CopySubTexImage2D.
     */
    @Api int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515, GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516, GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518, GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetDoublev, GetIntegerv, and GetFloatv.
     */
    @Api int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;











    @Api int GL_ACTIVE_TEXTURE = 0x84E0;
    @Api int GL_DEPTH_BUFFER_BIT = 0x00000100;
    @Api int GL_STENCIL_BUFFER_BIT = 0x00000400;
    @Api int GL_COLOR_BUFFER_BIT = 0x00004000;
    @Api int GL_FALSE = 0;
    @Api int GL_TRUE = 1;
    @Api int GL_POINTS = 0x0000;
    @Api int GL_LINES = 0x0001;
    @Api int GL_LINE_LOOP = 0x0002;
    @Api int GL_LINE_STRIP = 0x0003;
    @Api int GL_TRIANGLES = 0x0004;
    @Api int GL_TRIANGLE_STRIP = 0x0005;
    @Api int GL_TRIANGLE_FAN = 0x0006;
    @Api int GL_ZERO = 0;
    @Api int GL_ONE = 1;
    @Api int GL_SRC_COLOR = 0x0300;
    @Api int GL_ONE_MINUS_SRC_COLOR = 0x0301;
    @Api int GL_SRC_ALPHA = 0x0302;
    @Api int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
    @Api int GL_DST_ALPHA = 0x0304;
    @Api int GL_ONE_MINUS_DST_ALPHA = 0x0305;
    @Api int GL_DST_COLOR = 0x0306;
    @Api int GL_ONE_MINUS_DST_COLOR = 0x0307;
    @Api int GL_SRC_ALPHA_SATURATE = 0x0308;
    @Api int GL_BLEND_COLOR = 0x8005;
    @Api int GL_FRONT = 0x0404;
    @Api int GL_BACK = 0x0405;
    @Api int GL_FRONT_AND_BACK = 0x0408;
    @Api int GL_TEXTURE_2D = 0x0DE1;
    @Api int GL_CULL_FACE = 0x0B44;
    @Api int GL_BLEND = 0x0BE2;
    @Api int GL_DITHER = 0x0BD0;
    @Api int GL_STENCIL_TEST = 0x0B90;
    @Api int GL_DEPTH_TEST = 0x0B71;
    @Api int GL_SCISSOR_TEST = 0x0C11;
    @Api int GL_POLYGON_OFFSET_FILL = 0x8037;
    @Api int GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E;
    @Api int GL_SAMPLE_COVERAGE = 0x80A0;
    @Api int GL_NO_ERROR = 0;
    @Api int GL_INVALID_ENUM = 0x0500;
    @Api int GL_INVALID_VALUE = 0x0501;
    @Api int GL_INVALID_OPERATION = 0x0502;
    @Api int GL_OUT_OF_MEMORY = 0x0505;
    @Api int GL_CW = 0x0900;
    @Api int GL_CCW = 0x0901;
    @Api int GL_LINE_WIDTH = 0x0B21;
    @Api int GL_ALIASED_POINT_SIZE_RANGE = 0x846D;
    @Api int GL_ALIASED_LINE_WIDTH_RANGE = 0x846E;
    @Api int GL_CULL_FACE_MODE = 0x0B45;
    @Api int GL_FRONT_FACE = 0x0B46;
    @Api int GL_DEPTH_RANGE = 0x0B70;
    @Api int GL_DEPTH_WRITEMASK = 0x0B72;
    @Api int GL_DEPTH_CLEAR_VALUE = 0x0B73;
    @Api int GL_DEPTH_FUNC = 0x0B74;
    @Api int GL_STENCIL_CLEAR_VALUE = 0x0B91;
    @Api int GL_STENCIL_FUNC = 0x0B92;
    @Api int GL_STENCIL_FAIL = 0x0B94;
    @Api int GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95;
    @Api int GL_STENCIL_PASS_DEPTH_PASS = 0x0B96;
    @Api int GL_STENCIL_REF = 0x0B97;
    @Api int GL_STENCIL_VALUE_MASK = 0x0B93;
    @Api int GL_STENCIL_WRITEMASK = 0x0B98;
    @Api int GL_VIEWPORT = 0x0BA2;
    @Api int GL_SCISSOR_BOX = 0x0C10;
    @Api int GL_COLOR_CLEAR_VALUE = 0x0C22;
    @Api int GL_COLOR_WRITEMASK = 0x0C23;
    @Api int GL_UNPACK_ALIGNMENT = 0x0CF5;
    @Api int GL_PACK_ALIGNMENT = 0x0D05;
    @Api int GL_MAX_TEXTURE_SIZE = 0x0D33;
    @Api int GL_MAX_VIEWPORT_DIMS = 0x0D3A;
    @Api int GL_SUBPIXEL_BITS = 0x0D50;
    @Api int GL_RED_BITS = 0x0D52;
    @Api int GL_GREEN_BITS = 0x0D53;
    @Api int GL_BLUE_BITS = 0x0D54;
    @Api int GL_ALPHA_BITS = 0x0D55;
    @Api int GL_DEPTH_BITS = 0x0D56;
    @Api int GL_STENCIL_BITS = 0x0D57;
    @Api int GL_POLYGON_OFFSET_UNITS = 0x2A00;
    @Api int GL_POLYGON_OFFSET_FACTOR = 0x8038;
    @Api int GL_TEXTURE_BINDING_2D = 0x8069;
    @Api int GL_SAMPLE_BUFFERS = 0x80A8;
    @Api int GL_SAMPLES = 0x80A9;
    @Api int GL_SAMPLE_COVERAGE_VALUE = 0x80AA;
    @Api int GL_SAMPLE_COVERAGE_INVERT = 0x80AB;
    @Api int GL_DONT_CARE = 0x1100;
    @Api int GL_FASTEST = 0x1101;
    @Api int GL_NICEST = 0x1102;
    @Api int GL_BYTE = 0x1400;
    @Api int GL_UNSIGNED_BYTE = 0x1401;
    @Api int GL_SHORT = 0x1402;
    @Api int GL_UNSIGNED_SHORT = 0x1403;
    @Api int GL_INT = 0x1404;
    @Api int GL_UNSIGNED_INT = 0x1405;
    @Api int GL_FLOAT = 0x1406;
    @Api int GL_FIXED = 0x140C;
    @Api int GL_DEPTH_COMPONENT = 0x1902;
    @Api int GL_ALPHA = 0x1906;
    @Api int GL_RGB = 0x1907;
    @Api int GL_RGBA = 0x1908;
    @Api int GL_LUMINANCE = 0x1909;
    @Api int GL_LUMINANCE_ALPHA = 0x190A;
    @Api int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
    @Api int GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034;
    @Api int GL_UNSIGNED_SHORT_5_6_5 = 0x8363;
    @Api int GL_MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB;
    @Api int GL_MAX_VARYING_VECTORS = 0x8DFC;
    @Api int GL_MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD;
    @Api int GL_NEVER = 0x0200;
    @Api int GL_LESS = 0x0201;
    @Api int GL_EQUAL = 0x0202;
    @Api int GL_LEQUAL = 0x0203;
    @Api int GL_GREATER = 0x0204;
    @Api int GL_NOTEQUAL = 0x0205;
    @Api int GL_GEQUAL = 0x0206;
    @Api int GL_ALWAYS = 0x0207;
    @Api int GL_KEEP = 0x1E00;
    @Api int GL_REPLACE = 0x1E01;
    @Api int GL_INCR = 0x1E02;
    @Api int GL_DECR = 0x1E03;
    @Api int GL_INVERT = 0x150A;
    @Api int GL_VENDOR = 0x1F00;
    @Api int GL_RENDERER = 0x1F01;
    @Api int GL_VERSION = 0x1F02;
    @Api int GL_EXTENSIONS = 0x1F03;
    @Api int GL_NEAREST = 0x2600;
    @Api int GL_LINEAR = 0x2601;
    @Api int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
    @Api int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
    @Api int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
    @Api int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
    @Api int GL_TEXTURE_MAG_FILTER = 0x2800;
    @Api int GL_TEXTURE_MIN_FILTER = 0x2801;
    @Api int GL_TEXTURE_WRAP_S = 0x2802;
    @Api int GL_TEXTURE_WRAP_T = 0x2803;
    @Api int GL_TEXTURE = 0x1702;
    @Api int GL_TEXTURE0 = 0x84C0;
    @Api int GL_TEXTURE1 = 0x84C1;
    @Api int GL_TEXTURE2 = 0x84C2;
    @Api int GL_TEXTURE3 = 0x84C3;
    @Api int GL_TEXTURE4 = 0x84C4;
    @Api int GL_TEXTURE5 = 0x84C5;
    @Api int GL_TEXTURE6 = 0x84C6;
    @Api int GL_TEXTURE7 = 0x84C7;
    @Api int GL_TEXTURE8 = 0x84C8;
    @Api int GL_TEXTURE9 = 0x84C9;
    @Api int GL_TEXTURE10 = 0x84CA;
    @Api int GL_TEXTURE11 = 0x84CB;
    @Api int GL_TEXTURE12 = 0x84CC;
    @Api int GL_TEXTURE13 = 0x84CD;
    @Api int GL_TEXTURE14 = 0x84CE;
    @Api int GL_TEXTURE15 = 0x84CF;
    @Api int GL_TEXTURE16 = 0x84D0;
    @Api int GL_TEXTURE17 = 0x84D1;
    @Api int GL_TEXTURE18 = 0x84D2;
    @Api int GL_TEXTURE19 = 0x84D3;
    @Api int GL_TEXTURE20 = 0x84D4;
    @Api int GL_TEXTURE21 = 0x84D5;
    @Api int GL_TEXTURE22 = 0x84D6;
    @Api int GL_TEXTURE23 = 0x84D7;
    @Api int GL_TEXTURE24 = 0x84D8;
    @Api int GL_TEXTURE25 = 0x84D9;
    @Api int GL_TEXTURE26 = 0x84DA;
    @Api int GL_TEXTURE27 = 0x84DB;
    @Api int GL_TEXTURE28 = 0x84DC;
    @Api int GL_TEXTURE29 = 0x84DD;
    @Api int GL_TEXTURE30 = 0x84DE;
    @Api int GL_TEXTURE31 = 0x84DF;
    @Api int GL_REPEAT = 0x2901;
    @Api int GL_CLAMP_TO_EDGE = 0x812F;
    @Api int GL_IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A;
    @Api int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B;
    @Api int GL_SHADER_COMPILER = 0x8DFA;
    @Api int GL_SHADER_BINARY_FORMATS = 0x8DF8;
    @Api int GL_NUM_SHADER_BINARY_FORMATS = 0x8DF9;
    @Api int GL_LOW_FLOAT = 0x8DF0;
    @Api int GL_MEDIUM_FLOAT = 0x8DF1;
    @Api int GL_HIGH_FLOAT = 0x8DF2;
    @Api int GL_LOW_INT = 0x8DF3;
    @Api int GL_MEDIUM_INT = 0x8DF4;
    @Api int GL_HIGH_INT = 0x8DF5;
    @Api int GL_FRAMEBUFFER = 0x8D40;
    @Api int GL_RENDERBUFFER = 0x8D41;
    @Api int GL_RGBA4 = 0x8056;
    @Api int GL_RGB5_A1 = 0x8057;
    @Api int GL_RGB565 = 0x8D62;
    @Api int GL_STENCIL_INDEX8 = 0x8D48;
    @Api int GL_RENDERBUFFER_WIDTH = 0x8D42;
    @Api int GL_RENDERBUFFER_HEIGHT = 0x8D43;
    @Api int GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;
    @Api int GL_RENDERBUFFER_RED_SIZE = 0x8D50;
    @Api int GL_RENDERBUFFER_GREEN_SIZE = 0x8D51;
    @Api int GL_RENDERBUFFER_BLUE_SIZE = 0x8D52;
    @Api int GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53;
    @Api int GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54;
    @Api int GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2;
    @Api int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;
    @Api int GL_COLOR_ATTACHMENT0 = 0x8CE0;
    @Api int GL_DEPTH_ATTACHMENT = 0x8D00;
    @Api int GL_STENCIL_ATTACHMENT = 0x8D20;
    @Api int GL_NONE = 0;
    @Api int GL_FRAMEBUFFER_COMPLETE = 0x8CD5;
    @Api int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
    @Api int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
    @Api int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;
    @Api int GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD;
    @Api int GL_FRAMEBUFFER_BINDING = 0x8CA6;
    @Api int GL_RENDERBUFFER_BINDING = 0x8CA7;
    @Api int GL_MAX_RENDERBUFFER_SIZE = 0x84E8;
    @Api int GL_INVALID_FRAMEBUFFER_OPERATION = 0x0506;

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Accepted by the {@code cap} parameter of Enable, Disable, and IsEnabled, and by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and
     * GetDoublev.
     */
    public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E, GL_SAMPLE_COVERAGE = 0x80A0;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetDoublev, GetIntegerv, and GetFloatv.
     */
    public static final int GL_SAMPLE_BUFFERS = 0x80A8, GL_SAMPLES = 0x80A9, GL_SAMPLE_COVERAGE_VALUE = 0x80AA, GL_SAMPLE_COVERAGE_INVERT = 0x80AB;

    /**
     * Accepted by the {@code texture} parameter of ActiveTexture and MultiTexCoord.
     */
    public static final int GL_TEXTURE0 = 0x84C0, GL_TEXTURE1 = 0x84C1, GL_TEXTURE2 = 0x84C2, GL_TEXTURE3 = 0x84C3, GL_TEXTURE4 = 0x84C4, GL_TEXTURE5 = 0x84C5, GL_TEXTURE6 = 0x84C6, GL_TEXTURE7 = 0x84C7, GL_TEXTURE8 = 0x84C8, GL_TEXTURE9 = 0x84C9, GL_TEXTURE10 = 0x84CA, GL_TEXTURE11 = 0x84CB, GL_TEXTURE12 = 0x84CC, GL_TEXTURE13 = 0x84CD, GL_TEXTURE14 = 0x84CE, GL_TEXTURE15 = 0x84CF, GL_TEXTURE16 = 0x84D0, GL_TEXTURE17 = 0x84D1, GL_TEXTURE18 = 0x84D2, GL_TEXTURE19 = 0x84D3, GL_TEXTURE20 = 0x84D4, GL_TEXTURE21 = 0x84D5, GL_TEXTURE22 = 0x84D6, GL_TEXTURE23 = 0x84D7, GL_TEXTURE24 = 0x84D8, GL_TEXTURE25 = 0x84D9, GL_TEXTURE26 = 0x84DA, GL_TEXTURE27 = 0x84DB, GL_TEXTURE28 = 0x84DC, GL_TEXTURE29 = 0x84DD, GL_TEXTURE30 = 0x84DE, GL_TEXTURE31 = 0x84DF;

    /**
     * Accepted by the {@code pname} parameter of GetBooleanv, GetDoublev, GetIntegerv, and GetFloatv.
     */
    public static final int GL_ACTIVE_TEXTURE = 0x84E0,

    /**
     * Aliases for smooth points and lines.
     */
    public static final int GL_ALIASED_POINT_SIZE_RANGE = 0x846D, GL_ALIASED_LINE_WIDTH_RANGE = 0x846E,

    /**
     * Accepted by the {@code type} parameter of DrawPixels, ReadPixels, TexImage1D, TexImage2D, GetTexImage, TexImage3D, TexSubImage1D, TexSubImage2D,
     * TexSubImage3D, GetHistogram, GetMinmax, ConvolutionFilter1D, ConvolutionFilter2D, ConvolutionFilter3D, GetConvolutionFilter, SeparableFilter2D,
     * SeparableFilter3D, GetSeparableFilter, ColorTable, GetColorTable, TexImage4D, and TexSubImage4D.
     */
    public static final int GL_UNSIGNED_SHORT_5_6_5 = 0x8363, GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033, GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034,

    /**
     * Accepted by the {@code param} parameter of TexParameteri and TexParameterf, and by the {@code params} parameter of TexParameteriv and TexParameterfv,
     * when their {@code pname} parameter is TEXTURE_WRAP_S, TEXTURE_WRAP_T, or TEXTURE_WRAP_R.
     */
    public static final int GL_CLAMP_TO_EDGE = 0x812F;

    /**
     * AlphaFunction
     */
    public static final int GL_NEVER = 0x200, GL_LESS = 0x201, GL_EQUAL = 0x202, GL_LEQUAL = 0x203, GL_GREATER = 0x204, GL_NOTEQUAL = 0x205, GL_GEQUAL = 0x206, GL_ALWAYS = 0x207;

    /**
     * AttribMask
     */
    public static final int GL_DEPTH_BUFFER_BIT = 0x100, GL_STENCIL_BUFFER_BIT = 0x400, GL_COLOR_BUFFER_BIT = 0x4000,

    /**
     * BeginMode
     */
    public static final int GL_POINTS = 0x0, GL_LINES = 0x1, GL_LINE_LOOP = 0x2, GL_LINE_STRIP = 0x3, GL_TRIANGLES = 0x4, GL_TRIANGLE_STRIP = 0x5, GL_TRIANGLE_FAN = 0x6,

    /**
     * BlendingFactorDest
     */
    public static final int GL_ZERO = 0, GL_ONE = 1, GL_SRC_COLOR = 0x300, GL_ONE_MINUS_SRC_COLOR = 0x301, GL_SRC_ALPHA = 0x302, GL_ONE_MINUS_SRC_ALPHA = 0x303, GL_DST_ALPHA = 0x304, GL_ONE_MINUS_DST_ALPHA = 0x305;

    /**
     * BlendingFactorSrc
     */
    public static final int GL_DST_COLOR = 0x306, GL_ONE_MINUS_DST_COLOR = 0x307, GL_SRC_ALPHA_SATURATE = 0x308;

    /**
     * Boolean
     */
    public static final int GL_TRUE = 1, GL_FALSE = 0;

    /**
     * DataType
     */
    public static final int GL_BYTE = 0x1400, GL_UNSIGNED_BYTE = 0x1401, GL_SHORT = 0x1402, GL_UNSIGNED_SHORT = 0x1403, GL_INT = 0x1404, GL_UNSIGNED_INT = 0x1405, GL_FLOAT = 0x1406,

    /**
     * DrawBufferMode
     */
    public static final int GL_NONE = 0, GL_FRONT = 0x404, GL_BACK = 0x405, GL_FRONT_AND_BACK = 0x408,

    /**
     * ErrorCode
     */
    public static final int GL_NO_ERROR = 0, GL_INVALID_ENUM = 0x500, GL_INVALID_VALUE = 0x501, GL_INVALID_OPERATION = 0x502, GL_OUT_OF_MEMORY = 0x505;

    /**
     * FrontFaceDirection
     */
    public static final int GL_CW = 0x900, GL_CCW = 0x901;

    /**
     * GetTarget
     */
    public static final int GL_LINE_WIDTH = 0xB21, GL_CULL_FACE = 0xB44, GL_CULL_FACE_MODE = 0xB45, GL_FRONT_FACE = 0xB46, GL_DEPTH_RANGE = 0xB70, GL_DEPTH_TEST = 0xB71, GL_DEPTH_WRITEMASK = 0xB72, GL_DEPTH_CLEAR_VALUE = 0xB73, GL_DEPTH_FUNC = 0xB74, GL_STENCIL_TEST = 0xB90, GL_STENCIL_CLEAR_VALUE = 0xB91, GL_STENCIL_FUNC = 0xB92, GL_STENCIL_VALUE_MASK = 0xB93, GL_STENCIL_FAIL = 0xB94, GL_STENCIL_PASS_DEPTH_FAIL = 0xB95, GL_STENCIL_PASS_DEPTH_PASS = 0xB96, GL_STENCIL_REF = 0xB97, GL_STENCIL_WRITEMASK = 0xB98, GL_VIEWPORT = 0xBA2, GL_DITHER = 0xBD0, GL_BLEND = 0xBE2, GL_SCISSOR_BOX = 0xC10, GL_SCISSOR_TEST = 0xC11, GL_COLOR_CLEAR_VALUE = 0xC22, GL_COLOR_WRITEMASK = 0xC23, GL_UNPACK_ALIGNMENT = 0xCF5, GL_PACK_ALIGNMENT = 0xD05, GL_MAX_TEXTURE_SIZE = 0xD33, GL_MAX_VIEWPORT_DIMS = 0xD3A, GL_SUBPIXEL_BITS = 0xD50, GL_RED_BITS = 0xD52, GL_GREEN_BITS = 0xD53, GL_BLUE_BITS = 0xD54, GL_ALPHA_BITS = 0xD55, GL_DEPTH_BITS = 0xD56, GL_STENCIL_BITS = 0xD57, GL_TEXTURE_2D = 0xDE1,

    /**
     * HintMode
     */
    public static final int GL_DONT_CARE = 0x1100, GL_FASTEST = 0x1101, GL_NICEST = 0x1102;

    /**
     * LogicOp
     */
    public static final int GL_INVERT = 0x150A,

    /**
     * MatrixMode
     */
    public static final int GL_TEXTURE = 0x1702;

    /**
     * PixelFormat
     */
    public static final int GL_DEPTH_COMPONENT = 0x1902, GL_ALPHA = 0x1906, GL_RGB = 0x1907, GL_RGBA = 0x1908, GL_LUMINANCE = 0x1909, GL_LUMINANCE_ALPHA = 0x190A;

    /**
     * StencilOp
     */
    public static final int GL_KEEP = 0x1E00, GL_REPLACE = 0x1E01, GL_INCR = 0x1E02, GL_DECR = 0x1E03;

    /**
     * StringName
     */
    public static final int GL_VENDOR = 0x1F00, GL_RENDERER = 0x1F01, GL_VERSION = 0x1F02, GL_EXTENSIONS = 0x1F03;

    /**
     * TextureMagFilter
     */
    public static final int GL_NEAREST = 0x2600, GL_LINEAR = 0x2601;

    /**
     * TextureMinFilter
     */
    public static final int GL_NEAREST_MIPMAP_NEAREST = 0x2700, GL_LINEAR_MIPMAP_NEAREST = 0x2701, GL_NEAREST_MIPMAP_LINEAR = 0x2702, GL_LINEAR_MIPMAP_LINEAR = 0x2703;

    /**
     * TextureParameterName
     */
    public static final int GL_TEXTURE_MAG_FILTER = 0x2800, GL_TEXTURE_MIN_FILTER = 0x2801, GL_TEXTURE_WRAP_S = 0x2802, GL_TEXTURE_WRAP_T = 0x2803;

    /**
     * TextureWrapMode
     */
    public static final int GL_REPEAT = 0x2901;

    /**
     * polygon_offset
     */
    public static final int GL_POLYGON_OFFSET_FACTOR = 0x8038, GL_POLYGON_OFFSET_UNITS = 0x2A00, GL_POLYGON_OFFSET_FILL = 0x8037;

    /**
     * texture
     */
    public static final int GL_RGBA4 = 0x8056, GL_RGB5_A1 = 0x8057,

    /**
     * texture_object
     */
    public static final int GL_TEXTURE_BINDING_2D = 0x8069;

    /**
     * <a href="http://docs.gl/es3/glActiveTexture">...</a>
     * <a target="_blank" href="http://docs.gl/es3/glActiveTexture">Reference Page</a>
     */
    @Api void glActiveTexture(int texture);

    @Api void glAttachShader(int program, int shader);

    @Api void glBindAttribLocation(int program, int index, String name);

    @Api void glBindBuffer(int target, int buffer);

    @Api void glBindFramebuffer(int target, int framebuffer);

    @Api void glBindRenderbuffer(int target, int renderbuffer);

    @Api void glBindTexture(int target, int texture);

    @Api void glBlendColor(float red, float green, float blue, float alpha);

    @Api void glBlendEquation(int mode);

    @Api void glBlendEquationSeparate(int modeRGB, int modeAlpha);

    @Api void glBlendFunc(int sfactor, int dfactor);

    @Api void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

    @Api void glBufferData(int target, long size, java.nio.Buffer data, int usage);

    @Api default void glBufferData(int target, IntBuffer buffer, int usage) {
        glBufferData(target, Integer.toUnsignedLong(buffer.remaining()) << 2, buffer, usage);
    }

    @Api default void glBufferData(int target, FloatBuffer buffer, int usage) {
        glBufferData(target, Integer.toUnsignedLong(buffer.remaining()) << 2, buffer, usage);
    }

    @Api void glBufferSubData(int target, int offset, long size, java.nio.Buffer data);

    @Api int glCheckFramebufferStatus(int target);

    @Api void glClear(int mask);

    @Api void glClearColor(float red, float green, float blue, float alpha);

    @Api void glClearDepthf(float depth);

    @Api void glClearStencil(int s);

    @Api void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);

    @Api void glCompileShader(int shader);

    @Api void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, java.nio.Buffer data);

    @Api void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, java.nio.Buffer data);

    @Api void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);

    @Api void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

    @Api int glCreateProgram();

    @Api int glCreateShader(int type);

    @Api void glCullFace(int mode);

    @Api void glDeleteBuffers(int n, int[] buffers, int offset);

    @Api void glDeleteBuffers(int n, java.nio.IntBuffer buffers);

    @Api void glDeleteFramebuffers(int n, int[] framebuffers, int offset);

    @Api void glDeleteFramebuffers(int n, java.nio.IntBuffer framebuffers);

    @Api void glDeleteProgram(int program);

    @Api void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset);

    @Api void glDeleteRenderbuffers(int n, java.nio.IntBuffer renderbuffers);

    @Api void glDeleteShader(int shader);

    @Api void glDeleteTextures(int n, int[] textures, int offset);

    @Api void glDeleteTextures(int n, java.nio.IntBuffer textures);

    @Api void glDepthFunc(int func);

    @Api void glDepthMask(boolean flag);

    @Api void glDepthRangef(float zNear, float zFar);

    @Api void glDetachShader(int program, int shader);

    @Api void glDisable(int cap);

    @Api void glDisableVertexAttribArray(int index);

    @Api void glDrawArrays(int mode, int first, int count);

    @Api void glDrawElements(int mode, int count, int type, int offset);

    @Api void glDrawElements(int mode, int count, int type, java.nio.Buffer indices);

    @Api void glEnable(int cap);

    @Api void glEnableVertexAttribArray(int index);

    @Api void glFinish();

    @Api void glFlush();

    @Api void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer);

    @Api void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level);

    @Api void glFrontFace(int mode);

    @Api void glGenBuffers(int n, int[] buffers, int offset);

    @Api default int glGenBuffers() {
        int[] a = new int[1];
        glGenBuffers(1, a, 0);
        return a[0];
    }

    @Api void glGenBuffers(int n, java.nio.IntBuffer buffers);

    @Api void glGenerateMipmap(int target);

    @Api void glGenFramebuffers(int n, int[] framebuffers, int offset);

    @Api void glGenFramebuffers(int n, java.nio.IntBuffer framebuffers);

    @Api default int glGenFramebuffers() {
        int[] a = new int[1];
        glGenFramebuffers(1, a, 0);
        return a[0];
    }

    @Api void glGenRenderbuffers(int n, int[] renderbuffers, int offset);

    @Api void glGenRenderbuffers(int n, java.nio.IntBuffer renderbuffers);

    @Api default int glGenRenderbuffers() {
        int[] a = new int[1];
        glGenRenderbuffers(1, a, 0);
        return a[0];
    }

    @Api void glGenTextures(int n, int[] textures, int offset);

    @Api void glGenTextures(int n, java.nio.IntBuffer textures);

    @Api default int glGenTextures() {
        int[] a = new int[1];
        glGenTextures(1, a, 0);
        return a[0];
    }

    @Api void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset);

    String glGetActiveAttrib(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset);

    String glGetActiveAttrib(int program, int index, java.nio.IntBuffer size, java.nio.IntBuffer type);

    @Api void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset);

    String glGetActiveUniform(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset);

    String glGetActiveUniform(int program, int index, java.nio.IntBuffer size, java.nio.IntBuffer type);

    @Api void glGetAttachedShaders(int program, int maxcount, int[] count, int countOffset, int[] shaders, int shadersOffset);

    @Api void glGetAttachedShaders(int program, int maxcount, java.nio.IntBuffer count, java.nio.IntBuffer shaders);

    @Api int glGetAttribLocation(int program, String name);

    @Api void glGetBooleanv(int pname, boolean[] params, int offset);

    @Api void glGetBooleanv(int pname, java.nio.IntBuffer params);

    @Api void glGetBufferParameteriv(int target, int pname, int[] params, int offset);

    @Api void glGetBufferParameteriv(int target, int pname, java.nio.IntBuffer params);

    @Api int glGetError();

    @Api void glGetFloatv(int pname, float[] params, int offset);

    @Api void glGetFloatv(int pname, java.nio.FloatBuffer params);

    @Api void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, int[] params, int offset);

    @Api void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, java.nio.IntBuffer params);

    @Api void glGetIntegerv(int pname, int[] params, int offset);

    @Api default int glGetInteger(int pname) {
        int[] a = new int[1];
        glGetIntegerv(pname, a, 0);
        return a[0];
    }

    @Api void glGetIntegerv(int pname, java.nio.IntBuffer params);

    @Api void glGetProgramiv(int program, int pname, int[] params, int offset);

    @Api void glGetProgramiv(int program, int pname, java.nio.IntBuffer params);

    String glGetProgramInfoLog(int program);

    @Api void glGetRenderbufferParameteriv(int target, int pname, int[] params, int offset);

    @Api void glGetRenderbufferParameteriv(int target, int pname, java.nio.IntBuffer params);

    @Api void glGetShaderiv(int shader, int pname, int[] params, int offset);

    @Api void glGetShaderiv(int shader, int pname, java.nio.IntBuffer params);

    String glGetShaderInfoLog(int shader);

    @Api void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range, int rangeOffset, int[] precision, int precisionOffset);

    @Api void glGetShaderPrecisionFormat(int shadertype, int precisiontype, java.nio.IntBuffer range, java.nio.IntBuffer precision);

    @Api void glGetShaderSource(int shader, int bufsize, int[] length, int lengthOffset, byte[] source, int sourceOffset);

    String glGetShaderSource(int shader);

    String glGetString(int name);

    @Api void glGetTexParameterfv(int target, int pname, float[] params, int offset);

    @Api void glGetTexParameterfv(int target, int pname, java.nio.FloatBuffer params);

    @Api void glGetTexParameteriv(int target, int pname, int[] params, int offset);

    @Api void glGetTexParameteriv(int target, int pname, java.nio.IntBuffer params);

    @Api void glGetUniformfv(int program, int location, float[] params, int offset);

    @Api void glGetUniformfv(int program, int location, java.nio.FloatBuffer params);

    @Api void glGetUniformiv(int program, int location, int[] params, int offset);

    @Api void glGetUniformiv(int program, int location, java.nio.IntBuffer params);

    @Api int glGetUniformLocation(int program, String name);

    @Api void glGetVertexAttribfv(int index, int pname, float[] params, int offset);

    @Api void glGetVertexAttribfv(int index, int pname, java.nio.FloatBuffer params);

    @Api void glGetVertexAttribiv(int index, int pname, int[] params, int offset);

    @Api void glGetVertexAttribiv(int index, int pname, java.nio.IntBuffer params);

    @Api void glHint(int target, int mode);

    boolean glIsBuffer(int buffer);

    boolean glIsEnabled(int cap);

    boolean glIsFramebuffer(int framebuffer);

    boolean glIsProgram(int program);

    boolean glIsRenderbuffer(int renderbuffer);

    boolean glIsShader(int shader);

    boolean glIsTexture(int texture);

    @Api void glLineWidth(float width);

    @Api void glLinkProgram(int program);

    @Api void glPixelStorei(int pname, int param);

    @Api void glPolygonOffset(float factor, float units);

    @Api void glReadPixels(int x, int y, int width, int height, int format, int type, java.nio.Buffer pixels);

    @Api void glReleaseShaderCompiler();

    @Api void glRenderbufferStorage(int target, int internalformat, int width, int height);

    @Api void glSampleCoverage(float value, boolean invert);

    @Api void glScissor(int x, int y, int width, int height);

    @Api void glShaderBinary(int n, int[] shaders, int offset, int binaryformat, java.nio.Buffer binary, int length);

    @Api void glShaderBinary(int n, java.nio.IntBuffer shaders, int binaryformat, java.nio.Buffer binary, int length);

    @Api void glShaderSource(int shader, String string);

    @Api void glStencilFunc(int func, int ref, int mask);

    @Api void glStencilFuncSeparate(int face, int func, int ref, int mask);

    @Api void glStencilMask(int mask);

    @Api void glStencilMaskSeparate(int face, int mask);

    @Api void glStencilOp(int fail, int zfail, int zpass);

    @Api void glStencilOpSeparate(int face, int fail, int zfail, int zpass);

    @Api void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, java.nio.Buffer pixels);

    @Api void glTexParameterf(int target, int pname, float param);

    @Api void glTexParameterfv(int target, int pname, float[] params, int offset);

    @Api void glTexParameterfv(int target, int pname, java.nio.FloatBuffer params);

    @Api void glTexParameteri(int target, int pname, int param);

    @Api void glTexParameteriv(int target, int pname, int[] params, int offset);

    @Api void glTexParameteriv(int target, int pname, java.nio.IntBuffer params);

    @Api void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, java.nio.Buffer pixels);

    @Api void glUniform1f(int location, float x);

    @Api void glUniform1fv(int location, int count, float[] v, int offset);

    @Api void glUniform1fv(int location, int count, java.nio.FloatBuffer v);

    @Api void glUniform1i(int location, int x);

    @Api void glUniform1iv(int location, int count, int[] v, int offset);

    @Api void glUniform1iv(int location, int count, java.nio.IntBuffer v);

    @Api void glUniform2f(int location, float x, float y);

    @Api void glUniform2fv(int location, int count, float[] v, int offset);

    @Api void glUniform2fv(int location, int count, java.nio.FloatBuffer v);

    @Api void glUniform2i(int location, int x, int y);

    @Api void glUniform2iv(int location, int count, int[] v, int offset);

    @Api void glUniform2iv(int location, int count, java.nio.IntBuffer v);

    @Api void glUniform3f(int location, float x, float y, float z);

    @Api void glUniform3fv(int location, int count, float[] v, int offset);

    @Api void glUniform3fv(int location, int count, java.nio.FloatBuffer v);

    @Api void glUniform3i(int location, int x, int y, int z);

    @Api void glUniform3iv(int location, int count, int[] v, int offset);

    @Api void glUniform3iv(int location, int count, java.nio.IntBuffer v);

    @Api void glUniform4f(int location, float x, float y, float z, float w);

    @Api void glUniform4fv(int location, int count, float[] v, int offset);

    @Api void glUniform4fv(int location, int count, java.nio.FloatBuffer v);

    @Api void glUniform4i(int location, int x, int y, int z, int w);

    @Api void glUniform4iv(int location, int count, int[] v, int offset);

    @Api void glUniform4iv(int location, int count, java.nio.IntBuffer v);

    @Api void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset);

    @Api void glUniformMatrix2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    @Api void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset);

    @Api void glUniformMatrix3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    @Api void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset);

    @Api void glUniformMatrix4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    @Api void glUseProgram(int program);

    @Api void glValidateProgram(int program);

    @Api void glVertexAttrib1f(int indx, float x);

    @Api void glVertexAttrib1fv(int indx, float[] values, int offset);

    @Api void glVertexAttrib1fv(int indx, java.nio.FloatBuffer values);

    @Api void glVertexAttrib2f(int indx, float x, float y);

    @Api void glVertexAttrib2fv(int indx, float[] values, int offset);

    @Api void glVertexAttrib2fv(int indx, java.nio.FloatBuffer values);

    @Api void glVertexAttrib3f(int indx, float x, float y, float z);

    @Api void glVertexAttrib3fv(int indx, float[] values, int offset);

    @Api void glVertexAttrib3fv(int indx, java.nio.FloatBuffer values);

    @Api void glVertexAttrib4f(int indx, float x, float y, float z, float w);

    @Api void glVertexAttrib4fv(int indx, float[] values, int offset);

    @Api void glVertexAttrib4fv(int indx, java.nio.FloatBuffer values);

    @Api void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset);

//    private static native void glVertexAttribPointerBounds(int indx, int size, int type, boolean normalized, int stride, java.nio.Buffer ptr, int remaining);

    @Api void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, java.nio.Buffer ptr);

    @Api void glViewport(int x, int y, int width, int height);
}
