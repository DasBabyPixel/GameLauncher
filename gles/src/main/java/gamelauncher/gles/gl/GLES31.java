package gamelauncher.gles.gl;

import de.dasbabypixel.annotations.Api;

public interface GLES31 extends GLES30 {
    @Api int GL_VERTEX_SHADER_BIT = 0x00000001;
    @Api int GL_FRAGMENT_SHADER_BIT = 0x00000002;
    @Api int GL_COMPUTE_SHADER_BIT = 0x00000020;
    @Api int GL_ALL_SHADER_BITS = -1; // 0xFFFFFFFF
    @Api int GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT = 0x00000001;
    @Api int GL_ELEMENT_ARRAY_BARRIER_BIT = 0x00000002;
    @Api int GL_UNIFORM_BARRIER_BIT = 0x00000004;
    @Api int GL_TEXTURE_FETCH_BARRIER_BIT = 0x00000008;
    @Api int GL_SHADER_IMAGE_ACCESS_BARRIER_BIT = 0x00000020;
    @Api int GL_COMMAND_BARRIER_BIT = 0x00000040;
    @Api int GL_PIXEL_BUFFER_BARRIER_BIT = 0x00000080;
    @Api int GL_TEXTURE_UPDATE_BARRIER_BIT = 0x00000100;
    @Api int GL_BUFFER_UPDATE_BARRIER_BIT = 0x00000200;
    @Api int GL_FRAMEBUFFER_BARRIER_BIT = 0x00000400;
    @Api int GL_TRANSFORM_FEEDBACK_BARRIER_BIT = 0x00000800;
    @Api int GL_ATOMIC_COUNTER_BARRIER_BIT = 0x00001000;
    @Api int GL_SHADER_STORAGE_BARRIER_BIT = 0x00002000;
    @Api int GL_ALL_BARRIER_BITS = -1; // 0xFFFFFFFF

