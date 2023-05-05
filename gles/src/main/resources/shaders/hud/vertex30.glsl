#version 300 es
precision mediump float;

in vec3 position;
in vec2 texCoord;
in vec3 vertexNormal;

out vec2 outTexCoord;

uniform mat4 ModelMat;
uniform mat4 ProjectionMat;

void main() {
    gl_Position = ProjectionMat * ModelMat * vec4(position.xyz, 1.0);
    outTexCoord = texCoord;
}
