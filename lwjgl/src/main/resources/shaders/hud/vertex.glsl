#version 320 es

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;

out vec2 outTexCoord;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * modelMatrix * vec4(position, 1.0);
    outTexCoord = texCoord;
}