    @Api int GL_TEXTURE_WIDTH = 0x1000;
    @Api int GL_TEXTURE_HEIGHT = 0x1001;
    @Api int GL_TEXTURE_INTERNAL_FORMAT = 0x1003;
    @Api int GL_STENCIL_INDEX = 0x1901;
    @Api int GL_TEXTURE_RED_SIZE = 0x805C;
    @Api int GL_TEXTURE_GREEN_SIZE = 0x805D;
    @Api int GL_TEXTURE_BLUE_SIZE = 0x805E;
    @Api int GL_TEXTURE_ALPHA_SIZE = 0x805F;
    @Api int GL_TEXTURE_DEPTH = 0x8071;
    @Api int GL_PROGRAM_SEPARABLE = 0x8258;
    @Api int GL_ACTIVE_PROGRAM = 0x8259;
    @Api int GL_PROGRAM_PIPELINE_BINDING = 0x825A;
    @Api int GL_MAX_COMPUTE_SHARED_MEMORY_SIZE = 0x8262;
    @Api int GL_MAX_COMPUTE_UNIFORM_COMPONENTS = 0x8263;
    @Api int GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS = 0x8264;
    @Api int GL_MAX_COMPUTE_ATOMIC_COUNTERS = 0x8265;
    @Api int GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS = 0x8266;
    @Api int GL_COMPUTE_WORK_GROUP_SIZE = 0x8267;
    @Api int GL_MAX_UNIFORM_LOCATIONS = 0x826E;
    @Api int GL_VERTEX_ATTRIB_BINDING = 0x82D4;
    @Api int GL_VERTEX_ATTRIB_RELATIVE_OFFSET = 0x82D5;
    @Api int GL_VERTEX_BINDING_DIVISOR = 0x82D6;
    @Api int GL_VERTEX_BINDING_OFFSET = 0x82D7;
    @Api int GL_VERTEX_BINDING_STRIDE = 0x82D8;
    @Api int GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET = 0x82D9;
    @Api int GL_MAX_VERTEX_ATTRIB_BINDINGS = 0x82DA;
    @Api int GL_MAX_VERTEX_ATTRIB_STRIDE = 0x82E5;
    @Api int GL_TEXTURE_COMPRESSED = 0x86A1;
    @Api int GL_TEXTURE_DEPTH_SIZE = 0x884A;
    @Api int GL_READ_ONLY = 0x88B8;
    @Api int GL_WRITE_ONLY = 0x88B9;
    @Api int GL_READ_WRITE = 0x88BA;
    @Api int GL_TEXTURE_STENCIL_SIZE = 0x88F1;
    @Api int GL_TEXTURE_RED_TYPE = 0x8C10;
    @Api int GL_TEXTURE_GREEN_TYPE = 0x8C11;
    @Api int GL_TEXTURE_BLUE_TYPE = 0x8C12;
    @Api int GL_TEXTURE_ALPHA_TYPE = 0x8C13;
    @Api int GL_TEXTURE_DEPTH_TYPE = 0x8C16;
    @Api int GL_TEXTURE_SHARED_SIZE = 0x8C3F;
    @Api int GL_SAMPLE_POSITION = 0x8E50;
    @Api int GL_SAMPLE_MASK = 0x8E51;
    @Api int GL_SAMPLE_MASK_VALUE = 0x8E52;
    @Api int GL_MAX_SAMPLE_MASK_WORDS = 0x8E59;
    @Api int GL_MIN_PROGRAM_TEXTURE_GATHER_OFFSET = 0x8E5E;
    @Api int GL_MAX_PROGRAM_TEXTURE_GATHER_OFFSET = 0x8E5F;
    @Api int GL_MAX_IMAGE_UNITS = 0x8F38;
    @Api int GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES = 0x8F39;
    @Api int GL_IMAGE_BINDING_NAME = 0x8F3A;
    @Api int GL_IMAGE_BINDING_LEVEL = 0x8F3B;
    @Api int GL_IMAGE_BINDING_LAYERED = 0x8F3C;
    @Api int GL_IMAGE_BINDING_LAYER = 0x8F3D;
    @Api int GL_IMAGE_BINDING_ACCESS = 0x8F3E;
    @Api int GL_DRAW_INDIRECT_BUFFER = 0x8F3F;
    @Api int GL_DRAW_INDIRECT_BUFFER_BINDING = 0x8F43;
    @Api int GL_VERTEX_BINDING_BUFFER = 0x8F4F;
    @Api int GL_IMAGE_2D = 0x904D;
    @Api int GL_IMAGE_3D = 0x904E;
    @Api int GL_IMAGE_CUBE = 0x9050;
    @Api int GL_IMAGE_2D_ARRAY = 0x9053;
    @Api int GL_INT_IMAGE_2D = 0x9058;
    @Api int GL_INT_IMAGE_3D = 0x9059;
    @Api int GL_INT_IMAGE_CUBE = 0x905B;
    @Api int GL_INT_IMAGE_2D_ARRAY = 0x905E;
    @Api int GL_UNSIGNED_INT_IMAGE_2D = 0x9063;
    @Api int GL_UNSIGNED_INT_IMAGE_3D = 0x9064;
    @Api int GL_UNSIGNED_INT_IMAGE_CUBE = 0x9066;
    @Api int GL_UNSIGNED_INT_IMAGE_2D_ARRAY = 0x9069;
    @Api int GL_IMAGE_BINDING_FORMAT = 0x906E;
    @Api int GL_IMAGE_FORMAT_COMPATIBILITY_TYPE = 0x90C7;
    @Api int GL_IMAGE_FORMAT_COMPATIBILITY_BY_SIZE = 0x90C8;
    @Api int GL_IMAGE_FORMAT_COMPATIBILITY_BY_CLASS = 0x90C9;
    @Api int GL_MAX_VERTEX_IMAGE_UNIFORMS = 0x90CA;
    @Api int GL_MAX_FRAGMENT_IMAGE_UNIFORMS = 0x90CE;
    @Api int GL_MAX_COMBINED_IMAGE_UNIFORMS = 0x90CF;
    @Api int GL_SHADER_STORAGE_BUFFER = 0x90D2;
    @Api int GL_SHADER_STORAGE_BUFFER_BINDING = 0x90D3;
    @Api int GL_SHADER_STORAGE_BUFFER_START = 0x90D4;
    @Api int GL_SHADER_STORAGE_BUFFER_SIZE = 0x90D5;
    @Api int GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS = 0x90D6;
    @Api int GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS = 0x90DA;
    @Api int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS = 0x90DB;
    @Api int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS = 0x90DC;
    @Api int GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS = 0x90DD;
    @Api int GL_MAX_SHADER_STORAGE_BLOCK_SIZE = 0x90DE;
    @Api int GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT = 0x90DF;
    @Api int GL_DEPTH_STENCIL_TEXTURE_MODE = 0x90EA;
    @Api int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 0x90EB;
    @Api int GL_DISPATCH_INDIRECT_BUFFER = 0x90EE;
    @Api int GL_DISPATCH_INDIRECT_BUFFER_BINDING = 0x90EF;
    @Api int GL_TEXTURE_2D_MULTISAMPLE = 0x9100;
    @Api int GL_TEXTURE_BINDING_2D_MULTISAMPLE = 0x9104;
    @Api int GL_TEXTURE_SAMPLES = 0x9106;
    @Api int GL_TEXTURE_FIXED_SAMPLE_LOCATIONS = 0x9107;
    @Api int GL_SAMPLER_2D_MULTISAMPLE = 0x9108;
    @Api int GL_INT_SAMPLER_2D_MULTISAMPLE = 0x9109;
    @Api int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE = 0x910A;
    @Api int GL_MAX_COLOR_TEXTURE_SAMPLES = 0x910E;
    @Api int GL_MAX_DEPTH_TEXTURE_SAMPLES = 0x910F;
    @Api int GL_MAX_INTEGER_SAMPLES = 0x9110;
    @Api int GL_COMPUTE_SHADER = 0x91B9;
    @Api int GL_MAX_COMPUTE_UNIFORM_BLOCKS = 0x91BB;
    @Api int GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS = 0x91BC;
    @Api int GL_MAX_COMPUTE_IMAGE_UNIFORMS = 0x91BD;
    @Api int GL_MAX_COMPUTE_WORK_GROUP_COUNT = 0x91BE;
    @Api int GL_MAX_COMPUTE_WORK_GROUP_SIZE = 0x91BF;
    @Api int GL_ATOMIC_COUNTER_BUFFER = 0x92C0;
    @Api int GL_ATOMIC_COUNTER_BUFFER_BINDING = 0x92C1;
    @Api int GL_ATOMIC_COUNTER_BUFFER_START = 0x92C2;
    @Api int GL_ATOMIC_COUNTER_BUFFER_SIZE = 0x92C3;
    @Api int GL_MAX_VERTEX_ATOMIC_COUNTER_BUFFERS = 0x92CC;
    @Api int GL_MAX_FRAGMENT_ATOMIC_COUNTER_BUFFERS = 0x92D0;
    @Api int GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS = 0x92D1;
    @Api int GL_MAX_VERTEX_ATOMIC_COUNTERS = 0x92D2;
    @Api int GL_MAX_FRAGMENT_ATOMIC_COUNTERS = 0x92D6;
    @Api int GL_MAX_COMBINED_ATOMIC_COUNTERS = 0x92D7;
    @Api int GL_MAX_ATOMIC_COUNTER_BUFFER_SIZE = 0x92D8;
    @Api int GL_ACTIVE_ATOMIC_COUNTER_BUFFERS = 0x92D9;
    @Api int GL_UNSIGNED_INT_ATOMIC_COUNTER = 0x92DB;
    @Api int GL_MAX_ATOMIC_COUNTER_BUFFER_BINDINGS = 0x92DC;
    @Api int GL_UNIFORM = 0x92E1;
    @Api int GL_UNIFORM_BLOCK = 0x92E2;
    @Api int GL_PROGRAM_INPUT = 0x92E3;
    @Api int GL_PROGRAM_OUTPUT = 0x92E4;
    @Api int GL_BUFFER_VARIABLE = 0x92E5;
    @Api int GL_SHADER_STORAGE_BLOCK = 0x92E6;
    @Api int GL_TRANSFORM_FEEDBACK_VARYING = 0x92F4;
    @Api int GL_ACTIVE_RESOURCES = 0x92F5;
    @Api int GL_MAX_NAME_LENGTH = 0x92F6;
    @Api int GL_MAX_NUM_ACTIVE_VARIABLES = 0x92F7;
    @Api int GL_NAME_LENGTH = 0x92F9;
    @Api int GL_TYPE = 0x92FA;
    @Api int GL_ARRAY_SIZE = 0x92FB;
    @Api int GL_OFFSET = 0x92FC;
    @Api int GL_BLOCK_INDEX = 0x92FD;
    @Api int GL_ARRAY_STRIDE = 0x92FE;
    @Api int GL_MATRIX_STRIDE = 0x92FF;
    @Api int GL_IS_ROW_MAJOR = 0x9300;
    @Api int GL_ATOMIC_COUNTER_BUFFER_INDEX = 0x9301;
    @Api int GL_BUFFER_BINDING = 0x9302;
    @Api int GL_BUFFER_DATA_SIZE = 0x9303;
    @Api int GL_NUM_ACTIVE_VARIABLES = 0x9304;
    @Api int GL_ACTIVE_VARIABLES = 0x9305;
    @Api int GL_REFERENCED_BY_VERTEX_SHADER = 0x9306;
    @Api int GL_REFERENCED_BY_FRAGMENT_SHADER = 0x930A;
    @Api int GL_REFERENCED_BY_COMPUTE_SHADER = 0x930B;
    @Api int GL_TOP_LEVEL_ARRAY_SIZE = 0x930C;
    @Api int GL_TOP_LEVEL_ARRAY_STRIDE = 0x930D;
    @Api int GL_LOCATION = 0x930E;
    @Api int GL_FRAMEBUFFER_DEFAULT_WIDTH = 0x9310;
    @Api int GL_FRAMEBUFFER_DEFAULT_HEIGHT = 0x9311;
    @Api int GL_FRAMEBUFFER_DEFAULT_SAMPLES = 0x9313;
    @Api int GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS = 0x9314;
    @Api int GL_MAX_FRAMEBUFFER_WIDTH = 0x9315;
    @Api int GL_MAX_FRAMEBUFFER_HEIGHT = 0x9316;
    @Api int GL_MAX_FRAMEBUFFER_SAMPLES = 0x9318;

    // C function void glDispatchCompute ( GLuint num_groups_x, GLuint num_groups_y, GLuint num_groups_z )

    @Api void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z);

    // C function void glDispatchComputeIndirect ( GLintptr indirect );

    @Api void glDispatchComputeIndirect(long indirect);

    // C function void glDrawArraysIndirect ( GLenum mode, const void *indirect );

    @Api void glDrawArraysIndirect(int mode, long indirect);

    // C function glDrawElementsIndirect ( GLenum mode, GLenum type, const void *indirect );

    @Api void glDrawElementsIndirect(int mode, int type, long indirect);

    // C function void glFramebufferParameteri ( GLenum target, GLenum pname, GLint param )

    @Api void glFramebufferParameteri(int target, int pname, int param);

    // C function void glGetFramebufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    @Api void glGetFramebufferParameteriv(int target, int pname, int[] params, int offset);

    // C function void glGetFramebufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    @Api void glGetFramebufferParameteriv(int target, int pname, java.nio.IntBuffer params);

    // C function void glGetProgramInterfaceiv ( GLuint program, GLenum programInterface, GLenum pname, GLint *params )

    @Api void glGetProgramInterfaceiv(int program, int programInterface, int pname, int[] params, int offset);

    // C function void glGetProgramInterfaceiv ( GLuint program, GLenum programInterface, GLenum pname, GLint *params )

    @Api void glGetProgramInterfaceiv(int program, int programInterface, int pname, java.nio.IntBuffer params);

    // C function GLuint glGetProgramResourceIndex ( GLuint program, GLenum programInterface, const GLchar *name )

    @Api int glGetProgramResourceIndex(int program, int programInterface, String name);

    // C function void glGetProgramResourceName ( GLuint program, GLenum programInterface, GLuint index, GLsizei bufSize, GLsizei *length, GLchar *name )

    @Api String glGetProgramResourceName(int program, int programInterface, int index);

    // C function void glGetProgramResourceiv ( GLuint program, GLenum programInterface, GLuint index, GLsizei propCount, const GLenum *props, GLsizei bufSize, GLsizei *length, GLint *params )

    @Api void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, int[] props, int propsOffset, int bufSize, int[] length, int lengthOffset, int[] params, int paramsOffset);

    // C function void glGetProgramResourceiv ( GLuint program, GLenum programInterface, GLuint index, GLsizei propCount, const GLenum *props, GLsizei bufSize, GLsizei *length, GLint *params )

    @Api void glGetProgramResourceiv(int program, int programInterface, int index, int propCount, java.nio.IntBuffer props, int bufSize, java.nio.IntBuffer length, java.nio.IntBuffer params);

    // C function GLint glGetProgramResourceLocation ( GLuint program, GLenum programInterface, const GLchar *name )

    @Api int glGetProgramResourceLocation(int program, int programInterface, String name);

    // C function void glUseProgramStages ( GLuint pipeline, GLbitfield stages, GLuint program )

    @Api void glUseProgramStages(int pipeline, int stages, int program);

    // C function void glActiveShaderProgram ( GLuint pipeline, GLuint program )

    @Api void glActiveShaderProgram(int pipeline, int program);

    // C function GLuint glCreateShaderProgramv ( GLenum type, GLsizei count, const GLchar *const *strings )

    @Api int glCreateShaderProgramv(int type, String[] strings);

    // C function void glBindProgramPipeline ( GLuint pipeline )

    @Api void glBindProgramPipeline(int pipeline);

    // C function void glDeleteProgramPipelines ( GLsizei n, const GLuint *pipelines )

    @Api void glDeleteProgramPipelines(int n, int[] pipelines, int offset);

    // C function void glDeleteProgramPipelines ( GLsizei n, const GLuint *pipelines )

    @Api void glDeleteProgramPipelines(int n, java.nio.IntBuffer pipelines);

    // C function void glGenProgramPipelines ( GLsizei n, GLuint *pipelines )

    @Api void glGenProgramPipelines(int n, int[] pipelines, int offset);

    // C function void glGenProgramPipelines ( GLsizei n, GLuint *pipelines )

    @Api void glGenProgramPipelines(int n, java.nio.IntBuffer pipelines);

    // C function GLboolean glIsProgramPipeline ( GLuint pipeline )

    @Api boolean glIsProgramPipeline(int pipeline);

    // C function void glGetProgramPipelineiv ( GLuint pipeline, GLenum pname, GLint *params )

    @Api void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int offset);

    // C function void glGetProgramPipelineiv ( GLuint pipeline, GLenum pname, GLint *params )

    @Api void glGetProgramPipelineiv(int pipeline, int pname, java.nio.IntBuffer params);

    // C function void glProgramUniform1i ( GLuint program, GLint location, GLint v0 )

    @Api void glProgramUniform1i(int program, int location, int v0);

    // C function void glProgramUniform2i ( GLuint program, GLint location, GLint v0, GLint v1 )

    @Api void glProgramUniform2i(int program, int location, int v0, int v1);

    // C function void glProgramUniform3i ( GLuint program, GLint location, GLint v0, GLint v1, GLint v2 )

    @Api void glProgramUniform3i(int program, int location, int v0, int v1, int v2);

    // C function void glProgramUniform4i ( GLuint program, GLint location, GLint v0, GLint v1, GLint v2, GLint v3 )

    @Api void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3);

    // C function void glProgramUniform1ui ( GLuint program, GLint location, GLuint v0 )

    @Api void glProgramUniform1ui(int program, int location, int v0);

    // C function void glProgramUniform2ui ( GLuint program, GLint location, GLuint v0, GLuint v1 )

    @Api void glProgramUniform2ui(int program, int location, int v0, int v1);

    // C function void glProgramUniform3ui ( GLuint program, GLint location, GLuint v0, GLuint v1, GLuint v2 )

    @Api void glProgramUniform3ui(int program, int location, int v0, int v1, int v2);

    // C function void glProgramUniform4ui ( GLuint program, GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )

    @Api void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3);

    // C function void glProgramUniform1f ( GLuint program, GLint location, GLfloat v0 )

    @Api void glProgramUniform1f(int program, int location, float v0);

    // C function void glProgramUniform2f ( GLuint program, GLint location, GLfloat v0, GLfloat v1 )

    @Api void glProgramUniform2f(int program, int location, float v0, float v1);

    // C function void glProgramUniform3f ( GLuint program, GLint location, GLfloat v0, GLfloat v1, GLfloat v2 )

    @Api void glProgramUniform3f(int program, int location, float v0, float v1, float v2);

    // C function void glProgramUniform4f ( GLuint program, GLint location, GLfloat v0, GLfloat v1, GLfloat v2, GLfloat v3 )

    @Api void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3);

    // C function void glProgramUniform1iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform1iv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform1iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform1iv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform2iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform2iv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform2iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform2iv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform3iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform3iv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform3iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform3iv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform4iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform4iv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform4iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    @Api void glProgramUniform4iv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform1uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform1uiv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform1uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform1uiv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform2uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform2uiv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform2uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform2uiv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform3uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform3uiv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform3uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform3uiv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform4uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform4uiv(int program, int location, int count, int[] value, int offset);

    // C function void glProgramUniform4uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    @Api void glProgramUniform4uiv(int program, int location, int count, java.nio.IntBuffer value);

    // C function void glProgramUniform1fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform1fv(int program, int location, int count, float[] value, int offset);

    // C function void glProgramUniform1fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform1fv(int program, int location, int count, java.nio.FloatBuffer value);

    // C function void glProgramUniform2fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform2fv(int program, int location, int count, float[] value, int offset);

    // C function void glProgramUniform2fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform2fv(int program, int location, int count, java.nio.FloatBuffer value);

    // C function void glProgramUniform3fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform3fv(int program, int location, int count, float[] value, int offset);

    // C function void glProgramUniform3fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform3fv(int program, int location, int count, java.nio.FloatBuffer value);

    // C function void glProgramUniform4fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform4fv(int program, int location, int count, float[] value, int offset);

    // C function void glProgramUniform4fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    @Api void glProgramUniform4fv(int program, int location, int count, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix2x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix3x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix2x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix4x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix3x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, float[] value, int offset);

    // C function void glProgramUniformMatrix4x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    @Api void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void glValidateProgramPipeline ( GLuint pipeline )

    @Api void glValidateProgramPipeline(int pipeline);

    // C function void glGetProgramPipelineInfoLog( GLuint program, GLsizei maxLength, GLsizei * length, GLchar * infoLog);

    @Api String glGetProgramPipelineInfoLog(int program);

    // C function void glBindImageTexture ( GLuint unit, GLuint texture, GLint level, GLboolean layered, GLint layer, GLenum access, GLenum format )

    @Api void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format);

    // C function void glGetBooleani_v ( GLenum target, GLuint index, GLboolean *data )

    @Api void glGetBooleani_v(int target, int index, boolean[] data, int offset);

    // C function void glGetBooleani_v ( GLenum target, GLuint index, GLboolean *data )

    @Api void glGetBooleani_v(int target, int index, java.nio.IntBuffer data);

    // C function void glMemoryBarrier ( GLbitfield barriers )

    @Api void glMemoryBarrier(int barriers);

    // C function void glMemoryBarrierByRegion ( GLbitfield barriers )

    @Api void glMemoryBarrierByRegion(int barriers);

    // C function void glTexStorage2DMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width, GLsizei height, GLboolean fixedsamplelocations )

    @Api void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations);

    // C function void glGetMultisamplefv ( GLenum pname, GLuint index, GLfloat *val )

    @Api void glGetMultisamplefv(int pname, int index, float[] val, int offset);

    // C function void glGetMultisamplefv ( GLenum pname, GLuint index, GLfloat *val )

    @Api void glGetMultisamplefv(int pname, int index, java.nio.FloatBuffer val);

    // C function void glSampleMaski ( GLuint maskNumber, GLbitfield mask )

    @Api void glSampleMaski(int maskNumber, int mask);

    // C function void glGetTexLevelParameteriv ( GLenum target, GLint level, GLenum pname, GLint *params )

    @Api void glGetTexLevelParameteriv(int target, int level, int pname, int[] params, int offset);

    // C function void glGetTexLevelParameteriv ( GLenum target, GLint level, GLenum pname, GLint *params )

    @Api void glGetTexLevelParameteriv(int target, int level, int pname, java.nio.IntBuffer params);

    // C function void glGetTexLevelParameterfv ( GLenum target, GLint level, GLenum pname, GLfloat *params )

    @Api void glGetTexLevelParameterfv(int target, int level, int pname, float[] params, int offset);

    // C function void glGetTexLevelParameterfv ( GLenum target, GLint level, GLenum pname, GLfloat *params )

    @Api void glGetTexLevelParameterfv(int target, int level, int pname, java.nio.FloatBuffer params);

    // C function void glBindVertexBuffer (GLuint bindingindex, GLuint buffer, GLintptr offset, GLsizei stride)

    @Api void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride);

    // C function void glVertexAttribFormat ( GLuint attribindex, GLint size, GLenum type, GLboolean normalized, GLuint relativeoffset )

    @Api void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset);

    // C function void glVertexAttribIFormat ( GLuint attribindex, GLint size, GLenum type, GLuint relativeoffset )

    @Api void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset);

    // C function void glVertexAttribBinding ( GLuint attribindex, GLuint bindingindex )

    @Api void glVertexAttribBinding(int attribindex, int bindingindex);

    // C function void glVertexBindingDivisor ( GLuint bindingindex, GLuint divisor )

    @Api void glVertexBindingDivisor(int bindingindex, int divisor);

}
